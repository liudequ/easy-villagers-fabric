package net.fengye.easyvillagers.client.renderer;

import net.fengye.easyvillagers.block.ConverterBlock;
import net.fengye.easyvillagers.block.entity.ConverterBlockEntity;
import net.minecraft.class_1309;
import net.minecraft.class_2350;
import net.minecraft.class_2680;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_5614;
import net.minecraft.class_827;
import net.minecraft.class_898;
import org.joml.Quaternionf;

public class ConverterBlockEntityRenderer implements class_827<ConverterBlockEntity> {
    private final class_898 entityRenderDispatcher;

    public ConverterBlockEntityRenderer(class_5614.class_5615 ctx) {
        this.entityRenderDispatcher = ctx.method_43334();
    }

    @Override
    public void method_3569(ConverterBlockEntity entity, float tickDelta, class_4587 matrices, class_4597 vertexConsumers, int light, int overlay) {
        if (!entity.hasVillager()) return;

        matrices.method_22903(); // push
        matrices.method_22904(0.5, 0.05, 0.5);

        class_2680 state = entity.method_11010();
        class_2350 facing = class_2350.field_11043;
        if (state.method_28498(ConverterBlock.FACING)) {
            facing = state.method_11654(ConverterBlock.FACING);
        }

        float baseAngle = -facing.method_10144();
        matrices.method_22907(new Quaternionf().rotateY((float) Math.toRadians(baseAngle)));

        ConverterBlockEntity.State cState = entity.getCurrentState();

        // 治愈阶段高频颤抖视觉特效
        if (cState == ConverterBlockEntity.State.CURING) {
            double shake = Math.sin((System.currentTimeMillis() % 10000) * 0.05) * 0.02;
            matrices.method_22904(shake, 0.0, 0.0);
        }

        float scale = 0.45f;
        matrices.method_22905(scale, scale, scale);

        class_1309 toRender;
        if (cState == ConverterBlockEntity.State.INFECTING || cState == ConverterBlockEntity.State.CURING) {
            toRender = entity.getOrCreateCachedZombieVillager();
        } else {
            toRender = entity.getOrCreateCachedVillager();
        }

        if (toRender != null) {
            VillagerRenderFreezeHelper.freezeRotation(toRender);
            this.entityRenderDispatcher.method_3954(toRender, 0.0, 0.0, 0.0, 0.0f, 0.0f, matrices, vertexConsumers, light);
        }

        matrices.method_22909(); // pop
    }
}
