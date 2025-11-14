/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.client.loading;

import necesse.engine.network.client.Client;
import necesse.engine.network.client.loading.ClientLoading;

public abstract class ClientLoadingUtil {
    public final ClientLoading loading;
    public final Client client;
    private long waitTime;

    public ClientLoadingUtil(ClientLoading loading) {
        this.loading = loading;
        this.client = loading.client;
    }

    protected final void setWait(int millis) {
        this.waitTime = System.currentTimeMillis() + (long)millis;
    }

    protected final boolean isWaiting() {
        return System.currentTimeMillis() < this.waitTime;
    }
}

