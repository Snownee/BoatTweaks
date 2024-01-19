package snownee.boattweaks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import snownee.boattweaks.duck.BTServerPlayer;

@Mixin(value = Boat.class, priority = 500)
public class BoatVerificationMixin {
	// We don't inject into Player.startRiding because the player can start riding before the server ping
	@Inject(method = "interact", at = @At("HEAD"), cancellable = true)
	private void interact(Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResult> ci) {
		if (player.level.isClientSide || player.isSecondaryUseActive() || !(player instanceof BTServerPlayer serverPlayer)) {
			return;
		}
		if (!serverPlayer.boattweaks$isVerified()) {
			//TODO (1.20): fallback translatable
			player.displayClientMessage(Component.translatable("Please install BoatTweaks first."), true);
			ci.setReturnValue(InteractionResult.FAIL);
		}
	}

	@Inject(method = "tick", at = @At("HEAD"))
	private void tick(CallbackInfo ci) {
		Boat boat = (Boat) (Object) this;
		if (boat.level.isClientSide || boat.tickCount < 10) {
			return;
		}
		if (boat.getControllingPassenger() instanceof ServerPlayer player && !((BTServerPlayer) player).boattweaks$isVerified()) {
			player.stopRiding();
			player.displayClientMessage(Component.translatable("Please install BoatTweaks first."), true);
		}
	}
}
