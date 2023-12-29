package dev.drtheo.rptweaks;

import dev.drtheo.rptweaks.config.Config;
import dev.drtheo.rptweaks.config.PackEntry;
import dev.drtheo.rptweaks.mixin.PackEntryAcessor;
import dev.drtheo.rptweaks.mixin.ServerResourcePackLoaderAccessor;
import dev.drtheo.rptweaks.mixin.ServerResourcePackManagerAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.resource.server.ServerResourcePackLoader;
import net.minecraft.client.resource.server.ServerResourcePackManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class RPTweaks implements ClientModInitializer {

    public static Logger LOGGER = LoggerFactory.getLogger("rptweaks");

    private static ServerResourcePackLoaderAccessor loader;

    /**
     * Runs the mod initializer on the client environment.
     */
    @Override
    public void onInitializeClient() {
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            ServerResourcePackManager manager = loader.getManager();
            List<PackEntry> packs = ((ServerResourcePackManagerAccessor) manager).getPacks().stream()
                    .map(packEntry -> {
                        PackEntryAcessor accessor = (PackEntryAcessor) packEntry;

                        Path path = accessor.getPath() == null ? Path.of(accessor.getUrl().getPath())
                                : accessor.getPath();
                        UUID uuid = accessor.getId();

                        return new PackEntry(path, uuid);
                    })
                    .toList();

            Config.getConfig().setLatest(packs);
            Config.getConfig().save();
        });

        Config config = Config.getConfig();
        Collection<PackEntry> latest = config.getLatest();

        loader.rptweaks$setPackProvider(
                loader.invokeToProfiles(latest.stream().map(PackEntry::toPackInfo).toList())
        );
    }

    public static void setLoader(ServerResourcePackLoader loader) {
        RPTweaks.loader = (ServerResourcePackLoaderAccessor) loader;
    }
}
