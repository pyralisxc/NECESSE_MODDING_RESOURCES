/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import necesse.gfx.gameTexture.GameTexture;

public class GameSkinColor {
    public int weight;
    public ArrayList<Color> colors = new ArrayList();

    public GameSkinColor(int weight) {
        this.weight = weight;
    }

    public void applyColorToTexture(GameTexture texture, ArrayList<Color> toneColors, HashSet<Color> appliedColors) {
        for (int i = 0; i < this.colors.size(); ++i) {
            texture.replaceColor(toneColors.get(i), this.colors.get(i));
            if (appliedColors == null) continue;
            appliedColors.add(this.colors.get(i));
        }
    }

    public int hashCode() {
        return this.colors.hashCode();
    }
}

