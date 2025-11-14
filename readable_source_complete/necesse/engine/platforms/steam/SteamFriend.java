/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.codedisaster.steamworks.SteamFriends$FriendRelationship
 *  com.codedisaster.steamworks.SteamFriends$PersonaState
 *  com.codedisaster.steamworks.SteamID
 */
package necesse.engine.platforms.steam;

import com.codedisaster.steamworks.SteamFriends;
import com.codedisaster.steamworks.SteamID;

public abstract class SteamFriend {
    public final SteamID steamID;

    public SteamFriend(SteamID steamID) {
        this.steamID = steamID;
    }

    public abstract String getName();

    public abstract SteamFriends.PersonaState getState();

    public abstract SteamFriends.FriendRelationship getRelationship();
}

