package ru.plotmod.events;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import ru.plotmod.effects.EffectUtils;

import java.util.Random;

/** Случайные события мира, происходящие во время сюжетного прохождения */
public class EventManager {

    private static long nextEventTime = 0;
    private static final Random RND = new Random();

    public static void init() {}

    /** Вызывается из тика сервера (каждые ~20 секунд шанс) */
    public static void onServerTick(net.minecraft.server.MinecraftServer server) {
        long time = server.getOverworld().getTime();
        if (time < nextEventTime) return;
        nextEventTime = time + 400L + RND.nextInt(600); // каждые 20-50 сек
        if (RND.nextFloat() > 0.35f) return;

        for (ServerPlayerEntity p : server.getPlayerManager().getPlayerList()) {
            if (p.isRemoved()) continue;
            triggerRandom(p);
        }
    }

    public static void triggerRandom(ServerPlayerEntity player) {
        int ev = RND.nextInt(5);
        switch (ev) {
            case 0 -> meteorShower(player);
            case 1 -> luckyRain(player);
            case 2 -> shadowAmbush(player);
            case 3 -> hermitBlessing(player);
            default -> treasureCache(player);
        }
    }

    /** Падающие звёзды: бонусные частицы + немного опыта */
    private static void meteorShower(ServerPlayerEntity player) {
        player.sendMessage(Text.translatable("event.plotmod.meteor").formatted(Formatting.LIGHT_PURPLE), false);
        EffectUtils.burstParticles(player.getServerWorld(), player.getBlockPos().up(10),
                net.minecraft.particle.ParticleTypes.FALLING_STAR, 25, 4, 2, 4);
        EffectUtils.giveXpOrb(player.getWorld(), player.getBlockPos().up(), 5);
        player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT,
                SoundCategory.WORLD_EVENTS, 0.7f, 1.4f);
    }

    /** Дождь удачи: Speed + Jump Boost на минуту */
    private static void luckyRain(ServerPlayerEntity player) {
        player.sendMessage(Text.translatable("event.plotmod.lucky_rain").formatted(Formatting.GREEN), false);
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 1200, 1));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP, 1200, 1));
        EffectUtils.burstParticles(player.getServerWorld(), player.getBlockPos().up(),
                net.minecraft.particle.ParticleTypes.HAPPY_VILLAGER, 30, 2, 1, 2);
    }

    /** Засадa теней: спавнит 2 зомби, но даёт алмаз если выживешь (просто выдаём предупреждение + меч) */
    private static void shadowAmbush(ServerPlayerEntity player) {
        player.sendMessage(Text.translatable("event.plotmod.shadow").formatted(Formatting.DARK_GRAY), false);
        for (int i = 0; i < 2; i++) {
            var zombie = new net.minecraft.entity.mob.ZombieEntity(net.minecraft.entity.EntityType.ZOMBIE, player.getServerWorld());
            zombie.refreshPositionAndAngles(player.getX() + RND.nextDouble() * 6 - 3,
                    player.getY(), player.getZ() + RND.nextDouble() * 6 - 3, 0, 0);
            player.getServerWorld().spawnEntity(zombie);
        }
        player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.ENTITY_ZOMBIE_AMBIENT,
                SoundCategory.HOSTILE, 1f, 0.8f);
    }

    /** Благословение отшельника: регенерация и золотое яблоко */
    private static void hermitBlessing(ServerPlayerEntity player) {
        player.sendMessage(Text.translatable("event.plotmod.hermit").formatted(Formatting.GOLD), false);
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 1));
        player.giveItemStack(new ItemStack(Items.GOLDEN_CARROT, 3));
    }

    /** Тайник с сокровищем: кучка ресурсов */
    private static void treasureCache(ServerPlayerEntity player) {
        player.sendMessage(Text.translatable("event.plotmod.treasure").formatted(Formatting.AQUA), false);
        player.giveItemStack(new ItemStack(Items.IRON_INGOT, 6));
        player.giveItemStack(new ItemStack(Items.BREAD, 4));
        EffectUtils.compassSparkle(player);
    }
}
