package dev.drtheo.rptweaks.mixin.impl;

import dev.drtheo.rptweaks.mixininterface.ClientLike;
import dev.drtheo.rptweaks.mixininterface.ServerInfoLike;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin implements ClientLike {

    @Shadow @Nullable public abstract ServerInfo getCurrentServerEntry();

    @Override
    public ServerInfoLike rptweaks$serverEntry() {
        return (ServerInfoLike) this.getCurrentServerEntry();
    }
}
