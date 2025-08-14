package org.fastcreepermod.fastcreepermod;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.*;

public class RandomBlockRewarder {

    private static final Map<UUID, List<Item>> playerRewardCache = new HashMap<>();
    private static final Map<UUID, Set<String>> playerAdvancementCache = new HashMap<>();

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

    public static void registerCacheEvents() {
        // When a player joins, initialize their reward and advancement caches
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            updateUserRewardPool(player);
            playerAdvancementCache.put(player.getUuid(), getCompletedAdvancements(player));
        });

        // Remove from cache on leave
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            playerRewardCache.remove(handler.getPlayer().getUuid());
            playerAdvancementCache.remove(handler.getPlayer().getUuid());
        });

        // Check advancements every tick
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                Set<String> current = getCompletedAdvancements(player);
                Set<String> old = playerAdvancementCache.get(player.getUuid());

                if (old == null || !current.equals(old)) {
                    playerAdvancementCache.put(player.getUuid(), current);
                    updateUserRewardPool(player); // Rebuild when advancement list changes
                }
            }
        });
    }

    public static void updateUserRewardPool(ServerPlayerEntity player) {
        List<Item> blockPool = new ArrayList<>(startingBlocks);

        if (hasAdvancement(player, "minecraft:story/mine_stone")) blockPool.addAll(stoneAgeBlocks);
        if (hasAdvancement(player, "minecraft:story/smelt_iron")) blockPool.addAll(ironBlocks);
        if (hasAdvancement(player, "minecraft:adventure/kill_a_mob")) blockPool.addAll(monsterHunterBlocks);
        if (hasAdvancement(player, "minecraft:story/enter_the_nether")) blockPool.addAll(netherBlocks);
        if (hasAdvancement(player, "minecraft:story/enter_the_end")) blockPool.addAll(endBlocks);

        if (hasAdvancement(player, "minecraft:adventure/kill_the_wither") || hasAdvancement(player, "minecraft:end/kill_dragon")) {
            blockPool.addAll(milestoneBlocks);
        }

        if (hasAdvancement(player, "minecraft:husbandry/plant_seed")) blockPool.addAll(farmBlocks);
        if (hasAdvancement(player, "minecraft:adventure/adventuring_time")) blockPool.addAll(oceanBlocks);

        playerRewardCache.put(player.getUuid(), blockPool);
    }

    public static void giveAppropriateRandomBlock(ServerPlayerEntity player) {
        List<Item> blockPool = playerRewardCache.get(player.getUuid());

        if (blockPool == null) {
            updateUserRewardPool(player);
            blockPool = playerRewardCache.get(player.getUuid());
        }

        if (blockPool != null && !blockPool.isEmpty()) {
            Item randomBlock = blockPool.get(player.getRandom().nextInt(blockPool.size()));
            player.giveItemStack(new ItemStack(randomBlock));
            player.sendMessage(Text.literal(
                    "You received a random block based on ALL your achievements!"
            ), false);
        }
    }

    private static boolean hasAdvancement(ServerPlayerEntity player, String id) {
        AdvancementEntry advancement = player.getServer().getAdvancementLoader().get(Identifier.of(id));
        return advancement != null && player.getAdvancementTracker().getProgress(advancement).isDone();
    }

    private static Set<String> getCompletedAdvancements(ServerPlayerEntity player) {
        Set<String> completed = new HashSet<>();
        for (AdvancementEntry adv : player.getServer().getAdvancementLoader().getAdvancements()) {
            if (player.getAdvancementTracker().getProgress(adv).isDone()) {
                completed.add(adv.id().toString());
            }
        }
        return completed;
    }
}
