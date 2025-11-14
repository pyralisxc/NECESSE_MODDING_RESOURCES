/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps;

import java.util.stream.Stream;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.WorldEntity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.hostile.bosses.ArenaEntrancePortalMob;
import necesse.level.maps.Level;
import necesse.level.maps.incursion.IncursionData;
import necesse.level.maps.incursion.SettlementRuinsIncursionLevel;
import necesse.level.maps.regionSystem.Region;

public class AscendedVoidLevel
extends Level {
    private IncursionData incursionData;
    private ArenaEntrancePortalMob arenaPortal;

    public AscendedVoidLevel(LevelIdentifier identifier, int width, int height, WorldEntity worldEntity) {
        super(identifier, width, height, worldEntity);
        this.keepTrackOfReturnedItems = true;
        this.isProtected = true;
        this.lightManager.ambientLightOverride = this.lightManager.newLight(150.0f);
        this.baseBiome = BiomeRegistry.ASCENDED_VOID;
    }

    public AscendedVoidLevel(LevelIdentifier identifier, int width, int height, WorldEntity worldEntity, IncursionData incursionData, ArenaEntrancePortalMob arenaPortal) {
        this(identifier, width, height, worldEntity);
        this.incursionData = incursionData;
        this.arenaPortal = arenaPortal;
    }

    @Override
    public void writeLevelDataPacket(PacketWriter writer) {
        super.writeLevelDataPacket(writer);
        if (this.incursionData != null) {
            writer.putNextBoolean(true);
            IncursionData.writePacket(this.incursionData, writer);
        } else {
            writer.putNextBoolean(false);
        }
    }

    @Override
    public void readLevelDataPacket(PacketReader reader) {
        super.readLevelDataPacket(reader);
        this.incursionData = reader.getNextBoolean() ? IncursionData.fromPacket(reader) : null;
    }

    @Override
    public void clientTick() {
        super.clientTick();
        SettlementRuinsIncursionLevel.updateSceneShade(this);
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (this.arenaPortal != null) {
            this.arenaPortal.keepAlive();
        }
    }

    @Override
    public void generateRegion(Region region) {
        super.generateRegion(region);
        for (int regionTileX = 0; regionTileX < region.tileLayer.region.tileWidth; ++regionTileX) {
            for (int regionTileY = 0; regionTileY < region.tileLayer.region.tileHeight; ++regionTileY) {
                region.tileLayer.setTileByRegion(regionTileX, regionTileY, TileRegistry.ascendedVoidID);
            }
        }
    }

    @Override
    public Stream<ModifierValue<?>> getDefaultLevelModifiers() {
        if (this.incursionData == null) {
            return super.getDefaultLevelModifiers();
        }
        return Stream.concat(super.getDefaultLevelModifiers(), this.incursionData.getDefaultLevelModifiers());
    }

    @Override
    public Stream<ModifierValue<?>> getMobModifiers(Mob mob) {
        Stream<ModifierValue<?>> out = super.getMobModifiers(mob);
        if (this.incursionData != null) {
            out = Stream.concat(out, this.incursionData.getMobModifiers(mob));
        }
        return out;
    }

    @Override
    public boolean shouldSave() {
        return false;
    }

    @Override
    public void onUnloading() {
        super.onUnloading();
        if (this.arenaPortal != null && !this.arenaPortal.removed()) {
            this.arenaPortal.remove();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        if (this.arenaPortal != null && !this.arenaPortal.removed()) {
            this.arenaPortal.remove();
        }
    }
}

