/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs;

import java.util.ArrayList;
import java.util.Arrays;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;

public class DeathMessageTable {
    private final ArrayList<GameMessage> messages = new ArrayList();

    public GameMessage getRandomDeathMessage(GameRandom random) {
        if (this.messages.isEmpty()) {
            return null;
        }
        return this.messages.get(random.nextInt(this.messages.size()));
    }

    public DeathMessageTable add(GameMessage ... messages) {
        this.messages.addAll(Arrays.asList(messages));
        return this;
    }

    public DeathMessageTable add(String ... keys) {
        for (String key : keys) {
            this.messages.add(new LocalMessage("deaths", key));
        }
        return this;
    }

    public DeathMessageTable addRange(String key, int prefixMin, int prefixMax) {
        for (int i = prefixMin; i <= prefixMax; ++i) {
            this.messages.add(new LocalMessage("deaths", key + i));
        }
        return this;
    }

    public DeathMessageTable addRange(String key, int count) {
        return this.addRange(key, 1, count);
    }

    public static DeathMessageTable oneOf(String ... keys) {
        DeathMessageTable out = new DeathMessageTable();
        out.add(keys);
        return out;
    }

    public static DeathMessageTable fromRange(String key, int prefixMin, int prefixMax) {
        DeathMessageTable out = new DeathMessageTable();
        out.addRange(key, prefixMin, prefixMax);
        return out;
    }

    public static DeathMessageTable fromRange(String key, int count) {
        DeathMessageTable out = new DeathMessageTable();
        out.addRange(key, count);
        return out;
    }

    public static GameMessage getDeathMessage(Attacker attacker, GameMessage victimName) {
        DeathMessageTable deathMessages;
        GameMessage deathMessage = null;
        if (attacker != null && (deathMessages = attacker.getDeathMessages()) != null) {
            deathMessage = deathMessages.getRandomDeathMessage(GameRandom.globalRandom);
        }
        if (deathMessage == null) {
            deathMessage = attacker != null ? DeathMessageTable.fromRange("generic", 8).getRandomDeathMessage(GameRandom.globalRandom) : new LocalMessage("deaths", "default");
        }
        DeathMessageTable.formatDeathMessage(deathMessage, attacker, victimName);
        return deathMessage;
    }

    private static void formatDeathMessage(GameMessage deathMessage, Attacker attacker, GameMessage victimName) {
        if (deathMessage instanceof LocalMessage) {
            ((LocalMessage)deathMessage).addReplacement("victim", victimName);
            if (attacker != null) {
                ((LocalMessage)deathMessage).addReplacement("attacker", attacker.getAttackerName());
            }
        }
    }
}

