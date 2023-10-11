package dev.drtheo.rptweaks.mixin;

import com.google.common.collect.ImmutableMap;
import dev.drtheo.rptweaks.RPTweaks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.*;
import net.minecraft.text.Text;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Mixin(ResourcePackManager.class)
public class ResourcePackManagerMixin {

    @Unique
    private static final Text SERVER_NAME_TEXT = Text.translatable("resourcePack.server.name");

    @Unique
    private static final File serverPacksRoot = new File(MinecraftClient.getInstance().runDirectory, "server-resource-packs");

    @Redirect(method = "providePackProfiles", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableMap;copyOf(Ljava/util/Map;)Lcom/google/common/collect/ImmutableMap;"))
    public ImmutableMap<String, ResourcePackProfile> providePackProfiles(Map<String, ResourcePackProfile> kvMap) {
        List<File> list = new ArrayList<>(FileUtils.listFiles(serverPacksRoot, TrueFileFilter.TRUE, null));

        // this will allow sorting
        Map<String, ResourcePackProfile> map = new LinkedHashMap<>();

        for (File file : list) {
            RPTweaks.LOGGER.warn(list.toString());
            ResourcePackProfile.PackFactory packFactory = new ZipResourcePack.ZipBackedFactory(file, false);

            ResourcePackProfile profile = ResourcePackProfile.create(
                    file.getName(), SERVER_NAME_TEXT, false, packFactory,
                    ResourceType.CLIENT_RESOURCES, ResourcePackProfile.InsertionPosition.TOP,
                    ResourcePackSource.BUILTIN
            );

            map.put(file.getName(), profile);
        }

        map.putAll(kvMap);
        return ImmutableMap.copyOf(map);
    }
}
