package dev.darcosse.common.scoremons.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import dev.darcosse.common.scoremons.ScoremonsCommon;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Loads, caches and persists the ScoreMons JSON configuration.
 * <p>
 * The configuration directory is injected by the platform entry point, which is the only
 * loader-specific part of the config system.
 *
 * @author Darcosse
 * @since 2026
 */
public final class ConfigManager {

    /** File name inside the loader config directory. Changing it orphans every existing user config. */
    private static final String CONFIG_FILE = "scoremons_config.json";

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static Path configDir;
    private static ScoremonsConfig config = new ScoremonsConfig();

    /**
     * Disabled stat paths, flattened into a set because {@link #isStatDisabled} runs on every award.
     */
    private static Set<String> disabledStats = Set.of();

    /**
     * Prevents instantiation: configuration state is global to the mod.
     */
    private ConfigManager() {
    }

    /**
     * Remembers the loader configuration directory and performs the first load.
     *
     * @param dir directory supplied by FabricLoader or FMLPaths
     */
    public static void init(Path dir) {
        configDir = dir;
        loadConfig();
    }

    /**
     * Reads the configuration from disk, then writes it back so new options appear in old files.
     * <p>
     * A missing, empty or malformed file falls back to defaults instead of crashing: in 3.x an
     * empty file made Gson return null and the first broadcast threw a NullPointerException.
     */
    public static void loadConfig() {
        Path file = getConfigFile();
        ScoremonsConfig loaded = null;

        if (Files.exists(file)) {
            try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                loaded = GSON.fromJson(reader, ScoremonsConfig.class);
            } catch (IOException | JsonParseException e) {
                ScoremonsCommon.LOGGER.error("Invalid {}, using defaults", CONFIG_FILE, e);
            }
        }

        config = loaded != null ? loaded : new ScoremonsConfig();
        if (config.disabledStats == null) config.disabledStats = new ArrayList<>();
        disabledStats = config.disabledStats.stream().map(String::trim).collect(Collectors.toUnmodifiableSet());
        saveConfig();
    }

    /**
     * Writes the in-memory configuration back to disk.
     */
    public static void saveConfig() {
        try (Writer writer = Files.newBufferedWriter(getConfigFile(), StandardCharsets.UTF_8)) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            ScoremonsCommon.LOGGER.error("Failed to save {}", CONFIG_FILE, e);
        }
    }

    /**
     * Returns the active configuration.
     *
     * @return the cached configuration, never null
     */
    public static ScoremonsConfig getConfig() {
        return config;
    }

    /**
     * Tells whether shiny captures are announced to the whole server.
     *
     * @return the configured flag
     */
    public static boolean shouldBroadcastShinyCaught() {
        return config.broadcastShinyCaught;
    }

    /**
     * Tells whether legendary, mythical and Ultra Beast captures are announced to the whole server.
     *
     * @return the configured flag
     */
    public static boolean shouldBroadcastLegendaryCaught() {
        return config.broadcastLegendaryCaught;
    }

    /**
     * Tells whether shiny fossil revivals are announced to the whole server.
     *
     * @return the configured flag
     */
    public static boolean shouldBroadcastShinyFossilRevived() {
        return config.broadcastShinyFossilRevived;
    }

    /**
     * Tells whether a stat was switched off in the config.
     *
     * @param stat the stat identifier; only its path is compared, the namespace is always ScoreMons
     * @return true when the stat must not be awarded
     */
    public static boolean isStatDisabled(ResourceLocation stat) {
        return !disabledStats.isEmpty() && disabledStats.contains(stat.getPath());
    }

    /**
     * Resolves the configuration file against the injected directory.
     *
     * @return the configuration file path
     */
    private static Path getConfigFile() {
        // Fails loudly rather than writing into the working directory, which would hide a missing init() call.
        if (configDir == null) {
            throw new IllegalStateException("ConfigManager used before ScoremonsCommon.init()");
        }
        return configDir.resolve(CONFIG_FILE);
    }
}
