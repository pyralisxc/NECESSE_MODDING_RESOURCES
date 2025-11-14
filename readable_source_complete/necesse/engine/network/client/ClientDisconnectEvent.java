/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.client;

import necesse.engine.network.client.Client;

@FunctionalInterface
public interface ClientDisconnectEvent {
    public void apply(Client var1);
}

