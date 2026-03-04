package dev.diamond.luafy.mixin;

import dev.diamond.luafy.holder.ScriptHolderBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BlockItem.class)
public class BlockItemMixin {


    @Inject(method = "place", at = @At(value="INVOKE",target="Lnet/minecraft/world/item/ItemStack;consume(ILnet/minecraft/world/entity/LivingEntity;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void luafy$onScriptolderBlockPlaced(BlockPlaceContext blockPlaceContext, CallbackInfoReturnable<InteractionResult> cir, BlockPlaceContext no, BlockState no2, BlockPos blockPos, Level level, Player player, ItemStack itemStack, BlockState blockState, SoundType soundType) {

        if (ScriptHolderBlockItem.isItem(itemStack)) {
            // get command block entity
            if (blockState.hasBlockEntity()) {
                BlockEntity be = level.getBlockEntity(blockPos);
                assert be != null;
                ScriptHolderBlockItem.ScriptHolderAttachment.giveHolderAttachment(be);
            }
        }

    }
}
