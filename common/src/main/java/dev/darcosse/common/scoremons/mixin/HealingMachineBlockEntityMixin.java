package dev.darcosse.common.scoremons.mixin;

import com.cobblemon.mod.common.api.storage.party.PartyStore;
import com.cobblemon.mod.common.block.entity.HealingMachineBlockEntity;
import dev.darcosse.common.scoremons.stats.handler.CareStatHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

/**
 * Counts healing machine activations, for which Cobblemon fires no event.
 * <p>
 * The only Cobblemon heal event, {@code POKEMON_HEALED}, fires once per Pokémon with a generic
 * source shared with commands and PC healing, so it cannot tell a machine use apart.
 * <p>
 * {@code remap = false}: the target is a Cobblemon class whose method descriptor contains no
 * Minecraft type, so its name is identical in every mapping set and needs no refmap.
 *
 * @author Darcosse
 * @since 2026
 */
@Mixin(value = HealingMachineBlockEntity.class, remap = false)
public abstract class HealingMachineBlockEntityMixin {

    /**
     * Credits the player once the machine has accepted the party.
     *
     * @param user  UUID of whoever started the machine (player or NPC)
     * @param party the party being healed
     * @param ci    mixin callback, unused
     */
    @Inject(method = "activate", at = @At("TAIL"))
    private void scoremons$countActivation(UUID user, PartyStore party, CallbackInfo ci) {
        CareStatHandler.onHealingMachineUsed(user);
    }
}
