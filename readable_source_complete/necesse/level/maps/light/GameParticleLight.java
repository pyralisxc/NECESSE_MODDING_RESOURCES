/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.light;

import necesse.level.maps.light.GameLight;

public class GameParticleLight {
    public GameLight light;
    public long endTime;

    public GameParticleLight(GameLight light) {
        this.light = light;
    }

    public void updateLevel(long currentTime) {
        if (this.endTime == 0L) {
            this.light.setLevel(0.0f);
            return;
        }
        long deltaTime = this.endTime - currentTime;
        if (deltaTime < 0L) {
            this.light.setLevel(0.0f);
            return;
        }
        float progress = (float)deltaTime / 750.0f;
        this.light.setLevel((int)(255.0f * progress));
    }
}

