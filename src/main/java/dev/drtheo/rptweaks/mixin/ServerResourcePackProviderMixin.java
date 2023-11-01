package dev.drtheo.rptweaks.mixin;

import dev.drtheo.rptweaks.RPTweaks;
import dev.drtheo.rptweaks.config.Config;
import net.minecraft.client.resource.ServerResourcePackProvider;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.ZipResourcePack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.util.concurrent.CompletableFuture;

@Mixin(ServerResourcePackProvider.class)
public class ServerResourcePackProviderMixin {

    @Shadow @Nullable private ResourcePackProfile serverContainer;
    @Shadow @Final private static Text SERVER_NAME_TEXT;
    @Unique
    private boolean allowLoad = false;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(File file, CallbackInfo ci) {
        Config config = Config.getConfig();
        File packZip = config.getLatest();

        if (config.shouldPreload() && config.getLatest() != null) {
            ResourcePackProfile.PackFactory packFactory = (name) -> new ZipResourcePack(name, packZip, false);
            ResourcePackProfile.Metadata metadata = ResourcePackProfile.loadMetadata("server", packFactory);

            if (metadata == null) {
                RPTweaks.LOGGER.error("Invalid pack metadata at " + packZip);
            } else {
                RPTweaks.LOGGER.info("Applying server pack {}", packZip);
                this.serverContainer = ResourcePackProfile.of("server", SERVER_NAME_TEXT, true, packFactory, metadata, ResourceType.CLIENT_RESOURCES, ResourcePackProfile.InsertionPosition.TOP, false, ResourcePackSource.SERVER);
            }
        }
    }

    // stop server resourcepack from de-loading
    @Inject(method = "clear", at = @At("HEAD"), cancellable = true)
    public void clear(CallbackInfoReturnable<CompletableFuture<?>> cir) {
        if (this.allowLoad) {
            this.allowLoad = false;
            cir.setReturnValue(null);
        }
    }

    @Inject(method = "loadServerPack(Ljava/io/File;Lnet/minecraft/resource/ResourcePackSource;)Ljava/util/concurrent/CompletableFuture;", at = @At("HEAD"), cancellable = true)
    public void loadServerPack(File packZip, ResourcePackSource packSource, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        Config config = Config.getConfig();
        if (config.isLatest(packZip))
            cir.setReturnValue(CompletableFuture.completedFuture(null));

        config.setLatest(packZip);
        this.allowLoad = true;
    }
}
