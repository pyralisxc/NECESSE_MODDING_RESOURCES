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
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameMath;
import necesse.entity.levelEvent.AscendedBlackHoleEvent;
import necesse.entity.levelEvent.IncursionLevelEvent;
import necesse.entity.manager.ObjectDestroyedListenerEntityComponent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.AscendedWizardMob;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.AscendedWizardPeripheralMob;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.levelBuffManager.LevelBuffsEntityComponent;
import necesse.level.maps.levelBuffManager.LevelModifiers;
import necesse.level.maps.levelBuffManager.MobBuffsEntityComponent;

public class AscendedIncursionEvent
extends IncursionLevelEvent
implements ObjectDestroyedListenerEntityComponent,
MobBuffsEntityComponent,
LevelBuffsEntityComponent {
    public int pylonObjectID = ObjectRegistry.getObjectID("ascendedpylon");
    public int startObjects = -1;
    public int remainingObjects = -1;
    public int peripheralWizardUniqueID;
    public boolean hasSpawnedBlackHole = false;

    public AscendedIncursionEvent() {
    }

    public AscendedIncursionEvent(String bossStringID) {
        super(bossStringID);
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addInt("startObjects", this.startObjects);
        save.addBoolean("hasSpawnedBlackHole", this.hasSpawnedBlackHole);
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.startObjects = save.getInt("startObjects", this.startObjects, false);
        this.hasSpawnedBlackHole = save.getBoolean("hasSpawnedBlackHole", this.hasSpawnedBlackHole, false);
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextInt(this.startObjects);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.startObjects = reader.getNextInt();
    }

    @Override
    public void setupUpdatePacket(PacketWriter writer) {
        super.setupUpdatePacket(writer);
        writer.putNextInt(this.remainingObjects);
    }

    @Override
    public void applyUpdatePacket(PacketReader reader) {
        super.applyUpdatePacket(reader);
        this.remainingObjects = reader.getNextInt();
    }

    @Override
    public void init() {
        super.init();
        if (!this.isClient()) {
            this.remainingObjects = 0;
            this.level.regionManager.ensureEntireLevelIsLoaded();
            for (int x = 0; x < this.level.tileWidth; ++x) {
                for (int y = 0; y < this.level.tileHeight; ++y) {
                    if (this.level.getObjectID(x, y) != this.pylonObjectID) continue;
                    ++this.remainingObjects;
                }
            }
            if (this.startObjects == -1) {
                this.startObjects = this.remainingObjects;
            }
        }
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (!this.isObjectiveDone()) {
            Mob mob = this.level.entityManager.mobs.get(this.peripheralWizardUniqueID, false);
            if (mob instanceof AscendedWizardPeripheralMob) {
                ((AscendedWizardPeripheralMob)mob).removeBuffer = 0;
            } else {
                AscendedWizardPeripheralMob newMob = new AscendedWizardPeripheralMob();
                newMob.setLevel(this.level);
                newMob.setPos((float)(this.level.tileWidth * 32) / 2.0f, (float)(this.level.tileHeight * 32) / 2.0f, true);
                this.level.entityManager.mobs.add(newMob);
                this.peripheralWizardUniqueID = newMob.getUniqueID();
            }
        }
    }

    @Override
    public void onObjectDestroyed(GameObject object, int layerID, int tileX, int tileY, ServerClient client, ArrayList<ItemPickupEntity> itemsDropped) {
        if (object.getID() == this.pylonObjectID) {
            --this.remainingObjects;
            this.isDirty = true;
        }
    }

    public float getPercentProgress() {
        return 1.0f - GameMath.limit((float)this.remainingObjects / (float)this.startObjects, 0.0f, 1.0f);
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
                return Stream.of(new ModifierValue<Integer>(BuffModifiers.MOB_SPAWN_LIGHT_THRESHOLD).min(150), new ModifierValue<Float>(BuffModifiers.MOB_SPAWN_RATE, Float.valueOf(0.0f)).max(Float.valueOf(0.0f), 1000000));
            }
            if (mob.isHostile) {
                return Stream.of(new ModifierValue<Float>(BuffModifiers.CHASER_RANGE, Float.valueOf(10.0f)), new ModifierValue<Boolean>(BuffModifiers.CAN_BREAK_OBJECTS, true));
            }
        }
        return Stream.empty();
    }

    @Override
    public void onObjectiveCompleted() {
        this.bossPortalSpawned = true;
        this.countdownStarted = true;
        this.countdownTimer = this.countdownTotal;
    }

    @Override
    public Mob spawnBossAtPosition(int levelX, int levelY) {
        int tileX = GameMath.getTileCoordinate(levelX);
        int tileY = GameMath.getTileCoordinate(levelY);
        if (this.hasSpawnedBlackHole) {
            AscendedWizardMob wizardMob = new AscendedWizardMob();
            wizardMob.setLevel(this.level);
            wizardMob.onSpawned(tileX * 32 + 16, (tileY - 4) * 32 + 16);
            wizardMob.setSpawnTilePosition(tileX, tileY);
            this.level.entityManager.mobs.add(wizardMob);
            return wizardMob;
        }
        AscendedBlackHoleEvent event = new AscendedBlackHoleEvent(tileX, tileY, this.getTime() + 15000L);
        this.level.entityManager.events.add(event);
        this.hasSpawnedBlackHole = true;
        return event.wizardMob;
    }

    @Override
    public boolean isObjectiveDone() {
        return this.remainingObjects <= 0;
    }

    @Override
    public int getObjectiveCurrent() {
        return this.startObjects - this.remainingObjects;
    }

    @Override
    public int getObjectiveMax() {
        return this.startObjects;
    }
}

