package ru.plotmod.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import ru.plotmod.effects.EffectUtils;
import ru.plotmod.story.StoryManager;

/**
 * Сюжетная кирка. Каждый уровень (tier) даёт:
 *  - повышенную скорость/урон
 *  - ауру частиц
 *  - пассивные бафы при нахождении в руке
 *  - активную способность по правому клику (если есть энергия)
 */
public class StoryPickaxeItem extends PickaxeItem {
    public final int storyTier; // 1..5

    public StoryPickaxeItem(ToolMaterial material, int attackDamage, float attackSpeed,
                            int storyTier, Rarity rarity) {
        super(material, attackDamage, attackSpeed,
                new FabricItemSettings().rarity(rarity));
        this.storyTier = storyTier;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, net.minecraft.entity.Entity entity, int slot, boolean selected) {
        if (!world.isClient && entity instanceof PlayerEntity player && selected) {
            EffectUtils.applyPickaxePassives(player, storyTier);
        }
        // Заряд энергии способности
        NbtCompound nbt = stack.getOrCreateNbt();
        int energy = nbt.getInt("energy");
        int maxEnergy = maxEnergy();
        if (energy < maxEnergy && world.getTime % 40 == 0) {
            nbt.putInt("energy", energy + 1);
        }
    }

    public int maxEnergy() {
        return switch (storyTier) {
            case 1 -> 20;
            case 2 -> 30;
            case 3 -> 40;
            case 4 -> 60;
            default -> 100;
        };
    }

    @Override
    public boolean use(World world, PlayerEntity player, net.minecraft.util.Hand hand) {
        ItemStack stack = player.getMainHandStack();
        if (stack.getItem() != this) return false;
        NbtCompound nbt = stack.getOrCreateNbt();
        int energy = nbt.getInt("energy");
        int cost = abilityCost();
        if (energy < cost) {
            player.sendMessage(net.minecraft.text.Text.translatable("msg.plotmod.no_energy"), true);
            return false;
        }
        nbt.putInt("energy", energy - cost);
        activateAbility(world, player);
        return true;
    }

    private int abilityCost() {
        return switch (storyTier) {
            case 1 -> 5;
            case 2 -> 8;
            case 3 -> 12;
            case 4 -> 15;
            default -> 25;
        };
    }

    /** Активные способности каждой кирки */
    private void activateAbility(World world, PlayerEntity player) {
        BlockPos pos = player.getBlockPos();
        world.playSound(null, pos, SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.PLAYERS, 1f, 1f);
        switch (storyTier) {
            case 1 -> { // Земляная: "Толчок" — подбрасывает игрока и рыхлит землю вокруг
                player.addVelocity(0, 0.8, 0);
                EffectUtils.burstParticles((ServerWorld) world, pos.up(), ParticleTypes.DUST, 30, 0.6, 0.5, 0.2);
                EffectUtils.tillAround(world, pos, 1);
                player.sendMessage(net.minecraft.text.Text.translatable("ability.plotmod.earth"), true);
            }
            case 2 -> { // Каменная: "Seismic Slam" — ломает камень 3x3 под ногами + слоновость врагам
                EffectUtils.breakRadiusOre(world, player, pos.down(), 1, true);
                EffectUtils.applyToNearbyMobs(world, player, 5,
                        e -> e.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                                net.minecraft.entity.effect.StatusEffects.SLOWNESS, 100, 2)));
                player.sendMessage(net.minecraft.text.Text.translatable("ability.plotmod.stone"), true);
            }
            case 3 -> { // Железная: "Магнит руды" — притягивает дроп и подсветка руд вокруг
                EffectUtils.magnetLoot(world, player, 10);
                EffectUtils.highlightOres(world, player, 12);
                player.sendMessage(net.minecraft.text.Text.translatable("ability.plotmod.iron"), true);
            }
            case 4 -> { // Алмазная: "Кристаллический шторм" — молнии-частицы + регенерация
                EffectUtils.crystalStorm((ServerWorld) world, player);
                player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                        net.minecraft.entity.effect.StatusEffects.REGENERATION, 200, 1));
                player.sendMessage(net.minecraft.text.Text.translatable("ability.plotmod.diamond"), true);
            }
            default -> { // Легендарная "Сердце Горы": "Извержение" — огромный взрыв руды + Haste III + Fire Res
                EffectUtils.eruption(world, player);
                player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                        net.minecraft.entity.effect.StatusEffects.HASTE, 600, 2));
                player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                        net.minecraft.entity.effect.StatusEffects.FIRE_RESISTANCE, 600, 0));
                player.sendMessage(net.minecraft.text.Text.translatable("ability.plotmod.legend"), true);
            }
        }
    }

    @Override
    public ItemStack finishMining(ItemStack stack, World world, net.minecraft.block.BlockState state,
                                  BlockPos pos, LivingEntity miner) {
        if (miner instanceof PlayerEntity player) {
            // Бонусный опыт за добычу сюжетной киркой
            if (!world.isClient && world.random.nextFloat() < 0.15f * storyTier) {
                EffectUtils.giveXpOrb(world, pos.up(), 1 + storyTier);
            }
            // Визуал: частицы при поломке блока
            if (world instanceof ServerWorld sw) {
                EffectUtils.burstParticles(sw, pos.up(),
                        storyTier >= 4 ? ParticleTypes.END_ROD : ParticleTypes.CRIT,
                        8, 0.3, 0.3, 0.3);
            }
        }
        return super.finishMining(stack, world, state, pos, miner);
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return switch (storyTier) {
            case 1 -> Rarity.COMMON;
            case 2 -> Rarity.UNCOMMON;
            case 3 -> Rarity.RARE;
            default -> Rarity.EPIC;
        };
    }
}
