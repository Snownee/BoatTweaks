package snownee.boattweaks;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.block.Block;
import snownee.boattweaks.duck.BTMovementDistance;

public class BoatTweaksUtil {
	public static Object2IntMap<Block> getCustomSpecialBlocks() {
		return BoatTweaks.CUSTOM_SPECIAL_BLOCKS;
	}

	public static double getMovementDistance(Boat boat) {
		if (boat.level.isClientSide) {
			BoatTweaks.LOGGER.warn("BoatTweaksUtil#getMovementDistance can only be called on server side.");
			return 0;
		}
		return ((BTMovementDistance) boat).boattweaks$getDistance();
	}

	public static void resetMovementDistance(Boat boat) {
		if (boat.level.isClientSide) {
			BoatTweaks.LOGGER.warn("BoatTweaksUtil#resetMovementDistance can only be called on server side.");
			return;
		}
		((BTMovementDistance) boat).boattweaks$resetDistance();
	}
}
