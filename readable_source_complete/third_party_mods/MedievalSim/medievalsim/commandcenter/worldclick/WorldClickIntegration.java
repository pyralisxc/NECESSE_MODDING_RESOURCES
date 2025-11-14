/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.GlobalData
 *  necesse.engine.input.InputEvent
 *  necesse.engine.network.client.Client
 *  necesse.engine.state.State
 *  necesse.gfx.camera.GameCamera
 *  necesse.level.maps.Level
 *  necesse.level.maps.hudManager.HudDrawElement
 */
package medievalsim.commandcenter.worldclick;

import medievalsim.commandcenter.worldclick.WorldClickHandler;
import medievalsim.commandcenter.worldclick.WorldClickOverlay;
import necesse.engine.GlobalData;
import necesse.engine.input.InputEvent;
import necesse.engine.network.client.Client;
import necesse.engine.state.State;
import necesse.gfx.camera.GameCamera;
import necesse.level.maps.Level;
import necesse.level.maps.hudManager.HudDrawElement;

public class WorldClickIntegration {
    private static WorldClickOverlay activeOverlay = null;
    private static Client activeClient = null;

    public static void startIntegration(Client client) {
        if (activeClient == client && activeOverlay != null) {
            return;
        }
        activeClient = client;
        activeOverlay = new WorldClickOverlay();
        Level level = client.getLevel();
        if (level != null) {
            level.hudManager.addElement((HudDrawElement)activeOverlay);
            System.out.println("[WorldClickIntegration] Overlay registered to HUD");
        }
    }

    public static void stopIntegration() {
        if (activeOverlay != null) {
            activeOverlay.remove();
            System.out.println("[WorldClickIntegration] Overlay removed from HUD");
        }
        activeOverlay = null;
        activeClient = null;
    }

    public static void updateHoverPosition() {
        WorldClickHandler handler = WorldClickHandler.getInstance();
        if (!handler.isActive()) {
            return;
        }
        State currentState = GlobalData.getCurrentState();
        if (currentState == null) {
            return;
        }
        GameCamera camera = currentState.getCamera();
        if (camera == null) {
            return;
        }
        int tileX = camera.getMouseLevelTilePosX();
        int tileY = camera.getMouseLevelTilePosY();
        handler.updateHoverCoordinates(tileX, tileY);
    }

    public static boolean handleClick(InputEvent event) {
        int tileY;
        WorldClickHandler handler = WorldClickHandler.getInstance();
        if (!handler.isActive()) {
            return false;
        }
        if (!event.state || event.getID() != -100) {
            return false;
        }
        State currentState = GlobalData.getCurrentState();
        if (currentState == null) {
            return false;
        }
        GameCamera camera = currentState.getCamera();
        if (camera == null) {
            return false;
        }
        int tileX = camera.getMouseLevelTilePosX(event);
        boolean consumed = handler.handleWorldClick(tileX, tileY = camera.getMouseLevelTilePosY(event));
        if (consumed && !handler.isActive()) {
            WorldClickIntegration.stopIntegration();
        }
        return consumed;
    }

    public static boolean isActive() {
        return activeOverlay != null && activeClient != null;
    }
}

