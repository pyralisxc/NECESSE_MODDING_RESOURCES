/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.drawables;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.function.Supplier;
import necesse.engine.util.GameUtils;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;

public class SpecialSharedDrawables {
    private final ArrayList<Element> drawablesList = new ArrayList();
    private final HashMap<String, SharedTextureDrawOptions> drawablesMap = new HashMap();

    public synchronized SharedTextureDrawOptions getOrCreate(String stringID, int priority, Supplier<SharedTextureDrawOptions> drawableSupplier) {
        if (this.drawablesMap.containsKey(stringID)) {
            return this.drawablesMap.get(stringID);
        }
        SharedTextureDrawOptions drawable = drawableSupplier.get();
        Element element = new Element(stringID, priority, drawable);
        this.drawablesList.add(element);
        this.drawablesMap.put(stringID, drawable);
        GameUtils.insertSortedList(this.drawablesList, element, Comparator.comparingInt(e -> e.priority));
        return drawable;
    }

    public void draw(int maxDrawPerCall) {
        for (Element drawable : this.drawablesList) {
            drawable.drawable.draw(maxDrawPerCall);
        }
    }

    protected static class Element {
        public final String stringID;
        public final int priority;
        public final SharedTextureDrawOptions drawable;

        public Element(String stringID, int priority, SharedTextureDrawOptions drawable) {
            this.stringID = stringID;
            this.priority = priority;
            this.drawable = drawable;
        }
    }
}

