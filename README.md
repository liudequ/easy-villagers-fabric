# Easy Villagers (Fabric Edition)

A Fabric 1.20.1 port and evolution of the popular [Easy Villagers](https://github.com/henkelmax/easy-villagers) mod by henkelmax.

## Features & Implementation Status

- [x] **Villager Item (Phase 1)**: Sneak + Right Click any villager with empty hand to pick them up into your inventory with full NBT preservation (profession, level, custom name, trade history, discounts). Right-click on the ground to safely release.
- [x] **Trader Showcase Block (Phase 2)**: 1x1 glass display block (`easy_villagers:trader`) with horizontal facing and Cutout transparency. Rendered with custom 3D miniature villager entity via `TraderBlockEntityRenderer`. Right-click to open native merchant trade UI; Sneak + Right Click with empty hand to extract villager safely.
- [x] **Workstation Slot & Instant Trade Cycling (Phase 2.5)**: 
  - Insert any of the 13 vanilla workstations (Lectern, Blast Furnace, etc.) directly into the showcase with dual miniature 3D rendering.
  - Sneak + Right Click to prioritize extracting the workstation, instantly resetting unlocked villagers back to unemployed. Re-insert to cycle trades in 1 second!
  - Integrated server-side block ticker auto-restocks trades twice a day without entity AI/pathfinding lag.
- [x] **Auto Trader (Phase 3)**:
  - 1x1 automated trading block (`easy_villagers:auto_trader`) crafted with a Trader Showcase + Hopper + Redstone.
  - SidedInventory automation: 4 input slots (top/sides) and 4 output slots (bottom) natively supporting hoppers and pipe mods.
  - Cycle target trade recipe easily by right-clicking with a Redstone Dust, Redstone Torch, or Stick (Sneak to cycle backwards) with instant ActionBar HUD feedback.
  - Background ticker executes trades automatically every 5 seconds (100 ticks), awards merchant XP, auto-restocks every minute, and drops all inventory contents safely on block break.
- [x] **Breeder & Incubator (Phase 4)**:
  - **Breeder (`easy_villagers:breeder`)**: Compact single-block villager breeding station. Accommodates 2 adult villagers with 3D miniature face-to-face rendering. Includes 4 food input slots (top/sides) supporting bread, carrots, potatoes, and beetroots. Automatically breeds every 20 seconds consuming food points and outputs Baby Villager items to the output slot (hopper extractable from bottom).
  - **Incubator (`easy_villagers:incubator`)**: Single-block rapid baby villager maturation chamber. Displays miniature baby villager inside with glowing warm hay base. Automatically accelerates maturation from 20 minutes down to 30 seconds, turning baby villager items into adult unemployed villagers extractable via hopper from bottom.
  - **Baby Villager Item**: Extended `VillagerItem` with custom `IsBaby` NBT tag, distinct tooltip ("👶 幼年村民"), and safe world spawning.
- [x] **Converter (Phase 4.5)**:
  - **Converter (`easy_villagers:converter`)**: Automated 1x1 villager infection and curing chamber with crying obsidian base.
  - SidedInventory automation: 2 input slots (top/sides) accepting Golden Apples and Weakness Potions, and 1 output slot (bottom) for hopper extraction.
  - Full automated state machine: Normal villager -> 3-second zombie infection -> 15-second high-frequency curing vibration with 3D miniature zombie model -> cured villager with permanent 1-emerald discount injected into all trade recipes.
  - Automatic Golden Apple and Potion consumption upon curing initiation.
- [ ] **Iron Farm & Farmer (Phase 5)**: Compact, lag-free single-block iron farming and crop harvesting.

## Architecture

- **Platform**: Fabric Loader (Minecraft 1.20.1)
- **Mapping**: Yarn / Intermediary
- **Renderer**: Custom BlockEntityRenderer (BER) with 3D miniature entity rendering
- **License**: GNU General Public License v3.0 (GPLv3)

## Build

```bash
./package.sh
```
Output jar will be located at `build/libs/easy-villagers-fabric-1.0.0.jar`.
