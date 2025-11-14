/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.server;

import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.loading.ServerLoader;
import necesse.engine.network.server.Server;

public abstract class ServerWindow {
    public abstract void init(ServerLoader var1);

    public abstract void showExit();

    public abstract void clearConsole();

    public abstract void setServer(Server var1);

    public abstract void updateGUI(TickManager var1);

    public abstract void dispose();
}

