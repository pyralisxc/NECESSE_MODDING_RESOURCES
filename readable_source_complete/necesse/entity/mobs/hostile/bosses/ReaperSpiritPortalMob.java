/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses;

import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.EmptyMobAbility;
import necesse.entity.mobs.hostile.bosses.BossMob;
import necesse.entity.mobs.hostile.bosses.ReaperMob;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class ReaperSpiritPortalMob
extends BossMob {
    private long spawnTime;
    public ReaperMob owner;
    public final EmptyMobAbility magicSoundAbility;

    public ReaperSpiritPortalMob() {
        super(100);
        this.isSummoned = true;
        this.isStatic = true;
        this.setSpeed(100.0f);
        this.setArmor(20);
        this.setFriction(1.0f);
        this.setKnockbackModifier(0.0f);
        this.collision = new Rectangle(-18, -15, 36, 30);
        this.hitBox = new Rectangle(-18, -15, 36, 36);
        this.selectBox = new Rectangle(-20, -18, 40, 36);
        this.magicSoundAbility = this.registerAbility(new EmptyMobAbility(){

            @Override
            protected void run() {
                if (ReaperSpiritPortalMob.this.isClient()) {
                    SoundManager.playSound(GameResources.magicbolt1, (SoundEffect)SoundEffect.effect(ReaperSpiritPortalMob.this));
                }
            }
        });
    }

    @Override
    public void init() {
        super.init();
        this.spawnTime = this.getWorldEntity().getTime();
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(ReaperSpiritPortalMob.getTileCoordinate(x), ReaperSpiritPortalMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 16;
        int drawY = camera.getDrawY(y) - 16;
        float angle = (float)(this.getWorldEntity().getTime() - this.spawnTime) / 2.0f;
        TextureDrawOptionsEnd drawOptions = MobRegistry.Textures.reaperSpiritPortal.initDraw().sprite(0, 0, 32).light(light).rotate(-angle, 16, 16).pos(drawX, drawY);
        topList.add(tm -> drawOptions.draw());
    }
}

