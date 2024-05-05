package dev.drtheo.rptweaks.mixin;

import dev.drtheo.rptweaks.TweaksMod;
import dev.drtheo.rptweaks.config.entry.PackEntry;
import dev.drtheo.rptweaks.resource.AbstractPackStateObserver;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import net.minecraft.client.resources.server.PackReloadConfig;
import net.minecraft.client.resources.server.DownloadedPackSource;
import net.minecraft.client.resources.server.ServerPackManager;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.network.chat.Component;
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

@Mixin(DownloadedPackSource.class)
public abstract class ServerResourcePackLoaderMixin {

    @Shadow @Final ServerPackManager manager;

    @Unique private static final TweaksMod mod = TweaksMod.get();

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(Minecraft client, Path downloadsDirectory, GameConfig.UserData runArgs, CallbackInfo ci) {
        if (!mod.config().shouldPreload())
            return;

        Collection<PackEntry> entries = mod.config().getPacks();

        if (entries.isEmpty())
            return;

        this.manager.allowServerPacks();
        for (PackEntry entry : entries) {
            this.manager.pushLocalPack(entry.uuid(), entry.path());
        }

        mod.observer().setInitializing(true);
        mod.observer().setPreloaded(true);
    }

    @Inject(method = "startReload", at = @At("HEAD"), cancellable = true)
    public void reload(PackReloadConfig.Callbacks context, CallbackInfo ci) {
        AbstractPackStateObserver observer = mod.observer();
        boolean result = observer.shouldReload();

        if (!result) {
            context.onSuccess();
            ci.cancel();
        }
    }

    @Inject(method = "cleanupAfterDisconnect", at = @At("HEAD"), cancellable = true)
    public void clear(CallbackInfo ci) {
        if (mod.observer().shouldClear())
            ci.cancel();
    }

    @Inject(method = "onReloadSuccess", at = @At("HEAD"))
    public void onReloadSuccess(CallbackInfo ci) {
        mod.observer().onFinish();
    }

    @Redirect(method = "loadRequestedPacks", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/repository/Pack;create(Ljava/lang/String;Lnet/minecraft/network/chat/Component;ZLnet/minecraft/server/packs/repository/Pack$ResourcesSupplier;Lnet/minecraft/server/packs/repository/Pack$Info;Lnet/minecraft/server/packs/repository/Pack$Position;ZLnet/minecraft/server/packs/repository/PackSource;)Lnet/minecraft/server/packs/repository/Pack;"))
    public Pack createProfile(String id, Component title, boolean required, Pack.ResourcesSupplier resources, Pack.Info info, Pack.Position defaultPosition, boolean fixedPosition, PackSource packSource) {
        return Pack.create(id, title, required, resources, info, defaultPosition, false, packSource);
    }
}
