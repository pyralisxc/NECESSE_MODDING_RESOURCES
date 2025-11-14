/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import necesse.engine.GameTileRange;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.entity.events.BooleanEntityEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.level.gameObject.InvisibleTriggerObject;
import necesse.level.maps.Level;

public class InvisibleTriggerObjectEntity
extends ObjectEntity {
    public GameTileRange range;
    public GameTileRange triggerNearbyRange;
    public boolean requirePath;
    public boolean detectPlayers;
    public boolean detectSettlers;
    public boolean destroyOnTrigger;
    public boolean detectHostileMobs;
    public boolean detectPassiveMobs;
    public boolean detectAllHumans;
    public InvisibleTriggerObject.TriggerFunction onTrigger;
    protected boolean triggered;
    protected boolean triggeredFromNearby;
    protected Mob triggeredNearbyMob;
    protected final BooleanEntityEvent triggeredEvent;

    public InvisibleTriggerObjectEntity(Level level, int x, int y, GameTileRange tileRange, GameTileRange triggerNearbyTileRange, boolean detectHostileMobs, boolean detectPassiveMobs, boolean detectAllHumans, boolean detectPlayers, boolean detectSettlers, boolean destroyOnTrigger, boolean requirePath, InvisibleTriggerObject.TriggerFunction onTrigger) {
        super(level, "invisibletrigger", x, y);
        this.range = tileRange;
        this.triggerNearbyRange = triggerNearbyTileRange;
        this.detectHostileMobs = detectHostileMobs;
        this.detectPassiveMobs = detectPassiveMobs;
        this.detectAllHumans = detectAllHumans;
        this.detectPlayers = detectPlayers;
        this.detectSettlers = detectSettlers;
        this.destroyOnTrigger = destroyOnTrigger;
        this.requirePath = requirePath;
        this.onTrigger = onTrigger;
        this.triggeredEvent = this.registerEvent(new BooleanEntityEvent(){

            @Override
            protected void run(boolean value) {
                InvisibleTriggerObjectEntity.this.triggered = value;
            }
        });
    }

    @Override
    public void serverTick() {
        super.serverTick();
        Performance.record((PerformanceTimerManager)this.getLevel().tickManager(), "triggerObjectEntity", () -> {
            boolean wasTriggered = this.triggered;
            Mob mobThatTriggered = this.checkIfTriggered();
            if (!wasTriggered && mobThatTriggered != null) {
                this.triggeredEvent.runAndSend(true);
                if (this.onTrigger != null) {
                    this.onTrigger.onTrigger(this.getLevel(), this.tileX, this.tileY, mobThatTriggered);
                    if (this.triggerNearbyRange.maxRange > 0) {
                        Level level = this.getLevel();
                        int id = level.getObjectID(this.tileX, this.tileY);
                        int regionId = level.getRegionID(this.tileX, this.tileY);
                        this.triggerNearbyRange.streamValidTiles(this.tileX, this.tileY).filter(pos -> level.getObjectID(pos.x, pos.y) == id && level.getRegionID(pos.x, pos.y) == regionId).forEach(pos -> level.entityManager.getObjectEntity(pos.x, pos.y, InvisibleTriggerObjectEntity.class).triggerFromNearby(mobThatTriggered));
                    }
                }
                if (this.destroyOnTrigger) {
                    this.getLevel().setObject(this.tileX, this.tileY, 0);
                    if (this.isServer()) {
                        this.getLevel().sendObjectUpdatePacket(0, this.tileX, this.tileY);
                    }
                }
            } else if (wasTriggered && mobThatTriggered == null) {
                this.triggeredEvent.runAndSend(false);
            }
        });
    }

    protected Mob checkIfTriggered() {
        if (this.triggeredFromNearby) {
            this.triggeredFromNearby = false;
            return this.triggeredNearbyMob;
        }
        if (this.range.maxRange == 0) {
            return null;
        }
        return this.getLevel().entityManager.streamAreaMobsAndPlayers(this.tileX * 32, this.tileY * 32, this.range.maxRange * 32).filter(m -> InvisibleTriggerObjectEntity.canTriggerByMob(m, this.detectHostileMobs, this.detectPassiveMobs, this.detectAllHumans, this.detectPlayers, this.detectSettlers) && this.range.isWithinRange(this.tileX, this.tileY, m.getTileX(), m.getTileY()) && (!this.requirePath || m.estimateCanMoveTo(this.tileX, this.tileY, true))).findFirst().orElse(null);
    }

    public static boolean canTriggerByMob(Mob mob, boolean detectHostileMobs, boolean detectPassiveMobs, boolean detectAllHumans, boolean detectPlayers, boolean detectSettlers) {
        if (!mob.canLevelInteract()) {
            return false;
        }
        return detectHostileMobs && mob.isHostile || detectPassiveMobs && !mob.isHostile || detectAllHumans && mob.isHuman || detectPlayers && mob.isPlayer || detectSettlers && mob instanceof HumanMob && ((HumanMob)mob).isSettler();
    }

    public void triggerFromNearby(Mob mob) {
        this.triggeredFromNearby = true;
        this.triggeredNearbyMob = mob;
    }

    public boolean isBeingTriggered() {
        return this.triggered;
    }
}

