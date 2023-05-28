package snownee.boatrace;

import java.util.Map;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.config.KiwiConfig;

@KiwiConfig
public class BoatRaceTweaksConfig {

	private static final Object2FloatMap<Block> frictionOverridesMap = new Object2FloatOpenHashMap<>();
	@KiwiModule.Skip
	public static boolean inited;
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
	public static float outOfControlTicks = 120F;
	@KiwiConfig.Path("boostingBlock.block")
	public static String boostingBlockId = "magenta_glazed_terracotta";
	public static Block boostingBlock;
	@KiwiConfig.Range(min = 0)
	@KiwiConfig.Path("boostingBlock.ticks")
	public static int boostingTicks = 15;
	@KiwiConfig.Path("boostingBlock.force")
	public static float boostingForce = 0.08F;
	@KiwiConfig.Path("jumpingBlock.block")
	public static String jumpingBlockId = "yellow_glazed_terracotta";
	public static Block jumpingBlock;
	@KiwiConfig.Range(min = 0)
	@KiwiConfig.Path("jumpingBlock.force")
	public static float jumpingForce = 1F;
	@KiwiConfig.Range(min = 0, max = 1)
	@KiwiConfig.Path("wallHit.speedLoss")
	public static float wallHitSpeedLoss = 0.2F;
	@KiwiConfig.Range(min = 0)
	@KiwiConfig.Path("wallHit.cooldown")
	public static int wallHitCooldown = 10;

	public static void onChanged(String path) {
		if (inited) {
			deferredRefresh();
		}
	}

	public static void deferredRefresh() {
		frictionOverridesMap.clear();
		frictionOverrides.forEach((k, v) -> {
			Block block = Registry.BLOCK.get(ResourceLocation.tryParse(k));
			if (block != Blocks.AIR) {
				frictionOverridesMap.put(block, v.floatValue());
			}
		});
		boostingBlock = Registry.BLOCK.get(ResourceLocation.tryParse(boostingBlockId));
		jumpingBlock = Registry.BLOCK.get(ResourceLocation.tryParse(jumpingBlockId));
		BoatRaceTweaks.LOGGER.info("BoatRaceTweaks configurations reloaded");
	}

	public static float getFriction(Block block) {
		return frictionOverridesMap.getOrDefault(block, block.getFriction());
	}
}
