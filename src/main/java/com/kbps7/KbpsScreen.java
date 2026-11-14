package com.kbps7;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.client.option.ParticlesMode;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.function.Consumer;
import java.util.function.IntSupplier;

/** The 7KBPS settings panel. */
public class KbpsScreen extends Screen {
    private static final int PANEL_W = 340;
    private static final int PANEL_H = 232;
    private static final int PANEL_BG = 0xE60E1116;
    private static final int PANEL_EDGE = 0xFF1F2733;
    private static final int ACCENT = 0xFF3DDC97;

    private final Screen parent;

    public KbpsScreen(Screen parent) {
        super(Text.literal("7KBPS"));
        this.parent = parent;
    }

    private int left() { return (this.width - PANEL_W) / 2; }
    private int top() { return (this.height - PANEL_H) / 2; }

    @Override
    protected void init() {
        GameOptions o = this.client.options;
        int x = left() + 12;
        int y = top() + 40;
        int fullW = PANEL_W - 24;
        int colW = (fullW - 8) / 2;
        int rowH = 22;

        // Presets
        int presetW = (fullW - 16) / 3;
        addDrawableChild(ButtonWidget.builder(Text.literal("Potato"), b -> applyPreset(Preset.POTATO))
                .dimensions(x, y, presetW, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Balanced"), b -> applyPreset(Preset.BALANCED))
                .dimensions(x + presetW + 8, y, presetW, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Quality"), b -> applyPreset(Preset.QUALITY))
                .dimensions(x + (presetW + 8) * 2, y, presetW, 20).build());
        y += rowH + 8;

        // Sliders
        addDrawableChild(new IntSlider(x, y, colW, 20, "Render Distance", 2, 32,
                () -> o.getViewDistance().getValue(), v -> o.getViewDistance().setValue(v), " chunks"));
        addDrawableChild(new IntSlider(x + colW + 8, y, colW, 20, "Simulation", 5, 32,
                () -> o.getSimulationDistance().getValue(), v -> o.getSimulationDistance().setValue(v), " chunks"));
        y += rowH;
        addDrawableChild(new IntSlider(x, y, colW, 20, "Max FPS", 10, 260,
                () -> o.getMaxFps().getValue(), v -> o.getMaxFps().setValue(v), ""));
        addDrawableChild(new IntSlider(x + colW + 8, y, colW, 20, "Biome Blend", 0, 7,
                () -> o.getBiomeBlendRadius().getValue(), v -> o.getBiomeBlendRadius().setValue(v), ""));
        y += rowH + 4;

        // Toggles
        addDrawableChild(cycle(x, y, colW, "Clouds", () -> o.getCloudRenderMode().getValue().name(),
                () -> {
                    CloudRenderMode m = o.getCloudRenderMode().getValue();
                    o.getCloudRenderMode().setValue(m == CloudRenderMode.OFF ? CloudRenderMode.FAST
                            : m == CloudRenderMode.FAST ? CloudRenderMode.FANCY : CloudRenderMode.OFF);
                }));
        addDrawableChild(cycle(x + colW + 8, y, colW, "Particles", () -> o.getParticles().getValue().name(),
                () -> {
                    ParticlesMode m = o.getParticles().getValue();
                    o.getParticles().setValue(m == ParticlesMode.ALL ? ParticlesMode.DECREASED
                            : m == ParticlesMode.DECREASED ? ParticlesMode.MINIMAL : ParticlesMode.ALL);
                }));
        y += rowH;
        addDrawableChild(cycle(x, y, colW, "Graphics", () -> o.getGraphicsMode().getValue().name(),
                () -> {
                    GraphicsMode m = o.getGraphicsMode().getValue();
                    o.getGraphicsMode().setValue(m == GraphicsMode.FAST ? GraphicsMode.FANCY : GraphicsMode.FAST);
                }));
        addDrawableChild(cycle(x + colW + 8, y, colW, "VSync", () -> onOff(o.getEnableVsync().getValue()),
                () -> o.getEnableVsync().setValue(!o.getEnableVsync().getValue())));
        y += rowH;
        addDrawableChild(cycle(x, y, colW, "Entity Shadows", () -> onOff(o.getEntityShadows().getValue()),
                () -> o.getEntityShadows().setValue(!o.getEntityShadows().getValue())));
        addDrawableChild(cycle(x + colW + 8, y, colW, "Smooth Lighting", () -> onOff(o.getAo().getValue()),
                () -> o.getAo().setValue(!o.getAo().getValue())));
        y += rowH;
        addDrawableChild(cycle(x, y, colW, "View Bobbing", () -> onOff(o.getViewBobbing().getValue()),
                () -> o.getViewBobbing().setValue(!o.getViewBobbing().getValue())));

        addDrawableChild(ButtonWidget.builder(Text.literal("Done"), b -> close())
                .dimensions(x + colW + 8, y, colW, 20).build());
    }

    private static String onOff(boolean v) {
        return v ? "ON" : "OFF";
    }

    /** A button that shows "Label: VALUE" and runs an action on click. */
    private ButtonWidget cycle(int x, int y, int w, String label,
                               java.util.function.Supplier<String> value, Runnable action) {
        return ButtonWidget.builder(Text.literal(label + ": " + value.get()), b -> {
            action.run();
            b.setMessage(Text.literal(label + ": " + value.get()));
        }).dimensions(x, y, w, 20).build();
    }

    private enum Preset { POTATO, BALANCED, QUALITY }

    private void applyPreset(Preset p) {
        GameOptions o = this.client.options;
        switch (p) {
            case POTATO -> {
                o.getViewDistance().setValue(6);
                o.getSimulationDistance().setValue(5);
                o.getMaxFps().setValue(260);
                o.getEnableVsync().setValue(false);
                o.getCloudRenderMode().setValue(CloudRenderMode.OFF);
                o.getParticles().setValue(ParticlesMode.MINIMAL);
                o.getEntityShadows().setValue(false);
                o.getBiomeBlendRadius().setValue(0);
                o.getGraphicsMode().setValue(GraphicsMode.FAST);
                o.getAo().setValue(false);
            }
            case BALANCED -> {
                o.getViewDistance().setValue(10);
                o.getSimulationDistance().setValue(8);
                o.getMaxFps().setValue(144);
                o.getEnableVsync().setValue(false);
                o.getCloudRenderMode().setValue(CloudRenderMode.FAST);
                o.getParticles().setValue(ParticlesMode.DECREASED);
                o.getEntityShadows().setValue(false);
                o.getBiomeBlendRadius().setValue(2);
                o.getGraphicsMode().setValue(GraphicsMode.FAST);
                o.getAo().setValue(true);
            }
            case QUALITY -> {
                o.getViewDistance().setValue(16);
                o.getSimulationDistance().setValue(12);
                o.getMaxFps().setValue(144);
                o.getEnableVsync().setValue(true);
                o.getCloudRenderMode().setValue(CloudRenderMode.FANCY);
                o.getParticles().setValue(ParticlesMode.ALL);
                o.getEntityShadows().setValue(true);
                o.getBiomeBlendRadius().setValue(5);
                o.getGraphicsMode().setValue(GraphicsMode.FANCY);
                o.getAo().setValue(true);
            }
        }
        // Rebuild widgets so every label reflects the new values.
        this.clearAndInit();
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderBackground(context, mouseX, mouseY, delta);
        int l = left(), t = top();
        context.fill(l - 1, t - 1, l + PANEL_W + 1, t + PANEL_H + 1, PANEL_EDGE);
        context.fill(l, t, l + PANEL_W, t + PANEL_H, PANEL_BG);
        context.fill(l, t, l + PANEL_W, t + 2, ACCENT);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        int l = left(), t = top();
        context.drawTextWithShadow(this.textRenderer,
                Text.literal("7KBPS").formatted(Formatting.BOLD, Formatting.GREEN), l + 12, t + 12, 0xFFFFFFFF);
        context.drawTextWithShadow(this.textRenderer,
                Text.literal("performance panel").formatted(Formatting.GRAY),
                l + 12 + this.textRenderer.getWidth("7KBPS") + 8, t + 12, 0xFFAAAAAA);
        context.drawTextWithShadow(this.textRenderer,
                Text.literal("Right Shift to open").formatted(Formatting.DARK_GRAY),
                l + PANEL_W - 12 - this.textRenderer.getWidth("Right Shift to open"), t + 12, 0xFF555555);
    }

    @Override
    public void close() {
        this.client.options.write();
        this.client.setScreen(parent);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    /** Integer slider bound to a getter/setter. */
    private static class IntSlider extends SliderWidget {
        private final String label;
        private final int min, max;
        private final Consumer<Integer> setter;
        private final String suffix;

        IntSlider(int x, int y, int w, int h, String label, int min, int max,
                  IntSupplier getter, Consumer<Integer> setter, String suffix) {
            super(x, y, w, h, Text.empty(), (getter.getAsInt() - min) / (double) (max - min));
            this.label = label;
            this.min = min;
            this.max = max;
            this.setter = setter;
            this.suffix = suffix;
            updateMessage();
        }

        private int current() {
            return min + (int) Math.round(this.value * (max - min));
        }

        @Override
        protected void updateMessage() {
            int v = current();
            String shown = (max == 260 && v >= 260) ? "Unlimited" : v + suffix;
            setMessage(Text.literal(label + ": " + shown));
        }

        @Override
        protected void applyValue() {
            setter.accept(current());
        }
    }
}
