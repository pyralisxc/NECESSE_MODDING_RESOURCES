/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.drawables;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.function.Consumer;
import java.util.function.Supplier;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.gfx.drawables.Drawable;

public class OrderableDrawables {
    private NavigableMap<Integer, List<Drawable>> map;
    private Supplier<List<Drawable>> listGenerator;

    public OrderableDrawables(NavigableMap<Integer, List<Drawable>> map, Supplier<List<Drawable>> listGenerator) {
        this.map = map;
        this.listGenerator = listGenerator;
    }

    public OrderableDrawables(NavigableMap<Integer, List<Drawable>> map) {
        this(map, ArrayList::new);
    }

    public void add(Drawable drawable) {
        this.add(0, drawable);
    }

    public void add(int drawOrder, Drawable drawable) {
        this.map.compute(drawOrder, (i, drawables) -> {
            if (drawables == null) {
                drawables = this.listGenerator.get();
            }
            drawables.add(drawable);
            return drawables;
        });
    }

    public void forEach(Consumer<? super Drawable> consumer) {
        this.map.forEach((? super K drawOrder, ? super V drawables) -> drawables.forEach(consumer));
    }

    public void draw(TickManager tickManager) {
        this.forEach(e -> e.draw(tickManager));
    }
}

