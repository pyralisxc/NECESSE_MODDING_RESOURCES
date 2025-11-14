/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.server;

import necesse.engine.network.server.ServerCreationSettings;
import necesse.engine.save.SaveData;

public abstract class ServerSettings {
    public String password = "";
    public int slots;
    public ServerCreationSettings creationSettings;

    protected ServerSettings(ServerCreationSettings creationSettings, int slots) {
        this.creationSettings = creationSettings;
        this.slots = slots;
    }

    public void addSaveData(SaveData save) {
        save.addInt("slots", this.slots);
        save.addSafeString("password", this.password);
    }

    public abstract boolean isSinglePlayer();
}

