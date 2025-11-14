/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.loading;

import necesse.engine.platforms.Platform;

public abstract class Loader {
    public abstract boolean loadGame(String[] var1, Platform var2) throws Exception;

    public abstract void unloadGame();

    public abstract void startGame();
}

