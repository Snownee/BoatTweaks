package snownee.boattweaks.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.server.level.ServerPlayer;
import snownee.boattweaks.duck.BTServerPlayer;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin implements BTServerPlayer {

	private boolean boattweaks$verified;

	@Override
	public void boattweaks$setVerified(boolean value) {
		boattweaks$verified = value;
	}

	@Override
	public boolean boattweaks$isVerified() {
		return boattweaks$verified;
	}
}
