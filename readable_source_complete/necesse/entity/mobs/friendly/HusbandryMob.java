/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.friendly;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.MobRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameObjectReservable;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.event.AIEvent;
import necesse.entity.mobs.friendly.FeedingTroughMob;
import necesse.entity.mobs.friendly.FriendlyRopableMob;
import necesse.entity.mobs.misc.MobProcessMobHandler;
import necesse.entity.mobs.misc.StartMobProcessMobMobAbility;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.gfx.GameResources;
import necesse.gfx.HumanGender;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.placeableItem.consumableItem.food.GrainItem;
import necesse.level.maps.levelData.jobs.MilkHusbandryMobLevelJob;
import necesse.level.maps.levelData.jobs.ShearHusbandryMobLevelJob;
import necesse.level.maps.levelData.jobs.SlaughterHusbandryMobLevelJob;
import necesse.level.maps.regionSystem.ConnectedSubRegionsResult;
import necesse.level.maps.regionSystem.RegionType;
import necesse.level.maps.regionSystem.SubRegion;

public abstract class HusbandryMob
extends FriendlyRopableMob
implements FeedingTroughMob {
    public static int maxCloseMobsToBirthTileRange = 8;
    public static int maxCloseMobsToBirth = 10;
    public float hungerLossPerHour = 2.0f;
    public float defaultHungerGainPerFeed = 0.75f;
    public float tamenessLossPerHour = 0.5f;
    public float tamenessGainPerHour = 1.0f;
    public int birthingCooldown = 1440000;
    public int timeToGrowUp = 1920000;
    protected float hunger = 0.0f;
    protected float tameness = 0.0f;
    protected long growUpTime;
    protected long nextBirthTime;
    public MilkHusbandryMobLevelJob milkJob;
    public ShearHusbandryMobLevelJob shearJob;
    public SlaughterHusbandryMobLevelJob slaughterJob;
    protected MobProcessMobHandler<HusbandryMob, HusbandryMob> impregnateHandler = new MobProcessMobHandler<HusbandryMob, HusbandryMob>((Mob)this){

        @Override
        public void tickInProgress() {
            super.tickInProgress();
            HusbandryMob target = (HusbandryMob)this.targetMob.get(HusbandryMob.this.getLevel());
            if (!HusbandryMob.this.isClient() && target != null) {
                target.impregnateReservable.reserve(HusbandryMob.this);
            }
            if (!HusbandryMob.this.isServer()) {
                if (GameRandom.globalRandom.nextInt(20) == 0) {
                    HusbandryMob.this.spawnLoveParticles();
                }
                if (target != null && GameRandom.globalRandom.nextInt(20) == 0) {
                    target.spawnLoveParticles();
                }
            }
        }

        @Override
        public void onCompleted() {
            super.onCompleted();
            HusbandryMob target = (HusbandryMob)this.targetMob.get(HusbandryMob.this.getLevel());
            if (target != null && !HusbandryMob.this.isClient()) {
                target.refreshBirthingCooldown();
                HusbandryMob.this.refreshBirthingCooldown();
                HusbandryMob.this.sendMovementPacket(false);
                target.sendMovementPacket(false);
                target.onImpregnated(HusbandryMob.this);
                HusbandryMob.this.ai.blackboard.submitEvent("wanderNow", new AIEvent());
                target.ai.blackboard.submitEvent("wanderNow", new AIEvent());
            }
            if (!HusbandryMob.this.isServer()) {
                for (int i = 0; i < 5; ++i) {
                    HusbandryMob.this.spawnLoveParticles();
                    if (target == null) continue;
                    target.spawnLoveParticles();
                }
            }
        }
    };
    public StartMobProcessMobMobAbility<HusbandryMob, HusbandryMob> impregnateMobAbility;
    public GameObjectReservable impregnateReservable = new GameObjectReservable();

    public HusbandryMob(int health) {
        super(health);
        this.impregnateMobAbility = this.registerAbility(new StartMobProcessMobMobAbility<HusbandryMob, HusbandryMob>(this.impregnateHandler));
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addFloat("hunger", this.hunger);
        save.addFloat("tameness", this.tameness);
        save.addLong("growUpTime", this.growUpTime);
        save.addLong("nextBirthTime", this.nextBirthTime);
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.hunger = save.getFloat("hunger", this.hunger, false);
        this.tameness = save.getFloat("tameness", this.tameness, false);
        this.growUpTime = save.getLong("growUpTime", this.growUpTime, false);
        this.nextBirthTime = save.getLong("nextBirthTime", this.nextBirthTime, false);
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextFloat(this.tameness);
        writer.putNextLong(this.growUpTime);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.tameness = reader.getNextFloat();
        this.growUpTime = reader.getNextLong();
    }

    @Override
    public void setupMovementPacket(PacketWriter writer) {
        super.setupMovementPacket(writer);
        writer.putNextFloat(this.hunger);
        if (this.canBirth() || this.canImpregnate()) {
            writer.putNextBoolean(true);
            writer.putNextLong(this.nextBirthTime);
        } else {
            writer.putNextBoolean(false);
        }
    }

    @Override
    public void applyMovementPacket(PacketReader reader, boolean isDirect) {
        super.applyMovementPacket(reader, isDirect);
        this.hunger = reader.getNextFloat();
        this.nextBirthTime = reader.getNextBoolean() ? reader.getNextLong() : 0L;
    }

    @Override
    public void clientTick() {
        super.clientTick();
        this.tickHungerAndTameness();
        this.impregnateHandler.tick();
    }

    @Override
    public void serverTick() {
        super.serverTick();
        this.tickHungerAndTameness();
        this.impregnateHandler.tick();
    }

    public void tickHungerAndTameness() {
        if (this.hunger > 0.0f) {
            int secondsPerHour = 3600;
            float hungerLossPerSecond = this.hungerLossPerHour / (float)secondsPerHour;
            float hungerLossPerTick = hungerLossPerSecond / 20.0f;
            this.hunger = Math.max(this.hunger - hungerLossPerTick, 0.0f);
            if (this.tameness < 1.0f) {
                float tameGainPerSecond = this.tamenessGainPerHour / (float)secondsPerHour;
                float tameGainPerTick = tameGainPerSecond / 20.0f;
                this.tameness = Math.min(this.tameness + tameGainPerTick, 1.0f);
            }
        } else if (this.tameness > 0.0f) {
            int secondsPerHour = 3600;
            float tameLossPerSecond = this.tamenessLossPerHour / (float)secondsPerHour;
            float tameLossPerTick = tameLossPerSecond / 20.0f;
            this.tameness = Math.max(this.tameness - tameLossPerTick, 0.0f);
        }
    }

    public void startBaby() {
        this.growUpTime = this.getWorldTime() + (long)this.timeToGrowUp;
        this.tameness = 1.0f;
        this.hunger = 1.0f;
    }

    public void setTameness(float percent) {
        this.tameness = percent;
        this.hunger = 1.0f;
    }

    public void setImported() {
        this.tameness = 1.0f;
        this.hunger = 1.0f;
    }

    public boolean canBeUsedAsSettlerThought() {
        return this.tameness > 0.0f || !this.isGrown();
    }

    public List<HusbandryMob> getNearbyHusbandryMobs() {
        Predicate<Mob> test;
        SubRegion subregion = this.getLevel().regionManager.getSubRegionByTile(this.getTileX(), this.getTileY());
        if (subregion != null) {
            ConnectedSubRegionsResult connected = subregion.getAllConnected(sr -> sr.getType() == RegionType.OPEN || sr.getType() == RegionType.SOLID, 500);
            if (connected.size >= 500) {
                test = m -> true;
            } else {
                Set regionIDs = connected.connectedRegions.stream().map(SubRegion::getRegionID).collect(Collectors.toSet());
                test = m -> regionIDs.contains(this.getLevel().getRegionID(m.getTileX(), m.getTileY()));
            }
        } else {
            test = m -> true;
        }
        return this.getLevel().entityManager.mobs.getInRegionByTileRange(this.getTileX(), this.getTileY(), maxCloseMobsToBirthTileRange).stream().filter(m -> m != this).filter(test).filter(m -> m instanceof HusbandryMob).map(m -> (HusbandryMob)m).filter(m -> GameMath.diagonalMoveDistance(this.getX(), this.getY(), m.getX(), m.getY()) <= (double)(maxCloseMobsToBirthTileRange * 32)).collect(Collectors.toList());
    }

    @Override
    public InventoryItem onFed(InventoryItem item) {
        this.hunger += this.defaultHungerGainPerFeed;
        this.sendMovementPacket(false);
        item.setAmount(item.getAmount() - 1);
        return item;
    }

    @Override
    public boolean canFeed(InventoryItem item) {
        return !this.isOnFeedCooldown() && item.item instanceof GrainItem;
    }

    @Override
    public boolean isOnFeedCooldown() {
        return this.hunger > 1.0f;
    }

    public boolean isGrown() {
        return this.growUpTime <= this.getWorldTime();
    }

    public boolean canBirth() {
        return this.getGender() == HumanGender.FEMALE && this.isGrown() && this.tameness >= 1.0f;
    }

    public boolean canImpregnate() {
        return this.isGrown() && this.getGender() == HumanGender.MALE && this.tameness >= 1.0f;
    }

    public boolean isReservedForImpregnating() {
        return !this.impregnateReservable.isAvailable(this.impregnateReservable, this.getWorldEntity());
    }

    public boolean isImpregnating() {
        return this.impregnateHandler.isInProgress();
    }

    public boolean canImpregnateMob(HusbandryMob other) {
        return false;
    }

    public void onImpregnated(HusbandryMob father) {
        Mob child = MobRegistry.getMob(this.getRandomChildMobStringID(father), this.getLevel());
        if (child instanceof HusbandryMob) {
            ((HusbandryMob)child).startBaby();
        }
        ArrayList<Point> possibleSpawnPoints = new ArrayList<Point>();
        for (int x = -1; x <= 1; ++x) {
            for (int y = -1; y <= 1; ++y) {
                if (x == 0 && y == 0) continue;
                Point point = new Point(this.getX() + x * 4, this.getY() + y * 4);
                if (child.collidesWith(this.getLevel(), point.x, point.y)) continue;
                possibleSpawnPoints.add(point);
            }
        }
        Point spawnPoint = (Point)GameRandom.globalRandom.getOneOf(possibleSpawnPoints);
        if (spawnPoint == null) {
            spawnPoint = new Point(this.getX(), this.getY());
        }
        this.getLevel().entityManager.addMob(child, spawnPoint.x, spawnPoint.y);
    }

    public String getRandomChildMobStringID(HusbandryMob father) {
        return this.getStringID();
    }

    public void spawnLoveParticles() {
        float posX = this.x + (float)GameRandom.globalRandom.getIntBetween(-10, 10);
        float posY = this.y + (float)GameRandom.globalRandom.getIntBetween(-5, 5);
        int startHeight = 8 + GameRandom.globalRandom.nextInt(24);
        int lifeTime = GameRandom.globalRandom.getIntBetween(2500, 3500);
        final int swing = GameRandom.globalRandom.getIntBetween(5, 10);
        final float swingOffset = GameRandom.globalRandom.nextFloat();
        float moveX = GameRandom.globalRandom.floatGaussian() * 2.0f;
        float moveY = GameRandom.globalRandom.floatGaussian() * 1.5f;
        final ParticleOption.FrictionMover frictionMover = new ParticleOption.FrictionMover(moveX, moveY, 0.0f);
        this.getLevel().entityManager.addParticle(posX, posY, Particle.GType.COSMETIC).sprite(GameResources.heartParticle).dontRotate().color(new Color(255, 255, 255)).heightMoves(startHeight, startHeight + 40).moves(new ParticleOption.Mover(){

            @Override
            public void tick(Point2D.Float pos, float delta, int lifeTime, int timeAlive, float lifePercent) {
                frictionMover.tick(pos, delta, lifeTime, timeAlive, lifePercent);
                float angle = (lifePercent + swingOffset) * 500.0f;
                pos.x += GameMath.sin(angle) * (float)swing * delta / 250.0f;
            }
        }).lifeTime(lifeTime);
    }

    public void refreshBirthingCooldown() {
        this.nextBirthTime = this.birthingCooldown < 0 ? -1L : this.getWorldTime() + (long)this.birthingCooldown;
    }

    public boolean isOnBirthingCooldown() {
        return this.nextBirthTime < 0L || this.getWorldTime() <= this.nextBirthTime;
    }

    public abstract HumanGender getGender();

    public boolean canMilk(InventoryItem item) {
        return false;
    }

    public InventoryItem onMilk(InventoryItem item, List<InventoryItem> products) {
        return item;
    }

    public boolean canShear(InventoryItem item) {
        return false;
    }

    public InventoryItem onShear(InventoryItem item, List<InventoryItem> products) {
        return item;
    }

    @Override
    protected void addHoverTooltips(ListGameTooltips tooltips, boolean debug) {
        super.addHoverTooltips(tooltips, debug);
        if (this.tameness > 0.0f && this.hunger <= 0.0f) {
            tooltips.add(Localization.translate("misc", "animalhungry"));
        }
        if (this.tameness >= 1.0f) {
            tooltips.add(Localization.translate("misc", "animaltame"));
        } else if (this.tameness > 0.0f) {
            int tamenessPercent = (int)(this.tameness * 100.0f);
            tooltips.add(Localization.translate("misc", "animaltameness", "percent", (Object)tamenessPercent));
        }
    }

    @Override
    protected void addDebugTooltips(ListGameTooltips tooltips) {
        super.addDebugTooltips(tooltips);
        if (this.isOnBirthingCooldown()) {
            tooltips.add("Birth cooldown: " + GameUtils.getTimeStringMillis(this.nextBirthTime - this.getWorldTime()));
        }
        if (!this.isGrown()) {
            tooltips.add("Grown in: " + GameUtils.getTimeStringMillis(this.growUpTime - this.getWorldTime()) + " to " + MobRegistry.getDisplayName(this.getID()));
        }
        tooltips.add("Hunger: " + this.hunger);
        tooltips.add("Tameness: " + this.tameness);
        if (this.isServer()) {
            tooltips.add("Slaughter job: " + this.slaughterJob);
            tooltips.add("Milk job: " + this.milkJob);
            tooltips.add("Shear job: " + this.shearJob);
        }
    }
}

