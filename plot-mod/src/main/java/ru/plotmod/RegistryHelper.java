package ru.plotmod;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class RegistryHelper {
    public static void register(Item item, String name) {
        Registry.register(Registries.ITEM, PlotMod.id(name), item);
    }
}
