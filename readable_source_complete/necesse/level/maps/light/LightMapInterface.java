/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.light;

import java.awt.Point;
import java.util.List;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.light.LightManager;
import necesse.level.maps.light.SourcedGameLight;

public interface LightMapInterface {
    public void resetLights(LightManager var1);

    public GameLight getLight(int var1, int var2);

    public List<SourcedGameLight> getLightSources(int var1, int var2);

    public void update(int var1, int var2, boolean var3);

    public void update(int var1, int var2, int var3, int var4, boolean var5);

    public void update(Iterable<Point> var1, boolean var2);
}

