/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.codedisaster.steamworks.SteamID
 */
package necesse.engine.platforms.steam.network.server.network;

import com.codedisaster.steamworks.SteamID;
import java.util.HashMap;
import java.util.function.Consumer;
import necesse.engine.util.GameLinkedList;

public class ServerSteamDeniedConnections {
    private static final int connectionTimeout = 10000;
    private final HashMap<SteamID, GameLinkedList.Element> steamIDs = new HashMap();
    private final GameLinkedList<InvitedUser> timeoutQueue = new GameLinkedList();

    public synchronized void addDeniedUser(SteamID steamID) {
        GameLinkedList.Element current = this.steamIDs.get(steamID);
        if (current != null && !current.isRemoved()) {
            current.remove();
        }
        GameLinkedList.Element element = this.timeoutQueue.addLast(new InvitedUser(steamID));
        this.steamIDs.put(steamID, element);
    }

    public synchronized void removeDeniedUser(SteamID steamID) {
        GameLinkedList.Element current = this.steamIDs.remove(steamID);
        if (current != null && !current.isRemoved()) {
            current.remove();
        }
    }

    public synchronized boolean isDenied(SteamID steamID) {
        return this.steamIDs.containsKey(steamID);
    }

    public synchronized void runCleanup(Consumer<SteamID> onTimedOut) {
        while (!this.timeoutQueue.isEmpty()) {
            InvitedUser first = this.timeoutQueue.getFirst();
            long timeSinceInvite = System.currentTimeMillis() - first.invitedTime;
            if (timeSinceInvite <= 10000L) break;
            onTimedOut.accept(first.steamID);
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

