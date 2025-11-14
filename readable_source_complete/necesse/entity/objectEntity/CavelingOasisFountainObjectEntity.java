/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import java.awt.Point;
import necesse.engine.journal.JournalChallenge;
import necesse.engine.journal.JournalChallengeUtils;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.JournalChallengeRegistry;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.FountainObjectEntity;
import necesse.level.maps.Level;

public class CavelingOasisFountainObjectEntity
extends FountainObjectEntity {
    public CavelingOasisFountainObjectEntity(Level level, int x, int y) {
        super(level, "cavelingoasisfountain", x, y);
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (this.getLevel().tickManager().isGameTickInSecond(3)) {
            Level level = this.getLevel();
            if (!level.objectLayer.isPlayerPlaced(this.tileX, this.tileY) && level.isCave && JournalChallengeUtils.isDesertBiome(level.getBiome(this.tileX, this.tileY))) {
                int tileRange = 6;
                Point centerPos = level.getObject(this.tileX, this.tileY).getMultiTile(level.getObjectRotation(this.tileX, this.tileY)).getCenterLevelPos(this.tileX, this.tileY);
                level.entityManager.players.streamAreaTileRange(centerPos.x, centerPos.y, tileRange).filter(PlayerMob::isServerClient).filter(p -> GameMath.diagonalMoveDistance(centerPos.x, centerPos.y, p.getX(), p.getY()) <= (double)(tileRange * 32)).map(PlayerMob::getServerClient).forEach(client -> {
                    JournalChallenge challenge = JournalChallengeRegistry.getChallenge(JournalChallengeRegistry.FIND_CAVELING_OASIS_ID);
                    if (!challenge.isCompleted((ServerClient)client) && challenge.isJournalEntryDiscovered((ServerClient)client)) {
                        challenge.markCompleted((ServerClient)client);
                        client.forceCombineNewStats();
                    }
                });
            }
        }
    }
}

