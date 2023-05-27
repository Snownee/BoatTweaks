package snownee.boatrace.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.block.Block;
import snownee.boatrace.BoatRaceTweaksConfig;

@Mixin(Boat.class)
public class BoatMixin {

	@Shadow
	private Boat.Status status;
	@Shadow
	private boolean inputUp;
	@Shadow
	private boolean inputDown;

	@Redirect(
			method = "getGroundFriction", at = @At(
			value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;getFriction()F"
	)
	)
	private float boatrace$getGroundFriction(Block block) {
		return BoatRaceTweaksConfig.getFriction(block);
	}

	@ModifyVariable(method = "controlBoat", at = @At(value = "STORE", ordinal = 0), index = 1)
	private float boatrace$modifyForce(float f) {
		if (status == Boat.Status.ON_LAND) {
			if (inputUp) {
				f += BoatRaceTweaksConfig.forwardForce - 0.04F;
			}
			if (inputDown) {
				f -= BoatRaceTweaksConfig.backwardForce - 0.005F;
			}
		}
		return f;
	}

	@ModifyConstant(method = "tick", constant = @Constant(floatValue = 60F))
	private float boatrace$modifyTimeOutTicks(float f) {
		return BoatRaceTweaksConfig.outOfControlTicks;
	}
}
