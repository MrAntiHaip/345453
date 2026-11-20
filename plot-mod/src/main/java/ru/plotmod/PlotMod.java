package ru.plotmod;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlotMod implements ModInitializer {
    public static final String MOD_ID = "plotmod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        ModItems.register();
        PlotModCommands.register();
        PlotModEvents.register();
        StoryManager.init();
        RouletteManager.init();
        EventManager.init();
        LOGGER.info("Plot Mod инициализирован: Дима и Ульяна начинают путь!");
    }
}
