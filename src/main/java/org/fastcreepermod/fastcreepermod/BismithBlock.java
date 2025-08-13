package org.fastcreepermod.fastcreepermod;

import net.minecraft.block.Block;
// import net.minecraft.block.Material; <-- REMOVE THIS LINE
import net.minecraft.block.AbstractBlock;
import net.minecraft.sound.BlockSoundGroup;

public class BismithBlock extends Block {
    public BismithBlock() {
        // Change "of(Material.METAL)" to "create()"
        super(AbstractBlock.Settings.create()
                .strength(5.0f, 30.0f)
                .sounds(BlockSoundGroup.METAL)
                .requiresTool()
        );
    }
}