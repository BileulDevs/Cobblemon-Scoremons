package dev.darcosse.common.scoremons;

import dev.darcosse.common.scoremons.broadcast.CaptureBroadcaster;
import dev.darcosse.common.scoremons.config.ConfigManager;
import dev.darcosse.common.scoremons.stats.handler.BattleStatHandler;
import dev.darcosse.common.scoremons.stats.handler.CaptureStatHandler;
import dev.darcosse.common.scoremons.stats.handler.CareStatHandler;
import dev.darcosse.common.scoremons.stats.handler.GatheringStatHandler;
import dev.darcosse.common.scoremons.stats.handler.PokedexStatHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

/**
 * Loader-agnostic bootstrap of ScoreMons.
 * <p>
 * Owns everything that does not depend on Fabric or NeoForge: configuration loading and the
 * Cobblemon event subscriptions that feed the custom statistics. Platform entry points call
 * {@link #init(Path)} once, after or alongside their own registry work.
 *
 * @author Darcosse
 * @since 2026
 */
public final class ScoremonsCommon {

    /** Mod identifier declared to both loaders (fabric.mod.json, neoforge.mods.toml). */
    public static final String MOD_ID = "cobblemon_scoremons";

    /**
     * Namespace of the custom stats, deliberately kept from 3.x instead of following {@link #MOD_ID}.
     * Stat ids are stored in every player's stats file and in every scoreboard objective
     * ({@code minecraft.custom:scoremons.<path>}); changing this namespace resets all of them.
     * The {@code stat.scoremons.*} translation keys derive from it too.
     */
    public static final String STAT_NAMESPACE = "scoremons";

    /** Shared logger, so both platforms report under the same name. */
    public static final Logger LOGGER = LoggerFactory.getLogger("Scoremons");

    /**
     * Prevents instantiation: this class only groups the bootstrap logic.
     */
    private ScoremonsCommon() {
    }

    /**
     * Loads the configuration and hooks every handler onto Cobblemon's event bus.
     *
     * @param configDir loader-provided configuration directory; injected so that common code never
     *                  touches FabricLoader or FMLPaths
     */
    public static void init(Path configDir) {
        LOGGER.info("Initializing...");

        ConfigManager.init(configDir);

        BattleStatHandler.register();
        CaptureStatHandler.register();
        PokedexStatHandler.register();
        CareStatHandler.register();
        GatheringStatHandler.register();
        CaptureBroadcaster.register();
    }
}
