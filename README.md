# Easy Villagers (Fabric Edition)

A Fabric 1.20.1 port and evolution of the popular [Easy Villagers](https://github.com/henkelmax/easy-villagers) mod by henkelmax.

## Features & Implementation Status

- [x] **Villager Item (Phase 1)**: Sneak + Right Click any villager with empty hand to pick them up into your inventory with full NBT preservation (profession, level, custom name, trade history, discounts). Right-click on the ground to safely release.
- [x] **Trader Showcase Block (Phase 2)**: 1x1 glass display block (`easy_villagers:trader`) with horizontal facing and Cutout transparency. Rendered with custom 3D miniature villager entity via `TraderBlockEntityRenderer`. Right-click to open native merchant trade UI; Sneak + Right Click with empty hand to extract villager safely.
- [ ] **Workstation Slot & Trade Cycling (Phase 2.5)**: Insert workstation block (Lectern, Blast Furnace, etc.) inside the showcase to instantly assign/cycle professions and auto-restock daily.
- [ ] **Auto Trader (Phase 3)**: Automatic trades with hopper/container input & output.
- [ ] **Breeder & Incubator (Phase 4)**: Single-block automated breeding and baby villager maturation.
- [ ] **Converter (Phase 4)**: Automated villager curing for permanent emerald trade discounts.
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
