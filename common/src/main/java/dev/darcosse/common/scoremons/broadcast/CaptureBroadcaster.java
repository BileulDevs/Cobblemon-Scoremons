package dev.darcosse.common.scoremons.broadcast;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.pokemon.FossilRevivedEvent;
import com.cobblemon.mod.common.api.events.pokemon.PokemonCapturedEvent;
import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.darcosse.common.scoremons.config.ConfigManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;

/**
 * Server-wide chat announcements for remarkable captures and fossil revivals.
 * <p>
 * Kept separate from the statistics: broadcasts are toggled in the config independently and do
 * not depend on any stat being enabled. Server-side only.
 *
 * @author Darcosse
 * @since 2026
 */
public final class CaptureBroadcaster {

    /** Gold-ish RGB used for shiny fossil broadcasts; purely cosmetic. */
    private static final int SHINY_FOSSIL_COLOR = 0xC49E33;

    /**
     * Prevents instantiation: this class only groups static listeners.
     */
    private CaptureBroadcaster() {
    }

    /**
     * Subscribes the broadcast listeners to Cobblemon's event bus.
     */
    public static void register() {
        CobblemonEvents.POKEMON_CAPTURED.subscribe(Priority.LOWEST, CaptureBroadcaster::onCaptured);
        CobblemonEvents.FOSSIL_REVIVED.subscribe(Priority.LOWEST, CaptureBroadcaster::onFossilRevived);
    }

    /**
     * Announces shiny captures and legendary / mythical / Ultra Beast captures.
     *
     * @param event the capture event
     */
    private static void onCaptured(PokemonCapturedEvent event) {
        ServerPlayer player = event.getPlayer();
        Pokemon pokemon = event.getPokemon();

        if (pokemon.getShiny() && ConfigManager.shouldBroadcastShinyCaught()) {
            broadcast(player, "scoremons.message.shiny_caught", pokemon, ChatFormatting.GOLD);
        }
        if ((pokemon.isLegendary() || pokemon.isMythical() || pokemon.isUltraBeast())
                && ConfigManager.shouldBroadcastLegendaryCaught()) {
            broadcast(player, "scoremons.message.legendary_caught", pokemon, ChatFormatting.LIGHT_PURPLE);
        }
    }

    /**
     * Announces shiny fossil revivals.
     *
     * @param event the revival event; the player is null when revived by automation
     */
    private static void onFossilRevived(FossilRevivedEvent event) {
        ServerPlayer player = event.getPlayer();
        if (player == null || !event.getPokemon().getShiny() || !ConfigManager.shouldBroadcastShinyFossilRevived()) return;

        MutableComponent msg = message(player, "scoremons.message.shiny_fossil_revived", event.getPokemon())
                .withStyle(style -> style.withColor(SHINY_FOSSIL_COLOR));
        player.server.getPlayerList().broadcastSystemMessage(msg, false);
    }

    /**
     * Sends a coloured announcement to every connected player.
     *
     * @param player  the player being announced
     * @param key     translation key taking the player name then the species name
     * @param pokemon the Pokémon being announced
     * @param color   the message colour
     */
    private static void broadcast(ServerPlayer player, String key, Pokemon pokemon, ChatFormatting color) {
        player.server.getPlayerList().broadcastSystemMessage(message(player, key, pokemon).withStyle(color), false);
    }

    /**
     * Builds an announcement.
     * <p>
     * The species name uses Cobblemon's own translation, which every client already has in every
     * language Cobblemon supports; 3.x shipped a copy of all 1025 species names instead.
     *
     * @param player  the player being announced
     * @param key     translation key taking the player name then the species name
     * @param pokemon the Pokémon being announced
     * @return the translatable message
     */
    private static MutableComponent message(ServerPlayer player, String key, Pokemon pokemon) {
        return Component.translatable(key, player.getName(), pokemon.getSpecies().getTranslatedName());
    }
}
