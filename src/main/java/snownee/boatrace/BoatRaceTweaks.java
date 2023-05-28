package snownee.boatrace;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import snownee.boatrace.duck.BRTClientPacketListener;
import snownee.boatrace.network.SUpdateGhostModePacket;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.KiwiGO;
import snownee.kiwi.KiwiModule;

@KiwiModule
public class BoatRaceTweaks extends AbstractModule {

	public static final String ID = "boatracetweaks";
	public static final Logger LOGGER = LogManager.getLogger("BoatRaceTweaks");

	public static final KiwiGO<SoundEvent> BOOST_SOUND = go(() -> new SoundEvent(new ResourceLocation(ID, "boost_sound")));
	public static final KiwiGO<SoundEvent> JUMP_SOUND = go(() -> new SoundEvent(new ResourceLocation(ID, "jump_sound")));
	public static final GameRules.Key<GameRules.BooleanValue> AUTO_REMOVE_BOAT = GameRules.register(ID + ":autoRemoveBoat", GameRules.Category.MISC, GameRules.BooleanValue.create(false));
	public static final GameRules.Key<GameRules.BooleanValue> GHOST_MODE = GameRules.register(ID + ":ghostMode", GameRules.Category.MISC, GameRules.BooleanValue.create(false, (server, rule) -> {
		server.getPlayerList().getPlayers().forEach(p -> {
			SUpdateGhostModePacket.sync(p, rule.get());
		});
	}));

	public static boolean isGhostMode(Level level) {
		if (level.isClientSide) {
			return ((BRTClientPacketListener) Objects.requireNonNull(Minecraft.getInstance().getConnection())).boatrace$getGhostMode();
		} else {
			return level.getGameRules().getBoolean(GHOST_MODE);
		}
	}
}
