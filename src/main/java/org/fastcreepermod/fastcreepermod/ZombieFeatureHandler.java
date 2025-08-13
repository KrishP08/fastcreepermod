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
            if (world.isClient() || !(entity instanceof ZombieEntity zombie)) {
                return;
            }

            // FIX: Correctly reference the game rule from your main mod class.
            if (world.getGameRules().getBoolean(FastCreeperMod.ZOMBIES_ENHANCED)) {
                enhanceZombie(zombie);
            }
        });
    }

    private static void enhanceZombie(ZombieEntity zombie) {
        EntityAttributeInstance attackAttribute = zombie.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE);
        if (attackAttribute != null) {
            attackAttribute.setBaseValue(attackAttribute.getBaseValue() + EXTRA_DAMAGE);
        }

        if (!zombie.isBaby()) {
            zombie.setBaby(true);
        }

        zombie.addStatusEffect(new StatusEffectInstance(
                StatusEffects.SPEED,
                StatusEffectInstance.INFINITE,
                SPEED_AMPLIFIER,
                false, // isAmbient
                false  // showParticles
        ));
    }
}
