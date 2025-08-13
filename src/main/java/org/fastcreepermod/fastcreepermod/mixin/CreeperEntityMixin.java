package org.fastcreepermod.fastcreepermod.mixin;

import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
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
    private void customExplode(CallbackInfo ci) {
        CreeperEntity self = (CreeperEntity)(Object)this;
        if (!self.getWorld().isClient && self.getWorld() instanceof ServerWorld sw) {
            boolean endCrystalRule = sw.getGameRules().get(ModGameRules.FAST_RANDOM_CREEPER_ENDCRYSTAL).get();
            boolean chargedRule = sw.getGameRules().get(ModGameRules.FAST_RANDOM_CREEPER_CHARGED).get();

            float radius = this.explosionRadius;
            if (endCrystalRule) {
                radius = 8.0f;
            } else if (chargedRule || self.isCharged()) {
                radius = radius * 2.0f;
            }

            ci.cancel();
            FastRandomCreeperHandler.doExplosionEffects(sw, self, radius);
            sw.createExplosion(self, self.getX(), self.getY(), self.getZ(), radius, World.ExplosionSourceType.TNT);
            self.discard();
        }
    }
}
