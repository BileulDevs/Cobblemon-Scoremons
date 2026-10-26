package dev.darcosse.common.scoremons.stats.handler;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.fishing.BobberSpawnPokemonEvent;
import com.cobblemon.mod.common.api.events.pokeball.PokeBallCaptureCalculatedEvent;
import com.cobblemon.mod.common.api.events.pokeball.ThrownPokeballHitEvent;
import com.cobblemon.mod.common.api.events.pokemon.PokemonCapturedEvent;
import com.cobblemon.mod.common.api.pokeball.catching.CaptureContext;
import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.darcosse.common.scoremons.stats.StatAwarder;
import net.minecraft.server.level.ServerPlayer;

import static dev.darcosse.common.scoremons.stats.ScoremonsStats.*;

/**
 * Capture statistics: rare captures, critical captures, Poké Ball hits and Poké Rod bites.
 * <p>
 * Plain capture and shiny capture counts are left to Cobblemon ({@code cobblemon:captured},
 * {@code cobblemon:shinies_captured}). Server-side only.
 *
 * @author Darcosse
 * @since 2026
 */
public final class CaptureStatHandler {

    /**
     * Prevents instantiation: this class only groups static listeners.
     */
    private CaptureStatHandler() {
    }

    /**
     * Subscribes every capture listener to Cobblemon's event bus.
     */
    public static void register() {
        CobblemonEvents.POKEMON_CAPTURED.subscribe(Priority.LOWEST, CaptureStatHandler::onCaptured);
        CobblemonEvents.POKE_BALL_CAPTURE_CALCULATED.subscribe(Priority.LOWEST, CaptureStatHandler::onCaptureCalculated);
        CobblemonEvents.THROWN_POKEBALL_HIT.subscribe(Priority.LOWEST, CaptureStatHandler::onBallHit);
        CobblemonEvents.BOBBER_SPAWN_POKEMON_POST.subscribe(Priority.LOWEST, CaptureStatHandler::onBite);
    }

    /**
     * Counts legendary, mythical and Ultra Beast captures, plus the marks the Pokémon arrives with.
     *
     * @param event the capture event
     */
    private static void onCaptured(PokemonCapturedEvent event) {
        ServerPlayer player = event.getPlayer();
        Pokemon pokemon = event.getPokemon();

        // Not exclusive: a species may carry several of these labels.
        if (pokemon.isLegendary()) StatAwarder.award(player, LEGENDARY_CAUGHT);
        if (pokemon.isMythical()) StatAwarder.award(player, MYTHICAL_CAUGHT);
        if (pokemon.isUltraBeast()) StatAwarder.award(player, ULTRA_BEASTS_CAUGHT);

        // Wild marks are applied at spawn, before any owner exists, so the mark mixin cannot see them.
        StatAwarder.award(player, MARKS_OBTAINED, pokemon.getMarks().size());
    }

    /**
     * Counts successful critical captures.
     *
     * @param event fired once the shake count is known, before the shake animation
     */
    private static void onCaptureCalculated(PokeBallCaptureCalculatedEvent event) {
        CaptureContext result = event.getCaptureResult();
        if (result.isCriticalCapture() && result.isSuccessfulCapture()
                && event.getThrower() instanceof ServerPlayer player) {
            StatAwarder.award(player, CRITICAL_CAPTURES);
        }
    }

    /**
     * Counts Poké Balls that hit a Pokémon and start a capture attempt.
     *
     * @param event cancelable hit event; cancelled hits drop the ball and are not counted
     */
    private static void onBallHit(ThrownPokeballHitEvent event) {
        if (event.isCanceled()) return;
        if (event.getPokeBall().getOwner() instanceof ServerPlayer player) {
            StatAwarder.award(player, POKEBALL_THROWN);
        }
    }

    /**
     * Counts Pokémon hooked with a Poké Rod.
     *
     * @param event fired once the hooked Pokémon has spawned
     */
    private static void onBite(BobberSpawnPokemonEvent.Post event) {
        if (event.getBobber().getPlayerOwner() instanceof ServerPlayer player) {
            StatAwarder.award(player, POKEMON_REEL);
        }
    }
}
