package org.fastcreepermod.fastcreepermod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class FastCreeperMod implements ModInitializer {
    public static final String MODID = "fastcreepermod";

    @Override
    public void onInitialize() {
        ModGameRules.registerRules();
        FastRandomCreeperHandler.registerEvents();
        ModRegistry.register();
        BismithOreWorldGen.init();

        // Message to ops when server starts
        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStart);
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
