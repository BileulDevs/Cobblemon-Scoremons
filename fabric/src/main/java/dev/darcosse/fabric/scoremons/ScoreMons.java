package dev.darcosse.fabric.scoremons;

import dev.darcosse.common.scoremons.ScoremonsCommon;
import dev.darcosse.common.scoremons.stats.ScoremonsStats;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

/**
 * Fabric entry point of ScoreMons.
 * <p>
 * Performs the Fabric-specific work (direct registry writes, config directory lookup) and
 * delegates everything else to {@link ScoremonsCommon}.
 *
 * @author Darcosse
 * @since 2026
 */
public class ScoreMons implements ModInitializer {

    /**
     * Registers the custom stats, then boots the shared logic.
     */
    @Override
    public void onInitialize() {
        // Fabric keeps vanilla registries unfrozen during mod init, so a direct write is legal here.
        // A custom stat's value is its own identifier, hence the id/id pair.
        for (ResourceLocation stat : ScoremonsStats.ALL) {
            Registry.register(BuiltInRegistries.CUSTOM_STAT, stat, stat);
        }
        ScoremonsStats.createStatEntries();

        ScoremonsCommon.init(FabricLoader.getInstance().getConfigDir());
    }
}
