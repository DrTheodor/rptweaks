package dev.drtheo.rptweaks.config.entry;

import dev.drtheo.rptweaks.TweaksMod;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public interface AdvancedPackEntry {

    static PackEntry fromString(TweaksMod mod, String entry) {
        // 7ebfa78f-3a46-3ff5-97ab-525d4ffeaf95/a4bd2e849c14f3359a7a21910ad966e035077112
        Path path = mod.getPacksDir().resolve(entry);

        if (!Files.exists(path))
            return null;

        String[] parts = entry.split("/");
        String uuid = parts.length == 2 ? parts[0] : null; // the format for 1.20.4+ is: "uuid/hash"

        if (uuid == null)
            return new PackEntry(path, null);

        return new PackEntry(path, UUID.fromString(uuid));
    }
}
