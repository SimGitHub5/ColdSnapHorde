package com.cartoonishvillain.coldsnaphorde.events;

import com.cartoonishvillain.coldsnaphorde.ColdSnapHorde;
import com.cartoonishvillain.coldsnaphorde.entities.mobs.basemob.*;
import com.cartoonishvillain.coldsnaphorde.entities.mobs.hordevariantmanager.EndHorde;
import com.cartoonishvillain.coldsnaphorde.entities.mobs.hordevariantmanager.NetherHorde;
import com.cartoonishvillain.coldsnaphorde.entities.mobs.hordevariantmanager.PlagueHorde;
import com.cartoonishvillain.coldsnaphorde.entities.mobs.hordevariantmanager.StandardHorde;
import com.cartoonishvillain.coldsnaphorde.Register;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public class Horde {
    private Optional<BlockPos> hordeSpawn = Optional.empty();
    private ServerLevel world;
    private BlockPos center;
    private Boolean hordeActive = false;
    private MinecraftServer server;
    private float Alive = 0;
    private float initAlive = 0;
    private float Active = 0;
    private int updateCenter = 0;
    private ServerPlayer serverPlayer;
    private ArrayList<ServerPlayer> players = new ArrayList<>();
    private ArrayList<GenericHordeMember> activeHordeMembers = new ArrayList<>();
    private final ServerBossEvent bossInfo = new ServerBossEvent(new TextComponent("Cold Snap Horde").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD), BossEvent.BossBarColor.BLUE, BossEvent.BossBarOverlay.NOTCHED_10);


    public Horde(MinecraftServer server) {
        this.server = server;
        for (ServerLevel serverWorld : server.getAllLevels()) {
            String Dimension = serverWorld.dimension().toString();
            //LOCKS CODE TO OVERWORLD
//            if(Dimension.contains("overworld")) {
//                serverWorld.getCapability(WorldCapability.INSTANCE).ifPresent(h -> {
//                    hordeActive = h.getCooldownTicks() == -1;
//                    if (hordeActive) {Alive = h.getAlive();}
//                    world = serverWorld;
//                });
//            }
        }
    }

    public void Stop() {
        this.bossInfo.setVisible(false);
        bossInfo.removeAllPlayers();
        hordeActive = false;
        Alive = 0;
        initAlive = 0;
        Active = 0;
        serverPlayer = null;
        activeHordeMembers.clear();
        center = null;
        players.clear();

        world.getCapability(ColdSnapHorde.WORLDCAPABILITYINSTANCE).ifPresent(h -> {
            h.setCooldownTicks(ColdSnapHorde.sconfig.GLOBALHORDECOOLDOWN.get() * 20);
        });
    }

    public Boolean getHordeActive() {
        return hordeActive;
    }

    public void SetUpHorde(ServerPlayer serverPlayerEntity) {
        world = serverPlayerEntity.getLevel();
        bossInfo.setCreateWorldFog(true);
        if (serverPlayerEntity.level.dimension().toString().contains("end")) {
            bossInfo.setColor(BossEvent.BossBarColor.PURPLE);
            bossInfo.setName(new TextComponent("Cold Snap Horde").withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD));
        } else if (serverPlayerEntity.level.dimension().toString().contains("nether")) {
            bossInfo.setColor(BossEvent.BossBarColor.RED);
            bossInfo.setName(new TextComponent("Cold Snap Horde").withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
        } else {
            bossInfo.setColor(BossEvent.BossBarColor.BLUE);
        }
        if (serverPlayerEntity.level.dimension().equals(world.dimension())) {
            serverPlayer = serverPlayerEntity;
            hordeActive = true;
            //Set alive counter based on difficulty
            switch (world.getDifficulty()) {
                case EASY:
                    Alive = ColdSnapHorde.sconfig.ALIVEEASY.get();
                    initAlive = ColdSnapHorde.sconfig.ALIVEEASY.get();
                    break;
                case NORMAL:
                    Alive = ColdSnapHorde.sconfig.ALIVENORMAL.get();
                    initAlive = ColdSnapHorde.sconfig.ALIVENORMAL.get();
                    break;
                case HARD:
                    Alive = ColdSnapHorde.sconfig.ALIVEHARD.get();
                    initAlive = ColdSnapHorde.sconfig.ALIVEHARD.get();
                    break;
            }
            center = serverPlayerEntity.blockPosition();

            world.getCapability(ColdSnapHorde.WORLDCAPABILITYINSTANCE).ifPresent(h -> {
                h.setCooldownTicks(-1);
            });
        }
    }

    //So when a player dies the serverPlayer would desync which is... a fun issue
    //This checks for the desync, returns false if the player should not count any longer.
    private boolean checkIfPlayerIsStillValid(ServerPlayer serverPlayer) {
        return serverPlayer.getHealth() != 0.0f;
    }

    public void tick() {
        if (hordeActive) {
            if (Alive > 0) {
                if (serverPlayer.level.dimensionType().equals(world.dimensionType()) && checkIfPlayerIsStillValid(serverPlayer) && !serverPlayer.isChangingDimension()) {
                    boolean flag = this.hordeActive;
                    if (this.world.getDifficulty() == Difficulty.PEACEFUL) {
                        this.Stop();
                        return;
                    }

                    if (Active != activeHordeMembers.size()) {
                        Active = activeHordeMembers.size();
                    }

                    this.bossInfo.setVisible(true);

//                    if (flag != this.hordeActive) {
//                        this.bossInfo.setVisible(this.hordeActive);
//                    }

                    if (Active < ColdSnapHorde.sconfig.HORDESIZE.get()) {
                        this.hordeSpawn = this.getValidSpawn(2);
                        if (!hordeSpawn.equals(Optional.empty()) && hordeSpawn.isPresent()) {
                            spawnSnowman(hordeSpawn.get());
                        }
                    }

                    if (updateCenter == 0) {
                        center = serverPlayer.blockPosition();
                        updateCenter = ColdSnapHorde.sconfig.UPDATETICK.get();
                        if (!world.dimension().toString().contains("nether") && !world.dimension().toString().contains("end")) {
                            if (world.getBiome(center).getRegistryName().toString().contains("swamp")) {
                                bossInfo.setColor(BossEvent.BossBarColor.GREEN);
                                bossInfo.setName(new TextComponent("Cold Snap Horde").withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD));
                            } else {
                                bossInfo.setColor(BossEvent.BossBarColor.BLUE);
                                bossInfo.setName(new TextComponent("Cold Snap Horde").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD));
                            }
                        }
                        updatePlayers();
                        updateHorde();
                    } else {
                        updateCenter--;
                    }

                    this.bossInfo.setProgress(Mth.clamp((float) (Alive / initAlive), 0.0f, 1f));

                } else {
                    //look for viable player, or cancel.
                    updatePlayers();
                    if (players.size() == 0) {
                        this.Stop();
                    } else {
                        bossInfo.removePlayer(serverPlayer);
                        serverPlayer = players.get(0);
                        players.remove(0);
                    }
                }
            } else {
                this.Stop();
            }
        }
    }

    private void updatePlayers() {
        for (ServerPlayer serverPlayer : server.getPlayerList().getPlayers()) {
            if (this.serverPlayer == serverPlayer) {
                bossInfo.addPlayer(serverPlayer);
                continue;
            }
            //player is not the tracked player and is in the same world as the tracked world.
            if (serverPlayer.level.dimensionType().equals(world.dimensionType()) && checkIfPlayerIsStillValid(serverPlayer)) {
                double distance = Mth.sqrt((float) serverPlayer.distanceToSqr(center.getX(), center.getY(), center.getZ()));
                if (distance < 64) {
                    if (!players.contains(serverPlayer)) {
                        bossInfo.addPlayer(serverPlayer);
                        players.add(serverPlayer);
                    }
                } else {
                    bossInfo.removePlayer(serverPlayer);
                    players.remove(serverPlayer);
                }

            } else {
                bossInfo.removePlayer(serverPlayer);
                players.remove(serverPlayer);
            }

        }
    }

    private void updateHorde() {
        ArrayList<GenericHordeMember> removals = new ArrayList<>();
        ArrayList<GenericHordeMember> additions = new ArrayList<>();
        for (GenericHordeMember hordeMember : activeHordeMembers) {
            if (!hordeMember.isHordeMember()) {
                removals.add(hordeMember);
                UnitLost();
            }

            if (hordeMember.isDeadOrDying()) {
                removals.add(hordeMember);
                UnitDown();
            }

            hordeMember.updateHordeMember(center);
            BlockPos hordeTarget = hordeMember.getLoc();
            if (Mth.sqrt((float) hordeMember.distanceToSqr(hordeTarget.getX(), hordeTarget.getY(), hordeTarget.getZ())) > 64) {
                hordeMember.cancelHordeMembership();
                removals.add(hordeMember);
                UnitLost();
            }


            inviteNearbySnowmentoHorde(hordeMember, additions);
        }
        for (GenericHordeMember removal : removals) {
            activeHordeMembers.remove(removal);
        }
        activeHordeMembers.addAll(additions);
        removals.clear();
    }

    private void inviteNearbySnowmentoHorde(GenericHordeMember Member, ArrayList<GenericHordeMember> additions) {
        List<GenericHordeMember> list = Member.level.getEntitiesOfClass(GenericHordeMember.class, Member.getBoundingBox().inflate(8));
        for (GenericHordeMember snowman : list) {
            if (Member.getLoc() != null) {
                if (!snowman.isHordeMember() && snowman.getTarget() == null) {
                    snowman.toggleHordeMember(Member.getLoc());
                    additions.add(snowman);
                    InviteUnit();
                }
            }
        }
    }

    private Optional<BlockPos> getValidSpawn(int var) {
        for (int i = 0; i < 3; ++i) {
            BlockPos blockPos = this.findRandomSpawnPos(var, 1);
            if (blockPos != null) return Optional.of(blockPos);
        }
        return Optional.empty();
    }

    private BlockPos findRandomSpawnPos(int logicvar, int loopvar) {
        int i = logicvar == 0 ? 2 : 2 - logicvar;
        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();

        for (int a = 0; a < loopvar; ++a) {
            float f = this.world.random.nextFloat() * ((float) Math.PI * 2F);
            double DISTANCE = -1;
            int j = Integer.MAX_VALUE, l = Integer.MAX_VALUE;
            while ((DISTANCE == -1 || !(DISTANCE > 450 && DISTANCE < 1250)) || !biomeCheck(world, new BlockPos(j, center.getY(), l))) { //check for appropriate distance from start and proper biome
                j = randFinder(this.center.getX(), f, i);
                l = randFinder(this.center.getZ(), f, i);
                DISTANCE = center.distSqr(new BlockPos(j, center.getY(), l));
            }

//            int k = this.world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, j, l);
            int k = findYPosition(j, l);
            if (k != -1) {
                blockPos.set(j, k, l);
                if (this.world.isAreaLoaded(blockPos, 20)) return blockPos;
            }
        }
        return null;
    }

    private int findYPosition(int j, int l) {
        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
        //Look Higher first, it is the preferable option
        for (int i = center.getY(); i < center.getY() + 25; i++) {
            blockPos.set(j, i - 1, l);
            BlockState blockstate = world.getBlockState(blockPos);
            if (blockstate.canOcclude() && !(blockstate.getBlock() instanceof LeavesBlock) && !(blockstate.equals(Blocks.BEDROCK.defaultBlockState()))) {
                blockPos.set(j, i, l);
                blockstate = world.getBlockState(blockPos);
                if (blockstate.equals(Blocks.AIR.defaultBlockState())) {
                    blockPos.set(j, i + 1, l);
                    blockstate = world.getBlockState(blockPos);
                    if (blockstate.equals(Blocks.AIR.defaultBlockState())) {
                        return i;
                    }
                }
            }
        }

        //if no spot above, begrudgingly look below. Not too deep
        for (int i = center.getY(); i > center.getY() - 15; i--) {
            blockPos.set(j, i - 1, l);
            BlockState blockstate = world.getBlockState(blockPos);
            if (blockstate.canOcclude() && !(blockstate.getBlock() instanceof LeavesBlock) && !(blockstate.equals(Blocks.BEDROCK.defaultBlockState()))) {
                blockPos.set(j, i, l);
                blockstate = world.getBlockState(blockPos);
                if (blockstate.equals(Blocks.AIR.defaultBlockState())) {
                    blockPos.set(j, i + 1, l);
                    blockstate = world.getBlockState(blockPos);
                    if (blockstate.equals(Blocks.AIR.defaultBlockState())) {
                        return i;
                    }
                }
            }
        }

        return -1;
    }

    private int randFinder(int centercoord, float f, int i) {
        return centercoord + (this.world.random.nextInt(25 + 25) - 25);
    }

    public void SpawnUnit() {
        Active++;
    }

    public void InviteUnit() {
        Active++;
    }

    //when a horde member dies
    public void UnitDown() {
        Active--;
        Alive--;
    }

    //when a horde member loses range.
    public void UnitLost() {
        Active--;
    }


    private boolean biomeCheck(ServerLevel world, BlockPos pos) {
        if (world.getBiome(pos).getRegistryName().toString().contains("swamp")) {
            return true;
        }
        if (!world.dimension().toString().contains("over")) {
            return true;
        }
        int protlvl = ColdSnapHorde.cconfig.HEATPROT.get();
        float temp = world.getBiome(pos).getBaseTemperature();
        int code = -1;
        if (temp < 0.3) {
            code = 0;
        } else if (temp >= 0.3 && temp < 0.9) {
            code = 1;
        } else if (temp >= 0.9 && temp < 1.5) {
            code = 2;
        } else if (temp >= 1.5) {
            code = 3;
        }

        return code <= protlvl;
    }

    private boolean trueBiomeCheck(ServerLevel world, BlockPos pos) {
        int protlvl = ColdSnapHorde.cconfig.HEATPROT.get();
        float temp = world.getBiome(pos).getBaseTemperature();
        int code = -1;
        if (temp < 0.3) {
            code = 0;
        } else if (temp >= 0.3 && temp < 0.9) {
            code = 1;
        } else if (temp >= 0.9 && temp < 1.5) {
            code = 2;
        } else if (temp >= 1.5) {
            code = 3;
        }

        return code <= protlvl;
    }


    private void spawnSnowman(BlockPos pos) {
        ArrayList<Integer> SpawnWeights = new ArrayList<>();
        SpawnWeights.add(ColdSnapHorde.cconfig.GUNNER.get());
        SpawnWeights.add(ColdSnapHorde.cconfig.STABBER.get());
        SpawnWeights.add(ColdSnapHorde.cconfig.SNOWBALLER.get());
        SpawnWeights.add(ColdSnapHorde.cconfig.ZAPPER.get());
        SpawnWeights.add(ColdSnapHorde.cconfig.GIFTER.get());
        SpawnWeights.add(ColdSnapHorde.cconfig.BRAWLER.get());
        int combined = 0;
        for (Integer weight : SpawnWeights) combined += weight;
        Random random = new Random();
        int rng = random.nextInt(combined);
        int selected = -1;
        int counter = 0;
        for (Integer weights : SpawnWeights) {
            if ((rng - weights) <= 0) {
                selected = counter;
                break;
            } else counter++;
            rng -= weights;
        }

        switch (selected) {
            case 0:
                ColdSnapGunner coldSnapGunner = gunnerSpawnRules(world, pos);
                coldSnapGunner.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                coldSnapGunner.toggleHordeMember(center);
                world.addFreshEntity(coldSnapGunner);
                activeHordeMembers.add(coldSnapGunner);
                break;
            case 1:
                ColdSnapStabber coldSnapStabber = stabberSpawnRules(world, pos);
                coldSnapStabber.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                coldSnapStabber.toggleHordeMember(center);
                world.addFreshEntity(coldSnapStabber);
                activeHordeMembers.add(coldSnapStabber);
                break;
            case 2:
                ColdSnapSnowballer coldSnapSnowballer = snowballerSpawnRules(world, pos);
                coldSnapSnowballer.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                coldSnapSnowballer.toggleHordeMember(center);
                world.addFreshEntity(coldSnapSnowballer);
                activeHordeMembers.add(coldSnapSnowballer);
                break;
            case 3:
                ColdSnapZapper coldSnapZapper = zapperSpawnRules(world, pos);
                coldSnapZapper.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                coldSnapZapper.toggleHordeMember(center);
                world.addFreshEntity(coldSnapZapper);
                activeHordeMembers.add(coldSnapZapper);
                break;
            case 4:
                ColdSnapGifter coldSnapGifter = gifterSpawnRules(world, pos);
                coldSnapGifter.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                coldSnapGifter.toggleHordeMember(center);
                world.addFreshEntity(coldSnapGifter);
                activeHordeMembers.add(coldSnapGifter);
                break;
            case 5:
                ColdSnapBrawler coldSnapBrawler = brawlerSpawnRules(world, pos);
                coldSnapBrawler.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                coldSnapBrawler.toggleHordeMember(center);
                world.addFreshEntity(coldSnapBrawler);
                activeHordeMembers.add(coldSnapBrawler);
                break;
        }
    }


    private ColdSnapGunner gunnerSpawnRules(ServerLevel world, BlockPos pos) {
        ColdSnapGunner coldSnapGunner = null;
        String BiomeName = world.getBiome(pos).getRegistryName().toString();
        if (BiomeName.contains("swamp")) {
            coldSnapGunner = new PlagueHorde.PlagueGunner(Register.PCOLDSNAPGUNNER.get(), world);
        } else if (world.dimension().toString().contains("end")) {
            coldSnapGunner = new EndHorde.EndGunner(Register.ECOLDSNAPGUNNER.get(), world);
        } else if (world.dimension().toString().contains("nether")) {
            coldSnapGunner = new EndHorde.EndGunner(Register.NCOLDSNAPGUNNER.get(), world);
        } else if (trueBiomeCheck(world, pos)) {
            if (!world.isRainingAt(pos)) {
                Random random = new Random();
                int chance = random.nextInt(100);
                if (chance <= 5) {
                    coldSnapGunner = new NetherHorde.NetherGunner(Register.NCOLDSNAPGUNNER.get(), world);
                }
                chance = random.nextInt(100);
                if (chance <= 5 && coldSnapGunner == null)
                    coldSnapGunner = new EndHorde.EndGunner(Register.ECOLDSNAPGUNNER.get(), world);
                chance = random.nextInt(100);
                if (chance <= 5 && coldSnapGunner == null)
                    coldSnapGunner = new PlagueHorde.PlagueGunner(Register.PCOLDSNAPGUNNER.get(), world);
                if (coldSnapGunner == null)
                    coldSnapGunner = new StandardHorde.StandardGunner(Register.COLDSNAPGUNNER.get(), world);
            } else {
                Random random = new Random();
                int chance = random.nextInt(150);
                if (chance <= 10 && coldSnapGunner == null)
                    coldSnapGunner = new PlagueHorde.PlagueGunner(Register.PCOLDSNAPGUNNER.get(), world);
                if (coldSnapGunner == null)
                    coldSnapGunner = new StandardHorde.StandardGunner(Register.COLDSNAPGUNNER.get(), world);
            }

        } else coldSnapGunner = new NetherHorde.NetherGunner(Register.NCOLDSNAPGUNNER.get(), world);
        return coldSnapGunner;
    }

    private ColdSnapStabber stabberSpawnRules(ServerLevel world, BlockPos pos) {
        ColdSnapStabber coldSnapStabber = null;
        String BiomeName = world.getBiome(pos).getRegistryName().toString();
        if (BiomeName.contains("swamp")) {
            coldSnapStabber = new PlagueHorde.PlagueStabber(Register.PCOLDSNAPSTABBER.get(), world);
        } else if (world.dimension().toString().contains("end")) {
            coldSnapStabber = new EndHorde.EndStabber(Register.ECOLDSNAPSTABBER.get(), world);
        } else if (world.dimension().toString().contains("nether")) {
            coldSnapStabber = new NetherHorde.NetherStabber(Register.NCOLDSNAPSTABBER.get(), world);
        } else if (trueBiomeCheck(world, pos)) {
            if (!world.isRainingAt(pos)) {
                Random random = new Random();
                int chance = random.nextInt(100);
                if (chance <= 5) {
                    coldSnapStabber = new NetherHorde.NetherStabber(Register.NCOLDSNAPSTABBER.get(), world);
                }
                chance = random.nextInt(100);
                if (chance <= 5 && coldSnapStabber == null)
                    coldSnapStabber = new EndHorde.EndStabber(Register.ECOLDSNAPSTABBER.get(), world);
                chance = random.nextInt(100);
                if (chance <= 5 && coldSnapStabber == null)
                    coldSnapStabber = new PlagueHorde.PlagueStabber(Register.PCOLDSNAPSTABBER.get(), world);
                if (coldSnapStabber == null)
                    coldSnapStabber = new StandardHorde.StandardStabber(Register.COLDSNAPSTABBER.get(), world);
            } else {
                Random random = new Random();
                int chance = random.nextInt(150);
                if (chance <= 10 && coldSnapStabber == null)
                    coldSnapStabber = new PlagueHorde.PlagueStabber(Register.PCOLDSNAPSTABBER.get(), world);
                if (coldSnapStabber == null)
                    coldSnapStabber = new StandardHorde.StandardStabber(Register.COLDSNAPSTABBER.get(), world);
            }

        } else coldSnapStabber = new NetherHorde.NetherStabber(Register.NCOLDSNAPSTABBER.get(), world);
        return coldSnapStabber;
    }


    private ColdSnapSnowballer snowballerSpawnRules(ServerLevel world, BlockPos pos) {
        ColdSnapSnowballer coldSnapSnowballer = null;
        String BiomeName = world.getBiome(pos).getRegistryName().toString();
        if (BiomeName.contains("swamp")) {
            coldSnapSnowballer = new PlagueHorde.PlagueSnowballer(Register.PCOLDSNAPSNOWBALLER.get(), world);
        } else if (world.dimension().toString().contains("end")) {
            coldSnapSnowballer = new EndHorde.EndSnowballer(Register.ECOLDSNAPSNOWBALLER.get(), world);
        } else if (world.dimension().toString().contains("nether")) {
            coldSnapSnowballer = new NetherHorde.NetherSnowballer(Register.NCOLDSNAPSNOWBALLER.get(), world);
        }
        else if (trueBiomeCheck(world, pos)) {
                if (!world.isRainingAt(pos)) {
                    Random random = new Random();
                    int chance = random.nextInt(100);
                    if (chance <= 5) {
                        coldSnapSnowballer = new NetherHorde.NetherSnowballer(Register.NCOLDSNAPSNOWBALLER.get(), world);}
                    chance = random.nextInt(100);
                    if (chance <= 5 && coldSnapSnowballer == null)
                        coldSnapSnowballer = new EndHorde.EndSnowballer(Register.ECOLDSNAPSNOWBALLER.get(), world);
                    chance = random.nextInt(100);
                    if (chance <= 5 && coldSnapSnowballer == null)
                        coldSnapSnowballer = new PlagueHorde.PlagueSnowballer(Register.PCOLDSNAPSNOWBALLER.get(), world);
                    if (coldSnapSnowballer == null)
                        coldSnapSnowballer = new StandardHorde.StandardSnowballer(Register.COLDSNAPSNOWBALLER.get(), world);
                } else {
                    Random random = new Random();
                    int chance = random.nextInt(150);
                    if (chance <= 10 && coldSnapSnowballer == null)
                        coldSnapSnowballer = new PlagueHorde.PlagueSnowballer(Register.PCOLDSNAPSNOWBALLER.get(), world);
                    if (coldSnapSnowballer == null)
                        coldSnapSnowballer = new StandardHorde.StandardSnowballer(Register.COLDSNAPSNOWBALLER.get(), world);
                }

            } else coldSnapSnowballer = new NetherHorde.NetherSnowballer(Register.NCOLDSNAPSNOWBALLER.get(), world);
            return coldSnapSnowballer;
        }

    private ColdSnapGifter gifterSpawnRules(ServerLevel world, BlockPos pos){
        ColdSnapGifter coldSnapGifter = null;
        String BiomeName = world.getBiome(pos).getRegistryName().toString();
        if (BiomeName.contains("swamp")){
            coldSnapGifter = new PlagueHorde.PlagueGifter(Register.PCOLDSNAPGIFTER.get(), world);
        }else if(world.dimension().toString().contains("end")){
            coldSnapGifter = new EndHorde.EndGifter(Register.ECOLDSNAPGIFTER.get(), world);
        }else if(world.dimension().toString().contains("nether")){
            coldSnapGifter = new NetherHorde.NetherGifter(Register.NCOLDSNAPGIFTER.get(), world);
        } else if(trueBiomeCheck(world, pos)){
            if(!world.isRainingAt(pos)){
                Random random = new Random();
                int chance = random.nextInt(100);
                if(chance <= 5){coldSnapGifter = new NetherHorde.NetherGifter(Register.NCOLDSNAPGIFTER.get(), world);}
                chance = random.nextInt(100);
                if(chance <= 5 && coldSnapGifter == null) coldSnapGifter = new EndHorde.EndGifter(Register.ECOLDSNAPGIFTER.get(), world);
                chance = random.nextInt(100);
                if(chance <= 5 && coldSnapGifter == null) coldSnapGifter = new PlagueHorde.PlagueGifter(Register.PCOLDSNAPGIFTER.get(), world);
                if(coldSnapGifter == null) coldSnapGifter = new StandardHorde.StandardGifter(Register.COLDSNAPGIFTER.get(), world);
            }else {
                Random random = new Random();
                int chance = random.nextInt(150);
                if(chance <= 10 && coldSnapGifter == null) coldSnapGifter = new PlagueHorde.PlagueGifter(Register.PCOLDSNAPGIFTER.get(), world);
                if(coldSnapGifter == null) coldSnapGifter = new StandardHorde.StandardGifter(Register.COLDSNAPGIFTER.get(), world);
            }

        }
        else coldSnapGifter = new NetherHorde.NetherGifter(Register.NCOLDSNAPGIFTER.get(), world);
        return coldSnapGifter;
    }

    private ColdSnapZapper zapperSpawnRules(ServerLevel world, BlockPos pos){
        ColdSnapZapper coldSnapZapper = null;
        String BiomeName = world.getBiome(pos).getRegistryName().toString();
        if (BiomeName.contains("swamp")){coldSnapZapper = new PlagueHorde.PlagueZapper(Register.PCOLDSNAPZAPPER.get(), world);
        }else if(world.dimension().toString().contains("end")){
            coldSnapZapper = new EndHorde.EndZapper(Register.ECOLDSNAPZAPPER.get(), world);
        }else if(world.dimension().toString().contains("nether")){
            coldSnapZapper = new NetherHorde.NetherZapper(Register.NCOLDSNAPZAPPER.get(), world);
        }else if(trueBiomeCheck(world, pos)){
            if(!world.isRainingAt(pos)){
                Random random = new Random();
                int chance = random.nextInt(100);
                if(chance <= 5){coldSnapZapper = new NetherHorde.NetherZapper(Register.NCOLDSNAPZAPPER.get(), world);}
                chance = random.nextInt(100);
                if(chance <= 5 && coldSnapZapper == null) coldSnapZapper = new EndHorde.EndZapper(Register.ECOLDSNAPZAPPER.get(), world);
                chance = random.nextInt(100);
                if(chance <= 5 && coldSnapZapper == null) coldSnapZapper = new PlagueHorde.PlagueZapper(Register.PCOLDSNAPZAPPER.get(), world);
                if(coldSnapZapper == null) coldSnapZapper = new StandardHorde.StandardZapper(Register.COLDSNAPZAPPER.get(), world);
            }else {
                Random random = new Random();
                int chance = random.nextInt(150);
                if(chance <= 10 && coldSnapZapper == null) coldSnapZapper = new PlagueHorde.PlagueZapper(Register.PCOLDSNAPZAPPER.get(), world);
                if(coldSnapZapper == null) coldSnapZapper = new StandardHorde.StandardZapper(Register.COLDSNAPZAPPER.get(), world);
            }

        }
        else coldSnapZapper = new NetherHorde.NetherZapper(Register.NCOLDSNAPZAPPER.get(), world);
        return coldSnapZapper;
    }

    private ColdSnapBrawler brawlerSpawnRules(ServerLevel world, BlockPos pos){
        ColdSnapBrawler coldSnapBrawler = null;
        String BiomeName = world.getBiome(pos).getRegistryName().toString();
        if (BiomeName.contains("swamp")){
            coldSnapBrawler = new PlagueHorde.PlagueBrawler(Register.PCOLDSNAPBRAWLER.get(), world);
        }else if(world.dimension().toString().contains("end")){
            coldSnapBrawler = new EndHorde.EndBrawler(Register.ECOLDSNAPBRAWLER.get(), world);
        }else if(world.dimension().toString().contains("nether")){
            coldSnapBrawler = new NetherHorde.NetherBrawler(Register.NCOLDSNAPBRAWLER.get(), world);
        } else if(trueBiomeCheck(world, pos)){
            if(!world.isRainingAt(pos)){
                Random random = new Random();
                int chance = random.nextInt(100);
                if(chance <= 5){coldSnapBrawler = new NetherHorde.NetherBrawler(Register.NCOLDSNAPBRAWLER.get(), world);}
                chance = random.nextInt(100);
                if(chance <= 5 && coldSnapBrawler == null) coldSnapBrawler = new EndHorde.EndBrawler(Register.ECOLDSNAPBRAWLER.get(), world);
                chance = random.nextInt(100);
                if(chance <= 5 && coldSnapBrawler == null) coldSnapBrawler = new PlagueHorde.PlagueBrawler(Register.PCOLDSNAPBRAWLER.get(), world);
                if(coldSnapBrawler == null) coldSnapBrawler = new StandardHorde.StandardBrawler(Register.COLDSNAPBRAWLER.get(), world);
            }else {
                Random random = new Random();
                int chance = random.nextInt(150);
                if(chance <= 10 && coldSnapBrawler == null) coldSnapBrawler = new PlagueHorde.PlagueBrawler(Register.PCOLDSNAPBRAWLER.get(), world);
                if(coldSnapBrawler == null) coldSnapBrawler = new StandardHorde.StandardBrawler(Register.COLDSNAPBRAWLER.get(), world);
            }

        }
        else coldSnapBrawler = new NetherHorde.NetherBrawler(Register.NCOLDSNAPBRAWLER.get(), world);
        return coldSnapBrawler;
    }



    private void broadcast(MinecraftServer server, TextComponent translationTextComponent){
        server.getPlayerList().broadcastMessage(translationTextComponent, ChatType.CHAT, UUID.randomUUID());
    }

}