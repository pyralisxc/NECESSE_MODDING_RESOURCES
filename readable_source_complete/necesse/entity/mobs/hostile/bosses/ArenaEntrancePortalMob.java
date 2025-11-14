/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.TeleportResult;
import necesse.entity.levelEvent.TeleportEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.regionSystem.Region;

public class ArenaEntrancePortalMob
extends Mob {
    protected int aliveBuffer;
    public boolean keepAliveAlways = false;
    public LevelIdentifier targetLevelIdentifier;
    public Point targetPos;
    public boolean keepLoaded;

    public ArenaEntrancePortalMob() {
        super(100);
        this.setFriction(1.0f);
        this.isSummoned = true;
        this.shouldSave = true;
        this.isStatic = true;
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-14, -12, 28, 24);
        this.selectBox = new Rectangle(-18, -45, 36, 48);
        this.setKnockbackModifier(0.0f);
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        if (this.keepAliveAlways) {
            save.addBoolean("keepAliveAlways", true);
        }
        if (this.keepLoaded) {
            save.addBoolean("keepLoaded", true);
        }
        if (this.targetLevelIdentifier != null) {
            save.addSafeString("targetLevelIdentifier", this.targetLevelIdentifier.stringID);
        }
        if (this.targetPos != null) {
            save.addPoint("targetPos", this.targetPos);
        }
    }

    @Override
    public void applyLoadData(LoadData save) {
        Point targetPos;
        super.applyLoadData(save);
        this.keepAliveAlways = save.getBoolean("keepAliveAlways", false, false);
        this.keepLoaded = save.getBoolean("keepLoaded", false, false);
        String targetIdentifierStringID = save.getSafeString("targetLevelIdentifier", null, false);
        if (targetIdentifierStringID != null) {
            this.targetLevelIdentifier = new LevelIdentifier(targetIdentifierStringID);
        }
        if ((targetPos = save.getPoint("targetPos")) != null) {
            this.targetPos = targetPos;
        }
    }

    @Override
    public void init() {
        super.init();
        this.setDir(0);
        this.keepAlive();
    }

    @Override
    public boolean canPushMob(Mob other) {
        return false;
    }

    @Override
    public boolean canBePushed(Mob other) {
        return false;
    }

    public void keepAlive() {
        this.aliveBuffer = 20;
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (!this.keepAliveAlways) {
            --this.aliveBuffer;
            if (this.aliveBuffer <= 0) {
                this.remove();
            }
        }
        if (this.keepLoaded) {
            this.getLevel().unloadLevelBuffer = 0;
            Region region = this.getLevel().regionManager.getRegionByTile(this.getTileX(), this.getTileY(), false);
            if (region != null) {
                region.unloadRegionBuffer.keepLoaded();
            }
        }
    }

    @Override
    public void clientTick() {
        int i;
        super.clientTick();
        this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, 280.0f, 0.7f);
        for (i = 0; i < 2; ++i) {
            int angle = GameRandom.globalRandom.nextInt(360);
            Point2D.Float dir = GameMath.getAngleDir(angle);
            float range = GameRandom.globalRandom.getFloatBetween(25.0f, 40.0f);
            float startX = this.x + dir.x * range;
            float startY = this.y + 4.0f;
            float endHeight = 29.0f;
            float startHeight = endHeight + dir.y * range;
            int lifeTime = GameRandom.globalRandom.getIntBetween(200, 500);
            float speed = dir.x * range * 250.0f / (float)lifeTime;
            Color color1 = new Color(65, 178, 227);
            Color color2 = new Color(82, 210, 255);
            Color color3 = new Color(116, 245, 253);
            Color color = GameRandom.globalRandom.getOneOf(color1, color2, color3);
            this.getLevel().entityManager.addParticle(startX, startY, Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.nextInt(5), 0, 12)).sizeFades(10, 16).rotates().heightMoves(startHeight, endHeight).movesConstant(-speed, 0.0f).color(color).fadesAlphaTime(100, 50).lifeTime(lifeTime);
        }
        for (i = 0; i < 2; ++i) {
            int lifeTime = GameRandom.globalRandom.getIntBetween(500, 1000);
            float lifePerc = (float)lifeTime / 1000.0f;
            float startHeight = 5 + GameRandom.globalRandom.nextInt(5);
            float height = startHeight + (float)GameRandom.globalRandom.getIntBetween(20, 50) * lifePerc;
            Color color1 = new Color(65, 178, 227);
            Color color2 = new Color(82, 210, 255);
            Color color3 = new Color(116, 245, 253);
            Color color = GameRandom.globalRandom.getOneOf(color1, color2, color3);
            this.getLevel().entityManager.addParticle(this.x + GameRandom.globalRandom.getFloatBetween(-10.0f, 10.0f), this.y + GameRandom.globalRandom.getFloatBetween(-5.0f, 5.0f), Particle.GType.COSMETIC).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.nextInt(5), 0, 12)).sizeFadesInAndOut(6, 12, 100, lifeTime - 100).movesFriction(GameRandom.globalRandom.getFloatBetween(-20.0f, 20.0f), GameRandom.globalRandom.getFloatBetween(-10.0f, 10.0f), 0.7f).heightMoves(startHeight, height).color(color).lifeTime(lifeTime);
        }
    }

    @Override
    public void interact(PlayerMob player) {
        super.interact(player);
        if (player != null && player.isServerClient() && !player.buffManager.hasBuff("teleportsickness")) {
            ServerClient client = player.getServerClient();
            LevelIdentifier oldLevelIdentifier = client.getLevelIdentifier();
            TeleportEvent e = new TeleportEvent(client, 0, this.targetLevelIdentifier == null ? oldLevelIdentifier : this.targetLevelIdentifier, 0.0f, null, newLevel -> {
                if (!newLevel.shouldSave()) {
                    client.setFallbackLevel(this.getLevel(), this.getTileX(), this.getTileY());
                }
                return new TeleportResult(true, this.targetPos.x, this.targetPos.y);
            });
            this.getLevel().entityManager.events.add(e);
        }
    }

    @Override
    public boolean canInteract(Mob mob) {
        return true;
    }

    @Override
    protected String getInteractTip(PlayerMob perspective, boolean debug) {
        return Localization.translate("controls", "usetip");
    }

    @Override
    protected SoundSettings getHitDeathSound() {
        return new SoundSettings(GameResources.fadedeath3);
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(ArenaEntrancePortalMob.getTileCoordinate(x), ArenaEntrancePortalMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 1;
        int drawY = camera.getDrawY(y) - 25;
        int offset = (int)(this.getWorldEntity().getTime() % 1600L) / 200;
        if (offset > 4) {
            offset = 4 - offset % 4;
        }
        Color color1 = new Color(65, 178, 227);
        Color color2 = new Color(82, 210, 255);
        Color color3 = new Color(116, 245, 253);
        Color color4 = new Color(166, 246, 255);
        Color color5 = new Color(198, 243, 255);
        int maxSize = 50;
        final SharedTextureDrawOptions options = new SharedTextureDrawOptions(MobRegistry.Textures.portalSphere);
        this.addSphere(drawX, drawY + offset, 0L, 1000, maxSize - 16, maxSize, color1, light, options);
        this.addSphere(drawX, drawY + offset, 0L, 900, maxSize - 16, maxSize, color1, light, options);
        this.addSphere(drawX, drawY + offset, 0L, 800, maxSize - 18, maxSize - 6, color2, light, options);
        this.addSphere(drawX, drawY + offset, 0L, 760, maxSize - 18, maxSize - 6, color2, light, options);
        this.addSphere(drawX, drawY + offset, 0L, 700, maxSize - 18, maxSize - 6, color2, light, options);
        this.addSphere(drawX, drawY + offset, 0L, 740, maxSize - 26, maxSize - 16, color3, light, options);
        this.addSphere(drawX, drawY + offset, 0L, 680, maxSize - 26, maxSize - 16, color3, light, options);
        this.addSphere(drawX, drawY + offset, 0L, 650, maxSize - 26, maxSize - 16, color3, light, options);
        this.addSphere(drawX, drawY + offset, 0L, 630, maxSize - 36, maxSize - 24, color4, light, options);
        this.addSphere(drawX, drawY + offset, 0L, 580, maxSize - 36, maxSize - 24, color4, light, options);
        this.addSphere(drawX, drawY + offset, 0L, 550, maxSize - 36, maxSize - 24, color4, light, options);
        this.addSphere(drawX, drawY + offset, 0L, 540, maxSize - 42, maxSize - 32, color5, light, options);
        this.addSphere(drawX, drawY + offset, 0L, 520, maxSize - 42, maxSize - 32, color5, light, options);
        this.addSphere(drawX, drawY + offset, 0L, 490, maxSize - 42, maxSize - 32, color5, light, options);
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
        this.addShadowDrawables(tileList, level, x, y, light, camera);
    }

    protected void addSphere(int drawX, int drawY, long timeOffset, int animTime, int minSize, int maxSize, Color color, GameLight light, SharedTextureDrawOptions options) {
        float animFloat = GameUtils.getAnimFloat(this.getWorldEntity().getLocalTime() + timeOffset, animTime);
        int sizeDelta = maxSize - minSize;
        int size = minSize + (int)((float)sizeDelta * animFloat);
        options.addFull().color(color).alpha(0.5f).light(light).size(size).posMiddle(drawX, drawY, true);
    }
}

