package org.fastcreepermod.fastcreepermod;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.UUID;

public class CreeperFeatureHandler {
    private static final HashMap<UUID, Integer> creeperKills = new HashMap<>();
    private static final int CREEPER_KILL_TARGET = 10;
    private static final String ADVANCEMENT_ID = "fastcreepermod:creeper_hunter";

    public static void registerEvents() {
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, entity, killedEntity) -> {
            if (entity instanceof ServerPlayerEntity player && killedEntity instanceof CreeperEntity) {
                UUID id = player.getUuid();
                int kills = creeperKills.getOrDefault(id, 0) + 1;
                creeperKills.put(id, kills);

                player.sendMessage(Text.literal("You have killed " + kills + " creepers."), false);

                if (kills >= CREEPER_KILL_TARGET) {
                    grantAdvancement(player, ADVANCEMENT_ID, "creeper_kill");
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