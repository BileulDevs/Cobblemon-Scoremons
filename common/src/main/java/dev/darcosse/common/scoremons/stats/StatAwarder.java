package dev.darcosse.common.scoremons.stats;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import dev.darcosse.common.scoremons.config.ConfigManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;

import java.util.UUID;

/**
 * Single entry point through which every handler changes a ScoreMons statistic.
 * <p>
 * Centralising the writes keeps the per-stat kill switch ({@code disabledStats} in the config)
 * in one place and guarantees scoreboards are always updated alongside the stat itself.
 *
 * @author Darcosse
 * @since 2026
 */
public final class StatAwarder {

    /**
     * Prevents instantiation: this class only exposes static helpers.
     */
    private StatAwarder() {
    }

    /**
     * Adds one to a stat.
     *
     * @param player the player to credit; ignored when null so callers can pass optional owners
     * @param stat   the ScoreMons stat identifier
     */
    public static void award(ServerPlayer player, ResourceLocation stat) {
        award(player, stat, 1);
    }

    /**
     * Adds an arbitrary positive amount to a stat.
     *
     * @param player the player to credit; ignored when null
     * @param stat   the ScoreMons stat identifier
     * @param amount the increment; non-positive values are ignored
     */
    public static void award(ServerPlayer player, ResourceLocation stat, int amount) {
        if (player == null || amount <= 0 || ConfigManager.isStatDisabled(stat)) return;
        // ServerPlayer.awardStat also pushes the delta to every scoreboard objective using this stat.
        player.awardStat(Stats.CUSTOM.get(stat), amount);
    }

    /**
     * Adds one to a stat for every human player controlling a battle actor.
     *
     * @param actor the battle actor; wild and NPC actors expose no player UUIDs and are skipped
     * @param stat  the ScoreMons stat identifier
     */
    public static void awardActor(BattleActor actor, ResourceLocation stat) {
        MinecraftServer server = server();
        if (server == null) return;
        for (UUID uuid : actor.getPlayerUUIDs()) {
            award(server.getPlayerList().getPlayer(uuid), stat);
        }
    }

    /**
     * Forces a stat to an absolute value, for stats that mirror state rather than count events.
     * <p>
     * Only the difference is applied, so the cost is constant whatever the value; the 3.x
     * implementation re-awarded the stat once per unit and scaled with the Pokédex size.
     *
     * @param player the player whose stat is rewritten
     * @param stat   the ScoreMons stat identifier
     * @param value  the target value
     */
    public static void set(ServerPlayer player, ResourceLocation stat, int value) {
        if (player == null || ConfigManager.isStatDisabled(stat)) return;
        Stat<ResourceLocation> entry = Stats.CUSTOM.get(stat);
        int current = player.getStats().getValue(entry);
        if (value > current) {
            player.awardStat(entry, value - current);
        } else if (value < current) {
            // No negative award exists: reset (which also resets scoreboards) then re-add.
            player.resetStat(entry);
            if (value > 0) player.awardStat(entry, value);
        }
    }

    /**
     * Resolves an online player from a UUID.
     *
     * @param uuid the player UUID
     * @return the online player, or null when offline or when no server is running
     */
    public static ServerPlayer player(UUID uuid) {
        MinecraftServer server = server();
        return server == null || uuid == null ? null : server.getPlayerList().getPlayer(uuid);
    }

    /**
     * Returns the running server through Cobblemon, which works the same on both loaders.
     *
     * @return the server, or null on a client without integrated server
     */
    public static MinecraftServer server() {
        return Cobblemon.INSTANCE.getImplementation().server();
    }
}
