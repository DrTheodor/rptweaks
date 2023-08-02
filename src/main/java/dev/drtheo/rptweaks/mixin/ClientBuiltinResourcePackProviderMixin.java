package dev.drtheo.rptweaks.mixin;

import dev.drtheo.rptweaks.config.Config;
import net.minecraft.client.resource.ClientBuiltinResourcePackProvider;
import net.minecraft.resource.ResourcePackSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.util.concurrent.CompletableFuture;

@Mixin(ClientBuiltinResourcePackProvider.class)
public class ClientBuiltinResourcePackProviderMixin {

    // stop server resourcepack from de-loading
    @Inject(method = "clear", at = @At("HEAD"), cancellable = true)
    public void clear(CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "loadServerPack", at = @At("HEAD"), cancellable = true)
    public void loadServerPack(File packZip, ResourcePackSource packSource, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        Config config = Config.getConfig();
        if (config.isLatest(packZip))
            cir.setReturnValue(null);

        config.setLatest(packZip);
    }
}
