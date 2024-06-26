package dev.drtheo.rptweaks;

import dev.drtheo.rptweaks.config.TweaksConfig;
import dev.drtheo.rptweaks.config.entry.SimplePackEntry;
import dev.drtheo.rptweaks.resource.AbstractPackStateObserver;
import dev.drtheo.rptweaks.resource.PackStateObserver;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class RPTweaks extends TweaksMod {

    public RPTweaks() {
        super("server-resource-packs");
    }

    @Override
    protected Path getGameDir() {
        return FabricLoader.getInstance().getGameDir();
    }

    @Override
    protected TweaksConfig createConfig() {
        return new TweaksConfig("rptweaks.v1", this, SimplePackEntry::fromString);
    }

    @Override
    protected AbstractPackStateObserver createObserver() {
        return new PackStateObserver(this);
    }
}
