package ru.plotmod.item;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Rarity;
import ru.plotmod.PlotMod;
import ru.plotmod.RegistryHelper;

public class StoryPickaxes {

    public static final StoryPickaxeItem[] ALL = new StoryPickaxeItem[6]; // индекс = storyTier

    private static ToolMaterial mat(int tier) {
        return switch (tier) {
            case 1 -> new StoryToolMaterial(0, 90, 3.5f, 1.0f, 10, () -> new ItemStack(Items.CLAY_BALL));      // Земляная
            case 2 -> new StoryToolMaterial(1, 160, 5.5f, 1.5f, 12, () -> new ItemStack(Items.COBBLESTONE));   // Каменная
            case 3 -> new StoryToolMaterial(2, 320, 7.5f, 2.5f, 15, () -> new ItemStack(Items.IRON_INGOT));   // Железная
            case 4 -> new StoryToolMaterial(3, 600, 9.5f, 3.5f, 18, () -> new ItemStack(Items.DIAMOND));      // Алмазная
            default -> new StoryToolMaterial(4, 1200, 13f, 5.0f, 25, () -> new ItemStack(Items.NETHERITE_INGOT)); // Легендарная
        };
    }

    private static int dmg(int tier) {
        return switch (tier) { case 1 -> 1; case 2 -> 2; case 3 -> 3; case 4 -> 4; default -> 6; };
    }

    public static StoryPickaxeItem create(String name, int storyTier, Rarity rarity) {
        StoryPickaxeItem item = new StoryPickaxeItem(mat(storyTier), dmg(storyTier), -2.8f, storyTier, rarity);
        ALL[storyTier] = item;
        RegistryHelper.register(item, name);
        return item;
    }

    public static void registerAll() {
        PlotMod.LOGGER.info("Сюжетные кирки зарегистрированы: {}", ALL.length - 1);
    }
}
