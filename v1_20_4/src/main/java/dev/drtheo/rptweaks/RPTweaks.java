package dev.drtheo.rptweaks;

import dev.drtheo.rptweaks.config.TweaksConfig;
import dev.drtheo.rptweaks.config.entry.AdvancedPackEntry;
import dev.drtheo.rptweaks.resource.AbstractPackStateObserver;
import dev.drtheo.rptweaks.resource.PackStateObserver;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class RPTweaks extends TweaksMod {

    public RPTweaks() {
        super("downloads");

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client)
                -> this.handleDisconnect());
    }

    @Override
    protected Path getGameDir() {
        return FabricLoader.getInstance().getGameDir();
    }

    @Override
    protected TweaksConfig createConfig() {
        return new TweaksConfig("rptweaks.v2", this, AdvancedPackEntry::fromString);
    }

    @Override
    protected AbstractPackStateObserver createObserver() {
        return new PackStateObserver(this);
    }
}
