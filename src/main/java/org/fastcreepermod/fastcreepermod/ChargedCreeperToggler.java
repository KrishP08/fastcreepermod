package org.fastcreepermod.fastcreepermod;

import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class ChargedCreeperToggler extends Item {
    public ChargedCreeperToggler() {
        super(new Item.Settings().maxCount(1));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity player = context.getPlayer();
        if (player != null && !player.getWorld().isClient && player.getWorld() instanceof ServerWorld sw) {
            boolean val = !sw.getGameRules().get(ModGameRules.FAST_RANDOM_CREEPER_CHARGED).get();
            sw.getGameRules().get(ModGameRules.FAST_RANDOM_CREEPER_CHARGED).set(val, sw);
            player.sendMessage(Text.literal("fastRandomCreeperCharged: " + (val ? "enabled" : "disabled")), true);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
}
