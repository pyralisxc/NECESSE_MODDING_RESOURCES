/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.polymorph;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.gameSound.GameSound;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.PolymorphMob;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class RoosterPolymorphMob
extends PolymorphMob {
    public RoosterPolymorphMob() {
        this.setSpeed(25.0f);
        this.setFriction(3.0f);
        this.collision = new Rectangle(-12, -9, 24, 18);
        this.hitBox = new Rectangle(-16, -12, 32, 24);
        this.selectBox = new Rectangle(-18, -30, 36, 36);
        this.swimMaskMove = 8;
        this.swimMaskOffset = 0;
        this.swimSinkOffset = 0;
        this.spaceAbilityCooldown = 1100;
    }

    @Override
    protected GameSound getMobSound() {
        return GameResources.roosterAmbient;
    }

    @Override
    protected Buff getPolymorphBuff() {
        return BuffRegistry.Debuffs.POLYMORPH_ROOSTER;
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (!this.isMounted()) {
            return;
        }
        GameLight light = level.getLightLevel(GameMath.getTileCoordinate(x), GameMath.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 22 - 10;
        int drawY = camera.getDrawY(y) - 44 - 7;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        TextureDrawOptionsEnd shadow = MobRegistry.Textures.rooster_shadow.initDraw().sprite(0, dir, 64).light(light).pos(drawX, drawY += this.getBobbing(x, y));
        tileList.add(tm -> shadow.draw());
        final MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        final TextureDrawOptionsEnd options = MobRegistry.Textures.rooster.initDraw().sprite(sprite.x, sprite.y, 64).light(light).addMaskShader(swimMask).pos(drawX, drawY += this.getLevel().getTile(GameMath.getTileCoordinate(x), GameMath.getTileCoordinate(y)).getMobSinkingAmount(this));
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                swimMask.use();
                options.draw();
                swimMask.stop();
            }
        });
    }
}

