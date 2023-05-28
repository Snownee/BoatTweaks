package snownee.boattweaks.mixin;

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
import snownee.boattweaks.BoatTweaksConfig;
import snownee.boattweaks.duck.BTBoostingBoat;

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
	private int boattweaks$wallHitCd;

	@Redirect(
			method = "getGroundFriction", at = @At(
			value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;getFriction()F"
	)
	)
	private float boattweaks$getGroundFriction(Block block) {
		return BoatTweaksConfig.getFriction(block);
	}

	@ModifyVariable(method = "controlBoat", at = @At(value = "STORE", ordinal = 0), index = 1)
	private float boattweaks$modifyForce(float f) {
		if (status == Boat.Status.ON_LAND) {
			if (inputUp) {
				BTBoostingBoat boat = (BTBoostingBoat) this;
				f += BoatTweaksConfig.forwardForce - 0.04F + boat.boattweaks$getExtraForwardForce();
			}
			if (inputDown) {
				f -= BoatTweaksConfig.backwardForce - 0.005F;
			}
			if (inputLeft) {
				deltaRotation += 1 - BoatTweaksConfig.turningForce;
			}
			if (inputRight) {
				deltaRotation -= 1 - BoatTweaksConfig.turningForce;
			}
		} else if (status == Boat.Status.IN_AIR) {
			if (inputLeft) {
				deltaRotation += 1 - BoatTweaksConfig.turningForceInAir;
			}
			if (inputRight) {
				deltaRotation -= 1 - BoatTweaksConfig.turningForceInAir;
			}
		}
		return f;
	}

	@Inject(method = "tick", at = @At("HEAD"))
	private void boattweaks$tick(CallbackInfo ci) {
		Boat boat = (Boat) (Object) this;
		if (boattweaks$wallHitCd > 0) {
			boattweaks$wallHitCd--;
		} else if (boat.horizontalCollision) {
			boattweaks$wallHitCd = BoatTweaksConfig.wallHitCooldown;
			float scale = 1 - BoatTweaksConfig.wallHitSpeedLoss;
			boat.setDeltaMovement(boat.getDeltaMovement().multiply(scale, 1, scale));
		}
	}

	@ModifyConstant(method = "tick", constant = @Constant(floatValue = 60F))
	private float boattweaks$modifyTimeOutTicks(float f) {
		return BoatTweaksConfig.outOfControlTicks;
	}
}
