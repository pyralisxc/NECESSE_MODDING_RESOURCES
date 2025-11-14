/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.platforms.steam.forms;

import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.platforms.Platform;
import necesse.engine.platforms.steam.forms.FormSteamFriendsList;
import necesse.engine.platforms.steam.network.SteamNetworkManager;
import necesse.engine.state.MainMenu;
import necesse.gfx.forms.floatMenu.FloatMenu;
import necesse.gfx.forms.floatMenu.SelectionFloatMenu;
import necesse.gfx.gameFont.FontOptions;

public class FormSteamFriendsJoinList
extends FormSteamFriendsList {
    protected MainMenu mainMenu;

    public FormSteamFriendsJoinList(MainMenu mainMenu, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.mainMenu = mainMenu;
    }

    @Override
    protected void onFriendClicked(FormSteamFriendsList.FriendElement from) {
        this.playTickSound();
        SelectionFloatMenu menu = new SelectionFloatMenu(this);
        menu.add(Localization.translate("ui", "join", "name", from.name), () -> {
            this.mainMenu.startConnection(((SteamNetworkManager)Platform.getNetworkManager()).startJoinFriendClient(from.name, from.steamID), MainMenu.ConnectFrom.MultiplayerJoinFriend);
            menu.remove();
        });
        this.getManager().openFloatMenu((FloatMenu)menu, -2, 0);
    }

    @Override
    public boolean shouldShow(FormSteamFriendsList.FriendElement friend) {
        return friend.inGame && friend.gameInfo.getGameID() == 1169040L;
    }

    @Override
    public GameMessage getEmptyMessage() {
        return new LocalMessage("ui", "nofriendsingame");
    }

    @Override
    public FontOptions getEmptyMessageFontOptions() {
        return new FontOptions(16);
    }
}

