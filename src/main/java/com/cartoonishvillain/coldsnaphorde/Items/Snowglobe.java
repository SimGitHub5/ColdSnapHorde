package com.cartoonishvillain.coldsnaphorde.Items;

import com.cartoonishvillain.coldsnaphorde.Capabilities.CooldownCapability;
import com.cartoonishvillain.coldsnaphorde.ColdSnapHorde;
import com.cartoonishvillain.coldsnaphorde.Events.Horde;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.concurrent.atomic.AtomicInteger;

public class Snowglobe extends Item {
    public Snowglobe(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        if(handIn == InteractionHand.MAIN_HAND && !worldIn.isClientSide() && playerIn != null) {
            if (worldIn.isAreaLoaded(playerIn.blockPosition(), 20) && (biomeCheck(worldIn, playerIn.blockPosition()) || worldIn.getBiome(playerIn.blockPosition()).getRegistryName().toString().contains("swamp") || worldIn.dimension().toString().contains("end"))) {
                AtomicInteger atomicInteger = new AtomicInteger(0);
                worldIn.getCapability(CooldownCapability.INSTANCE).ifPresent(h->{
                    if(h.getCooldownTicks() > 0){
                        atomicInteger.set(h.getCooldownTicks());
                    }
                });

                if(!(atomicInteger.get() > 0)) {
                    Horde horde = new Horde((ServerLevel) worldIn, playerIn.blockPosition(), (ServerPlayer) playerIn);
                    worldIn.getCapability(CooldownCapability.INSTANCE).ifPresent(h->{h.setCooldownTicks(ColdSnapHorde.sconfig.GLOBALHORDECOOLDOWN.get() * 20);});
                    playerIn.getMainHandItem().shrink(1);
                }else{
                    playerIn.displayClientMessage(new TextComponent("Horde on cooldown! Returning in: " + TimeBuilder(atomicInteger.get())), false);
                }
            }else{
                playerIn.displayClientMessage(new TextComponent("Temperature too hot for the horde to summon!"), false);
            }
        }
        return super.use(worldIn, playerIn, handIn);
    }

    private boolean biomeCheck(Level world, BlockPos pos){
        int protlvl = ColdSnapHorde.cconfig.HEATPROT.get();
        float temp = world.getBiome(pos).getBaseTemperature();
        int code = -1;
        if (temp < 0.3){code = 0;}
        else if(temp >= 0.3 && temp < 0.9){code = 1;}
        else if(temp >= 0.9 && temp < 1.5){code = 2;}
        else if(temp >= 1.5){code = 3;}

        return code <= protlvl;
    }

    private String TimeBuilder(int duration){
        String timer = "(";
        int timermath = duration/20;
        if (timermath >= 60){
            timer += Integer.toString(timermath/60);
            while(timermath >= 60){timermath -= 60;}
            timer += ":";
        }else{
            timer += "00:";
        }
        if (timermath > 9){
            timer += Integer.toString(timermath);
            timer += ")";
        } else{
            timer += "0";
            timer += Integer.toString(timermath);
            timer += ")";
        }
        return timer;
    }
}
