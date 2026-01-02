package dev.diamond.luafy;

import dev.diamond.luafy.registry.LuafyRegistries;
import dev.diamond.luafy.registry.ScriptPlugins;
import dev.diamond.luafy.script.ScriptManager;
import dev.diamond.luafy.script.ScriptResourceLoader;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.impl.resource.loader.ResourceManagerHelperImpl;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Luafy implements ModInitializer {
	public static final String MOD_ID = "luafy";
	public static final String LUAJ_VER = "3.0.8";


	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


	public static final ScriptManager SCRIPT_MANAGER = new ScriptManager();
	public static final ScriptResourceLoader SCRIPT_RESOURCE_LOADER = new ScriptResourceLoader();


	@Override
	public void onInitialize() {

		LOGGER.info("initialising luafy ({} w/ luaj {}) ; {}",
				FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow().getMetadata().getVersion().getFriendlyString(),
				LUAJ_VER,
				HelloWorldSupplier.supply(System.currentTimeMillis())
		);

		LuafyRegistries.register();
		CommandRegistrationCallback.EVENT.register(LuafyCommand::register);
		ResourceManagerHelperImpl.get(ResourceType.SERVER_DATA).registerReloadListener(SCRIPT_RESOURCE_LOADER);

		ScriptPlugins.registerAll();

	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}

	public static Identifier id_luaj(String path) {
		return Identifier.of("luaj", path);
	}
}