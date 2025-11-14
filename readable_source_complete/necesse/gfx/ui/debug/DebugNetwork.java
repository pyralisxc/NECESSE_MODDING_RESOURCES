/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.ui.debug;

import java.util.Arrays;
import java.util.Comparator;
import necesse.engine.Settings;
import necesse.engine.input.InputEvent;
import necesse.engine.network.PacketManager;
import necesse.engine.network.SizePacket;
import necesse.engine.network.StatPacket;
import necesse.engine.network.client.Client;
import necesse.engine.registries.PacketRegistry;
import necesse.gfx.TableContentDraw;
import necesse.gfx.ui.debug.Debug;

public class DebugNetwork
extends Debug {
    private boolean recent = true;
    private boolean sortByAmount = true;

    @Override
    protected void submitDebugInputEvent(InputEvent event, Client client) {
        if (event.state && event.getID() == 79) {
            this.recent = !this.recent;
            event.use();
        } else if (event.state && event.getID() == 80) {
            this.sortByAmount = !this.sortByAmount;
            event.use();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void drawDebug(Client client) {
        String amountS;
        String bytesS;
        StatPacket c;
        int i;
        PacketManager manager = client.packetManager;
        if (Settings.serverPerspective && client.getLocalServer() != null) {
            manager = client.getLocalServer().packetManager;
        }
        StatPacket[] in = new StatPacket[PacketRegistry.getTotalRegistered()];
        StatPacket[] out = new StatPacket[PacketRegistry.getTotalRegistered()];
        if (this.recent) {
            Iterable<SizePacket> recentInPackets;
            Iterable<SizePacket> iterable = recentInPackets = manager.getRecentInPackets();
            synchronized (iterable) {
                for (SizePacket sizePacket : recentInPackets) {
                    if (sizePacket.type == -1) continue;
                    StatPacket stat = in[sizePacket.type];
                    if (stat == null) {
                        in[sizePacket.type] = stat = new StatPacket(sizePacket.type);
                    }
                    ++stat.amount;
                    stat.bytes += sizePacket.byteSize;
                }
            }
            Iterable<SizePacket> recentOutPackets = manager.getRecentOutPackets();
            Iterable<SizePacket> iterable2 = recentOutPackets;
            synchronized (iterable2) {
                for (SizePacket p3 : recentOutPackets) {
                    if (p3.type == -1) continue;
                    StatPacket stat = out[p3.type];
                    if (stat == null) {
                        out[p3.type] = stat = new StatPacket(p3.type);
                    }
                    ++stat.amount;
                    stat.bytes += p3.byteSize;
                }
            }
        }
        for (int i2 = 0; i2 < PacketRegistry.getTotalRegistered(); ++i2) {
            in[i2] = manager.getTotalInStats(i2);
            out[i2] = manager.getTotalOutStats(i2);
        }
        if (this.sortByAmount) {
            Arrays.sort(in, Comparator.nullsLast(Comparator.comparingInt(p -> -p.amount)));
            Arrays.sort(out, Comparator.nullsLast(Comparator.comparingInt(p -> -p.amount)));
        } else {
            Arrays.sort(in, Comparator.nullsLast(Comparator.comparingInt(p -> -p.bytes)));
            Arrays.sort(out, Comparator.nullsLast(Comparator.comparingInt(p -> -p.bytes)));
        }
        if (this.recent) {
            this.drawString("Press 'O' to show total packets");
        } else {
            this.drawString("Press 'O' to show recent packets");
        }
        if (this.sortByAmount) {
            this.drawString("Press 'P' to sort by bytes");
        } else {
            this.drawString("Press 'P' to sort by count");
        }
        TableContentDraw tableDraw = new TableContentDraw();
        if (this.recent) {
            tableDraw.newRow().addTextColumn("Recent packets in: ", bigFontOptions, 5, 5).addTextColumn(manager.getAverageIn() + "/s", bigFontOptions, 5, 0).addTextColumn("(" + manager.getAverageInPackets() + ")", bigFontOptions, 10, 0);
        } else {
            tableDraw.newRow().addTextColumn("Total packets in: ", bigFontOptions, 5, 5).addTextColumn(manager.getTotalIn(), bigFontOptions, 5, 0).addTextColumn("(" + manager.getTotalInPackets() + ")", bigFontOptions, 10, 0);
        }
        for (i = 0; i < Math.min(10, in.length); ++i) {
            c = in[i];
            TableContentDraw.TableRow tableRow = tableDraw.newRow();
            if (c == null) {
                tableRow.addTextColumn(" ", smallFontOptions);
                continue;
            }
            float amountP = (float)((int)((float)c.amount / (float)manager.getTotalInPackets() * 10000.0f)) / 100.0f;
            float bytesP = (float)((int)((float)c.bytes / (float)manager.getTotalInBytes() * 10000.0f)) / 100.0f;
            bytesS = bytesP + "% (" + c.getBytes() + ")";
            amountS = amountP + "% (" + c.amount + ")";
            tableRow.addTextColumn(PacketRegistry.getPacketSimpleName(c.type) + ":", smallFontOptions, 5, 2).addTextColumn(this.sortByAmount ? amountS : bytesS, smallFontOptions, 10, 0).addTextColumn(this.sortByAmount ? bytesS : amountS, smallFontOptions, 10, 0);
        }
        tableDraw.newRow().addTextColumn("", smallFontOptions, 0, 2);
        if (this.recent) {
            tableDraw.newRow().addTextColumn("Recent packets out: ", bigFontOptions, 5, 5).addTextColumn(manager.getAverageOut() + "/s", bigFontOptions, 5, 0).addTextColumn("(" + manager.getAverageOutPackets() + ")", bigFontOptions, 10, 0);
        } else {
            tableDraw.newRow().addTextColumn("Total packets out: ", bigFontOptions, 5, 5).addTextColumn(manager.getTotalOut(), bigFontOptions, 5, 0).addTextColumn("(" + manager.getTotalOutPackets() + ")", bigFontOptions, 10, 0);
        }
        for (i = 0; i < Math.min(10, out.length); ++i) {
            c = out[i];
            TableContentDraw.TableRow tableRow = tableDraw.newRow();
            if (c == null) {
                tableRow.addTextColumn(" ", smallFontOptions);
                continue;
            }
            float amountP = (float)((int)((float)c.amount / (float)manager.getTotalOutPackets() * 10000.0f)) / 100.0f;
            float bytesP = (float)((int)((float)c.bytes / (float)manager.getTotalOutBytes() * 10000.0f)) / 100.0f;
            bytesS = bytesP + "% (" + c.getBytes() + ")";
            amountS = amountP + "% (" + c.amount + ")";
            tableRow.addTextColumn(PacketRegistry.getPacketSimpleName(c.type) + ":", smallFontOptions, 5, 2).addTextColumn(this.sortByAmount ? amountS : bytesS, smallFontOptions, 10, 0).addTextColumn(this.sortByAmount ? bytesS : amountS, smallFontOptions, 10, 0);
        }
        tableDraw.draw(10, this.skipY(tableDraw.getHeight() + 10));
    }
}

