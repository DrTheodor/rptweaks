package dev.drtheo.rptweaks.mixininterface;

public interface ServerInfoLike {
    String rpt$address();

    default boolean rpt$equals(ServerInfoLike other) {
        return other == this || this.rpt$equals(other.rpt$address());
    }

    default boolean rpt$equals(String address) {
        return this.rpt$address().equals(address);
    }
}
