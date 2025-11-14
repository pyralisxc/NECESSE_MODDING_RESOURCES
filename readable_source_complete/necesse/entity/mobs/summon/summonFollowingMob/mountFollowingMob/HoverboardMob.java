/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.summon.summonFollowingMob.mountFollowingMob;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.stream.Stream;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.gameLoop.tickManager.TicksPerSecond;
import necesse.engine.localization.Localization;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.Packet;
import necesse.engine.network.packet.PacketForceOfWind;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MountAbility;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.summon.summonFollowingMob.mountFollowingMob.MountFollowingMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class HoverboardMob
extends MountFollowingMob
implements MountAbility {
    protected TicksPerSecond particleTicks = TicksPerSecond.ticksPerSecond(30);

    public HoverboardMob() {
        super(100);
        this.setSpeed(120.0f);
        this.setFriction(0.2f);
        this.setSwimSpeed(1.0f);
        this.accelerationMod = 0.8f;
        this.setKnockbackModifier(0.5f);
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-14, -14, 28, 28);
        this.selectBox = new Rectangle(-15, -15, 30, 30);
        this.overrideMountedWaterWalking = true;
    }

    @Override
    public void runMountAbility(PlayerMob player, Packet content) {
        int strength = 160;
        Point2D.Float dir = PacketForceOfWind.getMobDir(player);
        PacketForceOfWind.applyToMob(this.getLevel(), this, dir.x, dir.y, strength);
        PacketForceOfWind.addCooldownStack(player, 3.0f, false);
        player.buffManager.addBuff(new ActiveBuff(BuffRegistry.FOW_ACTIVE, (Mob)player, 0.15f, null), false);
        player.buffManager.forceUpdateBuffs();
        if (this.isClient()) {
            SoundManager.playSound(GameResources.fadedeath1, (SoundEffect)SoundEffect.effect(player).volume(0.5f).pitch(2.0f));
        }
    }

    @Override
    public boolean canRunMountAbility(PlayerMob player, Packet content) {
        return !PacketForceOfWind.isOnCooldown(player);
    }

    @Override
    public void tickMovement(float delta) {
        super.tickMovement(delta);
        if (this.isClient() && (this.moveX != 0.0f || this.moveY != 0.0f)) {
            this.particleTicks.tick(delta);
            while (this.particleTicks.shouldTick()) {
                int dir = this.getDir();
                float dirX = 0.0f;
                float dirY = 0.0f;
                if (dir == 0) {
                    dirY = -1.0f;
                } else if (dir == 1) {
                    dirX = 1.0f;
                } else if (dir == 2) {
                    dirY = 1.0f;
                } else if (dir == 3) {
                    dirX = -1.0f;
                }
                this.getLevel().entityManager.addParticle(this.x - dirX * 20.0f + GameRandom.globalRandom.floatGaussian() * 3.0f, this.y - dirY * 12.0f + GameRandom.globalRandom.floatGaussian() * 3.0f, Particle.GType.COSMETIC).movesConstant(this.dx / 5.0f, this.dy / 5.0f).color(new Color(80, 98, 108)).sizeFades(6, 12).lifeTime(1000);
            }
        }
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (!this.isMounted()) {
            this.moveX = 0.0f;
            this.moveY = 0.0f;
        }
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (!this.isMounted()) {
            this.moveX = 0.0f;
            this.moveY = 0.0f;
        }
    }

    @Override
    public int getFlyingHeight() {
        return 2;
    }

    @Override
    protected String getInteractTip(PlayerMob perspective, boolean debug) {
        if (this.isMounted()) {
            return null;
        }
        return Localization.translate("controls", "usetip");
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(HoverboardMob.getTileCoordinate(x), HoverboardMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 32;
        int drawY = camera.getDrawY(y) - 40;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        final TextureDrawOptionsEnd options = MobRegistry.Textures.hoverBoard.body.initDraw().sprite(sprite.x, sprite.y, 64).light(light).pos(drawX, drawY += this.getBobbing(x, y));
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
            }

            @Override
            public void drawBehindRider(TickManager tickManager) {
                options.draw();
            }
        });
        this.addShadowDrawables(tileList, level, x, y, light, camera);
    }

    @Override
    public Point getAnimSprite(int x, int y, int dir) {
        return new Point((int)(this.getWorldEntity().getTime() / (long)this.getRockSpeed()) % 4, dir % 4);
    }

    @Override
    protected TextureDrawOptions getShadowDrawOptions(Level level, int x, int y, GameLight light, GameCamera camera) {
        GameTexture shadowTexture = MobRegistry.Textures.hoverBoard.shadow;
        int drawX = camera.getDrawX(x) - 32;
        int drawY = camera.getDrawY(y) - 40;
        return shadowTexture.initDraw().sprite(0, this.getDir(), 64).light(light).pos(drawX, drawY += this.getBobbing(x, y));
    }

    @Override
    public int getRockSpeed() {
        return 500;
    }

    @Override
    public int getWaterRockSpeed() {
        return 500;
    }

    @Override
    public Point getSpriteOffset(int spriteX, int spriteY) {
        Point p = new Point(0, 0);
        if (spriteX == 0 || spriteX == 2) {
            p.y = 2;
        }
        p.x += this.getRiderDrawXOffset();
        p.y += this.getRiderDrawYOffset();
        return p;
    }

    @Override
    public int getRiderDrawYOffset() {
        return -8;
    }

    @Override
    public int getRiderArmSpriteX() {
        return 0;
    }

    @Override
    public int getRiderSpriteX() {
        return 0;
    }

    @Override
    public int getRiderDir(int startDir) {
        return (startDir + 1) % 4;
    }

    @Override
    public GameTexture getRiderMask() {
        return null;
    }

    @Override
    public Stream<ModifierValue<?>> getDefaultModifiers() {
        return Stream.of(new ModifierValue<Boolean>(BuffModifiers.BOUNCY, true));
    }

    @Override
    public Stream<ModifierValue<?>> getDefaultRiderModifiers() {
        return Stream.of(new ModifierValue<Boolean>(BuffModifiers.WATER_WALKING, true));
    }

    @Override
    protected SoundSettings getAmbientSound() {
        return new SoundSettings(GameResources.hoverboard).volume(0.15f);
    }
}

