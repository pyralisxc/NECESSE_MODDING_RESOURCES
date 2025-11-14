/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData.notifications;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import necesse.engine.localization.Localization;
import necesse.engine.network.client.Client;
import necesse.engine.registries.IDData;
import necesse.gfx.GameColor;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.notifications.SettlementNotificationManager;
import necesse.level.maps.levelData.settlementData.settler.SettlerMob;

public abstract class SettlementNotification {
    public final IDData idData = new IDData();

    public final int getID() {
        return this.idData.getID();
    }

    public String getStringID() {
        return this.idData.getStringID();
    }

    public abstract GameTooltips getTooltip(SettlementNotificationManager.ActiveNotification var1);

    public void addSettlerSubmissionsTooltips(ListGameTooltips tooltips, SettlementNotificationManager.ActiveNotification notification) {
        tooltips.add(new StringTooltips(Localization.translate("ui", "notificationsettlers"), GameColor.GRAY));
        for (SettlerMob settlerMob : this.getSubmitterMobs(notification, false)) {
            tooltips.add(new StringTooltips("- " + settlerMob.getMob().getDisplayName(), GameColor.GRAY));
        }
    }

    public List<SettlerMob> getSubmitterMobs(SettlementNotificationManager.ActiveNotification notification, boolean addNotFoundNulls) {
        Collection<SettlementNotificationManager.SettlerSubmission> submissions = notification.getSettlerSubmissions();
        ArrayList<SettlerMob> out = new ArrayList<SettlerMob>(submissions.size());
        for (SettlementNotificationManager.SettlerSubmission submission : submissions) {
            SettlerMob settlerMob = submission.getMob();
            if (settlerMob != null) {
                out.add(settlerMob);
                continue;
            }
            if (!addNotFoundNulls) continue;
            out.add(null);
        }
        return out;
    }

    public abstract void onClicked(Client var1, SettlementNotificationManager.ActiveNotification var2);

    public abstract boolean isStillValid(SettlerMob var1);

    public abstract boolean isStillValid(ServerSettlementData var1);
}

