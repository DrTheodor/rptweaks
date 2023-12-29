package dev.drtheo.rptweaks.mixin;

import dev.drtheo.rptweaks.mixininterface.PackProviderSetter;
import net.minecraft.client.resource.server.ReloadScheduler;
import net.minecraft.client.resource.server.ServerResourcePackLoader;
import net.minecraft.client.resource.server.ServerResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(ServerResourcePackLoader.class)
public interface ServerResourcePackLoaderAccessor extends PackProviderSetter {

    @Invoker
    List<ResourcePackProfile> invokeToProfiles(List<ReloadScheduler.PackInfo> packs);

    @Accessor
    ServerResourcePackManager getManager();

    @Invoker("getPackProvider")
    ResourcePackProvider invokeGetPackProvider(List<ResourcePackProfile> list);
}
