package org.fastcreepermod.fastcreepermod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameRules;

public class FastCreeperMod implements ModInitializer {

    public static final String MODID = "fastcreepermod";
    public static final GameRules.Key<GameRules.BooleanRule> ZOMBIES_ENHANCED =
            GameRuleRegistry.register("zombiesEnhanced", // The name used in the /gamerule command
                    GameRules.Category.MOBS, // The category it appears under in the game rule screen
                    GameRuleFactory.createBooleanRule(true) // Creates a boolean (true/false) rule, defaulting to false
            );

    @Override
    public void onInitialize() {
        ModGameRules.registerRules();
        FastRandomCreeperHandler.registerEvents();
        ZombieFeatureHandler.registerEvents(); // Assuming you want to register this too!
        // Register the timed block reward system
        TimedBlockRewarder.register();
        CropGrowthHandler.registerEvents();
        // Message to ops when server starts
        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStart);

        // Use the new ServerMessageEvents.CHAT_MESSAGE event
        ServerMessageEvents.CHAT_MESSAGE.register((message, sender, typeKey) -> {
            String msg = message.getContent().getString().trim().toLowerCase();
            // FIX: Use getWorld() instead of getServerWorld()
            ServerWorld sw = sender.getWorld();

            switch (msg) {
                case "go to creeper level 1":
                    sw.getGameRules().get(ModGameRules.FAST_RANDOM_CREEPER).set(true, sw.getServer());
                    sw.getGameRules().get(ModGameRules.FAST_RANDOM_CREEPER_CHARGED).set(false, sw.getServer());
                    sw.getGameRules().get(ModGameRules.FAST_RANDOM_CREEPER_ENDCRYSTAL).set(false, sw.getServer());
                    sender.sendMessage(Text.literal("Creeper Level 1 enabled (normal)"), false);
                    break;
                case "go to creeper level 2":
                    sw.getGameRules().get(ModGameRules.FAST_RANDOM_CREEPER).set(false, sw.getServer());
                    sw.getGameRules().get(ModGameRules.FAST_RANDOM_CREEPER_CHARGED).set(true, sw.getServer());
                    sw.getGameRules().get(ModGameRules.FAST_RANDOM_CREEPER_ENDCRYSTAL).set(false, sw.getServer());
                    sender.sendMessage(Text.literal("Creeper Level 2 enabled (charged)"), false);
                    break;
                case "go to creeper level 3":
                    sw.getGameRules().get(ModGameRules.FAST_RANDOM_CREEPER).set(false, sw.getServer());
                    sw.getGameRules().get(ModGameRules.FAST_RANDOM_CREEPER_CHARGED).set(false, sw.getServer());
                    sw.getGameRules().get(ModGameRules.FAST_RANDOM_CREEPER_ENDCRYSTAL).set(true, sw.getServer());
                    sender.sendMessage(Text.literal("Creeper Level 3 enabled (End Crystal)"), false);
                    break;
                default:
                    break;
            }
        });
    }

    private void onServerStart(MinecraftServer server) {
        Text msg = Text.literal(
                "[FastCreeperMod] Gamerules: /gamerule fastRandomCreeper true | fastRandomCreeperCharged true | fastRandomCreeperEndCrystal true"
        ).formatted(Formatting.GREEN);

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (server.getPlayerManager().isOperator(player.getGameProfile())) {
                player.sendMessage(msg, false);
            }
        }
    }
}