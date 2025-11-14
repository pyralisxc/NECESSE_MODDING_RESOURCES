/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs;

import java.util.function.Consumer;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketOEUseUpdateFullRequest;
import necesse.engine.network.packet.PacketPlayerMovement;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ObjectUserActive;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEUsers;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.level.gameObject.ObjectUsersObject;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;

public interface ObjectUserMob {
    public void setUsingObject(ObjectUserActive var1);

    public ObjectUserActive getUsingObject();

    public void clearUsingObject();

    default public void clearUsingObject(Level level, int tileX, int tileY) {
        ObjectUserActive obj = this.getUsingObject();
        if (obj != null && obj.level.isSamePlace(level) && obj.tileX == tileX && obj.tileY == tileY) {
            this.clearUsingObject();
        }
    }

    public DrawOptions getUserDrawOptions(Level var1, int var2, int var3, TickManager var4, GameCamera var5, PlayerMob var6, Consumer<HumanDrawOptions> var7);

    default public void writeObjectUserPacket(PacketWriter writer) {
        ObjectUserActive usingObject = this.getUsingObject();
        if (usingObject != null) {
            writer.putNextBoolean(true);
            writer.putNextInt(usingObject.tileX);
            writer.putNextInt(usingObject.tileY);
        } else {
            writer.putNextBoolean(false);
        }
    }

    default public boolean readObjectUserPacket(PacketReader reader) {
        boolean isUsing = reader.getNextBoolean();
        ObjectUserActive last = this.getUsingObject();
        if (isUsing) {
            int tileX = reader.getNextInt();
            int tileY = reader.getNextInt();
            if (last == null || last.tileX != tileX || last.tileY != tileY) {
                Mob mob = (Mob)((Object)this);
                Level level = mob.getLevel();
                if (level != null) {
                    if (level.isServer() && mob.isPlayer) {
                        ServerClient client = ((PlayerMob)mob).getServerClient();
                        client.sendPacket(new PacketPlayerMovement(client, true));
                    } else {
                        ObjectEntity oe = level.entityManager.getObjectEntity(tileX, tileY);
                        if (oe instanceof OEUsers) {
                            OEUsers oeUsers = (OEUsers)((Object)oe);
                            if (oeUsers.isMobUsing(mob)) {
                                oeUsers.onUsageChanged(mob, true);
                                oeUsers.updateUserPosition(mob);
                            } else if (level.isClient()) {
                                level.getClient().network.sendPacket(new PacketOEUseUpdateFullRequest(oeUsers));
                            }
                        }
                    }
                }
                return true;
            }
        } else if (last != null) {
            Mob mob = (Mob)((Object)this);
            Level level = mob.getLevel();
            if (level != null && level.isServer() && mob.isPlayer) {
                ServerClient client = ((PlayerMob)mob).getServerClient();
                client.sendPacket(new PacketPlayerMovement(client, true));
            } else {
                last.stopUsing();
                this.clearUsingObject();
            }
            return true;
        }
        return false;
    }

    default public ObjectUserActive startUsingObject(LevelObject object, ObjectUsersObject usersObject, final OEUsers oeUsers) {
        ObjectUserActive out = new ObjectUserActive(object.level, object.tileX, object.tileY, usersObject){

            @Override
            public void keepUsing() {
                oeUsers.startUser(this.mob());
            }
        };
        this.setUsingObject(out);
        ((Mob)((Object)this)).dismount();
        return out;
    }
}

