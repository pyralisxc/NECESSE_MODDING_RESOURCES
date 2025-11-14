/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.network.client.Client
 */
package medievalsim.commandcenter.worldclick;

import medievalsim.commandcenter.worldclick.WorldClickHandler;
import medievalsim.commandcenter.worldclick.WorldClickIntegration;
import necesse.engine.network.client.Client;

public class WorldClickTickManager {
    private static WorldClickTickManager instance;
    private Client client;

    private WorldClickTickManager() {
    }

    public static WorldClickTickManager getInstance() {
        if (instance == null) {
            instance = new WorldClickTickManager();
        }
        return instance;
    }

    public void init(Client client) {
        this.client = client;
        System.out.println("[WorldClickTickManager] Initialized with client");
    }

    public void tick() {
        if (this.client == null) {
            return;
        }
        WorldClickHandler handler = WorldClickHandler.getInstance();
        if (handler.isActive()) {
            WorldClickIntegration.updateHoverPosition();
        }
    }
}

