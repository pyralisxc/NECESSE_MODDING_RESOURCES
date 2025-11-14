/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.egtb.syzygy;

import bagaturchess.uci.api.ChannelManager;
import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SyzygyJNIBridge {
    private static boolean libLoaded = false;
    private static int tbLargest = 0;
    private static final String FILE_SCHEME = "file";

    private SyzygyJNIBridge() {
    }

    public static boolean loadNativeLibrary() {
        block12: {
            try {
                String libName = System.mapLibraryName("JSyzygy");
                Path jarfile = Paths.get(SyzygyJNIBridge.class.getProtectionDomain().getCodeSource().getLocation().toURI());
                File libFile = jarfile.getParent().resolve(libName).toFile();
                if (ChannelManager.getChannel() != null) {
                    ChannelManager.getChannel().dump("Looking for " + libName + " at location " + String.valueOf(libFile));
                }
                if (libFile.exists()) {
                    System.load(libFile.getAbsolutePath());
                    if (ChannelManager.getChannel() != null) {
                        ChannelManager.getChannel().dump(libName + " is now loaded");
                    }
                } else {
                    URL classpathLibUrl = SyzygyJNIBridge.class.getClassLoader().getResource(libName);
                    if (ChannelManager.getChannel() != null) {
                        ChannelManager.getChannel().dump("Looking for " + libName + " at location " + String.valueOf(classpathLibUrl));
                    }
                    if (classpathLibUrl != null && FILE_SCHEME.equalsIgnoreCase(classpathLibUrl.toURI().getScheme()) && Paths.get(classpathLibUrl.toURI()).toFile().exists()) {
                        File classpathLibFile = Paths.get(classpathLibUrl.toURI()).toFile();
                        System.load(classpathLibFile.getAbsolutePath());
                        if (ChannelManager.getChannel() != null) {
                            ChannelManager.getChannel().dump("Loaded " + libName + " located in the resources directory");
                        }
                    } else {
                        if (ChannelManager.getChannel() != null) {
                            ChannelManager.getChannel().dump("Looking for " + libName + " at java.library.path: " + System.getProperty("java.library.path"));
                        }
                        System.loadLibrary("JSyzygy");
                        if (ChannelManager.getChannel() != null) {
                            ChannelManager.getChannel().dump("Loaded " + libName + " located in the java library path");
                        }
                    }
                }
                libLoaded = true;
            }
            catch (Throwable t) {
                if (ChannelManager.getChannel() == null) break block12;
                ChannelManager.getChannel().dump("Unable to load JSyzygy library " + String.valueOf(t));
            }
        }
        return libLoaded;
    }

    private static native boolean init(String var0);

    private static native int getTBLargest();

    private static native int probeDTM(long var0, long var2, long var4, long var6, long var8, long var10, long var12, long var14, int var16, int var17, boolean var18);

    private static native int probeWDL(long var0, long var2, long var4, long var6, long var8, long var10, long var12, long var14, int var16, int var17, boolean var18);

    private static native int probeDTZ(long var0, long var2, long var4, long var6, long var8, long var10, long var12, long var14, int var16, int var17, boolean var18);

    public static boolean isLibLoaded() {
        return libLoaded;
    }

    public static boolean isAvailable(int piecesLeft) {
        return libLoaded && piecesLeft <= tbLargest;
    }

    public static synchronized int load(String path) {
        if (ChannelManager.getChannel() != null) {
            ChannelManager.getChannel().dump("Loading syzygy tablebases from " + path);
        }
        if (tbLargest > 0) {
            if (ChannelManager.getChannel() != null) {
                ChannelManager.getChannel().dump("Syzygy tablebases are already loaded");
            }
            return tbLargest;
        }
        boolean result = SyzygyJNIBridge.init(path);
        if (result) {
            tbLargest = SyzygyJNIBridge.getTBLargest();
            if (ChannelManager.getChannel() != null) {
                ChannelManager.getChannel().dump("Syzygy tablebases loaded");
            }
        } else {
            tbLargest = -1;
            if (ChannelManager.getChannel() != null) {
                ChannelManager.getChannel().dump("Syzygy tablebases NOT loaded");
            }
        }
        return tbLargest;
    }

    public static int getSupportedSize() {
        return tbLargest;
    }

    public static int probeSyzygyDTM(long white, long black, long kings, long queens, long rooks, long bishops, long knights, long pawns, int rule50, int ep, boolean turn) {
        return SyzygyJNIBridge.probeDTM(white, black, kings, queens, rooks, bishops, knights, pawns, rule50, ep, turn);
    }

    public static int probeSyzygyWDL(long white, long black, long kings, long queens, long rooks, long bishops, long knights, long pawns, int rule50, int ep, boolean turn) {
        return SyzygyJNIBridge.probeWDL(white, black, kings, queens, rooks, bishops, knights, pawns, rule50, ep, turn);
    }

    public static int probeSyzygyDTZ(long white, long black, long kings, long queens, long rooks, long bishops, long knights, long pawns, int rule50, int ep, boolean turn) {
        return SyzygyJNIBridge.probeDTZ(white, black, kings, queens, rooks, bishops, knights, pawns, rule50, ep, turn);
    }
}

