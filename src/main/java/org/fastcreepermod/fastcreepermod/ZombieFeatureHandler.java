package org.fastcreepermod.fastcreepermod;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public class ZombieFeatureHandler {

    private static final HashMap<UUID, Integer> zombieKills = new HashMap<>();
    private static final Random rand = new Random();
    private static final int ZOMBIE_KILL_TARGET = 20;
    private static final String ADVANCEMENT_ID = "fastcreepermod:zombie_slayer";
    private static final String ADVANCEMENT_CRITERION = "zombie_kill";

    public static void registerEvents() {

        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, entity, killedEntity) -> {
            if (entity instanceof ServerPlayerEntity player && killedEntity instanceof ZombieEntity) {
                UUID playerId = player.getUuid();
                int kills = zombieKills.getOrDefault(playerId, 0) + 1;
                zombieKills.put(playerId, kills);
                player.sendMessage(Text.literal("You have killed " + kills + " zombies."), false);

                if (kills >= ZOMBIE_KILL_TARGET) {
                    grantAdvancement(player, ADVANCEMENT_ID, ADVANCEMENT_CRITERION);
                }
            }
        });

        ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (world.getTime() % 1200 != 0) return; // Check once every 60 seconds
            for (ServerPlayerEntity player : world.getPlayers()) {
                if (zombieKills.getOrDefault(player.getUuid(), 0) == 0 && world.isDay()) {
                    for (int i = 0; i < 2; i++) {
                        ZombieEntity zombie = new ZombieEntity(EntityType.ZOMBIE, world);
                        zombie.refreshPositionAndAngles(
                                player.getBlockPos().add(rand.nextInt(10) - 5, 1, rand.nextInt(10) - 5),
                                0, 0
                        );
                        zombie.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.LEATHER_HELMET));
                        world.spawnEntity(zombie);
                    }
                    player.sendMessage(Text.literal(
                            "More zombies with helmets are spawning because you haven't killed any yet!"
                    ), false);
                }
            }
        });

        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof ZombieEntity zombie && world instanceof ServerWorld serverWorld) {
                ServerPlayerEntity nearest = (ServerPlayerEntity) serverWorld.getClosestPlayer(zombie, 40);
                int kills = (nearest != null)
                        ? zombieKills.getOrDefault(nearest.getUuid(), 0)
                        : 0;
                if (kills > 0) {
                    double baseSpeed = 0.23;
                    double extraSpeed = 0.05 * Math.min(14, kills);
                    var speedAttribute = zombie.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
                    if (speedAttribute != null) {
                        speedAttribute.setBaseValue(baseSpeed + extraSpeed);
                    }
                }
            }
        });
    }

    private static void grantAdvancement(ServerPlayerEntity player, String advancementId, String criterionName) {
        var server = player.getServer();
        if (server == null) return;

        var advancement = server.getAdvancementLoader().get(Identifier.of(advancementId));
        if (advancement == null) {
            System.err.println("[FastCreeperMod] Advancement not found: " + advancementId);
            return;
        }

        var progress = player.getAdvancementTracker().getProgress(advancement);
        if (!progress.isDone()) {
            if (advancement.value().criteria().containsKey(criterionName)) {
                player.getAdvancementTracker().grantCriterion(advancement, criterionName);
                advancement.value().display().ifPresent(display ->
                        player.sendMessage(Text.literal("Advancement granted: " + display.getTitle().getString()), false)
                );
            } else {
                System.err.println("[FastCreeperMod] Criterion not found: " + criterionName + " in advancement " + advancementId);
            }
        }
    }
}