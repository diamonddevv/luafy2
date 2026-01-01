package dev.diamond.luafy;

import dev.diamond.luafy.script.ScriptManager;
import dev.diamond.luafy.script.ScriptResourceLoader;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.impl.resource.loader.ResourceManagerHelperImpl;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Luafy implements ModInitializer {
	public static final String MOD_ID = "luafy";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


	public static final ScriptManager SCRIPT_MANAGER = new ScriptManager();
	public static final ScriptResourceLoader SCRIPT_RESOURCE_LOADER = new ScriptResourceLoader();


	@Override
	public void onInitialize() {

		LOGGER.info("hei, maailma!");


		CommandRegistrationCallback.EVENT.register(LuafyCommand::register);
		ResourceManagerHelperImpl.get(ResourceType.SERVER_DATA).registerReloadListener(SCRIPT_RESOURCE_LOADER);
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}