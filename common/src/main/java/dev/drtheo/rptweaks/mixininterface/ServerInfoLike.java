package dev.drtheo.rptweaks.mixininterface;

public interface ServerInfoLike {
    String rptweaks$address();

    default boolean rptweaks$equals(ServerInfoLike other) {
        return other == this || this.rptweaks$address().equals(other.rptweaks$address());
    }
}
