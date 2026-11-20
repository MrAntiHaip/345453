package ru.plotmod;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import ru.plotmod.item.LuckyCompassItem;

/**
 * Глобальные правила мода:
 *  - Компас удачи НЕЛЬЗЯ выбросить / потерять (отмена drop + возврат при респауне).
 */
public class PlotModEvents {

    public static void register() {
        // Запрет выбрасывания компаса
        net.fabricmc.fabric.api.event.player.PlayerDropItemEvents.ALLOW.drop(
            (player, stack) -> !(stack.getItem() instanceof LuckyCompassItem)
        );

        // Если всё же как-то упал — удаляем сущность предмета и возвращаем игроку
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (world.isClient) return;
            for (var entity : world.getEntitiesByClass(net.minecraft.entity.ItemEntity.class,
                    new net.minecraft.util.math.Box(net.minecraft.util.math.Vec3d.ZERO, 1, 1, 1).expand(Double.POSITIVE_INFINITY),
                    e -> e.getStack().getItem() instanceof LuckyCompassItem)) {
                entity.discard();
                var closest = world.getClosestPlayer(entity);
                if (closest instanceof ServerPlayerEntity sp) {
                    sp.giveItemStack(new ItemStack(ModItems.LUCKY_COMPASS));
                    sp.sendMessage(Text.translatable("msg.plotmod.compass_bound").formatted(Formatting.RED), true);
                }
            }
        });

        // Возврат компаса после смерти
        ServerPlayerEvents.AFTER_RESPAWN.register((oldP, newP, alive) -> {
            boolean has = false;
            for (ItemStack s : newP.getInventory().main) {
                if (s.getItem() instanceof LuckyCompassItem) { has = true; break; }
            }
            if (!has && oldP.getPersistentData().getBoolean("plotmod_had_compass")) {
                newP.giveItemStack(new ItemStack(ModItems.LUCKY_COMPASS));
                newP.sendMessage(Text.translatable("msg.plotmod.compass_returned").formatted(Formatting.GOLD), false);
            }
        });

        // Отметка "игрок владел компасом" в persistent data
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {});
    }
}
