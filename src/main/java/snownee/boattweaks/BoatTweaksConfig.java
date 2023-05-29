package snownee.boattweaks;

import java.util.Map;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMaps;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import snownee.kiwi.config.KiwiConfig;

@KiwiConfig
public class BoatTweaksConfig {

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

	public static void refresh() {
		BoatTweaks.CONFIG = Instance.fromLocal();
		BoatTweaks.LOGGER.info("BoatTweaks configurations reloaded");
	}

	// Normalize it to the vanilla values
	public static class Instance {
		public Object2FloatMap<Block> frictionOverrides = Object2FloatMaps.emptyMap();
		public float forwardForce = 0.04F;
		public float backwardForce = 0.005F;
		public float turningForce = 1F;
		public float turningForceInAir = 1F;
		public float outOfControlTicks = 60F;
		public Block boostingBlock = Blocks.AIR;
		public int boostingTicks = 0;
		public float boostingForce = 0F;
		public Block ejectingBlock = Blocks.AIR;
		public float ejectingForce = 0F;
		public float wallHitSpeedLoss = 0F;
		public int wallHitCooldown = 0;

		public static Instance fromNetwork(FriendlyByteBuf buf) {
			Instance instance = new Instance();
			int size = buf.readVarInt();
			instance.frictionOverrides = new Object2FloatOpenHashMap<>(size);
			for (int i = 0; i < size; i++) {
				instance.frictionOverrides.put(Registry.BLOCK.byId(buf.readVarInt()), buf.readFloat());
			}
			instance.forwardForce = buf.readFloat();
			instance.backwardForce = buf.readFloat();
			instance.turningForce = buf.readFloat();
			instance.turningForceInAir = buf.readFloat();
			instance.outOfControlTicks = buf.readFloat();
			instance.boostingBlock = Registry.BLOCK.byId(buf.readVarInt());
			instance.boostingTicks = buf.readVarInt();
			instance.boostingForce = buf.readFloat();
			instance.ejectingBlock = Registry.BLOCK.byId(buf.readVarInt());
			instance.ejectingForce = buf.readFloat();
			instance.wallHitSpeedLoss = buf.readFloat();
			instance.wallHitCooldown = buf.readVarInt();
			return instance;
		}

		public static Instance fromLocal() {
			Instance instance = new Instance();
			instance.frictionOverrides = new Object2FloatOpenHashMap<>(BoatTweaksConfig.frictionOverrides.size());
			BoatTweaksConfig.frictionOverrides.forEach((k, v) -> {
				Block block = Registry.BLOCK.get(ResourceLocation.tryParse(k));
				if (block != Blocks.AIR) {
					instance.frictionOverrides.put(block, v.floatValue());
				}
			});
			instance.forwardForce = BoatTweaksConfig.forwardForce;
			instance.backwardForce = BoatTweaksConfig.backwardForce;
			instance.turningForce = BoatTweaksConfig.turningForce;
			instance.turningForceInAir = BoatTweaksConfig.turningForceInAir;
			instance.outOfControlTicks = BoatTweaksConfig.outOfControlTicks;
			instance.boostingBlock = Registry.BLOCK.get(ResourceLocation.tryParse(BoatTweaksConfig.boostingBlock));
			instance.boostingTicks = BoatTweaksConfig.boostingTicks;
			instance.boostingForce = BoatTweaksConfig.boostingForce;
			instance.ejectingBlock = Registry.BLOCK.get(ResourceLocation.tryParse(BoatTweaksConfig.ejectingBlock));
			instance.ejectingForce = BoatTweaksConfig.ejectingForce;
			instance.wallHitSpeedLoss = BoatTweaksConfig.wallHitSpeedLoss;
			instance.wallHitCooldown = BoatTweaksConfig.wallHitCooldown;
			return instance;
		}

		public void toNetwork(FriendlyByteBuf buf) {
			buf.writeVarInt(frictionOverrides.size());
			frictionOverrides.forEach((k, v) -> {
				buf.writeVarInt(Registry.BLOCK.getId(k));
				buf.writeFloat(v);
			});
			buf.writeFloat(forwardForce);
			buf.writeFloat(backwardForce);
			buf.writeFloat(turningForce);
			buf.writeFloat(turningForceInAir);
			buf.writeFloat(outOfControlTicks);
			buf.writeVarInt(Registry.BLOCK.getId(boostingBlock));
			buf.writeVarInt(boostingTicks);
			buf.writeFloat(boostingForce);
			buf.writeVarInt(Registry.BLOCK.getId(ejectingBlock));
			buf.writeFloat(ejectingForce);
			buf.writeFloat(wallHitSpeedLoss);
			buf.writeVarInt(wallHitCooldown);
		}

		public float getFriction(Block block) {
			return frictionOverrides.getOrDefault(block, block.getFriction());
		}
	}
}
