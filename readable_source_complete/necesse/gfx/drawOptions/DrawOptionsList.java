/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.drawOptions;

import java.util.ArrayList;
import java.util.List;
import necesse.gfx.drawOptions.DrawOptions;

public class DrawOptionsList
extends ArrayList<DrawOptions>
implements DrawOptions {
    public DrawOptionsList(List<DrawOptions> options) {
        super(options);
    }

    public DrawOptionsList(int initialCapacity) {
        super(initialCapacity);
    }

    public DrawOptionsList() {
    }

    @Override
    public void draw() {
        this.forEach(DrawOptions::draw);
    }
}

