package dev.drtheo.rptweaks;

import dev.drtheo.rptweaks.config.TweaksConfig;
import dev.drtheo.rptweaks.entrypoint.EarlyClientModInitializer;
import dev.drtheo.rptweaks.resource.AbstractPackStateObserver;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public abstract class TweaksMod implements EarlyClientModInitializer {

    public static Logger LOGGER = LoggerFactory.getLogger("rptweaks");

    private static TweaksMod instance;

    private static AbstractPackStateObserver observer;
    private static TweaksConfig config;

    private final Path packs;
    private final Path configs;

    public TweaksMod(String packs) {
        instance = this;

        this.packs = this.getGameDir().resolve(packs);
        this.configs = this.getGameDir().resolve("config");
    }

    @Override
    public void onEarlyInitializeClient() {
        config = this.createConfig();
        observer = this.createObserver();
    }

    public void handleDisconnect() {
        observer.onDisconnect();
    }

    public Path getConfigDir() {
        return configs;
    }

    public Path getPacksDir() {
        return packs;
    }

    protected abstract Path getGameDir();

    protected abstract TweaksConfig createConfig();
    protected abstract AbstractPackStateObserver createObserver();

    public TweaksConfig config() {
        return config;
    }

    public AbstractPackStateObserver observer() {
        return observer;
    }

    public Logger logger() {
        return LOGGER;
    }

    public static TweaksMod get() {
        if (instance == null) {
            // every time an entrypoint is ran, a new instance of the class is initialized.
            FabricLoader.getInstance().getEntrypoints("client-early",
                    EarlyClientModInitializer.class
            ).forEach(EarlyClientModInitializer::onEarlyInitializeClient);
        }

        return instance;
    }
}
