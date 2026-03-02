package dev.diamond.luafy.holder;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.CustomData;

public class HolderItem {



    public static ItemStack getItem(ServerLevel level) {
        ItemStack itemStack = Items.ENCHANTED_BOOK.getDefaultInstance();

        itemStack.set(DataComponents.RARITY, Rarity.UNCOMMON);
        itemStack.set(DataComponents.ITEM_NAME, Component.literal("Script Holder"));
        itemStack.set(DataComponents.ITEM_MODEL, Identifier.withDefaultNamespace("command_block"));
        itemStack.set(DataComponents.MAX_STACK_SIZE, 64);
        itemStack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);

        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putBoolean("script_holder_block", true);
        CustomData customData = CustomData.of(compoundTag);
        itemStack.set(DataComponents.CUSTOM_DATA, customData);

        return itemStack;
    }


}
