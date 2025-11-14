/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import necesse.engine.GameSystemInfo;
import necesse.engine.gameLoop.tickManager.PerformanceTimerUtils;
import necesse.engine.gameLoop.tickManager.PerformanceTotal;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;

public class PacketPerformanceStart
extends Packet {
    public final int uniqueID;
    public final int seconds;
    public final boolean waitForServer;

    public PacketPerformanceStart(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.uniqueID = reader.getNextInt();
        this.seconds = reader.getNextInt();
        this.waitForServer = reader.getNextBoolean();
    }

    public PacketPerformanceStart(int uniqueID, int seconds, boolean waitForServer) {
        this.uniqueID = uniqueID;
        this.seconds = seconds;
        this.waitForServer = waitForServer;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(uniqueID);
        writer.putNextInt(seconds);
        writer.putNextBoolean(waitForServer);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        String dateFormat = new SimpleDateFormat("yyyy-MM-dd HH'h'mm'm'ss's'").format(new Date());
        client.performanceDumpCache.submitIncomingDump(this.uniqueID, System.currentTimeMillis() + (long)(this.seconds + 30) * 1000L, this.seconds, "performance " + dateFormat);
        client.tickManager().runPerformanceDump(this.seconds, history -> {
            ByteArrayOutputStream text = new ByteArrayOutputStream();
            PrintStream printStream = new PrintStream(text);
            printStream.println("Client performance recording ran for " + this.seconds + " seconds starting at " + dateFormat);
            PerformanceTotal total = PerformanceTimerUtils.combineTimers(history);
            if (total != null) {
                printStream.println("A total of " + total.getTotalFrames() + " frames were recorded.");
                printStream.println();
                total.print(printStream);
                printStream.println();
                printStream.println();
                GameSystemInfo.printSystemInfo(printStream);
            }
            client.performanceDumpCache.submitClientDump(this.uniqueID, text.toString(), this.waitForServer);
        });
        if (this.waitForServer) {
            client.chat.addMessage("Recording server and client performance for the next " + this.seconds + " seconds...");
        } else {
            client.chat.addMessage("Recording client performance for the next " + this.seconds + " seconds");
        }
    }
}

