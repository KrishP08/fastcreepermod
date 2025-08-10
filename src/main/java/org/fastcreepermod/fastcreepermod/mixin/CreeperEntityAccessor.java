package org.fastcreepermod.fastcreepermod.mixin;

import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.data.TrackedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CreeperEntity.class)
public interface CreeperEntityAccessor {
    @Accessor("CHARGED")
    static TrackedData<Boolean> getChargedData() {
        throw new AssertionError();
    }
}
