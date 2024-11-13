package com.hibiscusmc.hmccosmetics.util;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.hibiscusmc.hmccosmetics.HMCCosmeticsPlugin;
import com.hibiscusmc.hmccosmetics.config.Settings;
import io.github.retrooper.packetevents.util.folia.FoliaScheduler;
import me.lojosho.hibiscuscommons.util.packets.PacketManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HMCCPlayerUtils {

    @Nullable
    public static WrappedSignedProperty getSkin(Player player) {
        WrappedSignedProperty skinData = WrappedGameProfile.fromPlayer(player).getProperties()
                .get("textures").stream().findAny().orElse(null);

        if (skinData == null) {
            return null;
        }
        return new WrappedSignedProperty("textures", skinData.getValue(), skinData.getSignature());
    }
    // Temporary fix
    // Look for rewrite
    @NotNull
    public static List<Player> getNearbyPlayers(@NotNull Player player) {
        return new ArrayList<>(Bukkit.getOnlinePlayers());
    }
    // Temporary fix
    // Look for rewrite
    @NotNull
    public static List<Player> getNearbyPlayers(@NotNull Location location) {
        return new ArrayList<>(Bukkit.getOnlinePlayers());
    }
}
