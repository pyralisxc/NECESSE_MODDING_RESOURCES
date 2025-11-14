/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.BounceGlyphTrapEvent;
import necesse.level.gameObject.GlyphTrapObject;
import necesse.level.maps.Level;

public class BounceGlyphTrapObject
extends GlyphTrapObject {
    public BounceGlyphTrapObject() {
        super("glyphtrapbounce", BounceGlyphTrapEvent.particleHue);
    }

    @Override
    protected void addLevelEvent(Level level, int x, int y) {
        BounceGlyphTrapEvent event = new BounceGlyphTrapEvent(x, y, GameRandom.globalRandom);
        level.entityManager.events.add(event);
    }
}

