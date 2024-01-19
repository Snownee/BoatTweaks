package snownee.boattweaks.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.client.multiplayer.ClientPacketListener;
import snownee.boattweaks.duck.BTClientPacketListener;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin implements BTClientPacketListener {
	@Unique
	private boolean ghostMode;

	@Override
	public boolean boattweaks$getGhostMode() {
		return ghostMode;
	}

	@Override
	public void boattweaks$setGhostMode(boolean value) {
		ghostMode = value;
	}
}
