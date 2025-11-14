/*
 * Decompiled with CFR 0.152.
 */
import necesse.StartPlatformClient;
import necesse.engine.platforms.steam.SteamPlatform;

public class StartSteamClient {
    public static void main(String[] args) {
        StartPlatformClient.start(args, new SteamPlatform());
    }
}

