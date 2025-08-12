package org.fastcreepermod.fastcreepermod;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.util.Identifier;
import net.minecraft.registry.RegistryEntry;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.placementmodifier.CountPlacementModifier;
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier;
import net.minecraft.world.gen.placementmodifier.HeightRangePlacementModifier;

import java.util.List;

public class BismithOreWorldGen {

    public static void init() {
        OreFeatureConfig oreConfig = new OreFeatureConfig(
                OreFeatureConfig.Target.BASE_STONE_OVERWORLD,
                ModRegistry.BISMITH_ORE.getDefaultState(),
                9 // vein size
        );
        ConfiguredFeature<?, ?> configuredFeature = new ConfiguredFeature<>(Feature.ORE, oreConfig);
        PlacedFeature placedFeature = new PlacedFeature(
                RegistryEntry.of(configuredFeature),
                List.of(
                        CountPlacementModifier.of(14),
                        SquarePlacementModifier.of(),
                        HeightRangePlacementModifier.uniform(0,48)
                )
        );

        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier("fastcreepermod", "bismith_ore_configured"), configuredFeature);
        Registry.register(BuiltinRegistries.PLACED_FEATURE, new Identifier("fastcreepermod", "bismith_ore_placed"), placedFeature);

        BiomeModifications.addFeature(
            BiomeSelectors.foundInOverworld(),
            GenerationStep.Feature.UNDERGROUND_ORES,
            RegistryKey.of(Registry.PLACED_FEATURE_KEY, new Identifier("fastcreepermod", "bismith_ore_placed"))
        );
    }
}
