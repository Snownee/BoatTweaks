package snownee.boattweaks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.vehicle.Boat;
import snownee.boattweaks.BoatTweaks;
import snownee.boattweaks.duck.BTMovementDistance;

@Mixin(value = Boat.class, priority = 1500)
public class BoatMovementDistanceMixin implements BTMovementDistance {

	@Shadow
	private Boat.Status status;
	@Shadow
	private Boat.Status oldStatus;
	@Shadow
	private double lastYd;
	private double boattweaks$lastX;
	private double boattweaks$lastZ;

	@Override
	public float boattweaks$getDistance() {
		Boat boat = (Boat) (Object) this;
		return boat.getEntityData().get(BoatTweaks.DATA_ID_MOVEMENT_DISTANCE);
	}

	@Override
	public void boattweaks$setDistance(float distance) {
		Boat boat = (Boat) (Object) this;
		boat.getEntityData().set(BoatTweaks.DATA_ID_MOVEMENT_DISTANCE, distance);
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private void boattweaks$postTick(CallbackInfo ci) {
		Boat boat = (Boat) (Object) this;
		if (boat.level.isClientSide) {
			return;
		}
		if (boat.tickCount == 1) {
			boattweaks$updateLastPos();
			return;
		}
		if (oldStatus != Boat.Status.ON_LAND && oldStatus != Boat.Status.IN_AIR) {
			boattweaks$updateLastPos();
			return;
		}
		if (status != Boat.Status.ON_LAND && status != Boat.Status.IN_AIR) {
			boattweaks$updateLastPos();
			return;
		}
		double dx = Math.abs(boattweaks$lastX - boat.getX());
		double dz = Math.abs(boattweaks$lastZ - boat.getZ());
		if (dx > 0.001 || dz > 0.001) {
			boattweaks$setDistance(boattweaks$getDistance() + (float) Math.sqrt(dx * dx + dz * dz));
		}
		boattweaks$updateLastPos();
	}

	private void boattweaks$updateLastPos() {
		Boat boat = (Boat) (Object) this;
		boattweaks$lastX = boat.getX();
		boattweaks$lastZ = boat.getZ();
	}

	@Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
	private void boattweaks$addAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
		compoundTag.putDouble("BoatTweaksDistance", boattweaks$getDistance());
	}

	@Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
	private void boattweaks$readAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
		boattweaks$setDistance(compoundTag.getFloat("BoatTweaksDistance"));
	}

	@Inject(method = "defineSynchedData", at = @At("TAIL"))
	private void boattweaks$defineSynchedData(CallbackInfo ci) {
		Boat boat = (Boat) (Object) this;
		boat.getEntityData().define(BoatTweaks.DATA_ID_MOVEMENT_DISTANCE, 0F);
	}
}
