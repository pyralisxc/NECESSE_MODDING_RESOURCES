/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.friendly.human;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;

public class HappinessModifier {
    public static HappinessModifier noBedModifier = new HappinessModifier(-40, new LocalMessage("settlement", "nobed"));
    public static HappinessModifier bedOutsideModifier = new HappinessModifier(-30, new LocalMessage("settlement", "bedoutside"));
    public final int happiness;
    public final GameMessage description;

    public HappinessModifier(int happiness, GameMessage description) {
        this.happiness = happiness;
        this.description = description;
    }

    public HappinessModifier(PacketReader reader) {
        this.happiness = reader.getNextInt();
        this.description = GameMessage.fromPacket(reader);
    }

    public void writePacket(PacketWriter writer) {
        writer.putNextInt(this.happiness);
        this.description.writePacket(writer);
    }
}

