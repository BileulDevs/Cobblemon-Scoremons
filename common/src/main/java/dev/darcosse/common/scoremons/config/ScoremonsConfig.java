package dev.darcosse.common.scoremons.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Plain data holder deserialized by Gson from {@code config/scoremons_config.json}.
 * <p>
 * Field names are the JSON keys: renaming one silently resets that option to its default
 * for every existing installation. Fields missing from an older file keep the defaults below
 * and are written back on load.
 *
 * @author Darcosse
 * @since 2026
 */
public class ScoremonsConfig {

    /** Whether a server-wide chat message is sent when a player catches a shiny Pokémon. */
    public boolean broadcastShinyCaught = true;

    /** Whether a server-wide chat message is sent when a player catches a legendary, mythical or Ultra Beast. */
    public boolean broadcastLegendaryCaught = true;

    /** Whether a server-wide chat message is sent when a player revives a shiny fossil. */
    public boolean broadcastShinyFossilRevived = true;

    /**
     * Stat paths (e.g. {@code "forme_changes"}) that are never awarded. The stats stay registered
     * so existing scoreboard objectives keep loading; they simply stop changing.
     */
    public List<String> disabledStats = new ArrayList<>();
}
