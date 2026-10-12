package mc.egor4787.b2c.client.configuration;

import mc.egor4787.b2c.client.B2C;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public class ConfigScreen extends Screen {
    private final Screen parent;
    private final EditBox[] fields = new EditBox[B2C.COMMANDS.length];

    public ConfigScreen(Screen parent) {
        super(Component.literal("B2C"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;

        for (int i = 0; i < B2C.COMMANDS.length; i++) {
            int y = 10 + i * 34;

            EditBox field = new EditBox(
                    this.font,
                    centerX - 160, y + 10,
                    320, 20,
                    Component.literal("Command " + (i + 1))
            );

            field.setMaxLength(255);
            field.setValue(B2C.COMMANDS[i]);

            fields[i] = field;
            this.addRenderableWidget(field);
        }

        addRenderableWidget(
                Button.builder(
                                Component.translatable("gui.done"),
                                _ -> onClose()
                        )
                        .bounds(centerX - 100, this.height - 30, 200, 20)
                        .build()
        );
    }

    private void saveFields() {
        for (int i = 0; i < fields.length; i++) {
            if (fields[i] != null) {
                B2C.COMMANDS[i] = fields[i].getValue();
            }
        }

        B2C.saveConfig();
    }

    @Override
    public void removed() {
        saveFields();
        super.removed();
    }

    @Override
    public void extractRenderState(
            @NonNull GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            float delta
    ) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);

        int centerX = this.width / 2;

        for (int i = 0; i < B2C.COMMANDS.length; i++) {
            graphics.text(
                    this.font,
                    "Bind " + (i + 1),
                    centerX - 160,
                    10 + i * 34,
                    0xFFFFFFFF,
                    false
            );
        }
    }

    @Override
    public void onClose() {
        saveFields();
        this.minecraft.gui.setScreen(parent);
    }
}
