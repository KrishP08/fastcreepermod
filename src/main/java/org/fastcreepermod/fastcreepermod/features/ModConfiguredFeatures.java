// src/main/java/org/fastcreepermod/fastcreepermod/features/ModConfiguredFeatures.java
package org.fastcreepermod.fastcreepermod.features;

import org.fastcreepermod.fastcreepermod.ModRegistry;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.TagMatchRuleTest;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;

import java.util.List;

public class ModConfiguredFeatures {

    // Create a RegistryKey for your configured feature
    public static final RegistryKey<ConfiguredFeature<?, ?>> BISMITH_ORE_KEY = RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, new Identifier("fastcreepermod", "bismith_ore"));

    public static void bootstrap(Registerable<ConfiguredFeature<?, ?>> context) {
        // Defines which blocks your ore can replace
        RuleTest stoneOreReplaceables = new TagMatchRuleTest(BlockTags.STONE_ORE_REPLACEABLES);
        RuleTest deepslateOreReplaceables = new TagMatchRuleTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);

        List<OreFeatureConfig.Target> overworldBismithOres = List.of(
                OreFeatureConfig.createTarget(stoneOreReplaceables, ModRegistry.BISMITH_ORE.getDefaultState()),
                OreFeatureConfig.createTarget(deepslateOreReplaceables, ModRegistry.BISMITH_ORE.getDefaultState())
        );

        // Register the configured feature
        context.register(BISMITH_ORE_KEY, new ConfiguredFeature<>(Feature.ORE, new OreFeatureConfig(overworldBismithOres, 9))); // 9 is vein size
    }
}