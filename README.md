# Cobblemon Scoremons

Trainer statistics for [Cobblemon](https://cobblemon.com): 32 new stats covering battles, rare captures, the Pokédex, training, healing and gathering, shown in the vanilla Statistics screen and ready for `/scoreboard` leaderboards. Shiny and legendary captures can be announced to the whole server.

[![CurseForge](https://img.shields.io/curseforge/dt/1210408?logo=curseforge&label=Downloads&color=f16436)](https://www.curseforge.com/minecraft/mc-mods/cobblemon-scoremons)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-green?logo=minecraft)](https://www.minecraft.net)
[![Fabric](https://img.shields.io/badge/Fabric-supported-dbb37d?logo=fabric)](https://fabricmc.net)
[![NeoForge](https://img.shields.io/badge/NeoForge-supported-e04e14)](https://neoforged.net)

---

## Features

### What Cobblemon doesn't count

Cobblemon already tracks captures, shinies, evolutions, fossils, trades, releases, eggs, level-ups, fishing casts and rides. Scoremons adds everything else, and never duplicates a stat Cobblemon already has.

Every stat is a vanilla custom stat: it appears under **Statistics → General** and can feed any scoreboard objective with `minecraft.custom:scoremons.<id>`.

### Battles

- **PvP**: battles won and lost against other players, every player of a multi-battle counted.
- **NPC trainers**: battles won and lost.
- **Wild Pokémon**: battles lost (Cobblemon only counts the wins).
- **Knock-outs**: opposing Pokémon fainted, and your own Pokémon that fainted.
- **Battle gimmicks**: Mega Evolutions, Terastallizations, Z-Moves and forme changes.

| Stat | Id |
|---|---|
| PvP battles won / lost | `battle_won` / `battle_lose` |
| NPC battles won / lost | `npc_battles_won` / `npc_battles_lost` |
| Battles lost to wild Pokémon | `wild_battles_lost` |
| Opposing Pokémon knocked out | `pokemon_knocked_out` |
| Own Pokémon fainted | `pokemon_fainted` |
| Mega Evolutions | `mega_evolutions` |
| Terastallizations | `terastallizations` |
| Z-Moves used | `z_moves_used` |
| Battle forme changes | `forme_changes` |

### Captures

- **Rare captures**: legendary, mythical and Ultra Beast Pokémon, each with its own counter.
- **Critical captures** that succeed.
- **Poké Balls that hit** a Pokémon, and **Pokémon hooked** with a Poké Rod (not just reeled in).

| Stat | Id |
|---|---|
| Legendary Pokémon caught | `legendary_caught` |
| Mythical Pokémon caught | `mythical_caught` |
| Ultra Beasts caught | `ultra_beasts_caught` |
| Critical captures | `critical_captures` |
| Poké Balls hit | `pokeball_thrown` |
| Pokémon hooked | `pokemon_reel` |

### Pokédex

The Pokédex stats mirror your national Pokédex instead of counting events, so they stay exact. They are refreshed whenever the Pokédex changes and every time you join, so an existing world gets the right numbers straight away.

| Stat | Id |
|---|---|
| Species caught | `pokemon_registered` |
| Species seen | `pokedex_seen` |
| Pokémon scanned | `pokemon_scanned` |

### Training and care

- **Levels gained**: a jump of three levels counts three, not one.
- **Milestones**: Pokémon raised to the level cap, Pokémon reaching maximum friendship.
- **Items**: Rare Candies and Exp. Candies (only when they actually gave experience), IVs raised by Hyper Training, healing items used on your Pokémon.
- **Healing Machine** uses.
- **Marks**: the ones your catches arrive with, plus every mark earned afterwards.
- **Nicknames** given.

| Stat | Id |
|---|---|
| Levels gained | `levels_gained` |
| Pokémon raised to max level | `pokemon_max_level` |
| Pokémon at max friendship | `pokemon_max_friendship` |
| Rare Candies used | `rare_candies_used` |
| Exp. Candies used | `exp_candies_used` |
| IVs Hyper Trained | `ivs_hyper_trained` |
| Pokémon nicknamed | `pokemon_nicknamed` |
| Marks obtained | `marks_obtained` |
| Healing Machine uses | `healing_machine_uses` |
| Healing items used | `healing_items_used` |

### Gathering

| Stat | Id |
|---|---|
| Apricorns harvested | `apricorns_harvested` |
| Berries harvested (each berry counts) | `berries_harvested` |

### Server announcements

A chat message to every player when someone catches a **shiny**, catches a **legendary, mythical or Ultra Beast**, or revives a **shiny fossil**. Each announcement has its own switch. Species names use Cobblemon's own translations, so every player reads them in their language.

### Everywhere else

- **Fabric and NeoForge**, same features on both.
- **Configurable**: one small JSON file toggles the announcements and can switch off any stat.
- **Lightweight**: server-side event listeners only, nothing runs every tick.
- **Translated** in English, French, German, Spanish (Spain and Mexico), Italian, Portuguese (Brazil), Polish, Russian, Ukrainian, Turkish, Japanese, Korean and Chinese (Simplified and Traditional).

---

## How to use

Pick a stat id from the tables above and create an objective with it:

```mcfunction
/scoreboard objectives add legendaries minecraft.custom:scoremons.legendary_caught "Legendaries"
/scoreboard objectives setdisplay sidebar legendaries
```

Cobblemon's own stats work the same way with the `cobblemon` namespace, for example `minecraft.custom:cobblemon.shinies_captured`.

Like every vanilla stat objective, a new objective starts empty and counts from the moment it is created; the Statistics screen still shows the full totals.

### Configuration

`config/scoremons_config.json` is created on first start. New options are added to an existing file automatically.

```json
{
  "broadcastShinyCaught": true,
  "broadcastLegendaryCaught": true,
  "broadcastShinyFossilRevived": true,
  "disabledStats": []
}
```

| Option | Default | Effect |
|---|---|---|
| `broadcastShinyCaught` | `true` | Announce shiny captures |
| `broadcastLegendaryCaught` | `true` | Announce legendary, mythical and Ultra Beast captures |
| `broadcastShinyFossilRevived` | `true` | Announce shiny fossil revivals |
| `disabledStats` | `[]` | Stat ids that stop counting, e.g. `["forme_changes", "pokemon_scanned"]`. They stay registered, so existing objectives keep loading |

### Commands

None. Use the vanilla `/scoreboard` command and the Statistics screen.

---

## Installation

1. Install [Cobblemon](https://www.curseforge.com/minecraft/mc-mods/cobblemon) 1.8.0 or later for Minecraft 1.21.1.
2. Drop the Cobblemon Scoremons jar for your loader into the `mods` folder.
3. Start the game. The config file is created in `config/`.

### Dependencies

| Dependency | Required | Version |
|---|---|---|
| Minecraft | Yes | 1.21.1 |
| Cobblemon | Yes | 1.8.0+ |
| Fabric API | Yes (Fabric) | any for 1.21.1 |
| Fabric Language Kotlin | Yes (Fabric) | any |
| NeoForge | Yes (NeoForge) | 21.1.182+ |

### Client or server?

**Both.** The stats are counted on the server, but clients need the mod to show them in the Statistics screen with their translated names. Install it on the server and on every client.

In single player, nothing else to do.

### Upgrading from 3.x

Version 4.0.0 removes five stats that Cobblemon now tracks identically. Objectives using them keep their last value but stop updating; recreate them with the Cobblemon criterion:

| Removed | Use instead |
|---|---|
| `scoremons.pokemon_caught` | `cobblemon.captured` |
| `scoremons.shiny_pokemon_caught` | `cobblemon.shinies_captured` |
| `scoremons.pokemon_evolved` | `cobblemon.evolved` |
| `scoremons.fossil_revived` | `cobblemon.fossils_revived` |
| `scoremons.trades` | `cobblemon.traded` |

All other stats keep their id and their values. The mod id is now `cobblemon_scoremons`.

---

## More

- [CHANGELOG.md](https://github.com/BileulDevs/Cobblemon-Scoremons/blob/main/CHANGELOG.md): what changed in each version.

## Credits & License

- **Developer**: Darcosse
- **License**: All Rights Reserved, see [LICENSE](https://github.com/BileulDevs/Cobblemon-Scoremons/blob/main/LICENSE)

Found a bug or have an idea? Open an issue on [GitHub](https://github.com/BileulDevs/Cobblemon-Scoremons/issues).

---
