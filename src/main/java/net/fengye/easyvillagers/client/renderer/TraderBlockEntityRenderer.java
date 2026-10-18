package net.fengye.easyvillagers.client.renderer;

import net.fengye.easyvillagers.block.TraderBlock;
import net.fengye.easyvillagers.block.entity.TraderBlockEntity;
import net.minecraft.class_1646;
import net.minecraft.class_2350;
import net.minecraft.class_2680;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_5614;
import net.minecraft.class_827;
import net.minecraft.class_898;
import org.joml.Quaternionf;

public class TraderBlockEntityRenderer implements class_827<TraderBlockEntity> {
    private final class_898 entityRenderDispatcher;

    public TraderBlockEntityRenderer(class_5614.class_5615 ctx) {
        this.entityRenderDispatcher = ctx.method_43334();
    }

    @Override
    public void method_3569(TraderBlockEntity entity, float tickDelta, class_4587 matrices, class_4597 vertexConsumers, int light, int overlay) {
        if (!entity.hasVillager()) {
            return;
        }

        class_1646 villager = entity.getOrCreateCachedVillager();
        if (villager == null) {
            return;
        }

        matrices.method_22903(); // push

        // 平移到方块中心微偏下方
        matrices.method_22904(0.5, 0.05, 0.5); // translate

        // 获取朝向并旋转，使村民面向正面玻璃
        class_2680 state = entity.method_11010();
        float angle = 0.0f;
        if (state.method_28498(TraderBlock.FACING)) {
            class_2350 facing = state.method_11654(TraderBlock.FACING);
            switch (facing) {
                case field_11036 -> angle = 180.0f; // SOUTH
                case field_11039 -> angle = 90.0f;  // WEST
                case field_11034 -> angle = 270.0f; // EAST
                default -> angle = 0.0f;            // NORTH
            }
        }
        matrices.method_22907(new Quaternionf().rotateY((float) Math.toRadians(angle))); // rotate

        // 缩放到微缩比例 (0.45x)
        float scale = 0.45f;
        matrices.method_22905(scale, scale, scale); // scale

        // 临时禁用阴影或设置实体的偏航角保持端正
        villager.field_6241 = 0.0f;
        villager.field_6283 = 0.0f;
        villager.field_6014 = 0.0f;

        // 调用实体渲染器绘制 3D 模型
        this.entityRenderDispatcher.method_3954(villager, 0.0, 0.0, 0.0, 0.0f, tickDelta, matrices, vertexConsumers, light);

        matrices.method_22909(); // pop
    }
}
