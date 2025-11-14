/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.miscItem;

import java.awt.Color;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.MergeFunction;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.matItem.MatItem;

public class AscendedShardItem
extends MatItem {
    public GameTexture[] shineFrames;

    public AscendedShardItem() {
        super(100, Item.Rarity.UNIQUE, "ascendedshardtip");
        this.tooltipMaxLength = 350;
    }

    @Override
    protected void loadItemTextures() {
        super.loadItemTextures();
        this.shineFrames = AscendedShardItem.addShine(this.itemTexture);
    }

    @Override
    public GameSprite getItemSprite(InventoryItem item, PlayerMob perspective) {
        long time = perspective == null ? System.currentTimeMillis() : perspective.getLocalTime();
        int frame = GameUtils.getAnim(time, this.shineFrames.length * 2, this.shineFrames.length * 250);
        if (frame >= this.shineFrames.length) {
            return new GameSprite(this.itemTexture);
        }
        return new GameSprite(this.shineFrames[frame]);
    }

    public static GameTexture[] addShine(GameTexture iconTexture) {
        GameTexture shineTexture = GameTexture.fromFile("items/itemshine");
        int tileWidth = shineTexture.getWidth() / 32;
        GameTexture[] frames = new GameTexture[tileWidth];
        for (int i = 0; i < frames.length; ++i) {
            frames[i] = new GameTexture(iconTexture.debugName + "-shine-" + i, 32, shineTexture.getHeight());
            frames[i].copy(iconTexture, 0, 0);
            frames[i].merge(shineTexture, 0, 0, i * 32, 0, 32, shineTexture.getHeight(), new MergeFunction(){

                @Override
                public Color merge(Color currentColor, Color mergeColor) {
                    if (currentColor.getAlpha() == 0) {
                        return currentColor;
                    }
                    if (mergeColor.getAlpha() == 0) {
                        return currentColor;
                    }
                    mergeColor = new Color(mergeColor.getRed(), mergeColor.getGreen(), mergeColor.getBlue(), (int)((float)mergeColor.getAlpha() / 1.2f));
                    Color merge = MergeFunction.NORMAL.merge(currentColor, mergeColor);
                    return new Color(merge.getRed(), merge.getGreen(), merge.getBlue(), currentColor.getAlpha());
                }
            });
        }
        return frames;
    }
}

