/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData.settler;

import necesse.engine.network.client.Client;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Mob;

public interface CommandMob {
    public boolean hasCommandOrders();

    public boolean canBeCommanded(Client var1);

    public boolean canBeCommanded(ServerClient var1);

    public void clearCommandsOrders(ServerClient var1);

    public void commandFollow(ServerClient var1, Mob var2);

    public void commandGuard(ServerClient var1, int var2, int var3);

    public void commandAttack(ServerClient var1, Mob var2);

    public void setHideOnLowHealth(boolean var1);

    public boolean getHideOnLowHealth();
}

