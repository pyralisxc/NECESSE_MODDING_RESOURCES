/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData.notifications;

import necesse.engine.localization.Localization;
import necesse.engine.network.client.Client;
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

public class UnhappySettlementNotification
extends SettlementNotification {
    @Override
    public GameTooltips getTooltip(SettlementNotificationManager.ActiveNotification notification) {
        ListGameTooltips tooltips = new ListGameTooltips();
        tooltips.add(Localization.translate("ui", "notificationunhappy"), 400);
        tooltips.add(new StringTooltips(Localization.translate("ui", "notificationunhappynote"), GameColor.GRAY, 400));
        tooltips.add(new SpacerGameTooltip(10));
        this.addSettlerSubmissionsTooltips(tooltips, notification);
        tooltips.add(new SpacerGameTooltip(10));
        tooltips.add(new StringTooltips(Localization.translate("ui", "notificationclickhelp"), GameColor.GRAY, 400));
        return new BackgroundedGameTooltips(tooltips, GameBackground.getItemTooltipBackground());
    }

    @Override
    public void onClicked(Client client, SettlementNotificationManager.ActiveNotification data) {
        HelpForms.openHelpForm("settlerunhappy", new Object[0]);
    }

    @Override
    public boolean isStillValid(SettlerMob settlerMob) {
        return settlerMob.isSettler() && settlerMob.getSettlerHappiness() < 50;
    }

    @Override
    public boolean isStillValid(ServerSettlementData data) {
        return false;
    }
}

