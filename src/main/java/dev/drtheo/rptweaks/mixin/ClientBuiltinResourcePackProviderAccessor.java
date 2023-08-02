package dev.drtheo.rptweaks.mixin;

import net.minecraft.client.resource.ClientBuiltinResourcePackProvider;
import net.minecraft.resource.ResourcePackProfile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientBuiltinResourcePackProvider.class)
public interface ClientBuiltinResourcePackProviderAccessor {

    @Accessor("serverContainer")
    void setServerContainer(ResourcePackProfile profile);
}
