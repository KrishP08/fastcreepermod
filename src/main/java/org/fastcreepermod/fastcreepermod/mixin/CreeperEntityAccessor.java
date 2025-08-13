package org.fastcreepermod.fastcreepermod.mixin;

import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.mob.CreeperEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CreeperEntity.class)
public interface CreeperEntityAccessor {
    /**
     * This exposes the private "CHARGED" TrackedData field.
     */
    @Accessor("CHARGED")
    static TrackedData<Boolean> getChargedData() {
        throw new AssertionError();
    }

    /**
     * This exposes the private "ignite()" method.
     */
    @Invoker("ignite")
    void invokeIgnite();

    /**
     * This exposes the private "fuseTime" field.
     */
    @Accessor("fuseTime")
    void setFuseTime(int fuseTime);
}