package dev.drtheo.rptweaks.mixin;

import dev.drtheo.rptweaks.RPTweaks;
import dev.drtheo.rptweaks.TweaksMod;
import dev.drtheo.rptweaks.config.TweaksConfig;
import dev.drtheo.rptweaks.config.entry.PackEntry;
import dev.drtheo.rptweaks.resource.AbstractPackStateObserver;
import net.minecraft.SharedConstants;
import net.minecraft.client.resources.DownloadedPackSource;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Mixin(DownloadedPackSource.class)
public class ServerResourcePackProviderMixin {

    @Shadow @Nullable private Pack serverPack;
    @Shadow @Final private static Component SERVER_NAME;

    @Shadow @Final private File serverPackDir;
    @Unique private static final TweaksMod mod = TweaksMod.get();

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(File file, CallbackInfo ci) {
        TweaksConfig config = mod.config();
        PackEntry packZip = config.getPacks().get(0);

        if (!config.shouldPreload() || config.getPacks() == null)
            return;

        Pack.ResourcesSupplier packFactory = new FilePackResources.FileResourcesSupplier(packZip.path(), false);
        int version = SharedConstants.getCurrentVersion().getPackVersion(PackType.CLIENT_RESOURCES);

        Pack.Info metadata = Pack.readPackInfo("server", packFactory, version);

        if (metadata == null) {
            RPTweaks.LOGGER.error("Invalid pack metadata at " + packZip);
        } else {
            RPTweaks.LOGGER.info("Applying server pack {}", packZip);

            this.serverPack = Pack.create(
                    "server", SERVER_NAME, true, packFactory,
                    metadata, Pack.Position.TOP, false,
                    PackSource.SERVER
            );

            mod.observer().setPreloaded(true);
        }
    }

    // stop server resourcepack from de-loading
    @Inject(method = "clearServerPack", at = @At("HEAD"))
    public void clear(CallbackInfoReturnable<CompletableFuture<?>> cir) {
        if (mod.observer().shouldClear())
            cir.cancel();
    }

    @Inject(method = "setServerPack", at = @At("HEAD"), cancellable = true)
    public void loadServerPack(File packZip, PackSource packSource, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        AbstractPackStateObserver observer = mod.observer();
        boolean result = observer.shouldReload();
        mod.observer().onFinish();

        if (!result)
            cir.setReturnValue(CompletableFuture.completedFuture(null));

        mod.config().setLatest(List.of(new PackEntry(packZip.toPath(), null)));
    }

    /**
     * @author DrTheo_
     * @reason yes.
     */
    @Overwrite
    private void clearOldDownloads() {
        // TODO: move this to common.
        if (!this.serverPackDir.isDirectory())
            return;

        try {
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