/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.job;

import java.util.Comparator;
import java.util.TreeSet;
import java.util.function.Supplier;
import necesse.engine.Settings;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.localization.message.LocalMessage;
import necesse.gfx.ui.ButtonIcon;

public class JobPriority {
    public static TreeSet<JobPriority> priorities = new TreeSet<JobPriority>(Comparator.comparingInt(p -> -p.priority));
    public final int priority;
    public GameMessage displayName;
    public String iconString;
    public Supplier<ButtonIcon> icon;

    public static JobPriority getJobPriority(int priority) {
        for (JobPriority value : priorities) {
            if (priority < value.priority) continue;
            return value;
        }
        return priorities.last();
    }

    public JobPriority(int priority, GameMessage displayName, String iconString, Supplier<ButtonIcon> icon) {
        this.priority = priority;
        this.displayName = displayName;
        this.iconString = iconString;
        this.icon = icon;
    }

    public GameMessage getFullDisplayName() {
        return new GameMessageBuilder().append(this.iconString == null || this.iconString.isEmpty() ? "" : this.iconString + " ").append(this.displayName);
    }

    static {
        priorities.add(new JobPriority(300, new LocalMessage("ui", "prioritytop"), "+++", () -> Settings.UI.priority_top));
        priorities.add(new JobPriority(200, new LocalMessage("ui", "priorityhigher"), "++", () -> Settings.UI.priority_higher));
        priorities.add(new JobPriority(100, new LocalMessage("ui", "priorityhigh"), "+", () -> Settings.UI.priority_high));
        priorities.add(new JobPriority(0, new LocalMessage("ui", "prioritynormal"), "", () -> Settings.UI.priority_normal));
        priorities.add(new JobPriority(-100, new LocalMessage("ui", "prioritylow"), "-", () -> Settings.UI.priority_low));
        priorities.add(new JobPriority(-200, new LocalMessage("ui", "prioritylower"), "--", () -> Settings.UI.priority_lower));
        priorities.add(new JobPriority(-300, new LocalMessage("ui", "prioritylast"), "---", () -> Settings.UI.priority_last));
    }
}

