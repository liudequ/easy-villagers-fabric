package net.fengye.easyvillagers.client.renderer;

import net.fengye.easyvillagers.block.FarmerBlock;
import net.fengye.easyvillagers.block.entity.FarmerBlockEntity;
import net.minecraft.class_1646;
import net.minecraft.class_2350;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_5614;
import net.minecraft.class_827;
import net.minecraft.class_898;
import org.joml.Quaternionf;

public class FarmerBlockEntityRenderer implements class_827<FarmerBlockEntity> {
    private final class_898 entityRenderDispatcher;

    public FarmerBlockEntityRenderer(class_5614.class_5615 ctx) {
        this.entityRenderDispatcher = ctx.method_43334();
    }

    @Override
    public void method_3569(FarmerBlockEntity entity, float tickDelta, class_4587 matrices, class_4597 vertexConsumers, int light, int overlay) {
        if (!entity.hasVillager()) return;

        class_1646 villager = entity.getOrCreateCachedVillager();
        if (villager == null) return;

        class_2350 facing = entity.method_11010().method_11654(FarmerBlock.FACING);
        float rotationY = switch (facing) {
            case field_11035 -> 180.0f;
            case field_11034 -> 90.0f;
            case field_11039 -> 270.0f;
            default -> 0.0f;
        };

        matrices.method_22903();
        matrices.method_22904(0.5, 0.12, 0.5);
        matrices.method_22907(new Quaternionf().rotationY((float) Math.toRadians(rotationY)));
        matrices.method_22905(0.42f, 0.42f, 0.42f);

        villager.field_6283 = 0.0f;
        villager.field_6241 = 0.0f;
        villager.field_6012 = 0;

        this.entityRenderDispatcher.method_3954(villager, 0.0, 0.0, 0.0, 0.0f, tickDelta, matrices, vertexConsumers, light);
        matrices.method_22909();
    }
}
