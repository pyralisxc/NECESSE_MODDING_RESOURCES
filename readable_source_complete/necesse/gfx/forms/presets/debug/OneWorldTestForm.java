/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.debug;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.InputPosition;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientDebugMapHudDrawElement;
import necesse.engine.network.server.Server;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.engine.world.biomeGenerator.BiomeGeneratorStack;
import necesse.engine.world.biomeGenerator.GeneratorStack;
import necesse.engine.world.biomeGenerator.layers.GeneratorLayer;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormCheckBox;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormTextButton;
import necesse.gfx.forms.components.FormTextButtonToggle;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.forms.components.FormTextureMapBox;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.ui.ButtonColor;
import necesse.level.maps.Level;

public abstract class OneWorldTestForm
extends Form {
    protected int textureTileSize = 128;
    protected String seed;
    protected int worldSeed;
    protected BiomeGeneratorStack generatorStack;
    protected GeneratorLayer currentLayer;
    protected FormContentBox layerSelectorBox;
    protected FormCheckBox useParentColorsCheckBox;
    protected FormTextureMapBox map;
    protected FormLabel debugLabel;
    protected ThreadPoolExecutor executor;
    protected HashMap<Point, GameTexture> textures = new HashMap();
    protected final LinkedList<GameTexture> texturesToDelete = new LinkedList();
    protected int texturesStartX = 0;
    protected int texturesStartY = 0;
    protected int texturesEndX = -1;
    protected int texturesEndY = -1;

    protected static String getRandomSeed() {
        StringBuilder seed = new StringBuilder();
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for (int i = 0; i < 5; ++i) {
            int index = GameRandom.globalRandom.nextInt(alphabet.length());
            seed.append(alphabet, index, index + 1);
        }
        return seed.toString();
    }

    public OneWorldTestForm(final Client client) {
        super(600, 400);
        Server localServer = client.getLocalServer();
        if (localServer != null) {
            this.worldSeed = localServer.world.worldEntity.getWorldSeed();
            this.seed = "";
        } else {
            this.seed = OneWorldTestForm.getRandomSeed();
        }
        FormFlow flow = new FormFlow(4);
        this.addComponent(flow.nextY(new FormLabel("One World Tests", new FontOptions(20), -1, 4, 4), 4));
        this.addComponent(new FormContentIconButton(this.getWidth() - 24 - 4, 4, FormInputSize.SIZE_24, ButtonColor.BASE, this.getInterfaceStyle().container_storage_remove, new GameMessage[0])).onClicked(e -> this.removeForm());
        this.setDraggingBox(new Rectangle(this.getWidth(), flow.next()));
        this.addComponent(flow.nextY(new FormLabel("Seed", new FontOptions(16), -1, 10, 0), 4));
        int seedY = flow.next(32);
        FormTextInput input = this.addComponent(new FormTextInput(4, seedY, FormInputSize.SIZE_24, this.getWidth() - 8 - 24, -1));
        this.addComponent(new FormContentIconButton(this.getWidth() - 24 - 4, seedY, FormInputSize.SIZE_24, ButtonColor.BASE, this.getInterfaceStyle().inventory_sort, new GameMessage[0])).onClicked(e -> {
            this.seed = OneWorldTestForm.getRandomSeed();
            this.reloadStack();
            this.reloadTextures();
            input.setText(this.seed);
        });
        input.setText(this.seed, false);
        input.onSubmit(e -> {
            this.seed = ((FormTextInput)e.from).getText();
            this.reloadStack();
            this.reloadTextures();
        });
        this.addComponent(flow.nextY(new FormLabel("Layer", new FontOptions(16), -1, 10, 0), 4));
        this.layerSelectorBox = this.addComponent(flow.nextY(new FormContentBox(0, 0, this.getWidth(), 200), 4));
        this.useParentColorsCheckBox = this.addComponent(flow.nextY(new FormCheckBox("Use parent colors", 4, 0, this.getWidth() - 8, true).useButtonTexture(), 4));
        this.useParentColorsCheckBox.onClicked(e -> this.reloadTextures());
        this.addComponent(flow.nextY(new FormTextButton("Reload stack", 4, 0, this.getWidth() - 8, FormInputSize.SIZE_24, ButtonColor.BASE), 4)).onClicked(e -> {
            this.reloadStack();
            this.reloadTextures();
        });
        this.reloadStack();
        int renderSize = this.getWidth();
        int[] zoomLevels = new int[]{2, 4, 6, 8, 12, 16, 24, 32, 48, 64, 96, 128, 192, 256, 384, 512};
        this.map = this.addComponent(flow.nextY(new FormTextureMapBox(0, 0, renderSize, renderSize, this.textureTileSize, zoomLevels, 3, true){

            @Override
            public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
                if (this.isMouseOver(event) && !event.isUsed() && event.state && event.getID() == -100 && (client.getPermissionLevel().getLevel() >= PermissionLevel.ADMIN.getLevel() || client.worldSettings.creativeMode) && WindowManager.getWindow().isKeyDown(340)) {
                    if (client.worldSettings.cheatsAllowedOrHidden() || client.worldSettings.creativeMode) {
                        int posX = this.getMouseMapPosX(event.pos.hudX);
                        int posY = this.getMouseMapPosY(event.pos.hudY);
                        PlayerMob player = client.getPlayer();
                        Level level = client.getLevel();
                        if (level != null) {
                            Rectangle edge = new Rectangle(level.tileWidth * 32, level.tileHeight * 32);
                            if (edge.width <= 0) {
                                edge.x = posX - 16;
                                edge.width = 32;
                            }
                            if (edge.height <= 0) {
                                edge.y = posY - 16;
                                edge.height = 32;
                            }
                            if (edge.contains(player.getCollision(posX, posY))) {
                                player.setPos(posX, posY, true);
                                client.sendMovementPacket(true);
                            }
                        }
                    } else {
                        client.chat.addMessage(Localization.translate("misc", "allowcheats"));
                    }
                    event.use();
                }
                super.handleInputEvent(event, tickManager, perspective);
            }

            @Override
            public int getTileScale() {
                return 32;
            }

            @Override
            public Rectangle getTileBounds() {
                int size = 20000;
                return new Rectangle(-size, -size, size * 2, size * 2);
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void drawMapTexture(int textureX, int textureY, double tileScale, int drawX, int drawY) {
                1 var7_6 = this;
                synchronized (var7_6) {
                    GameTexture texture = OneWorldTestForm.this.textures.get(new Point(textureX, textureY));
                    if (texture != null) {
                        int textureWidth = (int)(tileScale * (double)OneWorldTestForm.this.textureTileSize);
                        int textureHeight = (int)(tileScale * (double)OneWorldTestForm.this.textureTileSize);
                        texture.initDraw().size(textureWidth, textureHeight).draw(drawX, drawY);
                    }
                }
            }

            @Override
            public void drawMapOverlays(TickManager tickManager, PlayerMob perspective, int scale, double tileScale, double resHalfX, double resHalfY, int mouseHudX, int mouseHudY) {
                ClientDebugMapHudDrawElement debugMapDrawElement;
                super.drawMapOverlays(tickManager, perspective, scale, tileScale, resHalfX, resHalfY, mouseHudX, mouseHudY);
                Rectangle drawBounds = new Rectangle(this.getWidth(), this.getHeight());
                PlayerMob player = client.getPlayer();
                if (player != null) {
                    Point mapPos = player.getMapPos();
                    int drawX = this.getHudPosX(resHalfX, scale, mapPos.x);
                    int drawY = this.getHudPosY(resHalfY, scale, mapPos.y);
                    Rectangle drawBox = new Rectangle(player.drawOnMapBox(tileScale, false));
                    drawBox.x += drawX;
                    drawBox.y += drawY;
                    if (drawBounds.intersects(drawBox)) {
                        player.drawOnMap(tickManager, client, drawX, drawY, tileScale, drawBounds, false);
                    }
                }
                if ((debugMapDrawElement = client.levelManager.debugMapDrawElement) != null && debugMapDrawElement.shouldDrawOnMap(client, client.levelManager.getMap())) {
                    Point levelPos = debugMapDrawElement.getMapLevelPos();
                    int drawX = this.getHudPosX(resHalfX, scale, levelPos.x);
                    int drawY = this.getHudPosY(resHalfY, scale, levelPos.y);
                    debugMapDrawElement.drawOnMap(tickManager, client, drawX, drawY, tileScale, drawBounds, false);
                }
            }
        }));
        flow.next(4);
        this.debugLabel = this.addComponent(new FormLabel("", new FontOptions(16), -1, 10, flow.next(24)));
        this.setHeight(flow.next());
        GameWindow window = WindowManager.getWindow();
        this.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
    }

    @Override
    protected void init() {
        super.init();
        this.reloadTextures();
    }

    protected synchronized void reloadStack() {
        this.generatorStack = new BiomeGeneratorStack(this.seed.isEmpty() ? this.worldSeed : this.seed.hashCode());
        this.currentLayer = this.generatorStack.getLayer(0);
        this.updateLayerSelector();
        this.texturesStartX = 0;
        this.texturesStartY = 0;
        this.texturesEndX = -1;
        this.texturesEndY = -1;
    }

    protected synchronized void reloadTextures() {
        this.textures = new HashMap();
        if (this.executor != null) {
            this.executor.shutdownNow();
        }
        this.executor = new ThreadPoolExecutor(0, 4, 1L, TimeUnit.MINUTES, new LinkedBlockingDeque<Runnable>(), r -> new Thread(null, r, "one-world-test-loader"));
        this.executor.allowCoreThreadTimeOut(true);
        this.executor.setCorePoolSize(4);
        this.texturesStartX = 0;
        this.texturesStartY = 0;
        this.texturesEndX = -1;
        this.texturesEndY = -1;
    }

    protected void updateLayerSelector() {
        this.layerSelectorBox.clearComponents();
        AtomicInteger currentX = new AtomicInteger(0);
        int maxY = this.addChildren(this.generatorStack.getLayers(), 0, currentX, 0);
        this.layerSelectorBox.setContentBox(new Rectangle(-10, -10, currentX.get() + 20, maxY + 20));
    }

    protected int addChildren(Iterable<GeneratorLayer> children, int currentDepth, AtomicInteger currentX, int startY) {
        int buttonWidth = 100;
        int x = currentX.getAndAdd(buttonWidth + 4);
        int maxY = startY;
        for (final GeneratorLayer layer : children) {
            String debugName = layer.debugName;
            String simpleName = layer.getClass().getSimpleName();
            while (simpleName.trim().isEmpty()) {
                Class<?> superClass = layer.getClass().getSuperclass();
                if (superClass != null) {
                    simpleName = superClass.getSimpleName();
                    continue;
                }
                simpleName = layer.getClass().getName();
                int index = simpleName.lastIndexOf(".");
                if (index == -1) break;
                simpleName = simpleName.substring(index + 1);
                break;
            }
            debugName = debugName == null || debugName.isEmpty() ? simpleName : debugName + " - " + simpleName;
            String name = currentDepth + ": " + debugName + " (" + layer.getStackScale() + ")";
            FormTextButtonToggle button = this.layerSelectorBox.addComponent(new FormTextButtonToggle(name, x, startY, buttonWidth, FormInputSize.SIZE_24, ButtonColor.BASE){

                @Override
                public boolean isToggled() {
                    return OneWorldTestForm.this.currentLayer == layer;
                }
            });
            button.onClicked(e -> {
                this.currentLayer = layer;
                this.reloadTextures();
            });
            for (GeneratorStack stack : layer.getBranchingStacks()) {
                int nextY = this.addChildren(stack.getLayers(), currentDepth, currentX, startY);
                if (nextY <= maxY) continue;
                maxY = nextY;
            }
            ++currentDepth;
            startY += 28;
        }
        return Math.max(startY, maxY);
    }

    public synchronized void updateAndScheduleTextures() {
        GameTexture remove;
        int y;
        int x;
        Rectangle bounds = this.map.getCurrentDrawBounds();
        int modBoundsX = Math.floorMod(bounds.x, this.textureTileSize);
        int modBoundsY = Math.floorMod(bounds.y, this.textureTileSize);
        int modBoundsWidth = bounds.width % this.textureTileSize;
        int modBoundsHeight = bounds.height % this.textureTileSize;
        int texturesStartX = (int)Math.floor((double)bounds.x / (double)this.textureTileSize);
        int texturesStartY = (int)Math.floor((double)bounds.y / (double)this.textureTileSize);
        int texturesEndX = texturesStartX + (int)Math.ceil((double)bounds.width / (double)this.textureTileSize) + (this.textureTileSize - modBoundsWidth <= modBoundsX ? 1 : 0);
        int texturesEndY = texturesStartY + (int)Math.ceil((double)bounds.height / (double)this.textureTileSize) + (this.textureTileSize - modBoundsHeight <= modBoundsY ? 1 : 0);
        int prevTexturesStartX = this.texturesStartX;
        int prevTexturesEndX = this.texturesEndX;
        int prevTexturesStartY = this.texturesStartY;
        int prevTexturesEndY = this.texturesEndY;
        this.texturesStartX = texturesStartX;
        this.texturesEndX = texturesEndX;
        this.texturesStartY = texturesStartY;
        this.texturesEndY = texturesEndY;
        if (texturesStartX < prevTexturesStartX) {
            for (x = texturesStartX; x <= prevTexturesStartX; ++x) {
                for (y = this.texturesStartY; y <= this.texturesEndY; ++y) {
                    this.startLoadTexture(x, y);
                }
            }
        } else if (texturesStartX > prevTexturesStartX) {
            for (x = prevTexturesStartX; x < texturesStartX; ++x) {
                for (y = this.texturesStartY; y <= this.texturesEndY; ++y) {
                    remove = this.textures.remove(new Point(x, y));
                    if (remove == null) continue;
                    remove.delete();
                }
            }
        }
        if (texturesEndX > prevTexturesEndX) {
            for (x = prevTexturesEndX; x <= texturesEndX; ++x) {
                for (y = this.texturesStartY; y <= this.texturesEndY; ++y) {
                    this.startLoadTexture(x, y);
                }
            }
        } else if (texturesEndX < prevTexturesEndX) {
            for (x = texturesEndX; x <= prevTexturesEndX; ++x) {
                for (y = this.texturesStartY; y <= this.texturesEndY; ++y) {
                    remove = this.textures.remove(new Point(x, y));
                    if (remove == null) continue;
                    remove.delete();
                }
            }
        }
        if (texturesStartY < prevTexturesStartY) {
            for (x = this.texturesStartX; x <= this.texturesEndX; ++x) {
                for (y = texturesStartY; y <= prevTexturesStartY; ++y) {
                    this.startLoadTexture(x, y);
                }
            }
        } else if (texturesStartY > prevTexturesStartY) {
            for (x = this.texturesStartX; x <= this.texturesEndX; ++x) {
                for (y = prevTexturesStartY; y < texturesStartY; ++y) {
                    remove = this.textures.remove(new Point(x, y));
                    if (remove == null) continue;
                    remove.delete();
                }
            }
        }
        if (texturesEndY > prevTexturesEndY) {
            for (x = this.texturesStartX; x <= this.texturesEndX; ++x) {
                for (y = prevTexturesEndY; y <= texturesEndY; ++y) {
                    this.startLoadTexture(x, y);
                }
            }
        } else if (texturesEndY < prevTexturesEndY) {
            for (x = this.texturesStartX; x <= this.texturesEndX; ++x) {
                for (y = texturesEndY; y <= prevTexturesEndY; ++y) {
                    remove = this.textures.remove(new Point(x, y));
                    if (remove == null) continue;
                    remove.delete();
                }
            }
        }
        int scale = this.generatorStack.getLayer(this.generatorStack.getStackSize() - 1).getStackScale() / this.currentLayer.getStackScale();
        InputPosition mousePos = WindowManager.getWindow().getInput().mousePos();
        int mouseMapPosX = this.map.getMouseMapPosX(mousePos.hudX - this.getX());
        int mouseMapPosY = this.map.getMouseMapPosY(mousePos.hudY - this.getY());
        int mouseMapTileX = GameMath.getTileCoordinate(mouseMapPosX);
        int mouseMapTileY = GameMath.getTileCoordinate(mouseMapPosY);
        int mouseMapRegionX = GameMath.getRegionCoordByTile(mouseMapTileX);
        int mouseMapRegionY = GameMath.getRegionCoordByTile(mouseMapTileY);
        this.debugLabel.setText("T: " + mouseMapTileX + "x" + mouseMapTileY + ", R: " + mouseMapRegionX + "x" + mouseMapRegionY + ", B: " + bounds.width / scale + "x" + bounds.height / scale + ", S: " + scale + ", " + this.executor.getActiveCount() + ", " + this.executor.getQueue().size());
    }

    public synchronized void startLoadTexture(int textureX, int textureY) {
        if (this.executor == null) {
            return;
        }
        HashMap<Point, GameTexture> textures = this.textures;
        int scale = this.generatorStack.getLayer(this.generatorStack.getStackSize() - 1).getStackScale() / this.currentLayer.getStackScale();
        this.executor.submit(() -> {
            if (textures != this.textures) {
                return;
            }
            if (textureX < this.texturesStartX || textureX > this.texturesEndX || textureY < this.texturesStartY || textureY > this.texturesEndY) {
                return;
            }
            GameTexture texture = new GameTexture("OneWorldTest", this.textureTileSize, this.textureTileSize);
            texture.setBlendQuality(GameTexture.BlendQuality.NEAREST);
            for (int x = 0; x < this.textureTileSize / scale; ++x) {
                for (int y = 0; y < this.textureTileSize / scale; ++y) {
                    Color color;
                    try {
                        color = this.currentLayer.getDebugColor(x + textureX * this.textureTileSize / scale, y + textureY * this.textureTileSize / scale, this.useParentColorsCheckBox.checked);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        color = null;
                    }
                    if (color == null) {
                        color = new Color(255, 255, 255, 0);
                    }
                    for (int i = 0; i < scale; ++i) {
                        for (int j = 0; j < scale; ++j) {
                            texture.setPixel(x * scale + i, y * scale + j, color);
                        }
                    }
                }
            }
            if (textures != this.textures) {
                return;
            }
            this.putTexture(textureX, textureY, texture);
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected synchronized void putTexture(int textureX, int textureY, GameTexture texture) {
        Point point = new Point(textureX, textureY);
        GameTexture last = this.textures.get(point);
        if (last != null) {
            LinkedList<GameTexture> linkedList = this.texturesToDelete;
            synchronized (linkedList) {
                this.texturesToDelete.add(last);
            }
        }
        if (textureX < this.texturesStartX || textureX > this.texturesEndX || textureY < this.texturesStartY || textureY > this.texturesEndY) {
            return;
        }
        this.textures.put(point, texture);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        this.updateAndScheduleTextures();
        LinkedList<GameTexture> linkedList = this.texturesToDelete;
        synchronized (linkedList) {
            for (GameTexture texture : this.texturesToDelete) {
                texture.delete();
            }
            this.texturesToDelete.clear();
        }
        super.draw(tickManager, perspective, renderBox);
    }

    public synchronized void deleteTextures() {
        for (GameTexture value : this.textures.values()) {
            value.delete();
        }
        this.textures.clear();
    }

    @Override
    public void dispose() {
        super.dispose();
        this.deleteTextures();
        if (this.executor != null) {
            this.executor.shutdownNow();
        }
    }

    public abstract void removeForm();
}

