package snownee.boattweaks.duck;

import org.jetbrains.annotations.Nullable;

import snownee.boattweaks.BoatSettings;

public interface BTConfigurableBoat {
	BoatSettings boattweaks$getSettings();

	void boattweaks$setSettings(@Nullable BoatSettings settings);

}
