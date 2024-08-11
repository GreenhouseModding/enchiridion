package dev.greenhouseteam.enchiridion.network.clientbound;

import dev.greenhouseteam.enchiridion.Enchiridion;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public record SyncEnchantmentLevelUpSeedsClientboundPacket(int entityId, int index, int seed, boolean clear) implements CustomPacketPayload {
    public static final ResourceLocation ID = Enchiridion.asResource("sync_enchantment_level_up_seeds");
    public static final Type<SyncEnchantmentLevelUpSeedsClientboundPacket> TYPE = new Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, SyncEnchantmentLevelUpSeedsClientboundPacket> STREAM_CODEC = CustomPacketPayload.codec(
            SyncEnchantmentLevelUpSeedsClientboundPacket::write, SyncEnchantmentLevelUpSeedsClientboundPacket::new
    );

    public static SyncEnchantmentLevelUpSeedsClientboundPacket add(int entityId, int index, int seed) {
        return new SyncEnchantmentLevelUpSeedsClientboundPacket(entityId, index, seed, false);
    }

    public static SyncEnchantmentLevelUpSeedsClientboundPacket clear(int entityId) {
        return new SyncEnchantmentLevelUpSeedsClientboundPacket(entityId, -1, -1, true);
    }

    public SyncEnchantmentLevelUpSeedsClientboundPacket(FriendlyByteBuf buf) {
        this(buf.readInt(), buf.readInt(), buf.readInt(), buf.readBoolean());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeInt(index);
        buf.writeInt(seed);
        buf.writeBoolean(clear);
    }

    public void handle() {
        Minecraft.getInstance().execute(() -> {
            Entity entity = Minecraft.getInstance().level.getEntity(entityId);
            if (!(entity instanceof Player player))
                return;
            if (clear)
                Enchiridion.getHelper().clearEnchantmentSeeds(player);
            else
                Enchiridion.getHelper().addEnchantmentSeed(player, index, seed);
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
