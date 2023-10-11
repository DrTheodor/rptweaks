package dev.drtheo.rptweaks.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.ServerResourcePackProvider;
import net.minecraft.resource.*;
import net.minecraft.text.Text;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class PackManagerUtil {

    private static final MinecraftClient client = MinecraftClient.getInstance();

    private static final ServerResourcePackProvider serverProvider = client.getServerResourcePackProvider();
    private static final ResourcePackManager manager = client.getResourcePackManager();

    public static void enable(String profile) {
        List<String> list = buildPacks();
        if (!list.contains(profile))
            list.add(profile);

        manager.setEnabledProfiles(list);
    }

    public static void disable(String profile) {
        List<String> list = buildPacks();
        list.remove(profile);

        manager.setEnabledProfiles(list);
    }

    public static void clearTempPack() {
        ((Reloadable) serverProvider).rptweaks$clear();
    }

    private static List<String> buildPacks() {
        return manager.getEnabledProfiles().stream().map(ResourcePackProfile::getName).collect(Collectors.toList());
    }
}
