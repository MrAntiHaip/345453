package ru.plotmod;

import net.minecraft.item.Item;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.CompassItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.TieredItem;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Rarity;
import ru.plotmod.item.LuckyCompassItem;
import ru.plotmod.item.StoryPickaxes;



public class ModItems {

    // ===== КИРКИ СЮЖЕТА (5 уровней апгрейда) =====
    public static final Item EARTH_PICKAXE =
            StoryPickaxes.create("earth_pickaxe", 1, Rarity.COMMON);      // Земляная
    public static final Item STONE_PICKAXE =
            StoryPickaxes.create("stone_pickaxe", 2, Rarity.UNCOMMON);   // Каменная
    public static final Item IRON_PICKAXE =
            StoryPickaxes.create("iron_pickaxe", 3, Rarity.RARE);        // Железная сюжетная
    public static final Item DIAMOND_PICKAXE =
            StoryPickaxes.create("diamond_pickaxe", 4, Rarity.EPIC);     // Алмазная сюжетная
    public static final Item LEGEND_PICKAXE =
            StoryPickaxes.create("legend_pickaxe", 5, Rarity.EPIC);      // Легендарная "Сердце Горы"

    // ===== КОМПАС УДАЧИ (рулетка) =====
    public static final Item LUCKY_COMPASS = new LuckyCompassItem(
            new Item.Settings().maxCount(1).rarity(Rarity.EPIC));

    public static void register() {
        StoryPickaxes.registerAll();
        RegistryHelper.register(LUCKY_COMPASS, "lucky_compass");
    }
}
