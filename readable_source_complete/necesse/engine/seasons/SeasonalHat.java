/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.seasons;

import java.util.function.Supplier;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;

public class SeasonalHat {
    public final Supplier<Boolean> isActive;
    public final float mobWearChance;
    public final String itemDropStringID;
    public final float itemDropChance;
    public String textureName;
    public GameTexture texture;

    public SeasonalHat(Supplier<Boolean> isActive, float mobWearChance, String itemDropStringID, float itemDropChance, String textureName) {
        this.isActive = isActive;
        this.mobWearChance = mobWearChance;
        this.itemDropStringID = itemDropStringID;
        this.itemDropChance = itemDropChance;
        this.textureName = textureName;
    }

    protected void loadTextures() {
        this.texture = GameTexture.fromFile("player/armor/" + this.textureName);
    }

    public HumanDrawOptions.HumanDrawOptionsGetter getDrawOptions() {
        return (player, dir, spriteX, spriteY, spriteRes, drawX, drawY, width, height, mirrorX, mirrorY, light, alpha, mask) -> this.texture.initDraw().sprite(spriteX, spriteY, spriteRes).light(light).alpha(alpha).size(width, height).mirror(mirrorX, mirrorY).addMaskShader(mask).pos(drawX, drawY);
    }

    public LootTable getLootTable(LootTable base) {
        if (this.itemDropStringID != null && this.itemDropChance > 0.0f) {
            return new LootTable(base, new ChanceLootItem(this.itemDropChance, this.itemDropStringID));
        }
        return base;
    }
}

