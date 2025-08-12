package org.fastcreepermod.fastcreepermod;

import net.minecraft.block.Block;
// import net.minecraft.block.Material; <-- REMOVE THIS LINE
import net.minecraft.block.AbstractBlock;
import net.minecraft.sound.BlockSoundGroup;

public class BismithOreBlock extends Block {
    public BismithOreBlock() {
        // Change "of(Material.STONE)" to "create()"
        super(AbstractBlock.Settings.create()
                .strength(3.0f, 9.0f)
                .sounds(BlockSoundGroup.STONE)
                .requiresTool()
        );
    }
}