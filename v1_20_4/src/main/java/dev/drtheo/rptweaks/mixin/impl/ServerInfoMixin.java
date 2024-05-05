package dev.drtheo.rptweaks.mixin.impl;

import dev.drtheo.rptweaks.mixininterface.ServerInfoLike;
import net.minecraft.client.multiplayer.ServerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerData.class)
public class ServerInfoMixin implements ServerInfoLike {

    @Shadow public String ip;

    @Override
    public String rpt$address() {
        return this.ip;
    }
}
