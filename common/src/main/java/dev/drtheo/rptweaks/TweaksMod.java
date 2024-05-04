package dev.drtheo.rptweaks;

import dev.drtheo.rptweaks.config.TweaksConfig;
import dev.drtheo.rptweaks.mixininterface.ClientLike;
import dev.drtheo.rptweaks.resource.AbstractPackStateObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.function.Function;

public abstract class TweaksMod {

    public static Logger LOGGER = LoggerFactory.getLogger("rptweaks");

    private static TweaksMod instance;

    private final AbstractPackStateObserver observer;
    private final TweaksConfig config;

    private final Path packs;
    private final Path configs;

    public TweaksMod(String packs, Function<TweaksMod, TweaksConfig> config, Function<TweaksMod, AbstractPackStateObserver> observer) {
        TweaksMod.set(this);

        this.packs = this.getGameDir().resolve(packs);
        this.configs = this.getGameDir().resolve("config");

        this.config = config.apply(this);
        this.observer = observer.apply(this);
    }

    public void onDisconnect() {
        this.config.setLatest(this.observer.active());
        this.config.save();
    }

    public void handleDisconnect() {
        new Thread(this::onDisconnect).start();
    }

    public Path getConfigDir() {
        return configs;
    }

    public Path getPacksDir() {
        return packs;
    }

    protected abstract Path getGameDir();

    public TweaksConfig config() {
        return config;
    }

    public AbstractPackStateObserver observer() {
        return observer;
    }

    public Logger logger() {
        return LOGGER;
    }

    public abstract ClientLike client();

    public static TweaksMod get() {
        return instance;
    }

    public static void set(TweaksMod impl) {
        instance = impl;
    }
}
