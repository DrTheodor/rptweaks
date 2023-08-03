package dev.drtheo.rptweaks.mixin;

import dev.drtheo.rptweaks.RPTweaks;
import dev.drtheo.rptweaks.config.Config;
import net.minecraft.client.resource.ClientBuiltinResourcePackProvider;
import net.minecraft.client.resource.ResourceIndex;
import net.minecraft.resource.*;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.text.TranslatableText;
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
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Mixin(ClientBuiltinResourcePackProvider.class)
public class ClientBuiltinResourcePackProviderMixin {

    @Shadow @Final private static String SERVER;

    @Shadow @Nullable private ResourcePackProfile serverContainer;

    @Unique
    private boolean allowLoad = false;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(File serverPacksRoot, ResourceIndex index, CallbackInfo ci) {
        Config config = Config.getConfig();
        File packZip = config.getLatest();

        if (config.shouldPreload() && config.getLatest() != null) {
            try(ZipResourcePack pack = new ZipResourcePack(packZip)) {
                PackResourceMetadata metadata = pack.parseMetadata(PackResourceMetadata.READER);

                RPTweaks.LOGGER.info("Applying server pack {}", packZip);

                this.serverContainer = new ResourcePackProfile(
                        SERVER, true, () -> new ZipResourcePack(packZip),
                        new TranslatableText("resourcePack.server.name"), metadata.getDescription(),
                        ResourcePackCompatibility.from(metadata, ResourceType.CLIENT_RESOURCES),
                        ResourcePackProfile.InsertionPosition.TOP, true, ResourcePackSource.PACK_SOURCE_SERVER
                );
            } catch (IOException exception) {
                RPTweaks.LOGGER.error("Invalid pack metadata at " + packZip);
            }
        }
    }

    // stop server resourcepack from de-loading
    @Inject(method = "clear", at = @At("HEAD"), cancellable = true)
    public void clear(CallbackInfo ci) {
        if (this.allowLoad) {
            ci.cancel();
            this.allowLoad = false;
        }
    }

    @Inject(method = "loadServerPack", at = @At("HEAD"), cancellable = true)
    public void loadServerPack(File packZip, ResourcePackSource packSource, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        Config config = Config.getConfig();
        if (config.isLatest(packZip))
            cir.setReturnValue(null);

        config.setLatest(packZip);
        this.allowLoad = true;
    }
}
