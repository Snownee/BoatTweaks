package snownee.boattweaks.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
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
	@Unique
	private int unridingDelay;

	@Inject(method = "tick", at = @At("TAIL"))
	private void tick(boolean bl, float f, CallbackInfo ci) {
		if (BoatTweaksClientConfig.unridingLockDelay == 0 || !options.keyShift.isDown()) {
			unridingDelay = 0;
			return;
		}
		LocalPlayer player = Minecraft.getInstance().player;
		if (player != null && player.getVehicle() instanceof Boat) {
			if (unridingDelay == 0 && BoatTweaksClientConfig.unridingLockDelay <= 100) {
				player.displayClientMessage(Component.translatable("boattweaks.unriding_hint"), true);
			}
			unridingDelay++;
			if (BoatTweaksClientConfig.unridingLockDelay > unridingDelay) {
				KeyboardInput input = (KeyboardInput) (Object) this;
				input.shiftKeyDown = false;
			}
		} else {
			unridingDelay = 0;
		}
	}
}
