package com.hibiscusmc.hmccosmetics.user.manager;

import com.hibiscusmc.hmccosmetics.util.PlayerUtils;
import com.hibiscusmc.hmccosmetics.util.packets.PacketManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserBalloonPufferfish extends UserEntity {

    private int id;
    private UUID uuid;

    public UserBalloonPufferfish(UUID owner, int id, UUID uuid) {
        super(owner);
        this.id = id;
        this.uuid = uuid;
    }

    public int getId() {
        return id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void hidePufferfish() {
        PacketManager.sendEntityDestroyPacket(id, getViewers());
        getViewers().clear();
    }
}
