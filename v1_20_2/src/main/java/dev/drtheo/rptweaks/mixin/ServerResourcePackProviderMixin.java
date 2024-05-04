package dev.drtheo.rptweaks.mixin;

import dev.drtheo.rptweaks.RPTweaks;
import dev.drtheo.rptweaks.TweaksMod;
import dev.drtheo.rptweaks.config.PackEntry;
import dev.drtheo.rptweaks.config.TweaksConfig;
import dev.drtheo.rptweaks.resource.AbstractPackStateObserver;
import net.minecraft.SharedConstants;
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
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Mixin(ServerResourcePackProvider.class)
public class ServerResourcePackProviderMixin {

    @Shadow @Nullable private ResourcePackProfile serverContainer;
    @Shadow @Final private static Text SERVER_NAME_TEXT;

    @Unique private static final TweaksMod mod = TweaksMod.get();

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(File file, CallbackInfo ci) {
        TweaksConfig config = mod.config();
        PackEntry packZip = config.getPacks().get(0);

        if (config.shouldPreload() && config.getPacks() != null) {
            ResourcePackProfile.PackFactory packFactory = new ZipResourcePack.ZipBackedFactory(packZip.path(), false);
            int version = SharedConstants.getGameVersion().getResourceVersion(ResourceType.CLIENT_RESOURCES);

            ResourcePackProfile.Metadata metadata = ResourcePackProfile.loadMetadata("server", packFactory, version);

            if (metadata == null) {
                RPTweaks.LOGGER.error("Invalid pack metadata at " + packZip);
            } else {
                RPTweaks.LOGGER.info("Applying server pack {}", packZip);

                this.serverContainer = ResourcePackProfile.of(
                        "server", SERVER_NAME_TEXT, true, packFactory,
                        metadata, ResourcePackProfile.InsertionPosition.TOP, false,
                        ResourcePackSource.SERVER
                );
            }
        }
    }

    // stop server resourcepack from de-loading
    @Inject(method = "clear", at = @At("HEAD"), cancellable = true)
    public void clear(CallbackInfoReturnable<CompletableFuture<?>> cir) {
        if (mod.observer().shouldClear())
            cir.cancel();
    }

    @Inject(method = "loadServerPack(Ljava/io/File;Lnet/minecraft/resource/ResourcePackSource;)Ljava/util/concurrent/CompletableFuture;", at = @At("HEAD"), cancellable = true)
    public void loadServerPack(File packZip, ResourcePackSource packSource, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        AbstractPackStateObserver observer = mod.observer();
        boolean result = observer.shouldReload();

        if (!result)
            cir.setReturnValue(CompletableFuture.completedFuture(null));

        mod.config().setLatest(List.of(new PackEntry(packZip.toPath(), null)));
    }
}