package snownee.boattweaks;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMaps;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

// Normalize it to the vanilla values
public class BoatSettings {
	public static BoatSettings DEFAULT = new BoatSettings();
	public Object2FloatMap<Block> frictionOverrides = Object2FloatMaps.emptyMap();
	public float forwardForce = 0.04F;
	public float backwardForce = 0.005F;
	public float turningForce = 1F;
	public float turningForceInAir = 1F;
	public float stepUpHeight = 0F;
	public float outOfControlTicks = 60F;
	public Block boostingBlock = Blocks.AIR;
	public int boostingTicks = 0;
	public float boostingForce = 0F;
	public Block ejectingBlock = Blocks.AIR;
	public float ejectingForce = 0F;
	public float wallHitSpeedLoss = 0F;
	public int wallHitCooldown = 0;

	public static BoatSettings fromNetwork(FriendlyByteBuf buf) {
		BoatSettings settings = new BoatSettings();
		int size = buf.readVarInt();
		settings.frictionOverrides = new Object2FloatOpenHashMap<>(size);
		for (int i = 0; i < size; i++) {
			settings.frictionOverrides.put(Registry.BLOCK.byId(buf.readVarInt()), buf.readFloat());
		}
		settings.frictionOverrides = Object2FloatMaps.unmodifiable(settings.frictionOverrides);
		settings.forwardForce = buf.readFloat();
		settings.backwardForce = buf.readFloat();
		settings.turningForce = buf.readFloat();
		settings.turningForceInAir = buf.readFloat();
		settings.stepUpHeight = buf.readFloat();
		settings.outOfControlTicks = buf.readFloat();
		settings.boostingBlock = Registry.BLOCK.byId(buf.readVarInt());
		settings.boostingTicks = buf.readVarInt();
		settings.boostingForce = buf.readFloat();
		settings.ejectingBlock = Registry.BLOCK.byId(buf.readVarInt());
		settings.ejectingForce = buf.readFloat();
		settings.wallHitSpeedLoss = buf.readFloat();
		settings.wallHitCooldown = buf.readVarInt();
		return settings;
	}

	public static void fromNBT(CompoundTag tag, BoatSettings settings) {
		CompoundTag overridesTag = tag.getCompound("frictionOverrides");
		settings.frictionOverrides = new Object2FloatOpenHashMap<>(overridesTag.size());
		for (String key : overridesTag.getAllKeys()) {
			Block block = Registry.BLOCK.get(ResourceLocation.tryParse(key));
			if (block != Blocks.AIR) {
				settings.frictionOverrides.put(block, overridesTag.getFloat(key));
			}
		}
		settings.frictionOverrides = Object2FloatMaps.unmodifiable(settings.frictionOverrides);
		settings.forwardForce = tag.getFloat("forwardForce");
		settings.backwardForce = tag.getFloat("backwardForce");
		settings.turningForce = tag.getFloat("turningForce");
		settings.turningForceInAir = tag.getFloat("turningForceInAir");
		settings.stepUpHeight = tag.getFloat("stepUpHeight");
		settings.outOfControlTicks = tag.getFloat("outOfControlTicks");
		settings.boostingBlock = Registry.BLOCK.get(ResourceLocation.tryParse(tag.getString("boostingBlock")));
		settings.boostingTicks = tag.getInt("boostingTicks");
		settings.boostingForce = tag.getFloat("boostingForce");
		settings.ejectingBlock = Registry.BLOCK.get(ResourceLocation.tryParse(tag.getString("ejectingBlock")));
		settings.ejectingForce = tag.getFloat("ejectingForce");
		settings.wallHitSpeedLoss = tag.getFloat("wallHitSpeedLoss");
		settings.wallHitCooldown = tag.getInt("wallHitCooldown");
	}

	public static BoatSettings fromLocal() {
		BoatSettings settings = new BoatSettings();
		settings.frictionOverrides = new Object2FloatOpenHashMap<>(BoatTweaksConfig.frictionOverrides.size());
		BoatTweaksConfig.frictionOverrides.forEach((k, v) -> {
			Block block = Registry.BLOCK.get(ResourceLocation.tryParse(k));
			if (block != Blocks.AIR) {
				settings.frictionOverrides.put(block, v.floatValue());
			}
		});
		settings.frictionOverrides = Object2FloatMaps.unmodifiable(settings.frictionOverrides);
		settings.forwardForce = BoatTweaksConfig.forwardForce;
		settings.backwardForce = BoatTweaksConfig.backwardForce;
		settings.turningForce = BoatTweaksConfig.turningForce;
		settings.turningForceInAir = BoatTweaksConfig.turningForceInAir;
		settings.stepUpHeight = BoatTweaksConfig.stepUpHeight;
		settings.outOfControlTicks = BoatTweaksConfig.outOfControlTicks;
		settings.boostingBlock = Registry.BLOCK.get(ResourceLocation.tryParse(BoatTweaksConfig.boostingBlock));
		settings.boostingTicks = BoatTweaksConfig.boostingTicks;
		settings.boostingForce = BoatTweaksConfig.boostingForce;
		settings.ejectingBlock = Registry.BLOCK.get(ResourceLocation.tryParse(BoatTweaksConfig.ejectingBlock));
		settings.ejectingForce = BoatTweaksConfig.ejectingForce;
		settings.wallHitSpeedLoss = BoatTweaksConfig.wallHitSpeedLoss;
		settings.wallHitCooldown = BoatTweaksConfig.wallHitCooldown;
		return settings;
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
		buf.writeFloat(stepUpHeight);
		buf.writeFloat(outOfControlTicks);
		buf.writeVarInt(Registry.BLOCK.getId(boostingBlock));
		buf.writeVarInt(boostingTicks);
		buf.writeFloat(boostingForce);
		buf.writeVarInt(Registry.BLOCK.getId(ejectingBlock));
		buf.writeFloat(ejectingForce);
		buf.writeFloat(wallHitSpeedLoss);
		buf.writeVarInt(wallHitCooldown);
	}

	public CompoundTag toNBT() {
		CompoundTag tag = new CompoundTag();
		CompoundTag overridesTag = new CompoundTag();
		frictionOverrides.forEach((k, v) -> overridesTag.putFloat(Registry.BLOCK.getKey(k).toString(), v));
		tag.put("frictionOverrides", overridesTag);
		tag.putFloat("forwardForce", forwardForce);
		tag.putFloat("backwardForce", backwardForce);
		tag.putFloat("turningForce", turningForce);
		tag.putFloat("turningForceInAir", turningForceInAir);
		tag.putFloat("stepUpHeight", stepUpHeight);
		tag.putFloat("outOfControlTicks", outOfControlTicks);
		tag.putString("boostingBlock", Registry.BLOCK.getKey(boostingBlock).toString());
		tag.putInt("boostingTicks", boostingTicks);
		tag.putFloat("boostingForce", boostingForce);
		tag.putString("ejectingBlock", Registry.BLOCK.getKey(ejectingBlock).toString());
		tag.putFloat("ejectingForce", ejectingForce);
		tag.putFloat("wallHitSpeedLoss", wallHitSpeedLoss);
		tag.putInt("wallHitCooldown", wallHitCooldown);
		return tag;
	}

	public float getFriction(Block block) {
		return frictionOverrides.getOrDefault(block, block.getFriction());
	}

	public BoatSettings copy() {
		BoatSettings settings = new BoatSettings();
		settings.frictionOverrides = frictionOverrides;
		settings.forwardForce = forwardForce;
		settings.backwardForce = backwardForce;
		settings.turningForce = turningForce;
		settings.turningForceInAir = turningForceInAir;
		settings.stepUpHeight = stepUpHeight;
		settings.outOfControlTicks = outOfControlTicks;
		settings.boostingBlock = boostingBlock;
		settings.boostingTicks = boostingTicks;
		settings.boostingForce = boostingForce;
		settings.ejectingBlock = ejectingBlock;
		settings.ejectingForce = ejectingForce;
		settings.wallHitSpeedLoss = wallHitSpeedLoss;
		settings.wallHitCooldown = wallHitCooldown;
		return settings;
	}
}
