package snownee.boattweaks.mixin.client;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.multiplayer.ClientPacketListener;
import snownee.boattweaks.duck.BTClientPacketListener;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin implements BTClientPacketListener {
	private boolean boattweaks$ghostMode;

	@Override
	public boolean boattweaks$getGhostMode() {
		return boattweaks$ghostMode;
	}

	@Override
	public void boattweaks$setGhostMode(boolean value) {
		boattweaks$ghostMode = value;
	}
}
