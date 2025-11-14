/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.debug;

import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.network.client.Client;
import necesse.engine.state.MainGame;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.MainGameCamera;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormButton;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormTextButton;
import necesse.gfx.forms.presets.debug.DebugBiomesForm;
import necesse.gfx.forms.presets.debug.DebugBuffsForm;
import necesse.gfx.forms.presets.debug.DebugItemForm;
import necesse.gfx.forms.presets.debug.DebugMobsForm;
import necesse.gfx.forms.presets.debug.DebugObjectsForm;
import necesse.gfx.forms.presets.debug.DebugPlayerForm;
import necesse.gfx.forms.presets.debug.DebugSceneForm;
import necesse.gfx.forms.presets.debug.DebugShadersForm;
import necesse.gfx.forms.presets.debug.DebugTilesForm;
import necesse.gfx.forms.presets.debug.DebugTimeForm;
import necesse.gfx.forms.presets.debug.DebugToolsList;
import necesse.gfx.forms.presets.debug.DebugWireForm;
import necesse.gfx.forms.presets.debug.DebugWorldForm;
import necesse.gfx.gameFont.FontOptions;

public class DebugForm
extends FormSwitcher {
    public Form mainMenu;
    public DebugItemForm items;
    public DebugMobsForm mobs;
    public DebugPlayerForm player;
    public DebugBuffsForm buffs;
    public DebugWorldForm world;
    public DebugBiomesForm biomes;
    public DebugTilesForm tiles;
    public DebugObjectsForm objects;
    public DebugWireForm wire;
    public DebugTimeForm time;
    public DebugShadersForm shaders;
    public DebugSceneForm scene;
    public DebugToolsList tools;
    public final Client client;
    public final MainGame mainGame;

    public DebugForm(String name, Client client, MainGame mainGame) {
        this.client = client;
        this.mainGame = mainGame;
        this.mainMenu = this.addComponent(new Form(name + "MainMenu", 160, 400));
        this.mainMenu.addComponent(new FormLabel("Debug menu", new FontOptions(20), 0, this.mainMenu.getWidth() / 2, 10));
        this.mainMenu.addComponent(new FormTextButton("Items", 0, 40, this.mainMenu.getWidth())).onClicked(e -> {
            this.items.itemList.populateIfNotAlready();
            this.makeCurrent(this.items);
        });
        this.mainMenu.addComponent(new FormTextButton("Mobs", 0, 80, this.mainMenu.getWidth())).onClicked(e -> {
            this.mobs.mobList.populateIfNotAlready();
            this.makeCurrent(this.mobs);
        });
        this.mainMenu.addComponent(new FormTextButton("Player", 0, 120, this.mainMenu.getWidth())).onClicked(e -> this.makeCurrent(this.player));
        this.mainMenu.addComponent(new FormTextButton("Buffs", 0, 160, this.mainMenu.getWidth())).onClicked(e -> {
            this.buffs.buffList.populateIfNotAlready();
            this.makeCurrent(this.buffs);
        });
        this.mainMenu.addComponent(new FormTextButton("World", 0, 200, this.mainMenu.getWidth())).onClicked(e -> this.makeCurrent(this.world));
        this.mainMenu.addComponent(new FormTextButton("Shaders", 0, 240, this.mainMenu.getWidth())).onClicked(e -> this.makeCurrent(this.shaders));
        this.mainMenu.addComponent(new FormTextButton("Scene", 0, 280, this.mainMenu.getWidth())).onClicked(e -> this.makeCurrent(this.scene));
        this.mainMenu.addComponent(new FormTextButton("Show server", 0, 320, this.mainMenu.getWidth())).onClicked(e -> {
            Settings.serverPerspective = !Settings.serverPerspective;
            ((FormTextButton)e.from).setText(Settings.serverPerspective ? "Show client" : "Show server");
        });
        this.mainMenu.addComponent(new FormTextButton("Dev tools", 0, 360, this.mainMenu.getWidth())).onClicked(e -> ((FormButton)e.from).getManager().openFloatMenu(this.tools.getFloatMenu(e.from)));
        this.items = this.addComponent(new DebugItemForm(name + "Items", this), (form, active) -> form.itemFilter.setTyping((boolean)active));
        this.mobs = this.addComponent(new DebugMobsForm(name + "Mobs", this), (form, active) -> form.mobFilter.setTyping((boolean)active));
        this.player = this.addComponent(new DebugPlayerForm(name + "Player", this), (c, isCurrent) -> {
            if (isCurrent.booleanValue()) {
                c.refreshPlayer();
            }
        });
        this.buffs = this.addComponent(new DebugBuffsForm(name + "Buffs", this), (form, active) -> form.buffFilter.setTyping((boolean)active));
        this.world = this.addComponent(new DebugWorldForm(name + "World", this));
        this.biomes = this.addComponent(new DebugBiomesForm(name + "Biomes", this), (form, active) -> form.biomeFilter.setTyping((boolean)active));
        this.tiles = this.addComponent(new DebugTilesForm(name + "Tiles", this), (form, active) -> form.tileFilter.setTyping((boolean)active));
        this.objects = this.addComponent(new DebugObjectsForm(name + "Objects", this), (form, active) -> form.objectFilter.setTyping((boolean)active));
        this.wire = this.addComponent(new DebugWireForm(name + "Wire", this));
        this.time = this.addComponent(new DebugTimeForm(name + "Time", this));
        this.shaders = this.addComponent(new DebugShadersForm(name + "Shaders", this));
        this.scene = this.addComponent(new DebugSceneForm(name + "Scene", this));
        this.tools = new DebugToolsList(this);
        this.onWindowResized(WindowManager.getWindow());
        this.makeCurrent(this.mainMenu);
    }

    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        super.handleInputEvent(event, tickManager, perspective);
        if (!this.mainGame.formManager.isMouseOver()) {
            if (!WindowManager.getWindow().isKeyDown(340) || !event.state) {
                return;
            }
            MainGameCamera camera = this.mainGame.getCamera();
            int mouseX = camera.getMouseLevelPosX();
            int mouseY = camera.getMouseLevelPosY();
            if (event.getID() == -100) {
                if (!this.mainGame.showMap()) {
                    PlayerMob player = this.client.getPlayer();
                    player.setPos(mouseX, mouseY, true);
                    this.client.sendMovementPacket(true);
                }
                event.use();
            }
        }
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        int windowWidth = window.getHudWidth();
        this.mainMenu.setPosition(windowWidth - this.mainMenu.getWidth() - 10, 230);
        this.items.setPosition(windowWidth - this.items.getWidth() - 10, 230);
        this.mobs.setPosition(windowWidth - this.mobs.getWidth() - 10, 230);
        this.player.setPosition(windowWidth - this.player.getWidth() - 10, 230);
        this.buffs.setPosition(windowWidth - this.buffs.getWidth() - 10, 230);
        this.world.setPosition(windowWidth - this.world.getWidth() - 10, 230);
        this.biomes.setPosition(windowWidth - this.biomes.getWidth() - 10, 230);
        this.tiles.setPosition(windowWidth - this.tiles.getWidth() - 10, 230);
        this.objects.setPosition(windowWidth - this.objects.getWidth() - 10, 230);
        this.wire.setPosition(windowWidth - this.wire.getWidth() - 10, 230);
        this.time.setPosition(windowWidth - this.time.getWidth() - 10, 230);
        this.shaders.setPosition(windowWidth - this.shaders.getWidth() - 10, 230);
        this.scene.setPosition(windowWidth - this.scene.getWidth() - 10, 230);
    }

    @Override
    public boolean shouldDraw() {
        return super.shouldDraw() && MainGame.debugFormActive;
    }
}

