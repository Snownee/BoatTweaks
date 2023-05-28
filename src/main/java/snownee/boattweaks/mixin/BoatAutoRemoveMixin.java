package snownee.boattweaks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import snownee.boattweaks.BoatTweaks;

@Mixin(Boat.class)
public class BoatAutoRemoveMixin {

	private long lastTimeRiding;

	@Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
	private void boattweaks$addAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
		compoundTag.putLong("LastTimeRiding", lastTimeRiding);
	}

	@Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
	private void boattweaks$readAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
		lastTimeRiding = compoundTag.getLong("LastTimeRiding");
	}

	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	private void boattweaks$tick(CallbackInfo ci) {
		Boat boat = (Boat) (Object) this;
		if (boat.level.isClientSide || boat.getType() == EntityType.CHEST_BOAT) {
			return;
		}
		if (!boat.level.getGameRules().getBoolean(BoatTweaks.AUTO_REMOVE_BOAT)) {
			return;
		}
		long gameTime = boat.level.getGameTime();
		if (boat.getControllingPassenger() instanceof Player) {
			lastTimeRiding = gameTime;
		} else if (gameTime - lastTimeRiding > 40 && gameTime % 20 == 0 && boat.level.getNearestPlayer(boat, 5) == null) {
			boat.discard();
			ci.cancel();
		}
	}

}
