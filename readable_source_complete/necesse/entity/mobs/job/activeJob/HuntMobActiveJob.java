/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.job.activeJob;

import java.awt.geom.Point2D;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.ProjectileRegistry;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.GameLinkedListJobSequence;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.entity.mobs.job.activeJob.ActiveJobResult;
import necesse.entity.mobs.job.activeJob.MobActiveJob;
import necesse.entity.mobs.job.activeJob.PickupItemEntityActiveJob;
import necesse.entity.projectile.Projectile;
import necesse.level.maps.levelData.jobs.EntityLevelJob;

public class HuntMobActiveJob
extends MobActiveJob<Mob> {
    public EntityLevelJob<? extends Mob> job;
    public GameLinkedListJobSequence sequence;
    protected String meleeItemStringID;
    protected GameDamage meleeDamage;
    protected int meleeRange;
    protected int meleeAimSpeed;
    protected int meleeCooldown;
    protected String rangedItemStringID;
    protected GameDamage rangedDamage;
    protected int rangedRange;
    protected int rangedAimSpeed;
    protected int rangedCooldown;
    protected String rangedProjectileStringID;
    protected int rangedProjectileSpeed;
    public long lastAttackTime;
    public int lastAttackCooldown;

    public HuntMobActiveJob(EntityJobWorker worker, JobTypeHandler.TypePriority priority, EntityLevelJob<? extends Mob> job, GameLinkedListJobSequence sequence, String attackItemStringID, GameDamage damage, int attackRange, int attackAnimTime, int attackCooldown) {
        super(worker, priority, (Mob)job.target);
        this.job = job;
        this.sequence = sequence;
        this.meleeItemStringID = attackItemStringID;
        this.meleeDamage = damage;
        this.meleeRange = attackRange;
        this.meleeAimSpeed = attackAnimTime;
        this.meleeCooldown = attackCooldown;
    }

    public HuntMobActiveJob addRangedAttack(String itemStringID, GameDamage damage, int attackRange, int attackAnimTime, int attackCooldown, String projectileStringID, int projectileSpeed) {
        this.rangedItemStringID = itemStringID;
        this.rangedDamage = damage;
        this.rangedRange = attackRange;
        this.rangedAimSpeed = attackAnimTime;
        this.rangedCooldown = attackCooldown;
        this.rangedProjectileStringID = projectileStringID;
        this.rangedProjectileSpeed = projectileSpeed;
        return this;
    }

    protected Point2D.Float getProjectileTargetPos() {
        Mob mob = this.worker.getMobWorker();
        return Projectile.getPredictedTargetPos((Mob)this.target, mob.x, mob.y, this.rangedProjectileSpeed, -10.0f);
    }

    @Override
    public boolean isAtTarget() {
        double distanceToTarget = this.getDistanceToTarget();
        if (distanceToTarget <= (double)this.meleeRange) {
            return this.hasLOS();
        }
        if (this.rangedProjectileStringID != null && distanceToTarget <= (double)this.rangedRange && this.hasLOS()) {
            Point2D.Float targetPos = this.getProjectileTargetPos();
            return this.hasProjectileLOS(targetPos.x, targetPos.y);
        }
        return false;
    }

    @Override
    public int getCompleteRange() {
        if (this.rangedProjectileStringID != null) {
            return Math.max(this.meleeRange, this.rangedRange);
        }
        return this.meleeRange;
    }

    @Override
    public boolean isJobValid(boolean isCurrent) {
        return this.job.reservable.isAvailable(this.worker.getMobWorker()) && ((Mob)this.target).canTakeDamage();
    }

    @Override
    public boolean isInvalidIfTargetRemoved(boolean isCurrent) {
        return this.lastAttackTime == 0L;
    }

    @Override
    public void tick(boolean isCurrent, boolean isMovingTo) {
        this.job.reservable.reserve(this.worker.getMobWorker());
    }

    @Override
    public ActiveJobResult perform() {
        if (this.worker.isInWorkAnimation()) {
            return ActiveJobResult.PERFORMING;
        }
        if (((Mob)this.target).removed() || ((Mob)this.target).getHealth() <= 0) {
            if (this.sequence != null) {
                PickupItemEntityActiveJob.addItemPickupJobs(this.worker, this.priority, ((Mob)this.target).itemsDropped, this.sequence);
            }
            return ActiveJobResult.FINISHED;
        }
        Mob mob = this.worker.getMobWorker();
        if (this.lastAttackTime + (long)this.lastAttackCooldown > mob.getWorldEntity().getTime()) {
            return ActiveJobResult.PERFORMING;
        }
        double distanceToTarget = this.getDistanceToTarget();
        if (distanceToTarget <= (double)(this.meleeRange + 10)) {
            if (this.hasLOS()) {
                this.lastAttackCooldown = this.meleeCooldown;
                if (this.meleeItemStringID != null) {
                    this.worker.showAttackAnimation(((Mob)this.target).getX(), ((Mob)this.target).getY(), ItemRegistry.getItem(this.meleeItemStringID), this.meleeAimSpeed);
                } else {
                    this.worker.showPlaceAnimation(((Mob)this.target).getX(), ((Mob)this.target).getY(), null, this.meleeAimSpeed);
                }
                ((Mob)this.target).isServerHit(this.meleeDamage, ((Mob)this.target).x - mob.x, ((Mob)this.target).y - mob.y, 100.0f, mob);
                this.lastAttackTime = mob.getWorldEntity().getTime();
                return ActiveJobResult.PERFORMING;
            }
            return ActiveJobResult.MOVE_TO;
        }
        if (this.rangedProjectileStringID != null && distanceToTarget <= (double)(this.rangedRange + 50)) {
            if (this.hasLOS()) {
                Point2D.Float targetPos = this.getProjectileTargetPos();
                if (this.hasProjectileLOS(targetPos.x, targetPos.y)) {
                    this.lastAttackCooldown = this.rangedCooldown;
                    if (this.rangedItemStringID != null) {
                        this.worker.showAttackAnimation((int)targetPos.x, (int)targetPos.y, ItemRegistry.getItem(this.rangedItemStringID), this.rangedAimSpeed);
                    } else {
                        this.worker.showPlaceAnimation((int)targetPos.x, (int)targetPos.y, null, this.rangedAimSpeed);
                    }
                    Projectile projectile = ProjectileRegistry.getProjectile(this.rangedProjectileStringID, ((Mob)this.target).getLevel(), mob.x, mob.y, targetPos.x, targetPos.y, (float)this.rangedProjectileSpeed, this.rangedRange + 150, this.rangedDamage, mob);
                    projectile.moveDist(10.0);
                    ((Mob)this.target).getLevel().entityManager.projectiles.add(projectile);
                    this.lastAttackTime = mob.getWorldEntity().getTime();
                    return ActiveJobResult.PERFORMING;
                }
                return ActiveJobResult.MOVE_TO;
            }
            return ActiveJobResult.MOVE_TO;
        }
        return ActiveJobResult.MOVE_TO;
    }

    @Override
    public ActiveJobResult performTarget() {
        return ActiveJobResult.FAILED;
    }
}

