package net.fengye.easyvillagers.client.renderer;

import net.fengye.easyvillagers.block.IronFarmBlock;
import net.fengye.easyvillagers.block.entity.IronFarmBlockEntity;
import net.minecraft.class_1439;
import net.minecraft.class_1646;
import net.minecraft.class_2350;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_5614;
import net.minecraft.class_827;
import net.minecraft.class_898;
import org.joml.Quaternionf;

public class IronFarmBlockEntityRenderer implements class_827<IronFarmBlockEntity> {
    private final class_898 entityRenderDispatcher;

    public IronFarmBlockEntityRenderer(class_5614.class_5615 ctx) {
        this.entityRenderDispatcher = ctx.method_43334();
    }

    @Override
    public void method_3569(IronFarmBlockEntity entity, float tickDelta, class_4587 matrices, class_4597 vertexConsumers, int light, int overlay) {
        if (!entity.hasVillager()) return;

        class_2350 facing = entity.method_11010().method_11654(IronFarmBlock.FACING);
        float rotationY = switch (facing) {
            case field_11035 -> 180.0f;
            case field_11034 -> 90.0f;
            case field_11039 -> 270.0f;
            default -> 0.0f;
        };

        // 1. 渲染微缩村民 (左前方)
        class_1646 villager = entity.getOrCreateCachedVillager();
        if (villager != null) {
            matrices.method_22903();
            matrices.method_22904(0.5, 0.12, 0.5);
            matrices.method_22907(new Quaternionf().rotationY((float) Math.toRadians(rotationY)));
            matrices.method_22904(-0.20, 0.0, 0.10);
            matrices.method_22905(0.32f, 0.32f, 0.32f);
            matrices.method_22907(new Quaternionf().rotationY((float) Math.toRadians(35.0f)));

            VillagerRenderFreezeHelper.freezeRotation(villager);

            this.entityRenderDispatcher.method_3954(villager, 0.0, 0.0, 0.0, 0.0f, 0.0f, matrices, vertexConsumers, light);
            matrices.method_22909();
        }

        // 2. 渲染微缩铁傀儡 (右后方)
        class_1439 ironGolem = entity.getOrCreateCachedIronGolem();
        if (ironGolem != null) {
            matrices.method_22903();
            matrices.method_22904(0.5, 0.12, 0.5);
            matrices.method_22907(new Quaternionf().rotationY((float) Math.toRadians(rotationY)));
            matrices.method_22904(0.15, 0.0, -0.05);
            matrices.method_22905(0.24f, 0.24f, 0.24f);
            matrices.method_22907(new Quaternionf().rotationY((float) Math.toRadians(-20.0f)));

            VillagerRenderFreezeHelper.freezeRotation(ironGolem);

            this.entityRenderDispatcher.method_3954(ironGolem, 0.0, 0.0, 0.0, 0.0f, 0.0f, matrices, vertexConsumers, light);
            matrices.method_22909();
        }
    }
}
