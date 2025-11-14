/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.screenHudManager;

import java.util.ArrayList;
import necesse.engine.screenHudManager.ScreenFloatTextFade;
import necesse.engine.screenHudManager.ScreenHudElement;
import necesse.gfx.gameFont.FontOptions;

public class UniqueScreenFloatText
extends ScreenFloatTextFade {
    public final String uniqueType;

    public UniqueScreenFloatText(int x, int y, String text, FontOptions options, String uniqueType) {
        super(x, y, text, options);
        this.avoidOtherText = false;
        this.uniqueType = uniqueType;
    }

    public UniqueScreenFloatText(int x, int y, String text, FontOptions options) {
        this(x, y, text, options, null);
    }

    @Override
    public void addThis(ArrayList<ScreenHudElement> elements) {
        if (this.uniqueType != null) {
            for (int i = 0; i < elements.size(); ++i) {
                String otherType;
                ScreenHudElement oText = elements.get(i);
                if (oText.isRemoved() || oText == this || !(oText instanceof UniqueScreenFloatText) || (otherType = ((UniqueScreenFloatText)oText).uniqueType) == null || !otherType.equals(this.uniqueType)) continue;
                elements.remove(i);
                --i;
            }
        }
        super.addThis(elements);
    }
}

