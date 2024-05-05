package dev.drtheo.rptweaks.config;

import dev.drtheo.rptweaks.TweaksMod;
import dev.drtheo.rptweaks.config.entry.PackEntry;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiFunction;

public class TweaksConfig {

    private static final String comment = """
If true, makes Minecraft load the latest pack before launch.
preload: (true|false)
""";

    protected final TweaksMod mod;
    private final Path file;

    private final Properties properties = new Properties();

    private final List<PackEntry> latest = new ArrayList<>();

    private String lastServer = "";
    private boolean preload = true;

    private int maxPacks = 30;

    private final BiFunction<TweaksMod, String, PackEntry> entryCreator;

    public TweaksConfig(String name, TweaksMod mod, BiFunction<TweaksMod, String, PackEntry> entryCreator) {
        this.mod = mod;
        this.file = mod.getConfigDir().resolve(name + ".properties");
        this.entryCreator = entryCreator;

        if (!Files.exists(this.file))
            this.save();

        this.load();
    }

    public PackEntry entry(String entry) {
        return this.entryCreator.apply(this.mod, entry);
    }

    private void load() {
        try (InputStream stream = Files.newInputStream(this.file)) {
            this.properties.load(stream);
            String latest = this.properties.getProperty("latest");

            if (latest != null) {
                this.latest.clear();

                if (!latest.isEmpty())
                    this.createEntries(latest);
            }

            this.preload = Boolean.parseBoolean(
                    this.properties.getProperty("preload", "")
            );

            this.maxPacks = Integer.parseInt(
                    this.properties.getProperty("max-packs", "30")
            );

            this.lastServer = this.properties.getProperty("last-server", "");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createEntries(String latest) {
        String[] files = latest.split(";");
        this.mod.logger().debug("Files: " + Arrays.toString(files));

        for (String file : files) {
            PackEntry entry = this.entry(file);
            this.mod.logger().debug("Deserialized entry: " + entry);

            if (entry != null)
                this.latest.add(entry);
        }
    }

    public void save() {
        try {
            Files.createDirectories(this.file.getParent());
            try (OutputStream stream = Files.newOutputStream(this.file)) {
                List<String> paths = new ArrayList<>();

                for (PackEntry entry : this.latest) {
                    paths.add(entry.toString(this.mod));
                }

                this.properties.setProperty("latest", String.join(";", paths));
                this.properties.setProperty("last-server", this.lastServer);

                this.properties.setProperty("preload", String.valueOf(this.preload));
                this.properties.setProperty("max-packs", String.valueOf(this.maxPacks));
                this.properties.store(stream, comment);
            }
        } catch (Exception e) {
            this.mod.logger().error("Couldn't save the config: ", e);
        }
    }

    public List<PackEntry> getPacks() {
        return latest;
    }

    public void setLatest(Collection<? extends PackEntry> latest) {
        this.latest.clear();
        this.latest.addAll(latest);
    }

    public boolean shouldPreload() {
        return preload;
    }

    public void shouldPreload(boolean preload) {
        this.preload = preload;
    }

    public int maxPacks() {
        return maxPacks;
    }

    public String getLastServer() {
        return lastServer;
    }

    public void setLastServer(String lastServer) {
        this.lastServer = lastServer;
    }
}
