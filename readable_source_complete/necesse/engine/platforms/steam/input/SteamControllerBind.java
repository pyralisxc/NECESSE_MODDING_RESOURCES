/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.codedisaster.steamworks.SteamController
 *  com.codedisaster.steamworks.SteamController$ActionOrigin
 *  com.codedisaster.steamworks.SteamControllerActionSetHandle
 *  com.codedisaster.steamworks.SteamControllerAnalogActionData
 *  com.codedisaster.steamworks.SteamControllerAnalogActionHandle
 *  com.codedisaster.steamworks.SteamControllerDigitalActionData
 *  com.codedisaster.steamworks.SteamControllerDigitalActionHandle
 *  com.codedisaster.steamworks.SteamNativeHandle
 */
package necesse.engine.platforms.steam.input;

import com.codedisaster.steamworks.SteamController;
import com.codedisaster.steamworks.SteamControllerActionSetHandle;
import com.codedisaster.steamworks.SteamControllerAnalogActionData;
import com.codedisaster.steamworks.SteamControllerAnalogActionHandle;
import com.codedisaster.steamworks.SteamControllerDigitalActionData;
import com.codedisaster.steamworks.SteamControllerDigitalActionHandle;
import com.codedisaster.steamworks.SteamNativeHandle;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.controller.ControllerAnalogState;
import necesse.engine.input.controller.ControllerBind;
import necesse.engine.input.controller.ControllerButtonState;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerHandle;
import necesse.engine.input.controller.ControllerInputState;
import necesse.engine.platforms.steam.input.SteamGameControllerHandle;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameUtils;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameTexture;

public class SteamControllerBind
extends ControllerBind {
    public final SteamControllerActionSetHandle actionSetHandle;
    public final SteamNativeHandle actionHandle;
    private final SteamControllerDigitalActionData steamControllerDigitalActionDataCache = new SteamControllerDigitalActionData();
    private final SteamControllerAnalogActionData steamControllerAnalogActionDataCache = new SteamControllerAnalogActionData();
    private SteamController.ActionOrigin[] lastUsedInput;
    private GameTexture glyph = null;

    public SteamControllerBind(SteamControllerActionSetHandle actionSetHandle, SteamNativeHandle actionHandle) {
        this.actionSetHandle = actionSetHandle;
        this.actionHandle = actionHandle;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.lastUsedInput);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SteamControllerBind that = (SteamControllerBind)o;
        return Objects.deepEquals(this.lastUsedInput, that.lastUsedInput);
    }

    @Override
    public boolean isBound() {
        return this.lastUsedInput == null || this.lastUsedInput.length > 0;
    }

    @Override
    public void saveBind(SaveData saveData) {
    }

    @Override
    public void loadBind(LoadData loadData) {
    }

    @Override
    public GameTexture getGlyph(ControllerHandle controllerHandle) {
        return this.glyph;
    }

    public void updateLastUsedInputToNewController(ControllerInputState state, SteamGameControllerHandle controllerHandle, SteamController steamController) {
        if (state instanceof ControllerButtonState) {
            steamController.getDigitalActionData(controllerHandle.steamNativeControllerHandle, (SteamControllerDigitalActionHandle)this.actionHandle, this.steamControllerDigitalActionDataCache);
            this.setLastUsedInput(this.getSteamOriginsOut(state, controllerHandle, steamController), steamController);
        } else if (state instanceof ControllerAnalogState) {
            steamController.getAnalogActionData(controllerHandle.steamNativeControllerHandle, (SteamControllerAnalogActionHandle)this.actionHandle, this.steamControllerAnalogActionDataCache);
            this.setLastUsedInput(this.getSteamOriginsOut(state, controllerHandle, steamController), steamController);
        }
    }

    private void setLastUsedInput(SteamController.ActionOrigin[] lastUsedInput, SteamController steamController) {
        String small;
        this.lastUsedInput = lastUsedInput;
        if (lastUsedInput == null || lastUsedInput[0] == null) {
            this.glyph = null;
            return;
        }
        String glyphForActionOrigin = steamController.getGlyphForActionOrigin(lastUsedInput[0]);
        if (glyphForActionOrigin == null) {
            this.glyph = null;
            return;
        }
        String extension = GameUtils.getFileExtension(glyphForActionOrigin);
        if (extension != null && glyphForActionOrigin.substring(0, glyphForActionOrigin.length() - extension.length() - 1).endsWith("_md") && new File(small = glyphForActionOrigin.substring(0, glyphForActionOrigin.length() - extension.length() - 4) + "_sm." + extension).exists()) {
            glyphForActionOrigin = small;
        }
        try {
            this.glyph = GameTexture.fromFileRawOutside(glyphForActionOrigin);
        }
        catch (FileNotFoundException e) {
            this.glyph = GameResources.error;
            e.printStackTrace();
        }
    }

    public SteamController.ActionOrigin[] getSteamOriginsOut(ControllerInputState state, SteamGameControllerHandle controllerHandle, SteamController steamController) {
        SteamController.ActionOrigin[] originsOut = null;
        if (state == null) {
            return null;
        }
        try {
            if (state instanceof ControllerButtonState) {
                originsOut = new SteamController.ActionOrigin[8];
                steamController.getDigitalActionOrigins(controllerHandle.steamNativeControllerHandle, this.actionSetHandle, (SteamControllerDigitalActionHandle)this.actionHandle, originsOut);
            } else if (state instanceof ControllerAnalogState) {
                originsOut = new SteamController.ActionOrigin[8];
                steamController.getAnalogActionOrigins(controllerHandle.steamNativeControllerHandle, this.actionSetHandle, (SteamControllerAnalogActionHandle)this.actionHandle, originsOut);
            }
        }
        catch (Exception e) {
            return null;
        }
        return originsOut;
    }

    public boolean updateStateIfInput(ControllerInputState state, SteamGameControllerHandle controllerHandle, SteamController steamController, ArrayList<ControllerEvent> outEvents, TickManager tickManager) {
        if (state instanceof ControllerButtonState) {
            steamController.getDigitalActionData(controllerHandle.steamNativeControllerHandle, (SteamControllerDigitalActionHandle)this.actionHandle, this.steamControllerDigitalActionDataCache);
            if (this.steamControllerDigitalActionDataCache.getActive() && this.steamControllerDigitalActionDataCache.getState()) {
                ((ControllerButtonState)state).updateState(true, controllerHandle, this, outEvents, tickManager);
                this.setLastUsedInput(this.getSteamOriginsOut(state, controllerHandle, steamController), steamController);
                return true;
            }
        } else if (state instanceof ControllerAnalogState) {
            ControllerAnalogState controllerAnalogState = (ControllerAnalogState)state;
            steamController.getAnalogActionData(controllerHandle.steamNativeControllerHandle, (SteamControllerAnalogActionHandle)this.actionHandle, this.steamControllerAnalogActionDataCache);
            if (this.steamControllerAnalogActionDataCache.getActive()) {
                boolean analogIsZero;
                boolean bl = analogIsZero = this.steamControllerAnalogActionDataCache.getX() == 0.0f && this.steamControllerAnalogActionDataCache.getY() == 0.0f;
                if (!analogIsZero) {
                    controllerAnalogState.updateState(this.steamControllerAnalogActionDataCache.getX(), -this.steamControllerAnalogActionDataCache.getY(), controllerHandle, outEvents);
                    this.setLastUsedInput(this.getSteamOriginsOut(state, controllerHandle, steamController), steamController);
                    return true;
                }
            }
        }
        return false;
    }
}

