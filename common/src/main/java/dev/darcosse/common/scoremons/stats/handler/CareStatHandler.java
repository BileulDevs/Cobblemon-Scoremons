package dev.darcosse.common.scoremons.stats.handler;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.CobblemonItems;
import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.pokemon.FriendshipUpdatedEvent;
import com.cobblemon.mod.common.api.events.pokemon.HyperTrainedIvEvent;
import com.cobblemon.mod.common.api.events.pokemon.LevelUpEvent;
import com.cobblemon.mod.common.api.events.pokemon.PokemonNicknamedEvent;
import com.cobblemon.mod.common.api.events.pokemon.healing.PokemonHealedEvent;
import com.cobblemon.mod.common.api.events.pokemon.interaction.ExperienceCandyUseEvent;
import com.cobblemon.mod.common.api.item.HealingSource;
import com.cobblemon.mod.common.api.mark.Mark;
import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.darcosse.common.scoremons.stats.StatAwarder;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

import static dev.darcosse.common.scoremons.stats.ScoremonsStats.*;

/**
 * Training and care statistics: levels, friendship, candies, Hyper Training, nicknames, marks
 * and healing.
 * <p>
 * Marks and healing machine uses have no Cobblemon event; they are fed by the mixins in
 * {@code dev.darcosse.common.scoremons.mixin}. Server-side only.
 *
 * @author Darcosse
 * @since 2026
 */
public final class CareStatHandler {

    /**
     * Prevents instantiation: this class only groups static listeners.
     */
    private CareStatHandler() {
    }

    /**
     * Subscribes every care listener to Cobblemon's event bus.
     */
    public static void register() {
        CobblemonEvents.LEVEL_UP_EVENT.subscribe(Priority.LOWEST, CareStatHandler::onLevelUp);
        CobblemonEvents.FRIENDSHIP_UPDATED.subscribe(Priority.LOWEST, CareStatHandler::onFriendshipUpdated);
        CobblemonEvents.EXPERIENCE_CANDY_USE_POST.subscribe(Priority.LOWEST, CareStatHandler::onCandyUsed);
        CobblemonEvents.HYPER_TRAINED_IV_POST.subscribe(Priority.LOWEST,
                (HyperTrainedIvEvent.Post e) -> StatAwarder.award(e.getPokemon().getOwnerPlayer(), IVS_HYPER_TRAINED));
        CobblemonEvents.POKEMON_NICKNAMED.subscribe(Priority.LOWEST, CareStatHandler::onNicknamed);
        CobblemonEvents.POKEMON_HEALED.subscribe(Priority.LOWEST, CareStatHandler::onHealed);
    }

    /**
     * Counts levels gained and Pokémon reaching the level cap.
     * <p>
     * Cobblemon fires a single event for a multi-level jump, hence the difference rather than +1.
     * Listening last matters: earlier listeners may rewrite {@code newLevel}.
     *
     * @param event the level-up event
     */
    private static void onLevelUp(LevelUpEvent event) {
        ServerPlayer owner = event.getPokemon().getOwnerPlayer();
        int oldLevel = event.getOldLevel();
        int newLevel = event.getNewLevel();
        StatAwarder.award(owner, LEVELS_GAINED, newLevel - oldLevel);

        int maxLevel = Cobblemon.INSTANCE.getConfig().getMaxPokemonLevel();
        if (oldLevel < maxLevel && newLevel >= maxLevel) {
            StatAwarder.award(owner, POKEMON_MAX_LEVEL);
        }
    }

    /**
     * Counts Pokémon reaching maximum friendship.
     * <p>
     * The event fires before the new value is stored, so {@code getFriendship()} still holds the
     * previous value and the crossing can be detected exactly once.
     *
     * @param event the friendship update event
     */
    private static void onFriendshipUpdated(FriendshipUpdatedEvent event) {
        Pokemon pokemon = event.getPokemon();
        int maxFriendship = Cobblemon.INSTANCE.getConfig().getMaxPokemonFriendship();
        if (pokemon.getFriendship() < maxFriendship && event.getNewFriendship() >= maxFriendship) {
            StatAwarder.award(pokemon.getOwnerPlayer(), POKEMON_MAX_FRIENDSHIP);
        }
    }

    /**
     * Counts Rare Candies and Exp. Candies that actually gave experience.
     *
     * @param event fired after the candy was applied; a candy used at the level cap gives nothing
     */
    private static void onCandyUsed(ExperienceCandyUseEvent.Post event) {
        if (!event.wasExperienceGiven()) return;
        StatAwarder.award(event.getPlayer(),
                event.getItem() == CobblemonItems.RARE_CANDY ? RARE_CANDIES_USED : EXP_CANDIES_USED);
    }

    /**
     * Counts nicknames given; clearing a nickname is not counted.
     *
     * @param event cancelable nickname event
     */
    private static void onNicknamed(PokemonNicknamedEvent event) {
        if (event.isCanceled()) return;
        String nickname = event.getNicknameString();
        if (nickname != null && !nickname.isBlank()) {
            StatAwarder.award(event.getPlayer(), POKEMON_NICKNAMED);
        }
    }

    /**
     * Counts healing items used on a Pokémon (potions, revives, healing berries...).
     * <p>
     * {@link HealingSource.Force} is excluded: it is what a full heal uses (healing machine,
     * PC heal, commands) and fires once per Pokémon, which would inflate the count.
     *
     * @param event cancelable heal event
     */
    private static void onHealed(PokemonHealedEvent event) {
        if (event.isCanceled() || event.getSource() == HealingSource.Force.INSTANCE) return;
        StatAwarder.award(event.getPokemon().getOwnerPlayer(), HEALING_ITEMS_USED);
    }

    /**
     * Counts a healing machine activation. Called from the healing machine mixin.
     *
     * @param user UUID of whoever started the machine; NPC users resolve to no player and are ignored
     */
    public static void onHealingMachineUsed(UUID user) {
        StatAwarder.award(StatAwarder.player(user), HEALING_MACHINE_USES);
    }

    /**
     * Counts a mark newly given to an owned Pokémon. Called from the Pokémon mixin, before the
     * mark is added.
     * <p>
     * {@code getOwnerPlayer()} returns null on the client copy of a Pokémon, which keeps the
     * client-side packet replay from counting twice in singleplayer.
     *
     * @param pokemon the Pokémon receiving the mark
     * @param mark    the mark being given
     */
    public static void onMarkGiven(Pokemon pokemon, Mark mark) {
        if (pokemon.getMarks().contains(mark)) return;
        StatAwarder.award(pokemon.getOwnerPlayer(), MARKS_OBTAINED);
    }
}
