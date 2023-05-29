package snownee.boattweaks.util;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import snownee.boattweaks.BoatTweaks;
import snownee.boattweaks.BoatTweaksConfig;
import snownee.boattweaks.network.SSyncConfigPacket;
import snownee.boattweaks.network.SUpdateGhostModePacket;
import snownee.kiwi.config.KiwiConfigManager;

public class CommonProxy implements ModInitializer {
	private static String version;

	public static String getVersion() {
		return version;
	}

	@Override
	public void onInitialize() {
		// Currently in 1.19.2, the serverInit method has a bug that it will not be called for integrated server.
		ServerLifecycleEvents.SERVER_STARTING.register($ -> {
			version = FabricLoader.getInstance().getModContainer(BoatTweaks.ID).map(container -> container.getMetadata().getVersion().getFriendlyString()).orElseThrow();
			if (!$.isDedicatedServer()) {
				KiwiConfigManager.getHandler(BoatTweaksConfig.class).refresh();
			}
			BoatTweaksConfig.refresh();
		});
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			SSyncConfigPacket.sync(handler.player, true, BoatTweaks.CONFIG);
			if (handler.player.level.getGameRules().getBoolean(BoatTweaks.GHOST_MODE)) {
				SUpdateGhostModePacket.sync(handler.player, true);
			}
		});
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			BoatTweaks.CONFIG = new BoatTweaksConfig.Instance();
		});
	}
}
