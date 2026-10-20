# ToDo List

## 📋 待办

_暂无待办事项_

## ✅ 已完成

- [x] **修复治愈村民交易打折丢失缺陷** (2026-10-20)
  - 根因：原版在交易窗口关闭时会调用 `resetCustomer()` -> `clearSpecialPrices()` 将 `specialPrice` 归零，且治愈声望此前未持久化；村民升级和自动交易柜未挂接实际折后价格。
  - 修复：全生命周期持久化 `EasyVillagersCured` 状态与 `DiscountTitle`；在 Mixin 中对 `updatePrices`、`clearSpecialPrices`、升级和补货动态重算并压制首项买入价为 1 绿宝石（全服生效）；修正 `AutoTraderBlockEntity` 扣除原料逻辑与等级同步；增加完整的服务端自动化回归测试。
