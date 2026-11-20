package ru.plotmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class PlotModClient implements ClientModInitializer {

    public static final ItemGroup GROUP = FabricItemGroupBuilder.create(PlotMod.id("main_group"))
            .icon(() -> new ItemStack(ModItems.LEGEND_PICKAXE))
            .appendItems(stacks -> {
                stacks.add(new ItemStack(ModItems.EARTH_PICKAXE));
                stacks.add(new ItemStack(ModItems.STONE_PICKAXE));
                stacks.add(new ItemStack(ModItems.IRON_PICKAXE));
                stacks.add(new ItemStack(ModItems.DIAMOND_PICKAXE));
                stacks.add(new ItemStack(ModItems.LEGEND_PICKAXE));
                stacks.add(new ItemStack(ModItems.LUCKY_COMPASS));
            })
            .build();

    @Override
    public void onInitializeClient() {
        PlotMod.LOGGER.info("Клиентская часть Plot Mod загружена.");
    }
}
