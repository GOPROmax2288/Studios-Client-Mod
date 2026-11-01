# STUDIOS Client Core

The in-game mod menu and client HUD suite for **PRO STUDIOS** — a Fabric client mod in the spirit of Lunar/Feather, branded with the STUDIOS dark navy (#0b1120) / amber (#f59e0b) theme.

![icon](src/main/resources/assets/studios-client/icon.png)

## What it does

- **Studios Menu** — press **Right Shift** in game to open a clean grid of module cards with live toggle state (also opens via ModMenu's config button when ModMenu is installed).
- **HUD Editor** — from the menu, drag any HUD element to reposition it. Positions save to `config/studios-client.json`.

## Modules (v1)

| Module | What it does |
|---|---|
| FPS Display | Styled top-left FPS counter |
| CPS Counter | Clicks per second for both mouse buttons (rolling 1s window) |
| Keystrokes | WASD + LMB/RMB blocks that light amber on press |
| Coordinates | X / Y / Z readout |
| Ping Display | Your latency to the server |
| Zoom | Hold **C** for smooth Lunar/OptiFine-style zoom (strength configurable in config) |
| Fullbright | Gamma boost, toggled from the menu |
| Low Particle Density | Sets the vanilla particle option to "Decreased" while enabled |
| Sky Fog Tweak | Pushes distance fog start/end out so distant terrain stays visible (mixin on the vanilla fog renderer) |

Toggle state, HUD positions and zoom strength all persist in `config/studios-client.json` (Gson).

## Install

1. Install the [Fabric Loader](https://fabricmc.net/use/installer/) for Minecraft 1.21.11 (works alongside the STUDIOS Client launcher or a plain vanilla Fabric install).
2. Download the latest jar from [Releases](https://github.com/GOPROmax2288/Studios-Client-Mod/releases) or CI artifacts.
3. Drop it in your `.minecraft/mods` folder alongside [Fabric API](https://modrinth.com/mod/fabric-api).
4. In game: **Right Shift** opens the menu, **C** zooms.

## Compatibility

- **Sodium is NOT bundled** with this mod. The STUDIOS Client launcher already ships Sodium; this mod works fine alongside it and does not duplicate it. If you install it on a plain vanilla Fabric profile, install Sodium yourself if you want it.
- Requires [Fabric API](https://modrinth.com/mod/fabric-api).

## Building

Standard Fabric Gradle setup (fabric-loom, Mojang mappings, Java 21):

```
./gradlew build
```

The jar lands in `build/libs/`. Every push builds automatically via GitHub Actions.

## Roadmap

- Custom capes delivery (PRO STUDIOS supporter capes)
- Emotes
- Theme engine (user-selectable accent colors / HUD skins)
- Per-element scale & opacity in the HUD editor

## License

MIT

## Credits

Built by PRO STUDIOS (GOPROmax2288). Fabric/Loader/Loom, Fabric API, ModMenu (compile-only integration) by their respective authors.
