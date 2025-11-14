/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent;

import java.util.ArrayList;
import java.util.stream.Stream;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameMath;
import necesse.entity.levelEvent.IncursionLevelEvent;
import necesse.entity.manager.ObjectDestroyedListenerEntityComponent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.levelBuffManager.LevelBuffsEntityComponent;
import necesse.level.maps.levelBuffManager.LevelModifiers;
import necesse.level.maps.levelBuffManager.MobBuffsEntityComponent;

public class ExtractionIncursionEvent
extends IncursionLevelEvent
implements ObjectDestroyedListenerEntityComponent,
MobBuffsEntityComponent,
LevelBuffsEntityComponent {
    public int startObjects = -1;
    public int doneAtRemainingObjects = -1;
    public int remainingObjects = -1;
    public int mobSpawnsGraceTime = 3000;
    public long lastObjectDestroyedTime;
    public int startMobSpawns = 20;
    public int maxMobsSpawns = 220;
    public int currentMobsSpawned = 0;

    public ExtractionIncursionEvent() {
    }

    public ExtractionIncursionEvent(String bossStringID) {
        super(bossStringID);
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addInt("startObjects", this.startObjects);
        save.addInt("doneAtRemainingObjects", this.doneAtRemainingObjects);
        save.addInt("currentMobsSpawned", this.currentMobsSpawned);
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.startObjects = save.getInt("startObjects", this.startObjects, false);
        this.doneAtRemainingObjects = save.getInt("doneAtRemainingObjects", this.doneAtRemainingObjects, false);
        this.currentMobsSpawned = save.getInt("currentMobsSpawned", this.currentMobsSpawned, false);
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextInt(this.startObjects);
        writer.putNextInt(this.doneAtRemainingObjects);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.startObjects = reader.getNextInt();
        this.doneAtRemainingObjects = reader.getNextInt();
    }

    @Override
    public void setupUpdatePacket(PacketWriter writer) {
        super.setupUpdatePacket(writer);
        writer.putNextInt(this.remainingObjects);
        writer.putNextInt(this.currentMobsSpawned);
    }

    @Override
    public void applyUpdatePacket(PacketReader reader) {
        super.applyUpdatePacket(reader);
        this.remainingObjects = reader.getNextInt();
        this.currentMobsSpawned = reader.getNextInt();
    }

    @Override
    public void init() {
        super.init();
        if (!this.isClient()) {
            this.remainingObjects = 0;
            this.level.regionManager.ensureEntireLevelIsLoaded();
            for (int x = 0; x < this.level.tileWidth; ++x) {
                for (int y = 0; y < this.level.tileHeight; ++y) {
                    if (!this.level.getObject((int)x, (int)y).isIncursionExtractionObject || this.level.objectLayer.isPlayerPlaced(x, y)) continue;
                    ++this.remainingObjects;
                }
            }
            if (this.startObjects == -1 || this.doneAtRemainingObjects == -1) {
                this.startObjects = this.remainingObjects;
                this.doneAtRemainingObjects = this.startObjects / 5;
            }
        }
    }

    @Override
    public void onObjectDestroyed(GameObject object, int layerID, int tileX, int tileY, ServerClient client, ArrayList<ItemPickupEntity> itemsDropped) {
        if (object.isIncursionExtractionObject && !this.level.objectLayer.isPlayerPlaced(tileX, tileY)) {
            this.lastObjectDestroyedTime = this.getTime();
            --this.remainingObjects;
            this.isDirty = true;
        }
    }

    @Override
    public void onMobSpawned(Mob mob) {
        super.onMobSpawned(mob);
        if (mob.isHostile) {
            ++this.currentMobsSpawned;
            this.isDirty = true;
        }
    }

    public float getPercentProgress() {
        return 1.0f - GameMath.limit((float)(this.remainingObjects - this.doneAtRemainingObjects) / (float)(this.startObjects - this.doneAtRemainingObjects), 0.0f, 1.0f);
    }

    @Override
    public Stream<ModifierValue<?>> getLevelModifiers() {
        if (this.isDone || this.isFighting || this.bossPortalSpawned) {
            return Stream.of(new ModifierValue<Boolean>(LevelModifiers.ENEMIES_RETREATING, true));
        }
        return Stream.empty();
    }

    @Override
    public Stream<ModifierValue<?>> getLevelModifiers(Mob mob) {
        if (this.isDone || this.isFighting || this.bossPortalSpawned) {
            if (mob.isPlayer) {
                return Stream.of(new ModifierValue<Float>(BuffModifiers.MOB_SPAWN_RATE, Float.valueOf(0.0f)).max(Float.valueOf(0.0f), 1000000));
            }
        } else {
            if (mob.isPlayer) {
                ModifierValue<Float> spawnRateModifier;
                float percent = (float)Math.pow(this.getPercentProgress(), 2.0);
                int expectedMobSpawns = this.startMobSpawns + Math.round((float)this.maxMobsSpawns * percent);
                if (this.currentMobsSpawned >= expectedMobSpawns || this.lastObjectDestroyedTime + (long)this.mobSpawnsGraceTime >= this.getTime()) {
                    spawnRateModifier = new ModifierValue<Float>(BuffModifiers.MOB_SPAWN_RATE, Float.valueOf(0.0f)).max(Float.valueOf(0.0f), 1000000);
                } else {
                    int delta = expectedMobSpawns - this.currentMobsSpawned;
                    float rate = (float)delta / 25.0f + this.getPercentProgress();
                    spawnRateModifier = new ModifierValue<Float>(BuffModifiers.MOB_SPAWN_RATE, Float.valueOf(rate));
                }
                return Stream.of(spawnRateModifier, new ModifierValue<Integer>(BuffModifiers.MOB_SPAWN_LIGHT_THRESHOLD).min(150));
            }
            if (mob.isHostile) {
                return Stream.of(new ModifierValue<Float>(BuffModifiers.CHASER_RANGE, Float.valueOf(0.5f + this.getPercentProgress())));
            }
        }
        return Stream.empty();
    }

    @Override
    public boolean isObjectiveDone() {
        return this.remainingObjects <= this.doneAtRemainingObjects;
    }

    @Override
    public int getObjectiveCurrent() {
        return this.startObjects - this.remainingObjects;
    }

    @Override
    public int getObjectiveMax() {
        return this.startObjects - this.doneAtRemainingObjects;
    }
}

