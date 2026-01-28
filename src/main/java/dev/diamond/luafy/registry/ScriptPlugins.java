package dev.diamond.luafy.registry;

import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.script.ApiScriptPlugin;
import dev.diamond.luafy.script.ScriptPlugin;
import dev.diamond.luafy.script.api.*;
import net.minecraft.core.Registry;
import org.luaj.vm2.LoadState;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.Bit32Lib;
import org.luaj.vm2.lib.CoroutineLib;
import org.luaj.vm2.lib.PackageLib;
import org.luaj.vm2.lib.TableLib;
import org.luaj.vm2.lib.jse.*;

public class ScriptPlugins {

    public static ScriptPlugin LUAJ_JSE_BASE     = new ScriptPlugin(s -> s.getGlobals().load(new JseBaseLib()));
    public static ScriptPlugin LUAJ_PACKAGE      = new ScriptPlugin(s -> s.getGlobals().load(new PackageLib()));
    public static ScriptPlugin LUAJ_BIT32        = new ScriptPlugin(s -> s.getGlobals().load(new Bit32Lib()));
    public static ScriptPlugin LUAJ_TABLE        = new ScriptPlugin(s -> s.getGlobals().load(new TableLib()));
    public static ScriptPlugin LUAJ_JSE_STRING   = new ScriptPlugin(s -> s.getGlobals().load(new JseStringLib()));
    public static ScriptPlugin LUAJ_COROUTINE    = new ScriptPlugin(s -> s.getGlobals().load(new CoroutineLib()));
    public static ScriptPlugin LUAJ_JSE_MATH     = new ScriptPlugin(s -> s.getGlobals().load(new JseMathLib()));
    public static ScriptPlugin LUAJ_JSE_IO       = new ScriptPlugin(s -> s.getGlobals().load(new JseIoLib()));
    public static ScriptPlugin LUAJ_JSE_OS       = new ScriptPlugin(s -> s.getGlobals().load(new JseOsLib()));
    public static ScriptPlugin LUAJ_LUAJAVA      = new ScriptPlugin(s -> s.getGlobals().load(new LuajavaLib())); // lets not use luajava
    public static ScriptPlugin LUAJ_LUAC         = new ScriptPlugin(s -> LuaC.install(s.getGlobals()));
    public static ScriptPlugin LUAJ_LOADSTATE    = new ScriptPlugin(s -> LoadState.install(s.getGlobals()));

    public static ApiScriptPlugin<MinecraftApi> MINECRAFT = new ApiScriptPlugin<>(MinecraftApi::new);
    public static ApiScriptPlugin<LuafyApi> LUAFY = new ApiScriptPlugin<>(LuafyApi::new);
    public static ApiScriptPlugin<MathApi> MATH = new ApiScriptPlugin<>(MathApi::new);
    public static ApiScriptPlugin<FabricApi> FABRIC = new ApiScriptPlugin<>(FabricApi::new);
    public static ApiScriptPlugin<TextApi> TEXT = new ApiScriptPlugin<>(TextApi::new);

    public static void registerAll() {
        Registry.register(LuafyRegistries.SCRIPT_PLUGINS, Luafy.id_luaj("jse_base"), LUAJ_JSE_BASE);
        Registry.register(LuafyRegistries.SCRIPT_PLUGINS, Luafy.id_luaj("package"), LUAJ_PACKAGE);
        Registry.register(LuafyRegistries.SCRIPT_PLUGINS, Luafy.id_luaj("bit32"), LUAJ_BIT32);
        Registry.register(LuafyRegistries.SCRIPT_PLUGINS, Luafy.id_luaj("table"), LUAJ_TABLE);
        Registry.register(LuafyRegistries.SCRIPT_PLUGINS, Luafy.id_luaj("jse_string"), LUAJ_JSE_STRING);
        Registry.register(LuafyRegistries.SCRIPT_PLUGINS, Luafy.id_luaj("coroutine"), LUAJ_COROUTINE);
        Registry.register(LuafyRegistries.SCRIPT_PLUGINS, Luafy.id_luaj("jse_math"), LUAJ_JSE_MATH);
        Registry.register(LuafyRegistries.SCRIPT_PLUGINS, Luafy.id_luaj("jse_io"), LUAJ_JSE_IO);
        Registry.register(LuafyRegistries.SCRIPT_PLUGINS, Luafy.id_luaj("jse_os"), LUAJ_JSE_OS);
        //Registry.register(LuafyRegistries.SCRIPT_PLUGINS, Luafy.id_luaj("luajava"), LUAJ_LUAJAVA);
        Registry.register(LuafyRegistries.SCRIPT_PLUGINS, Luafy.id_luaj("luac"), LUAJ_LUAC);
        Registry.register(LuafyRegistries.SCRIPT_PLUGINS, Luafy.id_luaj("loadstate"), LUAJ_LOADSTATE);

        Registry.register(LuafyRegistries.SCRIPT_PLUGINS, Luafy.id("minecraft"), MINECRAFT);
        Registry.register(LuafyRegistries.SCRIPT_PLUGINS, Luafy.id("luafy"), LUAFY);
        Registry.register(LuafyRegistries.SCRIPT_PLUGINS, Luafy.id("math"), MATH);
        Registry.register(LuafyRegistries.SCRIPT_PLUGINS, Luafy.id("fabric"), FABRIC);
        Registry.register(LuafyRegistries.SCRIPT_PLUGINS, Luafy.id("text"), TEXT);
    }
}
