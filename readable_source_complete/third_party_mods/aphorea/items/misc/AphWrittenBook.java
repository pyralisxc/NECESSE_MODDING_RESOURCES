/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.Settings
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.engine.localization.Localization
 *  necesse.engine.network.gameNetworkData.GNDItemMap
 *  necesse.engine.network.packet.PacketOpenContainer
 *  necesse.engine.network.server.ServerClient
 *  necesse.engine.registries.ContainerRegistry
 *  necesse.engine.registries.ItemRegistry
 *  necesse.engine.util.GameBlackboard
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.itemAttacker.ItemAttackSlot
 *  necesse.entity.mobs.itemAttacker.ItemAttackerMob
 *  necesse.gfx.forms.components.FormComponent
 *  necesse.gfx.forms.components.FormContentBox
 *  necesse.gfx.forms.components.FormLabel
 *  necesse.gfx.gameFont.FontOptions
 *  necesse.gfx.gameTexture.GameTexture
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.PlayerInventorySlot
 *  necesse.inventory.container.Container
 *  necesse.inventory.container.ContainerActionResult
 *  necesse.inventory.container.slots.ContainerSlot
 *  necesse.inventory.item.ItemInteractAction
 *  necesse.level.maps.Level
 *  org.jetbrains.annotations.NotNull
 */
package aphorea.items.misc;

import aphorea.AphResources;
import aphorea.containers.book.BookContainerForm;
import aphorea.items.vanillaitemtypes.AphMiscItem;
import aphorea.registry.AphContainers;
import java.awt.Rectangle;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.container.Container;
import necesse.inventory.container.ContainerActionResult;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.item.ItemInteractAction;
import necesse.level.maps.Level;
import org.jetbrains.annotations.NotNull;

public class AphWrittenBook
extends AphMiscItem
implements ItemInteractAction {
    public BookPage[] content;

    public AphWrittenBook(BookPage ... content) {
        super(1);
        this.content = content;
    }

    public static AphWrittenBook getBook(int bookID) {
        return (AphWrittenBook)ItemRegistry.getItem((int)bookID);
    }

    public String getTitle() {
        return ItemRegistry.getDisplayName((int)this.getID());
    }

    public Supplier<ContainerActionResult> getInventoryRightClickAction(Container container, InventoryItem item, int slotIndex, ContainerSlot slot) {
        return () -> {
            PlayerInventorySlot playerSlot = null;
            if (slot.getInventory() == container.getClient().playerMob.getInv().main) {
                playerSlot = new PlayerInventorySlot(container.getClient().playerMob.getInv().main, slot.getInventorySlot());
            }
            if (slot.getInventory() == container.getClient().playerMob.getInv().cloud) {
                playerSlot = new PlayerInventorySlot(container.getClient().playerMob.getInv().cloud, slot.getInventorySlot());
            }
            if (playerSlot != null) {
                if (container.getClient().isClient() && BookContainerForm.bookID != this.getID()) {
                    BookContainerForm.bookID = this.getID();
                    BookContainerForm.page = 1;
                }
                if (container.getClient().isServer()) {
                    ServerClient client = container.getClient().getServerClient();
                    PacketOpenContainer p = new PacketOpenContainer(AphContainers.BOOK_CONTAINER);
                    ContainerRegistry.openAndSendContainer((ServerClient)client, (PacketOpenContainer)p);
                }
                return new ContainerActionResult(1328013989);
            }
            return new ContainerActionResult(60840742, Localization.translate((String)"itemtooltip", (String)"rclickinvopenerror"));
        };
    }

    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        if (attackerMob.isPlayer) {
            if (attackerMob.isClient() && BookContainerForm.bookID != this.getID()) {
                BookContainerForm.bookID = this.getID();
                BookContainerForm.page = 1;
            }
            if (attackerMob.isServer()) {
                ServerClient client = ((PlayerMob)attackerMob).getServerClient();
                PacketOpenContainer p = new PacketOpenContainer(AphContainers.BOOK_CONTAINER);
                ContainerRegistry.openAndSendContainer((ServerClient)client, (PacketOpenContainer)p);
            }
        }
        return super.onAttack(level, x, y, attackerMob, attackHeight, item, slot, animAttack, seed, mapContent);
    }

    public boolean canLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return true;
    }

    public InventoryItem onLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int seed, GNDItemMap mapContent) {
        if (attackerMob.isPlayer) {
            if (attackerMob.isClient() && BookContainerForm.bookID != this.getID()) {
                BookContainerForm.bookID = this.getID();
                BookContainerForm.page = 1;
            }
            if (attackerMob.isServer()) {
                ServerClient client = ((PlayerMob)attackerMob).getServerClient();
                PacketOpenContainer p = new PacketOpenContainer(AphContainers.BOOK_CONTAINER);
                ContainerRegistry.openAndSendContainer((ServerClient)client, (PacketOpenContainer)p);
            }
        }
        return super.onLevelInteract(level, x, y, attackerMob, attackHeight, item, slot, seed, mapContent);
    }

    public int getItemCooldownTime(InventoryItem item, ItemAttackerMob attackerMob) {
        return 1000;
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"rclickopentip"));
        return tooltips;
    }

    @NotNull
    public static String processTranslationTags(String input) {
        Pattern pattern = Pattern.compile("\\[translate:([^.\\]]+)\\.([^]]+)]");
        Matcher matcher = pattern.matcher(input);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            String category = matcher.group(1);
            String translationKey = matcher.group(2);
            String translation = Localization.translate((String)category, (String)translationKey);
            matcher.appendReplacement(result, Matcher.quoteReplacement(translation));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    public static class BookPage {
        public PageInstruction[] pageInstructions;

        public BookPage(PageInstruction ... pageInstructions) {
            this.pageInstructions = pageInstructions;
        }
    }

    public static class PageImage
    extends PageInstruction {
        public String imageID;

        public PageImage(String imageID) {
            this.imageID = imageID;
        }

        @Override
        public int execute(int y, FormContentBox pageContent) {
            final GameTexture gameTexture = AphResources.bookTextures.get(this.imageID);
            pageContent.addComponent((FormComponent)new FormContentBox(0, y, gameTexture.getWidth(), gameTexture.getHeight()){

                public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
                    super.draw(tickManager, perspective, renderBox);
                    gameTexture.initDraw().draw(this.getX(), this.getY());
                }
            });
            return gameTexture.getHeight();
        }

        @Override
        public int bottomPadding() {
            return 16;
        }
    }

    public static class PageHeader2
    extends PageHeader1 {
        public PageHeader2(String text) {
            super(text);
        }

        @Override
        public int getFontSize() {
            return 20;
        }
    }

    public static class PageHeader1
    extends PageText {
        public PageHeader1(String text) {
            super(text);
        }

        @Override
        public int topPadding() {
            return 32;
        }

        @Override
        public FontOptions getFontOptions() {
            return new FontOptions(this.getFontSize()).color(Settings.UI.activeTextColor);
        }

        @Override
        public int getFontSize() {
            return 24;
        }
    }

    public static class PageText
    extends PageInstruction {
        public String text;

        public PageText(String text) {
            this.text = text;
        }

        public FontOptions getFontOptions() {
            return new FontOptions(this.getFontSize()).color(Settings.UI.activeTextColor).alphaf(0.8f);
        }

        public int getFontSize() {
            return 14;
        }

        @Override
        public int execute(int y, FormContentBox pageContent) {
            FormLabel textLabel = new FormLabel(AphWrittenBook.processTranslationTags(this.text), this.getFontOptions(), -1, 0, y, pageContent.getWidth() - rightMargin);
            pageContent.addComponent((FormComponent)textLabel);
            return textLabel.getHeight();
        }

        @Override
        public int bottomPadding() {
            return 16;
        }
    }

    public static abstract class PageInstruction {
        public static int rightMargin = 30;

        public abstract int execute(int var1, FormContentBox var2);

        public int topPadding() {
            return 0;
        }

        public int bottomPadding() {
            return 0;
        }
    }
}

