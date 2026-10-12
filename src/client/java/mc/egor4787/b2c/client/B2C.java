package mc.egor4787.b2c.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class B2C implements ClientModInitializer {
    public static final String MOD_ID = "b2c";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final Path CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("b2c.json");

    public static final String[] COMMANDS = {
            "", "", "", "", "", ""
    };

    private static final KeyMapping[] BINDS = new KeyMapping[COMMANDS.length];

	private static final KeyMapping.Category B2C_CATEGORY =
			KeyMapping.Category.register(
					Identifier.parse("b2c:keybindings")
			);

    @Override
    public void onInitializeClient() {
        loadConfig();

        for (int i = 0; i < BINDS.length; i++) {
            BINDS[i] = KeyMappingHelper.registerKeyMapping(
                    new KeyMapping(
                            "key.b2c.bind_" + (i + 1),
                            InputConstants.Type.KEYSYM,
							GLFW.GLFW_KEY_UNKNOWN,
							B2C_CATEGORY
                    )
            );
        }

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            for (int i = 0; i < BINDS.length; i++) {
                while (BINDS[i].consumeClick()) {
                    execute(client, i);
                }
            }
        });
    }

    private static void execute(Minecraft client, int i) {
        if (client.player == null || client.getConnection() == null) return;

        String command = COMMANDS[i].trim();

        if (command.isEmpty()) return;

		if (command.startsWith("/")) {
			command = command.substring(1);
		}

		client.getConnection().sendCommand(command);
	}

	public static void saveConfig() {
		try {
			Files.createDirectories(CONFIG_FILE.getParent());
			Files.writeString(CONFIG_FILE, GSON.toJson(COMMANDS));
		} catch (IOException e) {
			LOGGER.error("Failed to save config file!", e);
		}
	}

	private static void loadConfig() {
		if (!Files.exists(CONFIG_FILE)) {
			saveConfig();
			return;
		}

		try {
			String[] loaded = GSON.fromJson(
					Files.readString(CONFIG_FILE),
					String[].class
			);

			if (loaded == null || loaded.length != COMMANDS.length) throw new JsonParseException("Expected 6 commands");

			System.arraycopy(loaded, 0, COMMANDS, 0, COMMANDS.length);

			for (int i = 0; i < COMMANDS.length; i++) {
				if (COMMANDS[i] == null) {
					COMMANDS[i] = "";
				}
			}
		} catch (IOException | JsonParseException e) {
			LOGGER.error("Failed to load config", e);
		}
	}
}