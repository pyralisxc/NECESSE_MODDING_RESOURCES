/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.server;

import necesse.engine.gameLoop.ServerGameLoop;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerHostSettings;

public class ServerTickThread
extends Thread {
    public Server server;
    public ServerGameLoop gameLoop;
    private boolean shouldInitWorld;
    private ServerHostSettings hostSettings;
    private boolean isRunning;

    public ServerTickThread(Server server, String name, int maxFPS) {
        super(name);
        this.server = server;
        this.gameLoop = new ServerGameLoop(server, name, maxFPS);
        server.setTickManager(this.gameLoop);
    }

    @Override
    public synchronized void start() {
        this.gameLoop.setMaxFPS(60);
        this.gameLoop.init();
        super.start();
    }

    @Override
    public void run() {
        if (this.shouldInitWorld) {
            try {
                this.server.world.init();
                this.server.markWorldInitialized(null, this.hostSettings);
            }
            catch (Exception e) {
                this.server.markWorldInitialized(e, this.hostSettings);
                return;
            }
            this.hostSettings = null;
            this.shouldInitWorld = false;
        }
        this.isRunning = true;
        this.gameLoop.runMainGameLoop();
        this.isRunning = false;
    }

    public void makeInitWorld(ServerHostSettings hostSettings) {
        this.shouldInitWorld = true;
        this.hostSettings = hostSettings;
    }

    public boolean isRunning() {
        return this.isRunning;
    }
}

