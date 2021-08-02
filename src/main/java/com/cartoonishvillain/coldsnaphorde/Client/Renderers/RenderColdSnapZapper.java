package com.cartoonishvillain.coldsnaphorde.Client.Renderers;

import com.cartoonishvillain.coldsnaphorde.Client.Models.ColdSnapZapperModel;
import com.cartoonishvillain.coldsnaphorde.ColdSnapHorde;
import com.cartoonishvillain.coldsnaphorde.Entities.Mobs.ColdSnapZapper;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import static com.cartoonishvillain.coldsnaphorde.Client.RenderManager.COLDSNAPZAPPER;

public class RenderColdSnapZapper extends MobRenderer<ColdSnapZapper, ColdSnapZapperModel<ColdSnapZapper>> {

    protected static final ResourceLocation TEXTURE = new ResourceLocation(ColdSnapHorde.MOD_ID, "textures/entity/coldsnapzapper.png");
    protected static final ResourceLocation FTEXTURE = new ResourceLocation(ColdSnapHorde.MOD_ID, "textures/entity/flamingcoldsnapzapper.png");
    protected static final ResourceLocation ETEXTURE = new ResourceLocation(ColdSnapHorde.MOD_ID, "textures/entity/endercoldsnapzapper.png");
    protected static final ResourceLocation PTEXTURE = new ResourceLocation(ColdSnapHorde.MOD_ID, "textures/entity/plaguecoldsnapzapper.png");

    public RenderColdSnapZapper(EntityRendererProvider.Context p_174304_) {
        super(p_174304_, new ColdSnapZapperModel<>(p_174304_.bakeLayer(COLDSNAPZAPPER)), 0.5f);
    }

//    public RenderColdSnapZapper(EntityRenderDispatcher renderManagerIn) {
//        super(renderManagerIn, new ColdSnapZapperModel<>(), 0.5F);
//    }

    @Override
    public ResourceLocation getTextureLocation(ColdSnapZapper entity) {
        switch (entity.getHordeVariant()){
            case ENDER -> {return ETEXTURE;}
            case FLAMING -> {return FTEXTURE;}
            case PLAGUE -> {return PTEXTURE;}
            default -> {return TEXTURE;}
        }
    }
}
