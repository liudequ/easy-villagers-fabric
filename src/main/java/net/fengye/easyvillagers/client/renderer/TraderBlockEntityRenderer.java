package net.fengye.easyvillagers.client.renderer;

import net.fengye.easyvillagers.block.TraderBlock;
import net.fengye.easyvillagers.block.entity.TraderBlockEntity;
import net.fengye.easyvillagers.util.VillagerWorkstationHelper;
import net.minecraft.class_1646;
import net.minecraft.class_1799;
import net.minecraft.class_2350;
import net.minecraft.class_2680;
import net.minecraft.class_3850;
import net.minecraft.class_3852;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_5614;
import net.minecraft.class_811;
import net.minecraft.class_827;
import net.minecraft.class_898;
import net.minecraft.class_918;
import org.joml.Quaternionf;

public class TraderBlockEntityRenderer<T extends TraderBlockEntity> implements class_827<T> {
    private final class_898 entityRenderDispatcher;
    private final class_918 itemRenderer;

    public TraderBlockEntityRenderer(class_5614.class_5615 ctx) {
        this.entityRenderDispatcher = ctx.method_43334();
        this.itemRenderer = ctx.method_43335();
    }

    @Override
    public void method_3569(T entity, float tickDelta, class_4587 matrices, class_4597 vertexConsumers, int light, int overlay) {
        boolean hasVillager = entity.hasVillager();
        boolean hasWorkstation = entity.hasWorkstation();

        if (!hasVillager && !hasWorkstation) {
            return;
        }

        matrices.method_22903(); // push
        // 平移至方块中心
        matrices.method_22904(0.5, 0.0, 0.5);

        // 计算朝向旋转角度
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
        matrices.method_22907(new Quaternionf().rotateY((float) Math.toRadians(angle)));

        // 1. 渲染微缩村民
        if (hasVillager) {
            class_1646 villager = entity.getOrCreateCachedVillager();
            if (villager != null) {
                // 核心校准：强制将村民 DataTracker 的职业外观与展柜状态同步
                if (hasWorkstation) {
                    class_3852 prof = VillagerWorkstationHelper.getProfession(entity.getWorkstation());
                    if (prof != null) {
                        class_3850 data = villager.method_7231();
                        if (data.method_16924() != prof && VillagerWorkstationHelper.canCycleTrade(villager)) {
                            villager.method_7195(data.method_16921(prof).method_16920(Math.max(1, data.method_16925())));
                        }
                    }
                } else {
                    if (VillagerWorkstationHelper.canCycleTrade(villager)) {
                        class_3850 data = villager.method_7231();
                        class_3852 noneProf = VillagerWorkstationHelper.getNoneProfession();
                        if (noneProf != null && data.method_16924() != noneProf) {
                            villager.method_7195(data.method_16921(noneProf).method_16920(1));
                        }
                    }
                }

                matrices.method_22903(); // push
                if (hasWorkstation) {
                    // 与工作台并排：偏左
                    matrices.method_22904(-0.16, 0.05, 0.0);
                    float scale = 0.42f;
                    matrices.method_22905(scale, scale, scale);
                } else {
                    // 独占居中
                    matrices.method_22904(0.0, 0.05, 0.0);
                    float scale = 0.45f;
                    matrices.method_22905(scale, scale, scale);
                }

                VillagerRenderFreezeHelper.freezeRotation(villager);

                this.entityRenderDispatcher.method_3954(villager, 0.0, 0.0, 0.0, 0.0f, 0.0f, matrices, vertexConsumers, light);
                matrices.method_22909(); // pop
            }
        }

        // 2. 渲染微缩工作台方块
        if (hasWorkstation) {
            class_1799 workstationStack = entity.getWorkstation();
            if (workstationStack != null && !workstationStack.method_7960()) {
                matrices.method_22903(); // push
                if (hasVillager) {
                    // 与村民并排：偏右
                    matrices.method_22904(0.18, 0.22, 0.0);
                    float scale = 0.45f;
                    matrices.method_22905(scale, scale, scale);
                } else {
                    // 独占居中
                    matrices.method_22904(0.0, 0.25, 0.0);
                    float scale = 0.50f;
                    matrices.method_22905(scale, scale, scale);
                }

                this.itemRenderer.method_23178(
                        workstationStack,
                        class_811.field_4319, // FIXED
                        light,
                        overlay,
                        matrices,
                        vertexConsumers,
                        entity.method_10997(),
                        0
                );
                matrices.method_22909(); // pop
            }
        }

        matrices.method_22909(); // pop
    }
}
