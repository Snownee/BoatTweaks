package snownee.boattweaks.mixin;

import java.util.IdentityHashMap;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import snownee.boattweaks.BoatTweaks;
import snownee.boattweaks.duck.BTBoostingBoat;

@Mixin(Boat.class)
public abstract class BoatSpecialBlockMixin implements BTBoostingBoat {

	private final Object2IntOpenCustomHashMap<Block> boattweaks$specialBlockCooldowns = new Object2IntOpenCustomHashMap<>(Math.min(BoatTweaks.CUSTOM_SPECIAL_BLOCKS.size(), 10), Util.identityStrategy());
	private final Map<Block, BlockPos> boattweaks$specialBlockRecords = new IdentityHashMap<>(Math.min(BoatTweaks.CUSTOM_SPECIAL_BLOCKS.size(), 10));
	private int boattweaks$eject;
	private int boattweaks$boostTicks;

	@Inject(
			method = "getGroundFriction", at = @At(
			value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getBlock()Lnet/minecraft/world/level/block/Block;", ordinal = 1
	), locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void boattweaks$getGroundFriction(CallbackInfoReturnable<Float> cir, AABB aABB, AABB aABB2, int i, int j, int k, int l, int m, int n, VoxelShape voxelShape, float f, int o, BlockPos.MutableBlockPos pos, int p, int q, int r, int s, BlockState blockState) {
		Boat boat = (Boat) (Object) this;
		if (blockState.is(BoatTweaks.CONFIG.ejectingBlock)) {
			if (boattweaks$eject == 0) {
				boat.playSound(BoatTweaks.EJECT.get());
			}
			int eject = 1;
			int y = pos.getY();
			BlockState blockState1;
			while (true) {
				pos.setY(y - eject);
				blockState1 = boat.level.getBlockState(pos);
				if (!blockState1.is(BoatTweaks.CONFIG.ejectingBlock)) {
					break;
				}
				eject++;
			}
			boattweaks$eject = Math.max(boattweaks$eject, eject);
			pos.setY(y);
		}
		if (boattweaks$boostTicks < BoatTweaks.CONFIG.boostingTicks && blockState.is(BoatTweaks.CONFIG.boostingBlock)) {
			if (BoatTweaks.CONFIG.boostingTicks - boattweaks$boostTicks > 10) {
				boat.playSound(BoatTweaks.BOOST.get());
			}
			boattweaks$boostTicks = BoatTweaks.CONFIG.boostingTicks;
		}
		Block block = blockState.getBlock();
		int cooldown = BoatTweaks.CUSTOM_SPECIAL_BLOCKS.getInt(block);
		if (cooldown > 0 && boattweaks$specialBlockCooldowns.getInt(block) == 0) {
			boattweaks$specialBlockCooldowns.put(block, cooldown);
			boattweaks$specialBlockRecords.put(block, pos.immutable());
		}
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private void boattweaks$tick(CallbackInfo ci) {
		Boat boat = (Boat) (Object) this;
		if (boattweaks$eject > 0) {
			float force;
			if (boattweaks$eject == 1) {
				force = BoatTweaks.CONFIG.ejectingForce;
			} else {
				force = BoatTweaks.CONFIG.ejectingForce * (float) (Math.pow(1.1, boattweaks$eject - 1));
			}
			boattweaks$eject = 0;
			boat.setDeltaMovement(boat.getDeltaMovement().with(Direction.Axis.Y, force));
		}
		if (boattweaks$boostTicks > 0) {
			boattweaks$boostTicks--;
		}
		boattweaks$specialBlockCooldowns.object2IntEntrySet().fastIterator().forEachRemaining(entry -> {
			Block block = entry.getKey();
			int cooldown = entry.getIntValue();
			if (BoatTweaks.CUSTOM_SPECIAL_BLOCKS.getInt(block) == cooldown) {
				BlockPos pos = boattweaks$specialBlockRecords.get(block);
				if (pos != null) {
					BlockState blockState = boat.level.getBlockState(pos);
					if (blockState.is(block)) {
						BoatTweaks.postSpecialBlockEvent(boat, blockState, pos);
					}
				}
			}
			if (cooldown > 0) {
				entry.setValue(cooldown - 1);
			}
		});
	}

	@Override
	public float boattweaks$getExtraForwardForce() {
		return boattweaks$boostTicks > 0 ? BoatTweaks.CONFIG.boostingForce : 0;
	}
}
