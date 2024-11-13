package com.hibiscusmc.hmccosmetics.cosmetic.types;

import com.hibiscusmc.hmccosmetics.HMCCosmeticsPlugin;
import com.hibiscusmc.hmccosmetics.config.Settings;
import com.hibiscusmc.hmccosmetics.cosmetic.Cosmetic;
import com.hibiscusmc.hmccosmetics.user.CosmeticUser;
import com.hibiscusmc.hmccosmetics.user.manager.UserBalloonManager;
import com.hibiscusmc.hmccosmetics.util.HMCCPlayerUtils;
import com.hibiscusmc.hmccosmetics.util.MessagesUtil;
import com.hibiscusmc.hmccosmetics.util.packets.HMCCPacketManager;
import io.github.retrooper.packetevents.util.folia.FoliaScheduler;
import lombok.Getter;
import me.lojosho.shaded.configurate.ConfigurationNode;
import me.lojosho.shaded.configurate.serialize.SerializationException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CosmeticBalloonType extends Cosmetic {

    @Getter
    private final String modelName;
    @Getter
    private List<String> dyableParts;
    @Getter
    private final boolean showLead;
    @Getter
    private Vector balloonOffset;

    public CosmeticBalloonType(String id, ConfigurationNode config) {
        super(id, config);

        String modelId = config.node("model").getString();
        showLead = config.node("show-lead").getBoolean(true);

        ConfigurationNode balloonOffsetNode = config.node("balloon-offset");
        if (balloonOffsetNode.virtual())
            balloonOffset = Settings.getBalloonOffset();
        else
            balloonOffset = Settings.loadVector(balloonOffsetNode);

        try {
            if (!config.node("dyable-parts").virtual()) {
                dyableParts = config.node("dyable-parts").getList(String.class);
            }
        } catch (SerializationException e) {
            // Seriously?
            throw new RuntimeException(e);
        }
        if (modelId != null) modelId = modelId.toLowerCase(); // ME only accepts lowercase
        this.modelName = modelId;
    }

    @Override
    public void update(@NotNull CosmeticUser user) {
        Entity entity = Bukkit.getEntity(user.getUniqueId());
        UserBalloonManager userBalloonManager = user.getBalloonManager();

        if (entity == null || userBalloonManager == null) return;
        if (user.isInWardrobe()) return;

        if (userBalloonManager.getModelEntity().getLocation().getWorld() != entity.getWorld()) {
            MessagesUtil.sendDebugMessages("Model Entity: " + userBalloonManager.getModelEntity().getLocation().getWorld() + "// Player" + entity.getWorld());
            FoliaScheduler.getEntityScheduler().run(userBalloonManager.getModelEntity(), HMCCosmeticsPlugin.getInstance(), (t) ->
            user.respawnBalloon(),null);
            return;
        }

        Location newLocation = entity.getLocation();
        Location currentLocation = user.getBalloonManager().getLocation();
        newLocation = newLocation.clone().add(getBalloonOffset());
        if (Settings.isBalloonHeadForward()) newLocation.setPitch(0);

        List<Player> viewer = HMCCPacketManager.getViewers(entity.getLocation());

        if (entity.getLocation().getWorld() != userBalloonManager.getLocation().getWorld()) {
            userBalloonManager.getModelEntity().teleportAsync(newLocation);
            HMCCPacketManager.sendTeleportPacket(userBalloonManager.getPufferfishBalloonId(), newLocation, false, viewer);
            return;
        }

        Vector velocity = newLocation.toVector().subtract(currentLocation.toVector());
        userBalloonManager.setVelocity(velocity.multiply(1.1));
        userBalloonManager.setLocation(newLocation);

        HMCCPacketManager.sendTeleportPacket(userBalloonManager.getPufferfishBalloonId(), newLocation, false, viewer);
        HMCCPacketManager.sendLeashPacket(userBalloonManager.getPufferfishBalloonId(), entity.getEntityId(), viewer);
        if (user.isHidden()) {
            userBalloonManager.getPufferfish().hidePufferfish();
            return;
        }
        if (!user.isHidden() && showLead) {
            List<Player> sendTo = userBalloonManager.getPufferfish().refreshViewers(newLocation);
            if (sendTo.isEmpty()) return;
            user.getBalloonManager().getPufferfish().spawnPufferfish(newLocation, sendTo);
        }
    }

    public boolean isDyablePart(String name) {
        // If player does not define parts, dye whole model
        if (dyableParts == null) return true;
        if (dyableParts.isEmpty()) return true;
        return dyableParts.contains(name);
    }
}
