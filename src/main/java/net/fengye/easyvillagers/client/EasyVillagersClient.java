package net.fengye.easyvillagers.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fengye.easyvillagers.block.ModBlocks;
import net.fengye.easyvillagers.client.renderer.BreederBlockEntityRenderer;
import net.fengye.easyvillagers.client.renderer.ConverterBlockEntityRenderer;
import net.fengye.easyvillagers.client.renderer.FarmerBlockEntityRenderer;
import net.fengye.easyvillagers.client.renderer.IncubatorBlockEntityRenderer;
import net.fengye.easyvillagers.client.renderer.IronFarmBlockEntityRenderer;
import net.fengye.easyvillagers.client.renderer.TraderBlockEntityRenderer;
import net.minecraft.class_1921;

public class EasyVillagersClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        System.out.println("[EasyVillagers-Client] Registering Client Renderers...");

        // 设置方块为半透明/抠图层 (Cutout)，以便透视内部的 3D 村民实体
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.TRADER_BLOCK, class_1921.method_23579());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.AUTO_TRADER_BLOCK, class_1921.method_23579());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.BREEDER_BLOCK, class_1921.method_23579());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.INCUBATOR_BLOCK, class_1921.method_23579());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CONVERTER_BLOCK, class_1921.method_23579());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.IRON_FARM_BLOCK, class_1921.method_23579());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.FARMER_BLOCK, class_1921.method_23579());

        // 注册方块实体的 3D 微缩实体渲染器
        BlockEntityRendererRegistry.register(ModBlocks.TRADER_BLOCK_ENTITY, TraderBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(ModBlocks.AUTO_TRADER_BLOCK_ENTITY, TraderBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(ModBlocks.BREEDER_BLOCK_ENTITY, BreederBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(ModBlocks.INCUBATOR_BLOCK_ENTITY, IncubatorBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(ModBlocks.CONVERTER_BLOCK_ENTITY, ConverterBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(ModBlocks.IRON_FARM_BLOCK_ENTITY, IronFarmBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(ModBlocks.FARMER_BLOCK_ENTITY, FarmerBlockEntityRenderer::new);

        System.out.println("[EasyVillagers-Client] Client Renderers registered successfully!");
    }
}
