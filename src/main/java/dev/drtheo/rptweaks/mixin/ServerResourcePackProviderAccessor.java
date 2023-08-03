package dev.drtheo.rptweaks.mixin;

import net.minecraft.client.resource.ServerResourcePackProvider;
import net.minecraft.resource.ResourcePackProfile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerResourcePackProvider.class)
public interface ServerResourcePackProviderAccessor {

    @Accessor("serverContainer")
    void setServerContainer(ResourcePackProfile profile);
}
