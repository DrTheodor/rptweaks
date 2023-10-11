package dev.drtheo.rptweaks.events;

import dev.drtheo.rptweaks.util.PackManagerUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ServerInfo;

public class ServerJoinListener implements ClientPlayConnectionEvents.Join {

    // This is important, because some servers seem to redirect the player after the pack loading which might make the mod to disable the pack.
    private ServerInfo lastServer;

    @Override
    public void onPlayReady(ClientPlayNetworkHandler handler, PacketSender sender, MinecraftClient client) {
        // If there's a left-over pack, then disable it
        if (this.lastServer != null && this.lastServer.equals(client.getCurrentServerEntry()))
            return;

        this.lastServer = client.getCurrentServerEntry();
        PackManagerUtil.clearTempPack();
    }
}
