# ToDo List

## 📋 待办

_暂无待办事项_

## ✅ 已完成

- [x] **修复交易柜中 Shift 快捷移动/批量交易失效缺陷** (2026-10-21)
  - 根因：交易柜为了支持未来交易预览，使用自定义包装类 `PreviewMerchant` 替代实体传入 `MerchantScreenHandler`。当玩家使用 Shift 点击产物槽（快捷移动/批量交易）时，原版 `quickMove` 内部调用 `playYesSound()`，无条件执行 `(Entity) this.merchant` 强转，因 `PreviewMerchant` 未继承 `Entity` 从而抛出 `ClassCastException`，导致整个交易流程在服务端被中断并丢弃。
  - 修复：编写 `MerchantScreenHandlerMixin` 拦截 `playYesSound` (`method_20595`)，安全兼容 `PreviewMerchant`（委派其持有的实际虚拟村民实体播放交易成功音效），消除类型转换崩溃；同时对补货 (`restockAndUpdateDemand`) 补全治愈折扣刷新拦截；编写端到端自动化测试验证 Shift 快捷移动批量交易成功入包。
- [x] **修复治愈村民交易打折丢失缺陷** (2026-10-20)
  - 根因：原版在交易窗口关闭时会调用 `resetCustomer()` -> `clearSpecialPrices()` 将 `specialPrice` 归零，且治愈声望此前未持久化；村民升级和自动交易柜未挂接实际折后价格。
  - 修复：全生命周期持久化 `EasyVillagersCured` 状态与 `DiscountTitle`；在 Mixin 中对 `updatePrices`、`clearSpecialPrices`、升级和补货动态重算并压制首项买入价为 1 绿宝石（全服生效）；修正 `AutoTraderBlockEntity` 扣除原料逻辑与等级同步；增加完整的服务端自动化回归测试。
