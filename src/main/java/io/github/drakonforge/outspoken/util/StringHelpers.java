package io.github.drakonforge.outspoken.util;

import com.hypixel.hytale.math.vector.Vector3d;

public final class StringHelpers {

    private StringHelpers() {}

    public static String vector3dToString(Vector3d vector3d) {
        return "(" + vector3d.getX() + ", " + vector3d.getY() + ", " + vector3d.getZ() + ")";
    }
}
