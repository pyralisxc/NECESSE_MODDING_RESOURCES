/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.particle.Particle;
import necesse.level.maps.LevelObject;

public class FlameTrapEvent
extends LevelEvent
implements Attacker {
    public static GameDamage damage = new GameDamage(30.0f, 100.0f, 0.0f, 2.0f, 1.0f);
    private int tileX;
    private int tileY;
    private int dir;
    private int ticks;
    private final int range;
    private int maxRange;
    private final ArrayList<Integer> hits;

    public FlameTrapEvent() {
        this.maxRange = this.range = 8;
        this.hits = new ArrayList();
    }

    public FlameTrapEvent(int tileX, int tileY, int dir) {
        this.tileX = tileX;
        this.tileY = tileY;
        this.dir = Math.abs(dir) % 4;
        this.maxRange = this.range = 8;
        this.hits = new ArrayList();
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.tileX = reader.getNextInt();
        this.tileY = reader.getNextInt();
        this.dir = reader.getNextByte();
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextInt(this.tileX);
        writer.putNextInt(this.tileY);
        writer.putNextByte((byte)this.dir);
    }

    @Override
    public void init() {
        super.init();
        this.ticks = 0;
    }

    @Override
    public void clientTick() {
        Point dir = this.getDir(this.dir);
        int currentRange = this.ticks % this.range;
        if (currentRange == 0) {
            this.maxRange = this.range;
        }
        int currentTileX = this.tileX + dir.x * currentRange;
        int currentTileY = this.tileY + dir.y * currentRange;
        if (this.maxRange >= currentRange && !this.level.getObject((int)currentTileX, (int)currentTileY).isWall && !this.level.getObject((int)currentTileX, (int)currentTileY).isRock) {
            for (int i = 0; i < 5; ++i) {
                Particle.GType gType = i <= 2 ? Particle.GType.CRITICAL : Particle.GType.COSMETIC;
                this.level.entityManager.addParticle(currentTileX * 32 + 16 + (int)(GameRandom.globalRandom.nextGaussian() * 8.0), currentTileY * 32 + 16 + (int)(GameRandom.globalRandom.nextGaussian() * 8.0), gType).movesConstant((float)dir.x * GameRandom.globalRandom.nextFloat() * 10.0f, (float)dir.y * GameRandom.globalRandom.nextFloat() * 10.0f).color(new Color(255, 186, 0)).givesLight(0.0f, 0.5f).height(12.0f);
            }
        } else {
            this.maxRange = currentRange;
        }
        ++this.ticks;
        if (this.ticks >= this.range * 2) {
            this.over();
        }
    }

    @Override
    public void serverTick() {
        Point dir = this.getDir(this.dir);
        int currentRange = this.ticks % this.range;
        if (currentRange == 0) {
            this.hits.clear();
            this.maxRange = this.range;
        }
        int currentTileX = this.tileX + dir.x * currentRange;
        int currentTileY = this.tileY + dir.y * currentRange;
        if (this.maxRange >= currentRange && !this.level.getObject((int)currentTileX, (int)currentTileY).isWall && !this.level.getObject((int)currentTileX, (int)currentTileY).isRock) {
            Rectangle hitBox = new Rectangle(currentTileX * 32, currentTileY * 32, 32, 32);
            LevelObject lo = this.level.getLevelObject(currentTileX, currentTileY);
            if (lo.object.attackThrough) {
                if (lo.getAttackThroughCollisions().stream().anyMatch(hitBox::intersects)) {
                    lo.attackThrough(damage, this);
                }
            }
            for (Mob target : this.getTargets(hitBox)) {
                if (!target.canBeHit(this) || !hitBox.intersects(target.getHitBox())) continue;
                this.hit(target);
            }
        } else {
            this.maxRange = currentRange;
        }
        ++this.ticks;
        if (this.ticks >= this.range * 2) {
            this.over();
        }
    }

    private void hit(Mob target) {
        if (this.hits.contains(target.getUniqueID())) {
            return;
        }
        if (target.getLevel().isTrialRoom) {
            GameDamage trialDamage = new GameDamage(DamageTypeRegistry.TRUE, (float)target.getMaxHealth() / 4.0f);
            target.isServerHit(trialDamage, 0.0f, 0.0f, 0.0f, this);
            this.hits.add(target.getUniqueID());
        } else {
            target.isServerHit(damage, 0.0f, 0.0f, 0.0f, this);
            ActiveBuff ab = new ActiveBuff("onfire", target, 10.0f, (Attacker)this);
            target.addBuff(ab, true);
            this.hits.add(target.getUniqueID());
        }
    }

    private ArrayList<Mob> getTargets(Rectangle hitbox) {
        ArrayList<Mob> out = this.level.entityManager.mobs.getInRegionRangeByTile(GameMath.getTileCoordinate(hitbox.getCenterX()), GameMath.getTileCoordinate(hitbox.getCenterY()), 1);
        if (this.level.isServer()) {
            this.level.getServer().streamClients().filter(sc -> sc != null && sc.playerMob != null).filter(sc -> sc.isSamePlace(this.getLevel())).forEach(sc -> out.add(sc.playerMob));
        }
        return out;
    }

    @Override
    public GameMessage getAttackerName() {
        return new LocalMessage("deaths", "flametrapname");
    }

    @Override
    public DeathMessageTable getDeathMessages() {
        return this.getDeathMessages("flametrap", 2);
    }

    @Override
    public Mob getFirstAttackOwner() {
        return null;
    }

    @Override
    public boolean isTrapAttacker() {
        return true;
    }

    public Point getDir(int dir) {
        if (dir == 0) {
            return new Point(0, -1);
        }
        if (dir == 1) {
            return new Point(1, 0);
        }
        if (dir == 2) {
            return new Point(0, 1);
        }
        if (dir == 3) {
            return new Point(-1, 0);
        }
        return new Point(0, 0);
    }

    @Override
    public Point getSaveToRegionPos() {
        return new Point(this.level.regionManager.getRegionCoordByTile(this.tileX), this.level.regionManager.getRegionCoordByTile(this.tileY));
    }
}

