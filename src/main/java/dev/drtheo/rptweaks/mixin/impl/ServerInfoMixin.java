package dev.drtheo.rptweaks.mixin.impl;

import dev.drtheo.rptweaks.mixininterface.ServerInfoLike;
import net.minecraft.client.network.ServerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerInfo.class)
public class ServerInfoMixin implements ServerInfoLike {

    @Shadow public String address;

    @Override
    public String rptweaks$address() {
        return this.address;
    }
}
