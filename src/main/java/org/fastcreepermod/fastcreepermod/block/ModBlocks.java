package org.fastcreepermod.fastcreepermod.block;

import static org.fastcreepermod.fastcreepermod.FastCreeperMod.MOD_ID;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import org.fastcreepermod.fastcreepermod.FastCreeperMod;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class ModBlocks {

    public static final Block BISMITH_ORE = registerBlock("bismith_ore", Block::new,
            AbstractBlock.Settings.create().strength(1.5f).requiresTool().sounds(BlockSoundGroup.STONE), true);
    public static final Block BISMITH_BLOCK = registerBlock("bismith_block", Block::new,
            AbstractBlock.Settings.create().strength(1.5f).requiresTool().sounds(BlockSoundGroup.STONE), true);

    private static Block registerBlock(String name, Function<AbstractBlock.Settings, Block> blockFactory, AbstractBlock.Settings settings, Boolean shouldRegisterItem) {
        RegistryKey<Block> blockKey = keyOfBlock(name);
        Block block = blockFactory.apply(settings.registryKey(blockKey));
        if (shouldRegisterItem) {
            RegistryKey<Item> itemKey = keyOfItem(name);
            BlockItem blockItem = new BlockItem(block, new Item.Settings().registryKey(itemKey));
            Registry.register(Registries.ITEM, itemKey, blockItem);
        }

        return Registry.register(Registries.BLOCK, blockKey, block);
    }

    private static RegistryKey<Block> keyOfBlock(String name) {
        return RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(FastCreeperMod.MOD_ID, name));
    }

    private static RegistryKey<Item> keyOfItem(String name) {
        return RegistryKey.of(RegistryKeys.ITEM, Identifier.of(FastCreeperMod.MOD_ID, name));
    }

    public static void registerModBlocks() {
        FastCreeperMod.LOGGER.info("Registering blocks for" + FastCreeperMod.MOD_ID);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL).register(entries->{
            entries.add(BISMITH_ORE);
            entries.add(BISMITH_BLOCK);
        });
    }
}