package org.fastcreepermod.fastcreepermod;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;

public class TimedBlockRewarder {
    private static int tickCounter = 0;

    // 20 ticks = 1 second, so 20 * 300 = 300 seconds = 5 minutes
    private static final int TICKS_PER_REWARD = 20 * 30; // Change 300 to your desired interval in seconds

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            tickCounter++;
            if (tickCounter >= TICKS_PER_REWARD) {
                tickCounter = 0;

                // Give block to every online player
                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                    RandomBlockRewarder.giveAppropriateRandomBlock(player);
                }
            }
        });
    }
}
