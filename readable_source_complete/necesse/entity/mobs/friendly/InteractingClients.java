/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.friendly;

import java.util.HashMap;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameLinkedList;
import necesse.entity.mobs.Mob;

public class InteractingClients {
    public static long CACHE_TIME = 2000L;
    protected final Mob mob;
    protected HashMap<ServerClient, GameLinkedList.Element> map = new HashMap();
    protected GameLinkedList<InteractingClient> list = new GameLinkedList();

    public InteractingClients(Mob mob) {
        this.mob = mob;
    }

    public synchronized void serverTick() {
        InteractingClient first;
        while (!this.list.isEmpty() && (first = this.list.getFirst()).hasTimedOut(this.mob.getTime())) {
            this.list.removeFirst();
            this.map.remove(first.client);
        }
    }

    public synchronized void refresh(ServerClient client) {
        if (client == null) {
            return;
        }
        GameLinkedList.Element element = this.map.get(client);
        if (element != null && !element.isRemoved()) {
            element.remove();
        }
        InteractingClient interactingClient = new InteractingClient(client);
        this.map.put(client, this.list.addLast(interactingClient));
        interactingClient.refreshTimeout(this.mob.getTime());
    }

    public synchronized void remove(ServerClient client) {
        GameLinkedList.Element element = this.map.remove(client);
        if (element != null && !element.isRemoved()) {
            element.remove();
        }
    }

    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    public int size() {
        return this.map.size();
    }

    public synchronized void clear() {
        this.map.clear();
        this.list.clear();
    }

    protected static class InteractingClient {
        public final ServerClient client;
        public long timeoutTime;

        public InteractingClient(ServerClient client) {
            this.client = client;
        }

        public void refreshTimeout(long currentTime) {
            this.timeoutTime = currentTime + CACHE_TIME;
        }

        public boolean hasTimedOut(long currentTime) {
            return this.timeoutTime >= currentTime;
        }
    }
}

