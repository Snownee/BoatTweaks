package snownee.boattweaks;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import snownee.boattweaks.network.SUpdateGhostModePacket;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.KiwiGO;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.loader.event.InitEvent;

@KiwiModule
public class BoatTweaks extends AbstractModule {

	public static final String ID = "boattweaks";
	public static final Logger LOGGER = LogUtils.getLogger();
	public static final KiwiGO<SoundEvent> BOOST = go(() -> new SoundEvent(new ResourceLocation(ID, "boost")));
	public static final KiwiGO<SoundEvent> EJECT = go(() -> new SoundEvent(new ResourceLocation(ID, "eject")));
	public static final GameRules.Key<GameRules.BooleanValue> AUTO_REMOVE_BOAT = GameRules.register(ID + ":autoRemoveBoat", GameRules.Category.MISC, GameRules.BooleanValue.create(false));
	public static final GameRules.Key<GameRules.BooleanValue> GHOST_MODE = GameRules.register(ID + ":ghostMode", GameRules.Category.MISC, GameRules.BooleanValue.create(false, (server, rule) -> {
		server.getPlayerList().getPlayers().forEach(p -> {
			SUpdateGhostModePacket.sync(p, rule.get());
		});
	}));
	public static final Object2IntMap<Block> CUSTOM_SPECIAL_BLOCKS = new Object2IntOpenCustomHashMap<>(8, Util.identityStrategy());
	public static final List<SpecialBlockEvent> SPECIAL_BLOCK_LISTENERS = Lists.newArrayList();
	public static final EntityDataSerializer<Optional<BoatSettings>> OPTIONAL_BOAT_SETTINGS = EntityDataSerializer.optional((buf, settings) -> settings.toNetwork(buf), BoatSettings::fromNetwork);
	public static final EntityDataAccessor<Optional<BoatSettings>> DATA_ID_BOAT_SETTINGS = SynchedEntityData.defineId(Boat.class, OPTIONAL_BOAT_SETTINGS);
	public static final EntityDataAccessor<Float> DATA_ID_MOVEMENT_DISTANCE = SynchedEntityData.defineId(Boat.class, EntityDataSerializers.FLOAT);

	public static void postSpecialBlockEvent(Boat boat, BlockState blockState, BlockPos blockPos) {
		SPECIAL_BLOCK_LISTENERS.forEach(listener -> listener.on(boat, blockState, blockPos));
	}

	@Override
	protected void init(InitEvent event) {
		event.enqueueWork(() -> EntityDataSerializers.registerSerializer(OPTIONAL_BOAT_SETTINGS));
	}
}
