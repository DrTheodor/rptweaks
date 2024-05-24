package dev.drtheo.rptweaks.mixin;

import net.minecraft.client.resource.server.ServerResourcePackManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(ServerResourcePackManager.class)
public interface ServerResourcePackManagerAccessor {

    @Accessor
    List<ServerResourcePackManager.PackEntry> getPacks();
}
