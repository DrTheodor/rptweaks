package dev.drtheo.rptweaks.mixin;

import dev.drtheo.rptweaks.util.PackManagerUtil;
import dev.drtheo.rptweaks.RPTweaks;
import dev.drtheo.rptweaks.util.Reloadable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.ServerResourcePackProvider;
import net.minecraft.resource.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.util.concurrent.CompletableFuture;

@Mixin(ServerResourcePackProvider.class)
public class ServerResourcePackProviderMixin implements Reloadable {

    // This is a pack that is forcefully loaded by the server and which needs to be disabled after use
    @Unique
    private String serverLoaded;

    // Stop server pack from de-loading
    @Inject(method = "clear", at = @At("HEAD"), cancellable = true)
    public void clear(CallbackInfoReturnable<CompletableFuture<?>> cir) {
        cir.setReturnValue(null);
    }

    // maybe @Overwrite it?
    @Inject(method = "loadServerPack(Ljava/io/File;Lnet/minecraft/resource/ResourcePackSource;)Ljava/util/concurrent/CompletableFuture;", at = @At("HEAD"), cancellable = true)
    public void loadServerPack(File packZip, ResourcePackSource packSource, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        this.serverLoaded = packZip.getName();
        PackManagerUtil.enable(this.serverLoaded);

        RPTweaks.LOGGER.info("Applying server pack {}", packZip);
        cir.setReturnValue(MinecraftClient.getInstance().reloadResourcesConcurrently());
    }

    // Custom clear strategy
    @Override
    public void rptweaks$clear() {
        if (this.serverLoaded != null) {
            RPTweaks.LOGGER.info("Disabling server pack {}", this.serverLoaded);

            PackManagerUtil.disable(this.serverLoaded);
            this.serverLoaded = null;
        }
    }
}
