package dev.diamond.luafy.holder;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.diamond.luafy.Luafy;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Optional;

public class ScriptHolderBlockItem {

    public static final String KEY = "script_holder_block";

    public static final AttachmentType<ScriptHolderAttachment> SCRIPT_HOLDER_ATTACHMENT = AttachmentRegistry.create(
            Luafy.id("script_holder"),
            builder -> builder
                    .initializer(() -> new ScriptHolderAttachment(null))
                    .persistent(ScriptHolderAttachment.CODEC)
    );

    public static ItemStack getItem(ServerLevel level) {
        ItemStack itemStack = new ItemStack(Items.ENDER_CHEST);

        itemStack.set(DataComponents.RARITY, Rarity.UNCOMMON);
        itemStack.set(DataComponents.ITEM_NAME, Component.literal("Script Holder"));
        itemStack.set(DataComponents.MAX_STACK_SIZE, 64);
        itemStack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);

        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putBoolean(KEY, true);
        CustomData customData = CustomData.of(compoundTag);
        itemStack.set(DataComponents.CUSTOM_DATA, customData);

        return itemStack;
    }

    private static boolean isHoldingItem(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        return isItem(stack);
    }

    public static boolean isItem(ItemStack stack) {
        CustomData customData = stack.getComponents().get(DataComponents.CUSTOM_DATA);

        if (customData == null || stack.getCount() < 1)
            return false;

        CompoundTag compoundTag = customData.copyTag();
        return compoundTag.getBoolean(KEY).orElse(false);
    }

    private static InteractionResult useBlock(Player player, Level level, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        MinecraftServer server = level.getServer();

        if (server == null || level.isClientSide() || player.isSpectator()) return InteractionResult.PASS;

        BlockEntity be = level.getBlockEntity(blockHitResult.getBlockPos());
        if (be instanceof EnderChestBlockEntity && player.canUseGameMasterBlocks()) {

            if (ScriptHolderBlockItem.ScriptHolderAttachment.hasHolderAttachment(be)) {

                HolderDialog.open(level, player);

                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }



    public static void registerHolderBlockStuff() {
        UseBlockCallback.EVENT.register(ScriptHolderBlockItem::useBlock);
    }




    public record ScriptHolderAttachment(Optional<Identifier> scriptId) {
        public static final Codec<ScriptHolderAttachment> CODEC = RecordCodecBuilder.create(instance -> instance.group(
           Identifier.CODEC.optionalFieldOf("script_id").forGetter(ScriptHolderAttachment::scriptId)
        ).apply(instance, ScriptHolderAttachment::new));

        public static boolean hasHolderAttachment(BlockEntity blockEntity) {
            return blockEntity.hasAttached(SCRIPT_HOLDER_ATTACHMENT);
        }

        public static ScriptHolderAttachment giveHolderAttachment(BlockEntity holderBlockEntity) {
            return holderBlockEntity.getAttachedOrCreate(SCRIPT_HOLDER_ATTACHMENT);
        }

        public static Optional<Identifier> getHolderScriptId(BlockEntity holderBlockEntity) {
            assert hasHolderAttachment(holderBlockEntity);
            return holderBlockEntity.getAttached(SCRIPT_HOLDER_ATTACHMENT).scriptId();
        }
    }

}
