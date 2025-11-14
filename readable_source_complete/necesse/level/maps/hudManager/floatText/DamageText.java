/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.hudManager.floatText;

import java.awt.Color;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.gfx.gameFont.FontOptions;
import necesse.level.maps.hudManager.floatText.FloatTextFade;

public class DamageText
extends FloatTextFade {
    public DamageText(int x, int y, int damage, FontOptions fontOptions, int heightIncrease) {
        super(x + (int)(GameRandom.globalRandom.nextGaussian() * 8.0), y + (int)(GameRandom.globalRandom.nextGaussian() * 8.0), String.valueOf(damage), fontOptions);
        this.heightIncrease = heightIncrease;
    }

    public DamageText(int x, int y, int damage, Color color, int heightIncrease) {
        this(x, y, damage, new FontOptions(16).outline().color(color), heightIncrease);
    }

    public DamageText(Mob mob, int damage, FontOptions fontOptions, int heightIncrease) {
        this(mob.getX(), mob.getY() - 16, damage, fontOptions, heightIncrease);
    }

    public DamageText(Mob mob, int damage, Color color, int heightIncrease) {
        this(mob, damage, new FontOptions(16).outline().color(color), heightIncrease);
    }
}

