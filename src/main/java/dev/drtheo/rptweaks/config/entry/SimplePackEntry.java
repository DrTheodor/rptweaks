package dev.drtheo.rptweaks.config.entry;

import dev.drtheo.rptweaks.TweaksMod;

import java.nio.file.Files;
import java.nio.file.Path;

public interface SimplePackEntry {

    static PackEntry fromString(TweaksMod mod, String entry) {
        // e.g. a4bd2e849c14f3359a7a21910ad966e035077112
        Path path = mod.getPacksDir().resolve(entry);

        if (!Files.exists(path))
            return null;

        return new PackEntry(mod.getPacksDir().resolve(path), null);
    }
}
