/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.codedisaster.steamworks.SteamFriends$PersonaChange
 *  com.codedisaster.steamworks.SteamFriendsCallback
 *  com.codedisaster.steamworks.SteamID
 *  com.codedisaster.steamworks.SteamResult
 */
package necesse.engine.platforms.steam.debug;

import com.codedisaster.steamworks.SteamFriends;
import com.codedisaster.steamworks.SteamFriendsCallback;
import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamResult;
import necesse.engine.platforms.steam.debug.DebugSteamCallback;

public class DebugSteamFriendsCallback
extends DebugSteamCallback
implements SteamFriendsCallback {
    public void onSetPersonaNameResponse(boolean success, boolean localSuccess, SteamResult result) {
        this.print("onSetPersonaNameResponse", success, localSuccess, result);
    }

    public void onPersonaStateChange(SteamID steamID, SteamFriends.PersonaChange change) {
        this.print("onPersonaStateChange", steamID, change);
    }

    public void onGameOverlayActivated(boolean active, boolean userInitiated, int appID) {
        this.print("onGameOverlayActivated", active, userInitiated, appID);
    }

    public void onGameLobbyJoinRequested(SteamID steamIDLobby, SteamID steamIDFriend) {
        this.print("onGameLobbyJoinRequested", steamIDLobby, steamIDFriend);
    }

    public void onAvatarImageLoaded(SteamID steamID, int image, int width, int height) {
        this.print("onAvatarImageLoaded", steamID, image, width, height);
    }

    public void onFriendRichPresenceUpdate(SteamID steamIDFriend, int appID) {
        this.print("onFriendRichPresenceUpdate", steamIDFriend, appID);
    }

    public void onGameRichPresenceJoinRequested(SteamID steamIDFriend, String connect) {
        this.print("onGameRichPresenceJoinRequested", steamIDFriend, connect);
    }

    public void onGameServerChangeRequested(String server, String password) {
        this.print("onGameServerChangeRequested", server, password);
    }
}

