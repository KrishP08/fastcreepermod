package org.fastcreepermod.fastcreepermod;

import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.AbstractBlock;
import net.minecraft.sound.BlockSoundGroup;

public class BismithBlock extends Block {
    public BismithBlock() {
        super(AbstractBlock.Settings.of(Material.METAL)
            .strength(5.0f, 30.0f)
            .sounds(BlockSoundGroup.METAL)
            .requiresTool()
        );
    }
}
