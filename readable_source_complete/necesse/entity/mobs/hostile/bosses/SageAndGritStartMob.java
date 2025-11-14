/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses;

import java.awt.Point;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.LevelMob;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.hostile.bosses.FlyingSpiritsHead;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;

public class SageAndGritStartMob
extends Mob {
    public LevelMob<FlyingSpiritsHead> sage;
    public LevelMob<FlyingSpiritsHead> grit;
    public boolean sageDead;
    public boolean gritDead;
    public Point pedestalPosition;

    public SageAndGritStartMob() {
        super(100000);
        this.shouldSave = false;
    }

    @Override
    public void init() {
        super.init();
        Level level = this.getLevel();
        if (this.isServer()) {
            float gritAngle = GameRandom.globalRandom.nextInt(360);
            float gritX = (float)Math.cos(Math.toRadians(gritAngle));
            float gritY = (float)Math.sin(Math.toRadians(gritAngle));
            float sageAngle = GameMath.fixAngle(gritAngle + 180.0f);
            float sageX = (float)Math.cos(Math.toRadians(sageAngle));
            float sageY = (float)Math.sin(Math.toRadians(sageAngle));
            float distance = 960.0f;
            FlyingSpiritsHead grit = (FlyingSpiritsHead)MobRegistry.getMob("grit", level);
            FlyingSpiritsHead sage = (FlyingSpiritsHead)MobRegistry.getMob("sage", level);
            grit.setLevel(level);
            sage.setLevel(level);
            grit.friend.uniqueID = sage.getUniqueID();
            sage.friend.uniqueID = grit.getUniqueID();
            grit.pedestalPosition = this.pedestalPosition;
            sage.pedestalPosition = this.pedestalPosition;
            if (this.isSecondaryIncursionBoss) {
                grit.isSecondaryIncursionBoss = true;
                sage.isSecondaryIncursionBoss = true;
            }
            level.entityManager.addMob(grit, this.getX() + (int)(gritX * distance), this.getY() + (int)(gritY * distance));
            level.entityManager.addMob(sage, this.getX() + (int)(sageX * distance), this.getY() + (int)(sageY * distance));
            this.grit = new LevelMob<FlyingSpiritsHead>(grit);
            this.sage = new LevelMob<FlyingSpiritsHead>(sage);
        }
    }

    @Override
    public void serverTick() {
        FlyingSpiritsHead mob;
        super.serverTick();
        boolean remove = true;
        if (this.sage != null) {
            mob = this.sage.get(this.getLevel());
            if (mob != null && !mob.removed()) {
                remove = false;
            } else if (mob != null && mob.hasDied()) {
                this.sageDead = true;
            }
        }
        if (this.grit != null) {
            mob = this.grit.get(this.getLevel());
            if (mob != null && !mob.removed()) {
                remove = false;
            } else if (mob != null && mob.hasDied()) {
                this.gritDead = true;
            }
        }
        if (remove) {
            this.remove(0.0f, 0.0f, null, this.sageDead && this.gritDead);
        }
    }

    @Override
    public boolean canBePushed(Mob other) {
        return false;
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
    }

    @Override
    public boolean isHealthBarVisible() {
        return false;
    }

    @Override
    public boolean canTakeDamage() {
        return false;
    }

    @Override
    public boolean countDamageDealt() {
        return false;
    }

    @Override
    public Point getLootDropsPosition(ServerClient privateClient) {
        Mob sageMob = null;
        Mob gritMob = null;
        if (this.sage != null) {
            sageMob = this.getLevel().entityManager.mobs.get(this.sage.uniqueID, true);
        }
        if (this.grit != null) {
            gritMob = this.getLevel().entityManager.mobs.get(this.grit.uniqueID, true);
        }
        if (sageMob != null && gritMob != null) {
            if (sageMob.removedTime != 0L && sageMob.removedTime < gritMob.removedTime) {
                return sageMob.getLootDropsPosition(privateClient);
            }
            if (gritMob.removedTime != 0L && gritMob.removedTime < sageMob.removedTime) {
                return gritMob.getLootDropsPosition(privateClient);
            }
            return sageMob.getLootDropsPosition(privateClient);
        }
        if (sageMob != null) {
            return sageMob.getLootDropsPosition(privateClient);
        }
        if (gritMob != null) {
            return gritMob.getLootDropsPosition(privateClient);
        }
        return super.getLootDropsPosition(privateClient);
    }
}

