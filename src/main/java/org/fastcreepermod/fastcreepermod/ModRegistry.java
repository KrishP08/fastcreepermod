package org.fastcreepermod.fastcreepermod;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

public class ModRegistry {
    // Blocks
    public static final Block BISMITH_ORE = new BismithOreBlock();
    public static final Block BISMITH_BLOCK = new BismithBlock();

    // Items
    public static final Item RAW_BISMITH = new RawBismith();
    public static final Item BISMITH_INGOT = new BismithIngot();

    // Toggler items for gamerules
    public static final Item FAST_CREEPER_TOGGLER = new FastCreeperToggler();
    public static final Item CHARGED_CREEPER_TOGGLER = new ChargedCreeperToggler();
    public static final Item END_CRYSTAL_TOGGLER = new EndCrystalToggler();

    public static void register() {
        // Register blocks and block items
        Registry.register(Registry.BLOCK, new Identifier("fastcreepermod", "bismith_ore"), BISMITH_ORE);
        Registry.register(Registry.ITEM, new Identifier("fastcreepermod", "bismith_ore"), new BlockItem(BISMITH_ORE, new Item.Settings()));
        
        Registry.register(Registry.BLOCK, new Identifier("fastcreepermod", "bismith_block"), BISMITH_BLOCK);
        Registry.register(Registry.ITEM, new Identifier("fastcreepermod", "bismith_block"), new BlockItem(BISMITH_BLOCK, new Item.Settings()));

        // Register standalone items
        Registry.register(Registry.ITEM, new Identifier("fastcreepermod", "raw_bismith"), RAW_BISMITH);
        Registry.register(Registry.ITEM, new Identifier("fastcreepermod", "bismith_ingot"), BISMITH_INGOT);

        // Register toggler items
        Registry.register(Registry.ITEM, new Identifier("fastcreepermod", "fast_creeper_toggler"), FAST_CREEPER_TOGGLER);
        Registry.register(Registry.ITEM, new Identifier("fastcreepermod", "charged_creeper_toggler"), CHARGED_CREEPER_TOGGLER);
        Registry.register(Registry.ITEM, new Identifier("fastcreepermod", "end_crystal_toggler"), END_CRYSTAL_TOGGLER);
    }
}
