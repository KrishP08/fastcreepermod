package org.fastcreepermod.fastcreepermod;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.ZombieEntity;

public class ZombieFeatureHandler {

    // --- Configuration ---
    private static final double EXTRA_DAMAGE = 3.0;
    private static final int SPEED_AMPLIFIER = 1;

    public static void registerEvents() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            // Return early if we are on the client or the entity is not a ZombieEntity
            if (world.isClient() || !(entity instanceof ZombieEntity zombie)) {
                return;
            }

            // Check the game rule to see if zombies should be enhanced
            if (world.getGameRules().getBoolean(FastCreeperMod.ZOMBIES_ENHANCED)) {
                enhanceZombie(zombie);
            }
        });
    }

    /**
     * Enhances a zombie with custom attributes.
     * @param zombie The ZombieEntity to modify.
     */
    private static void enhanceZombie(ZombieEntity zombie) {
        // Increase the zombie's base attack damage
        EntityAttributeInstance attackAttribute = zombie.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE);
        if (attackAttribute != null) {
            // Set the base damage to its current value plus our extra damage
            attackAttribute.setBaseValue(attackAttribute.getBaseValue() + EXTRA_DAMAGE);
        }

        // --- CHANGE ZOMBIE SIZE ---
        // By setting the zombie to a baby, its size is reduced to half.
        // This is the standard Minecraft way to change mob size without complex rendering changes.
        // This also changes its hitbox and makes it faster.
        zombie.setBaby(true);

        // Add a permanent speed boost
        zombie.addStatusEffect(new StatusEffectInstance(
                StatusEffects.SPEED,
                StatusEffectInstance.INFINITE, // Effect lasts forever
                SPEED_AMPLIFIER,               // Speed I = +20% speed, Speed II = +40%, etc.
                false,                         // isAmbient - less noticeable particles
                false                          // showParticles - hides particles completely
        ));
    }
}