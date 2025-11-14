/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.events;

import java.util.ArrayList;
import java.util.Comparator;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.friendly.human.humanShop.StylistHumanMob;
import necesse.inventory.container.events.ContainerEvent;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.LevelSettler;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.settler.SettlerMob;

public class StylistSettlersUpdateContainerEvent
extends ContainerEvent {
    public final ArrayList<Integer> mobUniqueIDs;

    public StylistSettlersUpdateContainerEvent(StylistHumanMob stylistHumanMob, ServerClient client) {
        ServerSettlementData settlement = stylistHumanMob.getSettlerSettlementServerData();
        if (settlement != null) {
            if (settlement.networkData.doesClientHaveAccess(client)) {
                this.mobUniqueIDs = new ArrayList();
                for (LevelSettler settler : settlement.getSettlers()) {
                    SettlerMob mob = settler.getMob();
                    if (!(mob instanceof HumanMob)) continue;
                    HumanMob humanMob = (HumanMob)mob;
                    if (humanMob.look == null) continue;
                    this.mobUniqueIDs.add(humanMob.getUniqueID());
                }
            } else {
                this.mobUniqueIDs = null;
            }
        } else {
            this.mobUniqueIDs = null;
        }
    }

    public StylistSettlersUpdateContainerEvent(PacketReader reader) {
        super(reader);
        if (reader.getNextBoolean()) {
            int size = reader.getNextShortUnsigned();
            this.mobUniqueIDs = new ArrayList(size);
            for (int i = 0; i < size; ++i) {
                int uniqueID = reader.getNextInt();
                this.mobUniqueIDs.add(uniqueID);
            }
        } else {
            this.mobUniqueIDs = null;
        }
    }

    @Override
    public void write(PacketWriter writer) {
        if (this.mobUniqueIDs != null) {
            writer.putNextBoolean(true);
            writer.putNextShortUnsigned(this.mobUniqueIDs.size());
            for (Integer uniqueID : this.mobUniqueIDs) {
                writer.putNextInt(uniqueID);
            }
        } else {
            writer.putNextBoolean(false);
        }
    }

    public ArrayList<HumanMob> getHumanMobs(Level level) {
        if (this.mobUniqueIDs == null) {
            return null;
        }
        ArrayList<HumanMob> humanMobs = new ArrayList<HumanMob>();
        for (Integer mobUniqueID : this.mobUniqueIDs) {
            Mob mob = level.entityManager.mobs.get(mobUniqueID, false);
            if (!(mob instanceof HumanMob)) continue;
            humanMobs.add((HumanMob)mob);
        }
        humanMobs.sort(Comparator.comparing(Mob::getDisplayName));
        return humanMobs;
    }
}

