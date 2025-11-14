/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.gfx.GameResources;
import necesse.inventory.lootTable.LootTable;
import necesse.level.gameObject.RandomBreakObject;
import necesse.level.maps.Level;

public class RandomVaseObject
extends RandomBreakObject {
    public RandomVaseObject(String texturePath) {
        super(new Rectangle(5, 12, 22, 8), texturePath, new Color(80, 80, 80));
        this.hoverHitbox = new Rectangle(0, -24, 32, 56);
        this.setItemCategory("objects", "landscaping", "masonry");
        this.setCraftingCategory("objects", "landscaping", "masonry");
    }

    @Override
    public LootTable getBreakLootTable(Level level, int tileX, int tileY) {
        return level.getCrateLootTable();
    }

    @Override
    public void playDamageSound(Level level, int x, int y, boolean damageDone) {
        SoundManager.playSound(GameResources.shatter2, (SoundEffect)SoundEffect.effect(x * 32 + 16, y * 32 + 16).volume(0.8f).pitch(GameRandom.globalRandom.getFloatBetween(0.8f, 1.2f)));
    }
}

