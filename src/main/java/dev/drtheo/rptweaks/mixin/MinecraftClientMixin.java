package dev.drtheo.rptweaks.mixin;

import dev.drtheo.rptweaks.RPTweaks;
import dev.drtheo.rptweaks.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.resource.ClientBuiltinResourcePackProvider;
import net.minecraft.resource.*;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.io.IOException;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Shadow @Final private ClientBuiltinResourcePackProvider builtinPackProvider;

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/WindowProvider;createWindow(Lnet/minecraft/client/WindowSettings;Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/client/util/Window;", shift = At.Shift.AFTER))
    public void init(RunArgs args, CallbackInfo ci) {
        Config config = Config.getConfig();
        if (config.shouldPreload() && config.getLatest() != null) {
            this.loadLatestPack(config.getLatest());
        }
    }

    @Unique
    private void loadLatestPack(File packZip) {
        try (ZipResourcePack pack = new ZipResourcePack(packZip)) {
            PackResourceMetadata metadata;

            try {
                metadata = pack.parseMetadata(PackResourceMetadata.READER);
            } catch (Throwable var8) {
                try {
                    pack.close();
                } catch (Throwable var7) {
                    var8.addSuppressed(var7);
                }

                throw var8;
            }

            RPTweaks.LOGGER.info("Applying server pack {}", packZip);
            ((ClientBuiltinResourcePackProviderAccessor) this.builtinPackProvider).setServerContainer(
                    new ResourcePackProfile("server", true,
                            () -> pack, Text.translatable("resourcePack.server.name"),
                            metadata.getDescription(), ResourcePackCompatibility.from(metadata, ResourceType.CLIENT_RESOURCES),
                            ResourcePackProfile.InsertionPosition.TOP, true, ResourcePackSource.PACK_SOURCE_SERVER
                    )
            );
        } catch (IOException var9) {
            RPTweaks.LOGGER.warn(String.format("Invalid resourcepack at %s", packZip));
        }
    }
}
