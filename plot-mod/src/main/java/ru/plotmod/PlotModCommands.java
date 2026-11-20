package ru.plotmod;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import com.mojang.brigadier.arguments.StringArgumentType;
import ru.plotmod.events.EventManager;
import ru.plotmod.story.StoryManager;

public class PlotModCommands {

    public static void register() {
        // /plot role <dima|ulyana> — выбор роли
        // /plot chapter — показать текущую главу
        // /plot compass — выдать компас удачи
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
            dispatcher.register(CommandManager.literal("plot")
                .then(CommandManager.literal("role")
                    .then(CommandManager.argument("name", StringArgumentType.word())
                        .executes(ctx -> {
                            var src = ctx.getSource();
                            var player = src.getPlayer();
                            if (player == null) return 0;
                            String name = StringArgumentType.getString(ctx, "name").toLowerCase();
                            StoryManager.Role role = switch (name) {
                                case "dima", "дима" -> StoryManager.Role.DIMA;
                                case "ulyana", "ульяна" -> StoryManager.Role.ULYANA;
                                default -> null;
                            };
                            if (role == null) {
                                src.sendError(Text.translatable("cmd.plotmod.bad_role"));
                                return 0;
                            }
                            StoryManager.setRole(player, role);
                            // Стартовый набор: земляная кирка + компас удачи
                            player.giveItemStack(new net.minecraft.item.ItemStack(ModItems.EARTH_PICKAXE));
                            giveCompass(player);
                            return 1;
                        })))
                .then(CommandManager.literal("chapter")
                    .executes(ctx -> {
                        var p = ctx.getSource().getPlayer();
                        if (p != null) StoryManager.showChapter(p);
                        return 1;
                    }))
                .then(CommandManager.literal("compass")
                    .requires(s -> s.hasPermissionLevel(2))
                    .executes(ctx -> {
                        var p = ctx.getSource().getPlayer();
                        if (p != null) giveCompass(p);
                        return 1;
                    }))));

        ServerTickEvents.END_SERVER_TICK.register(EventManager::onServerTick);

        // Прогресс сюжетных глав при поломке блоков
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (player instanceof net.minecraft.server.network.ServerPlayerEntity sp) {
                StoryManager.onBlockMined(sp);
            }
        });
    }

    private static void giveCompass(net.minecraft.server.network.ServerPlayerEntity player) {
        var stack = new net.minecraft.item.ItemStack(ModItems.LUCKY_COMPASS);
        stack.setCustomName(Text.translatable("item.plotmod.lucky_compass.name").formatted(Formatting.LIGHT_PURPLE));
        player.giveItemStack(stack);
        player.sendMessage(Text.translatable("story.plotmod.compass_hint").formatted(Formatting.YELLOW), false);
    }
}
