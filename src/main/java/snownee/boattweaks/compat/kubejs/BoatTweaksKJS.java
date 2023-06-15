package snownee.boattweaks.compat.kubejs;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.world.level.block.Block;
import snownee.boattweaks.BoatTweaks;

public class BoatTweaksKJS {
	public static Object2IntMap<Block> getCustomSpecialBlocks() {
		return BoatTweaks.CUSTOM_SPECIAL_BLOCKS;
	}
}
