# Easy Villagers (Fabric Edition)

A Fabric 1.20.1 port and evolution of the popular [Easy Villagers](https://github.com/henkelmax/easy-villagers) mod by henkelmax.

## Features

- **Villager Item**: Pick up villagers easily by Sneak + Right Clicking and transport them safely.
- **Trader & Auto Trader**: Trade with villagers inside a single-block glass display case with 3D miniature villager rendering.
- **Breeder**: Single-block villager breeding automation.
- **Converter**: Safe, automated zombie villager conversion for trade discounts.
- **Iron Farm**: Compact, lag-free single-block iron farming.
- **Farmer**: Automated crop harvesting in a single block.

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
