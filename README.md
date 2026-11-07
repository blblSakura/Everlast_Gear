# Everlast Gear — Minecraft 1.20.1 Forge

此分支为 **Minecraft 1.20.1 / Forge 47.4.x / Java 17** 版本，模组版本 1.0.2，作者 dysakura。

## 功能

- 受保护的工具、武器、护甲和其他可损耗物品在剩余 1 点耐久时进入破损状态，不会因原版扣耐久而消失。
- 破损工具禁止挖掘、普通攻击及物品/方块右键操作；破损护甲移除物品属性加成。
- 铁砧和经验修补沿用原版机制；修复到剩余耐久大于 1 后恢复使用及护甲属性。
- 绑定诅咒物品不受保护，正常损坏。
- 支持白名单/黑名单，默认排除暮色森林玻璃剑。

配置文件首次启动时生成于 `config/everlast_gear.json`：

```json
{
  "mode": "DENYLIST",
  "items": ["twilightforest:glass_sword"]
}
```

`DENYLIST` 保护名单之外的物品；`ALLOWLIST` 只保护名单中的物品。更改配置后重启游戏/服务器。

## 安装

将 `everlast_gear-1.0.2-mc1.20.1-forge.jar` 放入 Forge 1.20.1 实例的 `mods` 目录。单人游戏安装在客户端，联机建议客户端和服务器均安装。Mixin 由 Forge 提供，无需安装独立前置模组。

## 构建与验证

请参阅 [BUILDING.md](BUILDING.md)。

本版本通过 ItemStack 的 Mixin 在扣耐久时钳制损耗值，替代旧版无效的每刻锁定条件。该改动只属于此 Forge 分支。
模组主要兼容原版及遵循 ItemStack 扣耐久流程的物品；其他模组若直接删除物品或自行实现损坏流程，需要单独验证。

## English

Minecraft 1.20.1 Forge port. Requires Java 17 and Forge 47.4.x. Exhausted equipment stays at one durability, broken tools cannot mine, attack or use item/block interactions, and broken armor loses its item attribute bonuses. Vanilla anvil and Mending repair restore usability. Binding Curse items are excluded. Configure `config/everlast_gear.json` with `DENYLIST` or `ALLOWLIST`.

License: MIT. Original author: dysakura / bilibilisakura.
