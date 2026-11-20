package ru.plotmod.item;

import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;

import java.util.function.Supplier;

public class StoryToolMaterial implements ToolMaterial {
    private final int miningLevel;
    private final int durability;
    private final float miningSpeed;
    private final float attackDamage;
    private final int enchantability;
    private final Supplier<ItemStack> repairIngredient;

    public StoryToolMaterial(int miningLevel, int durability, float miningSpeed,
                             float attackDamage, int enchantability,
                             Supplier<ItemStack> repairIngredient) {
        this.miningLevel = miningLevel;
        this.durability = durability;
        this.miningSpeed = miningSpeed;
        this.attackDamage = attackDamage;
        this.enchantability = enchantability;
        this.repairIngredient = repairIngredient;
    }

    @Override public int getDurability() { return durability; }
    @Override public float getMiningSpeedMultiplier() { return miningSpeed; }
    @Override public float getAttackDamage() { return attackDamage; }
    @Override public int getMiningLevel() { return miningLevel; }
    @Override public int getEnchantability() { return enchantability; }
    @Override public Ingredient getRepairIngredient() { return Ingredient.ofItems(repairIngredient.get().getItem()); }
}
