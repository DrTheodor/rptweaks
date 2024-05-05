package dev.drtheo.rptweaks.mixin;

import dev.drtheo.rptweaks.TweaksMod;
import net.minecraft.server.packs.DownloadQueue;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Mixin(DownloadQueue.class)
public class DownloaderMixin {

    @Unique
    private static final TweaksMod mod = TweaksMod.get();

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/DownloadCacheCleaner;vacuumCacheDir(Ljava/nio/file/Path;I)V"))
    public void init(Path directory, int maxRetained) {
        try {
            // TODO: move this to common.
            if (!Files.exists(mod.getPacksDir()))
                return;

            List<File> list = new ArrayList<>(FileUtils.listFiles(mod.getPacksDir().toFile(), TrueFileFilter.TRUE, null));
            list.sort(LastModifiedFileComparator.LASTMODIFIED_REVERSE);

            int max = mod.config().maxPacks();

            for (int i = 0; i < list.size(); i++) {
                File file = list.get(i);

                if (i >= max)
                    FileUtils.deleteQuietly(file);
            }
        } catch (Exception var5) {
            mod.logger().error("Error while deleting old server resource pack : {}", var5.getMessage());
        }
    }
}
