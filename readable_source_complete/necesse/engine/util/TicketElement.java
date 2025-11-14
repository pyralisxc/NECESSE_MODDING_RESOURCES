/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util;

public abstract class TicketElement {
    protected int tickets;
    protected long startTicket;
    protected long endTicket;

    public TicketElement(int tickets, long startTicket, long endTicket) {
        this.tickets = tickets;
        this.startTicket = startTicket;
        this.endTicket = endTicket;
    }

    public boolean matchTicket(long ticket) {
        return this.startTicket <= ticket && ticket < this.endTicket;
    }
}

