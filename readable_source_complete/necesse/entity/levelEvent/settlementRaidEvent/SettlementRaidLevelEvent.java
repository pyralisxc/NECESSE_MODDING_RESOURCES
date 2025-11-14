/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.settlementRaidEvent;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;
import necesse.engine.GameLog;
import necesse.engine.MusicOptions;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketLevelEvent;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.LoadDataException;
import necesse.engine.save.SaveData;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.HashMapSet;
import necesse.engine.util.PointHashSet;
import necesse.engine.util.PointSetAbstract;
import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.actions.EmptyLevelEventAction;
import necesse.entity.levelEvent.settlementRaidEvent.ActiveSettlementRaidStage;
import necesse.entity.levelEvent.settlementRaidEvent.ApproachSettlementRaidStage;
import necesse.entity.levelEvent.settlementRaidEvent.PreparingSettlementRaidStage;
import necesse.entity.levelEvent.settlementRaidEvent.SettlementRaidStage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.RaiderMobPhase;
import necesse.entity.mobs.hostile.ItemAttackerRaiderMob;
import necesse.level.maps.levelData.settlementData.LevelSettler;
import necesse.level.maps.levelData.settlementData.NetworkSettlementData;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.SettlementBed;
import necesse.level.maps.levelData.settlementData.SettlementRaidLoadout;
import necesse.level.maps.levelData.settlementData.SettlementRaidOptions;

public abstract class SettlementRaidLevelEvent
extends LevelEvent {
    protected int settlementUniqueID = 0;
    protected NetworkSettlementData networkData;
    protected ArrayList<SettlementRaidStage> stages = new ArrayList();
    protected int currentStage;
    protected boolean loadedIntoStage;
    protected ApproachSettlementRaidStage approachStage;
    protected PreparingSettlementRaidStage preparingStage;
    protected ActiveSettlementRaidStage activeStage;
    protected int startSettlers = -1;
    protected int spawnedRaiders = 0;
    protected float difficultyModifier = 1.0f;
    protected Point centerSpawnTile;
    protected RaidDir direction;
    protected boolean started;
    public boolean combatStarted = false;
    protected LinkedList<ItemAttackerRaiderMob> raiders = new LinkedList();
    protected int[] loadedRaiderUniqueIDs;
    protected ServerSettlementData serverData;
    public final EmptyLevelEventAction combatTriggeredAction;
    protected ArrayList<SettlementRaidLoadout> loadouts = new ArrayList();
    protected ArrayList<Point> attackTiles = new ArrayList();
    protected ArrayList<Point> spawnTiles;
    public HashMapSet<Point, Integer> reservedLoot = new HashMapSet();

    public SettlementRaidLevelEvent() {
        super(true);
        this.shouldSave = true;
        this.currentStage = -1;
        this.approachStage = new ApproachSettlementRaidStage("approach", this, 120);
        this.stages.add(this.approachStage);
        this.preparingStage = new PreparingSettlementRaidStage("preparing", this, 60, 20);
        this.stages.add(this.preparingStage);
        this.activeStage = new ActiveSettlementRaidStage("active", this, 300);
        this.stages.add(this.activeStage);
        this.combatTriggeredAction = this.registerAction(new EmptyLevelEventAction(){

            @Override
            protected void run() {
                SettlementRaidLevelEvent.this.combatStarted = true;
            }
        });
    }

    public void triggerCombatEvent() {
        if (!this.combatStarted) {
            this.combatTriggeredAction.runAndSend();
        }
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addInt("settlementUniqueID", this.settlementUniqueID);
        save.addInt("currentStage", this.currentStage);
        for (SettlementRaidStage stage : this.stages) {
            SaveData stageSave = new SaveData(stage.stringID);
            stage.addSaveData(stageSave);
            if (stageSave.isEmpty()) continue;
            save.addSaveData(stageSave);
        }
        save.addInt("startSettlers", this.startSettlers);
        save.addInt("spawnedRaiders", this.spawnedRaiders);
        if (this.centerSpawnTile != null) {
            save.addPoint("centerSpawnTile", this.centerSpawnTile);
        }
        save.addFloat("difficultyModifier", this.difficultyModifier);
        if (this.direction != null) {
            save.addEnum("direction", this.direction);
        }
        save.addBoolean("started", this.started);
        save.addBoolean("combatStarted", this.combatStarted);
        int[] raiderUniqueIDs = this.raiders.stream().mapToInt(r -> r.getUniqueID()).toArray();
        save.addIntArray("raiders", raiderUniqueIDs);
        SaveData loadoutsSave = new SaveData("loadouts");
        for (SettlementRaidLoadout loadout : this.loadouts) {
            loadoutsSave.addSaveData(loadout.getSaveData(""));
        }
        save.addSaveData(loadoutsSave);
        if (this.attackTiles != null && !this.attackTiles.isEmpty()) {
            int[] attackTiles = new int[this.attackTiles.size() * 2];
            for (int i = 0; i < this.attackTiles.size(); ++i) {
                Point tile = this.attackTiles.get(i);
                attackTiles[i * 2] = tile.x;
                attackTiles[i * 2 + 1] = tile.y;
            }
            save.addIntArray("attackTiles", attackTiles);
        }
    }

    @Override
    public void applyLoadData(LoadData save) {
        int[] nArray;
        super.applyLoadData(save);
        this.settlementUniqueID = save.getInt("settlementUniqueID", this.settlementUniqueID, false);
        this.currentStage = save.getInt("currentStage", this.currentStage, -1, this.stages.size(), false);
        for (SettlementRaidStage settlementRaidStage : this.stages) {
            LoadData stageSave = save.getFirstLoadDataByName(settlementRaidStage.stringID);
            if (stageSave == null) continue;
            settlementRaidStage.applyLoadData(stageSave);
        }
        this.loadedIntoStage = true;
        this.startSettlers = save.getInt("startSettlers", this.startSettlers);
        this.spawnedRaiders = save.getInt("spawnedRaiders", this.spawnedRaiders);
        this.centerSpawnTile = save.getPoint("centerSpawnTile", this.centerSpawnTile, false);
        this.difficultyModifier = save.getFloat("difficultyModifier", this.difficultyModifier);
        this.direction = save.getEnum(RaidDir.class, "direction", GameRandom.globalRandom.getOneOf(RaidDir.values()), false);
        this.started = save.getBoolean("started", this.started);
        this.combatStarted = save.getBoolean("combatStarted", this.combatStarted);
        this.loadedRaiderUniqueIDs = save.getIntArray("raiders", new int[0]);
        LoadData loadoutsSave = save.getFirstLoadDataByName("loadouts");
        if (loadoutsSave != null) {
            this.loadouts = new ArrayList();
            for (LoadData loadoutSave : loadoutsSave.getLoadData()) {
                try {
                    this.loadouts.add(new SettlementRaidLoadout(loadoutSave));
                }
                catch (LoadDataException loadDataException) {}
            }
        }
        if ((nArray = save.getIntArray("attackTiles", null, false)) != null) {
            this.attackTiles = new ArrayList();
            for (int i = 0; i < nArray.length && i + 1 < nArray.length; i += 2) {
                this.attackTiles.add(new Point(nArray[i], nArray[i + 1]));
            }
        }
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.settlementUniqueID = reader.getNextInt();
        for (SettlementRaidStage stage : this.stages) {
            stage.applySpawnPacket(reader);
        }
        this.difficultyModifier = reader.getNextFloat();
        if (reader.getNextBoolean()) {
            int x = reader.getNextInt();
            int y = reader.getNextInt();
            this.centerSpawnTile = new Point(x, y);
        } else {
            this.centerSpawnTile = null;
        }
        this.direction = RaidDir.values()[reader.getNextByteUnsigned()];
        this.started = reader.getNextBoolean();
        this.combatStarted = reader.getNextBoolean();
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextInt(this.settlementUniqueID);
        for (SettlementRaidStage stage : this.stages) {
            stage.setupSpawnPacket(writer);
        }
        writer.putNextFloat(this.difficultyModifier);
        writer.putNextBoolean(this.centerSpawnTile != null);
        if (this.centerSpawnTile != null) {
            writer.putNextInt(this.centerSpawnTile.x);
            writer.putNextInt(this.centerSpawnTile.y);
        }
        writer.putNextByteUnsigned(this.direction.ordinal());
        writer.putNextBoolean(this.started);
        writer.putNextBoolean(this.combatStarted);
    }

    @Override
    public boolean isNetworkImportant() {
        return true;
    }

    public void initializeFromServerData(ServerSettlementData serverData, SettlementRaidOptions options) {
        this.settlementUniqueID = serverData.uniqueID;
        this.serverData = serverData;
        this.networkData = serverData.networkData;
        this.level = serverData.getLevel();
        this.difficultyModifier = options.difficultyModifier;
        this.direction = options.direction;
        if (options.dontAutoAttackSettlement) {
            this.preparingStage.changeIdleTile(Integer.MAX_VALUE);
        }
    }

    @Override
    public void init() {
        super.init();
        if (this.isServer()) {
            this.serverData = SettlementsWorldData.getSettlementsData(this.level).getServerData(this.settlementUniqueID);
            if (this.serverData != null) {
                this.networkData = this.serverData.networkData;
                if (this.startSettlers == -1) {
                    this.startSettlers = this.getCurrentSettlerMobs();
                }
                if (this.attackTiles == null || this.attackTiles.isEmpty()) {
                    this.attackTiles = new ArrayList();
                    for (SettlementBed bed : this.serverData.getBeds()) {
                        if (bed.getSettler() == null) continue;
                        this.attackTiles.add(new Point(bed.tileX, bed.tileY));
                    }
                    Point flagTile = this.serverData.getFlagTile();
                    if (flagTile != null) {
                        this.attackTiles.add(flagTile);
                    }
                }
            } else {
                this.loadouts = new ArrayList();
                this.over();
                GameLog.warn.println("Could not find settlement for raid with uniqueID: " + this.settlementUniqueID);
            }
            if (this.centerSpawnTile == null && this.serverData != null) {
                Rectangle tileRectangle = this.serverData.networkData.getLoadedTileRectangle();
                if (this.direction == null) {
                    this.direction = GameRandom.globalRandom.getOneOf(RaidDir.values());
                }
                ArrayList<Rectangle> spawnRectangles = SettlementRaidLevelEvent.getSpawnRectangles(tileRectangle, this.direction, 5, 0.3f);
                ArrayList<Point> potentialSpawnTiles = new ArrayList<Point>();
                for (Rectangle spawnRectangle : spawnRectangles) {
                    for (int x = spawnRectangle.x; x < spawnRectangle.x + spawnRectangle.width; ++x) {
                        for (int y = spawnRectangle.y; y < spawnRectangle.y + spawnRectangle.height; ++y) {
                            potentialSpawnTiles.add(new Point(x, y));
                        }
                    }
                }
                while (!potentialSpawnTiles.isEmpty()) {
                    int nextIndex = GameRandom.globalRandom.nextInt(potentialSpawnTiles.size());
                    Point next = (Point)potentialSpawnTiles.remove(nextIndex);
                    if (this.level.isSolidTile(next.x, next.y)) continue;
                    this.centerSpawnTile = new Point(next.x, next.y);
                    break;
                }
            }
            if (this.centerSpawnTile != null) {
                System.out.println("Raid spawned at " + this.level.getIdentifier() + " on tile " + this.centerSpawnTile.x + ", " + this.centerSpawnTile.y + " with difficulty " + this.difficultyModifier);
                for (int i = 0; i < this.stages.size(); ++i) {
                    this.stages.get(i).init(this.loadedIntoStage, this.currentStage > i);
                }
            } else {
                this.over();
            }
        } else {
            this.networkData = SettlementsWorldData.getSettlementsData(this.level).getNetworkData(this.settlementUniqueID);
        }
    }

    public static ArrayList<Rectangle> getSpawnRectangles(Rectangle rectangle, RaidDir direction, int edgeSize, float diagonalPercentFromEdge) {
        ArrayList<Rectangle> spawnRectangles = new ArrayList<Rectangle>();
        int edgeWidth = (int)((float)rectangle.width * diagonalPercentFromEdge);
        int edgeHeight = (int)((float)rectangle.height * diagonalPercentFromEdge);
        switch (direction) {
            case North: {
                spawnRectangles.add(new Rectangle(rectangle.x + edgeWidth, rectangle.y, rectangle.width - edgeWidth * 2, edgeSize));
                break;
            }
            case East: {
                spawnRectangles.add(new Rectangle(rectangle.x + rectangle.width - edgeSize, rectangle.y + edgeHeight, edgeSize, rectangle.height - edgeHeight * 2));
                break;
            }
            case South: {
                spawnRectangles.add(new Rectangle(rectangle.x + edgeWidth, rectangle.y + rectangle.height - edgeSize, rectangle.width - edgeWidth * 2, edgeSize));
                break;
            }
            case West: {
                spawnRectangles.add(new Rectangle(rectangle.x, rectangle.y + edgeHeight, edgeSize, rectangle.height - edgeHeight * 2));
                break;
            }
            case NorthEast: {
                spawnRectangles.add(new Rectangle(rectangle.x + rectangle.width - edgeWidth, rectangle.y, edgeWidth, edgeSize));
                spawnRectangles.add(new Rectangle(rectangle.x + rectangle.width - edgeSize, rectangle.y + edgeSize, edgeSize, edgeHeight - edgeSize));
                break;
            }
            case SouthEast: {
                spawnRectangles.add(new Rectangle(rectangle.x + rectangle.width - edgeSize, rectangle.y + rectangle.height - edgeHeight, edgeSize, edgeHeight - edgeSize));
                spawnRectangles.add(new Rectangle(rectangle.x + rectangle.width - edgeWidth, rectangle.y + rectangle.height - edgeSize, edgeWidth, edgeSize));
                break;
            }
            case SouthWest: {
                spawnRectangles.add(new Rectangle(rectangle.x, rectangle.y + rectangle.height - edgeHeight, edgeSize, edgeHeight - edgeSize));
                spawnRectangles.add(new Rectangle(rectangle.x, rectangle.y + rectangle.height - edgeSize, edgeWidth, edgeSize));
                break;
            }
            case NorthWest: {
                spawnRectangles.add(new Rectangle(rectangle.x, rectangle.y, edgeWidth, edgeSize));
                spawnRectangles.add(new Rectangle(rectangle.x, rectangle.y + edgeSize, edgeSize, edgeHeight - edgeSize));
            }
        }
        return spawnRectangles;
    }

    public void dontAutoAttack() {
        this.preparingStage.changeIdleTile(Integer.MAX_VALUE);
    }

    public void startNextStage() {
        ++this.currentStage;
        if (this.currentStage >= this.stages.size()) {
            this.over();
        } else {
            this.stages.get(this.currentStage).onStarted();
        }
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (this.isOver()) {
            return;
        }
        this.level.getWorldEntity().preventSleep();
        if (this.currentStage < 0) {
            this.currentStage = -1;
            this.startNextStage();
        }
        if (this.currentStage >= 0 && this.currentStage < this.stages.size()) {
            SettlementRaidStage stage = this.stages.get(this.currentStage);
            stage.clientTick();
            if (stage.isComplete()) {
                stage.onComplete();
                this.startNextStage();
            }
        } else {
            this.over();
        }
        if (this.networkData == null) {
            this.networkData = SettlementsWorldData.getSettlementsData(this.level).getNetworkData(this.settlementUniqueID);
        }
        if (this.networkData != null) {
            PlayerMob player;
            boolean isNearSettlement = false;
            Client client = this.getClient();
            if (client != null && (player = client.getPlayer()) != null) {
                Rectangle tileRectangle = this.networkData.getLoadedTileRectangle();
                int padding = 32;
                tileRectangle = new Rectangle(tileRectangle.x - padding, tileRectangle.y - padding, tileRectangle.width + padding * 2, tileRectangle.height + padding * 2);
                isNearSettlement = tileRectangle.contains(player.getTileX(), player.getTileY());
            }
            if (this.started) {
                if (isNearSettlement) {
                    if (this.combatStarted) {
                        SoundManager.setMusic(new MusicOptions(MusicRegistry.StormingTheHamletPart2).volume(1.5f).fadeInTime(250), SoundManager.MusicPriority.EVENT);
                    } else {
                        SoundManager.setMusic(new MusicOptions(MusicRegistry.StormingTheHamletPart1).volume(1.5f).fadeInTime(500).fadeOutTime(750), SoundManager.MusicPriority.EVENT);
                    }
                }
                this.networkData.refreshRaidActive();
            } else {
                this.networkData.refreshRaidApproaching();
                if (isNearSettlement) {
                    SoundManager.setMusic(new MusicOptions(MusicRegistry.StormingTheHamletPart1).volume(1.5f).fadeInTime(500).fadeOutTime(750), SoundManager.MusicPriority.EVENT);
                }
            }
        }
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (this.isOver()) {
            return;
        }
        if (this.loadedRaiderUniqueIDs != null) {
            for (int uniqueID : this.loadedRaiderUniqueIDs) {
                Mob mob = this.level.entityManager.mobs.get(uniqueID, false);
                if (!(mob instanceof ItemAttackerRaiderMob)) continue;
                this.raiders.add((ItemAttackerRaiderMob)mob);
            }
            this.loadedRaiderUniqueIDs = null;
        }
        this.raiders.removeIf(r -> r.removed());
        this.level.getWorldEntity().preventSleep();
        if (this.isServer() && this.loadedIntoStage && this.getServer().getPlayersOnline() > 0) {
            if (this.currentStage >= 0 && this.currentStage < this.stages.size()) {
                this.stages.get(this.currentStage).afterLoadingInit();
            }
            this.loadedIntoStage = false;
        }
        if (this.currentStage < 0) {
            this.currentStage = -1;
            this.startNextStage();
        }
        if (this.currentStage >= 0 && this.currentStage < this.stages.size()) {
            SettlementRaidStage stage = this.stages.get(this.currentStage);
            stage.serverTick();
            if (stage.isComplete()) {
                stage.onComplete();
                this.startNextStage();
            }
        } else {
            this.over();
        }
        if (this.started) {
            this.networkData.refreshRaidActive();
        } else {
            this.networkData.refreshRaidApproaching();
        }
    }

    public void startRaid(boolean reduceRaidTimers) {
        if (!this.started) {
            GameMessage startMessage;
            this.started = true;
            if (!this.isServer()) {
                return;
            }
            if (reduceRaidTimers) {
                int minStartTimer = Integer.MAX_VALUE;
                for (ItemAttackerRaiderMob raider : this.raiders) {
                    minStartTimer = Math.min(raider.getRaidingStartTimer(), minStartTimer);
                }
                if (minStartTimer > 0) {
                    for (ItemAttackerRaiderMob raider : this.raiders) {
                        raider.updateRaidingStartTimer(raider.getRaidingStartTimer() - minStartTimer);
                    }
                }
                this.preparingStage.onRaidStartedPrematurely(minStartTimer);
            }
            if ((startMessage = this.getStartMessage(this.networkData.getSettlementName())) != null) {
                this.networkData.streamTeamMembersAndInSettlement().forEach(c -> c.sendChatMessage(startMessage));
            }
            this.level.getServer().network.sendToClientsWithEntity(new PacketLevelEvent(this), this);
        }
    }

    protected boolean spawnRaider(Point tile, int startAttackingTimer, int raidingGroup) {
        if (this.loadouts.isEmpty()) {
            return false;
        }
        int loadoutIndex = GameRandom.globalRandom.nextInt(this.loadouts.size());
        SettlementRaidLoadout loadout = this.loadouts.get(loadoutIndex);
        ItemAttackerRaiderMob mob = loadout.getNewMob(this.getLevel());
        if (mob == null) {
            return false;
        }
        Point moveOffset = mob.getPathMoveOffset();
        if (mob.isValidSpawnLocation(this.level.getServer(), null, tile.x * 32 + moveOffset.x, tile.y * 32 + moveOffset.y)) {
            mob.onSpawned(tile.x * 32 + moveOffset.x, tile.y * 32 + moveOffset.y);
            this.addRaider(mob, startAttackingTimer, raidingGroup);
            this.level.entityManager.mobs.add(mob);
            this.loadouts.remove(loadoutIndex);
            return true;
        }
        return false;
    }

    protected void addRaider(ItemAttackerRaiderMob raider, int startAttackingTimer, int raidingGroup) {
        Point attackTile = GameRandom.globalRandom.getOneOf(this.attackTiles);
        raider.makeRaider(this, this.centerSpawnTile, attackTile, startAttackingTimer, raidingGroup, this.difficultyModifier);
        this.raiders.add(raider);
        ++this.spawnedRaiders;
    }

    public Point getNewAttackTile(Point oldAttackTile) {
        this.attackTiles.remove(oldAttackTile);
        return this.attackTiles.isEmpty() ? null : GameRandom.globalRandom.getOneOf(this.attackTiles);
    }

    public void onRaidOver() {
        if (this.isServer()) {
            if (this.currentStage < this.stages.size()) {
                this.raiders.forEach(raiderMob -> raiderMob.setRaiderPhase(RaiderMobPhase.ESCAPING));
                GameMessage leavingMessage = this.getAbruptEndingMessage();
                if (leavingMessage != null) {
                    GameUtils.streamServerClients(this.getLevel()).forEach(c -> c.sendChatMessage(leavingMessage));
                }
            } else if (this.raiders.isEmpty()) {
                GameMessage defeatedMessage = this.getDefeatedMessage();
                if (defeatedMessage != null) {
                    GameUtils.streamServerClients(this.getLevel()).forEach(c -> c.sendChatMessage(defeatedMessage));
                }
            } else {
                float aliveRaidersPercentage = this.spawnedRaiders <= 0 ? 0.0f : (float)this.raiders.size() / (float)this.spawnedRaiders;
                float lootChance = aliveRaidersPercentage * 2.0f;
                boolean startLooting = GameRandom.globalRandom.getChance(lootChance);
                this.raiders.forEach(raiderMob -> raiderMob.setRaiderPhase(startLooting ? RaiderMobPhase.LOOTING : RaiderMobPhase.ESCAPING));
                GameMessage leavingMessage = startLooting ? this.getLeavingWithLootMessage() : this.getLeavingMessage();
                if (leavingMessage != null) {
                    GameUtils.streamServerClients(this.getLevel()).forEach(c -> c.sendChatMessage(leavingMessage));
                }
            }
        }
        if (this.serverData != null) {
            int currentSettlers = this.getCurrentSettlerMobs();
            int killedSettlers = this.startSettlers - currentSettlers;
            float nextDifficulty = this.difficultyModifier;
            if (killedSettlers > 0) {
                nextDifficulty = GameMath.limit(this.difficultyModifier - 0.05f, 0.5f, 1.0f);
            } else {
                double min = this.serverData.settlers.stream().map(LevelSettler::getMob).filter(Objects::nonNull).mapToDouble(m -> (double)m.getMob().getHealth() / (double)m.getMob().getMaxHealth()).min().orElse(0.0);
                float change = (float)Math.min(Math.pow(min, 2.0) / 8.0 + (double)0.01f, (double)0.05f);
                nextDifficulty = Math.min(nextDifficulty + change, 1.25f);
            }
            this.serverData.onRaidOver(this, nextDifficulty);
        }
    }

    @Override
    public void over() {
        if (!this.isOver()) {
            this.onRaidOver();
        }
        super.over();
    }

    public int getCurrentSettlers() {
        if (this.serverData != null) {
            return this.serverData.countTotalSettlers();
        }
        return 0;
    }

    public int getCurrentSettlerMobs() {
        if (this.serverData != null) {
            return (int)this.serverData.settlers.stream().map(LevelSettler::getMob).filter(Objects::nonNull).count();
        }
        return 0;
    }

    public ServerSettlementData getServerSettlementData() {
        return this.serverData;
    }

    public NetworkSettlementData getNetworkSettlementData() {
        return this.networkData;
    }

    public abstract GameMessage getApproachMessage(GameMessage var1, boolean var2);

    public abstract GameMessage getPreparingMessage(GameMessage var1);

    public abstract GameMessage getStartMessage(GameMessage var1);

    public abstract GameMessage getDefeatedMessage();

    public abstract GameMessage getLeavingMessage();

    public abstract GameMessage getLeavingWithLootMessage();

    public GameMessage getAbruptEndingMessage() {
        return this.getLeavingMessage();
    }

    public float getSpawnWaves() {
        return 6.0f;
    }

    public float getTotalSpawns() {
        return this.spawnedRaiders + this.loadouts.size() + 1;
    }

    @Override
    public PointSetAbstract<?> getRegionPositions() {
        if (this.networkData == null) {
            this.networkData = SettlementsWorldData.getSettlementsData(this).getNetworkData(this.settlementUniqueID);
        }
        if (this.networkData != null) {
            PointHashSet regions = new PointHashSet();
            Rectangle regionRectangle = this.networkData.getLoadedRegionRectangle();
            for (int regionX = regionRectangle.x; regionX < regionRectangle.x + regionRectangle.width; ++regionX) {
                for (int regionY = regionRectangle.y; regionY < regionRectangle.y + regionRectangle.height; ++regionY) {
                    regions.add(regionX, regionY);
                }
            }
            return regions;
        }
        return super.getRegionPositions();
    }

    public static enum RaidDir {
        NorthWest("ui", "dirnorthwest"),
        North("ui", "dirnorth"),
        NorthEast("ui", "dirnortheast"),
        West("ui", "dirwest"),
        East("ui", "direast"),
        SouthWest("ui", "dirsouthwest"),
        South("ui", "dirsouth"),
        SouthEast("ui", "dirsoutheast");

        public final GameMessage displayName;

        private RaidDir(GameMessage displayName) {
            this.displayName = displayName;
        }

        private RaidDir(String category, String key) {
            this(new LocalMessage(category, key));
        }
    }
}

