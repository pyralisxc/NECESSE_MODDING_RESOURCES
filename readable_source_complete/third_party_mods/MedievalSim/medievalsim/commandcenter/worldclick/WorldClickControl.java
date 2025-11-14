/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.GlobalData
 *  necesse.engine.input.Control
 *  necesse.engine.input.InputEvent
 *  necesse.engine.localization.message.GameMessage
 *  necesse.engine.localization.message.StaticMessage
 *  necesse.engine.state.State
 *  necesse.gfx.camera.GameCamera
 */
package medievalsim.commandcenter.worldclick;

import medievalsim.commandcenter.worldclick.WorldClickHandler;
import medievalsim.commandcenter.worldclick.WorldClickIntegration;
import necesse.engine.GlobalData;
import necesse.engine.input.Control;
import necesse.engine.input.InputEvent;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.state.State;
import necesse.gfx.camera.GameCamera;

public class WorldClickControl
extends Control {
    public WorldClickControl() {
        super(-100, "medievalsim_worldclick", (GameMessage)new StaticMessage("World Click Selection"));
    }

    public void activate(InputEvent event) {
        int tileY;
        super.activate(event);
        WorldClickHandler handler = WorldClickHandler.getInstance();
        if (!handler.isActive() || !this.isPressed()) {
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
        int tileX = camera.getMouseLevelTilePosX(event);
        boolean consumed = handler.handleWorldClick(tileX, tileY = camera.getMouseLevelTilePosY(event));
        if (consumed) {
            event.use();
            if (!handler.isActive()) {
                WorldClickIntegration.stopIntegration();
            }
        }
    }
}

