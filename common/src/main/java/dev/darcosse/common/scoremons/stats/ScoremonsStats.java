package dev.darcosse.common.scoremons.stats;

import dev.darcosse.common.scoremons.ScoremonsCommon;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;

import java.util.List;

/**
 * Catalogue of the custom statistics added by ScoreMons.
 * <p>
 * Holds the identifiers only; registering them in {@code CUSTOM_STAT} is done by each platform,
 * because Fabric allows direct registry writes during init while NeoForge requires its
 * registration event.
 * <p>
 * The paths are part of the save format and of every scoreboard objective created by players
 * ({@code minecraft.custom:scoremons.<path>}): renaming one resets that stat for everybody.
 * Stats Cobblemon already tracks identically ({@code cobblemon:captured}, {@code cobblemon:evolved}...)
 * are deliberately absent.
 *
 * @author Darcosse
 * @since 2026
 */
public final class ScoremonsStats {

    // --- Battles -------------------------------------------------------------------------------
    // Cobblemon only counts wins against wild Pokémon and never increments its own battles_lost.

    /** PvP battles won. Path kept from 3.x so existing objectives keep working. */
    public static final ResourceLocation BATTLE_WON = id("battle_won");
    /** PvP battles lost. Path kept from 3.x so existing objectives keep working. */
    public static final ResourceLocation BATTLE_LOSE = id("battle_lose");
    public static final ResourceLocation NPC_BATTLES_WON = id("npc_battles_won");
    public static final ResourceLocation NPC_BATTLES_LOST = id("npc_battles_lost");
    public static final ResourceLocation WILD_BATTLES_LOST = id("wild_battles_lost");
    /** Opposing Pokémon fainted in a battle the player took part in. */
    public static final ResourceLocation POKEMON_KNOCKED_OUT = id("pokemon_knocked_out");
    /** The player's own Pokémon fainted in battle. */
    public static final ResourceLocation POKEMON_FAINTED = id("pokemon_fainted");
    public static final ResourceLocation MEGA_EVOLUTIONS = id("mega_evolutions");
    public static final ResourceLocation TERASTALLIZATIONS = id("terastallizations");
    public static final ResourceLocation Z_MOVES_USED = id("z_moves_used");
    public static final ResourceLocation FORME_CHANGES = id("forme_changes");

    // --- Captures ------------------------------------------------------------------------------

    public static final ResourceLocation LEGENDARY_CAUGHT = id("legendary_caught");
    public static final ResourceLocation MYTHICAL_CAUGHT = id("mythical_caught");
    public static final ResourceLocation ULTRA_BEASTS_CAUGHT = id("ultra_beasts_caught");
    public static final ResourceLocation CRITICAL_CAPTURES = id("critical_captures");
    /** Poké Balls that hit a Pokémon and started a capture attempt. Path kept from 3.x. */
    public static final ResourceLocation POKEBALL_THROWN = id("pokeball_thrown");
    /** Pokémon hooked with a Poké Rod (Cobblemon's reel_ins also counts empty reels). Path kept from 3.x. */
    public static final ResourceLocation POKEMON_REEL = id("pokemon_reel");

    // --- Pokédex -------------------------------------------------------------------------------

    /** National Pokédex species owned. Path kept from 3.x; Cobblemon's dex_entries over-counts. */
    public static final ResourceLocation POKEMON_REGISTERED = id("pokemon_registered");
    /** National Pokédex species seen or owned. */
    public static final ResourceLocation POKEDEX_SEEN = id("pokedex_seen");
    public static final ResourceLocation POKEMON_SCANNED = id("pokemon_scanned");

    // --- Training and care ---------------------------------------------------------------------

    /** Sum of levels gained; Cobblemon's level_up counts level-up events, not levels. */
    public static final ResourceLocation LEVELS_GAINED = id("levels_gained");
    public static final ResourceLocation POKEMON_MAX_LEVEL = id("pokemon_max_level");
    public static final ResourceLocation POKEMON_MAX_FRIENDSHIP = id("pokemon_max_friendship");
    public static final ResourceLocation RARE_CANDIES_USED = id("rare_candies_used");
    public static final ResourceLocation EXP_CANDIES_USED = id("exp_candies_used");
    public static final ResourceLocation IVS_HYPER_TRAINED = id("ivs_hyper_trained");
    public static final ResourceLocation POKEMON_NICKNAMED = id("pokemon_nicknamed");
    public static final ResourceLocation MARKS_OBTAINED = id("marks_obtained");
    public static final ResourceLocation HEALING_MACHINE_USES = id("healing_machine_uses");
    public static final ResourceLocation HEALING_ITEMS_USED = id("healing_items_used");

    // --- Gathering -----------------------------------------------------------------------------

    public static final ResourceLocation APRICORNS_HARVESTED = id("apricorns_harvested");
    public static final ResourceLocation BERRIES_HARVESTED = id("berries_harvested");

    /**
     * Every stat in registration order, which is also the order of the statistics screen.
     * Platforms iterate this list, so a stat missing from it is never registered and crashes
     * the first time it is awarded.
     */
    public static final List<ResourceLocation> ALL = List.of(
            BATTLE_WON,
            BATTLE_LOSE,
            NPC_BATTLES_WON,
            NPC_BATTLES_LOST,
            WILD_BATTLES_LOST,
            POKEMON_KNOCKED_OUT,
            POKEMON_FAINTED,
            MEGA_EVOLUTIONS,
            TERASTALLIZATIONS,
            Z_MOVES_USED,
            FORME_CHANGES,
            LEGENDARY_CAUGHT,
            MYTHICAL_CAUGHT,
            ULTRA_BEASTS_CAUGHT,
            CRITICAL_CAPTURES,
            POKEBALL_THROWN,
            POKEMON_REEL,
            POKEMON_REGISTERED,
            POKEDEX_SEEN,
            POKEMON_SCANNED,
            LEVELS_GAINED,
            POKEMON_MAX_LEVEL,
            POKEMON_MAX_FRIENDSHIP,
            RARE_CANDIES_USED,
            EXP_CANDIES_USED,
            IVS_HYPER_TRAINED,
            POKEMON_NICKNAMED,
            MARKS_OBTAINED,
            HEALING_MACHINE_USES,
            HEALING_ITEMS_USED,
            APRICORNS_HARVESTED,
            BERRIES_HARVESTED
    );

    /**
     * Prevents instantiation: this class is a constant holder.
     */
    private ScoremonsStats() {
    }

    /**
     * Creates the {@code Stat} objects for every registered identifier.
     * <p>
     * Must run after the identifiers are in {@code CUSTOM_STAT}: the vanilla statistics screen only
     * lists stats that already exist in {@code Stats.CUSTOM}, so skipping this would hide them until
     * first awarded.
     */
    public static void createStatEntries() {
        for (ResourceLocation stat : ALL) {
            Stats.CUSTOM.get(stat);
        }
    }

    /**
     * Builds an identifier in the ScoreMons stat namespace (not the mod id, see
     * {@link ScoremonsCommon#STAT_NAMESPACE}).
     *
     * @param path stat path
     * @return the namespaced identifier
     */
    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(ScoremonsCommon.STAT_NAMESPACE, path);
    }
}
