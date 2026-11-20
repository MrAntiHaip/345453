package ru.plotmod.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CompassItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import ru.plotmod.PlotMod;
import ru.plotmod.roulette.RouletteManager;

/**
 * Компас Удачи: правый клик запускает рулетку.
 * Рулетка крутится НЕДОЛГО (~2-3 секунды) и выдаёт ресурсный приз.
 * Нельзя выбросить из инвентаря (см. PlotModEvents).
 */
public class LuckyCompassItem extends CompassItem {

    public LuckyCompassItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        NbtCompound nbt = stack.getOrCreateNbt();

        long lastSpin = nbt.getLong("last_spin");
        long cooldown = 60L * 20; // 3 секунды
        if (world.getTime - lastSpin < cooldown && !world.isClient) {
            player.sendMessage(Text.translatable("msg.plotmod.compass_cooldown"), true);
            return TypedActionResult.fail(stack);
        }
        nbt.putLong("last_spin", world.getTime());

        if (!world.isClient && player instanceof ServerPlayerEntity sp) {
            RouletteManager.spin(sp, stack);
            world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_NOTE_BLOCK_PLING,
                    SoundCategory.PLAYERS, 1f, 1.5f);
        }
        return TypedActionResult.success(stack, true);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        return ActionResult.PASS; // компас не ставим
    }
}
