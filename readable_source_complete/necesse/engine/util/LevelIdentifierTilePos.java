/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util;

import java.awt.Point;
import java.util.Objects;
import necesse.engine.util.LevelIdentifier;

public class LevelIdentifierTilePos {
    public final LevelIdentifier identifier;
    public final Point tile;

    public LevelIdentifierTilePos(LevelIdentifier identifier, Point tile) {
        Objects.requireNonNull(identifier);
        this.identifier = identifier;
        this.tile = tile;
    }
}

