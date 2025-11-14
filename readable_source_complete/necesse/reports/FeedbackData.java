/*
 * Decompiled with CFR 0.152.
 */
package necesse.reports;

import necesse.reports.BasicsData;

public class FeedbackData
extends BasicsData {
    public FeedbackData(String state) {
        super(state);
    }

    public String generateFullFeedback(String message) {
        String fullFeedback = "";
        fullFeedback = fullFeedback + "Game version: " + (String)this.data.get("game_version") + "\n";
        fullFeedback = fullFeedback + "Steam build: " + (String)this.data.get("steam_build") + "\n";
        fullFeedback = fullFeedback + "Steam name: " + (String)this.data.get("steam_name") + "\n";
        fullFeedback = fullFeedback + "Authentication: " + (String)this.data.get("authentication") + "\n";
        fullFeedback = fullFeedback + "Launch parameters: " + (String)this.data.get("launch_parameters") + "\n";
        fullFeedback = fullFeedback + "Current state: " + (String)this.data.get("game_state") + "\n";
        fullFeedback = fullFeedback + "Current language: " + (String)this.data.get("game_language") + "\n";
        fullFeedback = fullFeedback + "\n";
        fullFeedback = fullFeedback + "Message:\n";
        fullFeedback = fullFeedback + message;
        return fullFeedback;
    }
}

