/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.manager;

import java.awt.Point;
import java.util.ArrayList;
import java.util.function.Function;
import necesse.engine.util.GameRandom;
import necesse.engine.util.TicketSystemList;

public class MobSpawnArea {
    public final int minSpawnDistance;
    public final int maxSpawnDistance;
    private ArrayList<Point> positions = new ArrayList();

    public MobSpawnArea(int minSpawnDistance, int maxSpawnDistance) {
        this.minSpawnDistance = minSpawnDistance;
        this.maxSpawnDistance = maxSpawnDistance;
        int start = -maxSpawnDistance / 32 - 1;
        int end = maxSpawnDistance / 32 + 1;
        for (int x = start; x <= end; ++x) {
            for (int y = start; y <= end; ++y) {
                double dist = new Point(x * 32, y * 32).distance(0.0, 0.0);
                if (dist < (double)minSpawnDistance || dist > (double)maxSpawnDistance) continue;
                this.positions.add(new Point(x, y));
            }
        }
    }

    public Point getRandomTile(GameRandom random, int centerTileX, int centerTileY) {
        Point pos = random.getOneOf(this.positions);
        return new Point(centerTileX + pos.x, centerTileY + pos.y);
    }

    public Point getRandomTicketTile(GameRandom random, int centerTileX, int centerTileY, Function<Point, Integer> ticketsGetter) {
        TicketSystemList ticketList = new TicketSystemList();
        for (Point dp : this.positions) {
            Point pos = new Point(centerTileX + dp.x, centerTileY + dp.y);
            int tickets = ticketsGetter.apply(pos);
            if (tickets <= 0) continue;
            ticketList.addObject(tickets, pos);
        }
        return (Point)ticketList.getRandomObject(random);
    }
}

