package snownee.boattweaks.network;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import com.google.common.base.Preconditions;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import snownee.boattweaks.BoatSettings;
import snownee.boattweaks.util.CommonProxy;
import snownee.kiwi.network.KiwiPacket;
import snownee.kiwi.network.PacketHandler;

@KiwiPacket(value = "sync_config", dir = KiwiPacket.Direction.PLAY_TO_CLIENT)
public class SSyncConfigPacket extends PacketHandler {
	public static SSyncConfigPacket I;

	public static void sync(ServerPlayer player, BoatSettings config) {
//		if (respond) {
//			BoatTweaks.LOGGER.info("Requesting config from {}", player.getGameProfile().getName());
//		}
		I.send(player, buf -> {
			buf.writeUtf(CommonProxy.getVersion());
//			buf.writeBoolean(respond);
			config.toNetwork(buf);
		});
	}

	@Override
	public CompletableFuture<FriendlyByteBuf> receive(Function<Runnable, CompletableFuture<FriendlyByteBuf>> executor, FriendlyByteBuf buf, @Nullable ServerPlayer player) {
		Preconditions.checkState(Objects.equals(buf.readUtf(), CommonProxy.getVersion()), "Version mismatch");
//		boolean respond = buf.readBoolean();
		BoatSettings settings = BoatSettings.fromNetwork(buf);
		return executor.apply(() -> {
			BoatSettings.DEFAULT = settings;
//			if (respond) {
//				CPingServerPacket.ping();
//			}
		});
	}
}
