package org.fastcreepermod.fastcreepermod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.PackOutput; // <-- IMPORT THE NEW PackOutput CLASS
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;


public class FastCreeperMod implements ModInitializer, DataGeneratorEntrypoint {

    @Override
    public void onInitialize() {
        ModRegistry.register();
        BismithOreWorldGen.generateOres();
    }

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(WorldgenProvider::new);
    }

    private static class WorldgenProvider extends FabricDynamicRegistryProvider {
        // Change "FabricDataGenerator.Pack.PackOutput" to just "PackOutput"
        public WorldgenProvider(PackOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries) {
            entries.addAll(registries.getWrapperOrThrow(RegistryKeys.CONFIGURED_FEATURE));
            entries.addAll(registries.getWrapperOrThrow(RegistryKeys.PLACED_FEATURE));
        }

        @Override
        public String getName() {
            return "World Gen";
        }
    }
}