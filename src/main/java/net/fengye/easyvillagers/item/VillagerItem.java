package net.fengye.easyvillagers.item;

import net.minecraft.class_1269;
import net.minecraft.class_1299;
import net.minecraft.class_1646;
import net.minecraft.class_1657;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1836;
import net.minecraft.class_1838;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2487;
import net.minecraft.class_2561;
import net.minecraft.class_3850;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class VillagerItem extends class_1792 {
    public static final String ENTITY_TAG = "VillagerEntity";
    public static final String PROFESSION_KEY = "ProfessionTitle";
    public static final String LEVEL_KEY = "LevelTitle";
    public static final String NAME_KEY = "CustomVillagerName";

    private static final Map<String, String> PROFESSION_NAMES = new HashMap<>();
    private static final String[] LEVEL_NAMES = {"", "新手 (Novice)", "学徒 (Apprentice)", "老手 (Journeyman)", "专家 (Expert)", "大师 (Master)"};

    static {
        PROFESSION_NAMES.put("none", "无业游民");
        PROFESSION_NAMES.put("armorer", "盔甲匠");
        PROFESSION_NAMES.put("butcher", "屠夫");
        PROFESSION_NAMES.put("cartographer", "制图师");
        PROFESSION_NAMES.put("cleric", "牧师");
        PROFESSION_NAMES.put("farmer", "农民");
        PROFESSION_NAMES.put("fisherman", "渔夫");
        PROFESSION_NAMES.put("fletcher", "制箭师");
        PROFESSION_NAMES.put("leatherworker", "皮革匠");
        PROFESSION_NAMES.put("librarian", "图书管理员");
        PROFESSION_NAMES.put("mason", "石匠");
        PROFESSION_NAMES.put("nitwit", "傻子");
        PROFESSION_NAMES.put("shepherd", "牧羊人");
        PROFESSION_NAMES.put("toolsmith", "工具匠");
        PROFESSION_NAMES.put("weaponsmith", "武器匠");
    }

    public VillagerItem(class_1792.class_1793 settings) {
        super(settings.method_7889(1)); // 最大堆叠为 1
    }

    /**
     * 将村民实体转化为物品 ItemStack
     */
    public static String getProfessionName(String profId) {
        return PROFESSION_NAMES.getOrDefault(profId, profId);
    }

    public static String getLevelName(int level) {
        return (level >= 1 && level <= 5) ? LEVEL_NAMES[level] : ("等级 " + level);
    }

    public static class_1799 createFromVillager(class_1646 villager, class_1792 item) {
        class_1799 stack = new class_1799(item);
        class_2487 tag = stack.method_7948(); // getOrCreateNbt()

        // 导出村民实体数据
        class_2487 entityData = new class_2487();
        villager.method_5647(entityData); // saveNbt()
        tag.method_10566(ENTITY_TAG, entityData);

        // 提取职业信息
        class_3850 villagerData = villager.method_7231();
        String profId = villagerData.method_16924().comp_818();
        String profName = PROFESSION_NAMES.getOrDefault(profId, profId);
        tag.method_10582(PROFESSION_KEY, profName);

        // 提取等级信息
        int level = villagerData.method_16925();
        String levelName = (level >= 1 && level <= 5) ? LEVEL_NAMES[level] : ("等级 " + level);
        tag.method_10582(LEVEL_KEY, levelName);

        // 自定义名称
        if (villager.method_16914()) { // hasCustomName
            tag.method_10582(NAME_KEY, villager.method_5797().getString());
        }

        return stack;
    }

    @Override
    public class_1269 method_7884(class_1838 context) { // useOnBlock
        class_1937 world = context.method_8045();
        if (world.field_9236) { // isClient
            return class_1269.field_5812;
        }

        class_2338 hitPos = context.method_8037();
        class_2350 side = context.method_8038();
        class_2338 targetPos = hitPos.method_10093(side);

        class_1799 stack = context.method_8041();
        class_1657 player = context.method_8036();

        // 实例化村民
        class_1646 villager = class_1299.field_6077.method_5883(world);
        if (villager != null) {
            class_2487 nbt = stack.method_7969();
            if (nbt != null && nbt.method_10545(ENTITY_TAG)) {
                class_2487 entityData = nbt.method_10562(ENTITY_TAG).method_10553(); // copy
                entityData.method_10551("UUID"); // 移除旧 UUID
                villager.method_5651(entityData);
            }

            // 分配新 UUID
            villager.method_5826(UUID.randomUUID());

            // 设置放置位置与朝向
            double spawnX = targetPos.method_10263() + 0.5;
            double spawnY = targetPos.method_10264();
            double spawnZ = targetPos.method_10260() + 0.5;
            villager.method_5808(spawnX, spawnY, spawnZ, context.method_8044(), 0.0f);

            // 成功在世界生成实体
            world.method_8649(villager);

            // 消耗物品（非创造模式）
            if (player != null && !player.method_7337()) {
                stack.method_7934(1);
            }

            return class_1269.field_5812;
        }

        return class_1269.field_5811;
    }

    @Override
    public void method_7851(class_1799 stack, class_1937 world, List<class_2561> tooltip, class_1836 context) { // appendTooltip
        class_2487 nbt = stack.method_7969();
        if (nbt != null) {
            if (nbt.method_10545(NAME_KEY)) {
                tooltip.add(class_2561.method_43470("§b村民: §f" + nbt.method_10558(NAME_KEY)));
            }
            String prof = nbt.method_10558(PROFESSION_KEY);
            String level = nbt.method_10558(LEVEL_KEY);

            if (!prof.isEmpty()) {
                tooltip.add(class_2561.method_43470("§e职业: §f" + prof));
            } else {
                tooltip.add(class_2561.method_43470("§7职业: 无业村民"));
            }

            if (!level.isEmpty()) {
                tooltip.add(class_2561.method_43470("§a等级: §f" + level));
            }
        } else {
            tooltip.add(class_2561.method_43470("§7空村民（未捕获具体数据）"));
        }

        tooltip.add(class_2561.method_43470("§8[提示] 对准地面右键即可安全放回世界"));
        super.method_7851(stack, world, tooltip, context);
    }
}
