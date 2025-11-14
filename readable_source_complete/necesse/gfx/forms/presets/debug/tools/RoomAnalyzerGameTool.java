/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.debug.tools;

import java.util.List;
import necesse.engine.gameTool.GameToolManager;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.gfx.forms.presets.debug.tools.MouseDebugGameTool;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.NetworkSettlementData;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.SettlementRoom;

public class RoomAnalyzerGameTool
extends MouseDebugGameTool {
    public RoomAnalyzerGameTool(DebugForm parent) {
        super(parent, "Room analyzer");
        this.onLeftClick(e -> {
            Level cLevel = this.getLevel();
            NetworkSettlementData networkData = new NetworkSettlementData(cLevel, -1, this.getMouseTileX(), this.getMouseTileY());
            ServerSettlementData serverData = new ServerSettlementData(null, networkData, -1);
            SettlementRoom room = new SettlementRoom(serverData, null, this.getMouseTileX(), this.getMouseTileY());
            List<String> tooltips = room.getDebugTooltips();
            tooltips.forEach(parent.client.chat::addMessage);
            return true;
        }, "Analyze room");
        this.onRightClick(e -> {
            GameToolManager.clearGameTool(this);
            return true;
        }, "Cancel");
    }

    @Override
    public void init() {
    }
}

