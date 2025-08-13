// src/main/java/org/fastcreepermod/fastcreepermod/BismithOreWorldGen.java
package org.fastcreepermod.fastcreepermod;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import org.fastcreepermod.fastcreepermod.features.ModPlacedFeatures; // Import your new class
import net.minecraft.world.gen.GenerationStep;

public class BismithOreWorldGen {

    public static void generateOres() {
        // Use the key directly to add the feature to biomes
        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_ORES,
                ModPlacedFeatures.BISMITH_ORE_PLACED_KEY
        );
    }
}