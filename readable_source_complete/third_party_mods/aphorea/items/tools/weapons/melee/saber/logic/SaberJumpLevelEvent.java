/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.network.PacketReader
 *  necesse.engine.network.PacketWriter
 *  necesse.engine.registries.BuffRegistry
 *  necesse.engine.util.GameMath
 *  necesse.engine.util.GameRandom
 *  necesse.entity.levelEvent.mobAbilityLevelEvent.MobAbilityLevelEvent
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.MobHitCooldowns
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.level.maps.Level
 *  necesse.level.maps.hudManager.HudDrawElement
 */
package aphorea.items.tools.weapons.melee.saber.logic;

import aphorea.patches.PlayerFlyingHeight;
import aphorea.registry.AphBuffs;
import aphorea.utils.AphColors;
import aphorea.utils.area.AphArea;
import aphorea.utils.area.AphAreaList;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobAbilityLevelEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobHitCooldowns;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.level.maps.Level;
import necesse.level.maps.hudManager.HudDrawElement;

public class SaberJumpLevelEvent
extends MobAbilityLevelEvent {
    protected float initialX;
    protected float initialY;
    protected float dirX;
    protected float dirY;
    protected float distance;
    protected long startTime;
    protected int lastProcessTime;
    protected int animTime;
    protected GameDamage damage;
    protected MobHitCooldowns hitCooldowns;
    protected HudDrawElement hudDrawElement;
    boolean alreadyArea = false;
    private static final int[][] NEIGHBOR_OFFSETS = new int[][]{{0, 0}, {1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    public SaberJumpLevelEvent() {
    }

    public SaberJumpLevelEvent(Mob owner, int seed, float dirX, float dirY, float distance, int animTime, GameDamage damage) {
        super(owner, new GameRandom((long)seed));
        this.initialX = owner.x;
        this.initialY = owner.y;
        this.dirX = dirX;
        this.dirY = dirY;
        this.distance = distance;
        this.startTime = owner.getTime();
        this.animTime = animTime;
        this.damage = damage;
    }

    public boolean isNetworkImportant() {
        return true;
    }

    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextFloat(this.initialX);
        writer.putNextFloat(this.initialY);
        writer.putNextFloat(this.dirX);
        writer.putNextFloat(this.dirY);
        writer.putNextFloat(this.distance);
        writer.putNextLong(this.startTime);
        writer.putNextInt(this.lastProcessTime);
        writer.putNextInt(this.animTime);
        if (this.damage != null) {
            writer.putNextBoolean(true);
            this.damage.writePacket(writer);
        } else {
            writer.putNextBoolean(false);
        }
    }

    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.initialX = reader.getNextFloat();
        this.initialY = reader.getNextFloat();
        this.dirX = reader.getNextFloat();
        this.dirY = reader.getNextFloat();
        this.distance = reader.getNextFloat();
        this.startTime = reader.getNextLong();
        this.lastProcessTime = reader.getNextInt();
        this.animTime = reader.getNextInt();
        if (reader.getNextBoolean()) {
            this.damage = GameDamage.fromReader((PacketReader)reader);
        }
    }

    public void init() {
        super.init();
        this.hitCooldowns = new MobHitCooldowns();
        if (this.owner != null) {
            this.owner.buffManager.addBuff(new ActiveBuff(BuffRegistry.INVULNERABLE_ACTIVE, this.owner, this.animTime + 200, null), false);
            this.owner.addBuff(new ActiveBuff(AphBuffs.SABER_DASH_ACTIVE, this.owner, this.animTime, null), false);
        }
    }

    public void tickMovement(float delta) {
        super.tickMovement(delta);
        if (this.owner != null && !this.owner.removed()) {
            int timeProgress = (int)Math.min(this.getTime() - this.startTime, (long)this.animTime);
            if (this.lastProcessTime < timeProgress) {
                double lastPercentToMove = this.getMoveCurve((double)this.lastProcessTime / (double)this.animTime);
                double nextPercentToMove = this.getMoveCurve((double)timeProgress / (double)this.animTime);
                double percentToMove = nextPercentToMove - lastPercentToMove;
                float fullDistanceToMove = (float)((double)this.distance * percentToMove);
                this.setOwnerPos(this.owner.x + this.dirX * fullDistanceToMove, this.owner.y + this.dirY * fullDistanceToMove);
                this.lastProcessTime = timeProgress;
                PlayerFlyingHeight.playersFlyingHeight.put(this.owner.getUniqueID(), (int)this.getMoveCurve(Math.sin((double)((float)timeProgress / (float)this.animTime) * Math.PI) * 10000.0));
            }
            if (timeProgress >= this.animTime) {
                this.over();
            }
        } else {
            this.over();
        }
    }

    public void setOwnerPos(float x, float y) {
        this.owner.setPos(x, y, this.owner.isSmoothSnapped() || GameMath.squareDistance((float)this.owner.x, (float)this.owner.y, (float)x, (float)y) < 4.0f);
    }

    protected double getMoveCurve(double x) {
        return Math.pow(x, 0.5);
    }

    private boolean checkNeighborTiles(Level level, int tileX, int tileY) {
        for (int[] offset : NEIGHBOR_OFFSETS) {
            if (level.isSolidTile(tileX + offset[0], tileY + offset[1])) continue;
            return false;
        }
        return true;
    }

    public AphAreaList getAreaList(GameDamage damage) {
        return new AphAreaList(new AphArea(100.0f, AphColors.black).setDamageArea(damage));
    }

    public void over() {
        super.over();
        if (this.hudDrawElement != null) {
            this.hudDrawElement.remove();
        }
        if (!this.alreadyArea) {
            this.alreadyArea = true;
            this.getAreaList(this.damage).execute(this.owner, false);
        }
        if (!this.checkNeighborTiles(this.level, (int)(this.initialX / 32.0f), (int)(this.initialY / 32.0f)) && this.checkNeighborTiles(this.owner.getLevel(), this.owner.getX() / 32, this.owner.getY() / 32)) {
            this.setOwnerPos(this.initialX, this.initialY);
        }
        PlayerFlyingHeight.playersFlyingHeight.put(this.owner.getUniqueID(), 0);
    }
}

