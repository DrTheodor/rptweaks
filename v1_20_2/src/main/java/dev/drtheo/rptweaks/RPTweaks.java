package dev.drtheo.rptweaks;

import dev.drtheo.rptweaks.config.RPConfig;
import dev.drtheo.rptweaks.mixininterface.ClientLike;
import dev.drtheo.rptweaks.resource.PackStateObserver;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;

import java.nio.file.Path;

public class RPTweaks extends TweaksMod implements ClientModInitializer {

    public RPTweaks() {
        super("server-resource-packs", RPConfig::new, PackStateObserver::new);
    }

    @Override
    public void onInitializeClient() {
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> this.handleDisconnect());
    }

    @Override
    protected Path getGameDir() {
        return FabricLoader.getInstance().getGameDir();
    }

    @Override
    public ClientLike client() {
        return (ClientLike) MinecraftClient.getInstance();
    }
}
