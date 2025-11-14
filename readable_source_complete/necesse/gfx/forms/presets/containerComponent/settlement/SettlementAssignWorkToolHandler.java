/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.settlement;

import java.awt.Point;
import java.util.ArrayList;
import java.util.function.Consumer;
import necesse.engine.Settings;
import necesse.engine.input.Input;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.client.Client;
import necesse.engine.util.GameMath;
import necesse.engine.window.GameWindow;
import necesse.gfx.forms.floatMenu.FloatMenu;
import necesse.gfx.forms.floatMenu.SelectionFloatMenu;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementAssignWorkForm;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementToolHandler;
import necesse.gfx.gameTooltips.InputTooltip;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.zones.SettlementWorkZone;
import necesse.level.maps.multiTile.MultiTile;

public class SettlementAssignWorkToolHandler
implements SettlementToolHandler {
    public final Level level;
    public final SettlementContainer container;
    public final SettlementAssignWorkForm<?> workForm;

    public SettlementAssignWorkToolHandler(Client client, SettlementContainer container, SettlementAssignWorkForm<?> workForm) {
        this.level = client.getLevel();
        this.container = container;
        this.workForm = workForm;
    }

    @Override
    public boolean onLeftClick(Point pos) {
        ArrayList<ConfigureOption> options = this.addOptionsAndTooltips(pos, null, null);
        if (!options.isEmpty()) {
            if (options.size() == 1) {
                ConfigureOption first = options.get(0);
                this.workForm.playTickSound();
                first.onClicked.run();
                return true;
            }
            this.workForm.playTickSound();
            SelectionFloatMenu selection = new SelectionFloatMenu(this.workForm);
            for (ConfigureOption option : options) {
                selection.add(option.str, () -> {
                    option.onClicked.run();
                    selection.remove();
                });
            }
            this.workForm.getManager().openFloatMenu((FloatMenu)selection, -5, -5);
            return true;
        }
        return false;
    }

    @Override
    public boolean onHover(Point pos, Consumer<ListGameTooltips> setTooltips, Consumer<GameWindow.CURSOR> setCursor) {
        this.addOptionsAndTooltips(pos, setTooltips, setCursor);
        return false;
    }

    public ArrayList<ConfigureOption> addOptionsAndTooltips(Point pos, Consumer<ListGameTooltips> setTooltips, Consumer<GameWindow.CURSOR> setCursor) {
        MultiTile multiTile;
        int tileX = GameMath.getTileCoordinate(pos.x);
        int tileY = GameMath.getTileCoordinate(pos.y);
        ArrayList<ConfigureOption> options = new ArrayList<ConfigureOption>();
        ListGameTooltips tooltips = new ListGameTooltips();
        if (setTooltips != null) {
            setTooltips.accept(tooltips);
        }
        if (!Settings.hideSettlementStorage.get().booleanValue()) {
            for (Point point : this.workForm.storagePositions) {
                multiTile = this.level.getObject(point.x, point.y).getMultiTile(this.level, 0, point.x, point.y);
                if (!multiTile.getTileRectangle(point.x, point.y).contains(tileX, tileY)) continue;
                options.add(new ConfigureOption(Localization.translate("ui", "settlementconfigurestorage"), () -> this.workForm.openStorageConfig(point.x, point.y)));
            }
        }
        if (!Settings.hideSettlementWorkstations.get().booleanValue()) {
            for (Point point : this.workForm.workstationPositions) {
                multiTile = this.level.getObject(point.x, point.y).getMultiTile(this.level, 0, point.x, point.y);
                if (!multiTile.getTileRectangle(point.x, point.y).contains(tileX, tileY)) continue;
                options.add(new ConfigureOption(Localization.translate("ui", "settlementconfigureworkstation"), () -> this.workForm.openWorkstationConfig(point.x, point.y)));
            }
        }
        for (SettlementWorkZone zone : this.workForm.settlementWorkZones.values()) {
            GameMessage displayName;
            if (zone.isHiddenSetting() || !zone.containsTile(tileX, tileY) || (displayName = zone.getName()) == null) continue;
            tooltips.add(displayName);
            if (!zone.canConfigure()) continue;
            options.add(new ConfigureOption(Localization.translate("ui", "settlementconfigurezone", "name", displayName.translate()), () -> this.workForm.openWorkZoneConfig(zone)));
        }
        if (!options.isEmpty()) {
            if (setCursor != null) {
                setCursor.accept(GameWindow.CURSOR.INTERACT);
            }
            if (options.size() == 1) {
                ConfigureOption first = options.get(0);
                if (Input.lastInputIsController) {
                    tooltips.add(new InputTooltip(ControllerInput.MENU_NEXT, first.str));
                } else {
                    tooltips.add(new InputTooltip(-100, first.str));
                }
            } else if (Input.lastInputIsController) {
                tooltips.add(new InputTooltip(ControllerInput.MENU_NEXT, Localization.translate("ui", "configurebutton")));
            } else {
                tooltips.add(new InputTooltip(-100, Localization.translate("ui", "configurebutton")));
            }
        }
        return options;
    }

    private static class ConfigureOption {
        public final String str;
        public final Runnable onClicked;

        public ConfigureOption(String str, Runnable onClicked) {
            this.str = str;
            this.onClicked = onClicked;
        }
    }
}

