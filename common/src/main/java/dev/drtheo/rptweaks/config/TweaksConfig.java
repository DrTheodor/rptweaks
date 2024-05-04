package dev.drtheo.rptweaks.config;

import dev.drtheo.rptweaks.TweaksMod;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public abstract class TweaksConfig {

    private static final String comment = """
If true, makes Minecraft load the latest pack before launch.
preload: (true|false)
""";

    protected final TweaksMod mod;
    private final Path file;

    private final Properties properties = new Properties();

    private final List<PackEntry> latest = new ArrayList<>();
    private boolean preload = true;

    public TweaksConfig(String name, TweaksMod mod) {
        this.file = mod.getConfigDir().resolve(name + ".properties");
        this.mod = mod;

        if (!Files.exists(this.file))
            this.save();

        this.load();
    }

    public void load() {
        try (InputStream stream = Files.newInputStream(this.file)) {
            this.properties.load(stream);

            String latest = this.properties.getProperty("latest");

            if (latest != null) {
                this.latest.clear();
                String[] files = latest.split(";");
                this.mod.logger().debug("FILES: " + Arrays.toString(files));

                for (String file : files) {
                    PackEntry entry = this.entry(file);
                    this.mod.logger().debug("DESERIALIZED ENTRY: " + entry);

                    if (entry != null)
                        this.latest.add(entry);
                }
            }

            this.preload = Boolean.parseBoolean(
                    this.properties.getProperty("preload", "none")
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void save() {
        try (OutputStream stream = Files.newOutputStream(this.file)) {
            List<String> paths = new ArrayList<>();

            for (PackEntry entry : this.latest) {
                paths.add(this.entry(entry));
            }

            this.properties.setProperty("latest", String.join(";", paths));

            this.properties.setProperty("preload", String.valueOf(this.preload));
            this.properties.store(stream, comment);
        } catch (IOException e) {
            throw new RuntimeException(e);
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

    public abstract PackEntry entry(String entry);

    public abstract String entry(PackEntry entry);
}
