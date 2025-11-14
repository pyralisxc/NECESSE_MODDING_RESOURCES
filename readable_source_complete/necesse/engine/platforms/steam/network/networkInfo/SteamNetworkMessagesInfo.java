/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.codedisaster.steamworks.SteamException
 *  com.codedisaster.steamworks.SteamID
 *  com.codedisaster.steamworks.SteamNativeHandle
 *  com.codedisaster.steamworks.SteamNetworkingMessages
 *  com.codedisaster.steamworks.SteamNetworkingMessages$SendFlag
 *  com.codedisaster.steamworks.SteamResult
 */
package necesse.engine.platforms.steam.network.networkInfo;

import com.codedisaster.steamworks.SteamException;
import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamNativeHandle;
import com.codedisaster.steamworks.SteamNetworkingMessages;
import com.codedisaster.steamworks.SteamResult;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Objects;
import necesse.engine.GameLog;
import necesse.engine.network.networkInfo.NetworkInfo;

public class SteamNetworkMessagesInfo
extends NetworkInfo {
    public static SteamNetworkingMessages.SendFlag sendFlag = SteamNetworkingMessages.SendFlag.Unreliable;
    public static final int defaultChannel = 0;
    public static final HashMap<Long, HashMap<SteamResult, MessageWarnings>> messageWarnings = new HashMap();
    public static int messageWarningCooldown = 10000;
    public final SteamNetworkingMessages networking;
    public final SteamID remoteID;

    public SteamNetworkMessagesInfo(SteamNetworkingMessages networking, SteamID remoteID) {
        this.networking = networking;
        this.remoteID = remoteID;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void send(byte[] data) throws IOException {
        block6: {
            ByteBuffer buffer = ByteBuffer.allocateDirect(data.length);
            buffer.put(data);
            buffer.flip();
            try {
                SteamResult steamResult = this.networking.sendMessageToUser(this.remoteID, buffer, sendFlag, 0);
                if (steamResult == SteamResult.OK) break block6;
                if (steamResult == SteamResult.ConnectFailed || steamResult == SteamResult.NoConnection || steamResult == SteamResult.InvalidParam || steamResult == SteamResult.InvalidState) {
                    this.networking.closeSessionWithUser(this.remoteID);
                    GameLog.warn.println("Closed Steam session with " + SteamID.getNativeHandle((SteamNativeHandle)this.remoteID) + " because " + steamResult.name());
                    break block6;
                }
                HashMap<Long, HashMap<SteamResult, MessageWarnings>> hashMap = messageWarnings;
                synchronized (hashMap) {
                    HashMap remoteIDWarnings = messageWarnings.compute(SteamID.getNativeHandle((SteamNativeHandle)this.remoteID), (key, last) -> {
                        if (last == null) {
                            return new HashMap();
                        }
                        return last;
                    });
                    MessageWarnings warnings = remoteIDWarnings.compute(steamResult, (key, last) -> {
                        if (last == null) {
                            return new MessageWarnings();
                        }
                        return last;
                    });
                    warnings.submit("Could not send packet to " + SteamID.getNativeHandle((SteamNativeHandle)this.remoteID) + " with length " + data.length + ": " + steamResult);
                }
            }
            catch (SteamException e) {
                throw new IOException(e);
            }
        }
    }

    @Override
    public String getDisplayName() {
        return "STEAM:" + SteamID.getNativeHandle((SteamNativeHandle)this.remoteID);
    }

    @Override
    public void closeConnection() {
    }

    @Override
    public void resetConnection() {
        System.out.println("RESET CONNECTION " + this.remoteID);
        this.networking.closeSessionWithUser(this.remoteID);
        this.networking.acceptSessionWithUser(this.remoteID);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SteamNetworkMessagesInfo) {
            SteamNetworkMessagesInfo other = (SteamNetworkMessagesInfo)obj;
            return Objects.equals(this.remoteID, other.remoteID);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (int)SteamID.getNativeHandle((SteamNativeHandle)this.remoteID);
    }

    protected static class MessageWarnings {
        public long lastPrintTime;
        public int warningsSinceLastPrint;

        protected MessageWarnings() {
        }

        public void submit(String warning) {
            long timeSinceLastPrint = System.currentTimeMillis() - this.lastPrintTime;
            if (timeSinceLastPrint >= (long)messageWarningCooldown) {
                if (this.warningsSinceLastPrint > 1) {
                    warning = warning + " (" + this.warningsSinceLastPrint + ")";
                }
                GameLog.warn.println(warning);
                this.lastPrintTime = System.currentTimeMillis();
                this.warningsSinceLastPrint = 0;
            }
            ++this.warningsSinceLastPrint;
        }
    }
}

