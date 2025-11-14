/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.network.client.Client
 */
package medievalsim.commandcenter.worldclick;

import java.util.function.BiConsumer;
import necesse.engine.network.client.Client;

public class WorldClickHandler {
    private static WorldClickHandler instance;
    private boolean isActive = false;
    private BiConsumer<Integer, Integer> coordinateCallback;
    private int lastHoverTileX = -1;
    private int lastHoverTileY = -1;

    private WorldClickHandler() {
    }

    public static WorldClickHandler getInstance() {
        if (instance == null) {
            instance = new WorldClickHandler();
        }
        return instance;
    }

    public void startSelection(Client client, BiConsumer<Integer, Integer> callback) {
        this.coordinateCallback = callback;
        this.isActive = true;
        System.out.println("[WorldClickHandler] Started coordinate selection mode");
        if (client != null) {
            client.chat.addMessage("\u00a7eClick on the world to select coordinates. Press ESC to cancel.");
        }
    }

    public void stopSelection() {
        this.isActive = false;
        this.coordinateCallback = null;
        this.lastHoverTileX = -1;
        this.lastHoverTileY = -1;
        System.out.println("[WorldClickHandler] Stopped coordinate selection mode");
    }

    public boolean isActive() {
        return this.isActive;
    }

    public boolean handleWorldClick(int tileX, int tileY) {
        if (!this.isActive) {
            return false;
        }
        System.out.println("[WorldClickHandler] World clicked at: " + tileX + ", " + tileY);
        if (this.coordinateCallback != null) {
            this.coordinateCallback.accept(tileX, tileY);
        }
        this.stopSelection();
        return true;
    }

    public void updateHoverCoordinates(int tileX, int tileY) {
        if (!this.isActive) {
            this.lastHoverTileX = -1;
            this.lastHoverTileY = -1;
            return;
        }
        this.lastHoverTileX = tileX;
        this.lastHoverTileY = tileY;
    }

    public int getHoverTileX() {
        return this.lastHoverTileX;
    }

    public int getHoverTileY() {
        return this.lastHoverTileY;
    }

    public String getHoverDisplayString() {
        if (!this.isActive || this.lastHoverTileX < 0 || this.lastHoverTileY < 0) {
            return null;
        }
        return "Click to select: (" + this.lastHoverTileX + ", " + this.lastHoverTileY + ")";
    }
}

