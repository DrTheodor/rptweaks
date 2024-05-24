package dev.drtheo.rptweaks.mixin;

import net.minecraft.client.resource.server.ServerResourcePackLoader;
import net.minecraft.client.resource.server.ServerResourcePackManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerResourcePackLoader.class)
public interface ServerResourcePackLoaderAccessor {

    @Accessor
    ServerResourcePackManager getManager();
}
