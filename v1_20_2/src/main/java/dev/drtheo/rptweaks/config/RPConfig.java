package dev.drtheo.rptweaks.config;

import dev.drtheo.rptweaks.TweaksMod;

import java.nio.file.Files;
import java.nio.file.Path;

public class RPConfig extends TweaksConfig {

    public RPConfig(TweaksMod mod) {
        super("rptweaks.v1", mod);
    }

    @Override
    public PackEntry entry(String entry) {
        Path path = this.mod.getPacksDir().resolve(entry);

        if (Files.notExists(path))
            return null;

        return new PackEntry(path, null);
    }

    @Override
    public String entry(PackEntry entry) {
        return this.mod.getPacksDir().relativize(entry.path()).toString();
    }
}
