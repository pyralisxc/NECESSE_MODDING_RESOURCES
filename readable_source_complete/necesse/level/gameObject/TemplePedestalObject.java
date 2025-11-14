/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.hostile.bosses.FlyingSpiritsHead;
import necesse.entity.mobs.hostile.bosses.GritHead;
import necesse.entity.mobs.hostile.bosses.SageAndGritStartMob;
import necesse.entity.mobs.hostile.bosses.SageHead;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class TemplePedestalObject
extends GameObject {
    public ObjectDamagedTextureArray texture;
    protected boolean hasRotation;
    protected int yOffset = -3;

    public TemplePedestalObject() {
        super(new Rectangle(2, 5, 28, 22));
        this.toolType = ToolType.UNBREAKABLE;
        this.mapColor = new Color(186, 136, 46);
        this.isLightTransparent = true;
        this.hoverHitbox = new Rectangle(0, -32, 32, 64);
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/templepedestal");
    }

    @Override
    public void tickEffect(Level level, int layerID, int tileX, int tileY) {
        super.tickEffect(level, layerID, tileX, tileY);
        int angle = (int)((double)level.getWorldEntity().getTime() / 1.5 % 360.0);
        Rectangle bounds = GameUtils.rangeTileBounds(tileX * 32 + 16, tileY * 32 + 16, 50);
        if (level.entityManager.mobs.streamInRegionsShape(bounds, 0).anyMatch(m -> m instanceof GritHead)) {
            this.spawnParticles(level, tileX, tileY, angle, FlyingSpiritsHead.Variant.GRIT.particleHue);
        }
        if (level.entityManager.mobs.streamInRegionsShape(bounds, 0).anyMatch(m -> m instanceof SageHead)) {
            this.spawnParticles(level, tileX, tileY, angle + 180, FlyingSpiritsHead.Variant.SAGE.particleHue);
        }
    }

    protected void spawnParticles(Level level, int tileX, int tileY, float angle, float hue) {
        level.lightManager.refreshParticleLightFloat((float)(tileX * 32 + 16), (float)(tileY * 32 + 16), hue, 0.8f);
        Point2D.Float dir = GameMath.getAngleDir(angle);
        for (int i = 0; i < 2; ++i) {
            int length = GameRandom.globalRandom.getIntBetween(6, 12);
            int startHeight = (int)(32.0f + dir.y * (float)length * 0.6f);
            level.entityManager.addParticle((float)(tileX * 32 + 16) + dir.x * (float)length, tileY * 32 + 16, i == 0 ? Particle.GType.CRITICAL : Particle.GType.IMPORTANT_COSMETIC).heightMoves(startHeight, startHeight + 16).color(ParticleOption.randomizeColor(hue, 0.8f, 0.6f, 0.0f, 0.0f, 0.1f)).sizeFades(8, 14).lifeTime(1500).onProgress(0.8f, p -> {
                for (int j = 0; j < GameRandom.globalRandom.getIntBetween(1, 2); ++j) {
                    level.entityManager.addParticle(p.x + (float)((int)(GameRandom.globalRandom.nextGaussian() * 2.0)), p.y, Particle.GType.COSMETIC).smokeColor(hue).sizeFades(6, 10).heightMoves(startHeight + 14, startHeight + 36);
                }
            });
        }
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        TextureDrawOptionsEnd drawOptions;
        GameLight light = level.getLightLevel(tileX, tileY);
        GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY) - texture.getHeight() + 32;
        if (this.hasRotation) {
            byte rotation = level.getObjectRotation(tileX, tileY);
            int spriteWidth = texture.getWidth() / 4;
            int xOffset = (spriteWidth - 32) / 2;
            drawOptions = texture.initDraw().sprite(rotation % 4, 0, spriteWidth, texture.getHeight()).light(light).pos(drawX - xOffset, drawY + this.yOffset);
        } else {
            int xOffset = (texture.getWidth() - 32) / 2;
            drawOptions = texture.initDraw().light(light).pos(drawX - xOffset, drawY + this.yOffset);
        }
        list.add(new LevelSortedDrawable(this, tileX, tileY){

            @Override
            public int getSortY() {
                return 16;
            }

            @Override
            public void draw(TickManager tickManager) {
                drawOptions.draw();
            }
        });
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        GameTexture texture = this.texture.getDamagedTexture(0.0f);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY) - texture.getHeight() + 32;
        if (this.hasRotation) {
            int spriteWidth = texture.getWidth() / 4;
            int xOffset = (spriteWidth - 32) / 2;
            texture.initDraw().sprite(rotation % 4, 0, spriteWidth, texture.getHeight()).alpha(alpha).draw(drawX - xOffset, drawY + this.yOffset);
        } else {
            int xOffset = (texture.getWidth() - 32) / 2;
            texture.initDraw().alpha(alpha).draw(drawX - xOffset, drawY + this.yOffset);
        }
    }

    @Override
    public boolean canInteract(Level level, int x, int y, PlayerMob player) {
        return true;
    }

    @Override
    public String getInteractTip(Level level, int x, int y, PlayerMob perspective, boolean debug) {
        return Localization.translate("controls", "activatetip");
    }

    @Override
    public void interact(Level level, int x, int y, PlayerMob player) {
        super.interact(level, x, y, player);
        Item item = ItemRegistry.getItem("dragonsouls");
        if (!player.isItemOnCooldown(item)) {
            GameMessage summonError = null;
            if (level instanceof IncursionLevel && (summonError = ((IncursionLevel)level).canSummonBoss("sageandgrit")) != null && player.isServerClient()) {
                player.getServerClient().sendChatMessage(summonError);
            }
            if (summonError == null && player.getInv().removeItems(item, 1, false, false, false, false, "use") > 0) {
                player.startItemCooldown(item, 2000);
                if (level.isServer()) {
                    System.out.println("Flying Spirits has been summoned at " + level.getIdentifier() + ".");
                    SageAndGritStartMob mob = (SageAndGritStartMob)MobRegistry.getMob("sageandgrit", level);
                    mob.pedestalPosition = new Point(x, y);
                    level.entityManager.addMob(mob, x * 32 + 16, y * 32 + 16);
                    level.getServer().network.sendToClientsWithEntity(new PacketChatMessage(new LocalMessage("misc", "bosssummon", "name", MobRegistry.getLocalization("grit"))), mob);
                    level.getServer().network.sendToClientsWithEntity(new PacketChatMessage(new LocalMessage("misc", "bosssummon", "name", MobRegistry.getLocalization("sage"))), mob);
                    if (level instanceof IncursionLevel) {
                        ((IncursionLevel)level).onBossSummoned(mob);
                    }
                }
            } else if (summonError == null && level.isServer() && player.isServerClient()) {
                player.getServerClient().sendChatMessage(new LocalMessage("misc", "bossmissingitem"));
            }
        }
    }
}

