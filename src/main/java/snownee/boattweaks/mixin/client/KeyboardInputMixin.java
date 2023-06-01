package snownee.boattweaks.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.vehicle.Boat;
import snownee.boattweaks.BoatTweaksClientConfig;

@Mixin(KeyboardInput.class)
public class KeyboardInputMixin {

	@Final
	@Shadow
	private Options options;
	private int boattweaks$unridingDelay;

	@Inject(method = "tick", at = @At("TAIL"))
	private void boattweaks$tick(boolean bl, float f, CallbackInfo ci) {
		if (BoatTweaksClientConfig.unridingLockDelay == 0 || !options.keyShift.isDown()) {
			boattweaks$unridingDelay = 0;
			return;
		}
		LocalPlayer player = Minecraft.getInstance().player;
		if (player != null && player.getVehicle() instanceof Boat) {
			if (boattweaks$unridingDelay == 0 && BoatTweaksClientConfig.unridingLockDelay <= 100) {
				player.displayClientMessage(Component.translatable("boattweaks.unriding_hint"), true);
			}
			boattweaks$unridingDelay++;
			if (BoatTweaksClientConfig.unridingLockDelay > boattweaks$unridingDelay) {
				KeyboardInput input = (KeyboardInput) (Object) this;
				input.shiftKeyDown = false;
			}
		} else {
			boattweaks$unridingDelay = 0;
		}
	}
}
