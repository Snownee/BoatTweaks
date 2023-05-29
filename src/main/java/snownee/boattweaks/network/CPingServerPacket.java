package snownee.boattweaks.network;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Sets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import snownee.boattweaks.BoatTweaks;
import snownee.kiwi.network.KiwiPacket;
import snownee.kiwi.network.PacketHandler;

@KiwiPacket(value = "ping_server", dir = KiwiPacket.Direction.PLAY_TO_SERVER)
public class CPingServerPacket extends PacketHandler {
	public static final Set<UUID> PENDING = Sets.newHashSet();
	public static CPingServerPacket I;

	public static void ping() {
		I.sendToServer(buf -> {
		});
	}

	@Override
	public CompletableFuture<FriendlyByteBuf> receive(Function<Runnable, CompletableFuture<FriendlyByteBuf>> executor, FriendlyByteBuf buf, @Nullable ServerPlayer player) {
		return executor.apply(() -> {
			BoatTweaks.LOGGER.info("Received config from {}", player.getGameProfile().getName());
			PENDING.remove(Objects.requireNonNull(player).getUUID());
		});
	}
}
