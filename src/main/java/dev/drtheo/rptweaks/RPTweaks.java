package dev.drtheo.rptweaks;

import dev.drtheo.rptweaks.config.Config;
import dev.drtheo.rptweaks.events.ServerJoinListener;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.network.ServerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RPTweaks implements ClientModInitializer {
    public static Logger LOGGER = LoggerFactory.getLogger("rptweaks");
    private ServerInfo lastServer;

    /**
     * Runs the mod initializer on the client environment.
     */
    @Override
    public void onInitializeClient() {
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> Config.getConfig().save());
        ClientPlayConnectionEvents.JOIN.register(new ServerJoinListener());
    }
}
