package snownee.boatrace.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import snownee.boatrace.BoatRaceTweaks;

@Mixin(Boat.class)
public class BoatGhostModeMixin {

	@Inject(method = "push", at = @At("HEAD"), cancellable = true)
	private void boatrace$push(Entity entity, CallbackInfo ci) {
		if (BoatRaceTweaks.isGhostMode(entity.level)) {
			ci.cancel();
		}
	}

	@Inject(method = "canCollideWith", at = @At("HEAD"), cancellable = true)
	private void boatrace$canCollideWith(Entity entity, CallbackInfoReturnable<Boolean> cir) {
		if (BoatRaceTweaks.isGhostMode(entity.level) && entity instanceof Boat) {
			cir.setReturnValue(false);
		}
	}
}
