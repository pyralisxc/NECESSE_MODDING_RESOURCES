/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.polymorph;

import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.packet.PacketMountMobJump;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.gameSound.GameSound;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.PolymorphMob;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.mobs.jumping.JumpingMobInterface;
import necesse.entity.mobs.jumping.JumpingMobStats;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class FrogPolymorphMob
extends PolymorphMob
implements JumpingMobInterface {
    protected long idleAnimTime;
    protected JumpingMobStats jumpStats = new JumpingMobStats(this);

    public FrogPolymorphMob() {
        this.setSpeed(45.0f);
        this.setFriction(2.0f);
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-12, -14, 24, 24);
        this.selectBox = new Rectangle(-16, -28, 32, 34);
        this.swimMaskMove = 12;
        this.swimMaskOffset = 0;
        this.swimSinkOffset = 0;
        this.jumpStats.setJumpCooldown(100);
        this.jumpStats.setJumpStrength(100.0f);
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    protected void spaceAbility() {
        super.spaceAbility();
        this.idleAnimTime = this.getLocalTime();
    }

    @Override
    protected void calcAcceleration(float speed, float friction, float moveX, float moveY, float delta) {
        boolean inLiquid = this.inLiquid();
        if (inLiquid) {
            super.calcAcceleration(speed, friction, moveX, moveY, delta);
        } else {
            boolean clientControlled;
            Mob mounter = this.getRider();
            boolean bl = clientControlled = mounter != null && mounter.isPlayer;
            if (!clientControlled) {
                this.tickJump(moveX, moveY);
            } else if (this.isClient() && this.getLevel().getClient().getPlayer() == mounter) {
                this.tickJump(moveX, moveY, (dx, dy) -> {
                    this.runJump(dx.floatValue(), dy.floatValue());
                    this.getLevel().getClient().network.sendPacket(new PacketMountMobJump(this, dx.floatValue(), dy.floatValue()));
                });
            }
            super.calcAcceleration(speed, friction, 0.0f, 0.0f, delta);
        }
    }

    @Override
    protected GameSound getMobSound() {
        return GameRandom.globalRandom.getOneOf(GameResources.frogAmbient1, GameResources.frogAmbient2, GameResources.frogAmbient3);
    }

    @Override
    protected Buff getPolymorphBuff() {
        return BuffRegistry.Debuffs.POLYMORPH_FROG;
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        long idleAnimTimePassed;
        if (!this.isMounted()) {
            return;
        }
        GameLight light = level.getLightLevel(GameMath.getTileCoordinate(x), GameMath.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 31;
        int drawY = camera.getDrawY(y) - 54;
        int dir = this.getDir();
        boolean inLiquid = this.inLiquid(x, y);
        int sprite = inLiquid ? 5 : this.getJumpAnimationFrame(5);
        drawY += this.getBobbing(x, y);
        drawY += this.getLevel().getTile(GameMath.getTileCoordinate(x), GameMath.getTileCoordinate(y)).getMobSinkingAmount(this);
        int dirSprite = dir;
        int shadowSprite = sprite;
        if (!this.isAccelerating() && sprite == 0 && (idleAnimTimePassed = this.getLocalTime() - this.idleAnimTime) <= 500L) {
            dirSprite += 4;
            sprite = (int)(idleAnimTimePassed / 100L);
        }
        final MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        final TextureDrawOptionsEnd options = MobRegistry.Textures.frog.body.initDraw().sprite(sprite, dirSprite, 64).addMaskShader(swimMask).light(light).pos(drawX, drawY);
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                swimMask.use();
                options.draw();
                swimMask.stop();
            }
        });
        TextureDrawOptionsEnd shadow = MobRegistry.Textures.frog.shadow.initDraw().sprite(shadowSprite, dir, 64).light(light).pos(drawX, drawY);
        tileList.add(tm -> shadow.draw());
    }

    @Override
    public JumpingMobStats getJumpStats() {
        return this.jumpStats;
    }
}

