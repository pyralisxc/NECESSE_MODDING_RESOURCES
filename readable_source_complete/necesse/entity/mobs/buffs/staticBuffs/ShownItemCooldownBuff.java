/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs;

import necesse.entity.mobs.buffs.staticBuffs.ShownCooldownBuff;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.MergeFunction;

public class ShownItemCooldownBuff
extends ShownCooldownBuff {
    String texturePath;

    public ShownItemCooldownBuff(int maxStacks, boolean showsFirstStackDurationText, String texturePath) {
        super(maxStacks, showsFirstStackDurationText);
        this.texturePath = texturePath;
    }

    @Override
    public void loadTextures() {
        GameTexture item;
        super.loadTextures();
        GameTexture mask = GameTexture.fromFile("buffs/mask", true);
        GameTexture base = GameTexture.fromFile("buffs/negative", true);
        GameTexture rawItemTexture = GameTexture.fromFile(this.texturePath, true);
        if (rawItemTexture.getWidth() != rawItemTexture.getHeight()) {
            int resolution = Math.max(rawItemTexture.getWidth(), rawItemTexture.getHeight());
            item = new GameTexture("buffs/cooldown " + this.getStringID(), resolution, resolution);
            int applyX = (resolution - rawItemTexture.getWidth()) / 2;
            int applyY = (resolution - rawItemTexture.getHeight()) / 2;
            item.copy(rawItemTexture, applyX, applyY);
            item = item.resize(base.getWidth() - 6, base.getHeight() - 6);
        } else {
            item = rawItemTexture.resize(base.getWidth() - 6, base.getHeight() - 6);
        }
        this.iconTexture = new GameTexture(base);
        int maskXOffset = (item.getWidth() - mask.getWidth()) / 2;
        int maskYOffset = (item.getHeight() - mask.getHeight()) / 2;
        item.merge(mask, maskXOffset, maskYOffset, MergeFunction.GLBLEND);
        int iconXOffset = (this.iconTexture.getWidth() - item.getWidth()) / 2;
        int iconYOffset = (this.iconTexture.getHeight() - item.getHeight()) / 2;
        this.iconTexture.merge(item, iconXOffset, iconYOffset, MergeFunction.NORMAL);
        this.iconTexture.makeFinal();
    }
}

