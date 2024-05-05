package dev.drtheo.rptweaks.resource;

import dev.drtheo.rptweaks.TweaksMod;
import dev.drtheo.rptweaks.config.entry.PackEntry;
import dev.drtheo.rptweaks.mixin.ServerResourcePackProviderAccessor;
import net.minecraft.class_310;

import java.util.Collection;
import java.util.List;

public class PackStateObserver extends AbstractPackStateObserver {

    public PackStateObserver(TweaksMod mod) {
        super(mod);
    }

    @Override
    public Collection<? extends PackEntry> active() {
        ServerResourcePackProviderAccessor accessor = (ServerResourcePackProviderAccessor)
                class_310.method_1551().method_1516();

        String name = accessor.getServerPack().getId();
        return List.of(this.mod.config().entry(name));
    }
}
