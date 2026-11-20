package ru.plotmod.effects;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.OreBlock;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.function.Consumer;

public class EffectUtils {

    /** Пассивные бафы сюжетных кирок, когда она в главной руке */
    public static void applyPickaxePassives(PlayerEntity player, int tier) {
        if (player.getWorld().getTime() % 100 != 0) return; // раз в 5 сек обновляем
        switch (tier) {
            case 2 -> add(player, net.minecraft.entity.effect.StatusEffects.NIGHT_VISION, 240, 0, true);
            case 3 -> add(player, net.minecraft.entity.effect.StatusEffects.HASTE, 200, 0, false);
            case 4 -> {
                add(player, net.minecraft.entity.effect.StatusEffects.HASTE, 200, 1, false);
                add(player, net.minecraft.entity.effect.StatusEffects.REGENERATION, 100, 0, false);
            }
            case 5 -> {
                add(player, net.minecraft.entity.effect.StatusEffects.HASTE, 300, 2, false);
                add(player, net.minecraft.entity.effect.StatusEffects.FIRE_RESISTANCE, 300, 0, false);
                add(player, net.minecraft.entity.effect.StatusEffects.SPEED, 300, 0, false);
            }
            default -> {}
        }
    }

    private static void add(PlayerEntity p, net.minecraft.entity.effect.StatusEffect e, int dur, int amp, boolean ambient) {
        p.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(e, dur, amp, true, ambient));
    }

    public static void burstParticles(ServerWorld world, BlockPos pos, ParticleEffect type,
                                      int count, double dx, double dy, double dz) {
        for (int i = 0; i < count; i++) {
            world.spawnParticles(type,
                    pos.getX() + 0.5 + (world.random.nextDouble() - 0.5) * dx * 2,
                    pos.getY() + 0.5 + (world.random.nextDouble() - 0.5) * dy * 2,
                    pos.getZ() + 0.5 + (world.random.nextDouble() - 0.5) * dz * 2,
                    1, 0.05, 0.05, 0.05, 0.01);
        }
    }

    /** Земляная кирка: рыхлит землю вокруг (превращает grass/dirt в farmland визуально — просто частицы + звук) */
    public static void tillAround(World world, BlockPos pos, int radius) {
        burstIfServer(world, pos, ParticleTypes.ANGRY_VILLAGER, 10, 1, 0.5, 1);
    }

    private static void burstIfServer(World world, BlockPos pos, ParticleEffect t, int c, double a, double b, double d) {
        if (world instanceof ServerWorld sw) burstParticles(sw, pos, t, c, a, b, d);
    }

    /** Каменная кирка: ломает stone/cobble/netherrack в радиусе */
    public static void breakRadiusOre(World world, PlayerEntity player, BlockPos center, int radius, boolean stoneOnly) {
        for (BlockPos p : BlockPos.iterate(center.add(-radius, -radius, -radius), center.add(radius, radius, radius))) {
            BlockState s = world.getBlockState(p);
            if (s.isAir()) continue;
            boolean ok = stoneOnly && (s.getBlock() == net.minecraft.block.Blocks.STONE
                    || s.getBlock() == net.minecraft.block.Blocks.COBBLESTONE
                    || s.getBlock() == net.minecraft.block.Blocks.DEEPSLATE);
            if (ok && world.breakBlock(p, true, player)) {
                burstIfServer(world, p.up(), ParticleTypes.CLOUD, 4, 0.3, 0.3, 0.3);
            }
        }
    }

    public static void applyToNearbyMobs(World world, PlayerEntity player, double radius, Consumer<LivingEntity> action) {
        for (LivingEntity e : world.getEntitiesByClass(LivingEntity.class,
                player.getBoundingBox().expand(radius), e -> e instanceof Monster && e != player)) {
            action.accept(e);
        }
    }

    /** Железная кирка: магнит лута */
    public static void magnetLoot(World world, PlayerEntity player, double radius) {
        Vec3d target = player.getEyePos();
        for (ItemEntity item : world.getEntitiesByClass(ItemEntity.class,
                player.getBoundingBox().expand(radius), i -> true)) {
            item.setVelocity(target.subtract(item.getPos()).normalize().multiply(0.6));
        }
        burstIfServer(world, player.getBlockPos().up(), ParticleTypes.WITCH, 25, 1.5, 1, 1.5);
    }

    /** Подсветка руд частицами над ними */
    public static void highlightOres(World world, PlayerEntity player, int radius) {
        BlockPos origin = player.getBlockPos();
        int found = 0;
        for (int x = -radius; x <= radius && found < 20; x++)
            for (int y = -radius; y <= radius && found < 20; y++)
                for (int z = -radius; z <= radius && found < 20; z++) {
                    BlockPos p = origin.add(x, y, z);
                    if (world.getBlockState(p).getBlock() instanceof OreBlock) {
                        burstIfServer(world, p.up(), ParticleTypes.TOWN_AURA, 6, 0.4, 0.2, 0.4);
                        found++;
                    }
                }
    }

    /** Алмазная кирка: кристаллический шторм */
    public static void crystalStorm(ServerWorld world, PlayerEntity player) {
        BlockPos pos = player.getBlockPos();
        for (int i = 0; i < 60; i++) {
            double ang = world.random.nextDouble() * Math.PI * 2;
            double r = 1 + world.random.nextDouble() * 3;
            world.spawnParticles(ParticleTypes.END_ROD,
                    pos.getX() + 0.5 + Math.cos(ang) * r,
                    pos.getY() + 1 + world.random.nextDouble() * 2,
                    pos.getZ() + 0.5 + Math.sin(ang) * r,
                    1, 0, 0.05, 0, 0.02);
        }
        applyToNearbyMobs(world, player, 6, e -> {
            e.damage(damageSources(world, player), 4f);
            e.addVelocity(0, 0.7, 0);
        });
        world.playSound(null, pos, SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.PLAYERS, 0.6f, 1.8f);
    }

    private static net.minecraft.entity.damage.DamageSource damageSources(ServerWorld world, PlayerEntity player) {
        return world.getDamageSources().playerAttack(player);
    }

    /** Легендарная кирка: Извержение */
    public static void eruption(World world, PlayerEntity player) {
        BlockPos pos = player.getBlockPos();
        int radius = 4;
        for (BlockPos p : BlockPos.iterate(pos.add(-radius, -2, -radius), pos.add(radius, 2, radius))) {
            BlockState s = world.getBlockState(p);
            Block b = s.getBlock();
            if (b instanceof OreBlock || b == net.minecraft.block.Blocks.STONE
                    || b == net.minecraft.block.Blocks.DEEPSLATE || b == net.minecraft.block.Blocks.NETHERRACK) {
                world.breakBlock(p, true, player);
                if (world instanceof ServerWorld sw)
                    burstParticles(sw, p.up(), ParticleTypes.LAVA, 3, 0.3, 0.3, 0.3);
            }
        }
        if (world instanceof ServerWorld sw)
            burstParticles(sw, pos.up(), ParticleTypes.FLAME, 80, 3, 2, 3);
        world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 1f, 0.8f);
    }

    public static void giveXpOrb(World world, BlockPos pos, int amount) {
        if (!world.isClient) {
            ExperienceOrbEntity orb = new ExperienceOrbEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, amount);
            world.spawnEntity(orb);
        }
    }

    public static void compassSparkle(PlayerEntity player) {
        World world = player.getWorld();
        if (world instanceof ServerWorld sw) {
            burstParticles(sw, player.getBlockPos().up(2), ParticleTypes.TOTEM_OF_UNDYING, 20, 0.8, 0.8, 0.8);
        }
    }
}
