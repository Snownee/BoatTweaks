package snownee.boattweaks;

import java.util.List;

import org.slf4j.Logger;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
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

@KiwiModule
public class BoatTweaks extends AbstractModule {

	public static final String ID = "boattweaks";
	public static final Logger LOGGER = LogUtils.getLogger();
	public static final KiwiGO<SoundEvent> BOOST = go(() ->  SoundEvent.createVariableRangeEvent(new ResourceLocation(ID, "boost")));
	public static final KiwiGO<SoundEvent> EJECT = go(() ->  SoundEvent.createVariableRangeEvent(new ResourceLocation(ID, "eject")));
	public static final GameRules.Key<GameRules.BooleanValue> AUTO_REMOVE_BOAT = GameRules.register(ID + ":autoRemoveBoat", GameRules.Category.MISC, GameRules.BooleanValue.create(false));
	public static final GameRules.Key<GameRules.BooleanValue> GHOST_MODE = GameRules.register(ID + ":ghostMode", GameRules.Category.MISC, GameRules.BooleanValue.create(false, (server, rule) -> {
		server.getPlayerList().getPlayers().forEach(p -> {
			SUpdateGhostModePacket.sync(p, rule.get());
		});
	}));
	public static final Object2IntMap<Block> CUSTOM_SPECIAL_BLOCKS = new Object2IntOpenCustomHashMap<>(8, Util.identityStrategy());
	public static final List<SpecialBlockEvent> SPECIAL_BLOCK_LISTENERS = Lists.newArrayList();

	public static void postSpecialBlockEvent(Boat boat, BlockState blockState, BlockPos blockPos) {
		SPECIAL_BLOCK_LISTENERS.forEach(listener -> listener.on(boat, blockState, blockPos));
	}
}
