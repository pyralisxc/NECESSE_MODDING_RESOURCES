/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util.gameAreaSearch;

import java.awt.Point;
import necesse.engine.util.gameAreaSearch.GameAreaSearch;

public class GameAreaPointSearch
extends GameAreaSearch<Point> {
    public GameAreaPointSearch(int startX, int startY, int minX, int minY, int maxX, int maxY, int maxDistance) {
        super(startX, startY, minX, minY, maxX, maxY, maxDistance);
    }

    public GameAreaPointSearch(int startX, int startY, int maxDistance) {
        super(startX, startY, maxDistance);
    }

    @Override
    protected Point get(int x, int y) {
        return new Point(x, y);
    }
}

