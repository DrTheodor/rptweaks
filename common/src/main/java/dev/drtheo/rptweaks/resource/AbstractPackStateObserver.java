package dev.drtheo.rptweaks.resource;

import dev.drtheo.rptweaks.TweaksMod;
import dev.drtheo.rptweaks.config.PackEntry;
import dev.drtheo.rptweaks.mixininterface.ClientLike;
import dev.drtheo.rptweaks.mixininterface.ServerInfoLike;

import java.util.Collection;

public abstract class AbstractPackStateObserver {

    protected final TweaksMod mod;
    private final ClientLike client;

    protected boolean preloaded = false;

    protected ServerInfoLike lastServer;

    public AbstractPackStateObserver(TweaksMod mod) {
        this.mod = mod;
        this.client = mod.client();
    }

    public boolean shouldReload() {
        if (this.sameServer(this.client.rptweaks$serverEntry()) || this.preloaded) {
            this.preloaded = false;
            return false;
        }

        return true;
    }

    public boolean shouldClear() {
        return !this.sameServer(this.client.rptweaks$serverEntry());
    }

    public void onFinish() {
        this.lastServer = this.client.rptweaks$serverEntry();
    }

    public boolean sameServer(ServerInfoLike info) {
        if (this.lastServer == null)
            return false;

        return this.lastServer.rptweaks$equals(info);
    }

    public void setPreloaded(boolean preloaded) {
        this.preloaded = preloaded;
    }

    public abstract Collection<? extends PackEntry> active();
}
