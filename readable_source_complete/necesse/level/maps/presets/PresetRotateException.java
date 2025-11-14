/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets;

import necesse.engine.localization.message.GameMessage;

public class PresetRotateException
extends Exception {
    private final GameMessage gameMessage;

    public PresetRotateException(GameMessage message) {
        super(message.translate());
        this.gameMessage = message;
    }

    public PresetRotateException(GameMessage message, Throwable cause) {
        super(message.translate(), cause);
        this.gameMessage = message;
    }

    public PresetRotateException(GameMessage message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message.translate(), cause, enableSuppression, writableStackTrace);
        this.gameMessage = message;
    }

    public GameMessage getGameMessage() {
        return this.gameMessage;
    }
}

