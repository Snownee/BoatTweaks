package snownee.boattweaks;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import snownee.boattweaks.duck.BTClientPacketListener;
import snownee.boattweaks.network.SUpdateGhostModePacket;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.KiwiGO;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.Mod;

@Mod(BoatTweaks.ID)
@KiwiModule
public class BoatTweaks extends AbstractModule {

	public static final String ID = "boattweaks";
	public static final Logger LOGGER = LogManager.getLogger("BoatTweaks");
	public static BoatTweaksConfig.Instance CONFIG = new BoatTweaksConfig.Instance();

	public static final KiwiGO<SoundEvent> BOOST = go(() -> new SoundEvent(new ResourceLocation(ID, "boost")));
	public static final KiwiGO<SoundEvent> EJECT = go(() -> new SoundEvent(new ResourceLocation(ID, "eject")));
	public static final GameRules.Key<GameRules.BooleanValue> AUTO_REMOVE_BOAT = GameRules.register(ID + ":autoRemoveBoat", GameRules.Category.MISC, GameRules.BooleanValue.create(false));
	public static final GameRules.Key<GameRules.BooleanValue> GHOST_MODE = GameRules.register(ID + ":ghostMode", GameRules.Category.MISC, GameRules.BooleanValue.create(false, (server, rule) -> {
		server.getPlayerList().getPlayers().forEach(p -> {
			SUpdateGhostModePacket.sync(p, rule.get());
		});
	}));

	public static boolean isGhostMode(Level level) {
		if (level.isClientSide) {
			return ((BTClientPacketListener) Objects.requireNonNull(Minecraft.getInstance().getConnection())).boattweaks$getGhostMode();
		} else {
			return level.getGameRules().getBoolean(GHOST_MODE);
		}
	}
}
