/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util;

import java.util.Collections;
import java.util.Set;
import necesse.engine.util.GameMath;
import necesse.engine.util.PointSetAbstract;

public class SingletonPointSet
extends PointSetAbstract<Set<Long>> {
    public SingletonPointSet(int x, int y) {
        super(Collections.singleton(GameMath.getUniqueLongKey(x, y)));
    }
}

