package snownee.boattweaks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.block.state.BlockState;

public interface SpecialBlockEvent {
	void on(Boat boat, BlockState blockState, BlockPos blockPos);
}
