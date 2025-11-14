/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util;

import java.util.Random;
import necesse.engine.util.ProtectedTicketSystemList;

public class TicketSystemList<T>
extends ProtectedTicketSystemList<T> {
    public TicketSystemList() {
    }

    public TicketSystemList(TicketSystemList<T> copy) {
        super(copy);
    }

    @Override
    public TicketSystemList<T> addAll(ProtectedTicketSystemList<T> list) {
        return (TicketSystemList)super.addAll(list);
    }

    @Override
    public TicketSystemList<T> addObject(int tickets, T object, boolean forceRemoveOnGet) {
        return (TicketSystemList)super.addObject(tickets, object, forceRemoveOnGet);
    }

    @Override
    public TicketSystemList<T> addObject(int tickets, T object) {
        return (TicketSystemList)super.addObject(tickets, object);
    }

    @Override
    protected boolean removeObject(T object) {
        return super.removeObject(object);
    }

    @Override
    public T getRandomObject(Random random) {
        return super.getRandomObject(random);
    }

    @Override
    public T getAndRemoveRandomObject(Random random) {
        return super.getAndRemoveRandomObject(random);
    }

    @Override
    public TicketSystemList<T> reversed() {
        return (TicketSystemList)new TicketSystemList<T>().addAllReversed(this);
    }

    @Override
    public Iterable<T> getAll() {
        return super.getAll();
    }
}

