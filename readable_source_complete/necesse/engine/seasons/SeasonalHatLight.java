/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.seasons;

import java.util.function.Supplier;
import necesse.engine.seasons.SeasonalHat;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.gameTexture.GameTexture;

public class SeasonalHatLight
extends SeasonalHat {
    public GameTexture lightTexture;

    public SeasonalHatLight(Supplier<Boolean> isActive, float mobWearChance, String itemDropStringID, float itemDropChance, String textureName) {
        super(isActive, mobWearChance, itemDropStringID, itemDropChance, textureName);
    }

    @Override
    protected void loadTextures() {
        super.loadTextures();
        this.lightTexture = GameTexture.fromFile("player/armor/" + this.textureName + "_light");
    }

    @Override
    public HumanDrawOptions.HumanDrawOptionsGetter getDrawOptions() {
        HumanDrawOptions.HumanDrawOptionsGetter superGetter = super.getDrawOptions();
        return (player, dir, spriteX, spriteY, spriteRes, drawX, drawY, width, height, mirrorX, mirrorY, light, alpha, mask) -> {
            DrawOptionsList options = new DrawOptionsList();
            options.add(superGetter.getDrawOptions(player, dir, spriteX, spriteY, spriteRes, drawX, drawY, width, height, mirrorX, mirrorY, light, alpha, mask));
            options.add(this.lightTexture.initDraw().sprite(spriteX, spriteY, spriteRes).light(light.minLevelCopy(150.0f)).alpha(alpha).size(width, height).mirror(mirrorX, mirrorY).addMaskShader(mask).pos(drawX, drawY));
            return options;
        };
    }
}

