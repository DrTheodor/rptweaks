package dev.drtheo.rptweaks.resource;

import dev.drtheo.rptweaks.TweaksMod;
import dev.drtheo.rptweaks.config.PackEntry;
import dev.drtheo.rptweaks.mixin.ServerResourcePackProviderAccessor;
import net.minecraft.client.MinecraftClient;

import java.util.Collection;
import java.util.List;

public class PackStateObserver extends AbstractPackStateObserver {

    private final MinecraftClient client = MinecraftClient.getInstance();

    public PackStateObserver(TweaksMod mod) {
        super(mod);
    }

    @Override
    public Collection<? extends PackEntry> active() {
        ServerResourcePackProviderAccessor accessor = (ServerResourcePackProviderAccessor)
                client.getServerResourcePackProvider();

        String name = accessor.getServerContainer().getName();
        return List.of(this.mod.config().entry(name));
    }
}
