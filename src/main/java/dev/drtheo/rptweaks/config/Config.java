package dev.drtheo.rptweaks.config;

import dev.drtheo.rptweaks.RPTweaks;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Config {

    private static final Config config = new Config();

    private static final String comment = """
If true, makes Minecraft load the latest pack before launch.
preload: (true|false)
""";

    private final Path file = FabricLoader.getInstance().getConfigDir()
            .resolve("rptweaks-v2.properties");

    private final Properties properties = new Properties();

    private List<PackEntry> latest = new ArrayList<>();
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
                this.latest.clear();
                String[] files = latest.split(";");
                RPTweaks.LOGGER.info("FILES: " + Arrays.toString(files));

                for (String file : files) {
                    PackEntry entry = PackEntry.fromString(file);
                    RPTweaks.LOGGER.info("DECODED ENTRY: " + entry);

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
            if (this.latest != null) {
                List<String> paths = new ArrayList<>();

                for (PackEntry entry : this.latest) {
                    paths.add(entry.asString());
                }

                this.properties.setProperty("latest", String.join(";", paths));
            }

            this.properties.setProperty("preload", String.valueOf(this.preload));
            this.properties.store(stream, comment);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<PackEntry> getLatest() {
        return latest;
    }

    public void setLatest(List<PackEntry> latest) {
        this.latest = latest;
    }

    public boolean isLatest(Path path) {
        if (this.latest.isEmpty())
            return false;

        path = path.toAbsolutePath();
        for (PackEntry entry : this.latest) {
            if (entry.path().toAbsolutePath().equals(path))
                return true;
        }

        return false;
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
