package ru.plotmod.story;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.nbt.NbtCompound;

/**
 * Сюжет и роли: Дима и Ульяна.
 * Игрок выбирает роль командой /plot role dima | ulyana
 * Далее проходит главы сюжета, каждая глава апгрейдит кирку и открывает рулетку.
 */
public class StoryManager {

    public enum Role { DIMA, ULYANA }

    public static class Chapter {
        public final int id;
        public final String titleKey;
        public final String descKey;
        public final int requiredBlocksMined; // прогресс главы (ломание блоков)
        public final int unlockPickaxeTier;   // какая кирка выдаётся после главы

        Chapter(int id, String t, String d, int need, int unlock) {
            this.id = id; this.titleKey = t; this.descKey = d;
            this.requiredBlocksMined = need; this.unlockPickaxeTier = unlock;
        }
    }

    public static final Chapter[] CHAPTERS = {
        new Chapter(1, "story.plotmod.ch1.title", "story.plotmod.ch1.desc", 30, 2),   // Каменная
        new Chapter(2, "story.plotmod.ch2.title", "story.plotmod.ch2.desc", 80, 3),   // Железная
        new Chapter(3, "story.plotmod.ch3.title", "story.plotmod.ch3.desc", 150, 4),  // Алмазная
        new Chapter(4, "story.plotmod.ch4.title", "story.plotmod.ch4.desc", 250, 5),  // Легендарная
        new Chapter(5, "story.plotmod.ch5.title", "story.plotmod.ch5.desc", 0, 0),    // Финал
    };

    public static void init() {}

    public static NbtCompound data(ServerPlayerEntity player) {
        NbtCompound nbt = player.getNetworkHandler() != null ? player.getPersistentData() : new NbtCompound();
        return nbt;
    }

    public static void setRole(ServerPlayerEntity player, Role role) {
        player.getPersistentData().putString("plotmod_role", role.name());
        player.getPersistentData().putInt("plotmod_chapter", 1);
        player.getPersistentData().putInt("plotmod_mined", 0);
        player.getPersistentData().putInt("pickaxe_tier", 1);
        Text welcome = role == Role.DIMA
                ? Text.translatable("story.plotmod.role.dima.welcome").formatted(Formatting.AQUA)
                : Text.translatable("story.plotmod.role.ulyana.welcome").formatted(Formatting.LIGHT_PURPLE);
        player.sendMessage(welcome, false);
        showChapter(player);
    }

    public static Role getRole(ServerPlayerEntity player) {
        String r = player.getPersistentData().getString("plotmod_role");
        if (r == null || r.isEmpty()) return null;
        return Role.valueOf(r);
    }

    public static void showChapter(ServerPlayerEntity player) {
        int idx = Math.min(Math.max(player.getPersistentData().getInt("plotmod_chapter"), 1), CHAPTERS.length);
        Chapter c = CHAPTERS[idx - 1];
        player.sendMessage(Text.translatable("story.plotmod.chapter_header", c.id)
                .formatted(Formatting.GOLD), false);
        player.sendMessage(Text.translatable(c.titleKey).formatted(Formatting.YELLOW, Formatting.BOLD), false);
        player.sendMessage(Text.translatable(c.descKey).formatted(Formatting.GRAY), false);
        player.sendMessage(Text.translatable("story.plotmod.goal", c.requiredBlocksMined)
                .formatted(Formatting.GRAY), false);
    }

    /** Вызывается при поломке блока — продвижение по сюжету */
    public static void onBlockMined(ServerPlayerEntity player) {
        if (getRole(player) == null) return;
        NbtCompound d = player.getPersistentData();
        int chapter = d.getInt("plotmod_chapter");
        if (chapter < 1 || chapter > CHAPTERS.length) return;
        Chapter c = CHAPTERS[chapter - 1];
        int mined = d.getInt("plotmod_mined") + 1;
        d.putInt("plotmod_mined", mined);
        if (mined >= c.requiredBlocksMined && c.unlockPickaxeTier > 0) {
            advance(player, c);
        }
    }

    private static void advance(ServerPlayerEntity player, Chapter done) {
        NbtCompound d = player.getPersistentData();
        d.putInt("plotmod_chapter", done.id + 1);
        d.putInt("plotmod_mined", 0);
        d.putInt("pickaxe_tier", done.unlockPickaxeTier);
        player.sendMessage(Text.translatable("story.plotmod.chapter_complete", done.id)
                .formatted(Formatting.GREEN, Formatting.BOLD), false);
        var pick = ru.plotmod.item.StoryPickaxes.ALL[done.unlockPickaxeTier];
        if (pick != null) {
            player.giveItemStack(new net.minecraft.item.ItemStack(pick));
            player.sendMessage(Text.translatable("story.plotmod.got_pickaxe",
                    Text.translatable(pick.getTranslationKey())).formatted(Formatting.GOLD), false);
        }
        showChapter(player);
        player.getWorld().playSound(null, player.getBlockPos(),
                net.minecraft.sound.SoundEvents.UI_TOAST_CHALLENGE_COMPLETE,
                net.minecraft.sound.SoundCategory.MASTER, 1f, 1f);
    }
}
