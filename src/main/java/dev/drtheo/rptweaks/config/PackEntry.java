package dev.drtheo.rptweaks.config;

import dev.drtheo.rptweaks.RPTweaks;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.resource.server.ReloadScheduler;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public record PackEntry(Path path, UUID uuid) {

    private static final Path DOWNLOADS = FabricLoader.getInstance().getGameDir().resolve("downloads");

    public String asString() {
        RPTweaks.LOGGER.info("SAVING PACK ENTRY: " + this);
        return DOWNLOADS.relativize(this.path) + ":" + uuid;
    }

    public static PackEntry fromString(String str) {
        String[] parts = str.split(":");

        if (parts.length != 2)
            return null;

        String file = parts[0];
        String uuid = parts[1];

        Path path = DOWNLOADS.resolve(file);

        if (Files.notExists(path))
            return null;

        return new PackEntry(path, UUID.fromString(uuid));
    }

    public ReloadScheduler.PackInfo toPackInfo() {
        return new ReloadScheduler.PackInfo(this.uuid, this.path);
    }
}
