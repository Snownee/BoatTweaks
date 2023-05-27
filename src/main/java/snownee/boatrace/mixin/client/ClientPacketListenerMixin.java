package snownee.boatrace.mixin.client;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.multiplayer.ClientPacketListener;
import snownee.boatrace.BRTClientPacketListener;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin implements BRTClientPacketListener {
	private boolean boatrace$ghostMode;

	@Override
	public boolean boatrace$getGhostMode() {
		return boatrace$ghostMode;
	}

	@Override
	public void boatrace$setGhostMode(boolean value) {
		boatrace$ghostMode = value;
	}
}
