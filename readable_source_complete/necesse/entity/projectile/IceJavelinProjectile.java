/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.journal.ImpaleIceJavelinsJournalChallenge;
import necesse.engine.journal.JournalChallenge;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.JournalChallengeRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ProjectileHitStuckParticle;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.RicochetableProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

public class IceJavelinProjectile
extends Projectile
implements RicochetableProjectile {
    @Override
    public void init() {
        super.init();
        this.height = 18.0f;
        this.setWidth(8.0f);
        this.trailOffset = -50.0f;
        this.heightBasedOnDistance = true;
    }

    @Override
    public Trail getTrail() {
        return new Trail(this, this.getLevel(), new Color(150, 150, 150), 10.0f, 250, 18.0f);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (this.removed()) {
            return;
        }
        GameLight light = level.getLightLevel(this);
        int drawX = camera.getDrawX(this.x) - 2;
        int drawY = camera.getDrawY(this.y) - 2;
        final TextureDrawOptionsEnd options = this.texture.initDraw().light(light).rotate(this.getAngle() + 45.0f, 2, 2).pos(drawX, drawY - (int)this.getHeight());
        list.add(new EntityDrawable(this){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
        this.addShadowDrawables(tileList, drawX, drawY, light, this.getAngle() + 45.0f, 2, 2);
    }

    @Override
    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (this.isServer() && mob != null) {
            Mob owner = this.getOwner();
            if (owner.isPlayer && ((PlayerMob)owner).isServerClient()) {
                ServerClient serverClient = ((PlayerMob)owner).getServerClient();
                JournalChallenge challenge = JournalChallengeRegistry.getChallenge(JournalChallengeRegistry.IMPALE_FIVE_ICE_JAVELINS_ID);
                ((ImpaleIceJavelinsJournalChallenge)challenge).submitIceJavelinImpale(serverClient, mob);
            }
        }
        if (this.isClient() && this.traveledDistance < (float)this.distance) {
            final float height = this.getHeight();
            this.getLevel().entityManager.addParticle(new ProjectileHitStuckParticle(mob, this, x, y, mob == null ? 10.0f : 40.0f, 5000L){

                @Override
                public void addDrawables(Mob target, float x, float y, float angle, List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
                    int fadeTime;
                    GameLight light = level.getLightLevel(this);
                    int drawX = camera.getDrawX(x) - 2;
                    int drawY = camera.getDrawY(y - height) - 2;
                    float alpha = 1.0f;
                    long lifeCycleTime = this.getLifeCycleTime();
                    if (lifeCycleTime >= this.lifeTime - (long)(fadeTime = 1000)) {
                        alpha = Math.abs((float)(lifeCycleTime - (this.lifeTime - (long)fadeTime)) / (float)fadeTime - 1.0f);
                    }
                    int cut = target == null ? 8 : 0;
                    final TextureDrawOptionsEnd options = IceJavelinProjectile.this.texture.initDraw().section(cut, IceJavelinProjectile.this.texture.getWidth(), cut, IceJavelinProjectile.this.texture.getHeight()).light(light).rotate(IceJavelinProjectile.this.getAngle() + 45.0f, 2, 2).alpha(alpha).pos(drawX, drawY);
                    EntityDrawable drawable = new EntityDrawable(this){

                        @Override
                        public void draw(TickManager tickManager) {
                            options.draw();
                        }
                    };
                    if (target != null) {
                        topList.add(drawable);
                    } else {
                        list.add(drawable);
                    }
                }
            }, Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    protected SoundSettings getHitSound() {
        return new SoundSettings(GameResources.jinglehit).basePitch(0.3f).pitchVariance(0.0f).fallOffDistance(1000);
    }

    @Override
    protected SoundSettings getSpawnSound() {
        return new SoundSettings(GameResources.iceJavelin).volume(0.4f);
    }
}

