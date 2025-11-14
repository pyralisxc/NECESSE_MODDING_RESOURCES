/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.codedisaster.steamworks.SteamID
 */
package necesse.engine.platforms.steam.network.server.network;

import com.codedisaster.steamworks.SteamID;
import java.util.HashMap;
import necesse.engine.util.GameLinkedList;

public class ServerSteamInvitedUsers {
    private static final int invitedUsersTimeout = 600000;
    private final HashMap<SteamID, GameLinkedList.Element> steamIDs = new HashMap();
    private final GameLinkedList<InvitedUser> timeoutQueue = new GameLinkedList();

    public synchronized void addInvitedUser(SteamID steamID) {
        this.clean();
        GameLinkedList.Element current = this.steamIDs.get(steamID);
        if (current != null && !current.isRemoved()) {
            current.remove();
        }
        GameLinkedList.Element element = this.timeoutQueue.addLast(new InvitedUser(steamID));
        this.steamIDs.put(steamID, element);
    }

    public synchronized boolean isInvited(SteamID steamID) {
        this.clean();
        return this.steamIDs.containsKey(steamID);
    }

    public synchronized void clean() {
        while (!this.timeoutQueue.isEmpty()) {
            InvitedUser first = this.timeoutQueue.getFirst();
            long timeSinceInvite = System.currentTimeMillis() - first.invitedTime;
            if (timeSinceInvite <= 600000L) break;
            this.steamIDs.remove(first.steamID);
            this.timeoutQueue.removeFirst();
        }
    }

    private static class InvitedUser {
        public final SteamID steamID;
        public long invitedTime;

        public InvitedUser(SteamID steamID) {
            this.steamID = steamID;
            this.invitedTime = System.currentTimeMillis();
        }
    }
}

