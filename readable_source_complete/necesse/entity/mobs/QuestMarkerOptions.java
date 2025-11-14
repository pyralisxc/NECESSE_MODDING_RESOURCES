/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs;

import java.awt.Color;
import necesse.entity.mobs.QuestGiver;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.level.maps.light.GameLight;

public class QuestMarkerOptions {
    public static Color orangeColor = new Color(200, 200, 50);
    public static Color grayColor = new Color(50, 50, 50);
    public final String icons;
    public final Color color;

    public QuestMarkerOptions(String icons, Color color) {
        this.icons = icons;
        this.color = color;
    }

    public QuestMarkerOptions(char icon, Color color) {
        this(Character.toString(icon), color);
    }

    public QuestMarkerOptions combine(QuestMarkerOptions other) {
        float otherLum;
        StringBuilder builder = new StringBuilder().append(this.icons);
        for (int i = 0; i < other.icons.length(); ++i) {
            char c = other.icons.charAt(i);
            if (builder.indexOf(String.valueOf(c)) != -1) continue;
            builder.append(c);
        }
        float thisLum = (float)this.color.getRed() * 0.2126f + (float)this.color.getGreen() * 0.7152f + (float)this.color.getBlue() * 0.0722f;
        Color newColor = thisLum > (otherLum = (float)other.color.getRed() * 0.2126f + (float)other.color.getGreen() * 0.7152f + (float)other.color.getBlue() * 0.0722f) ? this.color : other.color;
        return new QuestMarkerOptions(builder.toString(), newColor);
    }

    public static QuestMarkerOptions combine(QuestMarkerOptions first, QuestMarkerOptions second) {
        if (first == null) {
            return second;
        }
        if (second == null) {
            return first;
        }
        return first.combine(second);
    }

    public DrawOptions getDrawOptions(int x, int y, GameLight light, GameCamera camera, int xOffset, int yOffset) {
        return QuestGiver.getMarkerDrawOptions(this.icons, this.color, x, y, light, camera, xOffset, yOffset);
    }
}

