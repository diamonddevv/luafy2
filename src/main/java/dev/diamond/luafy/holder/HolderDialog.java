package dev.diamond.luafy.holder;

import dev.diamond.luafy.Luafy;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.dialog.Dialog;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class HolderDialog {

    public static final Identifier ID = Luafy.id("modify_script_holder");


    public static void open(Level level, Player player) {
        RegistryAccess access = level.registryAccess();
        Registry<Dialog> reg = access.lookupOrThrow(Registries.DIALOG);
        Optional<Holder.Reference<Dialog>> optional = reg.get(ID);

        optional.ifPresent(player::openDialog);
    }


}
