package net.fengye.easyvillagers.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fengye.easyvillagers.block.ModBlocks;
import net.fengye.easyvillagers.client.renderer.TraderBlockEntityRenderer;
import net.minecraft.class_1921;

public class EasyVillagersClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        System.out.println("[EasyVillagers-Client] Registering Client Renderers...");

        // 设置方块为半透明/抠图层 (Cutout)，以便透视内部的 3D 村民实体
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.TRADER_BLOCK, class_1921.method_23579());

        // 注册交易展柜的 3D 微缩实体渲染器
        BlockEntityRendererRegistry.register(ModBlocks.TRADER_BLOCK_ENTITY, TraderBlockEntityRenderer::new);

        System.out.println("[EasyVillagers-Client] Client Renderers registered successfully!");
    }
}
