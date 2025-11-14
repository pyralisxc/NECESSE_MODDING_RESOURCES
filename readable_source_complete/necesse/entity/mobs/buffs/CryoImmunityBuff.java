/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobBeforeHitCalculatedEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.MobExtraDrawBuff;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.mobs.buffs.staticBuffs.FrozenMobImmuneBuff;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.level.maps.light.GameLight;

public class CryoImmunityBuff
extends Buff
implements MobExtraDrawBuff {
    protected GameTexture iceOverlay;
    protected int iceX;
    protected long startShakeTime;

    public CryoImmunityBuff() {
        this.isVisible = true;
        this.isImportant = true;
        this.canCancel = false;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(BuffModifiers.PARALYZED, true);
        buff.setModifier(BuffModifiers.GROUNDED, true);
        buff.setModifier(BuffModifiers.SLOW, Float.valueOf(1.0f));
        buff.owner.dx = 0.0f;
        buff.owner.dy = 0.0f;
        this.iceX = GameRandom.globalRandom.getIntBetween(0, 2);
        this.startShakeTime = buff.owner.getLocalTime();
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.iceOverlay = GameTexture.fromFile("particles/cryoimmunity");
    }

    @Override
    public void onRemoved(ActiveBuff buff) {
        super.onRemoved(buff);
        if (buff.owner != null && buff.owner.isClient()) {
            SoundManager.playSound(GameResources.iceBreak, (SoundEffect)SoundEffect.effect(buff.owner).pitch(GameRandom.globalRandom.getFloatBetween(0.9f, 1.1f)));
            for (int i = 0; i < 5; ++i) {
                Rectangle selectBox = buff.owner.getSelectBox();
                GameTextureSection sprite = FrozenMobImmuneBuff.getAppropriateRandomDebrisSprite(selectBox.width, selectBox.height);
                buff.owner.getLevel().entityManager.addParticle(new FleshParticle(buff.owner.getLevel(), sprite, buff.owner.x, buff.owner.y, 20.0f, 0.0f, 0.0f), Particle.GType.IMPORTANT_COSMETIC);
            }
        }
    }

    @Override
    public void onBeforeHitCalculated(ActiveBuff buff, MobBeforeHitCalculatedEvent event) {
        super.onBeforeHitCalculated(buff, event);
        if (!event.isPrevented()) {
            event.prevent();
            event.playHitSound = false;
            event.showDamageTip = false;
        }
    }

    @Override
    public void clientTick(ActiveBuff buff) {
        super.clientTick(buff);
    }

    @Override
    public void addFrontDrawOptions(ActiveBuff buff, LinkedList<DrawOptions> list, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        Mob mount = buff.owner.getMount();
        if (mount != null && !mount.shouldDrawRider()) {
            return;
        }
        GameLight light = buff.owner.getLevel().getLightLevel(buff.owner);
        Rectangle selectBox = buff.owner.getSelectBox(buff.owner.getX(), buff.owner.getY());
        float timeAliveSeconds = (float)(buff.getDuration() - buff.getDurationLeft()) * 0.001f;
        float colorFactor = GameMath.limit(1.0f - 0.5f * (timeAliveSeconds / ((float)buff.getDuration() * 0.001f)), 0.0f, 1.0f);
        Point2D.Float shake = FrozenMobImmuneBuff.CRACK_SHAKE.getCurrentShake(this.startShakeTime, buff.owner.getLocalTime());
        if (this.iceOverlay != null) {
            int sizeW = GameMath.limit((int)(64.0f * timeAliveSeconds * 3.0f), 0, 64);
            int sizeY = GameMath.limit((int)(96.0f * timeAliveSeconds * 3.0f), 0, 96);
            int drawX = camera.getDrawX((float)(selectBox.x + 32) - (float)sizeW * 0.5f + shake.x) - 18;
            int drawY = camera.getDrawY((float)selectBox.y + 67.2f - (float)sizeY * 0.7f + shake.y) - 24;
            TextureDrawOptionsEnd drawOptions = this.iceOverlay.initDraw().sprite(this.iceX, 0, 64, 96).light(light).colorLight(new Color(colorFactor, GameMath.limit(colorFactor, 0.6f, 1.0f), 1.0f), light.minLevelCopy(100.0f * GameMath.limit(timeAliveSeconds, 0.0f, 1.0f))).alpha(1.5f * timeAliveSeconds).size(sizeW, sizeY).pos(drawX, drawY);
            list.add(drawOptions);
        }
    }

    @Override
    public void addBackDrawOptions(ActiveBuff buff, LinkedList<DrawOptions> list, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
    }
}

