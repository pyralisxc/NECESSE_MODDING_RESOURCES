/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.actions;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.actions.CommandSettlersCustomAction;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.settler.CommandMob;

public class CommandSettlersGuardAction
extends CommandSettlersCustomAction {
    public CommandSettlersGuardAction(SettlementContainer container) {
        super(container);
    }

    public void runAndSend(Collection<Integer> mobUniqueIDs, int x, int y) {
        PacketWriter writer = this.setupPacket(mobUniqueIDs);
        writer.putNextBoolean(false);
        writer.putNextInt(x);
        writer.putNextInt(y);
        this.runAndSendAction(writer.getPacket());
    }

    public void runAndSend(Collection<Integer> mobUniqueIDs, ArrayList<Point> points) {
        PacketWriter writer = this.setupPacket(mobUniqueIDs);
        writer.putNextBoolean(true);
        writer.putNextShortUnsigned(points.size());
        for (Point point : points) {
            writer.putNextInt(point.x);
            writer.putNextInt(point.y);
        }
        this.runAndSendAction(writer.getPacket());
    }

    @Override
    public void executePacket(PacketReader reader, ServerSettlementData data, ServerClient client, ArrayList<CommandMob> mobs) {
        if (reader.getNextBoolean()) {
            int i;
            int size = reader.getNextShortUnsigned();
            if (size <= 0) {
                return;
            }
            ArrayList<Point> points = new ArrayList<Point>(size);
            for (i = 0; i < size; ++i) {
                int x = reader.getNextInt();
                int y = reader.getNextInt();
                points.add(new Point(x, y));
            }
            if (size < mobs.size()) {
                double mobsPerPoint = (double)mobs.size() / (double)size;
                double mobCounter = 0.0;
                for (CommandMob mob : mobs) {
                    Point point = (Point)points.get(Math.min((int)mobCounter, points.size() - 1));
                    mob.commandGuard(client, point.x, point.y);
                    mobCounter += mobsPerPoint;
                }
            } else {
                for (i = 0; i < mobs.size(); ++i) {
                    CommandMob mob = mobs.get(i);
                    Point point = (Point)points.get(i);
                    mob.commandGuard(client, point.x, point.y);
                }
            }
        } else {
            int x = reader.getNextInt();
            int y = reader.getNextInt();
            for (CommandMob mob : mobs) {
                mob.commandGuard(client, x, y);
            }
        }
    }
}

