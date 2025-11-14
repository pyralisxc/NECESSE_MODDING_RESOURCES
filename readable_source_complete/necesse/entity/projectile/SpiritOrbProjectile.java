/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffManager;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.mobs.itemAttacker.FollowPosition;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.DryadSpiritFollowingMob;
import necesse.entity.projectile.followingProjectile.FollowingProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.item.toolItem.summonToolItem.SummonToolItem;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;

public class SpiritOrbProjectile
extends FollowingProjectile {
    private int dryadHauntedStacksOnHit;

    public SpiritOrbProjectile() {
    }

    public SpiritOrbProjectile(float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int dryadHauntedStacksOnHit, int knockback, Mob owner) {
        this();
        this.setLevel(owner.getLevel());
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.speed = speed;
        this.setDistance(distance);
        this.setDamage(damage);
        this.dryadHauntedStacksOnHit = dryadHauntedStacksOnHit;
        this.knockback = knockback;
        this.setOwner(owner);
        this.givesLight = true;
    }

    @Override
    public void init() {
        super.init();
        this.setWidth(10.0f);
        this.turnSpeed = 0.3f;
        this.height = 18.0f;
        this.trailOffset = 0.0f;
    }

    @Override
    public void updateTarget() {
        if (this.traveledDistance > 50.0f) {
            this.findTarget(m -> m.isHostile, 0.0f, 250.0f);
        }
    }

    @Override
    public void onHit(Mob mob, LevelObjectHit object, float x, float y, boolean fromPacket, ServerClient packetSubmitter) {
        super.onHit(mob, object, x, y, fromPacket, packetSubmitter);
    }

    @Override
    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (mob != null && this.isServer()) {
            BuffManager targetBM = mob.buffManager;
            Buff dryadHaunted = BuffRegistry.Debuffs.DRYAD_HAUNTED;
            ActiveBuff ab = new ActiveBuff(dryadHaunted, mob, 10000, (Attacker)this.getOwner());
            ab.setStacks(this.dryadHauntedStacksOnHit, 10000, this.getOwner());
            targetBM.addBuff(ab, true);
            if (targetBM.getStacks(dryadHaunted) >= 10) {
                targetBM.removeBuff(dryadHaunted, true);
                SpiritOrbProjectile.spawnDryadSpirit(this.getOwner());
            }
        }
    }

    public static void spawnDryadSpirit(Mob owner) {
        if (owner != null && owner.isServer()) {
            int maxSummons = 5;
            DryadSpiritFollowingMob summonedMob = (DryadSpiritFollowingMob)MobRegistry.getMob("dryadspirit", owner.getLevel());
            ((ItemAttackerMob)owner).serverFollowersManager.addFollower("summonedmobtemp", (Mob)summonedMob, FollowPosition.FLYING_CIRCLE_FAST, "summonedmob", 1.0f, p -> maxSummons, null, false);
            Point2D.Float spawnPoint = SummonToolItem.findSpawnLocation(summonedMob, owner.getLevel(), owner.x, owner.y);
            owner.getLevel().entityManager.addMob(summonedMob, spawnPoint.x, spawnPoint.y);
        }
    }

    @Override
    public Trail getTrail() {
        return new Trail(this, this.getLevel(), new Color(30, 177, 143), 12.0f, 200, this.getHeight());
    }

    @Override
    public Color getParticleColor() {
        return new Color(30, 177, 143);
    }

    @Override
    protected Color getWallHitColor() {
        return new Color(30, 177, 143);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
    }

    @Override
    protected SoundSettings getSpawnSound() {
        return new SoundSettings(GameResources.magicbolt3).volume(0.2f).basePitch(2.0f);
    }
}

