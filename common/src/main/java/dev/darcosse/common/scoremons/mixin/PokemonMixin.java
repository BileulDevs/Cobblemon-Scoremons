package dev.darcosse.common.scoremons.mixin;

import com.cobblemon.mod.common.api.mark.Mark;
import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.darcosse.common.scoremons.stats.handler.CareStatHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Counts marks given to owned Pokémon, for which Cobblemon fires no event.
 * <p>
 * {@code exchangeMark} is the single funnel used by commands, MoLang scripts (and therefore
 * datapacks and addons) and Poké Rod mark rolls. Marks a wild Pokémon already carries are
 * counted at capture time instead, see {@code CaptureStatHandler}.
 * <p>
 * {@code remap = false}: Cobblemon class and descriptor without Minecraft types.
 *
 * @author Darcosse
 * @since 2026
 */
@Mixin(value = Pokemon.class, remap = false)
public abstract class PokemonMixin {

    /**
     * Inspects the mark before it is added, while "already owned" can still be detected.
     *
     * @param mark the mark being exchanged
     * @param give true when the mark is given, false when removed
     * @param ci   mixin callback, unused
     */
    @Inject(method = "exchangeMark", at = @At("HEAD"))
    private void scoremons$countMarkGiven(Mark mark, boolean give, CallbackInfo ci) {
        if (give) CareStatHandler.onMarkGiven((Pokemon) (Object) this, mark);
    }
}
