package dev.drtheo.rptweaks.mixin;

import dev.drtheo.rptweaks.TweaksMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.MinecraftClient")
public class DisconnectHook {

    @Inject(method = "clearDownloadedResourcePacks", at = @At("HEAD"))
    public void onDisconnected(CallbackInfo ci) {
        TweaksMod.get().handleDisconnect();
    }
}
