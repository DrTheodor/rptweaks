package dev.drtheo.rptweaks.mixin;

import net.minecraft.client.resource.server.ServerResourcePackManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.net.URL;
import java.nio.file.Path;
import java.util.UUID;

@Mixin(ServerResourcePackManager.PackEntry.class)
public interface PackEntryAcessor {

    @Accessor
    URL getUrl();

    @Accessor
    UUID getId();

    @Accessor
    Path getPath();
}
