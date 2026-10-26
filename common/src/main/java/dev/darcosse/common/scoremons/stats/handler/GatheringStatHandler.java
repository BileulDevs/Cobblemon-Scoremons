package dev.darcosse.common.scoremons.stats.handler;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.berry.BerryHarvestEvent;
import com.cobblemon.mod.common.api.events.farming.ApricornHarvestEvent;
import dev.darcosse.common.scoremons.stats.StatAwarder;
import net.minecraft.world.item.ItemStack;

import static dev.darcosse.common.scoremons.stats.ScoremonsStats.*;

/**
 * Gathering statistics: Apricorns and berries harvested. Server-side only.
 *
 * @author Darcosse
 * @since 2026
 */
public final class GatheringStatHandler {

    /**
     * Prevents instantiation: this class only groups static listeners.
     */
    private GatheringStatHandler() {
    }

    /**
     * Subscribes every gathering listener to Cobblemon's event bus.
     */
    public static void register() {
        CobblemonEvents.APRICORN_HARVESTED.subscribe(Priority.LOWEST,
                (ApricornHarvestEvent e) -> StatAwarder.award(e.getPlayer(), APRICORNS_HARVESTED));
        CobblemonEvents.BERRY_HARVEST.subscribe(Priority.LOWEST, GatheringStatHandler::onBerryHarvest);
    }

    /**
     * Counts every berry dropped by a harvest, not the number of harvests.
     * <p>
     * Read last because other listeners may edit the mutable drop list.
     *
     * @param event the harvest event, posted before the drops are handed out
     */
    private static void onBerryHarvest(BerryHarvestEvent event) {
        int berries = 0;
        for (ItemStack drop : event.getDrops()) berries += drop.getCount();
        StatAwarder.award(event.getPlayer(), BERRIES_HARVESTED, berries);
    }
}
