/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.util.GameMath;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;

public class SpikeTrapEvent
extends LevelEvent
implements Attacker {
    public static GameDamage damage = new GameDamage(25.0f, 20.0f, 0.0f, 2.0f, 1.0f);
    private int tileX;
    private int tileY;
    private int ticks;
    private final int range;
    private final ArrayList<Integer> hits;

    public SpikeTrapEvent() {
        this.range = 1;
        this.hits = new ArrayList();
    }

    public SpikeTrapEvent(int tileX, int tileY) {
        this.tileX = tileX;
        this.tileY = tileY;
        this.range = 1;
        this.hits = new ArrayList();
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.tileX = reader.getNextInt();
        this.tileY = reader.getNextInt();
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextInt(this.tileX);
        writer.putNextInt(this.tileY);
    }

    @Override
    public void init() {
        super.init();
        this.ticks = 0;
    }

    @Override
    public void serverTick() {
        int currentRange = this.ticks % this.range;
        if (currentRange == 0) {
            this.hits.clear();
        }
        Rectangle hitBox = new Rectangle(this.tileX * 32 + 11, this.tileY * 32 + 11, 10, 10);
        for (Mob target : this.getTargets(hitBox)) {
            if (!target.canBeHit(this) || !hitBox.intersects(target.getHitBox())) continue;
            this.hit(target);
        }
        this.over();
    }

    private void hit(Mob target) {
        if (this.hits.contains(target.getUniqueID())) {
            return;
        }
        if (target.getLevel().isTrialRoom) {
            GameDamage trialDamage = new GameDamage(DamageTypeRegistry.TRUE, (float)target.getMaxHealth() / 4.0f);
            target.isServerHit(trialDamage, 0.0f, 0.0f, 0.0f, this);
        } else {
            target.isServerHit(damage, 0.0f, 0.0f, 0.0f, this);
            this.hits.add(target.getUniqueID());
        }
    }

    private ArrayList<Mob> getTargets(Rectangle hitbox) {
        ArrayList<Mob> out = this.level.entityManager.mobs.getInRegionRangeByTile(GameMath.getTileCoordinate(hitbox.getCenterX()), GameMath.getTileCoordinate(hitbox.getCenterY()), 1);
        this.level.getServer().streamClients().filter(sc -> sc != null && sc.playerMob != null).filter(sc -> sc.isSamePlace(this.getLevel())).forEach(sc -> out.add(sc.playerMob));
        return out;
    }

    @Override
    public GameMessage getAttackerName() {
        return new LocalMessage("deaths", "spiketrapname");
    }

    @Override
    public DeathMessageTable getDeathMessages() {
        return this.getDeathMessages("spiketrap", 2);
    }

    @Override
    public Mob getFirstAttackOwner() {
        return null;
    }

    @Override
    public Point getSaveToRegionPos() {
        return new Point(this.level.regionManager.getRegionCoordByTile(this.tileX), this.level.regionManager.getRegionCoordByTile(this.tileY));
    }
}

