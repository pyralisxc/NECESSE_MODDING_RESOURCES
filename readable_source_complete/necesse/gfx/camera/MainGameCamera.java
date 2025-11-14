/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.camera;

import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.client.Client;
import necesse.engine.state.MainGame;
import necesse.gfx.camera.GameCamera;

public abstract class MainGameCamera
extends GameCamera {
    public MainGameCamera(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public MainGameCamera(int x, int y) {
        super(x, y);
    }

    public MainGameCamera() {
    }

    public abstract void tickCamera(TickManager var1, MainGame var2, Client var3);
}

