package dev.drtheo.rptweaks.mixin;

import dev.drtheo.rptweaks.TweaksMod;
import dev.drtheo.rptweaks.config.PackEntry;
import dev.drtheo.rptweaks.resource.AbstractPackStateObserver;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.resource.server.ReloadScheduler;
import net.minecraft.client.resource.server.ServerResourcePackLoader;
import net.minecraft.client.resource.server.ServerResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;
import java.util.Collection;

@Mixin(ServerResourcePackLoader.class)
public abstract class ServerResourcePackLoaderMixin {

    @Shadow @Final
    ServerResourcePackManager manager;

    @Shadow private ResourcePackSource packSource;

    @Unique
    private static final TweaksMod mod = TweaksMod.get();

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(MinecraftClient client, Path downloadsDirectory, RunArgs.Network runArgs, CallbackInfo ci) {
        Collection<PackEntry> entries = mod.config().getPacks();

        if (entries.isEmpty())
            return;

        this.manager.acceptAll();
        for (PackEntry entry : entries) {
            this.manager.addResourcePack(entry.uuid(), entry.path());
        }

        mod.observer().setPreloaded(true);
    }

    @Inject(method = "reload", at = @At("HEAD"), cancellable = true)
    public void reload(ReloadScheduler.ReloadContext context, CallbackInfo ci) {
        AbstractPackStateObserver observer = mod.observer();
        boolean result = observer.shouldReload();

        if (!result) {
            context.onSuccess();
            ci.cancel();
        }
    }

    @Inject(method = "clear", at = @At("HEAD"), cancellable = true)
    public void clear(CallbackInfo ci) {
        if (mod.observer().shouldClear())
            ci.cancel();
    }

    @Inject(method = "onReloadSuccess", at = @At("HEAD"))
    public void onReloadSuccess(CallbackInfo ci) {
        mod.observer().onFinish();
    }

    @Redirect(method = "toProfiles", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourcePackProfile;of(Ljava/lang/String;Lnet/minecraft/text/Text;ZLnet/minecraft/resource/ResourcePackProfile$PackFactory;Lnet/minecraft/resource/ResourcePackProfile$Metadata;Lnet/minecraft/resource/ResourcePackProfile$InsertionPosition;ZLnet/minecraft/resource/ResourcePackSource;)Lnet/minecraft/resource/ResourcePackProfile;"))
    public ResourcePackProfile createProfile(String name, Text displayName, boolean alwaysEnabled, ResourcePackProfile.PackFactory packFactory, ResourcePackProfile.Metadata metadata, ResourcePackProfile.InsertionPosition position, boolean pinned, ResourcePackSource source) {
        return ResourcePackProfile.of(name, displayName, false, packFactory, metadata, ResourcePackProfile.InsertionPosition.TOP, false, packSource);
    }
}
