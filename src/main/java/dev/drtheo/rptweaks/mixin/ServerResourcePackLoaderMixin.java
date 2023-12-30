package dev.drtheo.rptweaks.mixin;

import dev.drtheo.rptweaks.RPTweaks;
import dev.drtheo.rptweaks.config.Config;
import dev.drtheo.rptweaks.mixininterface.PackProviderSetter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.resource.server.ReloadScheduler;
import net.minecraft.client.resource.server.ServerResourcePackLoader;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@Mixin(ServerResourcePackLoader.class)
public abstract class ServerResourcePackLoaderMixin implements PackProviderSetter {

    @Shadow private ResourcePackProvider packProvider;
    @Unique
    private boolean allowLoad = false;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(MinecraftClient client, Path downloadsDirectory, RunArgs.Network runArgs, CallbackInfo ci) {
        RPTweaks.setLoader((ServerResourcePackLoader) (Object) this);
    }

    // stop server resourcepack from de-loading
    @Inject(method = "clear", at = @At("HEAD"), cancellable = true)
    public void clear(CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "reload", at = @At("HEAD"), cancellable = true)
    public void reload(ReloadScheduler.ReloadContext context, CallbackInfo ci) {
        if (this.allowLoad) {
            this.allowLoad = false;

            context.onSuccess();
            ci.cancel();
        }
    }

    @Inject(method = "addResourcePack(Ljava/util/UUID;Ljava/net/URL;Ljava/lang/String;)V", at = @At("HEAD"), cancellable = true)
    public void addResourcePack(UUID id, URL url, String hash, CallbackInfo ci) {
        Config config = Config.getConfig();

        if (config.isLatest(Path.of(url.getPath())))
            ci.cancel();

        this.allowLoad = true;
    }

    @Inject(method = "addResourcePack(Ljava/util/UUID;Ljava/nio/file/Path;)V", at = @At("HEAD"), cancellable = true)
    public void addResourcePack(UUID id, Path path, CallbackInfo ci) {
        Config config = Config.getConfig();

        if (config.isLatest(path))
            ci.cancel();

        this.allowLoad = true;
    }

    @Override
    public void rptweaks$setPackProvider(List<ResourcePackProfile> profiles) {
        this.packProvider = ((ServerResourcePackLoaderAccessor) this).invokeGetPackProvider(profiles);
    }
}
