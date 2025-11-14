/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData.jobCondition;

import necesse.engine.network.Packet;

@FunctionalInterface
public interface JobConditionUpdatePacketSender {
    public void sendUpdatePacket(int var1, Packet var2);
}

