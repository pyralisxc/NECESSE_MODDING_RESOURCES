/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.network.Packet
 *  necesse.engine.network.client.Client
 *  necesse.engine.window.GameWindow
 *  necesse.gfx.forms.ContainerComponent
 *  necesse.gfx.forms.MainGameFormManager
 *  necesse.gfx.forms.components.FormComponent
 */
package medievalsim.ui;

import medievalsim.admintools.AdminToolsManager;
import medievalsim.packets.PacketRequestZoneSync;
import medievalsim.ui.AdminToolsHudForm;
import medievalsim.util.ModLogger;
import necesse.engine.network.Packet;
import necesse.engine.network.client.Client;
import necesse.engine.window.GameWindow;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.MainGameFormManager;
import necesse.gfx.forms.components.FormComponent;

public class AdminToolsHudManager {
    private static AdminToolsHudForm hudForm;
    private static Client client;

    public static void init(Client clientInstance) {
        client = clientInstance;
    }

    public static void showHud() {
        boolean isStale;
        if (client == null) {
            ModLogger.error("AdminToolsHudManager not initialized!");
            return;
        }
        if (!AdminToolsManager.hasAdminPermission(client)) {
            AdminToolsHudManager.client.chat.addMessage("\u00a7cAccess Denied: Admin permission required");
            return;
        }
        MainGameFormManager formManager = ContainerComponent.getFormManager();
        if (formManager == null) {
            ModLogger.error("Could not get MainGameFormManager!");
            return;
        }
        boolean bl = isStale = hudForm != null && !formManager.getComponents().contains((Object)hudForm);
        if (hudForm == null || isStale) {
            if (isStale) {
                hudForm = null;
            }
            hudForm = new AdminToolsHudForm(client);
            formManager.addComponent((FormComponent)hudForm);
            hudForm.setVisible(true);
            if (client.getLevel() != null) {
                AdminToolsHudManager.client.network.sendPacket((Packet)new PacketRequestZoneSync());
            }
        } else {
            hudForm.setVisible(true);
        }
    }

    public static void hideHud() {
        if (hudForm != null) {
            hudForm.setVisible(false);
        }
    }

    public static void toggleHud() {
        boolean isStale;
        if (client != null && !AdminToolsManager.hasAdminPermission(client)) {
            AdminToolsHudManager.client.chat.addMessage("\u00a7cAccess Denied: Admin permission required");
            return;
        }
        MainGameFormManager formManager = ContainerComponent.getFormManager();
        if (formManager == null) {
            ModLogger.error("Could not get MainGameFormManager!");
            return;
        }
        boolean bl = isStale = hudForm != null && !formManager.getComponents().contains((Object)hudForm);
        if (hudForm == null || isStale) {
            AdminToolsHudManager.showHud();
        } else if (hudForm.isVisible()) {
            AdminToolsHudManager.hideHud();
        } else {
            AdminToolsHudManager.showHud();
        }
    }

    public static boolean isHudVisible() {
        return hudForm != null && hudForm.isVisible();
    }

    public static void removeHud() {
        if (hudForm != null) {
            MainGameFormManager formManager = ContainerComponent.getFormManager();
            if (formManager != null) {
                formManager.removeComponent((FormComponent)hudForm);
            }
            hudForm = null;
        }
    }

    public static void onWindowResized(GameWindow window) {
        if (hudForm != null) {
            hudForm.onWindowResized(window);
        }
    }

    public static AdminToolsHudForm getHudForm() {
        return hudForm;
    }
}

