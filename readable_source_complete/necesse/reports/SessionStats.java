/*
 * Decompiled with CFR 0.152.
 */
package necesse.reports;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import necesse.engine.network.client.Client;
import necesse.engine.world.WorldSettings;
import necesse.reports.ReportUtils;

public class SessionStats {
    public final long sessionID;
    public HashMap<String, Integer> stats = new HashMap();

    public SessionStats(long sessionID) {
        this.sessionID = sessionID;
    }

    public void update(Client client) {
        int players;
        if (client == null) {
            this.addSecond("main_menu_seconds");
            return;
        }
        this.addSecond("main_game_seconds");
        WorldSettings worldSettings = client.worldSettings;
        if (worldSettings != null) {
            if (worldSettings.creativeMode) {
                this.addSecond("creative_seconds");
            } else {
                this.addSecond("survival_seconds");
                switch (worldSettings.difficulty) {
                    case CASUAL: {
                        this.addSecond("casual_seconds");
                        break;
                    }
                    case ADVENTURE: {
                        this.addSecond("adventure_seconds");
                        break;
                    }
                    case CLASSIC: {
                        this.addSecond("classic_seconds");
                        break;
                    }
                    case HARD: {
                        this.addSecond("hard_seconds");
                        break;
                    }
                    case BRUTAL: {
                        this.addSecond("brutal_seconds");
                    }
                }
                switch (worldSettings.deathPenalty) {
                    case NONE: {
                        this.addSecond("dp_none_seconds");
                        break;
                    }
                    case DROP_MATS: {
                        this.addSecond("dp_mats_seconds");
                        break;
                    }
                    case DROP_MAIN_INVENTORY: {
                        this.addSecond("dp_main_seconds");
                        break;
                    }
                    case DROP_FULL_INVENTORY: {
                        this.addSecond("dp_full_seconds");
                        break;
                    }
                    case HARDCORE: {
                        this.addSecond("dp_hardcore_seconds");
                    }
                }
            }
        }
        if (client.isSingleplayer()) {
            this.addSecond("singleplayer_seconds");
        } else if (client.getLocalServer() != null) {
            this.addSecond("hosting_seconds");
        } else {
            this.addSecond("multiplayer_seconds");
        }
        if (!client.isSingleplayer() && (players = client.getTotalPlayersConnected()) > 0) {
            if (players == 1) {
                this.addStat("players_1_seconds", 1);
            } else if (players == 2) {
                this.addStat("players_2_seconds", 1);
            } else if (players == 3) {
                this.addStat("players_3_seconds", 1);
            } else if (players == 4) {
                this.addStat("players_4_seconds", 1);
            } else if (players <= 10) {
                this.addStat("players_5_to_10_seconds", 1);
            } else {
                this.addStat("players_10_plus_seconds", 1);
            }
        }
    }

    public void addStat(String key, int value) {
        this.stats.merge(key, value, Integer::sum);
    }

    public void addSecond(String key) {
        this.addStat(key, 1);
    }

    public void addBodyContent(String prefix, Collection<ReportUtils.HTTPContent> contents) {
        for (Map.Entry<String, Integer> entry : this.stats.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            contents.add(new ReportUtils.HTTPStringContent(prefix + key, Integer.toString(value)));
        }
    }

    public LinkedList<ReportUtils.HTTPContent> getBodyContent() {
        LinkedList<ReportUtils.HTTPContent> content = new LinkedList<ReportUtils.HTTPContent>();
        content.add(new ReportUtils.HTTPStringContent("session_id", Long.toString(this.sessionID)));
        this.addBodyContent("", content);
        return content;
    }
}

