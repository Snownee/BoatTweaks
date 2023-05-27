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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import snownee.boatrace.BoatRaceTweaksConfig;

@Mixin(Boat.class)
public class BoatSpecialBlockMixin {

	private boolean boatrace$jump;
	private int[] boatrace$directionalBoostTicks = new int[4];

	@Inject(
			method = "getGroundFriction", at = @At(
			value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getBlock()Lnet/minecraft/world/level/block/Block;", ordinal = 1
	), locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void boatrace$getGroundFriction(CallbackInfoReturnable<Float> cir, AABB aABB, AABB aABB2, int i, int j, int k, int l, int m, int n, VoxelShape voxelShape, float f, int o, BlockPos.MutableBlockPos mutableBlockPos, int p, int q, int r, int s, BlockState blockState) {
		if (blockState.is(BoatRaceTweaksConfig.jumpingBlock)) {
			boatrace$jump = true;
		}
		if (blockState.is(BoatRaceTweaksConfig.directionalBoostingBlock) && blockState.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
			Direction direction = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite();
			int index = direction.get2DDataValue();
			if (boatrace$directionalBoostTicks[index] == 0) {
				boatrace$directionalBoostTicks[index] = BoatRaceTweaksConfig.directionalBoostingTicks;
				boatrace$directionalBoostTicks[direction.getOpposite().get2DDataValue()] = 0;
			}
		}
	}

	@Inject(method = "tick", at = @At("HEAD"))
	private void boatrace$tick(CallbackInfo ci) {
		Boat boat = (Boat) (Object) this;
		if (boatrace$jump) {
			boatrace$jump = false;
			boat.setDeltaMovement(boat.getDeltaMovement().with(Direction.Axis.Y, BoatRaceTweaksConfig.jumpingForce));
		}
		for (int i = 0; i < 4; i++) {
			if (boatrace$directionalBoostTicks[i] > 0) {
				boatrace$directionalBoostTicks[i]--;
				Direction direction = Direction.from2DDataValue(i);
				boat.setDeltaMovement(boat.getDeltaMovement().add(direction.getStepX() * BoatRaceTweaksConfig.directionalBoostingForce, 0, direction.getStepZ() * BoatRaceTweaksConfig.directionalBoostingForce));
			}
		}
	}

}
