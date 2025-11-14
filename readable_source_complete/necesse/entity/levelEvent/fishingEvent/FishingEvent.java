/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.fishingEvent;

import java.awt.Point;
import java.awt.geom.Point2D;
import necesse.engine.GameLog;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.PointSetAbstract;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.fishingEvent.FishingPhase;
import necesse.entity.levelEvent.fishingEvent.SwingFishingPhase;
import necesse.entity.mobs.FishingMob;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.baitItem.BaitItem;
import necesse.inventory.item.placeableItem.fishingRodItem.FishingRodItem;

public class FishingEvent
extends LevelEvent {
    public static final int MAX_TICKS = 4800;
    public static final int MAX_CATCH_TICKS = 500;
    public static final int MIN_CATCH_TICKS = 10;
    public static final int MAX_POWER = 100;
    private Mob mob;
    private FishingMob fishingMob;
    private int targetX;
    private int targetY;
    private FishingPhase currentPhase;
    private boolean isMine;
    private int seed;
    private int lines;
    private Point[] randomTargets;
    public FishingRodItem fishingRod;
    public BaitItem bait;
    public boolean isReeled;

    public FishingEvent() {
    }

    public FishingEvent(FishingMob fishingMob, int targetX, int targetY, FishingRodItem fishingRod, BaitItem bait) {
        this.mob = (Mob)((Object)fishingMob);
        this.fishingMob = fishingMob;
        this.targetX = targetX;
        this.targetY = targetY;
        this.fishingRod = fishingRod;
        this.bait = bait;
        this.seed = GameRandom.globalRandom.nextInt();
        if (this.mob != null && this.mob.getPositionPoint().distance(targetX, targetY) > (double)fishingRod.lineLength) {
            Point2D.Float tempPoint = new Point2D.Float(targetX - this.mob.getX(), targetY - this.mob.getY());
            float tempDist = (float)tempPoint.distance(0.0, 0.0);
            float normX = tempPoint.x / tempDist;
            float normY = tempPoint.y / tempDist;
            this.targetX = (int)((float)this.mob.getX() + normX * (float)fishingRod.lineLength);
            this.targetY = (int)((float)this.mob.getY() + normY * (float)fishingRod.lineLength);
        }
    }

    @Override
    public boolean shouldSendOverPacket() {
        return true;
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        int mobUniqueID = reader.getNextInt();
        Mob levelMob = GameUtils.getLevelMob(mobUniqueID, this.level);
        if (levelMob instanceof FishingMob && levelMob.getLevel() != null) {
            this.fishingMob = (FishingMob)((Object)levelMob);
            this.mob = levelMob;
        } else {
            this.over();
        }
        this.targetX = reader.getNextInt();
        this.targetY = reader.getNextInt();
        this.fishingRod = (FishingRodItem)ItemRegistry.getItem(reader.getNextShortUnsigned());
        short bait = reader.getNextShort();
        this.bait = bait == -1 ? null : (BaitItem)ItemRegistry.getItem(bait & 0xFFFF);
        this.seed = reader.getNextInt();
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextInt(this.mob.getUniqueID());
        writer.putNextInt(this.targetX);
        writer.putNextInt(this.targetY);
        writer.putNextShortUnsigned(this.fishingRod.getID());
        if (this.bait == null) {
            writer.putNextShort((short)-1);
        } else {
            writer.putNextShortUnsigned(this.bait.getID());
        }
        writer.putNextInt(this.seed);
    }

    @Override
    public void init() {
        super.init();
        if (this.mob == null) {
            GameLog.warn.println("Could not find owner for fishing event " + this.getClass().getSimpleName() + " server level: " + this.isServer());
            this.over();
            return;
        }
        if (this.mob.getLevel() == null) {
            GameLog.warn.println("Could not find owner level for fishing event " + this.getClass().getSimpleName() + " server level: " + this.isServer());
            this.over();
            return;
        }
        if (this.isClient()) {
            this.isMine = this.mob == this.level.getClient().getPlayer();
        }
        int buffLines = this.mob.buffManager.getModifier(BuffModifiers.FISHING_LINES);
        this.lines = GameMath.limit(this.fishingRod.lineCount + buffLines, 1, 1000);
        this.setPhase(new SwingFishingPhase(this));
        GameRandom random = new GameRandom(this.seed);
        this.randomTargets = new Point[this.lines];
        int precision = this.fishingRod.precision;
        for (int i = 0; i < this.lines; ++i) {
            int x = this.targetX + random.getIntBetween(-precision, precision);
            int y = this.targetY + random.getIntBetween(-precision, precision);
            this.randomTargets[i] = new Point(x, y);
        }
        this.updateMobDir();
    }

    public void setPhase(FishingPhase phase) {
        if (this.currentPhase != null) {
            this.currentPhase.end();
        }
        this.currentPhase = phase;
    }

    public Point getRandomTarget(int lineIndex) {
        return this.randomTargets[lineIndex];
    }

    public Point getTarget() {
        return new Point(this.targetX, this.targetY);
    }

    public Mob getMob() {
        return this.mob;
    }

    public FishingMob getFishingMob() {
        return this.fishingMob;
    }

    public int getLines() {
        return this.lines;
    }

    public boolean isMine() {
        return this.isMine;
    }

    @Override
    public void tickMovement(float delta) {
        if (this.isOver()) {
            return;
        }
        this.updateMobDir();
        if (this.currentPhase != null) {
            this.currentPhase.tickMovement(delta);
        }
    }

    @Override
    public void clientTick() {
        if (this.isOver()) {
            return;
        }
        if (this.getMob().removed()) {
            this.over();
            return;
        }
        if (this.currentPhase != null) {
            this.currentPhase.clientTick();
        }
    }

    @Override
    public void serverTick() {
        if (this.isOver()) {
            return;
        }
        if (this.getMob().removed()) {
            this.over();
            return;
        }
        if (this.currentPhase != null) {
            this.currentPhase.serverTick();
        }
    }

    public boolean checkOutsideRange() {
        if (this.mob.getPositionPoint().distance(this.targetX, this.targetY) > (double)(this.fishingRod.lineLength + this.fishingRod.precision + 64)) {
            this.giveBaitBack();
            this.over();
            return true;
        }
        return false;
    }

    private void updateMobDir() {
        if (this.targetX > this.mob.getX()) {
            this.mob.setDir(1);
        } else {
            this.mob.setDir(3);
        }
    }

    public Point getPoleTipPos() {
        return this.fishingRod.getTipPos(this.mob);
    }

    public int getPoleTipHeight() {
        return this.fishingRod.getTipHeight(this.mob);
    }

    public void addCatch(int lineIndex, int inTicks, InventoryItem item) {
        if (this.currentPhase != null) {
            this.currentPhase.addNewCatch(lineIndex, inTicks, item);
        }
    }

    public void reel() {
        if (this.currentPhase != null) {
            this.currentPhase.reel();
        }
        this.isReeled = true;
    }

    public int getTicksToNextCatch() {
        if (this.currentPhase != null) {
            return this.currentPhase.getTicksToNextCatch();
        }
        return 500;
    }

    public void giveBaitBack() {
        if (this.bait != null) {
            this.fishingMob.giveBaitBack(this.bait);
            this.bait = null;
        }
    }

    @Override
    public void over() {
        if (this.isOver()) {
            return;
        }
        super.over();
        if (this.currentPhase != null) {
            this.currentPhase.over();
        }
    }

    public int getMobUniqueID() {
        return this.mob.getUniqueID();
    }

    public int getSeed() {
        return this.seed;
    }

    @Override
    public PointSetAbstract<?> getRegionPositions() {
        if (this.mob != null && this.mob.getLevel() != null) {
            return this.mob.getRegionPositions();
        }
        return super.getRegionPositions();
    }

    @Override
    public Point getSaveToRegionPos() {
        if (this.mob != null) {
            return new Point(this.level.regionManager.getRegionCoordByTile(this.mob.getTileX()), this.level.regionManager.getRegionCoordByTile(this.mob.getTileY()));
        }
        return super.getSaveToRegionPos();
    }
}

