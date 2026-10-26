package dev.darcosse.common.scoremons.stats.handler;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.battles.BattleFaintedEvent;
import com.cobblemon.mod.common.api.events.battles.BattleVictoryEvent;
import com.cobblemon.mod.common.api.events.battles.instruction.FormeChangeEvent;
import com.cobblemon.mod.common.api.events.battles.instruction.MegaEvolutionEvent;
import com.cobblemon.mod.common.api.events.battles.instruction.TerastallizationEvent;
import com.cobblemon.mod.common.api.events.battles.instruction.ZMoveUsedEvent;
import dev.darcosse.common.scoremons.stats.StatAwarder;
import net.minecraft.resources.ResourceLocation;

import static dev.darcosse.common.scoremons.stats.ScoremonsStats.*;

/**
 * Battle statistics: outcomes per opponent kind, knock-outs and battle gimmicks.
 * <p>
 * Server-side only. All listeners run at {@link Priority#LOWEST} so that they observe the
 * battle after any other addon has reacted to it.
 *
 * @author Darcosse
 * @since 2026
 */
public final class BattleStatHandler {

    /**
     * Prevents instantiation: this class only groups static listeners.
     */
    private BattleStatHandler() {
    }

    /**
     * Subscribes every battle listener to Cobblemon's event bus.
     */
    public static void register() {
        CobblemonEvents.BATTLE_VICTORY.subscribe(Priority.LOWEST, BattleStatHandler::onVictory);
        CobblemonEvents.BATTLE_FAINTED.subscribe(Priority.LOWEST, BattleStatHandler::onFainted);
        CobblemonEvents.MEGA_EVOLUTION.subscribe(Priority.LOWEST,
                (MegaEvolutionEvent e) -> StatAwarder.awardActor(e.getPokemon().getActor(), MEGA_EVOLUTIONS));
        CobblemonEvents.TERASTALLIZATION.subscribe(Priority.LOWEST,
                (TerastallizationEvent e) -> StatAwarder.awardActor(e.getPokemon().getActor(), TERASTALLIZATIONS));
        CobblemonEvents.ZPOWER_USED.subscribe(Priority.LOWEST,
                (ZMoveUsedEvent e) -> StatAwarder.awardActor(e.getPokemon().getActor(), Z_MOVES_USED));
        CobblemonEvents.FORME_CHANGE.subscribe(Priority.LOWEST,
                (FormeChangeEvent e) -> StatAwarder.awardActor(e.getPokemon().getActor(), FORME_CHANGES));
    }

    /**
     * Credits wins and losses according to the kind of opponent.
     * <p>
     * Every player actor on each side is credited; 3.x only credited the first winner and the
     * first loser, which under-counted multi-battles.
     *
     * @param event the victory event
     */
    private static void onVictory(BattleVictoryEvent event) {
        // A capture ends the battle with a "victory" that is neither a win nor a loss.
        if (event.getWasWildCapture()) return;

        PokemonBattle battle = event.getBattle();
        ResourceLocation winStat;
        ResourceLocation lossStat;
        if (battle.isPvP()) {
            winStat = BATTLE_WON;
            lossStat = BATTLE_LOSE;
        } else if (battle.isPvN()) {
            winStat = NPC_BATTLES_WON;
            lossStat = NPC_BATTLES_LOST;
        } else if (battle.isPvW()) {
            // Wild wins are already Cobblemon's own battles_won.
            winStat = null;
            lossStat = WILD_BATTLES_LOST;
        } else {
            return;
        }

        if (winStat != null) {
            for (BattleActor winner : event.getWinners()) StatAwarder.awardActor(winner, winStat);
        }
        for (BattleActor loser : event.getLosers()) StatAwarder.awardActor(loser, lossStat);
    }

    /**
     * Credits a faint to the owner of the fainted Pokémon and a knock-out to every player on the
     * opposite side.
     * <p>
     * Showdown does not say which Pokémon dealt the final blow, so in a 2v2 both opposing
     * players are credited.
     *
     * @param event the faint event
     */
    private static void onFainted(BattleFaintedEvent event) {
        BattleActor victim = event.getKilled().getActor();
        StatAwarder.awardActor(victim, POKEMON_FAINTED);
        for (BattleActor opponent : victim.getSide().getOppositeSide().getActors()) {
            StatAwarder.awardActor(opponent, POKEMON_KNOCKED_OUT);
        }
    }
}
