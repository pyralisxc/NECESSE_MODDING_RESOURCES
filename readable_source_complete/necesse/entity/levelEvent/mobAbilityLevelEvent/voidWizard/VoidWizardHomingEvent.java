/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.mobAbilityLevelEvent.voidWizard;

import java.util.List;
import necesse.engine.GameLog;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobAbilityLevelEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.hostile.bosses.VoidWizard;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.followingProjectile.VoidWizardHomingProjectile;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class VoidWizardHomingEvent
extends MobAbilityLevelEvent {
    private int tickCounter;
    private int targetID;
    private VoidWizard wizard;
    private Mob target;
    private GameDamage damage;
    private boolean startInstantly;
    private boolean addParticle;

    public VoidWizardHomingEvent() {
    }

    public VoidWizardHomingEvent(Mob owner, Mob target, boolean startInstantly, boolean addParticle) {
        super(owner, GameRandom.globalRandom);
        this.target = target;
        this.targetID = target != null ? target.getUniqueID() : -1;
        this.startInstantly = startInstantly;
        this.addParticle = addParticle;
    }

    @Override
    public void init() {
        super.init();
        if (this.owner == null) {
            return;
        }
        this.tickCounter = this.startInstantly ? 20 : 0;
        this.damage = VoidWizard.homingExplosion.modDamage(0.8f);
        this.target = GameUtils.getLevelMob(this.targetID, this.level);
        if (this.target == null) {
            GameLog.warn.println("Could not find target for dungeon wizard attack homing event, server level: " + this.isServer());
            this.over();
            return;
        }
        if (this.isClient() && this.addParticle) {
            this.level.entityManager.particles.add(new EventParticle(this.level, this.owner, this.target, 3000L));
        }
        if (this.owner instanceof VoidWizard) {
            this.wizard = (VoidWizard)this.owner;
            this.wizard.swingAttack = false;
            this.wizard.showAttack(this.target.getX(), this.target.getY(), false);
        }
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextInt(this.targetID);
        writer.putNextBoolean(this.startInstantly);
        writer.putNextBoolean(this.addParticle);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.targetID = reader.getNextInt();
        this.startInstantly = reader.getNextBoolean();
        this.addParticle = reader.getNextBoolean();
    }

    @Override
    public void clientTick() {
        ++this.tickCounter;
        if (this.owner == null || this.owner.removed() || this.tickCounter > 60) {
            this.over();
            return;
        }
        if (this.wizard != null) {
            this.wizard.showAttack(this.target.getX(), this.target.getY(), false);
        }
    }

    @Override
    public void serverTick() {
        ++this.tickCounter;
        if (this.owner == null || this.owner.removed() || !this.target.isSamePlace(this.owner) || this.tickCounter > 60) {
            this.over();
            return;
        }
        if (this.wizard != null) {
            this.wizard.showAttack(this.target.getX(), this.target.getY(), false);
        }
        if (this.tickCounter > 20 && this.tickCounter % 5 == 0) {
            VoidWizardHomingProjectile p = new VoidWizardHomingProjectile(this.level, this.owner, this.target, this.damage);
            this.level.entityManager.projectiles.add(p);
            if (this.wizard != null) {
                this.wizard.playBoltSoundAbility.runAndSend(1.0f, 1.1f);
            }
        }
    }

    @Override
    public void over() {
        super.over();
        if (this.wizard != null) {
            this.wizard.isAttacking = false;
        }
    }

    public class EventParticle
    extends Particle {
        private final Mob owner;
        private final Mob target;

        public EventParticle(Level level, Mob owner, Mob target, long lifeTime) {
            super(level, owner.x, owner.y, lifeTime);
            this.owner = owner;
            this.target = target;
        }

        @Override
        public void tickMovement(float delta) {
            this.x = this.owner.x;
            this.y = this.owner.y;
        }

        @Override
        public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
            if (this.removed()) {
                return;
            }
            GameLight light = level.getLightLevel(this.getTileX(), this.getTileY());
            int drawX = camera.getDrawX(this.x);
            int drawY = camera.getDrawY(this.y) - 16;
            float rotation = this.getRotation();
            TextureDrawOptionsEnd options = MobRegistry.Textures.voidWizard.body.initDraw().sprite(0, 5, 64).light(light.minLevelCopy(Math.min(light.getLevel() + 100.0f, 150.0f))).rotate(rotation, 0, 0).pos(drawX, drawY);
            topList.add(tm -> options.draw());
        }

        public float getRotation() {
            float dx = this.owner.x - this.target.x;
            float dy = this.owner.y - this.target.y;
            float out = (float)(dx == 0.0f ? (double)(dy < 0.0f ? 90 : -90) : Math.toDegrees(Math.atan(dy / dx)));
            if (dx > 0.0f) {
                out += 180.0f;
            }
            return out - 45.0f;
        }
    }
}

