/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Color;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.sound.LinesSoundEmitter;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.Ray;
import necesse.engine.util.RayLinkedList;
import necesse.entity.ParticleBeamHandler;
import necesse.entity.levelEvent.actions.LevelEventAction;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobAbilityLevelEvent;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.LevelObjectHit;

public class MouseBeamLevelEvent
extends MobAbilityLevelEvent
implements LinesSoundEmitter {
    public int seed;
    public float currentAngle;
    public int targetAngle;
    protected float speed;
    public float height = 14.0f;
    protected int bounces;
    protected float resilienceGain;
    protected float distance;
    protected GameDamage damage;
    protected int knockback;
    protected Color color;
    protected boolean mobHitsIsPlayer;
    protected HashMap<Integer, Long> mobHits = new HashMap();
    public int hitCooldown;
    public float appendAttackSpeedModifier;
    public long lastResilienceGainTime;
    protected RayLinkedList<LevelObjectHit> lastRays;
    protected ParticleBeamHandler beamHandler;
    public final SetTargetAngleAction setTargetAngleAction;

    public MouseBeamLevelEvent() {
        this.setTargetAngleAction = this.registerAction(new SetTargetAngleAction());
    }

    public MouseBeamLevelEvent(Mob owner, int startTargetX, int startTargetY, int seed, float speed, float distance, GameDamage damage, int knockback, HashMap<Integer, Long> mobHits, int hitCooldown, float appendAttackSpeedModifier, int bounces, float resilienceGain, Color color) {
        super(owner, new GameRandom(seed));
        this.seed = seed;
        this.speed = speed;
        this.distance = distance;
        this.damage = damage;
        this.knockback = knockback;
        this.hitCooldown = hitCooldown;
        this.appendAttackSpeedModifier = appendAttackSpeedModifier;
        this.bounces = Math.min(bounces, 100);
        this.resilienceGain = resilienceGain;
        this.color = color;
        if (mobHits == null) {
            if (owner.isPlayer) {
                this.mobHitsIsPlayer = true;
                this.mobHits = ((PlayerMob)owner).toolHits;
            } else {
                this.mobHits = new HashMap();
            }
        } else {
            this.mobHits = mobHits;
        }
        this.targetAngle = (int)GameMath.fixAngle(GameMath.getAngle(new Point2D.Float((float)startTargetX - owner.x, (float)startTargetY - owner.y)));
        this.currentAngle = this.targetAngle;
        this.setTargetAngleAction = this.registerAction(new SetTargetAngleAction());
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.seed = reader.getNextShortUnsigned();
        this.targetAngle = reader.getNextShortUnsigned();
        this.currentAngle = reader.getNextFloat();
        this.speed = reader.getNextFloat();
        this.distance = reader.getNextFloat();
        this.bounces = reader.getNextByteUnsigned();
        this.color = new Color(reader.getNextInt());
        this.knockback = reader.getNextInt();
        this.hitCooldown = reader.getNextInt();
        this.appendAttackSpeedModifier = reader.getNextFloat();
        this.damage = GameDamage.fromReader(reader);
        this.mobHitsIsPlayer = reader.getNextBoolean();
        if (!this.mobHitsIsPlayer) {
            int size = reader.getNextShortUnsigned();
            this.mobHits = new HashMap(size);
            for (int i = 0; i < size; ++i) {
                int uniqueID = reader.getNextInt();
                long time = reader.getNextLong();
                this.mobHits.put(uniqueID, time);
            }
        }
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextShortUnsigned(this.seed);
        writer.putNextShortUnsigned(this.targetAngle);
        writer.putNextFloat(this.currentAngle);
        writer.putNextFloat(this.speed);
        writer.putNextFloat(this.distance);
        writer.putNextByteUnsigned(this.bounces);
        writer.putNextInt(this.color.getRGB());
        writer.putNextInt(this.knockback);
        writer.putNextInt(this.hitCooldown);
        writer.putNextFloat(this.appendAttackSpeedModifier);
        this.damage.writePacket(writer);
        writer.putNextBoolean(this.mobHitsIsPlayer);
        if (!this.mobHitsIsPlayer) {
            writer.putNextShortUnsigned(this.mobHits.size());
            for (Map.Entry<Integer, Long> entry : this.mobHits.entrySet()) {
                writer.putNextInt(entry.getKey());
                writer.putNextLong(entry.getValue());
            }
        }
    }

    @Override
    public boolean isNetworkImportant() {
        return true;
    }

    @Override
    public void init() {
        super.init();
        if (!(this.owner instanceof AttackAnimMob)) {
            this.over();
        }
        if (this.mobHitsIsPlayer && this.owner instanceof PlayerMob) {
            this.mobHits = ((PlayerMob)this.owner).toolHits;
        }
    }

    @Override
    public void tickMovement(float delta) {
        super.tickMovement(delta);
        if (!this.owner.isAttacking || ((AttackAnimMob)this.owner).attackSeed != this.seed) {
            this.over();
        }
        if (this.isOver()) {
            return;
        }
        RayLinkedList<LevelObjectHit> rays = null;
        if (this.currentAngle != (float)this.targetAngle) {
            float maxDistPerCheck = 20.0f;
            float maxAnglePerCheck = (float)((double)maxDistPerCheck / (Math.PI * 2 * (double)this.distance / 360.0));
            float speed = this.speed / this.getAttackSpeedModifier();
            float change = speed * delta / 250.0f;
            while (change > 0.0f) {
                float currentChange = Math.min(maxAnglePerCheck, change);
                change -= currentChange;
                float angleDelta = GameMath.getAngleDifference(this.currentAngle, this.targetAngle);
                if (Math.abs(angleDelta) < currentChange) {
                    this.currentAngle = this.targetAngle;
                } else if (angleDelta < 0.0f) {
                    this.currentAngle = GameMath.fixAngle(this.currentAngle + currentChange);
                } else if (angleDelta > 0.0f) {
                    this.currentAngle = GameMath.fixAngle(this.currentAngle - currentChange);
                }
                rays = this.checkRayHitbox();
            }
        }
        if (rays == null) {
            rays = this.checkRayHitbox();
        }
        this.lastRays = rays;
        if (this.isClient()) {
            this.updateTrail(rays, this.level.tickManager().getDelta());
        }
    }

    private RayLinkedList<LevelObjectHit> checkRayHitbox() {
        Point2D.Float dir = GameMath.getAngleDir(this.currentAngle);
        RayLinkedList<LevelObjectHit> rays = GameUtils.castRay(this.level, (double)this.owner.x, (double)this.owner.y, (double)dir.x, (double)dir.y, (double)this.distance, this.bounces, new CollisionFilter().projectileCollision().addFilter(tp -> !tp.object().object.attackThrough));
        for (Ray ray : rays) {
            this.handleHits(ray, this::canHit, null);
        }
        return rays;
    }

    @Override
    public int getHitCooldown(LevelObjectHit hit) {
        return this.getHitCooldown();
    }

    @Override
    public void hit(LevelObjectHit hit) {
        super.hit(hit);
        hit.getLevelObject().attackThrough(this.damage, this.owner);
    }

    public boolean canHit(Mob mob) {
        if (!mob.canBeHit(this)) {
            return false;
        }
        if (!this.mobHits.containsKey(mob.getHitCooldownUniqueID())) {
            return true;
        }
        return this.mobHits.get(mob.getHitCooldownUniqueID()) + (long)this.getHitCooldown() < this.owner.getTime();
    }

    public float getAttackSpeedModifier() {
        return 1.0f / this.damage.type.calculateTotalAttackSpeedModifier(this.owner, this.appendAttackSpeedModifier);
    }

    public int getHitCooldown() {
        return Math.round((float)this.hitCooldown * this.getAttackSpeedModifier());
    }

    @Override
    public void clientHit(Mob target, Packet content) {
        super.clientHit(target, content);
        this.mobHits.put(target.getHitCooldownUniqueID(), this.owner.getTime());
        target.startHitCooldown();
    }

    @Override
    public void serverHit(Mob target, Packet content, boolean clientSubmitted) {
        this.mobHits.put(target.getHitCooldownUniqueID(), this.owner.getTime());
        target.isServerHit(this.damage, target.x - this.owner.x, target.y - this.owner.y, this.knockback, this.owner);
        if (target.canGiveResilience(this.owner) && this.resilienceGain != 0.0f && target.getTime() >= this.lastResilienceGainTime + (long)this.hitCooldown) {
            this.owner.addResilience(this.resilienceGain);
            this.lastResilienceGainTime = target.getTime();
        }
    }

    protected ParticleBeamHandler constructBeam() {
        return new ParticleBeamHandler(this.level).color(this.color).thickness(40, 5).speed(this.distance / 6.0f).height(this.height).sprite(new GameSprite(GameResources.chains, 7, 0, 32));
    }

    protected void updateTrail(RayLinkedList<LevelObjectHit> rays, float delta) {
        if (this.beamHandler == null) {
            this.beamHandler = this.constructBeam();
        }
        this.beamHandler.update(rays, delta);
    }

    public float getDistance() {
        return this.distance;
    }

    @Override
    public void over() {
        if (this.beamHandler != null) {
            this.beamHandler.dispose();
        }
        super.over();
    }

    @Override
    public Iterable<Line2D> getSoundLines() {
        if (this.lastRays == null) {
            return Collections.emptyList();
        }
        return GameUtils.mapIterable(this.lastRays.iterator(), ray -> ray);
    }

    public class SetTargetAngleAction
    extends LevelEventAction {
        public void runAndSend(int targetAngle, boolean forcedClient) {
            Packet content = new Packet();
            PacketWriter writer = new PacketWriter(content);
            writer.putNextInt(targetAngle);
            writer.putNextBoolean(forcedClient);
            this.runAndSendAction(content);
        }

        @Override
        public void executePacket(PacketReader reader) {
            int targetAngle = reader.getNextInt();
            boolean forcedClient = reader.getNextBoolean();
            if (forcedClient || !MouseBeamLevelEvent.this.isClient() || MouseBeamLevelEvent.this.getClient().getPlayer() != MouseBeamLevelEvent.this.owner) {
                MouseBeamLevelEvent.this.targetAngle = targetAngle;
            }
        }
    }
}

