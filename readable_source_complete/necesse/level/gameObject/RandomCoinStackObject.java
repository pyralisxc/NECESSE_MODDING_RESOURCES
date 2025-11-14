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
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.gameObject.RandomBreakObject;
import necesse.level.maps.Level;

public class RandomCoinStackObject
extends RandomBreakObject {
    public RandomCoinStackObject() {
        super(new Rectangle(), "coinstacks", new Color(205, 180, 70), false);
        this.countAsCratesBroken = false;
    }

    @Override
    public LootTable getBreakLootTable(Level level, int tileX, int tileY) {
        int coinStack = this.getSprite(new GameRandom(), tileX, tileY, 4);
        int coinAmount = 40 + coinStack * 30;
        return new LootTable(LootItem.between("coin", coinAmount, coinAmount + 29));
    }

    @Override
    public void playDamageSound(Level level, int x, int y, boolean damageDone) {
        SoundManager.playSound(GameResources.coins, (SoundEffect)SoundEffect.effect(x * 32 + 16, y * 32 + 16));
    }
}

