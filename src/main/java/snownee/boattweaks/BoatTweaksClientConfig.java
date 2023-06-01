package snownee.boattweaks;

import snownee.kiwi.config.KiwiConfig;

@KiwiConfig(type = KiwiConfig.ConfigType.CLIENT)
public class BoatTweaksClientConfig {
	@KiwiConfig.Range(min = 0)
	public static int unridingLockDelay = 10;
}
