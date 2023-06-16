package snownee.boattweaks.mixin;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.block.Block;
import snownee.boattweaks.BoatSettings;
import snownee.boattweaks.BoatTweaks;
import snownee.boattweaks.duck.BTBoostingBoat;
import snownee.boattweaks.duck.BTConfigurableBoat;
import snownee.boattweaks.duck.BTMovementDistance;

@Mixin(value = Boat.class, priority = 900)
public class BoatSettingsMixin implements BTConfigurableBoat {

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

	@Inject(method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;)V", at = @At("RETURN"))
	private void boattweaks$init(CallbackInfo ci) {
		Boat boat = (Boat) (Object) this;
		boat.maxUpStep = BoatSettings.DEFAULT.stepUpHeight;
	}

	@Redirect(
			method = "getGroundFriction", at = @At(
			value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;getFriction()F"
	)
	)
	private float boattweaks$getGroundFriction(Block block) {
		return boattweaks$getSettings().getFriction(block);
	}

	@ModifyVariable(method = "controlBoat", at = @At(value = "STORE", ordinal = 0), index = 1)
	private float boattweaks$modifyForce(float f) {
		BoatSettings settings = boattweaks$getSettings();
		float distance = ((BTMovementDistance) this).boattweaks$getDistance();
		if (status == Boat.Status.ON_LAND) {
			if (inputUp) {
				BTBoostingBoat boat = (BTBoostingBoat) this;
				f += settings.forwardForce - 0.04F + boat.boattweaks$getExtraForwardForce();
			}
			if (inputDown) {
				f -= settings.backwardForce - 0.005F;
			}
			f = settings.getDegradedForce(f, distance);
			if (inputLeft) {
				deltaRotation += 1 - settings.getDegradedForce(settings.turningForce, distance);
			}
			if (inputRight) {
				deltaRotation -= 1 - settings.getDegradedForce(settings.turningForce, distance);
			}
		} else if (status == Boat.Status.IN_AIR) {
			if (inputLeft) {
				deltaRotation += 1 - settings.getDegradedForce(settings.turningForceInAir, distance);
			}
			if (inputRight) {
				deltaRotation -= 1 - settings.getDegradedForce(settings.turningForceInAir, distance);
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
			BoatSettings settings = boattweaks$getSettings();
			boattweaks$wallHitCd = settings.wallHitCooldown;
			float scale = 1 - settings.wallHitSpeedLoss;
			boat.setDeltaMovement(boat.getDeltaMovement().multiply(scale, 1, scale));
		}
	}

	@ModifyConstant(method = "tick", constant = @Constant(floatValue = 60F))
	private float boattweaks$modifyTimeOutTicks(float f) {
		return boattweaks$getSettings().outOfControlTicks;
	}

	@Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
	private void boattweaks$addAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
		Boat boat = (Boat) (Object) this;
		boat.getEntityData().get(BoatTweaks.DATA_ID_BOAT_SETTINGS).ifPresent(settings -> {
			compoundTag.put("BoatTweaksSettings", settings.toNBT());
		});
	}

	@Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
	private void boattweaks$readAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
		if (compoundTag.contains("BoatTweaksSettings")) {
			BoatSettings settings = new BoatSettings();
			BoatSettings.fromNBT(compoundTag.getCompound("BoatTweaksSettings"), settings);
			boattweaks$setSettings(settings);
		}
	}

	@Inject(method = "defineSynchedData", at = @At("TAIL"))
	private void boattweaks$defineSynchedData(CallbackInfo ci) {
		Boat boat = (Boat) (Object) this;
		boat.getEntityData().define(BoatTweaks.DATA_ID_BOAT_SETTINGS, Optional.empty());
	}

	@Override
	public BoatSettings boattweaks$getSettings() {
		Boat boat = (Boat) (Object) this;
		return boat.getEntityData().get(BoatTweaks.DATA_ID_BOAT_SETTINGS).orElse(BoatSettings.DEFAULT);
	}

	@Override
	public void boattweaks$setSettings(@Nullable BoatSettings settings) {
		Boat boat = (Boat) (Object) this;
		boat.getEntityData().set(BoatTweaks.DATA_ID_BOAT_SETTINGS, Optional.ofNullable(settings));
		boat.maxUpStep = boattweaks$getSettings().stepUpHeight;
	}

}
