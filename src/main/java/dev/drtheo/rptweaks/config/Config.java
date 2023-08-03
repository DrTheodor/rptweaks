package dev.drtheo.rptweaks.config;

import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class Config {

    private static final Config config = new Config();

    private static final String comment = """
If true, makes Minecraft load the latest pack before launch.
preload: (true|false)
""";

    private final Path serverResourcePacksFolder = FabricLoader.getInstance()
            .getGameDir().resolve("server-resource-packs");

    private final Path file = FabricLoader.getInstance().getConfigDir()
            .resolve("rptweaks.properties");

    private final Properties properties = new Properties();

    private File latest = null;
    private boolean preload = true;

    public Config() {
        if (!Files.exists(this.file)) {
            this.save();
        }

        this.load();
    }

    public void load() {
        try (InputStream stream = Files.newInputStream(this.file)) {
            this.properties.load(stream);

            String latest = this.properties.getProperty("latest");

            if (latest != null) {
                this.latest = serverResourcePacksFolder.resolve(latest).toFile();

                if (!this.latest.exists()) {
                    this.latest = null;
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
            if (this.latest != null) {
                this.properties.setProperty("latest", this.latest.getName());
            }

            this.properties.setProperty("preload", String.valueOf(this.preload));
            this.properties.store(stream, comment);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public File getLatest() {
        return latest;
    }

    public void setLatest(File latest) {
        this.latest = latest;
    }

    public boolean isLatest(File file) {
        return this.latest != null && this.latest.equals(file);
    }

    public boolean shouldPreload() {
        return preload;
    }

    public void shouldPreload(boolean preload) {
        this.preload = preload;
    }

    public static Config getConfig() {
        return config;
    }
}
