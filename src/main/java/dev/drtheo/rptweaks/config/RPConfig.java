package dev.drtheo.rptweaks.config;

import dev.drtheo.rptweaks.TweaksMod;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class RPConfig extends TweaksConfig {

    public RPConfig(TweaksMod mod) {
        super("rptweaks.v2", mod);
    }

    @Override
    public PackEntry entry(String entry) {
        String[] parts = entry.split(":");

        if (parts.length != 2)
            return null;

        String file = parts[0];
        String uuid = parts[1];

        Path path = this.mod.getPacksDir().resolve(file);

        if (Files.notExists(path))
            return null;

        return new PackEntry(path, UUID.fromString(uuid));
    }

    @Override
    public String entry(PackEntry entry) {
        return this.mod.getPacksDir().relativize(entry.path()) + ":" + entry.uuid();
    }
}
