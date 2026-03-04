package dev.diamond.luafy.mixin;

import dev.diamond.luafy.holder.HolderDialog;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {


    @Inject(method = "handleCustomClickAction", at = @At("HEAD"), cancellable = true)
    public void luafy$scriptHolderDialogHandler(Identifier id, Optional<Tag> tag, CallbackInfo ci) {
        if (tag.isPresent()) {
            if (id == HolderDialog.ID) {


                System.out.println(tag);

                ci.cancel();
            }
        }
    }
}
