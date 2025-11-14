/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Color;
import java.awt.geom.Point2D;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.Ray;
import necesse.engine.util.RayLinkedList;
import necesse.entity.ParticleBeamHandler;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobAbilityLevelEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobHitCooldowns;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.LevelObjectHit;

public class FireDanceLevelEvent
extends MobAbilityLevelEvent {
    protected float targetX;
    protected float targetY;
    protected float currentDistance;
    protected float expandSpeed = 50.0f;
    protected GameDamage damage;
    protected int knockback;
    protected Color color;
    protected int ticker;
    protected int aliveTime;
    protected MobHitCooldowns hitCooldowns = new MobHitCooldowns();
    private ParticleBeamHandler beamHandler;

    public FireDanceLevelEvent() {
    }

    public FireDanceLevelEvent(Mob owner, GameRandom uniqueIDRandom, float targetX, float targetY, GameDamage damage, int knockback, Color color, int aliveTime) {
        super(owner, uniqueIDRandom);
        this.targetX = targetX;
        this.targetY = targetY;
        this.damage = damage;
        this.knockback = knockback;
        this.color = color;
        this.aliveTime = aliveTime;
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextFloat(this.targetX);
        writer.putNextFloat(this.targetY);
        this.damage.writePacket(writer);
        writer.putNextInt(this.knockback);
        writer.putNextInt(this.color.getRGB());
        writer.putNextFloat(this.currentDistance);
        writer.putNextShortUnsigned(this.ticker);
        writer.putNextInt(this.aliveTime);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.targetX = reader.getNextFloat();
        this.targetY = reader.getNextFloat();
        this.damage = GameDamage.fromReader(reader);
        this.knockback = reader.getNextInt();
        this.color = new Color(reader.getNextInt());
        this.currentDistance = reader.getNextFloat();
        this.ticker = reader.getNextShortUnsigned();
        this.aliveTime = reader.getNextInt();
    }

    @Override
    public boolean isNetworkImportant() {
        return true;
    }

    @Override
    public void tickMovement(float delta) {
        super.tickMovement(delta);
        if (this.isOver()) {
            return;
        }
        this.currentDistance += this.expandSpeed * delta / 250.0f;
        Point2D.Float dir = GameMath.normalize(this.targetX - this.owner.x, this.targetY - this.owner.y);
        float rayDist = Math.min(this.owner.getDistance(this.targetX, this.targetY), this.currentDistance);
        RayLinkedList<LevelObjectHit> rays = GameUtils.castRay(this.level, (double)this.owner.x, (double)this.owner.y, (double)dir.x, (double)dir.y, (double)rayDist, 0, null);
        if (this.level.tickManager().isGameTick()) {
            for (Ray ray : rays) {
                this.handleHits(ray, this::canHit, null);
            }
        }
        if (this.isClient()) {
            this.updateTrail(rays, this.level.tickManager().getDelta(), rayDist);
        }
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (this.owner == null || this.owner.removed()) {
            this.over();
        }
        ++this.ticker;
        if (this.ticker * 50 >= this.aliveTime) {
            this.over();
        }
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (this.owner == null || this.owner.removed()) {
            this.over();
        }
        ++this.ticker;
        if (this.ticker * 50 >= this.aliveTime) {
            this.over();
        }
    }

    public boolean canHit(Mob mob) {
        if (!mob.canBeHit(this)) {
            return false;
        }
        return this.hitCooldowns.canHit(mob);
    }

    @Override
    public void clientHit(Mob target, Packet content) {
        super.clientHit(target, content);
        this.hitCooldowns.startCooldown(target);
        target.startHitCooldown();
    }

    @Override
    public void serverHit(Mob target, Packet content, boolean clientSubmitted) {
        this.hitCooldowns.startCooldown(target);
        target.isServerHit(this.damage, this.owner.dx, this.owner.dy, this.knockback, this.owner);
    }

    private void updateTrail(RayLinkedList<LevelObjectHit> rays, float delta, float dist) {
        if (this.beamHandler == null) {
            this.beamHandler = new ParticleBeamHandler(this.level).color(this.color).thickness(80, 40).speed(100.0f).sprite(new GameSprite(GameResources.chains, 7, 0, 32));
        }
        this.beamHandler.update(rays, delta);
    }

    @Override
    public void over() {
        if (this.beamHandler != null) {
            this.beamHandler.dispose();
        }
        super.over();
    }
}

