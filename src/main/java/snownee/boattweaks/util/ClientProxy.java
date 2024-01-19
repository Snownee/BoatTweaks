package snownee.boattweaks.util;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import snownee.boattweaks.BoatSettings;

public class ClientProxy implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			BoatSettings.DEFAULT = new BoatSettings();
		});
	}
}
