/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.registries.MobRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.levelEvent.IncursionLevelEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameColor;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.mapData.ClientDiscoveredMap;

public class BossSpawnPortalMob
extends Mob {
    protected Color color1 = new Color(150, 13, 38);
    protected Color color2 = new Color(255, 3, 62);
    protected Color color3 = new Color(255, 3, 49);
    protected Color color4 = new Color(244, 152, 152);
    protected Color color5 = new Color(253, 236, 236);
    public int remainingAttemptsTip = 0;

    public BossSpawnPortalMob() {
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
        save.addInt("remainingAttemptsTip", this.remainingAttemptsTip);
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.remainingAttemptsTip = save.getInt("remainingAttemptsTip", this.remainingAttemptsTip, false);
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextInt(this.remainingAttemptsTip);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.remainingAttemptsTip = reader.getNextInt();
    }

    @Override
    public void init() {
        super.init();
        this.setDir(0);
    }

    @Override
    public boolean canPushMob(Mob other) {
        return false;
    }

    @Override
    public boolean canBePushed(Mob other) {
        return false;
    }

    @Override
    public void clientTick() {
        int i;
        super.clientTick();
        this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, 0.0f, 0.7f);
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
            Color color = GameRandom.globalRandom.getOneOf(this.color1, this.color2, this.color3);
            this.getLevel().entityManager.addParticle(startX, startY, Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.nextInt(5), 0, 12)).sizeFades(10, 16).rotates().heightMoves(startHeight, endHeight).movesConstant(-speed, 0.0f).color(color).fadesAlphaTime(100, 50).lifeTime(lifeTime);
        }
        for (i = 0; i < 2; ++i) {
            int lifeTime = GameRandom.globalRandom.getIntBetween(500, 1000);
            float lifePerc = (float)lifeTime / 1000.0f;
            float startHeight = 5 + GameRandom.globalRandom.nextInt(5);
            float height = startHeight + (float)GameRandom.globalRandom.getIntBetween(20, 50) * lifePerc;
            Color color = GameRandom.globalRandom.getOneOf(this.color1, this.color2, this.color3);
            this.getLevel().entityManager.addParticle(this.x + GameRandom.globalRandom.getFloatBetween(-10.0f, 10.0f), this.y + GameRandom.globalRandom.getFloatBetween(-5.0f, 5.0f), Particle.GType.COSMETIC).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.nextInt(5), 0, 12)).sizeFadesInAndOut(6, 12, 100, lifeTime - 100).movesFriction(GameRandom.globalRandom.getFloatBetween(-20.0f, 20.0f), GameRandom.globalRandom.getFloatBetween(-10.0f, 10.0f), 0.7f).heightMoves(startHeight, height).color(color).lifeTime(lifeTime);
        }
    }

    @Override
    public void interact(PlayerMob player) {
        super.interact(player);
        if (player != null && player.isServerClient()) {
            Level level = this.getLevel();
            if (level instanceof IncursionLevel) {
                IncursionLevelEvent event = ((IncursionLevel)level).getIncursionLevelEvent();
                if (event != null) {
                    event.onBossSpawnTriggered(this);
                }
            } else {
                this.remove(0.0f, 0.0f, null, true);
            }
        }
    }

    @Override
    public boolean canInteract(Mob mob) {
        return true;
    }

    @Override
    protected void addHoverTooltips(ListGameTooltips tooltips, boolean debug) {
        super.addHoverTooltips(tooltips, debug);
        if (this.remainingAttemptsTip > 0) {
            GameColor color = GameColor.NO_COLOR;
            if (this.remainingAttemptsTip <= 2) {
                color = GameColor.RED;
            } else if (this.remainingAttemptsTip <= 4) {
                color = GameColor.ITEM_QUEST;
            }
            tooltips.add(new StringTooltips(Localization.translate("misc", "bossattempts", "number", (Object)this.remainingAttemptsTip), color));
        }
    }

    @Override
    protected String getInteractTip(PlayerMob perspective, boolean debug) {
        return Localization.translate("controls", "usetip");
    }

    @Override
    protected SoundSettings getHitDeathSound() {
        return new SoundSettings(GameResources.fadedeath2);
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 30; ++i) {
            int lifeTime = GameRandom.globalRandom.getIntBetween(500, 5000);
            float lifePerc = (float)lifeTime / 5000.0f;
            float startHeight = 20.0f;
            float height = startHeight + (float)GameRandom.globalRandom.getIntBetween(70, 150) * lifePerc;
            this.getLevel().entityManager.addParticle(this.x + GameRandom.globalRandom.getFloatBetween(-10.0f, 10.0f), this.y + GameRandom.globalRandom.getFloatBetween(-5.0f, 5.0f), Particle.GType.IMPORTANT_COSMETIC).sizeFades(8, 12).movesFriction(GameRandom.globalRandom.getFloatBetween(-40.0f, 40.0f), GameRandom.globalRandom.getFloatBetween(-20.0f, 20.0f), 0.5f).heightMoves(startHeight, height).colorRandom(GameRandom.globalRandom.getOneOf(this.color1, this.color2, this.color3), 0.1f, 0.2f, 0.2f).lifeTime(lifeTime);
        }
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(BossSpawnPortalMob.getTileCoordinate(x), BossSpawnPortalMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 1;
        int drawY = camera.getDrawY(y) - 25;
        int offset = (int)(this.getTime() % 1600L) / 200;
        if (offset > 4) {
            offset = 4 - offset % 4;
        }
        int maxSize = 50;
        final SharedTextureDrawOptions options = new SharedTextureDrawOptions(MobRegistry.Textures.bossPortal);
        this.addSphere(drawX, drawY + offset, 0L, 1000, (float)maxSize * 0.68f, maxSize, this.color1, light, options);
        this.addSphere(drawX, drawY + offset, 0L, 900, (float)maxSize * 0.68f, maxSize, this.color1, light, options);
        this.addSphere(drawX, drawY + offset, 0L, 800, (float)maxSize * 0.64f, (float)maxSize * 0.88f, this.color2, light, options);
        this.addSphere(drawX, drawY + offset, 0L, 760, (float)maxSize * 0.64f, (float)maxSize * 0.88f, this.color2, light, options);
        this.addSphere(drawX, drawY + offset, 0L, 700, (float)maxSize * 0.64f, (float)maxSize * 0.88f, this.color2, light, options);
        this.addSphere(drawX, drawY + offset, 0L, 740, (float)maxSize * 0.48f, (float)maxSize * 0.68f, this.color3, light, options);
        this.addSphere(drawX, drawY + offset, 0L, 680, (float)maxSize * 0.48f, (float)maxSize * 0.68f, this.color3, light, options);
        this.addSphere(drawX, drawY + offset, 0L, 650, (float)maxSize * 0.48f, (float)maxSize * 0.68f, this.color3, light, options);
        this.addSphere(drawX, drawY + offset, 0L, 630, (float)maxSize * 0.28f, (float)maxSize * 0.52f, this.color4, light, options);
        this.addSphere(drawX, drawY + offset, 0L, 580, (float)maxSize * 0.28f, (float)maxSize * 0.52f, this.color4, light, options);
        this.addSphere(drawX, drawY + offset, 0L, 550, (float)maxSize * 0.28f, (float)maxSize * 0.52f, this.color4, light, options);
        this.addSphere(drawX, drawY + offset, 0L, 540, (float)maxSize * 0.16f, (float)maxSize * 0.36f, this.color5, light, options);
        this.addSphere(drawX, drawY + offset, 0L, 520, (float)maxSize * 0.16f, (float)maxSize * 0.36f, this.color5, light, options);
        this.addSphere(drawX, drawY + offset, 0L, 490, (float)maxSize * 0.16f, (float)maxSize * 0.36f, this.color5, light, options);
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
        this.addShadowDrawables(tileList, level, x, y, light, camera);
    }

    protected void addSphere(int drawX, int drawY, long timeOffset, int animTime, float minSize, float maxSize, Color color, GameLight light, SharedTextureDrawOptions options) {
        float animFloat = GameUtils.getAnimFloat(this.getLocalTime() + timeOffset, animTime);
        float sizeDelta = maxSize - minSize;
        int size = (int)(minSize + sizeDelta * animFloat);
        options.addFull().color(color).alpha(0.5f).light(light).size(size).posMiddle(drawX, drawY, true);
    }

    @Override
    public boolean shouldDrawOnMap() {
        return true;
    }

    @Override
    public boolean isVisibleOnMap(Client client, ClientDiscoveredMap map) {
        return true;
    }

    @Override
    public void drawOnMap(TickManager tickManager, Client client, int x, int y, double tileScale, Rectangle drawBounds, boolean isMinimap) {
        GameLight light = this.getLevel().lightManager.newLight(150.0f);
        int drawX = x;
        int drawY = y - 10;
        int maxSize = 32;
        SharedTextureDrawOptions options = new SharedTextureDrawOptions(MobRegistry.Textures.bossPortal);
        this.addSphere(drawX, drawY, 0L, 1000, (float)maxSize * 0.68f, maxSize, this.color1, light, options);
        this.addSphere(drawX, drawY, 0L, 900, (float)maxSize * 0.68f, maxSize, this.color1, light, options);
        this.addSphere(drawX, drawY, 0L, 800, (float)maxSize * 0.64f, (float)maxSize * 0.88f, this.color2, light, options);
        this.addSphere(drawX, drawY, 0L, 760, (float)maxSize * 0.64f, (float)maxSize * 0.88f, this.color2, light, options);
        this.addSphere(drawX, drawY, 0L, 700, (float)maxSize * 0.64f, (float)maxSize * 0.88f, this.color2, light, options);
        this.addSphere(drawX, drawY, 0L, 740, (float)maxSize * 0.48f, (float)maxSize * 0.68f, this.color3, light, options);
        this.addSphere(drawX, drawY, 0L, 680, (float)maxSize * 0.48f, (float)maxSize * 0.68f, this.color3, light, options);
        this.addSphere(drawX, drawY, 0L, 650, (float)maxSize * 0.48f, (float)maxSize * 0.68f, this.color3, light, options);
        this.addSphere(drawX, drawY, 0L, 630, (float)maxSize * 0.28f, (float)maxSize * 0.52f, this.color4, light, options);
        this.addSphere(drawX, drawY, 0L, 580, (float)maxSize * 0.28f, (float)maxSize * 0.52f, this.color4, light, options);
        this.addSphere(drawX, drawY, 0L, 550, (float)maxSize * 0.28f, (float)maxSize * 0.52f, this.color4, light, options);
        this.addSphere(drawX, drawY, 0L, 540, (float)maxSize * 0.16f, (float)maxSize * 0.36f, this.color5, light, options);
        this.addSphere(drawX, drawY, 0L, 520, (float)maxSize * 0.16f, (float)maxSize * 0.36f, this.color5, light, options);
        this.addSphere(drawX, drawY, 0L, 490, (float)maxSize * 0.16f, (float)maxSize * 0.36f, this.color5, light, options);
        options.draw();
    }

    @Override
    public Rectangle drawOnMapBox(double tileScale, boolean isMinimap) {
        return new Rectangle(-12, -24, 24, 28);
    }

    @Override
    public GameTooltips getMapTooltips() {
        return new StringTooltips(this.getDisplayName());
    }
}

