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

import java.util.HashMap;
import java.util.UUID;

public class CropGrowthHandler {

    // --- Configuration ---
    private static final int GROWTH_RADIUS = 4;
    private static final int GROWTH_CHANCE = 15;
    // The chance for the "special" event to happen, now 30%
    private static final float FART_CHANCE = 0.30f;
    // Timer duration: 20 ticks/second * 60 seconds = 1200 ticks for 1 minute
    private static final int TIMER_DURATION = 1200;

    // A map to store a timer for each player
    private static final HashMap<UUID, Integer> playerTimers = new HashMap<>();

    public static void registerEvents() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                // Get the player's unique ID
                UUID playerId = player.getUuid();
                // Get the current timer value for this player, or 0 if they're not in the map yet
                int timer = playerTimers.getOrDefault(playerId, 0);

                // Only check crouching players
                if (player.isSneaking()) {
                    // Increment the timer
                    timer++;

                    // Check if the timer has reached 1 minute
                    if (timer >= TIMER_DURATION) {
                        // Reset the timer for the next minute
                        timer = 0;
                        // Now, check for the 30% chance event
                        handleTimedEvent(player);
                    } else {
                        // If the timer hasn't finished, run the normal growth logic
                        runGrowthLogic(player);
                    }
                } else {
                    // If the player is not sneaking, reset their timer
                    timer = 0;
                }
                // Update the timer value in the map
                playerTimers.put(playerId, timer);
            }
        });
    }

    /**
     * This method is called once per minute for a crouching player.
     * It has a chance to trigger the special event.
     */
    private static void handleTimedEvent(ServerPlayerEntity player) {
        ServerWorld world = player.getWorld();
        // Check for the 30% chance
        if (world.getRandom().nextFloat() < FART_CHANCE) {
            // Play a low-pitched "fart" sound at the player's location
            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 0.5f, 0.5f);

            // Spawn brown particles at the player's legs/bottom
            world.spawnParticles(
                    new BlockStateParticleEffect(ParticleTypes.FALLING_DUST, Blocks.DIRT.getDefaultState()),
                    player.getX(),
                    player.getY() + 0.2, // Lower Y value to be near the legs
                    player.getZ(),
                    30, 0.2, 0.2, 0.2, 0.0);
        } else {
            // If the 30% chance fails, we still do a burst of growth for that minute of crouching
            runGrowthLogic(player);
        }
    }

    /**
     * The logic for accelerating crop growth.
     */
    private static void runGrowthLogic(ServerPlayerEntity player) {
        ServerWorld world = player.getWorld();
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
