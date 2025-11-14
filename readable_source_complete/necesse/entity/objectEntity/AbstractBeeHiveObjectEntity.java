/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import necesse.engine.GameTileRange;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.HoneyBeeMob;
import necesse.entity.mobs.friendly.QueenBeeMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.InventoryItem;
import necesse.level.gameObject.FruitTreeObject;
import necesse.level.maps.Level;

public abstract class AbstractBeeHiveObjectEntity
extends ObjectEntity {
    public static int secondsToProduceHoneyPerBee = 10800;
    public static float frameProductionMultiplier = 2.0f;
    public static int frameLifespanSeconds = 1200;
    public static int workerBeeLifespanSeconds = 1200;
    public static int minCooldownForRoamingBee = 2000;
    public static int maxCooldownForRoamingBee = 15000;
    public static int maxRoamingBeesForSpawnTileRange = 14;
    public static int maxRoamingBeesCloseForSpawn = 30;
    public static GameTileRange pollinateTileRange = new GameTileRange(25, new Point[0]);
    public static GameTileRange migrateTileRange = new GameTileRange(5, 50, new Point[0]);
    public long lastProductionTickWorldTime;
    public boolean hasQueen;
    public ProductionTimer honey;
    public ProductionTimer frames;
    public ProductionTimer bees;
    public HashSet<Integer> beesOutsideApiaryUniqueIDs = new HashSet();
    public long nextRoamingBeeSpawnTime;

    public AbstractBeeHiveObjectEntity(Level level, String type, int x, int y) {
        super(level, type, x, y);
        this.lastProductionTickWorldTime = this.getWorldEntity().getWorldTime();
        this.nextRoamingBeeSpawnTime = this.getWorldEntity().getTime() + (long)GameRandom.globalRandom.getIntBetween(minCooldownForRoamingBee, maxCooldownForRoamingBee);
        this.honey = new ProductionTimer("honey", 0){

            @Override
            public int getSecondsForNextProduction() {
                if (AbstractBeeHiveObjectEntity.this.honey.amount < AbstractBeeHiveObjectEntity.this.getMaxStoredHoney() && AbstractBeeHiveObjectEntity.this.bees.amount > 0) {
                    float modifier = AbstractBeeHiveObjectEntity.this.frames.amount > 0 ? frameProductionMultiplier : 1.0f;
                    return (int)((float)(secondsToProduceHoneyPerBee / AbstractBeeHiveObjectEntity.this.bees.amount) / modifier);
                }
                return -1;
            }
        };
        this.frames = new ProductionTimer("frame", 0){

            @Override
            public int getSecondsForNextProduction() {
                if (AbstractBeeHiveObjectEntity.this.frames.amount > 0 && AbstractBeeHiveObjectEntity.this.bees.amount > 0) {
                    return frameLifespanSeconds;
                }
                return -1;
            }

            @Override
            public boolean shouldResetBufferOnNoProduction() {
                return AbstractBeeHiveObjectEntity.this.frames.amount <= 0 || AbstractBeeHiveObjectEntity.this.bees.amount <= 0;
            }

            @Override
            public void onProductionTick() {
                AbstractBeeHiveObjectEntity.this.makeProductionChange(this, () -> --this.amount);
            }
        };
        this.bees = new ProductionTimer("bee", 0){

            @Override
            public int getSecondsForNextProduction() {
                if (AbstractBeeHiveObjectEntity.this.hasQueen && (AbstractBeeHiveObjectEntity.this.bees.amount < AbstractBeeHiveObjectEntity.this.getMaxBees() || AbstractBeeHiveObjectEntity.this.canCreateQueens())) {
                    return (int)((float)workerBeeLifespanSeconds * Math.max(1.0f, (float)AbstractBeeHiveObjectEntity.this.bees.amount) / ((float)AbstractBeeHiveObjectEntity.this.getMaxBees() / 2.0f));
                }
                if (!AbstractBeeHiveObjectEntity.this.hasQueen && AbstractBeeHiveObjectEntity.this.bees.amount > 0 || AbstractBeeHiveObjectEntity.this.bees.amount > AbstractBeeHiveObjectEntity.this.getMaxBees()) {
                    return workerBeeLifespanSeconds / AbstractBeeHiveObjectEntity.this.bees.amount;
                }
                return -1;
            }

            @Override
            public void onProductionTick() {
                if (AbstractBeeHiveObjectEntity.this.hasQueen && AbstractBeeHiveObjectEntity.this.canCreateQueens() && AbstractBeeHiveObjectEntity.this.bees.amount == AbstractBeeHiveObjectEntity.this.getMaxBees()) {
                    if (AbstractBeeHiveObjectEntity.this.isServer() && AbstractBeeHiveObjectEntity.this.getLevel().isLoadingComplete() && GameRandom.globalRandom.getChance(AbstractBeeHiveObjectEntity.this.getQueenBeeChance())) {
                        AbstractBeeHiveObjectEntity.this.tryMigratingQueenBee();
                    }
                } else {
                    AbstractBeeHiveObjectEntity.this.makeProductionChange(this, () -> {
                        this.amount = AbstractBeeHiveObjectEntity.this.hasQueen ? ++this.amount : --this.amount;
                    });
                }
            }
        };
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addLong("lastProductionTickWorldTime", this.lastProductionTickWorldTime);
        save.addBoolean("hasQueen", this.hasQueen);
        this.honey.addSaveData("honey", save);
        this.bees.addSaveData("bee", save);
        this.frames.addSaveData("frame", save);
        save.addIntArray("beesOutsideApiaryUniqueIDs", this.beesOutsideApiaryUniqueIDs.stream().mapToInt(i -> i).toArray());
        save.addLong("nextRoamingBeeSpawnTime", this.nextRoamingBeeSpawnTime);
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.lastProductionTickWorldTime = save.getLong("lastProductionTickWorldTime", this.lastProductionTickWorldTime);
        this.hasQueen = save.getBoolean("hasQueen", this.hasQueen);
        this.honey.applyLoadData("honey", save);
        this.bees.applyLoadData("bee", save);
        this.frames.applyLoadData("frame", save);
        int[] loadedBeesOutsideApiaryUniqueIDs = save.getIntArray("beesOutsideApiaryUniqueIDs", new int[0]);
        this.beesOutsideApiaryUniqueIDs.clear();
        for (int beeUniqueID : loadedBeesOutsideApiaryUniqueIDs) {
            this.beesOutsideApiaryUniqueIDs.add(beeUniqueID);
        }
        this.nextRoamingBeeSpawnTime = save.getLong("nextRoamingBeeSpawnTime", this.nextRoamingBeeSpawnTime);
    }

    @Override
    public void setupContentPacket(PacketWriter writer) {
        super.setupContentPacket(writer);
        writer.putNextLong(this.lastProductionTickWorldTime);
        writer.putNextBoolean(this.hasQueen);
        this.honey.writePacket(writer);
        this.bees.writePacket(writer);
        this.frames.writePacket(writer);
    }

    @Override
    public void applyContentPacket(PacketReader reader) {
        super.applyContentPacket(reader);
        this.lastProductionTickWorldTime = reader.getNextLong();
        this.hasQueen = reader.getNextBoolean();
        this.honey.readPacket(reader);
        this.bees.readPacket(reader);
        this.frames.readPacket(reader);
    }

    @Override
    public void clientTick() {
        super.clientTick();
        this.tickProduction(false);
    }

    @Override
    public void serverTick() {
        super.serverTick();
        this.tickProduction(false);
        this.tickRoamingBeeSpawn();
    }

    public void tickProduction(boolean forceTick) {
        long timePassedSinceLastProductionTick = this.getWorldEntity().getWorldTime() - this.lastProductionTickWorldTime;
        long secondsPassedSinceLastProductionTick = timePassedSinceLastProductionTick / 1000L;
        if (secondsPassedSinceLastProductionTick > 0L || forceTick) {
            ProductionTimer[] timers = new ProductionTimer[]{this.honey, this.frames, this.bees};
            boolean shouldTick = false;
            int[] secondsForNext = new int[timers.length];
            for (int i = 0; i < timers.length; ++i) {
                int seconds;
                secondsForNext[i] = seconds = timers[i].getSecondsForNextProduction();
                if (seconds >= 0) {
                    shouldTick = true;
                    continue;
                }
                if (!timers[i].shouldResetBufferOnNoProduction()) continue;
                timers[i].buffer = 0;
            }
            if (shouldTick) {
                int[] secondsForNextExBuffer = new int[timers.length];
                int best = 0;
                for (int i = 0; i < timers.length; ++i) {
                    int secondsExBuffer;
                    int seconds = secondsForNext[i];
                    if (seconds < 0) {
                        secondsForNextExBuffer[i] = Integer.MAX_VALUE;
                        continue;
                    }
                    secondsForNextExBuffer[i] = secondsExBuffer = secondsForNext[i] - timers[i].buffer;
                    if (secondsExBuffer >= secondsForNextExBuffer[best]) continue;
                    best = i;
                }
                if ((long)secondsForNextExBuffer[best] <= secondsPassedSinceLastProductionTick) {
                    int secondsPassed = Math.max(0, secondsForNextExBuffer[best]);
                    timers[best].buffer = 0;
                    timers[best].onProductionTick();
                    this.lastProductionTickWorldTime += (long)secondsPassed * 1000L;
                    for (int i = 0; i < timers.length; ++i) {
                        if (i == best) continue;
                        timers[i].buffer += secondsPassed;
                    }
                    this.tickProduction(true);
                } else {
                    for (ProductionTimer timer : timers) {
                        timer.buffer = (int)((long)timer.buffer + secondsPassedSinceLastProductionTick);
                    }
                    this.lastProductionTickWorldTime += secondsPassedSinceLastProductionTick * 1000L;
                }
            } else {
                this.lastProductionTickWorldTime = this.getWorldEntity().getWorldTime();
            }
        }
    }

    public void tickRoamingBeeSpawn() {
        if (this.nextRoamingBeeSpawnTime <= this.getWorldEntity().getTime()) {
            long surroundingBees;
            this.nextRoamingBeeSpawnTime = this.getWorldEntity().getTime() + (long)GameRandom.globalRandom.getIntBetween(minCooldownForRoamingBee / 4, maxCooldownForRoamingBee / 4);
            if (this.getWorldEntity().isNight()) {
                return;
            }
            if (this.getLevel().weatherLayer.isRaining() && this.getLevel().isOutside(this.tileX, this.tileY)) {
                return;
            }
            this.removeInvalidRoamingBees();
            int beesInsideApiary = this.bees.amount - this.beesOutsideApiaryUniqueIDs.size();
            if (beesInsideApiary > 0 && (surroundingBees = this.getLevel().entityManager.mobs.getInRegionByTileRange(this.tileX, this.tileY, maxRoamingBeesForSpawnTileRange).stream().filter(m -> m instanceof HoneyBeeMob).filter(m -> GameMath.diagonalMoveDistance(this.tileX, this.tileY, m.getTileX(), m.getTileY()) <= (double)maxRoamingBeesForSpawnTileRange).count()) < (long)maxRoamingBeesCloseForSpawn) {
                Point spawnPos;
                HoneyBeeMob beeMob = (HoneyBeeMob)MobRegistry.getMob("honeybee", this.getLevel());
                beeMob.setApiaryHome(this.tileX, this.tileY);
                if (GameRandom.globalRandom.getChance(0.3f)) {
                    int stayOutTime = (int)(beeMob.returnToApiaryTime - this.getTime());
                    beeMob.pollinateTime = this.getTime() + (long)GameRandom.globalRandom.nextInt(stayOutTime);
                }
                if ((spawnPos = this.getAdjacentSpawnPos(beeMob)) != null) {
                    this.getLevel().entityManager.addMob(beeMob, spawnPos.x, spawnPos.y);
                    this.beesOutsideApiaryUniqueIDs.add(beeMob.getUniqueID());
                }
            }
        }
    }

    protected void removeInvalidRoamingBees() {
        HashSet<Integer> removes = new HashSet<Integer>();
        for (int uniqueID : this.beesOutsideApiaryUniqueIDs) {
            Mob mob = this.getLevel().entityManager.mobs.get(uniqueID, false);
            if (!(mob instanceof HoneyBeeMob)) continue;
            HoneyBeeMob beeMob = (HoneyBeeMob)mob;
            if (beeMob.apiaryHome != null && beeMob.apiaryHome.x == this.tileX && beeMob.apiaryHome.y == this.tileY) continue;
            removes.add(uniqueID);
        }
        if (!removes.isEmpty()) {
            removes.forEach(this.beesOutsideApiaryUniqueIDs::remove);
            this.makeProductionChange(() -> {
                this.bees.amount = Math.max(0, this.bees.amount - removes.size());
            });
        }
    }

    protected void tryMigratingQueenBee() {
        ArrayList<AbstractBeeHiveObjectEntity> validBeeHives = new ArrayList<AbstractBeeHiveObjectEntity>();
        for (Point validTile : migrateTileRange.getValidTiles(this.tileX, this.tileY)) {
            AbstractBeeHiveObjectEntity hiveEntity = this.getLevel().entityManager.getObjectEntity(validTile.x, validTile.y, AbstractBeeHiveObjectEntity.class);
            if (hiveEntity == null || !hiveEntity.canTakeMigratingQueen()) continue;
            validBeeHives.add(hiveEntity);
        }
        if (!validBeeHives.isEmpty()) {
            AbstractBeeHiveObjectEntity hiveEntity = (AbstractBeeHiveObjectEntity)GameRandom.globalRandom.getOneOf(validBeeHives);
            QueenBeeMob queenBee = (QueenBeeMob)MobRegistry.getMob("queenbee", this.getLevel());
            queenBee.setMigrationApiary(hiveEntity.tileX, hiveEntity.tileY);
            Point spawnPos = this.getAdjacentSpawnPos(queenBee);
            if (spawnPos != null) {
                this.getLevel().entityManager.addMob(queenBee, spawnPos.x, spawnPos.y);
                this.removeInvalidRoamingBees();
                int migratingBeeCount = GameMath.limit(GameRandom.globalRandom.getIntOffset(this.bees.amount / 2, this.bees.amount / 6), 0, this.bees.amount);
                if (migratingBeeCount > 0) {
                    int beesInsideApiary = this.bees.amount - this.beesOutsideApiaryUniqueIDs.size();
                    for (int i = 0; i < Math.min(migratingBeeCount, beesInsideApiary); ++i) {
                        HoneyBeeMob beeMob = (HoneyBeeMob)MobRegistry.getMob("honeybee", this.getLevel());
                        beeMob.setFollowingQueen(queenBee);
                        Point beeSpawnPos = this.getAdjacentSpawnPos(beeMob);
                        if (beeSpawnPos == null) continue;
                        --beesInsideApiary;
                        this.makeProductionChange(() -> --this.bees.amount);
                        this.getLevel().entityManager.addMob(beeMob, beeSpawnPos.x, beeSpawnPos.y);
                    }
                    if (beesInsideApiary < migratingBeeCount) {
                        List honeyBees = this.beesOutsideApiaryUniqueIDs.stream().map(uniqueID -> this.getLevel().entityManager.mobs.get((int)uniqueID, false)).filter(m -> m instanceof HoneyBeeMob).map(m -> (HoneyBeeMob)m).collect(Collectors.toList());
                        for (int i = beesInsideApiary; i < migratingBeeCount && !honeyBees.isEmpty(); ++i) {
                            HoneyBeeMob beeMob = (HoneyBeeMob)honeyBees.remove(GameRandom.globalRandom.nextInt(honeyBees.size()));
                            beeMob.setFollowingQueen(queenBee);
                        }
                    }
                    this.markDirty();
                }
            }
        }
    }

    protected Point getAdjacentSpawnPos(Mob mob) {
        LinkedList<Point> adjacentTiles = this.getObject().getMultiTile(this.getLevel(), 0, this.tileX, this.tileY).getAdjacentTiles(this.tileX, this.tileY, true);
        ArrayList<Point> validSpawnPositions = new ArrayList<Point>();
        for (Point adjacentTile : adjacentTiles) {
            Point pos = new Point(adjacentTile.x * 32 + 16, adjacentTile.y * 32 + 16);
            if (mob.collidesWith(this.getLevel(), pos.x, pos.y)) continue;
            validSpawnPositions.add(pos);
        }
        if (!validSpawnPositions.isEmpty()) {
            return (Point)GameRandom.globalRandom.getOneOf(validSpawnPositions);
        }
        return null;
    }

    protected void addDebugTooltips(StringTooltips tooltips) {
        tooltips.add("Has queen: " + this.hasQueen);
        tooltips.add("Honey: " + this.honey.amount);
        tooltips.add("Bees: " + this.bees.amount);
        tooltips.add("Frames: " + this.frames.amount);
        tooltips.add("Time since last production tick: " + GameUtils.getTimeStringMillis(this.getWorldEntity().getWorldTime() - this.lastProductionTickWorldTime));
        tooltips.add("Time for next bee spawn: " + GameUtils.getTimeStringMillis(this.nextRoamingBeeSpawnTime - this.getWorldEntity().getTime()));
        tooltips.add("Roaming bees: " + this.beesOutsideApiaryUniqueIDs);
        this.honey.addDebugTooltip(tooltips);
        this.bees.addDebugTooltip(tooltips);
        this.frames.addDebugTooltip(tooltips);
    }

    protected void makeProductionChange(Runnable changeLogic) {
        this.makeProductionChange(null, changeLogic);
    }

    protected void makeProductionChange(ProductionTimer exclude, Runnable changeLogic) {
        int nextHoney = exclude == this.honey ? -1 : this.honey.getSecondsForNextProduction();
        int nextFrame = exclude == this.frames ? -1 : this.frames.getSecondsForNextProduction();
        int nextBee = exclude == this.bees ? -1 : this.bees.getSecondsForNextProduction();
        changeLogic.run();
        this.honey.adjustProductionBuffer(nextHoney);
        this.frames.adjustProductionBuffer(nextFrame);
        this.bees.adjustProductionBuffer(nextBee);
    }

    public void onRoamingBeeDied(Mob mob) {
        if (this.beesOutsideApiaryUniqueIDs.remove(mob.getUniqueID())) {
            if (this.bees.amount > 0) {
                this.makeProductionChange(() -> --this.bees.amount);
            }
            this.markDirty();
        }
    }

    public void onRoamingBeeLost(Mob mob) {
        if (this.beesOutsideApiaryUniqueIDs.remove(mob.getUniqueID())) {
            if (this.bees.amount > 0) {
                this.makeProductionChange(() -> --this.bees.amount);
            }
            this.markDirty();
        }
    }

    public void onRoamingBeeReturned(Mob mob) {
        this.beesOutsideApiaryUniqueIDs.remove(mob.getUniqueID());
    }

    public boolean hasQueen() {
        return this.hasQueen;
    }

    public void addQueen() {
        if (!this.hasQueen) {
            this.makeProductionChange(() -> {
                this.hasQueen = true;
            });
            this.markDirty();
        }
    }

    public void migrateQueen(QueenBeeMob queenMob) {
        if (!this.hasQueen) {
            this.beesOutsideApiaryUniqueIDs.addAll(queenMob.honeyBeeUniqueIDs);
            this.makeProductionChange(() -> {
                this.hasQueen = true;
                this.bees.amount += queenMob.honeyBeeUniqueIDs.size();
            });
            this.markDirty();
        }
    }

    public void removeQueen(Mob removerMob) {
        if (this.hasQueen) {
            this.makeProductionChange(() -> {
                this.hasQueen = false;
            });
            Point dropPos = FruitTreeObject.getItemDropPos(this.tileX, this.tileY, removerMob);
            this.getLevel().entityManager.pickups.add(new InventoryItem("queenbee").getPickupEntity(this.getLevel(), dropPos.x, dropPos.y));
            this.markDirty();
        }
    }

    public boolean canAddWorkerBee() {
        return this.bees.amount < this.getMaxBees();
    }

    public void addWorkerBee() {
        this.makeProductionChange(() -> ++this.bees.amount);
        this.markDirty();
    }

    public boolean canAddFrame() {
        return this.frames.amount < this.getMaxFrames();
    }

    public void addFrame() {
        this.makeProductionChange(() -> ++this.frames.amount);
        this.markDirty();
    }

    public int getFrameAmount() {
        return this.frames.amount;
    }

    public int getHoneyAmount() {
        return this.honey.amount;
    }

    public int getBeeAmount() {
        return this.bees.amount;
    }

    public void resetHarvestItems() {
        if (this.honey.amount != 0) {
            this.makeProductionChange(() -> {
                this.honey.amount = 0;
            });
            this.markDirty();
        }
    }

    public ArrayList<InventoryItem> getHarvestItems() {
        ArrayList<InventoryItem> out = new ArrayList<InventoryItem>();
        int honey = this.getHoneyAmount();
        if (honey > 0) {
            out.add(new InventoryItem("honey", honey));
        }
        return out;
    }

    public ArrayList<InventoryItem> getHarvestSplitItems() {
        ArrayList<InventoryItem> out = new ArrayList<InventoryItem>();
        int honey = this.getHoneyAmount();
        for (int i = 0; i < honey; ++i) {
            out.add(new InventoryItem("honey"));
        }
        return out;
    }

    public void harvest(Mob mob) {
        ArrayList<InventoryItem> items;
        if (!this.isClient() && !(items = this.getHarvestSplitItems()).isEmpty()) {
            Point dropPos = FruitTreeObject.getItemDropPos(this.tileX, this.tileY, mob);
            for (InventoryItem item : items) {
                this.getLevel().entityManager.pickups.add(item.getPickupEntity(this.getLevel(), dropPos.x, dropPos.y));
            }
            this.resetHarvestItems();
        }
    }

    public String getInteractTip(PlayerMob perspective) {
        if (this.getHoneyAmount() > 0) {
            return Localization.translate("controls", "harvesttip");
        }
        return Localization.translate("controls", "inspecttip");
    }

    public void interact(PlayerMob player) {
        if (!this.isClient()) {
            ServerClient client;
            if (this.getHoneyAmount() > 0) {
                this.harvest(player);
            } else if (player != null && player.isServerClient() && (client = player.getServerClient()) != null) {
                GameMessageBuilder message = new GameMessageBuilder();
                float beePercent = (float)this.getBeeAmount() / (float)this.getMaxBees();
                int textX = this.tileX * 32 + 16;
                int textY = this.tileY * 32 + 32;
                if (!this.hasQueen) {
                    message.append("ui", "missingqueen").append("\n");
                }
                if (beePercent <= 0.0f) {
                    if (this.hasQueen) {
                        message.append("ui", "beesempty");
                    }
                } else if (beePercent < 0.25f) {
                    message.append("ui", "beesfewbees");
                } else if (beePercent < 0.5f) {
                    message.append("ui", "beesunderhalf");
                } else if (beePercent < 0.75f) {
                    message.append("ui", "beesoverhalf");
                } else if (beePercent < 1.0f) {
                    message.append("ui", "beesalmostfull");
                } else {
                    message.append("ui", "beesfull");
                }
                client.sendUniqueFloatText(textX, textY, message, "inspect", 6000);
            }
        }
    }

    @Override
    public ArrayList<InventoryItem> getDroppedItems() {
        ArrayList<InventoryItem> harvestItems = this.getHarvestSplitItems();
        if (this.hasQueen) {
            harvestItems.add(new InventoryItem("queenbee"));
        }
        return harvestItems;
    }

    public abstract int getMaxBees();

    public abstract int getMaxFrames();

    public abstract int getMaxStoredHoney();

    public abstract boolean canCreateQueens();

    public float getQueenBeeChance() {
        return 0.5f;
    }

    public boolean canTakeMigratingQueen() {
        return !this.hasQueen();
    }

    protected abstract class ProductionTimer {
        public String debugName;
        public int amount;
        public int buffer;

        public ProductionTimer(String debugName, int startAmount) {
            this.debugName = debugName;
            this.amount = startAmount;
        }

        public void addSaveData(String prefix, SaveData save) {
            save.addInt(prefix + "Amount", this.amount);
            save.addInt(prefix + "Buffer", this.buffer);
        }

        public void applyLoadData(String prefix, LoadData save) {
            this.amount = save.getInt(prefix + "Amount", this.amount);
            this.buffer = save.getInt(prefix + "Buffer", this.buffer);
        }

        public void writePacket(PacketWriter writer) {
            writer.putNextInt(this.amount);
            writer.putNextInt(this.buffer);
        }

        public void readPacket(PacketReader reader) {
            this.amount = reader.getNextInt();
            this.buffer = reader.getNextInt();
        }

        public abstract int getSecondsForNextProduction();

        public void adjustProductionBuffer(int lastSecondsForNextProduction) {
            if (lastSecondsForNextProduction < 0) {
                return;
            }
            int next = this.getSecondsForNextProduction();
            if (next < 0) {
                return;
            }
            double lastPercent = (double)this.buffer / (double)lastSecondsForNextProduction;
            this.buffer = (int)((double)next * lastPercent);
        }

        public boolean shouldResetBufferOnNoProduction() {
            return true;
        }

        public void onProductionTick() {
            AbstractBeeHiveObjectEntity.this.makeProductionChange(this, () -> ++this.amount);
        }

        public void addDebugTooltip(StringTooltips tooltips) {
            int secondsForNextProduction = this.getSecondsForNextProduction();
            tooltips.add(this.debugName + "Tick: " + secondsForNextProduction + ", " + this.buffer + " (" + (secondsForNextProduction - this.buffer) + ")");
        }
    }
}

