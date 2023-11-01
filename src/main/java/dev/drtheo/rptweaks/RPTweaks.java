package dev.drtheo.rptweaks;

import dev.drtheo.rptweaks.config.Config;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RPTweaks implements ClientModInitializer {

    public static Logger LOGGER = LoggerFactory.getLogger("rptweaks");

    /**
     * Runs the mod initializer on the client environment.
     */
    @Override
    public void onInitializeClient() {
        ClientLifecycleEvents.CLIENT_STOPPING.register(client ->
                Config.getConfig().save()
        );
    }
}
