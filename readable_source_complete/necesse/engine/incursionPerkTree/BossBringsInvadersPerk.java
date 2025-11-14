/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.incursionPerkTree;

import necesse.engine.incursionPerkTree.IncursionPerk;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.TicketSystemList;
import necesse.entity.levelEvent.WaitForSecondsEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.hostile.bosses.ChieftainGauntletSpawnerPortalMob;
import necesse.entity.mobs.hostile.theRunebound.BattleChefMob;
import necesse.level.maps.IncursionLevel;

public class BossBringsInvadersPerk
extends IncursionPerk {
    public BossBringsInvadersPerk(Integer tier, int perkCost, int xPositionOnPerkTree, IncursionPerk ... prerequisitePerkRequired) {
        super(tier, perkCost, xPositionOnPerkTree, true, prerequisitePerkRequired);
    }

    @Override
    public void onIncursionBossPortalClicked(IncursionLevel level) {
        super.onIncursionBossPortalClicked(level);
        level.entityManager.events.addHidden(new WaitForSecondsEvent(10.0f){

            @Override
            public void onWaitOver() {
                for (ServerClient client : this.level.getServer().getClients()) {
                    final ChieftainGauntletSpawnerPortalMob portalToSpawnMobsFrom = new ChieftainGauntletSpawnerPortalMob();
                    portalToSpawnMobsFrom.onSpawned(client.playerMob.getX(), client.playerMob.getY());
                    this.level.entityManager.mobs.add(portalToSpawnMobsFrom);
                    portalToSpawnMobsFrom.keepAlive(100);
                    final GameRandom random = new GameRandom(this.level.getSeed() + (long)client.getCharacterUniqueID());
                    final TicketSystemList mobIDs = new TicketSystemList();
                    mobIDs.addObject(50, "crazedraven");
                    mobIDs.addObject(35, "arcanicpylon");
                    mobIDs.addObject(20, "battlechef");
                    int mobsToSpawn = 3;
                    int i = 0;
                    while (i < mobsToSpawn) {
                        int currentMobCount = i++;
                        this.level.entityManager.events.addHidden(new WaitForSecondsEvent(1 + currentMobCount){

                            @Override
                            public void onWaitOver() {
                                String randomId = (String)mobIDs.getRandomObject(random);
                                Mob mobToSpawn = MobRegistry.getMob(randomId, this.level);
                                if (mobToSpawn instanceof BattleChefMob) {
                                    ((BattleChefMob)mobToSpawn).spawnedFromBossBringsInvadersPerk = true;
                                }
                                int spawnX = portalToSpawnMobsFrom.getX() + 32 * random.getIntBetween(-2, 2);
                                int spawnY = portalToSpawnMobsFrom.getY() + 32 * random.getIntBetween(-2, 2);
                                mobToSpawn.isSummoned = true;
                                mobToSpawn.canDespawn = true;
                                mobToSpawn.onSpawned(spawnX, spawnY);
                                this.level.entityManager.addMob(mobToSpawn, spawnX, spawnY);
                            }
                        });
                    }
                }
            }
        });
    }
}

