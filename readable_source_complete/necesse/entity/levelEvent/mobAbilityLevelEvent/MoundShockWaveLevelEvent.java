/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Color;
import java.awt.Polygon;
import java.awt.geom.Point2D;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GroundPillar;
import necesse.engine.util.GroundPillarList;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ShockWaveLevelEvent;
import necesse.entity.manager.GroundPillarHandler;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

public class MoundShockWaveLevelEvent
extends ShockWaveLevelEvent {
    protected final GroundPillarList<Mound> pillars = new GroundPillarList();

    public MoundShockWaveLevelEvent() {
        super(360.0f, 150.0f, 150.0f, 20.0f, 5.0f);
    }

    public MoundShockWaveLevelEvent(Mob owner, int x, int y, GameRandom uniqueIDRandom, float targetAngle) {
        super(owner, x, y, uniqueIDRandom, targetAngle, 360.0f, 150.0f, 150.0f, 20.0f, 5.0f);
    }

    @Override
    public void init() {
        super.init();
        this.drawDebugHitboxes = true;
        if (this.isClient()) {
            this.level.entityManager.addPillarHandler(new GroundPillarHandler<Mound>(this.pillars){

                @Override
                protected boolean canRemove() {
                    return MoundShockWaveLevelEvent.this.isOver();
                }

                @Override
                public double getCurrentDistanceMoved() {
                    return 0.0;
                }
            });
        }
    }

    @Override
    public void damageTarget(Mob target) {
        target.isServerHit(new GameDamage(10.0f), 0.0f, 0.0f, 0.0f, this.owner);
    }

    @Override
    protected void spawnHitboxParticles(Polygon hitbox) {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void spawnHitboxParticles(float radius, float startAngle, float endAngle) {
        if (this.isClient()) {
            GroundPillarList<Mound> groundPillarList = this.pillars;
            synchronized (groundPillarList) {
                for (Point2D.Float pos : this.getPositionsAlongHit(radius, startAngle, endAngle, 20.0f, false)) {
                    this.pillars.add(new Mound((int)(pos.x + GameRandom.globalRandom.getFloatBetween(-5.0f, 5.0f)), (int)(pos.y + GameRandom.globalRandom.getFloatBetween(-5.0f, 5.0f)), radius, this.level.getWorldEntity().getLocalTime()));
                }
            }
        }
    }

    @Override
    public void hitObject(LevelObjectHit hit) {
    }

    private static class Mound
    extends GroundPillar {
        public GameTexture texture = GameRandom.globalRandom.getOneOf(MobRegistry.Textures.mound1, MobRegistry.Textures.mound2, MobRegistry.Textures.mound3);

        public Mound(int x, int y, double spawnDistance, long spawnTime) {
            super(x, y, spawnDistance, spawnTime);
            this.behaviour = new GroundPillar.TimedBehaviour(200, 100, 200);
        }

        @Override
        public DrawOptions getDrawOptions(Level level, long currentTime, double distanceMoved, GameCamera camera) {
            GameTile tile = level.getTile(LevelEvent.getTileCoordinate(this.x), LevelEvent.getTileCoordinate(this.y));
            if (tile.isLiquid) {
                return null;
            }
            GameLight light = level.getLightLevel(LevelEvent.getTileCoordinate(this.x), LevelEvent.getTileCoordinate(this.y));
            Color color = tile.getMapColor(level, LevelEvent.getTileCoordinate(this.x), LevelEvent.getTileCoordinate(this.y));
            int drawX = camera.getDrawX(this.x);
            int drawY = camera.getDrawY(this.y);
            double height = this.getHeight(currentTime, distanceMoved);
            int endY = (int)(height * (double)this.texture.getHeight());
            return this.texture.initDraw().section(0, this.texture.getWidth(), 0, endY).color(color).light(light).pos(drawX - this.texture.getWidth() / 2, drawY - endY);
        }
    }
}

