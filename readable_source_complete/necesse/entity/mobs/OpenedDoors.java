/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs;

import java.awt.Point;
import java.util.HashMap;
import java.util.Iterator;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.OpenedDoor;

public class OpenedDoors
implements Iterable<OpenedDoor> {
    private final Mob mob;
    private HashMap<Point, OpenedDoor> openedDoors = new HashMap();

    public OpenedDoors(Mob mob) {
        this.mob = mob;
    }

    public boolean isEmpty() {
        return this.openedDoors.isEmpty();
    }

    @Override
    public Iterator<OpenedDoor> iterator() {
        return new OpenedDoorsIterator();
    }

    public void add(int tileX, int tileY, int mobX, int mobY, boolean isSwitched) {
        this.openedDoors.put(new Point(tileX, tileY), new OpenedDoor(tileX, tileY, mobX, mobY, isSwitched));
        this.mob.getLevel().entityManager.serverOpenedDoors.put(new Point(tileX, tileY), this.mob);
    }

    public OpenedDoor get(int tileX, int tileY) {
        return this.openedDoors.get(new Point(tileX, tileY));
    }

    public void clear() {
        for (OpenedDoor openedDoor : this.openedDoors.values()) {
            this.mob.getLevel().entityManager.serverOpenedDoors.remove(new Point(openedDoor.tileX, openedDoor.tileY), this.mob);
        }
        this.openedDoors.clear();
    }

    public boolean hasMobServerOpened(int tileX, int tileY) {
        Mob mob = this.mob.getLevel().entityManager.serverOpenedDoors.get(new Point(tileX, tileY));
        return mob != null && !mob.removed();
    }

    private class OpenedDoorsIterator
    implements Iterator<OpenedDoor> {
        private final Iterator<OpenedDoor> iterator;
        private OpenedDoor last;

        public OpenedDoorsIterator() {
            this.iterator = OpenedDoors.this.openedDoors.values().iterator();
        }

        @Override
        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        @Override
        public OpenedDoor next() {
            this.last = this.iterator.next();
            return this.last;
        }

        @Override
        public void remove() {
            if (this.last != null) {
                ((OpenedDoors)OpenedDoors.this).mob.getLevel().entityManager.serverOpenedDoors.remove(new Point(this.last.tileX, this.last.tileY), OpenedDoors.this.mob);
                this.last = null;
            }
            this.iterator.remove();
        }
    }
}

