package com.cartoonishvillain.coldsnaphorde.client;

import com.cartoonishvillain.coldsnaphorde.client.models.endmodel.EndColdSnapSnowballerModel;
import com.cartoonishvillain.coldsnaphorde.client.models.nethermodel.*;
import com.cartoonishvillain.coldsnaphorde.client.models.standardmodel.*;
import com.cartoonishvillain.coldsnaphorde.client.renderers.endrenders.RenderEndColdSnapSnowballer;
import com.cartoonishvillain.coldsnaphorde.client.renderers.netherrenders.*;
import com.cartoonishvillain.coldsnaphorde.client.renderers.standardrenders.*;
import com.cartoonishvillain.coldsnaphorde.ColdSnapHorde;
import com.cartoonishvillain.coldsnaphorde.Register;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = ColdSnapHorde.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class RenderManager {

    public static ModelLayerLocation COLDSNAPSTABBER = new ModelLayerLocation( new ResourceLocation("coldsnaphorde:stabber"), "stabber");
    public static ModelLayerLocation COLDSNAPGUNNER = new ModelLayerLocation( new ResourceLocation("coldsnaphorde:gunner"), "gunner");
    public static ModelLayerLocation COLDSNAPSNOWBALLER = new ModelLayerLocation( new ResourceLocation("coldsnaphorde:snowballer"), "snowballer");
    public static ModelLayerLocation COLDSNAPGIFTER = new ModelLayerLocation( new ResourceLocation("coldsnaphorde:gifter"), "gifter");
    public static ModelLayerLocation COLDSNAPZAPPER = new ModelLayerLocation( new ResourceLocation("coldsnaphorde:zapper"), "zapper");
    public static ModelLayerLocation COLDSNAPBRAWLER = new ModelLayerLocation( new ResourceLocation("coldsnaphorde:brawler"), "brawler");

    public static ModelLayerLocation NCOLDSNAPSTABBER = new ModelLayerLocation( new ResourceLocation("coldsnaphorde:nstabber"), "nstabber");
    public static ModelLayerLocation NCOLDSNAPGUNNER = new ModelLayerLocation( new ResourceLocation("coldsnaphorde:ngunner"), "ngunner");
    public static ModelLayerLocation NCOLDSNAPBRAWLER = new ModelLayerLocation( new ResourceLocation("coldsnaphorde:nbrawler"), "nbrawler");
    public static ModelLayerLocation NCOLDSNAPSNOWBALLER = new ModelLayerLocation( new ResourceLocation("coldsnaphorde:nsnowballer"), "nsnowballer");
    public static ModelLayerLocation NCOLDSNAPGIFTER = new ModelLayerLocation( new ResourceLocation("coldsnaphorde:ngifter"), "ngifter");
    public static ModelLayerLocation NCOLDSNAPZAPPER = new ModelLayerLocation( new ResourceLocation("coldsnaphorde:nzapper"), "nzapper");

    public static ModelLayerLocation ECOLDSNAPSNOWBALLER = new ModelLayerLocation( new ResourceLocation("coldsnaphorde:esnowballer"), "esnowballer");

    public static ModelLayerLocation TOPHAT = new ModelLayerLocation( new ResourceLocation("minecraft:player"), "tophat");



    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event){
        event.registerLayerDefinition(COLDSNAPSTABBER, ColdSnapStabberModel::createBodyLayer);
        event.registerLayerDefinition(COLDSNAPGUNNER, ColdSnapGunnerModel::createBodyLayer);
        event.registerLayerDefinition(COLDSNAPSNOWBALLER, ColdSnapSnowballerModel::createBodyLayer);
        event.registerLayerDefinition(COLDSNAPGIFTER, ColdSnapGifterModel::createBodyLayer);
        event.registerLayerDefinition(COLDSNAPZAPPER, ColdSnapZapperModel::createBodyLayer);
        event.registerLayerDefinition(COLDSNAPBRAWLER, ColdSnapBrawlerModel::createBodyLayer);
        event.registerLayerDefinition(ECOLDSNAPSNOWBALLER, EndColdSnapSnowballerModel::createBodyLayer);
        event.registerLayerDefinition(NCOLDSNAPSTABBER, NetherColdSnapStabberModel::createBodyLayer);
        event.registerLayerDefinition(NCOLDSNAPGUNNER, NetherColdSnapGunnerModel::createBodyLayer);
        event.registerLayerDefinition(NCOLDSNAPSNOWBALLER, NetherColdSnapSnowballerModel::createBodyLayer);
        event.registerLayerDefinition(NCOLDSNAPGIFTER, NetherColdSnapGifterModel::createBodyLayer);
        event.registerLayerDefinition(NCOLDSNAPZAPPER, NetherColdSnapZapperModel::createBodyLayer);
        event.registerLayerDefinition(NCOLDSNAPBRAWLER, NetherColdSnapBrawlerModel::createBodyLayer);
        event.registerLayerDefinition(TOPHAT, TopHatModel::createLayer);
        event.registerLayerDefinition(TOPHAT, TopHatModel::createLayer);
        event.registerLayerDefinition(TOPHAT, TopHatModel::createLayer);
        event.registerLayerDefinition(TOPHAT, TopHatModel::createLayer);
        event.registerLayerDefinition(TOPHAT, TopHatModel::createLayer);

    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event){
        event.registerEntityRenderer(Register.COLDSNAPSTABBER.get(), RenderColdSnapStabber::new);
        event.registerEntityRenderer(Register.COLDSNAPGUNNER.get(), RenderColdSnapGunner::new);
        event.registerEntityRenderer(Register.COLDSNAPSNOWBALLER.get(), RenderColdSnapSnowballer::new);
        event.registerEntityRenderer(Register.COLDSNAPGIFTER.get(), RenderColdSnapGifter::new);
        event.registerEntityRenderer(Register.COLDSNAPZAPPER.get(), RenderColdSnapZapper::new);
        event.registerEntityRenderer(Register.COLDSNAPBRAWLER.get(), RenderColdSnapBrawler::new);

        event.registerEntityRenderer(Register.NCOLDSNAPSTABBER.get(), RenderNetherColdSnapStabber::new);
        event.registerEntityRenderer(Register.NCOLDSNAPGUNNER.get(), RenderNetherColdSnapGunner::new);
        event.registerEntityRenderer(Register.NCOLDSNAPSNOWBALLER.get(), RenderNetherColdSnapSnowballer::new);
        event.registerEntityRenderer(Register.NCOLDSNAPGIFTER.get(), RenderNetherColdSnapGifter::new);
        event.registerEntityRenderer(Register.NCOLDSNAPZAPPER.get(), RenderNetherColdSnapZapper::new);
        event.registerEntityRenderer(Register.NCOLDSNAPBRAWLER.get(), RenderNetherColdSnapBrawler::new);

        event.registerEntityRenderer(Register.ECOLDSNAPSTABBER.get(), RenderColdSnapStabber::new);
        event.registerEntityRenderer(Register.ECOLDSNAPGUNNER.get(), RenderColdSnapGunner::new);
        event.registerEntityRenderer(Register.ECOLDSNAPSNOWBALLER.get(), RenderEndColdSnapSnowballer::new);
        event.registerEntityRenderer(Register.ECOLDSNAPGIFTER.get(), RenderColdSnapGifter::new);
        event.registerEntityRenderer(Register.ECOLDSNAPZAPPER.get(), RenderColdSnapZapper::new);
        event.registerEntityRenderer(Register.ECOLDSNAPBRAWLER.get(), RenderColdSnapBrawler::new);

        event.registerEntityRenderer(Register.PCOLDSNAPSTABBER.get(), RenderColdSnapStabber::new);
        event.registerEntityRenderer(Register.PCOLDSNAPGUNNER.get(), RenderColdSnapGunner::new);
        event.registerEntityRenderer(Register.PCOLDSNAPSNOWBALLER.get(), RenderColdSnapSnowballer::new);
        event.registerEntityRenderer(Register.PCOLDSNAPGIFTER.get(), RenderColdSnapGifter::new);
        event.registerEntityRenderer(Register.PCOLDSNAPZAPPER.get(), RenderColdSnapZapper::new);
        event.registerEntityRenderer(Register.PCOLDSNAPBRAWLER.get(), RenderColdSnapBrawler::new);

        event.registerEntityRenderer(Register.COLDSNAPCOW.get(), RenderColdSnapCow::new);
        event.registerEntityRenderer(Register.GUNNERPROJECTILE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(Register.LIGHTNINGSNOWBALLPROJECTILE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(Register.ROCKSNOWBALLPROJECTILE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(Register.SNOWIERSNOWBALLPROJECTILE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(Register.THROWNCHORUSPROJECTILE.get(), ThrownItemRenderer::new);

    }
    /*
For the functions: ConstructLayers and addLayertoPlayer, the following copyright notice applies.

Copyright (c) 2015, David Quintana <gigaherz@gmail.com>
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of the author nor the
      names of the contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

     */
    @SubscribeEvent
    public static void ConstructLayers(EntityRenderersEvent.AddLayers event)
    {
        addLayerToPlayer(event, "default");
        addLayerToPlayer(event, "slim");
    }



    private static void addLayerToPlayer(EntityRenderersEvent.AddLayers event, String skinName)
    {
        EntityRenderer<? extends Player> render = event.getSkin(skinName);
        if (render instanceof LivingEntityRenderer livingRenderer)
        {
            livingRenderer.addLayer(new TopHatLayer(livingRenderer));
        }
    }


}
