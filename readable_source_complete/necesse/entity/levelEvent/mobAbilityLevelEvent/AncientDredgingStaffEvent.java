/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Polygon;
import java.awt.geom.Point2D;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GroundPillar;
import necesse.engine.util.GroundPillarList;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.WeaponShockWaveLevelEvent;
import necesse.entity.manager.GroundPillarHandler;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class AncientDredgingStaffEvent
extends WeaponShockWaveLevelEvent {
    protected final GroundPillarList<AncientDredgePillar> pillars = new GroundPillarList();

    public AncientDredgingStaffEvent() {
        super(30.0f, 20.0f, 5.0f);
    }

    public AncientDredgingStaffEvent(Mob owner, int x, int y, GameRandom uniqueIDRandom, float targetAngle, GameDamage damage, float resilienceGain, float velocity, float knockback, float range) {
        super(owner, x, y, uniqueIDRandom, targetAngle, 30.0f, 20.0f, 5.0f, damage, resilienceGain, velocity, knockback, range);
    }

    @Override
    public void init() {
        super.init();
        if (this.isClient()) {
            this.level.entityManager.addPillarHandler(new GroundPillarHandler<AncientDredgePillar>(this.pillars){

                @Override
                protected boolean canRemove() {
                    return AncientDredgingStaffEvent.this.isOver();
                }

                @Override
                public double getCurrentDistanceMoved() {
                    return 0.0;
                }
            });
            SoundManager.playSound(GameResources.shake, (SoundEffect)SoundEffect.effect(this.owner));
            SoundManager.playSound(GameResources.stomp, (SoundEffect)SoundEffect.effect(this.owner).volume(0.5f));
        }
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
            GroundPillarList<AncientDredgePillar> groundPillarList = this.pillars;
            synchronized (groundPillarList) {
                for (Point2D.Float pos : this.getPositionsAlongHit(radius, startAngle, endAngle, 20.0f, false)) {
                    this.pillars.add(new AncientDredgePillar((int)(pos.x + GameRandom.globalRandom.getFloatBetween(-10.0f, 10.0f)), (int)(pos.y + GameRandom.globalRandom.getFloatBetween(-10.0f, 10.0f)), radius, this.level.getWorldEntity().getLocalTime()));
                }
            }
        }
    }

    public static class AncientDredgePillar
    extends GroundPillar {
        public GameTextureSection texture = null;
        public boolean mirror = GameRandom.globalRandom.nextBoolean();

        public AncientDredgePillar(int x, int y, double spawnDistance, long spawnTime) {
            super(x, y, spawnDistance, spawnTime);
            GameTexture pillarSprites = GameResources.ancientDredgingStaffPillars;
            if (pillarSprites != null) {
                int res = pillarSprites.getHeight();
                int sprite = GameRandom.globalRandom.nextInt(pillarSprites.getWidth() / res);
                this.texture = new GameTextureSection(GameResources.ancientDredgingStaffPillars).sprite(sprite, 0, res);
            }
            this.behaviour = new GroundPillar.TimedBehaviour(200, 100, 200);
        }

        @Override
        public DrawOptions getDrawOptions(Level level, long currentTime, double distanceMoved, GameCamera camera) {
            GameLight light = level.getLightLevel(LevelEvent.getTileCoordinate(this.x), LevelEvent.getTileCoordinate(this.y));
            int drawX = camera.getDrawX(this.x);
            int drawY = camera.getDrawY(this.y);
            double height = this.getHeight(currentTime, distanceMoved);
            int endY = (int)(height * (double)this.texture.getHeight());
            return this.texture.section(0, this.texture.getWidth(), 0, endY).initDraw().mirror(this.mirror, false).light(light).pos(drawX - this.texture.getWidth() / 2, drawY - endY);
        }
    }
}

