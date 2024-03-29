package snownee.boattweaks;

import java.util.Map;

import snownee.kiwi.config.KiwiConfig;

@KiwiConfig
public class BoatTweaksCommonConfig {

	public static Map<String, Double> frictionOverrides = Map.of(
			"blue_ice", 0.95,
			"packed_ice", 0.93,
			"ice", 0.93,
			"magenta_glazed_terracotta", 0.93,
			"yellow_glazed_terracotta", 0.93
	);
	@KiwiConfig.Range(min = 0)
	public static float forwardForce = 0.08F;
	@KiwiConfig.Range(min = 0)
	public static float backwardForce = 0.01F;
	@KiwiConfig.Range(min = 0)
	public static float turningForce = 0.9F;
	@KiwiConfig.Range(min = 0)
	public static float turningForceInAir = 0.25F;
	@KiwiConfig.Range(min = 0)
	public static float stepUpHeight = 1F;
	@KiwiConfig.Range(min = 0)
	public static int outOfControlTicks = 120;
	@KiwiConfig.Path("boostingBlock.block")
	public static String boostingBlock = "magenta_glazed_terracotta";
	@KiwiConfig.Range(min = 0)
	@KiwiConfig.Path("boostingBlock.ticks")
	public static int boostingTicks = 15;
	@KiwiConfig.Path("boostingBlock.force")
	public static float boostingForce = 0.08F;
	@KiwiConfig.Path("ejectingBlock.block")
	public static String ejectingBlock = "yellow_glazed_terracotta";
	@KiwiConfig.Range(min = 0)
	@KiwiConfig.Path("ejectingBlock.force")
	public static float ejectingForce = 1F;
	@KiwiConfig.Range(min = 0, max = 1)
	@KiwiConfig.Path("wallHit.speedLoss")
	public static float wallHitSpeedLoss = 0.4F;
	@KiwiConfig.Range(min = 0)
	@KiwiConfig.Path("wallHit.cooldown")
	public static int wallHitCooldown = 10;
	@KiwiConfig.Path("degradeOverDistance.forceLossPerMeter")
	public static float degradeForceLossPerMeter = 0.0001F;
	@KiwiConfig.Path("degradeOverDistance.forceLossStartFrom")
	public static int degradeForceLossStartFrom = 5000;
	@KiwiConfig.Path("degradeOverDistance.forceMaxLoss")
	public static float degradeForceMaxLoss = 0F;

	public static void refresh() {
		BoatSettings.DEFAULT = BoatSettings.fromLocal();
		BoatTweaks.LOGGER.info("BoatTweaks configurations reloaded");
	}

}
