/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import necesse.engine.util.GameMath;
import necesse.gfx.GameSkinColor;
import necesse.gfx.gameTexture.GameTexture;

public class GameSkinColors {
    private final ArrayList<GameSkinColor> colors = new ArrayList();
    private final ArrayList<Color> toneColors = new ArrayList();

    public GameSkinColor getSkinColor(int index) {
        return this.colors.get(index);
    }

    public void addBaseColors(GameTexture texture, int weightX, int startX, int endX) {
        for (int x = startX; x <= endX; ++x) {
            this.toneColors.add(texture.getColor(x, 0));
        }
        for (int y = 1; y < texture.getHeight(); ++y) {
            int weight = 0;
            if (weightX != -1) {
                Color weightColor = texture.getColor(weightX, y);
                weight = (GameMath.max(weightColor.getRed(), weightColor.getGreen(), weightColor.getBlue()) + GameMath.min(weightColor.getRed(), weightColor.getGreen(), weightColor.getBlue())) / 2;
            }
            GameSkinColor skinColor = new GameSkinColor(weight);
            this.colors.add(skinColor);
            for (int x = startX; x <= endX; ++x) {
                skinColor.colors.add(texture.getColor(x, y));
            }
        }
    }

    public int getSize() {
        return this.colors.size();
    }

    public int getWeight(int index) {
        return this.colors.get((int)index).weight;
    }

    public int getColorHash(int index) {
        return this.colors.get(index).hashCode();
    }

    public int getTonesHash() {
        return this.toneColors.hashCode();
    }

    public void replaceColors(GameTexture texture, int index, HashSet<Color> appliedColors) {
        this.applyColors(texture, index, appliedColors);
    }

    public void replaceColors(GameTexture texture, int index) {
        this.replaceColors(texture, index, null);
    }

    public void removeColors(GameTexture texture, HashSet<Color> excludes, GameSkinColors ... colors) {
        for (GameSkinColors color : colors) {
            this.removeToneColors(texture, color.toneColors, excludes);
        }
    }

    private void applyColors(GameTexture texture, int index, HashSet<Color> appliedColors) {
        this.colors.get(index).applyColorToTexture(texture, this.toneColors, appliedColors);
    }

    private void removeToneColors(GameTexture texture, ArrayList<Color> toneColors, HashSet<Color> excludes) {
        for (Color toneColor : toneColors) {
            if (excludes != null && excludes.contains(toneColor)) continue;
            texture.replaceColor(toneColor, new Color(255, 255, 255, 0));
        }
    }
}

