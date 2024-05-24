package dev.drtheo.rptweaks.resource;

import dev.drtheo.rptweaks.TweaksMod;
import dev.drtheo.rptweaks.config.entry.PackEntry;
import dev.drtheo.rptweaks.mixin.PackEntryAcessor;
import dev.drtheo.rptweaks.mixin.ServerResourcePackLoaderAccessor;
import dev.drtheo.rptweaks.mixin.ServerResourcePackManagerAccessor;
import dev.drtheo.rptweaks.mixininterface.ServerInfoLike;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.resource.server.ServerResourcePackManager;

import java.nio.file.Path;
import java.util.Collection;
import java.util.UUID;

public class PackStateObserver extends AbstractPackStateObserver {

    private final MinecraftClient client = MinecraftClient.getInstance();

    public PackStateObserver(TweaksMod mod) {
        super(mod);
    }

    @Override
    public Collection<? extends PackEntry> active() {
        ServerResourcePackLoaderAccessor loader = (ServerResourcePackLoaderAccessor) client
                .getServerResourcePackProvider();

        ServerResourcePackManagerAccessor manager = (ServerResourcePackManagerAccessor) loader.getManager();
        return manager.getPacks().stream().map(PackStateObserver::fromVanilla).toList();
    }

    @Override
    protected ServerInfoLike getCurrentServer() {
        ServerInfo result = client.getCurrentServerEntry();
        return result != null ? (ServerInfoLike) result : null;
    }

    private static PackEntry fromVanilla(ServerResourcePackManager.PackEntry entry) {
        PackEntryAcessor accessor = (PackEntryAcessor) entry;

        Path path = accessor.getPath() == null
                ? Path.of(accessor.getUrl().getPath())
                : accessor.getPath();

        UUID uuid = accessor.getId();
        return new PackEntry(path, uuid);
    }
}
