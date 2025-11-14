/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.hudManager;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.SortedDrawable;
import necesse.level.maps.Level;
import necesse.level.maps.hudManager.HudDrawElement;
import necesse.level.maps.hudManager.HudDrawOnMap;

public class HudManager {
    private final Level level;
    private final ArrayList<StoredElement> list;
    private final GameLinkedList<HudDrawOnMap> mapList;

    public HudManager(Level level) {
        this.level = level;
        this.list = new ArrayList();
        this.mapList = new GameLinkedList();
    }

    public void tick() {
        for (int i = 0; i < this.list.size(); ++i) {
            StoredElement e = this.list.get(i);
            if (!e.hudDrawElement.isRemoved()) continue;
            if (e.mapDrawElement != null) {
                e.mapDrawElement.remove();
                e.mapDrawElement = null;
            }
            this.list.remove(i);
            --i;
            e.hudDrawElement.onRemove();
        }
    }

    public Iterable<HudDrawElement> getElements() {
        return GameUtils.mapIterable(this.list.iterator(), e -> e.hudDrawElement);
    }

    public Stream<HudDrawElement> streamElements() {
        return this.list.stream().map(e -> e.hudDrawElement);
    }

    public boolean removeOneElements(Predicate<HudDrawElement> predicate) {
        return this.removeElements(predicate, 1) > 0;
    }

    public int removeElements(Predicate<HudDrawElement> predicate) {
        return this.removeElements(predicate, Integer.MAX_VALUE);
    }

    public int removeElements(Predicate<HudDrawElement> predicate, int limit) {
        if (limit <= 0) {
            return 0;
        }
        int removed = 0;
        for (int i = 0; i < this.list.size(); ++i) {
            StoredElement e = this.list.get(i);
            if (!predicate.test(e.hudDrawElement)) continue;
            ++removed;
            if (e.mapDrawElement != null) {
                e.mapDrawElement.remove();
                e.mapDrawElement = null;
            }
            this.list.remove(i);
            --i;
            e.hudDrawElement.onRemove();
            if (removed >= limit) break;
        }
        return removed;
    }

    public void addDrawables(List<SortedDrawable> list, GameCamera camera, PlayerMob perspective) {
        for (StoredElement e : this.list.toArray(new StoredElement[0])) {
            if (e == null || e.hudDrawElement == null || e.hudDrawElement.isRemoved()) continue;
            e.hudDrawElement.addDrawables(list, camera, perspective);
        }
    }

    public Iterable<HudDrawOnMap> getMapDraws() {
        return this.mapList;
    }

    public <T extends HudDrawElement> T addElement(T element) {
        if (element == null || this.level.isServer()) {
            return null;
        }
        StoredElement storedElement = new StoredElement(element);
        this.list.add(storedElement);
        if (element instanceof HudDrawOnMap) {
            storedElement.mapDrawElement = this.mapList.addLast((HudDrawOnMap)((Object)element));
        }
        element.setLevel(this.level);
        element.init(this);
        return element;
    }

    private static class StoredElement {
        public HudDrawElement hudDrawElement;
        public GameLinkedList.Element mapDrawElement;

        public StoredElement(HudDrawElement hudDrawElement) {
            this.hudDrawElement = hudDrawElement;
        }
    }
}

