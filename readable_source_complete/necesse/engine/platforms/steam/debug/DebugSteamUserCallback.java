/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.codedisaster.steamworks.SteamAuth$AuthSessionResponse
 *  com.codedisaster.steamworks.SteamAuthTicket
 *  com.codedisaster.steamworks.SteamID
 *  com.codedisaster.steamworks.SteamResult
 *  com.codedisaster.steamworks.SteamUserCallback
 */
package necesse.engine.platforms.steam.debug;

import com.codedisaster.steamworks.SteamAuth;
import com.codedisaster.steamworks.SteamAuthTicket;
import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamResult;
import com.codedisaster.steamworks.SteamUserCallback;
import necesse.engine.platforms.steam.debug.DebugSteamCallback;

public class DebugSteamUserCallback
extends DebugSteamCallback
implements SteamUserCallback {
    public void onValidateAuthTicket(SteamID steamID, SteamAuth.AuthSessionResponse authSessionResponse, SteamID ownerSteamID) {
        this.print("onValidateAuthTicket", steamID, authSessionResponse, ownerSteamID);
    }

    public void onMicroTxnAuthorization(int appID, long orderID, boolean authorized) {
        this.print("onMicroTxnAuthorization", appID, orderID, authorized);
    }

    public void onEncryptedAppTicket(SteamResult result) {
        this.print("onEncryptedAppTicket", result);
    }

    public void onAuthSessionTicket(SteamAuthTicket authTicket, SteamResult result) {
        this.print("onAuthSessionTicket", authTicket, result);
    }
}

