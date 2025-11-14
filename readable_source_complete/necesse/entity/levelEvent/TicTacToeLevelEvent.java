/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketLevelEvent;
import necesse.engine.network.packet.PacketPickTicTacToeTile;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.WaitForSecondsEvent;
import necesse.entity.levelEvent.actions.IntLevelEventAction;
import necesse.entity.levelEvent.actions.LevelEventAction;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.followingProjectile.TicTacToePunishProjectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelDrawUtils;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.Level;

public class TicTacToeLevelEvent
extends LevelEvent {
    public static int TIME_TO_PLAYER_ACTIVATE_TILE = 2000;
    public static int WAIT_TIME_FOR_AI_PICK = 2000;
    public static int WAIT_TIME_FOR_AI_PICK_AFTER_PLAYER = 7500;
    public static int WINNER_DISPLAY_TIME = 3000;
    public static int NON_WINNER_FADE_TIME = 1000;
    public static int INITIAL_PROJECTILE_WAIT_TIME = 1000;
    public static int ADDITIONAL_PROJECTILES_WAIT_TIME = 0;
    public int tileX;
    public int tileY;
    protected boolean isXPlayerControlled;
    protected boolean isOPlayerControlled;
    protected Mob xAIMob;
    protected Mob oAIMob;
    public GameEndedEvent onGameEnded;
    protected boolean isXTurn = true;
    protected long lastChangeTime;
    protected long lastClientPacketSendTime;
    protected long winnerDeterminedTime = -1L;
    protected boolean[] isWinnerIndex;
    protected PlayerMob lastXPlayer;
    protected PlayerMob lastOPlayer;
    protected final TileState[] gameState = new TileState[9];
    protected final long[] gameStateSpawnTimes = new long[9];
    protected final Rectangle[] playTiles = new Rectangle[9];
    protected final Rectangle combinedPlayArea;
    protected final int[] playerChooseBuffer = new int[9];
    protected final IntLevelEventAction chooseXTileAction;
    protected final IntLevelEventAction chooseOTileAction;
    protected final WinnerDeterminedAction winnerDeterminedAction;

    public TicTacToeLevelEvent() {
        super(true);
        Arrays.fill((Object[])this.gameState, (Object)TileState.EMPTY);
        Rectangle combinedPlayArea = null;
        int tileGap = 1;
        int tileRes = 3;
        for (int x = 0; x < 3; ++x) {
            for (int y = 0; y < 3; ++y) {
                Rectangle rectangle;
                int index = x + y * 3;
                this.playTiles[index] = rectangle = new Rectangle((x - 1) * (tileGap + tileRes) - tileRes / 2, (y - 1) * (tileGap + tileRes) - tileRes / 2, tileRes, tileRes);
                combinedPlayArea = combinedPlayArea == null ? rectangle : combinedPlayArea.union(rectangle);
            }
        }
        this.combinedPlayArea = combinedPlayArea;
        this.chooseXTileAction = this.registerAction(new IntLevelEventAction(){

            @Override
            protected void run(int value) {
                TicTacToeLevelEvent.this.gameState[value] = TileState.X;
                TicTacToeLevelEvent.this.gameStateSpawnTimes[value] = TicTacToeLevelEvent.this.getTime();
                TicTacToeLevelEvent.this.lastChangeTime = TicTacToeLevelEvent.this.getTime();
                TicTacToeLevelEvent.this.isXTurn = false;
                Arrays.fill(TicTacToeLevelEvent.this.playerChooseBuffer, 0);
            }
        });
        this.chooseOTileAction = this.registerAction(new IntLevelEventAction(){

            @Override
            protected void run(int value) {
                TicTacToeLevelEvent.this.gameState[value] = TileState.O;
                TicTacToeLevelEvent.this.gameStateSpawnTimes[value] = TicTacToeLevelEvent.this.getTime();
                TicTacToeLevelEvent.this.lastChangeTime = TicTacToeLevelEvent.this.getTime();
                TicTacToeLevelEvent.this.isXTurn = true;
                Arrays.fill(TicTacToeLevelEvent.this.playerChooseBuffer, 0);
            }
        });
        this.winnerDeterminedAction = this.registerAction(new WinnerDeterminedAction(){

            @Override
            public void run(TileState winner, int[] indexes) {
                TicTacToeLevelEvent.this.winnerDeterminedTime = TicTacToeLevelEvent.this.getTime();
                TicTacToeLevelEvent.this.isWinnerIndex = new boolean[9];
                if (winner != TileState.EMPTY) {
                    for (int index : indexes) {
                        TicTacToeLevelEvent.this.isWinnerIndex[index] = true;
                    }
                }
                if (TicTacToeLevelEvent.this.onGameEnded != null) {
                    Rectangle[] winnerTiles = new Rectangle[indexes == null ? 0 : indexes.length];
                    for (int i = 0; i < winnerTiles.length; ++i) {
                        Rectangle playTile = TicTacToeLevelEvent.this.playTiles[indexes[i]];
                        winnerTiles[i] = new Rectangle((TicTacToeLevelEvent.this.tileX + playTile.x) * 32, (TicTacToeLevelEvent.this.tileY + playTile.y) * 32, playTile.width * 32, playTile.height * 32);
                    }
                    TicTacToeLevelEvent.this.onGameEnded.run(winner, winnerTiles, TicTacToeLevelEvent.this.isXPlayerControlled ? TicTacToeLevelEvent.this.lastXPlayer : TicTacToeLevelEvent.this.xAIMob, TicTacToeLevelEvent.this.isOPlayerControlled ? TicTacToeLevelEvent.this.lastOPlayer : TicTacToeLevelEvent.this.oAIMob);
                }
            }
        });
    }

    public TicTacToeLevelEvent(int tileX, int tileY, Mob xAIMob, Mob oAIMob, GameEndedEvent onGameEnded) {
        this();
        this.tileX = tileX;
        this.tileY = tileY;
        this.xAIMob = xAIMob;
        this.isXPlayerControlled = xAIMob == null;
        this.oAIMob = oAIMob;
        this.isOPlayerControlled = oAIMob == null;
        this.onGameEnded = onGameEnded;
    }

    @Override
    public boolean isNetworkImportant() {
        return true;
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextInt(this.tileX);
        writer.putNextInt(this.tileY);
        writer.putNextBoolean(this.isXPlayerControlled);
        writer.putNextBoolean(this.isOPlayerControlled);
        writer.putNextBoolean(this.isXTurn);
        if (this.winnerDeterminedTime >= 0L) {
            writer.putNextBoolean(true);
            writer.putNextLong(this.winnerDeterminedTime);
            for (boolean winnerIndex : this.isWinnerIndex) {
                writer.putNextBoolean(winnerIndex);
            }
        } else {
            writer.putNextBoolean(false);
        }
        for (TileState tileState : this.gameState) {
            writer.putNextEnum(tileState);
        }
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        int i;
        super.applySpawnPacket(reader);
        this.tileX = reader.getNextInt();
        this.tileY = reader.getNextInt();
        this.isXPlayerControlled = reader.getNextBoolean();
        this.isOPlayerControlled = reader.getNextBoolean();
        this.isXTurn = reader.getNextBoolean();
        if (reader.getNextBoolean()) {
            this.winnerDeterminedTime = reader.getNextLong();
            this.isWinnerIndex = new boolean[9];
            for (i = 0; i < this.isWinnerIndex.length; ++i) {
                this.isWinnerIndex[i] = reader.getNextBoolean();
            }
        }
        for (i = 0; i < this.gameState.length; ++i) {
            this.gameState[i] = reader.getNextEnum(TileState.class);
        }
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void clientTick() {
        long timeSinceChange;
        super.clientTick();
        for (int i = 0; i < this.playTiles.length; ++i) {
            TileState state = this.gameState[i];
            if (state == TileState.EMPTY) continue;
            Rectangle playTile = this.playTiles[i];
            int centerX = (this.tileX + playTile.x) * 32 + playTile.width * 32 / 2;
            int centerY = (this.tileY + playTile.y) * 32 + playTile.height * 32 / 2;
            this.level.lightManager.refreshParticleLightFloat((float)centerX, (float)centerY, state == TileState.O ? 195.0f : 357.0f, 0.8f, 150);
        }
        if (this.winnerDeterminedTime != -1L) {
            long timeSinceWinnerDetermined = this.getTime() - this.winnerDeterminedTime;
            if (timeSinceWinnerDetermined >= (long)WINNER_DISPLAY_TIME) {
                this.over();
            }
        } else if (this.isClient() && (this.isXTurn && this.isXPlayerControlled || !this.isXTurn && this.isOPlayerControlled) && (timeSinceChange = this.getTime() - this.lastChangeTime) > (long)WAIT_TIME_FOR_AI_PICK) {
            Rectangle playArea = new Rectangle((this.tileX + this.combinedPlayArea.x) * 32, (this.tileY + this.combinedPlayArea.y) * 32, this.combinedPlayArea.width * 32, this.combinedPlayArea.height * 32);
            PlayerMob myPlayer = this.getClient().getPlayer();
            int myPick = -1;
            boolean[] allPicks = new boolean[9];
            List players = this.getLevel().entityManager.players.streamInRegionsShape(playArea, 1).collect(Collectors.toList());
            for (PlayerMob player : players) {
                for (int i = 0; i < this.playTiles.length; ++i) {
                    if (this.gameState[i] != TileState.EMPTY) continue;
                    Rectangle playTile = this.playTiles[i];
                    Rectangle tileCollision = new Rectangle((this.tileX + playTile.x) * 32, (this.tileY + playTile.y) * 32, playTile.width * 32, playTile.height * 32);
                    if (!tileCollision.contains(player.getX(), player.getY())) continue;
                    allPicks[i] = true;
                    if (player != myPlayer) continue;
                    myPick = i;
                }
            }
            for (int i = 0; i < allPicks.length; ++i) {
                int currentBuffer = this.playerChooseBuffer[i];
                if (allPicks[i]) {
                    long timeSinceLastSend;
                    this.playerChooseBuffer[i] = currentBuffer + 50;
                    if (currentBuffer > TIME_TO_PLAYER_ACTIVATE_TILE && myPick == i && (timeSinceLastSend = this.getTime() - this.lastClientPacketSendTime) >= 1000L) {
                        this.getClient().network.sendPacket(new PacketPickTicTacToeTile(this, i));
                        this.lastClientPacketSendTime = this.getTime();
                    }
                } else {
                    this.playerChooseBuffer[i] = Math.max(0, currentBuffer - 100);
                }
                if (this.playerChooseBuffer[i] <= 0) continue;
                float progress = Math.min(1.0f, (float)this.playerChooseBuffer[i] / (float)TIME_TO_PLAYER_ACTIVATE_TILE);
                int lightLevel = GameMath.lerp(progress, 50, 150);
                Rectangle playTile = this.playTiles[i];
                int centerX = (this.tileX + playTile.x) * 32 + playTile.width * 32 / 2;
                int centerY = (this.tileY + playTile.y) * 32 + playTile.height * 32 / 2;
                this.level.lightManager.refreshParticleLightFloat((float)centerX, (float)centerY, this.isXTurn ? 357.0f : 195.0f, 0.8f, lightLevel);
            }
        }
        this.checkObject();
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (this.winnerDeterminedTime != -1L) {
            long timeSinceWinnerDetermined = this.getTime() - this.winnerDeterminedTime;
            if (timeSinceWinnerDetermined >= (long)WINNER_DISPLAY_TIME) {
                this.over();
            }
        } else {
            long timeSinceChange = this.getTime() - this.lastChangeTime;
            if (!(timeSinceChange <= (long)WAIT_TIME_FOR_AI_PICK || this.checkWinner() || this.isXPlayerControlled && this.isOPlayerControlled)) {
                if (this.isXTurn && !this.isXPlayerControlled) {
                    int nextAIPickIndex = this.getNextAIPickIndex(TileState.X);
                    if (nextAIPickIndex != -1) {
                        this.chooseXTileAction.runAndSend(nextAIPickIndex);
                    } else {
                        this.winnerDeterminedAction.run(TileState.EMPTY, null);
                    }
                } else if (!this.isXTurn && !this.isOPlayerControlled) {
                    int nextAIPickIndex = this.getNextAIPickIndex(TileState.O);
                    if (nextAIPickIndex != -1) {
                        this.chooseOTileAction.runAndSend(nextAIPickIndex);
                    } else {
                        this.winnerDeterminedAction.run(TileState.EMPTY, null);
                    }
                } else if (timeSinceChange > (long)WAIT_TIME_FOR_AI_PICK_AFTER_PLAYER) {
                    int nextAIPickIndex = this.getNextAIPickIndex(this.isXTurn ? TileState.O : TileState.X);
                    if (nextAIPickIndex != -1) {
                        if (this.isXTurn) {
                            this.chooseOTileAction.runAndSend(nextAIPickIndex);
                        } else {
                            this.chooseXTileAction.runAndSend(nextAIPickIndex);
                        }
                    } else {
                        this.winnerDeterminedAction.run(TileState.EMPTY, null);
                    }
                }
            }
        }
        this.checkObject();
    }

    public void checkObject() {
        if (this.level.getObjectID(this.tileX, this.tileY) != ObjectRegistry.getObjectID("tictactoeboard")) {
            this.over();
        }
    }

    public void onClientPickedIndex(ServerClient client, int index) {
        if (this.isXTurn && this.isXPlayerControlled) {
            this.lastXPlayer = client.playerMob;
            this.chooseXTileAction.runAndSend(index);
        } else if (!this.isXTurn && this.isOPlayerControlled) {
            this.lastOPlayer = client.playerMob;
            this.chooseOTileAction.runAndSend(index);
        } else {
            client.sendPacket(new PacketLevelEvent(this));
        }
    }

    public int getNextAIPickIndex(TileState player) {
        ArrayList<Integer> validPicks = new ArrayList<Integer>();
        for (int i = 0; i < this.gameState.length; ++i) {
            if (this.gameState[i] != TileState.EMPTY) continue;
            validPicks.add(i);
        }
        if (validPicks.isEmpty()) {
            return -1;
        }
        return (Integer)GameRandom.globalRandom.getOneOf(validPicks);
    }

    protected boolean checkWinner() {
        for (int i = 0; i < 3; ++i) {
            if (this.gameState[i] != TileState.EMPTY && this.gameState[i] == this.gameState[i + 3] && this.gameState[i] == this.gameState[i + 6]) {
                this.winnerDeterminedAction.runAndSend(this.gameState[i], new int[]{i, i + 3, i + 6});
                return true;
            }
            int rowStart = i * 3;
            if (this.gameState[rowStart] == TileState.EMPTY || this.gameState[rowStart] != this.gameState[rowStart + 1] || this.gameState[rowStart] != this.gameState[rowStart + 2]) continue;
            this.winnerDeterminedAction.runAndSend(this.gameState[rowStart], new int[]{rowStart, rowStart + 1, rowStart + 2});
            return true;
        }
        if (this.gameState[0] != TileState.EMPTY && this.gameState[0] == this.gameState[4] && this.gameState[0] == this.gameState[8]) {
            this.winnerDeterminedAction.runAndSend(this.gameState[0], new int[]{0, 4, 8});
            return true;
        }
        if (this.gameState[2] != TileState.EMPTY && this.gameState[2] == this.gameState[4] && this.gameState[2] == this.gameState[6]) {
            this.winnerDeterminedAction.runAndSend(this.gameState[2], new int[]{2, 4, 6});
            return true;
        }
        for (TileState tileState : this.gameState) {
            if (tileState != TileState.EMPTY) continue;
            return false;
        }
        this.winnerDeterminedAction.runAndSend(TileState.EMPTY, null);
        return true;
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> sortedDrawables, OrderableDrawables tileDrawables, OrderableDrawables topDrawables, LevelDrawUtils.DrawArea drawArea, Level level, TickManager tickManager, GameCamera camera) {
        for (int i = 0; i < this.playTiles.length; ++i) {
            int sprite;
            int animTimePerSprite;
            int spawnTime;
            long timeSinceChange;
            int playerBuffer;
            Rectangle playTile = this.playTiles[i];
            TileState tileState = this.gameState[i];
            int spawnSprites = 4;
            float alpha = 1.0f;
            GameTexture texture = null;
            if (tileState == TileState.X) {
                texture = GameResources.ticTacToeX;
            } else if (tileState == TileState.O) {
                texture = GameResources.ticTacToeO;
            } else if ((!this.isXTurn || this.isXPlayerControlled) && (playerBuffer = this.playerChooseBuffer[i]) > 0) {
                alpha = Math.min(1.0f, (float)playerBuffer / (float)TIME_TO_PLAYER_ACTIVATE_TILE);
                texture = this.isXTurn ? GameResources.ticTacToeX : GameResources.ticTacToeO;
            }
            if (texture == null) continue;
            if (this.winnerDeterminedTime != -1L) {
                long timeSinceWinnerDetermined = this.getTime() - this.winnerDeterminedTime;
                if (this.isWinnerIndex != null) {
                    if (this.isWinnerIndex[i]) {
                        if (timeSinceWinnerDetermined >= (long)(WINNER_DISPLAY_TIME - 500)) {
                            float endProgress = Math.min(1.0f, (float)(timeSinceWinnerDetermined - (long)(WINNER_DISPLAY_TIME - 500)) / 500.0f);
                            alpha = GameMath.lerp(endProgress, 1.0f, 0.0f);
                        } else {
                            alpha = 1.0f;
                        }
                    } else {
                        float winnerDeterminedProgress = Math.min(1.0f, (float)timeSinceWinnerDetermined / (float)NON_WINNER_FADE_TIME);
                        alpha = GameMath.lerp(winnerDeterminedProgress, 1.0f, 0.0f);
                    }
                } else {
                    float winnerDeterminedProgress = Math.min(1.0f, (float)timeSinceWinnerDetermined / (float)WINNER_DISPLAY_TIME);
                    alpha = GameMath.lerp(winnerDeterminedProgress, 1.0f, 0.0f);
                }
            }
            if ((timeSinceChange = this.getTime() - this.gameStateSpawnTimes[i]) < (long)(spawnTime = (animTimePerSprite = 150) * spawnSprites)) {
                float progress = (float)timeSinceChange / (float)spawnTime;
                sprite = (int)(progress * (float)spawnSprites);
            } else {
                int idleSprites = texture.getWidth() / 96 - spawnSprites;
                sprite = spawnSprites + GameUtils.getAnim(timeSinceChange, idleSprites, idleSprites * animTimePerSprite);
            }
            int drawX = camera.getTileDrawX(this.tileX + playTile.x);
            int drawY = camera.getTileDrawY(this.tileY + playTile.y);
            TextureDrawOptionsEnd drawOptions = texture.initDraw().sprite(sprite, 0, 96).size(playTile.width * 32, playTile.height * 32).alpha(alpha).pos(drawX, drawY);
            tileDrawables.add(tm -> drawOptions.draw());
        }
    }

    @Override
    public Point getSaveToRegionPos() {
        return new Point(this.level.regionManager.getRegionCoordByTile(this.tileX), this.level.regionManager.getRegionCoordByTile(this.tileY));
    }

    public static ArrayList<Projectile> spawnPunishProjectiles(Rectangle[] tiles, Mob target, GameDamage damage, boolean isXProjectile) {
        ArrayList<Projectile> projectiles = new ArrayList<Projectile>(tiles.length);
        for (int i = 0; i < tiles.length; ++i) {
            Rectangle rectangle = tiles[i];
            int projectileStartX = rectangle.x + rectangle.width / 2;
            int projectileStartY = rectangle.y + rectangle.height / 2;
            final TicTacToePunishProjectile projectile = new TicTacToePunishProjectile(target.getLevel(), projectileStartX, projectileStartY, target, target.getUniqueID() + i * 20, 200.0f, damage, 100, isXProjectile);
            projectiles.add(projectile);
            target.getLevel().entityManager.events.addHidden(new WaitForSecondsEvent((float)INITIAL_PROJECTILE_WAIT_TIME / 1000.0f + (float)(i * ADDITIONAL_PROJECTILES_WAIT_TIME) / 1000.0f){

                @Override
                public void onWaitOver() {
                    this.level.entityManager.projectiles.add(projectile);
                }
            });
        }
        return projectiles;
    }

    public static enum TileState {
        EMPTY,
        X,
        O;

    }

    protected static abstract class WinnerDeterminedAction
    extends LevelEventAction {
        protected WinnerDeterminedAction() {
        }

        public void runAndSend(TileState winner, int[] indexes) {
            Packet packet = new Packet();
            PacketWriter writer = new PacketWriter(packet);
            writer.putNextEnum(winner);
            if (winner != TileState.EMPTY) {
                if (indexes.length != 3) {
                    throw new IllegalArgumentException("Indexes must be size 3");
                }
                for (int index : indexes) {
                    writer.putNextByteUnsigned(index);
                }
            }
            this.runAndSendAction(packet);
        }

        @Override
        public void executePacket(PacketReader reader) {
            TileState winner = reader.getNextEnum(TileState.class);
            if (winner != TileState.EMPTY) {
                int[] indexes = new int[3];
                for (int i = 0; i < indexes.length; ++i) {
                    indexes[i] = reader.getNextByteUnsigned();
                }
                this.run(winner, indexes);
            } else {
                this.run(winner, null);
            }
        }

        public abstract void run(TileState var1, int[] var2);
    }

    @FunctionalInterface
    public static interface GameEndedEvent {
        public void run(TileState var1, Rectangle[] var2, Mob var3, Mob var4);
    }
}

