# 🌾 Easy Villagers (Fabric Edition)

[![Minecraft 1.20.1](https://img.shields.io/badge/Minecraft-1.20.1-brightgreen.svg)](https://minecraft.net/)
[![Fabric Loader](https://img.shields.io/badge/Loader-Fabric-blue.svg)](https://fabricmc.net/)
[![Java 17](https://img.shields.io/badge/Java-17-orange.svg)](https://adoptium.net/)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-yellow.svg)](https://www.gnu.org/licenses/gpl-3.0)

> A modern, lag-free, single-block villager ecosystem for **Minecraft 1.20.1 (Fabric)**.  
> 专为 Minecraft 1.20.1 Fabric 打造的现代化、零卡顿、单方块村民生态模组。

---

## ✨ Features / 核心特性

- 🎒 **Pocket Villagers (随身抓取)**: Sneak + Right Click with an empty hand to pick up any villager into your inventory as an item. Preserves all NBT data, professions, trade history, discounts, and custom names.
- 📦 **1x1 Compact Blocks (单方块微缩)**: Replaces massive villager trading halls and noisy iron farms with clean 1x1 glass showcase blocks.
- 🎨 **Living 3D Miniature Renders (3D微缩实体渲染)**: Fully animated 3D mini villagers, zombies, and iron golems living inside the showcase blocks with frozen visual orientation.
- ⚡ **1-Second Trade Cycling (1秒极速洗词条)**: Insert workstations directly into showcases. Sneak-click to remove and instantly cycle novice trades without waiting for pathfinding.
- 🔄 **Fully Automated Pipeline (全自动化管道)**: Every machine is natively integrated with vanilla hoppers, droppers, and item transport mods (SidedInventory).
- 🚀 **Zero-Lag Performance (极致性能)**: All operations (trading, breeding, iron farming, cropping) are simulated in background tickers without triggering entity AI pathfinding.

---

## 🏛️ The Machine Matrix / 核心方块生态

| 方块 (Block) | 主要功能 | 漏斗输入 (Top/Sides) | 漏斗输出 (Bottom) |
| :--- | :--- | :--- | :--- |
| **Trader**<br>`交易展柜` | 容纳 1 只村民 + 1 个工作台，直接右键打开交易，秒洗词条 | — | — |
| **Auto Trader**<br>`全自动交易展柜` | 定时自动交易（每5秒），自动增长商人经验与每日补货 | 原材料（绿宝石/纸等） | 交易产物（附魔书/装备等） |
| **Breeder**<br>`村民繁殖机` | 容纳 2 只村民，消耗食物（20秒）自动繁衍幼体 | 面包/胡萝卜/马铃薯/甜菜根 | 幼年村民物品 |
| **Incubator**<br>`幼体催熟机` | 将幼年村民快速催熟为成年村民（从20分钟加速至30秒） | 幼年村民物品 | 成年村民物品 |
| **Converter**<br>`僵尸转化机` | 自动虚弱+金苹果治愈村民，赋予**全交易永久1绿宝石特惠** | 金苹果、虚弱药水 | 治愈后的骨折特惠村民 |
| **Iron Farm**<br>`单方块刷铁机` | 容纳 1 只村民，每 10 秒自动产出 3~5 铁锭与虞美人 | — | 铁锭、虞美人 |
| **Farmer**<br>`自动农夫方块` | 容纳 1 只农民村民，每 7 秒自动收获并增殖农作物 | 小麦/胡萝卜/土豆/甜菜种子 | 小麦、胡萝卜、马铃薯等成熟作物 |

---

## 🛠️ Crafting Recipes / 合成配方一览

| 产物 | 合成材料布局 |
| :--- | :--- |
| **Trader Showcase (交易展柜)** | 顶部与侧面为 **玻璃**，中央为 **绿宝石**，底部为 **平滑石台阶** |
| **Auto Trader (全自动交易展柜)** | **交易展柜** + **漏斗** + **红石粉** |
| **Breeder (村民繁殖机)** | 顶部为 **白羊毛**，中央为 **红色床**，底部为 **干草块** |
| **Incubator (幼体催熟机)** | 顶部与侧面为 **玻璃**，中央为 **岩浆膏**，底部为 **干草块** |
| **Converter (村民转化机)** | 顶部与侧面为 **玻璃**，中央为 **铁栏杆**，底部为 **哭泣的黑曜石** |
| **Iron Farm (单方块刷铁机)** | 中央为 **交易展柜**，两侧为 **铁块**，上方为 **雕刻过的南瓜**，底部为 **岩浆桶** |
| **Farmer (自动农夫方块)** | 中央为 **交易展柜**，两侧为 **泥土/耕地**，上方为 **铁锄**，底部为 **骨粉** |

---

## 🎮 How to Play / 玩法指南

### 1. 抓取与搬家
- **空手按住 Shift（潜行）+ 右键** 任意村民，即可将村民收入背包；
- 手持村民物品直接右键地面，即可原汁原味安全释放。

### 2. 挑选附魔书与完美词条
1. 放置 **交易展柜（Trader）**；
2. 手持村民物品右键塞入展柜；
3. 手持对应工作台（如讲台、锻造台）右键塞入工作台槽；
4. 鼠标右键打开交易面板查看附魔书；
5. 若不满意，**按住 Shift + 右键** 瞬间取下讲台（村民重置为无业游民），再次塞入即可秒刷下一本附魔书！

### 3. 建立 1 绿宝石骨折商场
1. 制作 **村民转化机（Converter）**；
2. 放入挑选好职业的村民，并通过漏斗或手动投入 1 瓶虚弱药水与 1 个金苹果；
3. 机器自动执行感染与治愈，18 秒后自动出炉带有全物品降至 1 绿宝石的超级特惠村民！

### 4. 搭建全自动交易流水线
1. 将 **全自动交易展柜（Auto Trader）** 放置在地面；
2. 手持 **红石粉 / 木棍** 右键敲击展柜，切换并锁定需要自动执行的交易（如绿宝石换钻石甲）；
3. 上方接漏斗注入材料，下方接漏斗抽取成品，无需人工介入即可 24 小时全自动产出！

---

## 🏗️ Building from Source / 源码编译

```bash
# Clone the repository
git clone https://github.com/liudequ/easy-villagers-fabric.git
cd easy-villagers-fabric

# Compile and package jar
./package.sh
```
The compiled mod jar will be generated at `build/libs/easy-villagers-fabric-1.0.0.jar`.

---

## 📜 License

This project is licensed under the **GNU General Public License v3.0 (GPLv3)**.  
Inspired by the original Forge mod [Easy Villagers](https://github.com/henkelmax/easy-villagers) by [henkelmax](https://github.com/henkelmax).
