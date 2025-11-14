/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.GlobalData
 *  necesse.engine.input.Control
 *  necesse.engine.input.InputEvent
 *  necesse.engine.localization.Localization
 *  necesse.engine.network.Packet
 *  necesse.engine.network.client.Client
 *  necesse.engine.state.MainGame
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.buffs.staticBuffs.Buff
 *  necesse.gfx.camera.MainGameCamera
 *  necesse.gfx.gameFont.FontOptions
 *  necesse.inventory.InventoryItem
 *  necesse.level.maps.hudManager.HudDrawElement
 *  necesse.level.maps.hudManager.floatText.UniqueFloatText
 */
package aphorea.registry;

import aphorea.buffs.Runes.AphBaseRuneTrinketBuff;
import aphorea.items.runes.AphRunesInjector;
import aphorea.packets.AphRunesInjectorAbilityPacket;
import aphorea.utils.AphColors;
import java.util.ArrayList;
import java.util.Arrays;
import necesse.engine.GlobalData;
import necesse.engine.input.Control;
import necesse.engine.input.InputEvent;
import necesse.engine.localization.Localization;
import necesse.engine.network.Packet;
import necesse.engine.network.client.Client;
import necesse.engine.state.MainGame;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.gfx.camera.MainGameCamera;
import necesse.gfx.gameFont.FontOptions;
import necesse.inventory.InventoryItem;
import necesse.level.maps.hudManager.HudDrawElement;
import necesse.level.maps.hudManager.floatText.UniqueFloatText;

public class AphControls {
    public static void registerCore() {
        Control.addModControl((Control)new Control(71, "runesinjectorability"){

            public void activate(InputEvent event) {
                PlayerMob player;
                MainGame mainGame;
                Client client;
                super.activate(event);
                if (this.isPressed() && GlobalData.getCurrentState() instanceof MainGame && (client = (mainGame = (MainGame)GlobalData.getCurrentState()).getClient()) != null && (player = client.getPlayer()) != null) {
                    MainGameCamera camera = mainGame.getCamera();
                    int mouseLevelX = event.pos.sceneX + camera.getX();
                    int mouseLevelY = event.pos.sceneY + camera.getY();
                    player.buffManager.getBuffs().values().stream().filter(b -> b.buff instanceof AphBaseRuneTrinketBuff).map(b -> (AphBaseRuneTrinketBuff)b.buff).findFirst().ifPresent(runeBuff -> {
                        ArrayList inventoryItems = player.equipmentBuffManager.getTrinketItems();
                        boolean notTheRuneOwner = inventoryItems.stream().anyMatch(inventoryItem -> {
                            InventoryItem rune;
                            if (inventoryItem != null && inventoryItem.item instanceof AphRunesInjector && Arrays.stream(((AphRunesInjector)inventoryItem.item).getBuffs((InventoryItem)inventoryItem)).anyMatch(b -> b == runeBuff) && (rune = ((AphRunesInjector)inventoryItem.item).getBaseRune((InventoryItem)inventoryItem)) != null) {
                                String runeOwner = rune.getGndData().getString("runeOwner", null);
                                return runeOwner != null && !runeOwner.equals(player.playerName);
                            }
                            return false;
                        });
                        if (notTheRuneOwner) {
                            UniqueFloatText text = new UniqueFloatText(player.getX(), player.getY() - 20, Localization.translate((String)"itemtooltip", (String)"notruneowner"), new FontOptions(16).outline().color(AphColors.fail_message), "injectorfail"){

                                public int getAnchorX() {
                                    return player.getX();
                                }

                                public int getAnchorY() {
                                    return player.getY() - 20;
                                }
                            };
                            player.getLevel().hudManager.addElement((HudDrawElement)text);
                        } else {
                            client.network.sendPacket((Packet)new AphRunesInjectorAbilityPacket(client.getSlot(), mouseLevelX, mouseLevelY, (Buff)runeBuff));
                        }
                    });
                }
            }
        });
    }
}

