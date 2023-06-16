package snownee.boattweaks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.vehicle.Boat;
import snownee.boattweaks.duck.BTMovementDistance;

@Mixin(value = Boat.class, priority = 1500)
public class BoatMovementDistanceMixin implements BTMovementDistance {

	@Shadow
	private Boat.Status status;
	@Shadow
	private Boat.Status oldStatus;
	private double boattweaks$distance;

	@Override
	public double boattweaks$getDistance() {
		return boattweaks$distance;
	}

	@Override
	public void boattweaks$resetDistance() {
		boattweaks$distance = 0;
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private void boattweaks$tick(CallbackInfo ci) {
		Boat boat = (Boat) (Object) this;
		if (boat.level.isClientSide) {
			return;
		}
		if (oldStatus != Boat.Status.ON_LAND && oldStatus != Boat.Status.IN_AIR) {
			return;
		}
		if (status != Boat.Status.ON_LAND && status != Boat.Status.IN_AIR) {
			return;
		}
		double dx = Math.abs(boat.xOld - boat.getX());
		double dz = Math.abs(boat.zOld - boat.getZ());
		boattweaks$distance += Math.sqrt(dx * dx + dz * dz);
	}

	@Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
	private void boattweaks$addAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
		compoundTag.putDouble("BoatTweaksDistance", boattweaks$distance);
	}

	@Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
	private void boattweaks$readAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
		boattweaks$distance = compoundTag.getDouble("BoatTweaksDistance");
	}
}
