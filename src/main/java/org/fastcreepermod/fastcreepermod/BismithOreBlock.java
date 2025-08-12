package org.fastcreepermod.fastcreepermod;

import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.AbstractBlock;
import net.minecraft.sound.BlockSoundGroup;

public class BismithOreBlock extends Block {
    public BismithOreBlock() {
        super(AbstractBlock.Settings.of(Material.STONE)
            .strength(3.0f, 9.0f)
            .sounds(BlockSoundGroup.STONE)
            .requiresTool()
        );
    }
}
