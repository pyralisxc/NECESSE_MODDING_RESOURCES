/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.Settings
 *  necesse.engine.localization.message.GameMessage
 *  necesse.engine.network.client.Client
 *  necesse.engine.window.GameWindow
 *  necesse.entity.mobs.PlayerMob
 *  necesse.gfx.forms.Form
 *  necesse.gfx.forms.components.FormBreakLine
 *  necesse.gfx.forms.components.FormComponent
 *  necesse.gfx.forms.components.FormComponentList
 *  necesse.gfx.forms.components.FormContentBox
 *  necesse.gfx.forms.components.FormCustomButton
 *  necesse.gfx.forms.components.FormLabel
 *  necesse.gfx.forms.components.localComponents.FormLocalTextButton
 *  necesse.gfx.forms.presets.containerComponent.ContainerFormSwitcher
 *  necesse.gfx.gameFont.FontManager
 *  necesse.gfx.gameFont.FontOptions
 *  necesse.gfx.gameFont.GameFontHandler
 */
package aphorea.containers.book;

import aphorea.containers.book.BookContainer;
import aphorea.items.misc.AphWrittenBook;
import java.awt.Color;
import java.awt.Rectangle;
import necesse.engine.Settings;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.client.Client;
import necesse.engine.window.GameWindow;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormBreakLine;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormComponentList;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormCustomButton;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.containerComponent.ContainerFormSwitcher;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameFont.GameFontHandler;

public class BookContainerForm<T extends BookContainer>
extends ContainerFormSwitcher<T> {
    public Client client;
    private final Form principalForm;
    public static int bookID = -1;
    public static int page = 1;
    public FormContentBox pageContent;
    public FormLabel pageNumber;
    public int pagesAmount;
    public AphWrittenBook.BookPage[] bookPages;

    public BookContainerForm(Client client, T container) {
        super(client, container);
        this.client = client;
        this.bookPages = BookContainerForm.getBookInstructions();
        this.pagesAmount = this.bookPages.length;
        FormComponentList formComponents = (FormComponentList)this.addComponent((FormComponent)new FormComponentList());
        this.principalForm = (Form)formComponents.addComponent((FormComponent)new Form(925, 500));
        this.principalForm.addComponent((FormComponent)new FormLabel(BookContainerForm.getTitle(), new FontOptions(30).color(Settings.UI.activeTextColor), -1, 20, 10, this.principalForm.getWidth() - 20));
        FormBreakLine middleVerticalLine = (FormBreakLine)this.principalForm.addComponent((FormComponent)new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 10, 50, this.principalForm.getWidth() - 20, true));
        middleVerticalLine.color = Settings.UI.activeTextColor;
        this.pageContent = new FormContentBox(20, 70, 885, this.principalForm.getHeight() - 170);
        this.principalForm.addComponent((FormComponent)this.pageContent);
        FormBreakLine middleVerticalLine2 = (FormBreakLine)this.principalForm.addComponent((FormComponent)new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 10, this.principalForm.getHeight() - 80, this.principalForm.getWidth() - 20, true));
        middleVerticalLine2.color = Settings.UI.activeTextColor;
        this.pageNumber = new FormLabel(String.valueOf(page), new FontOptions(20).color(Settings.UI.activeTextColor), 0, this.principalForm.getWidth() / 2, this.principalForm.getHeight() - 65, 20);
        this.principalForm.addComponent((FormComponent)this.pageNumber);
        (this.principalForm.addComponent((FormComponent)new FormCustomButton(this.principalForm.getWidth() / 2 - 60, this.principalForm.getHeight() - 65, 40, 20, new GameMessage[0]){

            public void draw(Color color, int i, int i1, PlayerMob playerMob) {
                GameFontHandler font = FontManager.bit;
                float alpha = 0.6f;
                if (1 >= page) {
                    alpha = 0.2f;
                } else if (this.isHovering()) {
                    alpha = 1.0f;
                }
                FontOptions fontOptions = new FontOptions(20).color(Settings.UI.activeTextColor).alphaf(alpha);
                String drawText = "<";
                font.drawString((float)this.getX() + (float)this.getBoundingBox().width / 2.0f - font.getWidth(drawText, fontOptions) / 2.0f, (float)this.getY(), drawText, fontOptions);
            }
        })).onClicked(event -> {
            if (1 < page) {
                --page;
                this.playTickSound();
                this.updateContent();
            }
        });
        (this.principalForm.addComponent((FormComponent)new FormCustomButton(this.principalForm.getWidth() / 2 + 20, this.principalForm.getHeight() - 65, 40, 20, new GameMessage[0]){

            public void draw(Color color, int i, int i1, PlayerMob playerMob) {
                GameFontHandler font = FontManager.bit;
                float alpha = 0.6f;
                if (BookContainerForm.this.pagesAmount <= page) {
                    alpha = 0.2f;
                } else if (this.isHovering()) {
                    alpha = 1.0f;
                }
                FontOptions fontOptions = new FontOptions(20).color(Settings.UI.activeTextColor).alphaf(alpha);
                String drawText = ">";
                font.drawString((float)this.getX() + (float)this.getBoundingBox().width / 2.0f - font.getWidth(drawText, fontOptions) / 2.0f, (float)this.getY(), drawText, fontOptions);
            }
        })).onClicked(event -> {
            if (this.pagesAmount > page) {
                ++page;
                this.playTickSound();
                this.updateContent();
            }
        });
        ((FormLocalTextButton)this.principalForm.addComponent((FormComponent)new FormLocalTextButton("ui", "closebutton", 4, this.principalForm.getHeight() - 40, this.principalForm.getWidth() - 8))).onClicked(e -> client.closeContainer(true));
        this.makeCurrent((FormComponent)formComponents);
        this.updateContent();
    }

    public void updateContent() {
        this.pageNumber.setText(String.valueOf(page));
        this.pageContent.clearComponents();
        AphWrittenBook.BookPage bookPage = this.bookPages[page - 1];
        int currentY = 5;
        for (int i = 0; i < bookPage.pageInstructions.length; ++i) {
            AphWrittenBook.PageInstruction pageInstruction = bookPage.pageInstructions[i];
            if (i != 0) {
                currentY += pageInstruction.topPadding();
            }
            currentY += pageInstruction.execute(currentY, this.pageContent);
            if (i == bookPage.pageInstructions.length - 1) continue;
            currentY += pageInstruction.bottomPadding();
        }
        this.pageContent.setContentBox(new Rectangle(0, 0, this.pageContent.getWidth(), currentY));
        this.pageContent.setScrollY(0);
    }

    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        this.principalForm.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
    }

    public boolean shouldOpenInventory() {
        return false;
    }

    public boolean shouldShowToolbar() {
        return false;
    }

    public static String getTitle() {
        return bookID == -1 ? "Not found" : AphWrittenBook.getBook(bookID).getTitle();
    }

    public static AphWrittenBook.BookPage[] getBookInstructions() {
        return bookID == -1 ? new AphWrittenBook.BookPage[]{} : AphWrittenBook.getBook((int)BookContainerForm.bookID).content;
    }
}

