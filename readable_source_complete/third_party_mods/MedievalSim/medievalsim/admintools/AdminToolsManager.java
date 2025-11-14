/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.Settings
 *  necesse.engine.commands.PermissionLevel
 *  necesse.engine.localization.message.GameMessage
 *  necesse.engine.localization.message.StaticMessage
 *  necesse.engine.network.client.Client
 *  necesse.gfx.forms.MainGameFormManager
 */
package medievalsim.admintools;

import medievalsim.ui.AdminToolsHudManager;
import necesse.engine.Settings;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.client.Client;
import necesse.gfx.forms.MainGameFormManager;

public class AdminToolsManager {
    public static void setupAdminButton(MainGameFormManager manager, Client client) {
        AdminToolsHudManager.init(client);
        manager.rightQuickbar.addButton("medievalsim_admintools", Settings.UI.quickbar_quests, e -> AdminToolsHudManager.toggleHud(), (GameMessage)new StaticMessage("Admin Tools"), () -> AdminToolsManager.hasAdminPermission(client));
    }

    public static boolean hasAdminPermission(Client client) {
        if (client == null) {
            return false;
        }
        PermissionLevel level = client.getPermissionLevel();
        return level != null && level.getLevel() >= PermissionLevel.ADMIN.getLevel();
    }
}

