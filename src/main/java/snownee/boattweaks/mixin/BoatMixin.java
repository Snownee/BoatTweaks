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

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.block.Block;
import snownee.boattweaks.BoatTweaks;
import snownee.boattweaks.duck.BTBoostingBoat;
import snownee.boattweaks.duck.BTServerPlayer;
import snownee.boattweaks.network.CPingServerPacket;

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
		return BoatTweaks.CONFIG.getFriction(block);
	}

	@ModifyVariable(method = "controlBoat", at = @At(value = "STORE", ordinal = 0), index = 1)
	private float boattweaks$modifyForce(float f) {
		if (status == Boat.Status.ON_LAND) {
			if (inputUp) {
				BTBoostingBoat boat = (BTBoostingBoat) this;
				f += BoatTweaks.CONFIG.forwardForce - 0.04F + boat.boattweaks$getExtraForwardForce();
			}
			if (inputDown) {
				f -= BoatTweaks.CONFIG.backwardForce - 0.005F;
			}
			if (inputLeft) {
				deltaRotation += 1 - BoatTweaks.CONFIG.turningForce;
			}
			if (inputRight) {
				deltaRotation -= 1 - BoatTweaks.CONFIG.turningForce;
			}
		} else if (status == Boat.Status.IN_AIR) {
			if (inputLeft) {
				deltaRotation += 1 - BoatTweaks.CONFIG.turningForceInAir;
			}
			if (inputRight) {
				deltaRotation -= 1 - BoatTweaks.CONFIG.turningForceInAir;
			}
		}
		return f;
	}

	@Inject(method = "tick", at = @At("HEAD"))
	private void boattweaks$tick(CallbackInfo ci) {
		Boat boat = (Boat) (Object) this;
		if (!boat.level.isClientSide && boat.getControllingPassenger() instanceof BTServerPlayer player && !player.boattweaks$isVerified()) {
			ServerPlayer serverplayer = (ServerPlayer) player;
			if (CPingServerPacket.PENDING.contains(serverplayer.getUUID())) {
				BoatTweaks.LOGGER.info("Player {} is not verified. Stopping riding.", serverplayer.getGameProfile().getName());
				serverplayer.stopRiding();
				serverplayer.sendSystemMessage(Component.literal("You are not allowed to control boats unless BoatTweaks is installed."));
				return;
			} else {
				player.boattweaks$setVerified(true);
			}
		}
		if (boattweaks$wallHitCd > 0) {
			boattweaks$wallHitCd--;
		} else if (boat.horizontalCollision) {
			boattweaks$wallHitCd = BoatTweaks.CONFIG.wallHitCooldown;
			float scale = 1 - BoatTweaks.CONFIG.wallHitSpeedLoss;
			boat.setDeltaMovement(boat.getDeltaMovement().multiply(scale, 1, scale));
		}
	}

	@ModifyConstant(method = "tick", constant = @Constant(floatValue = 60F))
	private float boattweaks$modifyTimeOutTicks(float f) {
		return BoatTweaks.CONFIG.outOfControlTicks;
	}
}
