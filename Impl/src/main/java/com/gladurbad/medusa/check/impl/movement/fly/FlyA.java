package com.gladurbad.medusa.check.impl.movement.fly;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;
import com.gladurbad.medusa.util.MathUtil;
import com.gladurbad.medusa.util.PlayerUtil;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;

import java.util.function.Predicate;

/**
 * Created on 11/17/2020 Package com.gladurbad.medusa.check.impl.movement.fly by GladUrBad
 */

@CheckInfo(name = "Fly (A)", description = "Checks for gravity.")
public final class FlyA extends Check {

    public FlyA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isPosition()) {
            final double deltaY = data.getPositionProcessor().getDeltaY();
            final double lastDeltaY = data.getPositionProcessor().getLastDeltaY();

            final boolean onGround = data.getPositionProcessor().getAirTicks() <= 5;

            final double prediction = Math.abs((lastDeltaY - 0.08) * 0.98F) < 0.005 ? 0 : (lastDeltaY - 0.08) * 0.98F;
            final double difference = Math.abs(deltaY - prediction);

            final boolean exempt = isExempt(
                    ExemptType.TELEPORT, ExemptType.NEAR_VEHICLE, ExemptType.FLYING,
                    ExemptType.INSIDE_VEHICLE, ExemptType.VELOCITY
            );

            final boolean invalid = !exempt
                    && difference > 0.001D
                    && !(data.getPositionProcessor().getY() % 0.5 == 0 && data.getPositionProcessor().isOnGround() && lastDeltaY < 0);

            debug("posY=" + data.getPositionProcessor().getY() + " dY=" + deltaY + " at=" + data.getPositionProcessor().getAirTicks());
            if (invalid) {
                if (!onGround) {
                    buffer += buffer < 50 ? 10 : 0;
                    if (buffer > 30) {
                        fail(String.format(
                                "diff=%.4f, buffer=%.2f, at=%o",
                                difference, buffer, data.getPositionProcessor().getAirTicks()
                        ));
                    }
                }
            } else {
                buffer = Math.max(buffer - 0.75, 0);
            }
        }
    }
}
