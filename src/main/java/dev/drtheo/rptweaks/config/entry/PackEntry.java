package dev.drtheo.rptweaks.config.entry;

import dev.drtheo.rptweaks.TweaksMod;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public record PackEntry(Path path, UUID uuid) {

    public String toString(TweaksMod mod) {
        return mod.getPacksDir().relativize(this.path).toString();
    }
}
