/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.gameNetworkData;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.entity.mobs.GameDamage;

public class GNDItemGameDamage
extends GNDItem {
    public GameDamage damage;

    public GNDItemGameDamage(GameDamage damage) {
        this.damage = damage;
    }

    public GNDItemGameDamage(PacketReader reader) {
        this.readPacket(reader);
    }

    public GNDItemGameDamage(LoadData data) {
        this.damage = GameDamage.fromLoadData(data.getFirstLoadDataByName("value"));
    }

    @Override
    public String toString() {
        if (this.damage == null) {
            return "damage{null}";
        }
        return "damage{" + this.damage.type + ", " + this.damage.damage + ", " + this.damage.armorPen + ", " + this.damage.baseCritChance + ", " + this.damage.playerDamageMultiplier + "}";
    }

    @Override
    public boolean isDefault() {
        return this.damage == null;
    }

    @Override
    public boolean equals(GNDItem item) {
        if (item instanceof GNDItemGameDamage) {
            GNDItemGameDamage other = (GNDItemGameDamage)item;
            return this == item || this.damage.equals(other.damage);
        }
        return false;
    }

    @Override
    public GNDItemGameDamage copy() {
        return new GNDItemGameDamage(this.damage);
    }

    @Override
    public void addSaveData(SaveData data) {
        SaveData save = new SaveData("value");
        this.damage.addSaveData(save);
        data.addSaveData(save);
    }

    @Override
    public void writePacket(PacketWriter writer) {
        this.damage.writePacket(writer);
    }

    @Override
    public void readPacket(PacketReader reader) {
        this.damage = GameDamage.fromReader(reader);
    }
}

