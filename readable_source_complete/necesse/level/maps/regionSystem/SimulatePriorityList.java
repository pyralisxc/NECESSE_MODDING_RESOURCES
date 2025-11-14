/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.regionSystem;

import java.awt.Point;
import java.util.HashMap;
import necesse.engine.util.GameLinkedList;

public class SimulatePriorityList {
    protected final GameLinkedList<SimulateLogic> queue = new GameLinkedList();
    protected final HashMap<Point, SimulateLogic> map = new HashMap();

    public void run() {
        while (!this.queue.isEmpty()) {
            this.queue.removeFirst().logic.run();
        }
    }

    public void add(Point tile, long remainingTicks, Runnable logic) {
        this.map.compute(tile, (point, last) -> {
            if (last == null) {
                last = new SimulateLogic(tile.x, tile.y, remainingTicks, logic);
                this.updateQueuePosition((SimulateLogic)last);
            } else if (last.remainingTicks > remainingTicks) {
                last.logic = logic;
                last.remainingTicks = remainingTicks;
                this.updateQueuePosition((SimulateLogic)last);
            }
            return last;
        });
    }

    public void add(int tileX, int tileY, long remainingTicks, Runnable logic) {
        this.add(new Point(tileX, tileY), remainingTicks, logic);
    }

    public boolean queueContains(Point tile) {
        SimulateLogic current = this.map.get(tile);
        return current != null && !current.queueElement.isRemoved();
    }

    public boolean queueContains(int tileX, int tileY) {
        return this.queueContains(new Point(tileX, tileY));
    }

    public boolean contains(Point tile) {
        return this.map.containsKey(tile);
    }

    public boolean contains(int tileX, int tileY) {
        return this.contains(new Point(tileX, tileY));
    }

    protected void updateQueuePosition(SimulateLogic tile) {
        block5: {
            if (tile.queueElement != null && !tile.queueElement.isRemoved()) {
                tile.queueElement.remove();
            }
            if (this.queue.isEmpty()) {
                tile.queueElement = this.queue.addFirst(tile);
            } else {
                GameLinkedList.Element current = this.queue.getFirstElement();
                while (true) {
                    if (tile.remainingTicks > ((SimulateLogic)current.object).remainingTicks) {
                        tile.queueElement = current.insertBefore(tile);
                        break block5;
                    }
                    if (!current.hasNext()) break;
                    current = current.next();
                }
                tile.queueElement = current.insertAfter(tile);
            }
        }
    }

    protected static class SimulateLogic {
        public final int tileX;
        public final int tileY;
        public long remainingTicks;
        public Runnable logic;
        public GameLinkedList.Element queueElement;

        public SimulateLogic(int tileX, int tileY, long remainingTicks, Runnable logic) {
            this.tileX = tileX;
            this.tileY = tileY;
            this.remainingTicks = remainingTicks;
            this.logic = logic;
        }
    }
}

