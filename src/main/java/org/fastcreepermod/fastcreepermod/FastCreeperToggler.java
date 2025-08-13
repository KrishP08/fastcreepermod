package org.fastcreepermod.fastcreepermod;

import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class FastCreeperToggler extends Item {
    public FastCreeperToggler() {
        super(new Item.Settings().maxCount(1));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity player = context.getPlayer();
        if (player != null && !player.getWorld().isClient && player.getWorld() instanceof ServerWorld sw) {
            boolean val = !sw.getGameRules().get(ModGameRules.FAST_RANDOM_CREEPER).get();
            sw.getGameRules().get(ModGameRules.FAST_RANDOM_CREEPER).set(val, sw);
            player.sendMessage(Text.literal("fastRandomCreeper: " + (val ? "enabled" : "disabled")), true);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
}
