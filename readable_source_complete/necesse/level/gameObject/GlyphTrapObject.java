/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.util.tween.Easings;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.GlyphTrapObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public abstract class GlyphTrapObject
extends GameObject {
    protected ObjectDamagedTextureArray damageTexture;
    protected GameTexture colorTexture;
    protected final String glyphTextureName;

    public GlyphTrapObject(String glyphTextureName, float lightHue) {
        super(new Rectangle(0, 0, 0, 0));
        this.glyphTextureName = glyphTextureName;
        this.showsWire = true;
        this.toolType = ToolType.ALL;
        this.rarity = Item.Rarity.UNCOMMON;
        this.setItemCategory("objects", "traps");
        this.displayMapTooltip = true;
        this.isLightTransparent = true;
        this.lightLevel = 100;
        this.lightHue = lightHue;
        this.lightSat = 0.65f;
        this.replaceCategories.add("glyphtrap");
        this.canReplaceCategories.add("glyphtrap");
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.damageTexture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/" + this.glyphTextureName + "_greyscale");
        this.colorTexture = GameTexture.fromFile("objects/" + this.glyphTextureName);
    }

    @Override
    public Color getMapColor(Level level, int tileX, int tileY) {
        return level.getTile(tileX, tileY).getMapColor(level, tileX, tileY).brighter();
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, level, tileX, tileY, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameTexture texture = this.damageTexture.getDamagedTexture(this, level, tileX, tileY);
        GlyphTrapObjectEntity entity = this.getCurrentObjectEntity(level, tileX, tileY, GlyphTrapObjectEntity.class);
        float cooldownPercent = entity == null ? 1.0f : entity.getCooldownPercent();
        TextureDrawOptionsEnd baseOptionsBackground = texture.initDraw().light(light).pos(drawX, drawY);
        int progressSizeOffset = (int)(16.0f * Easings.QuadIn.ease(cooldownPercent));
        TextureDrawOptionsEnd baseOptionsProgress = this.colorTexture.initDraw().section(16 - progressSizeOffset, 16 + progressSizeOffset, 16 - progressSizeOffset, 16 + progressSizeOffset).light(light).pos(drawX + 16 - progressSizeOffset, drawY + 16 - progressSizeOffset);
        tileList.add(tm -> {
            baseOptionsBackground.draw();
            baseOptionsProgress.draw();
        });
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        super.drawPreview(level, tileX, tileY, rotation, alpha, player, camera);
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameTexture texture = this.damageTexture.getDamagedTexture(this, level, tileX, tileY);
        texture.initDraw().sprite(0, 0, 32, 32).light(light).pos(drawX, drawY).alpha(alpha).draw(drawX, drawY);
    }

    @Override
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new GlyphTrapObjectEntity(level, x, y, () -> this.addLevelEvent(level, x, y));
    }

    @Override
    public void onWireUpdate(Level level, int layerID, int tileX, int tileY, int wireID, boolean active) {
        if (active && level.isServer()) {
            GlyphTrapObjectEntity currentObjectEntity = this.getCurrentObjectEntity(level, tileX, tileY, GlyphTrapObjectEntity.class);
            currentObjectEntity.triggerTrap(wireID, level.getObjectRotation(tileX, tileY));
        }
    }

    @Override
    public int getLightLevel(Level level, int layerID, int tileX, int tileY) {
        GlyphTrapObjectEntity currentObjectEntity = this.getCurrentObjectEntity(level, tileX, tileY, GlyphTrapObjectEntity.class);
        return (int)((float)super.getLightLevel(level, layerID, tileX, tileY) * currentObjectEntity.getCooldownPercent());
    }

    @Override
    public void interact(Level level, int x, int y, PlayerMob player) {
        if (level.isServer()) {
            PacketOpenContainer p = PacketOpenContainer.ObjectEntity(ContainerRegistry.GLYPH_TRAP_CONTAINER, level.entityManager.getObjectEntity(x, y));
            ContainerRegistry.openAndSendContainer(player.getServerClient(), p);
        }
    }

    @Override
    public boolean canInteract(Level level, int x, int y, PlayerMob player) {
        return level.objectLayer.isPlayerPlaced(x, y);
    }

    protected abstract void addLevelEvent(Level var1, int var2, int var3);
}

