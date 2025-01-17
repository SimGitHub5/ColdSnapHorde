package com.cartoonishvillain.coldsnaphorde.items;

import com.cartoonishvillain.coldsnaphorde.ColdSnapHorde;
import com.cartoonishvillain.coldsnaphorde.Register;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static com.cartoonishvillain.coldsnaphorde.ColdSnapHorde.*;

public class Present extends Item {
    public Present(Properties properties) {super(properties);}

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        if(!worldIn.isClientSide() && handIn == InteractionHand.MAIN_HAND){
        playerIn.getCooldowns().addCooldown(this, 20);

        playerIn.getMainHandItem().shrink(1);

        float Total = 0f;
        for(float totaling : presentWeights) Total += totaling;
        float randomized = 0 + worldIn.random.nextFloat() * (Total-0);
        int select = 0;

        for(Float percentage : presentWeights){
            randomized -= percentage;
            if(randomized <= 0) break;
            else select++;
        }

        if(select == presentPossibilities.size()) select = select - 1;
        String selected = presentPossibilities.get(select);
        RewardDispenser(worldIn, playerIn, selected);}

        if(playerIn.getServer() != null) {
            giveAdvancement((ServerPlayer) playerIn, playerIn.getServer(), new ResourceLocation(ColdSnapHorde.MOD_ID, "unboxing"));
        }

        return super.use(worldIn, playerIn, handIn);
    }

    private void ItemSpawner(BlockPos pos, Level world, Item item, int low, int high){
        int bound = 0;
        if(low == high) {low = 0; bound = high;}
        else bound = world.random.nextInt(high - low);
        ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ()+0.5, new ItemStack(item, bound + low));
        world.addFreshEntity(itemEntity);
    }

    private void SpawnDispenser(Level world, Player playerEntity, Entity entity){
        if(entity instanceof Wolf wolfEntity){
            wolfEntity.tame(playerEntity);
            wolfEntity.setBaby(true);
            wolfEntity.setCollarColor(DyeColor.byId(world.random.nextInt(15)));
            wolfEntity.setPos(playerEntity.getX(), playerEntity.getY(), playerEntity.getZ());
            world.addFreshEntity(wolfEntity);
        }
        if(entity instanceof Cat catEntity){
            catEntity.tame(playerEntity);
            catEntity.setCatType(-1);
            catEntity.setBaby(true);
            catEntity.setCollarColor(DyeColor.byId(world.random.nextInt(15)));
            catEntity.setPos(playerEntity.getX(), playerEntity.getY(), playerEntity.getZ());
            world.addFreshEntity(catEntity);
        }
        if(entity instanceof Parrot parrotEntity){
            parrotEntity.tame(playerEntity);
            parrotEntity.setBaby(true);
            parrotEntity.setVariant(world.random.nextInt(4));
            parrotEntity.setPos(playerEntity.getX(), playerEntity.getY(), playerEntity.getZ());
            world.addFreshEntity(parrotEntity);
        }
        if(entity instanceof Horse horseEntity){
            horseEntity.tameWithName(playerEntity);
            horseEntity.setTamed(true);
            horseEntity.setPos(playerEntity.getX(), playerEntity.getY(), playerEntity.getZ());
            world.addFreshEntity(horseEntity);
        }
        if(entity instanceof Pig pigEntity){
            pigEntity.setPos(playerEntity.getX(), playerEntity.getY(), playerEntity.getZ());
            world.addFreshEntity(pigEntity);
        }
        if(entity instanceof Axolotl axolotl){
            axolotl.setBaby(true);
            axolotl.setPos(playerEntity.getX(), playerEntity.getY(), playerEntity.getZ());
            SynchedEntityData synchedEntityData = ObfuscationReflectionHelper.getPrivateValue(Entity.class, axolotl, "f_19804_");
            EntityDataAccessor<Integer> entityDataAccessor = ObfuscationReflectionHelper.getPrivateValue(Axolotl.class, axolotl, "f_149096_");
            synchedEntityData.set(entityDataAccessor, world.random.nextInt(5));
            world.addFreshEntity(axolotl);
        }
        if(entity instanceof Goat goat){
            goat.setBaby(true);
            goat.setPos(playerEntity.getX(), playerEntity.getY(), playerEntity.getZ());
            SynchedEntityData synchedEntityData = ObfuscationReflectionHelper.getPrivateValue(Entity.class, goat, "f_19804_");
            EntityDataAccessor<Boolean> entityDataAccessor = ObfuscationReflectionHelper.getPrivateValue(Goat.class, goat, "f_149347_");
            synchedEntityData.set(entityDataAccessor, true);
            world.addFreshEntity(goat);
        }
        if (entity instanceof Panda panda){
            panda.setBaby(true);
            panda.setPos(playerEntity.getX(), playerEntity.getY(), playerEntity.getZ());
            panda.setMainGene(Panda.Gene.getRandom(world.random));
            panda.setHiddenGene(Panda.Gene.getRandom(world.random));
            world.addFreshEntity(panda);
        }
    }

    private void RewardDispenser(Level world, Player playerEntity, String selected) {
        switch (selected) {
            case "snow":
                ItemSpawner(playerEntity.blockPosition(), world, Items.SNOW_BLOCK, 12, 28);
                break;
            case "ice":
                ItemSpawner(playerEntity.blockPosition(), world, Items.ICE, 15, 18);
                break;
            case "packedice":
                ItemSpawner(playerEntity.blockPosition(), world, Items.PACKED_ICE, 9, 12);
                break;
            case "blueice":
                ItemSpawner(playerEntity.blockPosition(), world, Items.BLUE_ICE, 3, 6);
                break;
            case "doggo":
                Wolf wolfEntity = new Wolf(EntityType.WOLF, world);
                SpawnDispenser(world, playerEntity, wolfEntity);
                break;
            case "cats":
                Cat catEntity = new Cat(EntityType.CAT, world);
                SpawnDispenser(world, playerEntity, catEntity);
                break;
            case "birb":
                Parrot parrotEntity = new Parrot(EntityType.PARROT, world);
                SpawnDispenser(world, playerEntity, parrotEntity);
                break;
            case "friendsnowman":
                SnowGolem snowGolemEntity = new SnowGolem(EntityType.SNOW_GOLEM, world);
                SpawnDispenser(world, playerEntity, snowGolemEntity);
                break;
            case "music":
                ItemSpawner(playerEntity.blockPosition(), world, MusicDisc(), 1, 1);
                break;
            default:
                ItemSpawner(playerEntity.blockPosition(), world, Items.COAL, 6, 15);
                break;
            case "rollercoaster":
                ItemSpawner(playerEntity.blockPosition(), world, Items.MINECART, 1, 1);
                ItemSpawner(playerEntity.blockPosition(), world, Items.RAIL, 10, 24);
                ItemSpawner(playerEntity.blockPosition(), world, Items.POWERED_RAIL, 1, 4);
                ItemSpawner(playerEntity.blockPosition(), world, Items.DETECTOR_RAIL, 1, 4);
                break;
            case "horse":
                Horse horseEntity = new Horse(EntityType.HORSE, world);
                SpawnDispenser(world, playerEntity, horseEntity);
                ItemSpawner(playerEntity.blockPosition(), world, Items.SADDLE, 1, 1);
                break;
            case "pig":
                Pig pigEntity = new Pig(EntityType.PIG, world);
                SpawnDispenser(world, playerEntity, pigEntity);
                ItemSpawner(playerEntity.blockPosition(), world, Items.SADDLE, 1, 1);
                ItemSpawner(playerEntity.blockPosition(), world, Items.CARROT_ON_A_STICK, 1, 1);
                break;
            case "candycane":
                if(world.random.nextInt() % 2 == 0) ItemSpawner(playerEntity.blockPosition(), world, Register.REDCANDYCANEITEM.get(), 10, 20);
                else ItemSpawner(playerEntity.blockPosition(), world, Register.GREENCANDYCANEITEM.get(), 10, 20);
                break;
            case "axolotl":
                Axolotl axolotl = new Axolotl(EntityType.AXOLOTL, world);
                SpawnDispenser(world, playerEntity, axolotl);
                ItemSpawner(playerEntity.blockPosition(), world, Items.WATER_BUCKET, 1, 1);
                break;
            case "screamgoat":
                Goat goat = new Goat(EntityType.GOAT, world);
                SpawnDispenser(world, playerEntity, goat);
                break;
            case "panda":
                Panda panda = new Panda(EntityType.PANDA, world);
                SpawnDispenser(world, playerEntity, panda);
                break;
            case "icesword":
                ItemSpawner(playerEntity.blockPosition(), world, Register.ICESWORD.get(), 1, 1);
                break;
            case "frostcore":
                ItemSpawner(playerEntity.blockPosition(), world, Register.ICECORE.get(), 1, 1);
                break;
            case "transposer":
                ItemSpawner(playerEntity.blockPosition(), world, Register.LIGHTNINGTRANSPOSER.get(), 1, 1);
                break;
            case "transposerpiece":
                ItemSpawner(playerEntity.blockPosition(), world, Register.LIGHTNINGTRANSPOSERPIECE.get(), 1, 2);
                break;
            case "frostshard":
                ItemSpawner(playerEntity.blockPosition(), world, Register.ICESHARD.get(), 4, 10);
                break;
        }
        world.playSound(null, playerEntity.blockPosition(), SoundEvents.CHICKEN_EGG, SoundSource.PLAYERS, 1f, 0.5f);
    }
    private Item MusicDisc(){
        ArrayList<Item> music = new ArrayList<Item>(Arrays.asList(Items.MUSIC_DISC_11, Items.MUSIC_DISC_13, Items.MUSIC_DISC_BLOCKS, Items.MUSIC_DISC_CAT, Items.MUSIC_DISC_CHIRP, Items.MUSIC_DISC_FAR, Items.MUSIC_DISC_MALL, Items.MUSIC_DISC_MELLOHI, Items.MUSIC_DISC_STAL, Items.MUSIC_DISC_STRAD, Items.MUSIC_DISC_WAIT, Items.MUSIC_DISC_WARD, Items.MUSIC_DISC_11, Items.MUSIC_DISC_13, Items.MUSIC_DISC_BLOCKS, Items.MUSIC_DISC_CAT, Items.MUSIC_DISC_CHIRP, Items.MUSIC_DISC_FAR, Items.MUSIC_DISC_MALL, Items.MUSIC_DISC_MELLOHI, Items.MUSIC_DISC_STAL, Items.MUSIC_DISC_STRAD, Items.MUSIC_DISC_WAIT, Items.MUSIC_DISC_WARD, Items.MUSIC_DISC_PIGSTEP, Items.MUSIC_DISC_OTHERSIDE));
        Random random = new Random();
        int select = random.nextInt(music.size());
        if(select == music.size()) select--;
        return music.get(select);
    }

    @Override
    public void appendHoverText(ItemStack p_41421_, @Nullable Level p_41422_, List<Component> p_41423_, TooltipFlag p_41424_) {
        super.appendHoverText(p_41421_, p_41422_, p_41423_, p_41424_);
        p_41423_.add(new TranslatableComponent("itemtooltip.present.1").withStyle(ChatFormatting.AQUA));
        p_41423_.add(new TranslatableComponent("itemtooltip.present.2").withStyle(ChatFormatting.AQUA));
        p_41423_.add(new TranslatableComponent("itemtooltip.present.3").withStyle(ChatFormatting.AQUA));
        if(ColdSnapHorde.isInHolidayWindow) {
            p_41423_.add(new TranslatableComponent("itemtooltip.present.special").withStyle(ChatFormatting.BLUE));
        }
    }
}
