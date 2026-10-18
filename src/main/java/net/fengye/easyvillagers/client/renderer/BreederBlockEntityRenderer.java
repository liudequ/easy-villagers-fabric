package net.fengye.easyvillagers.client.renderer;

import net.fengye.easyvillagers.block.BreederBlock;
import net.fengye.easyvillagers.block.entity.BreederBlockEntity;
import net.minecraft.class_1646;
import net.minecraft.class_2350;
import net.minecraft.class_2680;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_5614;
import net.minecraft.class_827;
import net.minecraft.class_898;
import org.joml.Quaternionf;

public class BreederBlockEntityRenderer implements class_827<BreederBlockEntity> {
    private final class_898 entityRenderDispatcher;

    public BreederBlockEntityRenderer(class_5614.class_5615 ctx) {
        this.entityRenderDispatcher = ctx.method_43334();
    }

    @Override
    public void method_3569(BreederBlockEntity entity, float tickDelta, class_4587 matrices, class_4597 vertexConsumers, int light, int overlay) {
        boolean hasV1 = entity.hasVillager1();
        boolean hasV2 = entity.hasVillager2();
        if (!hasV1 && !hasV2) return;

        matrices.method_22903(); // push
        matrices.method_22904(0.5, 0.05, 0.5); // 方块中心

        class_2680 state = entity.method_11010();
        class_2350 facing = class_2350.field_11043;
        if (state.method_28498(BreederBlock.FACING)) {
            facing = state.method_11654(BreederBlock.FACING);
        }

        // 基础朝向旋转
        float baseAngle = -facing.method_10144();
        matrices.method_22907(new Quaternionf().rotateY((float) Math.toRadians(baseAngle)));

        float scale = 0.35f;

        // 渲染村民 1（左侧，微向内倾斜注视）
        if (hasV1) {
            class_1646 v1 = entity.getOrCreateCachedVillager1();
            if (v1 != null) {
                matrices.method_22903();
                matrices.method_22904(-0.22, 0.0, 0.0);
                matrices.method_22907(new Quaternionf().rotateY((float) Math.toRadians(35)));
                matrices.method_22905(scale, scale, scale);
                this.entityRenderDispatcher.method_3954(v1, 0.0, 0.0, 0.0, 0.0f, tickDelta, matrices, vertexConsumers, light);
                matrices.method_22909();
            }
        }

        // 渲染村民 2（右侧，微向内倾斜注视）
        if (hasV2) {
            class_1646 v2 = entity.getOrCreateCachedVillager2();
            if (v2 != null) {
                matrices.method_22903();
                matrices.method_22904(0.22, 0.0, 0.0);
                matrices.method_22907(new Quaternionf().rotateY((float) Math.toRadians(-35)));
                matrices.method_22905(scale, scale, scale);
                this.entityRenderDispatcher.method_3954(v2, 0.0, 0.0, 0.0, 0.0f, tickDelta, matrices, vertexConsumers, light);
                matrices.method_22909();
            }
        }

        matrices.method_22909(); // pop
    }
}
