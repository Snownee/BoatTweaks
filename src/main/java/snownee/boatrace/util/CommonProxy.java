package snownee.boatrace.util;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import snownee.boatrace.BoatRaceTweaks;
import snownee.boatrace.BoatRaceTweaksConfig;
import snownee.boatrace.network.SUpdateGhostModePacket;
import snownee.kiwi.config.KiwiConfigManager;

public class CommonProxy implements ModInitializer {
	@Override
	public void onInitialize() {
		// Currently in 1.19.2, the serverInit method has a bug that it will not be called for integrated server.
		ServerLifecycleEvents.SERVER_STARTING.register($ -> {
			if (!$.isDedicatedServer()) {
				KiwiConfigManager.getHandler(BoatRaceTweaksConfig.class).refresh();
			}
			BoatRaceTweaksConfig.inited = true;
			BoatRaceTweaksConfig.deferredRefresh();
		});
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			if (handler.player.level.getGameRules().getBoolean(BoatRaceTweaks.GHOST_MODE)) {
				SUpdateGhostModePacket.sync(handler.player, true);
			}
		});
	}
}
