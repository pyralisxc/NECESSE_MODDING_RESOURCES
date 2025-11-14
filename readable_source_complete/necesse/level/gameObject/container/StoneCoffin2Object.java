/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject.container;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEUsers;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.lootTable.LootTable;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.gameObject.container.CoffinObject;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;

class StoneCoffin2Object
extends CoffinObject {
    protected StoneCoffin2Object(String textureName, String droppedItemStringID, ToolType toolType, Color mapColor) {
        super(textureName, droppedItemStringID, toolType, mapColor);
    }

    @Override
    public MultiTile getMultiTile(int rotation) {
        return new MultiTile(0, 0, 1, 2, rotation, false, this.getID(), this.counterID);
    }

    @Override
    public Rectangle getCollision(Level level, int x, int y, int rotation) {
        if (rotation == 0) {
            return new Rectangle(x * 32 + 6, y * 32 + 6, 20, 26);
        }
        if (rotation == 1) {
            return new Rectangle(x * 32, y * 32 + 6, 26, 20);
        }
        if (rotation == 2) {
            return new Rectangle(x * 32 + 6, y * 32, 20, 26);
        }
        return new Rectangle(x * 32 + 6, y * 32 + 6, 26, 20);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        ObjectEntity ent;
        LevelObject master;
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        byte rotation = level.getObjectRotation(tileX, tileY);
        boolean treasureHunter = perspective != null && perspective.buffManager.getModifier(BuffModifiers.TREASURE_HUNTER) != false;
        ObjectDamagedTextureArray usedTexture = this.texture;
        if (this.openTexture != null && (master = (LevelObject)this.getMultiTile(rotation).getMasterLevelObject(level, 0, tileX, tileY).orElse(null)) != null && (ent = level.entityManager.getObjectEntity(master.tileX, master.tileY)) != null && ent.implementsOEUsers() && ((OEUsers)((Object)ent)).isInUse()) {
            usedTexture = this.openTexture;
        }
        GameTexture texture = usedTexture.getDamagedTexture(this, level, tileX, tileY);
        final SharedTextureDrawOptions draws = new SharedTextureDrawOptions(texture);
        if (rotation == 0) {
            draws.addSprite(0, 0, 32, texture.getHeight() - 32).spelunkerLight(light, treasureHunter, this.getID(), level).pos(drawX, drawY - texture.getHeight() + 64);
        } else if (rotation == 1) {
            draws.addSprite(2, 0, 32, texture.getHeight()).spelunkerLight(light, treasureHunter, this.getID(), level).pos(drawX, drawY - texture.getHeight() + 32);
        } else if (rotation == 2) {
            draws.addSprite(3, texture.getHeight() / 32 - 1, 32).spelunkerLight(light, treasureHunter, this.getID(), level).pos(drawX, drawY);
        } else {
            draws.addSprite(4, 0, 32, texture.getHeight()).spelunkerLight(light, treasureHunter, this.getID(), level).pos(drawX, drawY - texture.getHeight() + 32);
        }
        list.add(new LevelSortedDrawable(this, tileX, tileY){

            @Override
            public int getSortY() {
                return 16;
            }

            @Override
            public void draw(TickManager tickManager) {
                draws.draw();
            }
        });
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
    }

    @Override
    public LootTable getLootTable(Level level, int layerID, int tileX, int tileY) {
        return new LootTable();
    }

    @Override
    public void interact(Level level, int x, int y, PlayerMob player) {
        this.getMultiTile(level.getObjectRotation(x, y)).getMasterLevelObject(level, 0, x, y).ifPresent(e -> e.interact(player));
    }
}

