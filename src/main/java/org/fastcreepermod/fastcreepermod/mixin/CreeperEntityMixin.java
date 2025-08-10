package org.fastcreepermod.fastcreepermod.mixin;

import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.fastcreepermod.fastcreepermod.FastRandomCreeperHandler;
import org.fastcreepermod.fastcreepermod.ModGameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreeperEntity.class)
public abstract class CreeperEntityMixin {
    @Shadow private int explosionRadius;

    @Inject(method = "explode", at = @At("HEAD"), cancellable = true)
    private void modifyExplosion(CallbackInfo ci) {
        CreeperEntity self = (CreeperEntity)(Object)this;
        if (!self.getWorld().isClient && self.getWorld() instanceof ServerWorld sw) {

            boolean chargedRule = sw.getGameRules().get(ModGameRules.FAST_RANDOM_CREEPER_CHARGED).get();
            boolean endCrystalRule = sw.getGameRules().get(ModGameRules.FAST_RANDOM_CREEPER_ENDCRYSTAL).get();

            float radius = this.explosionRadius;

            if (endCrystalRule) {
                radius = 16.0F; // End Crystal power
            } else if (chargedRule) {
                radius = radius * 2.0F; // Charged creeper power
            } else if (self.isCharged()) {
                radius = radius * 2.0F; // Vanilla charged behavior
            }

            ci.cancel(); // prevent vanilla explosion
            sw.createExplosion(self, self.getX(), self.getY(), self.getZ(), radius, World.ExplosionSourceType.TNT);

            FastRandomCreeperHandler.applyExplosionKnockback(sw, new BlockPos(self.getBlockPos()), radius, self.getTarget() instanceof net.minecraft.entity.player.PlayerEntity p ? p : null);
            self.discard();
        }
    }
}
