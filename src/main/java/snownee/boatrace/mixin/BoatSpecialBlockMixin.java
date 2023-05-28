package snownee.boatrace.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import snownee.boatrace.BoatRaceTweaks;
import snownee.boatrace.BoatRaceTweaksConfig;
import snownee.boatrace.duck.BRTBoostingBoat;

@Mixin(Boat.class)
public abstract class BoatSpecialBlockMixin implements BRTBoostingBoat {

	private boolean boatrace$jump;
	private int boatrace$boostTicks;

	@Inject(
			method = "getGroundFriction", at = @At(
			value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getBlock()Lnet/minecraft/world/level/block/Block;", ordinal = 1
	), locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void boatrace$getGroundFriction(CallbackInfoReturnable<Float> cir, AABB aABB, AABB aABB2, int i, int j, int k, int l, int m, int n, VoxelShape voxelShape, float f, int o, BlockPos.MutableBlockPos mutableBlockPos, int p, int q, int r, int s, BlockState blockState) {
		Boat boat = (Boat) (Object) this;
		if (!boatrace$jump && blockState.is(BoatRaceTweaksConfig.jumpingBlock)) {
			boatrace$jump = true;
			boat.playSound(BoatRaceTweaks.JUMP_SOUND.get());
		}
		if (boatrace$boostTicks < BoatRaceTweaksConfig.boostingTicks && blockState.is(BoatRaceTweaksConfig.boostingBlock)) {
			if (BoatRaceTweaksConfig.boostingTicks - boatrace$boostTicks > 10) {
				boat.playSound(BoatRaceTweaks.BOOST_SOUND.get());
			}
			boatrace$boostTicks = BoatRaceTweaksConfig.boostingTicks;
		}
	}

	@Inject(method = "tick", at = @At("HEAD"))
	private void boatrace$tick(CallbackInfo ci) {
		Boat boat = (Boat) (Object) this;
		if (boatrace$jump) {
			boatrace$jump = false;
			boat.setDeltaMovement(boat.getDeltaMovement().with(Direction.Axis.Y, BoatRaceTweaksConfig.jumpingForce));
		}
		if (boatrace$boostTicks > 0) {
			boatrace$boostTicks--;
		}
	}

	@Override
	public float boatrace$getExtraForwardForce() {
		return boatrace$boostTicks > 0 ? BoatRaceTweaksConfig.boostingForce : 0;
	}
}
