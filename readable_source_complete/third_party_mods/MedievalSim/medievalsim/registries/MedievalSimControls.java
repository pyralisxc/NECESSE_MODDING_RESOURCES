/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.GlobalData
 *  necesse.engine.input.Control
 *  necesse.engine.input.InputEvent
 *  necesse.engine.localization.message.GameMessage
 *  necesse.engine.localization.message.StaticMessage
 *  necesse.engine.network.client.Client
 *  necesse.engine.state.MainGame
 *  necesse.entity.mobs.PlayerMob
 */
package medievalsim.registries;

import medievalsim.admintools.AdminToolsManager;
import medievalsim.buildmode.BuildModeManager;
import medievalsim.commandcenter.worldclick.WorldClickControl;
import necesse.engine.GlobalData;
import necesse.engine.input.Control;
import necesse.engine.input.InputEvent;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.client.Client;
import necesse.engine.state.MainGame;
import necesse.entity.mobs.PlayerMob;

public class MedievalSimControls {
    public static Control TOGGLE_BUILD_MODE;
    public static Control WORLD_CLICK_SELECTION;

    public static void registerCore() {
        TOGGLE_BUILD_MODE = Control.addModControl((Control)new Control(80, "medievalsim_togglebuildmode", (GameMessage)new StaticMessage("Toggle Build Mode")){

            public void activate(InputEvent event) {
                MainGame mainGame;
                Client client;
                super.activate(event);
                if (this.isPressed() && GlobalData.getCurrentState() instanceof MainGame && (client = (mainGame = (MainGame)GlobalData.getCurrentState()).getClient()) != null) {
                    if (!AdminToolsManager.hasAdminPermission(client)) {
                        client.chat.addMessage("\u00a7cAccess Denied: Admin permission required");
                        return;
                    }
                    PlayerMob player = client.getPlayer();
                    if (player != null) {
                        BuildModeManager manager = BuildModeManager.getInstance(client);
                        boolean newState = !manager.buildModeEnabled;
                        manager.setBuildModeEnabled(newState);
                        String message = newState ? "Build Mode: ON" : "Build Mode: OFF";
                        client.chat.addMessage(message);
                    }
                }
            }
        });
        WORLD_CLICK_SELECTION = Control.addModControl((Control)new WorldClickControl());
    }
}

