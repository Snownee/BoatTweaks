package snownee.boattweaks.compat.kubejs;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import net.minecraft.core.Registry;
import snownee.boattweaks.BoatTweaks;

public class BoatTweaksKubeJSPlugin extends KubeJSPlugin {

	@Override
	public void init() {
		BoatTweaks.LOGGER.info("KubeJS detected, loading Boat Tweaks KubeJS plugin");
		BoatTweaks.SPECIAL_BLOCK_LISTENERS.add((boat, blockState, blockPos) -> {
			SpecialBlockEventJS event = new SpecialBlockEventJS(boat, blockState, blockPos);
			BoatTweaksKubeJSEvents.ON_SPECIAL_BLOCK.post(Registry.BLOCK.getKey(blockState.getBlock()), event);
		});
	}

	@Override
	public void registerBindings(BindingsEvent event) {
		event.add("BoatTweaks", BoatTweaksKJS.class);
	}

	@Override
	public void registerEvents() {
		BoatTweaksKubeJSEvents.GROUP.register();
	}
}
