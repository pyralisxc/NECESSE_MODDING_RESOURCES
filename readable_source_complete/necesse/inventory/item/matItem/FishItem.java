/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.matItem;

import necesse.entity.particle.CirclingFishParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.Item;
import necesse.inventory.item.matItem.FishItemInterface;
import necesse.inventory.item.matItem.MatItem;
import necesse.level.maps.Level;

public class FishItem
extends MatItem
implements FishItemInterface {
    public GameTexture circlingFishTexture;

    public FishItem(int stackSize, String ... globalIngredients) {
        super(stackSize, globalIngredients);
    }

    public FishItem(int stackSize, Item.Rarity rarity, String ... globalIngredients) {
        super(stackSize, rarity, globalIngredients);
    }

    public FishItem(int stackSize, Item.Rarity rarity, String tooltipKey) {
        super(stackSize, rarity, tooltipKey);
    }

    public FishItem(int stackSize, Item.Rarity rarity, String tooltipKey, String ... globalIngredients) {
        super(stackSize, rarity, tooltipKey, globalIngredients);
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.circlingFishTexture = GameTexture.fromFile("particles/circlingfish");
    }

    @Override
    public Particle getParticle(Level level, int x, int y, int lifeTime) {
        return new CirclingFishParticle(level, (float)x, (float)y, lifeTime, this.circlingFishTexture, 60);
    }
}

