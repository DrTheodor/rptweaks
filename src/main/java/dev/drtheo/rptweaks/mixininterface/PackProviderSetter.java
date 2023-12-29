package dev.drtheo.rptweaks.mixininterface;

import net.minecraft.resource.ResourcePackProfile;

import java.util.List;

public interface PackProviderSetter {
    void rptweaks$setPackProvider(List<ResourcePackProfile> profiles);
}
