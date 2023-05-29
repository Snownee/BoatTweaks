package snownee.boattweaks.network;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import com.google.common.base.Preconditions;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import snownee.boattweaks.BoatTweaks;
import snownee.boattweaks.BoatTweaksConfig;
import snownee.boattweaks.util.CommonProxy;
import snownee.kiwi.network.KiwiPacket;
import snownee.kiwi.network.PacketHandler;

@KiwiPacket(value = "sync_config", dir = KiwiPacket.Direction.PLAY_TO_CLIENT)
public class SSyncConfigPacket extends PacketHandler {
	public static SSyncConfigPacket I;

	public static void sync(ServerPlayer player, boolean respond, BoatTweaksConfig.Instance config) {
		if (respond) {
			BoatTweaks.LOGGER.info("Requesting config from {}", player.getGameProfile().getName());
			CPingServerPacket.PENDING.add(player.getUUID());
		}
		I.send(player, buf -> {
			buf.writeUtf(CommonProxy.getVersion());
			buf.writeBoolean(respond);
			config.toNetwork(buf);
		});
	}

	@Override
	public CompletableFuture<FriendlyByteBuf> receive(Function<Runnable, CompletableFuture<FriendlyByteBuf>> executor, FriendlyByteBuf buf, @Nullable ServerPlayer player) {
		Preconditions.checkState(Objects.equals(buf.readUtf(), CommonProxy.getVersion()), "Version mismatch");
		boolean respond = buf.readBoolean();
		BoatTweaksConfig.Instance instance = BoatTweaksConfig.Instance.fromNetwork(buf);
		return executor.apply(() -> {
			BoatTweaks.CONFIG = instance;
			if (respond) {
				CPingServerPacket.ping();
			}
		});
	}
}
