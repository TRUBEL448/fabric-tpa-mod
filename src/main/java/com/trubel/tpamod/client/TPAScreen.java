package com.trubel.tpamod.client;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.text.Text;
import net.minecraft.client.util.math.MatrixStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;

import java.util.ArrayList;
import java.util.List;

public class TPAScreen extends Screen {
    private TextFieldWidget playerNameField;
    private ButtonWidget addButton;
    private ButtonWidget removeButton;
    private ButtonWidget startButton;
    private ButtonWidget stopButton;
    private List<String> playerList = new ArrayList<>(TPAManager.getPlayers());
    private int selectedIndex = -1;
    private int scrollOffset = 0;

    public TPAScreen() {
        super(Text.literal("TPA Manager"));
    }

    @Override
    protected void init() {
        // Nazwa pola
        this.playerNameField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 30, 200, 20, Text.literal("Nick gracza"));
        this.playerNameField.setMaxLength(32);
        this.addSelectableChild(this.playerNameField);

        // Przycisk dodaj
        this.addButton = this.addDrawableChild(new ButtonWidget.Builder(Text.literal("Dodaj"), button -> addPlayer())
                .dimensions(this.width / 2 - 100, 60, 90, 20)
                .build());

        // Przycisk usuń
        this.removeButton = this.addDrawableChild(new ButtonWidget.Builder(Text.literal("Usuń"), button -> removePlayer())
                .dimensions(this.width / 2 + 10, 60, 90, 20)
                .build());

        // Przycisk start
        this.startButton = this.addDrawableChild(new ButtonWidget.Builder(Text.literal("Start TPA"), button -> startTPA())
                .dimensions(this.width / 2 - 100, this.height - 40, 90, 20)
                .build());

        // Przycisk stop
        this.stopButton = this.addDrawableChild(new ButtonWidget.Builder(Text.literal("Stop TPA"), button -> stopTPA())
                .dimensions(this.width / 2 + 10, this.height - 40, 90, 20)
                .build());

        this.setInitialFocus(this.playerNameField);
    }

    private void addPlayer() {
        String playerName = this.playerNameField.getValue().trim();
        if (!playerName.isEmpty() && !this.playerList.contains(playerName)) {
            this.playerList.add(playerName);
            this.playerNameField.setValue("");
            TPAManager.addPlayer(playerName);
        }
    }

    private void removePlayer() {
        if (this.selectedIndex >= 0 && this.selectedIndex < this.playerList.size()) {
            String removed = this.playerList.remove(this.selectedIndex);
            TPAManager.removePlayer(removed);
            this.selectedIndex = -1;
        }
    }

    private void startTPA() {
        TPAManager.start();
    }

    private void stopTPA() {
        TPAManager.stop();
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTick);

        // Tytuł
        this.textRenderer.draw(poseStack, "TPA Manager", this.width / 2.0f - 50, 10, 0xFFFFFF);
        this.textRenderer.draw(poseStack, "Status: " + (TPAManager.isRunning() ? "§aWłączone" : "§cWyłączone"), this.width / 2.0f - 50, this.height - 60, 0xFFFFFF);

        // Lista graczy
        int listY = 100;
        int listHeight = this.height - 170;
        int playerSize = 20;

        fill(poseStack, this.width / 2 - 100, listY, this.width / 2 + 100, listY + listHeight, 0xFF1F1F1F);

        int visibleCount = listHeight / playerSize;
        for (int i = 0; i < Math.min(visibleCount, this.playerList.size()); i++) {
            int index = i + this.scrollOffset;
            if (index >= this.playerList.size()) break;

            String player = this.playerList.get(index);
            int y = listY + i * playerSize;
            int bgColor = index == this.selectedIndex ? 0xFF00AA00 : 0xFF2F2F2F;

            fill(poseStack, this.width / 2 - 95, y + 2, this.width / 2 + 95, y + 18, bgColor);
            this.textRenderer.draw(poseStack, player, this.width / 2.0f - 90, y + 5, 0xFFFFFF);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
        int listY = 100;
        int listHeight = this.height - 170;
        int playerSize = 20;
        int visibleCount = listHeight / playerSize;

        if (mouseY > listY && mouseY < listY + listHeight) {
            this.scrollOffset -= (int) vertical;
            this.scrollOffset = Math.max(0, Math.min(this.scrollOffset, Math.max(0, this.playerList.size() - visibleCount)));
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int listY = 100;
        int listHeight = this.height - 170;
        int playerSize = 20;
        int visibleCount = listHeight / playerSize;

        if (mouseX > this.width / 2 - 100 && mouseX < this.width / 2 + 100 && mouseY > listY && mouseY < listY + listHeight) {
            int clickedIndex = (int) ((mouseY - listY) / playerSize) + this.scrollOffset;
            if (clickedIndex < this.playerList.size()) {
                this.selectedIndex = clickedIndex;
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void close() {
        super.close();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
