package dev.darcosse.forge.scoremons;

import dev.darcosse.common.scoremons.ScoremonsCommon;
import dev.darcosse.common.scoremons.stats.ScoremonsStats;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.registries.RegisterEvent;

/**
 * NeoForge entry point of ScoreMons.
 * <p>
 * Registers the custom stats through {@link RegisterEvent} and boots the shared logic from
 * {@link ScoremonsCommon}.
 *
 * @author Darcosse
 * @since 2026
 */
@Mod(ScoremonsCommon.MOD_ID)
public class ScoreMons {

    /**
     * Wires the mod-bus listeners and initializes the shared logic.
     *
     * @param modEventBus the mod-specific event bus injected by FML
     */
    public ScoreMons(IEventBus modEventBus) {
        modEventBus.addListener(this::onRegister);
        modEventBus.addListener(this::onCommonSetup);

        ScoremonsCommon.init(FMLPaths.CONFIGDIR.get());
    }

    /**
     * Adds the ScoreMons identifiers to {@code CUSTOM_STAT}.
     *
     * @param event fired once per registry; filtered to {@code CUSTOM_STAT} by the key argument
     */
    private void onRegister(RegisterEvent event) {
        // NeoForge freezes vanilla registries before mod constructors run: a direct
        // Registry.register (as on Fabric) throws, so this event is the only legal window.
        event.register(Registries.CUSTOM_STAT, helper -> {
            for (ResourceLocation stat : ScoremonsStats.ALL) {
                helper.register(stat, stat);
            }
        });
    }

    /**
     * Creates the {@code Stat} entries once registries are populated.
     *
     * @param event common setup event
     */
    private void onCommonSetup(FMLCommonSetupEvent event) {
        // Common setup runs in parallel across mods and Stats.CUSTOM is backed by a non-thread-safe
        // map, so the work is pushed back onto the main thread.
        event.enqueueWork(ScoremonsStats::createStatEntries);
    }
}
