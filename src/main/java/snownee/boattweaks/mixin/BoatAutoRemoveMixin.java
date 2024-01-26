package snownee.boattweaks.mixin;

import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import snownee.boattweaks.BoatTweaks;

@Mixin(Boat.class)
public class BoatAutoRemoveMixin {

	@Unique
	private long lastTimeRiding;

	@Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
	private void addAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
		compoundTag.putLong("LastTimeRiding", lastTimeRiding);
	}

	@Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
	private void readAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
		lastTimeRiding = compoundTag.getLong("LastTimeRiding");
	}

	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	private void tick(CallbackInfo ci) {
		Boat boat = (Boat) (Object) this;
		final var level = boat.level();
		if (level.isClientSide || boat instanceof ChestBoat) {
			return;
		}
		if (!level.getGameRules().getBoolean(BoatTweaks.AUTO_REMOVE_BOAT)) {
			return;
		}
		long gameTime = level.getGameTime();
		if (boat.getControllingPassenger() instanceof Player) {
			lastTimeRiding = gameTime;
		} else if (gameTime - lastTimeRiding > 40 && gameTime % 20 == 0 && level.getNearestPlayer(boat, 5) == null) {
			boat.discard();
			ci.cancel();
		}
	}

}
