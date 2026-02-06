package dev.diamond.luafy.registry;

import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.script.event.ScriptEvent;
import dev.diamond.luafy.script.type.*;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

public class StringAliases {

    public static final IdentifierStringAlias IDENTIFIER = new IdentifierStringAlias();

    public static final RegistryIdentifierStringAlias<Argtype<?, ?>> REGISTRY_ARGTYPE = new RegistryIdentifierStringAlias<>(LuafyRegistries.ARGTYPES, "Argtype");
    public static final RegistryIdentifierStringAlias<ScriptEvent<?>> REGISTRY_SCRIPT_EVENT = new RegistryIdentifierStringAlias<>(LuafyRegistries.SCRIPT_EVENTS, "ScriptEvent");

    public static final RegistryKeyIdentifierStringAlias<Item> REGISTRY_ITEM = new RegistryKeyIdentifierStringAlias<>(Registries.ITEM, "Item");
    public static final RegistryKeyIdentifierStringAlias<Block> REGISTRY_BLOCK = new RegistryKeyIdentifierStringAlias<>(Registries.BLOCK, "Block");
    public static final RegistryKeyIdentifierStringAlias<EntityType<?>> REGISTRY_ENTITY_TYPE = new RegistryKeyIdentifierStringAlias<>(Registries.ENTITY_TYPE, "EntityType");
    public static final RegistryKeyIdentifierStringAlias<Biome> REGISTRY_BIOME = new RegistryKeyIdentifierStringAlias<>(Registries.BIOME, "Biome");
    public static final RegistryKeyIdentifierStringAlias<Level> REGISTRY_DIMENSION = new RegistryKeyIdentifierStringAlias<>(Registries.DIMENSION, "Dimension");
    public static final RegistryKeyIdentifierStringAlias<Enchantment> REGISTRY_ENCHANTMENT = new RegistryKeyIdentifierStringAlias<>(Registries.ENCHANTMENT, "Enchantment");
    public static final RegistryKeyIdentifierStringAlias<MobEffect> REGISTRY_EFFECT = new RegistryKeyIdentifierStringAlias<>(Registries.MOB_EFFECT, "StatusEffect");


    public static void registerAll() {
        register(Luafy.id("identifier"), IDENTIFIER);

        register(Luafy.id("argtype_identifier"), REGISTRY_ARGTYPE);
        register(Luafy.id("script_event_identifier"), REGISTRY_SCRIPT_EVENT);
        register(Luafy.id("item_identifier"), REGISTRY_ITEM);
        register(Luafy.id("block_identifier"), REGISTRY_BLOCK);
        register(Luafy.id("entity_type_identifier"), REGISTRY_ENTITY_TYPE);
        register(Luafy.id("biome_identifier"), REGISTRY_BIOME);
        register(Luafy.id("dimension_identifier"), REGISTRY_DIMENSION);
        register(Luafy.id("enchantment_identifier"), REGISTRY_ENCHANTMENT);
        register(Luafy.id("effect_identifier"), REGISTRY_EFFECT);
    }

    public static void register(Identifier id, StringAlias<?> alias) {
        Registry.register(LuafyRegistries.STRING_ALIASES, id, alias);
        Registry.register(LuafyRegistries.ARGTYPES, id, alias);
    }
}
