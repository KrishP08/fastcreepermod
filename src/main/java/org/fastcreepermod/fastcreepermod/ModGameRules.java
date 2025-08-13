package org.fastcreepermod.fastcreepermod;

import net.minecraft.world.GameRules;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;

public class ModGameRules {
    public static GameRules.Key<GameRules.BooleanRule> FAST_RANDOM_CREEPER;
    public static GameRules.Key<GameRules.BooleanRule> FAST_RANDOM_CREEPER_CHARGED;
    public static GameRules.Key<GameRules.BooleanRule> FAST_RANDOM_CREEPER_ENDCRYSTAL;

    public static void registerRules() {
        FAST_RANDOM_CREEPER = GameRuleRegistry.register(
                "fastRandomCreeper", GameRules.Category.MOBS, GameRuleFactory.createBooleanRule(true)
        );
        FAST_RANDOM_CREEPER_CHARGED = GameRuleRegistry.register(
                "fastRandomCreeperCharged", GameRules.Category.MOBS, GameRuleFactory.createBooleanRule(false)
        );
        FAST_RANDOM_CREEPER_ENDCRYSTAL = GameRuleRegistry.register(
                "fastRandomCreeperEndCrystal", GameRules.Category.MOBS, GameRuleFactory.createBooleanRule(false)
        );//
    }
}
