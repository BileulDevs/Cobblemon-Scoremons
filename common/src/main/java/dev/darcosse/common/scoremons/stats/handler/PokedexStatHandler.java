package dev.darcosse.common.scoremons.stats.handler;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.pokedex.scanning.PokemonScannedEvent;
import com.cobblemon.mod.common.api.events.pokemon.PokedexDataChangedEvent;
import com.cobblemon.mod.common.api.pokedex.AbstractPokedexManager;
import com.cobblemon.mod.common.api.pokedex.CaughtCount;
import com.cobblemon.mod.common.api.pokedex.PokedexEntryProgress;
import com.cobblemon.mod.common.api.pokedex.SeenCount;
import dev.darcosse.common.scoremons.stats.StatAwarder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import static dev.darcosse.common.scoremons.stats.ScoremonsStats.*;

/**
 * Pokédex statistics: species owned, species seen and scans.
 * <p>
 * Owned and seen are state stats: they mirror the Pokédex instead of counting events, so they
 * are rewritten on each relevant change and once when the player joins. Server-side only.
 *
 * @author Darcosse
 * @since 2026
 */
public final class PokedexStatHandler {

    /**
     * Cobblemon Pokédex the counts are computed against.
     * Pointing it at a regional dex would make both stats count only that region.
     */
    private static final ResourceLocation NATIONAL_DEX = ResourceLocation.fromNamespaceAndPath("cobblemon", "national");

    /**
     * Prevents instantiation: this class only groups static listeners.
     */
    private PokedexStatHandler() {
    }

    /**
     * Subscribes every Pokédex listener to Cobblemon's event bus.
     */
    public static void register() {
        CobblemonEvents.POKEDEX_DATA_CHANGED_POST.subscribe(Priority.LOWEST, PokedexStatHandler::onDexChanged);
        // Catches up players whose Pokédex predates the mod or changed while it was disabled.
        CobblemonEvents.DATA_SYNCHRONIZED.subscribe(Priority.LOWEST, PokedexStatHandler::onJoin);
        CobblemonEvents.POKEMON_SCANNED.subscribe(Priority.LOWEST,
                (PokemonScannedEvent e) -> StatAwarder.award(e.getPlayer(), POKEMON_SCANNED));
    }

    /**
     * Refreshes the Pokédex stats after a change.
     * <p>
     * Each count walks the whole national dex (~1000 lookups), and this event also fires for
     * minor record updates (new gender, shiny state...), so the owned count is only recomputed
     * when the change can actually raise it.
     *
     * @param event the post-change event
     */
    private static void onDexChanged(PokedexDataChangedEvent.Post event) {
        ServerPlayer player = StatAwarder.player(event.getPlayerUUID());
        if (player == null) return;

        AbstractPokedexManager dex = event.getPokedexManager();
        StatAwarder.set(player, POKEDEX_SEEN, dex.getDexCalculatedValue(NATIONAL_DEX, SeenCount.INSTANCE));
        if (event.getKnowledge() == PokedexEntryProgress.OWNED) {
            StatAwarder.set(player, POKEMON_REGISTERED, dex.getDexCalculatedValue(NATIONAL_DEX, CaughtCount.INSTANCE));
        }
    }

    /**
     * Rewrites both Pokédex stats when Cobblemon finishes syncing a joining player.
     *
     * @param player the player who just joined
     */
    private static void onJoin(ServerPlayer player) {
        AbstractPokedexManager dex = Cobblemon.INSTANCE.getPlayerDataManager().getPokedexData(player.getUUID());
        StatAwarder.set(player, POKEDEX_SEEN, dex.getDexCalculatedValue(NATIONAL_DEX, SeenCount.INSTANCE));
        StatAwarder.set(player, POKEMON_REGISTERED, dex.getDexCalculatedValue(NATIONAL_DEX, CaughtCount.INSTANCE));
    }
}
