package org.fastcreepermod.fastcreepermod;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.ArrayList;

public class RandomBlockRewarder {
    // You can add more vanilla blocks to these lists!
    public static final List<Item> startingBlocks = List.of(
            Items.GRASS_BLOCK, Items.DIRT, Items.OAK_LOG, Items.STONE, Items.COBBLESTONE, Items.SAND
    );
    public static final List<Item> stoneAgeBlocks = List.of(
            Items.COAL_ORE, Items.IRON_ORE, Items.GRANITE, Items.ANDESITE, Items.DIORITE, Items.TUFF, Items.SANDSTONE
    );
    public static final List<Item> ironBlocks = List.of(
            Items.GOLD_ORE, Items.COPPER_ORE, Items.RAW_IRON_BLOCK, Items.CALCITE, Items.DEEPSLATE
    );
    public static final List<Item> monsterHunterBlocks = List.of(
            Items.ZOMBIE_HEAD, Items.CREEPER_HEAD, Items.BONE_BLOCK, Items.REDSTONE_BLOCK
    );
    public static final List<Item> netherBlocks = List.of(
            Items.NETHER_BRICKS, Items.QUARTZ_BLOCK, Items.SOUL_SAND, Items.MAGMA_BLOCK, Items.SHROOMLIGHT
    );
    public static final List<Item> endBlocks = List.of(
            Items.END_STONE, Items.PURPUR_BLOCK, Items.CHORUS_FLOWER, Items.DRAGON_HEAD
    );
    public static final List<Item> milestoneBlocks = List.of(
            Items.DIAMOND_BLOCK, Items.EMERALD_BLOCK, Items.NETHERITE_BLOCK,
            Items.BEACON, Items.ENCHANTING_TABLE
    );
    public static final List<Item> farmBlocks = List.of(
            Items.WHEAT, Items.POTATO, Items.PUMPKIN, Items.MELON, Items.HAY_BLOCK, Items.HONEY_BLOCK
    );
    public static final List<Item> oceanBlocks = List.of(
            Items.PRISMARINE, Items.SPONGE, Items.SEA_LANTERN, Items.KELP
    );
    // Add more pools here!

    public static boolean hasAdvancement(ServerPlayerEntity player, String id) {
        return player.getAdvancementTracker().getProgress(
                player.getServer().getAdvancementLoader().get(Identifier.of(id))
        ).isDone();
    }

    public static void giveAppropriateRandomBlock(ServerPlayerEntity player) {
        List<Item> blockPool = new ArrayList<>(startingBlocks);

        if (hasAdvancement(player, "minecraft:story/mine_stone")) blockPool.addAll(stoneAgeBlocks);
        if (hasAdvancement(player, "minecraft:story/smelt_iron")) blockPool.addAll(ironBlocks);
        if (hasAdvancement(player, "minecraft:adventure/kill_a_mob")) blockPool.addAll(monsterHunterBlocks);
        if (hasAdvancement(player, "minecraft:story/enter_the_nether")) blockPool.addAll(netherBlocks);
        if (hasAdvancement(player, "minecraft:story/enter_the_end")) blockPool.addAll(endBlocks);
        if (hasAdvancement(player, "minecraft:adventure/kill_the_wither") ||
                hasAdvancement(player, "minecraft:end/kill_dragon")) blockPool.addAll(milestoneBlocks);

        if (hasAdvancement(player, "minecraft:husbandry/plant_seed")) blockPool.addAll(farmBlocks);
        if (hasAdvancement(player, "minecraft:adventure/adventuring_time")) blockPool.addAll(oceanBlocks);

        if (!blockPool.isEmpty()) {
            Item randomBlock = blockPool.get(player.getRandom().nextInt(blockPool.size()));
            player.giveItemStack(new ItemStack(randomBlock));
            player.sendMessage(Text.literal("You received a random block based on ALL your achievements!"), false);
        }
    }
}
