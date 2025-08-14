package org.fastcreepermod.fastcreepermod;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.fastcreepermod.fastcreepermod.mixin.CreeperEntityAccessor;

import java.util.Random;

public class FastRandomCreeperHandler {
    private static final Random random = new Random();
    private static int tickCounter = 0;
    private static int waveMultiplier = 1;
    private static final double FAST_SPEED = 0.45D;
    private static final double EXTRA_HEALTH = 60.0D;

    public static void registerEvents() {
        // Buff normal creepers on spawn
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof CreeperEntity creeper && world instanceof ServerWorld serverWorld) {
                if (!serverWorld.getGameRules().get(ModGameRules.FAST_RANDOM_CREEPER).get()) return;
                baseCreeperBuff(serverWorld, creeper);
            }
        });

        // Wave spawn
        ServerTickEvents.END_WORLD_TICK.register(FastRandomCreeperHandler::spawnCreeperWave);

        // Proximity fuse trigger
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (!(world instanceof ServerWorld sw)) return;
            if (!sw.getGameRules().get(ModGameRules.FAST_RANDOM_CREEPER).get()) return;

            for (PlayerEntity player : sw.getPlayers()) {
                if (player.isCreative() || player.isSpectator()) continue;

                Box searchBox = new Box(player.getBlockPos()).expand(300);
                for (CreeperEntity creeper : sw.getEntitiesByType(EntityType.CREEPER, searchBox, c -> true)) {
                    if (creeper.distanceTo(player) < 6) {
                        ((CreeperEntityAccessor) creeper).invokeIgnite(); // Ignite the creeper
                        creeper.setTarget(player);
                    }
                }
            }
        });
    }

    private static void spawnCreeperWave(ServerWorld world) {
        if (!world.getGameRules().get(ModGameRules.FAST_RANDOM_CREEPER).get()) return;

        tickCounter++;
        if (tickCounter % 6000 == 0 && waveMultiplier < 10) waveMultiplier++;

        if (tickCounter >= 100) { // every 5 seconds
            tickCounter = 0;
            int creepersPerPlayer = 2 * waveMultiplier;
            world.getPlayers().forEach(player -> {
                if (player.isCreative()) return;
                for (int i = 0; i < creepersPerPlayer; i++) {
                    BlockPos pos = player.getBlockPos().add(random.nextInt(18) - 9, 0, random.nextInt(18) - 9);
                    if (world.isAir(pos) && world.isAir(pos.up())) {
                        CreeperEntity creeper = new CreeperEntity(EntityType.CREEPER, world);
                        creeper.refreshPositionAndAngles(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
                        baseCreeperBuff(world, creeper);
                        creeper.setTarget(player);
                        world.spawnEntity(creeper);
                    }
                }
            });
        }
    }

    private static void baseCreeperBuff(ServerWorld world, CreeperEntity creeper) {
        // Set charged status FIRST
        if (world.getGameRules().get(ModGameRules.FAST_RANDOM_CREEPER_CHARGED).get()) {
            creeper.getDataTracker().set(CreeperEntityAccessor.getChargedData(), true);
        } else if (random.nextFloat() < 0.3f) {
            creeper.getDataTracker().set(CreeperEntityAccessor.getChargedData(), true);
        }

        // Now apply custom attributes AFTER the state change
        creeper.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(FAST_SPEED);
        creeper.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(EXTRA_HEALTH);
        creeper.setHealth((float) EXTRA_HEALTH);

        // --- MODIFIED FUSE TIME ---
        // Fuse time is in ticks (20 ticks = 1 second).
        // A value from 0-40 gives a random fuse time between 0 and 2 seconds.
        int fuseTicks = random.nextInt(41); // nextInt(41) generates a random number from 0 to 40.
        ((CreeperEntityAccessor) creeper).setFuseTime(fuseTicks);

        // Apply visual effects
        if (world.getGameRules().get(ModGameRules.FAST_RANDOM_CREEPER_ENDCRYSTAL).get()) {
            creeper.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 200, 1, false, false));
        } else if (creeper.isCharged()) {
            creeper.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 200, 0, false, false));
        }
    }

    public static void doExplosionEffects(ServerWorld world, CreeperEntity creeper, float radius) {
        BlockPos pos = creeper.getBlockPos();
        if (world.getGameRules().get(ModGameRules.FAST_RANDOM_CREEPER_ENDCRYSTAL).get()) {
            world.spawnParticles(ParticleTypes.END_ROD, creeper.getX(), creeper.getY() + 1, creeper.getZ(), 60, 2, 2, 2, 0.05);
            world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EXPLODE.value(), SoundCategory.HOSTILE, 2.0F, 1.0F);
        } else if (world.getGameRules().get(ModGameRules.FAST_RANDOM_CREEPER_CHARGED).get() || creeper.isCharged()) {
            world.spawnParticles(ParticleTypes.ELECTRIC_SPARK, creeper.getX(), creeper.getY() + 0.5, creeper.getZ(), 50, 1.5, 1.5, 1.5, 0.05);
            world.playSound(null, pos, SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.HOSTILE, 2.5F, 1.0F);
        } else {
            world.spawnParticles(ParticleTypes.EXPLOSION_EMITTER, creeper.getX(), creeper.getY(), creeper.getZ(), 20, 1, 1, 1, 0.0);
            world.playSound(null, pos, SoundEvents.ENTITY_CREEPER_PRIMED, SoundCategory.HOSTILE, 1.5F, 1.0F);
        }
    }
}