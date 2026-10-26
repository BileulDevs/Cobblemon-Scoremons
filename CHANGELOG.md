# Changelog

## 4.0.0

Major release: ScoreMons now runs on **Fabric and NeoForge**, drops the stats Cobblemon tracks
on its own, and adds 27 new statistics.

### ⚠️ Breaking changes

#### Removed stats (now built into Cobblemon)

Cobblemon 1.7+ ships identical statistics. Scoreboard objectives using the old ids keep their
last values but stop updating (Minecraft turns them into dummy objectives and logs a warning).
Recreate them with the Cobblemon criterion:

| Removed (3.x) | Use instead |
|---|---|
| `minecraft.custom:scoremons.pokemon_caught` | `minecraft.custom:cobblemon.captured` |
| `minecraft.custom:scoremons.shiny_pokemon_caught` | `minecraft.custom:cobblemon.shinies_captured` |
| `minecraft.custom:scoremons.pokemon_evolved` | `minecraft.custom:cobblemon.evolved` |
| `minecraft.custom:scoremons.fossil_revived` | `minecraft.custom:cobblemon.fossils_revived` |
| `minecraft.custom:scoremons.trades` | `minecraft.custom:cobblemon.traded` |

Cobblemon's counters only exist since Cobblemon 1.7, so their values can be lower than the old
ScoreMons ones on older worlds. Cobblemon's shiny count does not include shiny fossils, which
ScoreMons 3.x counted.

#### Other breaking changes

- **Mod id changed from `scoremons` to `cobblemon_scoremons`.** Modpacks, server configs or
  addons that reference the mod by id (dependencies, mod lists, `/fabric` or `/neoforge`
  tooling) must be updated. Statistics are **not** affected: they keep the `scoremons`
  namespace, so `minecraft.custom:scoremons.<id>` objectives and saved stats carry over.
- Log output now appears under the `Scoremons` logger (was `ScoreMons`).
- Java packages moved from `com.darcosse.scoremons.fabric` to `dev.darcosse.{common,fabric,forge}.scoremons`.
- `pokemon_reel` and `pokeball_thrown` keep their ids but got clearer names
  ("Pokémon Hooked", "Poké Balls Hit"): they always counted bites and hits, not casts and throws.
- Species names in broadcasts now come from Cobblemon's own translations; the 1025 species
  entries ScoreMons duplicated in its lang files are gone.

### ✨ New

- **NeoForge support** alongside Fabric (multiloader build, one jar per loader).
- **27 new statistics**:
  - Battles: `npc_battles_won`, `npc_battles_lost`, `wild_battles_lost`, `pokemon_knocked_out`,
    `pokemon_fainted`, `mega_evolutions`, `terastallizations`, `z_moves_used`, `forme_changes`.
  - Captures: `legendary_caught`, `mythical_caught`, `ultra_beasts_caught`, `critical_captures`.
  - Pokédex: `pokedex_seen`, `pokemon_scanned`.
  - Training & care: `levels_gained`, `pokemon_max_level`, `pokemon_max_friendship`,
    `rare_candies_used`, `exp_candies_used`, `ivs_hyper_trained`, `pokemon_nicknamed`,
    `marks_obtained`, `healing_machine_uses`, `healing_items_used`.
  - Gathering: `apricorns_harvested`, `berries_harvested`.
- **`disabledStats`** config option to switch off individual stats.
- **15 languages**: English, French, German, Spanish (Spain and Mexico), Italian,
  Brazilian Portuguese, Russian, Ukrainian, Polish, Turkish, Japanese, Korean,
  Simplified and Traditional Chinese.
- Pokédex stats are synchronised when a player joins, so worlds created before ScoreMons get
  correct values without waiting for the next Pokédex change.

### 🐛 Fixed

- **Crash on trades with a non-player participant**: the trade handler cast every participant
  to a player.
- **PvP stats in multi-battles**: only the first winner and first loser were credited; every
  player on each side is now counted.
- **`pokemon_registered` cost**: the stat was reset then incremented once per Pokédex entry on
  every Pokédex change (about 1000 scoreboard updates for a full dex); it is now set in one step
  and only recomputed when a species is newly caught.
- **Missing translation** for the PvP losses stat (`battle_lose`).
- **Config crash on an empty or malformed file**: Gson returned null and the first broadcast
  threw; the file now falls back to defaults and is rewritten.
- **Wrong mod icon path** in `fabric.mod.json`.
- Species names such as `Nidoranf` / `Nidoranm` displayed raw in broadcasts.

### 🔧 Internal

- Event listeners run at the lowest priority and ignore cancelled events, so ScoreMons counts
  what actually happened after other addons reacted.
- All stat writes go through a single helper (`StatAwarder`), which applies the config kill
  switch and keeps scoreboards in sync.
- Handlers split by domain (battles, captures, Pokédex, care, gathering, broadcasts).
- Two mixins on Cobblemon classes for actions without an event: healing machine activation and
  marks given to owned Pokémon.
