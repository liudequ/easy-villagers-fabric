package net.fengye.easyvillagers.client.renderer;

import net.minecraft.class_1309;

/**
 * 展柜内微缩实体渲染辅助工具：
 * 强制锁定实体的所有偏航角、头部角度、俯仰角与历史插值缓存，
 * 防止客户端在渲染帧根据 tickDelta 产生跳跃旋转与剧烈抖动。
 */
public class VillagerRenderFreezeHelper {

    public static void freezeRotation(class_1309 entity) {
        if (entity == null) return;

        // 身体偏航角与插值历史锁死
        entity.field_6283 = 0.0f; // bodyYaw
        entity.field_6220 = 0.0f; // prevBodyYaw

        // 头部偏航角与插值历史锁死
        entity.field_6241 = 0.0f; // headYaw
        entity.field_6259 = 0.0f; // prevHeadYaw

        // 实体朝向与俯仰角锁死
        entity.method_36456(0.0f); // setYaw
        entity.field_6004 = 0.0f;  // prevYaw
        entity.method_36457(0.0f); // setPitch
        entity.field_6039 = 0.0f;  // prevPitch

        // 肢体动画速度置零，避免行走动作抖动
        if (entity.field_42108 != null) {
            entity.field_42108.method_48567(0.0f);
        }

        // 年龄固定，避免由于随机动画计时造成模型摆动
        entity.field_6012 = 0;
    }
}
