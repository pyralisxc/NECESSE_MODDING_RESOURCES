/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util;

import java.util.HashMap;
import java.util.function.Function;
import necesse.engine.util.GameLinkedList;

public class HashProxyLinkedList<T, P>
extends GameLinkedList<T> {
    private final HashMap<P, GameLinkedList.Element> set = new HashMap();
    private final Function<T, P> proxyMapper;

    public HashProxyLinkedList(Function<T, P> proxyMapper) {
        this.proxyMapper = proxyMapper;
    }

    @Override
    public void onAdded(GameLinkedList.Element element) {
        this.set.put(this.proxyMapper.apply(element.object), element);
    }

    @Override
    public void onRemoved(GameLinkedList.Element element) {
        this.set.remove(this.proxyMapper.apply(element.object));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean contains(Object o) {
        Object object = this.lock;
        synchronized (object) {
            return this.set.containsKey(o);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean remove(Object object) {
        Object object2 = this.lock;
        synchronized (object2) {
            GameLinkedList.Element element = this.set.get(object);
            if (element != null) {
                element.remove();
                return true;
            }
            return false;
        }
    }

    public GameLinkedList.Element getElement(Object object) {
        return this.set.get(object);
    }

    public T getObject(Object object) {
        GameLinkedList.Element element = this.getElement(object);
        return element != null ? (T)element.object : null;
    }
}

