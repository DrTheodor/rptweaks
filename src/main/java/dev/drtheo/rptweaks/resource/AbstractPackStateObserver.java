package dev.drtheo.rptweaks.resource;

import dev.drtheo.rptweaks.TweaksMod;
import dev.drtheo.rptweaks.config.TweaksConfig;
import dev.drtheo.rptweaks.mixininterface.ServerInfoLike;
import dev.drtheo.rptweaks.config.entry.PackEntry;

import java.util.Collection;

public abstract class AbstractPackStateObserver {

    protected final TweaksMod mod;

    protected boolean allowReload = false;
    protected boolean preloaded = false;

    protected boolean initializing = false;

    protected ServerInfoLike lastServer;

    public AbstractPackStateObserver(TweaksMod mod) {
        this.mod = mod;
    }

    public void onDisconnect() {
        this.allowReload = false;

        new Thread(() -> {
            TweaksConfig config = this.mod.config();
            config.setLatest(this.active());
            config.save();
        }).start();
    }

    public boolean shouldReload() {
        if (this.initializing) {
            this.initializing = false;
            return true;
        }

        ServerInfoLike info = this.getCurrentServer();

        if (this.preloaded && info.rpt$equals(this.mod.config().getLastServer()))
            return false;

        this.preloaded = false;

        if (this.sameServer(info))
            return allowReload;

        return true;
    }

    public boolean shouldClear() {
        return !this.sameServer(this.getCurrentServer());
    }

    public void onFinish() {
        this.lastServer = this.getCurrentServer();
        this.allowReload = true;

        // may be null during the preload.
        if (this.lastServer != null)
            this.mod.config().setLastServer(this.lastServer.rpt$address());
    }

    public boolean sameServer(ServerInfoLike info) {
        System.out.println("SAME?: " + (info == null ? null : info.rpt$address()) + " vs " + (this.lastServer == null ? null : this.lastServer.rpt$address()));
        if (this.lastServer == null)
            return false;

        return this.lastServer.rpt$equals(info);
    }

    public void setPreloaded(boolean preloaded) {
        this.preloaded = preloaded;
    }

    public void setInitializing(boolean initializing) {
        this.initializing = initializing;
    }

    public abstract Collection<? extends PackEntry> active();

    protected abstract ServerInfoLike getCurrentServer();
}
