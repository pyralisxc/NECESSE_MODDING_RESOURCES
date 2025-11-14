/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.engine.localization.Localization
 *  necesse.engine.util.GameBlackboard
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.objectEntity.ObjectEntity
 *  necesse.gfx.camera.GameCamera
 *  necesse.gfx.drawOptions.texture.TextureDrawOptions
 *  necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd
 *  necesse.gfx.drawables.LevelSortedDrawable
 *  necesse.gfx.drawables.OrderableDrawables
 *  necesse.gfx.gameTexture.GameTexture
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.Item
 *  necesse.inventory.item.Item$Rarity
 *  necesse.inventory.item.placeableItem.objectItem.ObjectItem
 *  necesse.inventory.item.toolItem.ToolType
 *  necesse.level.gameObject.GameObject
 *  necesse.level.maps.Level
 *  necesse.level.maps.light.GameLight
 */
package aphorea.objects;

import aphorea.utils.AphColors;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.placeableItem.objectItem.ObjectItem;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class GoldWitchStatue
extends GameObject {
    private GameTexture texture;

    public GoldWitchStatue() {
        super(new Rectangle(0, 4, 32, 24));
        this.hoverHitbox = new Rectangle(0, -32, 32, 64);
        this.toolType = ToolType.PICKAXE;
        this.isLightTransparent = false;
        this.mapColor = AphColors.gold;
    }

    public void loadTextures() {
        super.loadTextures();
        this.texture = GameTexture.fromFile((String)"objects/goldwitchstatue");
    }

    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        TextureDrawOptionsEnd options = this.texture.initDraw().light(light).pos(drawX - 16, drawY - this.texture.getHeight() + 32);
        list.add(new LevelSortedDrawable(this, tileX, tileY, (TextureDrawOptions)options){
            final /* synthetic */ TextureDrawOptions val$options;
            {
                this.val$options = textureDrawOptions;
                super(arg0, arg1, arg2);
            }

            public int getSortY() {
                return 6;
            }

            public void draw(TickManager tickManager) {
                this.val$options.draw();
            }
        });
    }

    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        this.texture.initDraw().alpha(alpha).draw(drawX, drawY - this.texture.getHeight() + 32);
    }

    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new WitchStatueEntity(level, x, y);
    }

    public Item generateNewObjectItem() {
        return new WitchStatueItem(this);
    }

    public static class WitchStatueEntity
    extends ObjectEntity {
        public WitchStatueEntity(Level level, int tileX, int tileY) {
            super(level, "witchstatue", tileX, tileY);
        }
    }

    public static class WitchStatueItem
    extends ObjectItem {
        public WitchStatueItem(GameObject gameObject) {
            super(gameObject);
            this.rarity = Item.Rarity.RARE;
        }

        public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
            ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
            tooltips.add(Localization.translate((String)"itemtooltip", (String)"goldwitchstatue"));
            tooltips.add(Localization.translate((String)"global", (String)"aphorea"));
            return tooltips;
        }
    }
}

