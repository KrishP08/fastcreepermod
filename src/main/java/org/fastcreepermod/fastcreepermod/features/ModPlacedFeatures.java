// src/main/java/org/fastcreepermod/fastcreepermod/features/ModPlacedFeatures.java
package org.fastcreepermod.fastcreepermod.features;

import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.placementmodifier.CountPlacementModifier;
import net.minecraft.world.gen.placementmodifier.HeightRangePlacementModifier;
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier;

import java.util.List;

public class ModPlacedFeatures {

    // Create a RegistryKey for your placed feature
    public static final RegistryKey<PlacedFeature> BISMITH_ORE_PLACED_KEY = RegistryKey.of(RegistryKeys.PLACED_FEATURE, new Identifier("fastcreepermod", "bismith_ore_placed"));

    public static void bootstrap(Registerable<PlacedFeature> context) {
        var configuredFeatureRegistryEntryLookup = context.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);

        // Register the placed feature
        context.register(BISMITH_ORE_PLACED_KEY, new PlacedFeature(
                configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.BISMITH_ORE_KEY),
                List.of(
                        CountPlacementModifier.of(14), // Veins per chunk
                        SquarePlacementModifier.of(),  // Horizontal spread
                        HeightRangePlacementModifier.uniform(YOffset.fixed(0), YOffset.fixed(48)) // Y-level range
                )
        ));
    }
}