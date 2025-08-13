package org.fastcreepermod.fastcreepermod;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Fertilizable;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;

public class CropGrowthHandler {

    // --- Configuration ---
    private static final int GROWTH_RADIUS = 4;
    // Reduced chance to make growth a bit slower overall
    private static final int GROWTH_CHANCE = 15;
    // The chance for the "special" event to happen
    private static final float FART_CHANCE = 0.45f;

    public static void registerEvents() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                if (player.isSneaking()) {
                    accelerateCropGrowth(player);
                }
            }
        });
    }

    private static void accelerateCropGrowth(ServerPlayerEntity player) {
        ServerWorld world = player.getWorld();

        // Check if the special event should happen
        if (world.getRandom().nextFloat() < FART_CHANCE) {
            // FIX: Use the playSound method overload that takes coordinates instead of a BlockPos.
            // This avoids the type conversion error.
            world.playSound(
                    null, // To all players
                    player.getX(), // Player's X coordinate
                    player.getY(), // Player's Y coordinate
                    player.getZ(), // Player's Z coordinate
                    SoundEvents.ENTITY_GENERIC_EXPLODE,
                    SoundCategory.PLAYERS,
                    0.5f, // Volume
                    0.5f  // Pitch
            );

            // Spawn some brown "dirt" particles around the player
            world.spawnParticles(
                    new BlockStateParticleEffect(ParticleTypes.FALLING_DUST, Blocks.DIRT.getDefaultState()),
                    player.getX(),
                    player.getY() + 0.8, // Centered on the player's body
                    player.getZ(),
                    30,    // Particle count
                    0.3,   // X spread
                    0.5,   // Y spread
                    0.3,   // Z spread
                    0.0    // Speed
            );
            // On this tick, no crop growth occurs.
        } else {
            // If the special event doesn't happen, run the normal growth logic
            BlockPos playerPos = player.getBlockPos();
            for (int x = -GROWTH_RADIUS; x <= GROWTH_RADIUS; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -GROWTH_RADIUS; z <= GROWTH_RADIUS; z++) {
                        if (world.getRandom().nextInt(100) < GROWTH_CHANCE) {
                            BlockPos cropPos = playerPos.add(x, y, z);
                            BlockState cropState = world.getBlockState(cropPos);

                            if (cropState.getBlock() instanceof Fertilizable fertilizable) {
                                if (fertilizable.isFertilizable(world, cropPos, cropState)) {
                                    fertilizable.grow(world, world.getRandom(), cropPos, cropState);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
