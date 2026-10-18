package net.fengye.easyvillagers.client.renderer;

import net.fengye.easyvillagers.block.IncubatorBlock;
import net.fengye.easyvillagers.block.entity.IncubatorBlockEntity;
import net.minecraft.class_1646;
import net.minecraft.class_2350;
import net.minecraft.class_2680;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_5614;
import net.minecraft.class_827;
import net.minecraft.class_898;
import org.joml.Quaternionf;

public class IncubatorBlockEntityRenderer implements class_827<IncubatorBlockEntity> {
    private final class_898 entityRenderDispatcher;

    public IncubatorBlockEntityRenderer(class_5614.class_5615 ctx) {
        this.entityRenderDispatcher = ctx.method_43334();
    }

    @Override
    public void method_3569(IncubatorBlockEntity entity, float tickDelta, class_4587 matrices, class_4597 vertexConsumers, int light, int overlay) {
        if (!entity.hasBaby()) return;

        class_1646 baby = entity.getOrCreateCachedBaby();
        if (baby == null) return;

        matrices.method_22903();
        matrices.method_22904(0.5, 0.05, 0.5);

        class_2680 state = entity.method_11010();
        class_2350 facing = class_2350.field_11043;
        if (state.method_28498(IncubatorBlock.FACING)) {
            facing = state.method_11654(IncubatorBlock.FACING);
        }

        float baseAngle = -facing.method_10144();
        matrices.method_22907(new Quaternionf().rotateY((float) Math.toRadians(baseAngle)));

        float scale = 0.4f;
        matrices.method_22905(scale, scale, scale);

        this.entityRenderDispatcher.method_3954(baby, 0.0, 0.0, 0.0, 0.0f, tickDelta, matrices, vertexConsumers, light);

        matrices.method_22909();
    }
}
