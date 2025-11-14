/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.debug.tools;

import necesse.engine.gameTool.GameTool;
import necesse.engine.input.InputEvent;
import necesse.engine.network.server.ServerClient;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.level.maps.Level;

public abstract class DebugGameTool
implements GameTool {
    public final String name;
    public final DebugForm parent;

    public DebugGameTool(DebugForm parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    @Override
    public abstract void init();

    @Override
    public abstract boolean inputEvent(InputEvent var1);

    @Override
    public void isCancelled() {
    }

    @Override
    public void isCleared() {
    }

    @Override
    public GameTooltips getTooltips() {
        return this.name == null ? null : new StringTooltips(this.name);
    }

    public Level getLevel() {
        return this.parent.client.getLevel();
    }

    public Level getServerLevel() {
        if (this.parent.client.getLocalServer() == null) {
            return null;
        }
        ServerClient sClient = this.parent.client.getLocalServer().getLocalServerClient();
        return this.parent.client.getLocalServer().world.getLevel(sClient.getLevelIdentifier());
    }
}

