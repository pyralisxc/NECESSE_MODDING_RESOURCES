/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs;

import java.util.Comparator;
import java.util.HashMap;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ability.MobAbility;

public class ActivityDescription {
    public final Mob mob;
    public static int activityTimeoutMS = 500;
    protected HashMap<String, Activity> currentActivities = new HashMap();
    protected GameLinkedList<Activity> priorities = new GameLinkedList();
    protected GameLinkedList<Activity> timeouts = new GameLinkedList();
    protected MobSetActivityAbility setActivityAbility;
    protected GameMessage currentActivity;

    public ActivityDescription(Mob mob) {
        this.mob = mob;
        this.setActivityAbility = mob.registerAbility(new MobSetActivityAbility());
    }

    public void writeSpawnPacket(PacketWriter writer) {
        writer.putNextBoolean(this.currentActivity != null);
        if (this.currentActivity != null) {
            this.currentActivity.writePacket(writer);
        }
    }

    public void readSpawnPacket(PacketReader reader) {
        this.currentActivity = reader.getNextBoolean() ? GameMessage.fromPacket(reader) : null;
    }

    public void serverTick() {
        GameMessage last = this.currentActivity;
        this.refreshCurrentActive();
        GameMessage next = this.currentActivity;
        if (!GameMessage.isSame(last, next) && this.mob.isServer()) {
            this.setActivityAbility.updateActivity(next);
        }
    }

    public void refreshCurrentActive() {
        while (!this.timeouts.isEmpty() && this.timeouts.getFirst().shouldTimeout()) {
            this.timeouts.getFirst().remove();
        }
        Activity first = this.priorities.getFirst();
        this.currentActivity = first != null ? first.description : null;
    }

    public void setActivity(String type, int priority, GameMessage description) {
        this.currentActivities.compute(type, (s, last) -> {
            if (last == null) {
                return new Activity(type, priority, activityTimeoutMS, description);
            }
            last.refreshTimeoutTime(activityTimeoutMS);
            last.setPriority(priority);
            last.description = description;
            return last;
        });
    }

    public void clearActivity(String type) {
        Activity current = this.currentActivities.get(type);
        if (current != null) {
            current.remove();
        }
    }

    public GameMessage getCurrentActivity() {
        return this.currentActivity;
    }

    private class MobSetActivityAbility
    extends MobAbility {
        public void updateActivity(GameMessage description) {
            Packet packet = new Packet();
            PacketWriter writer = new PacketWriter(packet);
            writer.putNextBoolean(description != null);
            if (description != null) {
                description.writePacket(writer);
            }
            this.runAndSendAbility(packet);
        }

        @Override
        public void executePacket(PacketReader reader) {
            ActivityDescription.this.currentActivity = reader.getNextBoolean() ? GameMessage.fromPacket(reader) : null;
        }
    }

    private class Activity {
        public final String type;
        public int priority;
        public long timeoutLocalTime;
        public GameLinkedList.Element priorityElement;
        public GameLinkedList.Element timeoutElement;
        public GameMessage description;

        public Activity(String type, int priority, int timeoutMS, GameMessage description) {
            this.type = type;
            this.description = description;
            this.setPriority(priority);
            this.refreshTimeoutTime(timeoutMS);
        }

        public void setPriority(int priority) {
            if (this.priority == priority && this.priorityElement != null) {
                return;
            }
            if (this.priorityElement != null) {
                this.priorityElement.remove();
            }
            this.priorityElement = GameUtils.insertSortedList(ActivityDescription.this.priorities, this, Comparator.comparingInt(p -> -p.priority));
        }

        public void refreshTimeoutTime(int timeoutMS) {
            long nextTimeoutTime = ActivityDescription.this.mob.getWorldEntity().getLocalTime() + (long)timeoutMS;
            if (this.timeoutLocalTime == nextTimeoutTime && this.timeoutElement != null) {
                return;
            }
            this.timeoutLocalTime = nextTimeoutTime;
            if (this.timeoutElement != null) {
                this.timeoutElement.remove();
            }
            this.timeoutElement = GameUtils.insertSortedListReversed(ActivityDescription.this.timeouts, this, Comparator.comparingLong(p -> p.timeoutLocalTime));
        }

        public boolean shouldTimeout() {
            return ActivityDescription.this.mob.getWorldEntity().getLocalTime() >= this.timeoutLocalTime;
        }

        public void remove() {
            if (this.priorityElement != null) {
                this.priorityElement.remove();
                this.priorityElement = null;
            }
            if (this.timeoutElement != null) {
                this.timeoutElement.remove();
                this.timeoutElement = null;
            }
            ActivityDescription.this.currentActivities.remove(this.type);
        }
    }
}

