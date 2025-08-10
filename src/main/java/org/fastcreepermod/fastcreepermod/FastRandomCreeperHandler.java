package org.fastcreepermod.fastcreepermod;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.Random;

public class FastRandomCreeperHandler {
    private static final Random random = new Random();
    private static int tickCounter = 0;
    private static int waveMultiplier = 1;
    private static final double FAST_SPEED = 0.95D;
    private static final double EXTRA_HEALTH = 60.0D; // default was 20HP

    public static void registerEvents() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof CreeperEntity creeper && world instanceof ServerWorld serverWorld) {
                if (!serverWorld.getGameRules().get(ModGameRules.FAST_RANDOM_CREEPER).get()) return;

                creeper.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(FAST_SPEED);
                creeper.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(EXTRA_HEALTH);
                creeper.setHealth((float) EXTRA_HEALTH);

                PlayerEntity nearest = serverWorld.getClosestPlayer(creeper, 20);
                if (nearest != null && nearest.isCreative()) {
                    creeper.setFuseSpeed(0);
                    creeper.extinguish();
                } else if (nearest != null) {
                    double dist = creeper.distanceTo(nearest);
                    int fuseSpeed = dist < 5 ? Math.max(1, 5 - (int) dist) : 0;
                    creeper.setFuseSpeed(fuseSpeed);
                    creeper.setTarget(nearest);
                }
            }
        });

        ServerTickEvents.END_WORLD_TICK.register(FastRandomCreeperHandler::spawnCreeperWave);

        ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (!(world instanceof ServerWorld serverWorld)) return;
            if (!serverWorld.getGameRules().get(ModGameRules.FAST_RANDOM_CREEPER).get()) return;

            for (CreeperEntity creeper : serverWorld.getEntitiesByClass(
                    CreeperEntity.class,
                    new Box(-30000, -300, -30000, 30000, 300, 30000),
                    c -> true)) {
                PlayerEntity nearest = serverWorld.getClosestPlayer(creeper, 12);
                if (nearest != null && !nearest.isCreative()) {
                    double dist = creeper.distanceTo(nearest);
                    if (dist < 6 && creeper.getFuseSpeed() <= 0) {
                        int fuseSpeed = Math.max(1, 4 - (int) (dist / 2));
                        creeper.setFuseSpeed(fuseSpeed);
                    }
                    creeper.setTarget(nearest);
                } else {
                    creeper.setFuseSpeed(0);
                    creeper.extinguish();
                }
            }
        });
    }

    private static void spawnCreeperWave(ServerWorld world) {
        if (!world.getGameRules().get(ModGameRules.FAST_RANDOM_CREEPER).get()) return;

        tickCounter++;
        if (tickCounter % 6000 == 0 && waveMultiplier < 10) waveMultiplier++;

        if (tickCounter >= 60) { // ~30s
            tickCounter = 0;
            int creepersPerPlayer = 3 * 4 * waveMultiplier;
            world.getPlayers().forEach(player -> {
                if (player.isCreative()) return;
                for (int i = 0; i < creepersPerPlayer; i++) {
                    BlockPos pos = player.getBlockPos().add(random.nextInt(36) - 18, 0, random.nextInt(36) - 18);
                    if (world.getLightLevel(pos) < 8 && world.isAir(pos)) {
                        CreeperEntity creeper = new CreeperEntity(EntityType.CREEPER, world);
                        creeper.refreshPositionAndAngles(pos, 0, 0);
                        creeper.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(FAST_SPEED);
                        creeper.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(EXTRA_HEALTH);
                        creeper.setHealth((float) EXTRA_HEALTH);
                        creeper.setFuseSpeed(2);
                        creeper.setTarget(player);
                        world.spawnEntity(creeper);
                    }
                }
            });
        }
    }

    public static void applyExplosionKnockback(ServerWorld world, BlockPos explosionPos, double radius, PlayerEntity player) {
        List<CreeperEntity> creepers = world.getEntitiesByClass(
                CreeperEntity.class, new Box(explosionPos).expand(radius), c -> true);

        for (CreeperEntity creeper : creepers) {
            Vec3d dir = creeper.getPos().subtract(Vec3d.ofCenter(explosionPos)).normalize();
            double up = 0.6;
            if (player != null && player.getY() > creeper.getY()) {
                up += (player.getY() - creeper.getY()) * 0.22;
            }
            Vec3d knockback = dir.multiply(2).add(0, up, 0);
            creeper.addVelocity(knockback.x, knockback.y, knockback.z);
            creeper.velocityModified = true;
        }
    }
}
