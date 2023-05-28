package snownee.boatrace.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.block.Block;
import snownee.boatrace.BoatRaceTweaks;
import snownee.boatrace.BoatRaceTweaksConfig;
import snownee.boatrace.duck.BRTBoostingBoat;

@Mixin(value = Boat.class, priority = 900)
public class BoatMixin {

	@Shadow
	private Boat.Status status;
	@Shadow
	private boolean inputUp;
	@Shadow
	private boolean inputDown;
	@Shadow
	private boolean inputLeft;
	@Shadow
	private boolean inputRight;
	@Shadow
	private float deltaRotation;
	private int boatrace$wallHitCd;

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
				BRTBoostingBoat boat = (BRTBoostingBoat) this;
				f += BoatRaceTweaksConfig.forwardForce - 0.04F + boat.boatrace$getExtraForwardForce();
			}
			if (inputDown) {
				f -= BoatRaceTweaksConfig.backwardForce - 0.005F;
			}
			if (inputLeft) {
				deltaRotation += 1 - BoatRaceTweaksConfig.turningForce;
			}
			if (inputRight) {
				deltaRotation -= 1 - BoatRaceTweaksConfig.turningForce;
			}
		} else if (status == Boat.Status.IN_AIR) {
			if (inputLeft) {
				deltaRotation += 1 - BoatRaceTweaksConfig.turningForceInAir;
			}
			if (inputRight) {
				deltaRotation -= 1 - BoatRaceTweaksConfig.turningForceInAir;
			}
		}
		return f;
	}

	@Inject(method = "tick", at = @At("HEAD"))
	private void boatrace$tick(CallbackInfo ci) {
		Boat boat = (Boat) (Object) this;
		if (boatrace$wallHitCd > 0) {
			boatrace$wallHitCd--;
		} else if (boat.horizontalCollision) {
			boatrace$wallHitCd = BoatRaceTweaksConfig.wallHitCooldown;
			float scale = 1 - BoatRaceTweaksConfig.wallHitSpeedLoss;
			boat.setDeltaMovement(boat.getDeltaMovement().multiply(scale, 1, scale));
		}
	}

	@ModifyConstant(method = "tick", constant = @Constant(floatValue = 60F))
	private float boatrace$modifyTimeOutTicks(float f) {
		return BoatRaceTweaksConfig.outOfControlTicks;
	}
}
