/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.codedisaster.steamworks.SteamControllerHandle
 */
package necesse.engine.platforms.steam.input;

import com.codedisaster.steamworks.SteamControllerHandle;
import necesse.engine.input.InputSource;
import necesse.engine.input.controller.ControllerHandle;

public class SteamGameControllerHandle
extends ControllerHandle {
    public final SteamControllerHandle steamNativeControllerHandle;

    public SteamGameControllerHandle(SteamControllerHandle steamControllerHandle, InputSource inputSource) {
        super(inputSource, ControllerHandle.ControllerType.Unknown);
        this.steamNativeControllerHandle = steamControllerHandle;
    }
}

