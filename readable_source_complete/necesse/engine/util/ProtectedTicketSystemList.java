/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import necesse.engine.util.GameUtils;
import necesse.engine.util.TicketElement;

public class ProtectedTicketSystemList<T> {
    private final List<TicketObject> list = new ArrayList<TicketObject>();
    private long ticketCounter = 0L;

    public ProtectedTicketSystemList() {
    }

    public ProtectedTicketSystemList(ProtectedTicketSystemList<T> copy) {
        this();
        if (copy != null) {
            this.addAll(copy);
        }
    }

    protected ProtectedTicketSystemList<T> addAll(ProtectedTicketSystemList<T> list) {
        for (TicketObject ticketObject : list.list) {
            this.addObject(ticketObject.tickets, ticketObject.object);
        }
        return this;
    }

    protected ProtectedTicketSystemList<T> addAllReversed(ProtectedTicketSystemList<T> list) {
        for (TicketObject ticketObject : list.list) {
            int tickets = (int)(list.ticketCounter - (long)ticketObject.tickets);
            this.addObject(tickets, ticketObject.object);
        }
        return this;
    }

    protected ProtectedTicketSystemList<T> addObject(int tickets, T object, boolean forceRemoveOnGet) {
        long startTicket = this.ticketCounter;
        long endTicket = this.ticketCounter + (long)tickets;
        this.list.add(new TicketObject(tickets, startTicket, endTicket, object, forceRemoveOnGet));
        this.ticketCounter = endTicket;
        return this;
    }

    protected ProtectedTicketSystemList<T> addObject(int tickets, T object) {
        return this.addObject(tickets, object, false);
    }

    protected boolean removeObject(T object) {
        ListIterator<TicketObject> li = this.list.listIterator();
        TicketObject out = null;
        while (li.hasNext()) {
            TicketObject next = li.next();
            if (out != null) {
                next.startTicket = this.ticketCounter;
                this.ticketCounter = next.endTicket = this.ticketCounter + (long)next.tickets;
                continue;
            }
            if (!Objects.equals(next.object, object)) continue;
            li.remove();
            out = next;
            this.ticketCounter = next.startTicket;
        }
        return out != null;
    }

    protected long nextLong(Random random, long bound) {
        long val;
        long bits;
        while ((bits = random.nextLong() << 1 >>> 1) - (val = bits % bound) + (bound - 1L) < 0L) {
        }
        return val;
    }

    protected T getRandomObject(Random random) {
        if (this.isEmpty()) {
            return null;
        }
        long ticket = this.ticketCounter <= Integer.MAX_VALUE ? (long)random.nextInt((int)this.ticketCounter) : this.nextLong(random, this.ticketCounter);
        TicketObject foundObject = this.list.stream().filter(e -> e.matchTicket(ticket)).findFirst().orElse(null);
        if (foundObject != null) {
            if (foundObject.forceRemoveOnGet) {
                ListIterator<TicketObject> li = this.list.listIterator();
                boolean removed = false;
                while (li.hasNext()) {
                    TicketObject next = li.next();
                    if (removed) {
                        next.startTicket = this.ticketCounter;
                        this.ticketCounter = next.endTicket = this.ticketCounter + (long)next.tickets;
                        continue;
                    }
                    if (next != foundObject) continue;
                    li.remove();
                    removed = true;
                    this.ticketCounter = next.startTicket;
                }
            }
            return foundObject.object;
        }
        return null;
    }

    protected T getAndRemoveRandomObject(Random random) {
        if (this.isEmpty()) {
            return null;
        }
        long ticket = this.ticketCounter <= Integer.MAX_VALUE ? (long)random.nextInt((int)this.ticketCounter) : this.nextLong(random, this.ticketCounter);
        ListIterator<TicketObject> li = this.list.listIterator();
        TicketObject out = null;
        while (li.hasNext()) {
            TicketObject next = li.next();
            if (out != null) {
                next.startTicket = this.ticketCounter;
                this.ticketCounter = next.endTicket = this.ticketCounter + (long)next.tickets;
                continue;
            }
            if (!next.matchTicket(ticket)) continue;
            li.remove();
            out = next;
            this.ticketCounter = next.startTicket;
        }
        if (out == null) {
            return null;
        }
        return out.object;
    }

    protected void fixElements() {
        this.ticketCounter = 0L;
        for (TicketObject ticketObject : this.list) {
            ticketObject.startTicket = this.ticketCounter;
            this.ticketCounter = ticketObject.endTicket = this.ticketCounter + (long)ticketObject.tickets;
        }
    }

    public long getTotalTickets() {
        return this.ticketCounter;
    }

    public int getTotalElements() {
        return this.list.size();
    }

    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    public void clear() {
        this.list.clear();
        this.ticketCounter = 0L;
    }

    protected Iterable<TicketObject> getTicketItems() {
        return this.list;
    }

    public void printChances() {
        this.printChances(Objects::toString);
    }

    public void printChances(Function<T, String> toString) {
        System.out.println("Ticket chances (" + this.ticketCounter + " total tickets):");
        for (TicketObject ticketObject : this.list) {
            double chance = (double)ticketObject.tickets / (double)this.ticketCounter;
            System.out.println("\t" + toString.apply(ticketObject.object) + " " + chance * 100.0 + "% (" + ticketObject.tickets + " tickets)");
        }
    }

    protected ProtectedTicketSystemList<T> reversed() {
        ProtectedTicketSystemList<T> list = new ProtectedTicketSystemList<T>();
        list.addAllReversed(this);
        return list;
    }

    protected Iterable<T> getAll() {
        return GameUtils.mapIterable(this.list.iterator(), e -> e.object);
    }

    protected class TicketObject
    extends TicketElement {
        public final T object;
        public boolean forceRemoveOnGet;

        public TicketObject(int tickets, long startTicket, long endTicket, T object, boolean forceRemoveOnGet) {
            super(tickets, startTicket, endTicket);
            this.object = object;
            this.forceRemoveOnGet = forceRemoveOnGet;
        }
    }
}

