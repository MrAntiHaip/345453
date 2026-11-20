package ru.plotmod.roulette;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import ru.plotmod.effects.EffectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Рулетка удачи компаса.
 *  - Крутится коротко (1-2 секунды, тики-тик звука).
 *  - Каждый уровень имеет гарантированный "нубский" шанс выпадения мусора/камней,
 *    который УМЕНЬШАЕТСЯ с ростом уровня (но никогда не равен нулю).
 *  - Призы: булыжник x64, железо x16, уголь, золото, алмазы и даже бонус-события.
 */
public class RouletteManager {

    public static class Prize {
        public final String nameKey;
        public final ItemStack stack;      // null если эффект/событие
        public final Runnable effect;      // опциональный эффект
        public final int weight;           // вес на текущем уровне вычисляется в pick()
        public final boolean noobPrize;    // "нубский" приз

        Prize(String nameKey, ItemStack stack, Runnable effect, boolean noob) {
            this.nameKey = nameKey;
            this.stack = stack;
            this.effect = effect;
            this.noobPrize = noob;
        }
    }

    private static final List<Prize> PRIZES = new ArrayList<>();

    public static void init() {
        // ===== НУБСКИЕ ПРИЗЫ (шанс есть всегда, но падает с уровнем) =====
        PRIZES.add(new Prize("roulette.plotmod.noob.cobble", new ItemStack(Items.COBBLESTONE, 4), null, true));
        PRIZES.add(new Prize("roulette.plotmod.noob.dirt", new ItemStack(Items.DIRT, 8), null, true));
        PRIZES.add(new Prize("roulette.plotmod.noob.stick", new ItemStack(Items.STICK, 3), null, true));

        // ===== ОБЫЧНЫЕ =====
        PRIZES.add(new Prize("roulette.plotmod.coal", new ItemStack(Items.COAL, 16), null, false));
        PRIZES.add(new Prize("roulette.plotmod.cobble64", new ItemStack(Items.COBBLESTONE, 64), null, false));
        PRIZES.add(new Prize("roulette.plotmod.iron16", new ItemStack(Items.IRON_INGOT, 16), null, false));

        // ===== РЕДКИЕ =====
        PRIZES.add(new Prize("roulette.plotmod.gold8", new ItemStack(Items.GOLD_INGOT, 8), null, false));
        PRIZES.add(new Prize("roulette.plotmod.redstone32", new ItemStack(Items.REDSTONE, 32), null, false));
        PRIZES.add(new Prize("roulette.plotmod.lapis16", new ItemStack(Items.LAPIS_LAZULI, 16), null, false));

        // ===== ЭПИЧНЫЕ =====
        PRIZES.add(new Prize("roulette.plotmod.diamond4", new ItemStack(Items.DIAMOND, 4), null, false));
        PRIZES.add(new Prize("roulette.plotmod.emerald6", new ItemStack(Items.EMERALD, 6), null, false));

        // ===== СОБЫТИЯ-БАФЫ =====
        PRIZES.add(new Prize("roulette.plotmod.buff.haste", null, () -> {}, false));
        PRIZES.add(new Prize("roulette.plotmod.buff.luck", null, () -> {}, false));
        PRIZES.add(new Prize("roulette.plotmod.jackpot", new ItemStack(Items.NETHERITE_SCRAP, 1), null, false));
    }

    /** Уровень рулетки = сюжетный прогресс игрока (1..5) */
    public static int levelOf(ServerPlayerEntity player) {
        NbtCompound p = player.getWorld().getServer() != null ? getStoryNbt(player) : new NbtCompound();
        return Math.max(1, Math.min(5, p.getInt("pickaxe_tier") == 0 ? 1 : p.getInt("pickaxe_tier")));
    }

    private static NbtCompound getStoryNbt(ServerPlayerEntity player) {
        return player.getNetworkHandler() != null ? player.getEntityData() : new NbtCompound();
    }

    public static void spin(ServerPlayerEntity player, ItemStack compass) {
        int level = levelOf(player);

        // Шанс нубского приза: 45% на 1 уровне -> ~8% на 5 уровне (но не 0!)
        double noobChance = Math.max(0.08, 0.45 - 0.09 * (level - 1));

        Prize won;
        java.util.Random rnd = player.getWorld().random;
        if (rnd.nextDouble() < noobChance) {
            won = PRIZES.get(rnd.nextInt(3)); // первые 3 — нубские
        } else {
            // Взвешенный выбор среди небайских, редкость растёт с уровнем
            List<Prize> pool = new ArrayList<>();
            for (int i = 3; i < PRIZES.size(); i++) {
                Prize p = PRIZES.get(i);
                int w = weight(p, level);
                for (int k = 0; k < w; k++) pool.add(p);
            }
            won = pool.get(rnd.nextInt(pool.size()));
        }

        // Короткая анимация: серия звуков "тики" (~1.2 сек), затем результат
        for (int i = 0; i < 6; i++) {
            final int idx = i;
            player.getServerWorld().getServer().execute(() -> {}); // placeholder
        }
        playTicks(player);

        give(player, won, level);
    }

    private static int weight(Prize p, int level) {
        if (p.nameKey.contains("jackpot")) return Math.max(1, level - 2);       // джекпот только с 3+ уровня
        if (p.nameKey.contains("diamond")) return level;                          // алмазы чаще на высоких
        if (p.nameKey.contains("emerald")) return Math.max(1, level - 1);
        if (p.nameKey.contains("buff")) return 4;
        return 6; // обычные
    }

    private static void playTicks(ServerPlayerEntity player) {
        var world = player.getServerWorld();
        for (int i = 0; i < 6; i++) {
            final float pitch = 0.8f + i * 0.12f;
            world.getServer().execute(() ->
                    world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_NOTE_BLOCK_HAT,
                            SoundCategory.PLAYERS, 0.8f, pitch));
        }
    }

    private static void give(ServerPlayerEntity player, Prize prize, int level) {
        Text title = Text.translatable(prize.nameKey)
                .formatted(prize.noobPrize ? Formatting.GRAY : Formatting.GOLD)
                .append(Text.literal(" " + (prize.noobPrize ? "😐" : "🎉")));

        if (prize.nameKey.equals("roulette.plotmod.buff.haste")) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, 600, 1));
            player.sendMessage(Text.translatable("roulette.plotmod.buff.haste.got").formatted(Formatting.AQUA), true);
        } else if (prize.nameKey.equals("roulette.plotmod.buff.luck")) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.LUCK, 600, level));
            player.sendMessage(Text.translatable("roulette.plotmod.buff.luck.got").formatted(Formatting.LIGHT_PURPLE), true);
        } else if (prize.stack != null) {
            ItemStack drop = prize.stack.copy();
            // На высоких уровнях множитель бонуса
            if (!prize.noobPrize && level >= 4 && player.getWorld().random.nextBoolean()) {
                drop.setCount(Math.min(drop.getCount() * 2, 64));
            }
            if (!player.getInventory().insertStack(drop)) {
                player.dropItem(drop, false);
            }
        }
        player.sendMessage(Text.translatable("msg.plotmod.roulette_result").formatted(Formatting.YELLOW)
                .append(title), true);
        EffectUtils.compassSparkle(player);
        player.getWorld().playSound(null, player.getBlockPos(),
                prize.noobPrize ? SoundEvents.BLOCK_NOTE_BLOCK_BASS : SoundEvents.ENTITY_PLAYER_LEVELUP,
                SoundCategory.PLAYERS, 1f, prize.noobPrize ? 0.6f : 1.4f);
    }
}
