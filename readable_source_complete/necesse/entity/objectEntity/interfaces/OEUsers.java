/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity.interfaces;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketOEUseUpdate;
import necesse.engine.network.packet.PacketOEUseUpdateFull;
import necesse.engine.network.packet.PacketOEUseUpdateFullRequest;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import necesse.level.maps.regionSystem.SubRegion;

public interface OEUsers {
    default public Users constructUsersObject(long timeoutTime) {
        return new Users(timeoutTime);
    }

    public Users getUsersObject();

    public GameMessage getCanUseError(Mob var1);

    default public void startUser(Mob mob) {
        Level level = mob.getLevel();
        if (level.isClient()) {
            return;
        }
        boolean lastInUse = this.isInUse();
        Users users = this.getUsersObject();
        int mobUniqueID = mob.getUniqueID();
        UserTime last = (UserTime)users.users.get(mobUniqueID);
        long time = mob.getWorldEntity().getTime();
        if (last == null) {
            UserTime userTime = new UserTime(time);
            users.users.put(mobUniqueID, userTime);
            if (level.isServer()) {
                level.getServer().network.sendToClientsWithEntity(new PacketOEUseUpdate(this, mobUniqueID, true), (ObjectEntity)((Object)this));
            }
            this.onUsageChanged(mob, true);
            this.updateUserPosition(mob);
            userTime.hasTriggeredMobUsageChanged = true;
            if (!lastInUse) {
                this.onIsInUseChanged(true);
            }
        } else {
            last.refreshTime = time;
        }
    }

    default public void stopUser(Mob mob) {
        Level level = mob.getLevel();
        if (level == null || level.isClient()) {
            return;
        }
        int mobUniqueID = mob.getUniqueID();
        UserTime last = (UserTime)this.getUsersObject().users.remove(mobUniqueID);
        if (last != null) {
            this.onUsageChanged(mob, false);
            if (level.isServer()) {
                level.getServer().network.sendToClientsWithEntity(new PacketOEUseUpdate(this, mobUniqueID, false), (ObjectEntity)((Object)this));
            }
            if (!this.isInUse()) {
                this.onIsInUseChanged(false);
            }
        }
    }

    default public boolean isMobUsing(Mob mob) {
        Users users = this.getUsersObject();
        return users.users.containsKey(mob.getUniqueID());
    }

    default public boolean isClientUsing(ServerClient client) {
        return client.playerMob != null && this.isMobUsing(client.playerMob);
    }

    default public boolean isInUse() {
        return !this.getUsersObject().users.isEmpty();
    }

    default public int getTotalUsers() {
        return this.getUsersObject().users.size();
    }

    default public Collection<Integer> getUserUniqueIDs() {
        return this.getUsersObject().users.keySet();
    }

    default public Stream<Mob> streamUsers(Level level) {
        return this.getUserUniqueIDs().stream().map(mobUniqueID -> GameUtils.getLevelMob(mobUniqueID, level, false)).filter(Objects::nonNull);
    }

    default public void submitUpdatePacket(ObjectEntity entity, PacketOEUseUpdateFull packet) {
        this.getUsersObject().readUsersSpawnPacket(new PacketReader(packet.content), entity);
    }

    default public void submitUpdatePacket(ObjectEntity entity, PacketOEUseUpdate packet) {
        Level level = entity.getLevel();
        boolean lastInUse = this.isInUse();
        Users users = this.getUsersObject();
        if (packet.isUsing) {
            UserTime userTime = new UserTime(level.getWorldEntity().getTime());
            users.users.put(packet.mobUniqueID, userTime);
            Mob mob = GameUtils.getLevelMob(packet.mobUniqueID, level, false);
            if (mob != null && mob.getLevel() != null) {
                this.onUsageChanged(mob, true);
                this.updateUserPosition(mob);
                userTime.hasTriggeredMobUsageChanged = true;
            }
        } else {
            users.users.remove(packet.mobUniqueID);
            Mob mob = GameUtils.getLevelMob(packet.mobUniqueID, level, false);
            if (mob != null && mob.getLevel() != null) {
                this.onUsageChanged(mob, false);
            } else {
                this.onUnknownUsageStopped(packet.mobUniqueID);
            }
        }
        boolean newIsInUse = this.isInUse();
        if (lastInUse != newIsInUse) {
            this.onIsInUseChanged(newIsInUse);
        }
        if (packet.totalUsers != this.getUsersObject().users.size() && entity.isClient()) {
            entity.getLevel().getClient().network.sendPacket(new PacketOEUseUpdateFullRequest(this));
        }
    }

    default public void onUsageChanged(Mob mob, boolean using) {
        ObjectEntity objectEntity;
        if (this instanceof ObjectEntity && (objectEntity = (ObjectEntity)((Object)this)).isClient()) {
            objectEntity.getObject().playInteractSound(objectEntity.getLevel(), mob, objectEntity.tileX, objectEntity.tileY, this.getTotalUsers() == (using ? 1 : 0), !using);
        }
    }

    public void onIsInUseChanged(boolean var1);

    default public void onUnknownUsageStopped(int uniqueID) {
    }

    default public void updateUserPosition(Mob mob) {
    }

    default public void updateUserToExitPos(Mob mob, float dirX, float dirY) {
    }

    public static Point findExitPos(LevelObject levelObject, Mob mob, float dirX, float dirY) {
        return OEUsers.findExitPos(levelObject, mob, dirX, dirY, null);
    }

    public static Point findExitPos(LevelObject levelObject, Mob mob, float dirX, float dirY, Function<Comparator<Point>, Comparator<Point>> comparatorModifier) {
        ArrayList<Point> possibleSpawns = new ArrayList<Point>();
        for (Point tile : levelObject.getMultiTile().getAdjacentTiles(levelObject.tileX, levelObject.tileY, true)) {
            int posX = tile.x * 32 + 16;
            int posY = tile.y * 32 + 16;
            if (mob.collidesWith(levelObject.level, posX, posY)) continue;
            possibleSpawns.add(new Point(posX, posY));
        }
        Point2D.Float distancePoint = new Point2D.Float((float)(levelObject.tileX * 32 + 16) + dirX * 16.0f, (float)(levelObject.tileY * 32 + 16) + dirY * 16.0f);
        Comparator<Point> comparator = Comparator.comparingDouble(p -> p.distance(distancePoint.x, distancePoint.y));
        if (comparatorModifier != null) {
            comparator = comparatorModifier.apply(comparator);
        }
        Comparator<Point> regionSizeComparator = Comparator.comparingInt(p -> {
            int tileY;
            int tileX = GameMath.getTileCoordinate(p.x);
            SubRegion sr = levelObject.level.regionManager.getSubRegionByTile(tileX, tileY = GameMath.getTileCoordinate(p.y));
            if (sr == null || sr.getType().isSolid) {
                return 200;
            }
            int sameConnectedRegionSize = OEUsers.getConnectedRegionSize(sr, 100, sr1 -> !sr1.getType().isSolid);
            return 100 - sameConnectedRegionSize;
        });
        Comparator<Point> finalComparator = dirX == 0.0f && dirY == 0.0f ? regionSizeComparator.thenComparing(comparator) : comparator.thenComparing(regionSizeComparator);
        return possibleSpawns.stream().min(finalComparator).orElse(null);
    }

    public static int getConnectedRegionSize(SubRegion start, int maxSize, Predicate<SubRegion> validCheck) {
        return OEUsers.getConnectedRegionSize(new HashSet<SubRegion>(), start, 0, maxSize, validCheck);
    }

    public static int getConnectedRegionSize(HashSet<SubRegion> set, SubRegion current, int currentSize, int maxSize, Predicate<SubRegion> validCheck) {
        if (set.contains(current)) {
            return currentSize;
        }
        if ((currentSize += current.size()) >= maxSize) {
            return maxSize;
        }
        set.add(current);
        for (SubRegion adjacentRegion : current.getAdjacentRegions()) {
            if (!validCheck.test(adjacentRegion) || (currentSize = OEUsers.getConnectedRegionSize(set, adjacentRegion, currentSize, maxSize, validCheck)) < maxSize) continue;
            return maxSize;
        }
        return currentSize;
    }

    public static class Users {
        private final HashMap<Integer, UserTime> users = new HashMap();
        private final long timeoutTime;

        public Users(long timeoutTime) {
            this.timeoutTime = timeoutTime;
        }

        public void serverTick(ObjectEntity entity) {
            if (this.timeoutTime <= -1L || this.users.isEmpty()) {
                return;
            }
            Level level = entity.getLevel();
            OEUsers oeUsers = (OEUsers)((Object)entity);
            long time = entity.getWorldEntity().getTime();
            LinkedList<Integer> removes = new LinkedList<Integer>();
            for (Map.Entry<Integer, UserTime> e : this.users.entrySet()) {
                UserTime user = e.getValue();
                if (user.refreshTime + this.timeoutTime >= time) continue;
                removes.add(e.getKey());
            }
            Iterator<Map.Entry<Integer, UserTime>> iterator = removes.iterator();
            while (iterator.hasNext()) {
                int mobUniqueID = (Integer)((Object)iterator.next());
                this.users.remove(mobUniqueID);
                Mob mob = GameUtils.getLevelMob(mobUniqueID, level, false);
                if (mob != null) {
                    oeUsers.onUsageChanged(mob, false);
                } else {
                    oeUsers.onUnknownUsageStopped(mobUniqueID);
                }
                if (!level.isServer()) continue;
                level.getServer().network.sendToClientsWithEntity(new PacketOEUseUpdate(oeUsers, mobUniqueID, false), entity);
            }
            if (!removes.isEmpty() && !oeUsers.isInUse()) {
                oeUsers.onIsInUseChanged(false);
            }
        }

        public void clientTick(ObjectEntity entity) {
            for (Map.Entry<Integer, UserTime> e : this.users.entrySet()) {
                UserTime userTime = e.getValue();
                if (userTime.hasTriggeredMobUsageChanged) continue;
                OEUsers oeUsers = (OEUsers)((Object)entity);
                Mob mob = GameUtils.getLevelMob(e.getKey(), entity.getLevel(), false);
                if (mob == null) continue;
                oeUsers.onUsageChanged(mob, true);
                oeUsers.updateUserPosition(mob);
                userTime.hasTriggeredMobUsageChanged = true;
            }
        }

        public void writeUsersSpawnPacket(PacketWriter writer) {
            writer.putNextShortUnsigned(this.users.size());
            for (int mobUniqueID : this.users.keySet()) {
                writer.putNextInt(mobUniqueID);
            }
        }

        public void readUsersSpawnPacket(PacketReader reader, ObjectEntity entity) {
            Level level = entity.getLevel();
            OEUsers oeUsers = (OEUsers)((Object)entity);
            boolean lastInUse = oeUsers.isInUse();
            int size = reader.getNextShortUnsigned();
            HashSet<Integer> userUniqueIDs = new HashSet<Integer>();
            for (int i = 0; i < size; ++i) {
                userUniqueIDs.add(reader.getNextInt());
            }
            LinkedList<Integer> removes = new LinkedList<Integer>();
            for (int mobUniqueID : this.users.keySet()) {
                if (userUniqueIDs.contains(mobUniqueID)) continue;
                removes.add(mobUniqueID);
            }
            for (int mobUniqueID : removes) {
                this.users.remove(mobUniqueID);
                Mob mob = level.entityManager.mobs.get(mobUniqueID, false);
                if (mob != null) {
                    oeUsers.onUsageChanged(mob, false);
                    continue;
                }
                oeUsers.onUnknownUsageStopped(mobUniqueID);
            }
            for (int mobUniqueID : userUniqueIDs) {
                if (this.users.containsKey(mobUniqueID)) continue;
                UserTime userTime = new UserTime(level.getWorldEntity().getTime());
                this.users.put(mobUniqueID, userTime);
                Mob mob = GameUtils.getLevelMob(mobUniqueID, level, false);
                if (mob == null) continue;
                oeUsers.onUsageChanged(mob, true);
                userTime.hasTriggeredMobUsageChanged = true;
            }
            boolean newIsInUse = oeUsers.isInUse();
            if (lastInUse != newIsInUse) {
                oeUsers.onIsInUseChanged(newIsInUse);
            }
        }

        public void onRemoved(ObjectEntity entity) {
            OEUsers oeUsers = (OEUsers)((Object)entity);
            Level level = entity.getLevel();
            for (Integer mobUniqueID : this.users.keySet()) {
                Mob mob = GameUtils.getLevelMob(mobUniqueID, level, false);
                if (mob == null) {
                    oeUsers.onUnknownUsageStopped(mobUniqueID);
                    continue;
                }
                oeUsers.onUsageChanged(mob, false);
            }
            if (!this.users.isEmpty()) {
                this.users.clear();
                oeUsers.onIsInUseChanged(false);
            }
        }
    }

    public static class UserTime {
        public final long startUsageTime;
        public long refreshTime;
        public boolean hasTriggeredMobUsageChanged;

        public UserTime(long usageTime) {
            this.startUsageTime = this.refreshTime;
            this.refreshTime = usageTime;
        }
    }
}

