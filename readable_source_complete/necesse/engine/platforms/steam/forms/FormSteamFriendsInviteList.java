/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.platforms.steam.forms;

import necesse.engine.localization.Localization;
import necesse.engine.platforms.steam.forms.FormSteamFriendsList;
import necesse.engine.platforms.steam.network.client.SteamClient;
import necesse.gfx.forms.floatMenu.FloatMenu;
import necesse.gfx.forms.floatMenu.SelectionFloatMenu;

public class FormSteamFriendsInviteList
extends FormSteamFriendsList {
    protected SteamClient client;

    public FormSteamFriendsInviteList(SteamClient client, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.client = client;
    }

    @Override
    protected void onFriendClicked(FormSteamFriendsList.FriendElement from) {
        this.playTickSound();
        SelectionFloatMenu menu = new SelectionFloatMenu(this);
        menu.add(Localization.translate("ui", "invite", "name", from.name), () -> {
            if (this.client.inviteToSteamLobby(from.steamID)) {
                this.client.chat.addMessage(Localization.translate("ui", "invited", "name", from.name));
            } else {
                this.client.chat.addMessage(Localization.translate("ui", "couldnotinvite", "name", from.name));
            }
            menu.remove();
        });
        this.getManager().openFloatMenu((FloatMenu)menu, -2, 0);
    }
}

