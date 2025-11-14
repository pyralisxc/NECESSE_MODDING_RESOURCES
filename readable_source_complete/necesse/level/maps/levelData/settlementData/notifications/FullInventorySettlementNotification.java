/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData.notifications;

import necesse.engine.localization.Localization;
import necesse.engine.network.client.Client;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.gfx.GameBackground;
import necesse.gfx.GameColor;
import necesse.gfx.forms.presets.HelpForms;
import necesse.gfx.gameTooltips.BackgroundedGameTooltips;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.SpacerGameTooltip;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.notifications.SettlementNotification;
import necesse.level.maps.levelData.settlementData.notifications.SettlementNotificationManager;
import necesse.level.maps.levelData.settlementData.settler.SettlerMob;

public class FullInventorySettlementNotification
extends SettlementNotification {
    @Override
    public GameTooltips getTooltip(SettlementNotificationManager.ActiveNotification notification) {
        ListGameTooltips tooltips = new ListGameTooltips();
        tooltips.add(Localization.translate("ui", "notificationfullinv"), 400);
        tooltips.add(new StringTooltips(Localization.translate("ui", "notificationfullinvassign"), GameColor.GRAY, 400));
        tooltips.add(new StringTooltips(Localization.translate("ui", "notificationfullinvnote"), GameColor.GRAY, 400));
        tooltips.add(new SpacerGameTooltip(10));
        this.addSettlerSubmissionsTooltips(tooltips, notification);
        tooltips.add(new SpacerGameTooltip(10));
        tooltips.add(new StringTooltips(Localization.translate("ui", "notificationclickhelp"), GameColor.GRAY, 400));
        return new BackgroundedGameTooltips(tooltips, GameBackground.getItemTooltipBackground());
    }

    @Override
    public void onClicked(Client client, SettlementNotificationManager.ActiveNotification data) {
        HelpForms.openHelpForm("settlerfullinv", new Object[0]);
    }

    @Override
    public boolean isStillValid(SettlerMob settlerMob) {
        if (settlerMob instanceof EntityJobWorker) {
            return ((EntityJobWorker)((Object)settlerMob)).isFullInventoryNotificationStillValid();
        }
        return false;
    }

    @Override
    public boolean isStillValid(ServerSettlementData data) {
        return false;
    }
}

