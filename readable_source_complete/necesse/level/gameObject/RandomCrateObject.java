/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import necesse.engine.seasons.GameSeasons;
import necesse.engine.seasons.SeasonCrate;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.inventory.lootTable.LootTable;
import necesse.level.gameObject.RandomBreakObject;
import necesse.level.maps.Level;

public class RandomCrateObject
extends RandomBreakObject {
    public RandomCrateObject(String texturePath) {
        super(new Rectangle(5, 12, 22, 12), texturePath, new Color(112, 89, 52));
        this.setItemCategory("objects", "misc");
    }

    @Override
    public LootTable getBreakLootTable(Level level, int tileX, int tileY) {
        LootTable out = level.getCrateLootTable();
        if (this.useEventTexture(new GameRandom(), tileX, tileY)) {
            out = new LootTable(out, out);
        }
        return out;
    }

    @Override
    public void playDamageSound(Level level, int x, int y, boolean damageDone) {
        SoundManager.playSound(GameRandom.globalRandom.getOneOf(GameResources.cratebreak1, GameResources.cratebreak2, GameResources.cratebreak3), (SoundEffect)SoundEffect.effect(x * 32 + 16, y * 32 + 16));
    }

    protected SeasonCrate getSeasonCrate(GameRandom random, int tileX, int tileY) {
        return GameSeasons.getCrate(random.seeded(RandomCrateObject.getTileSeed(tileX, tileY, 38813)));
    }

    public boolean useEventTexture(GameRandom random, int tileX, int tileY) {
        return this.getSeasonCrate(random, tileX, tileY) != null;
    }

    @Override
    public GameTextureSection getSprite(GameRandom random, int tileX, int tileY) {
        SeasonCrate seasonCrate = this.getSeasonCrate(random, tileX, tileY);
        GameTexture texture = seasonCrate != null ? seasonCrate.getTexture() : this.objectTexture;
        int sprites = texture.getWidth() / 32;
        int sprite = this.getSprite(random, tileX, tileY, sprites);
        return new GameTextureSection(texture).sprite(sprite, 0, 32, texture.getHeight());
    }

    @Override
    public GameTextureSection[] getDebrisSprites(GameRandom random, int tileX, int tileY) {
        SeasonCrate seasonCrate = this.getSeasonCrate(random, tileX, tileY);
        GameTexture texture = seasonCrate != null ? seasonCrate.getDebrisTexture() : this.debrisTexture;
        int count = texture.getWidth() / 32;
        GameTextureSection[] sprites = new GameTextureSection[count];
        for (int i = 0; i < count; ++i) {
            int startX = i * 32;
            sprites[i] = new GameTextureSection(texture, startX, startX + 32, 0, 32);
        }
        return sprites;
    }
}

