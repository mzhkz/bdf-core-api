package net.bdfps.api.spigot.java.packet.original;

/**
 * Created by hsyhrs on 2017/07/13.
 */
public class PacketContainer {
    private static NMSCorpses_v1_12_R1 nmsCorpses_vv1_12_R11;
    private static NMSFakeBow_v1_12_R1 nmsFakeBow_vv1_12_R11;
    private static NMSHitBoxManager_v1_12_R1 nmsHitBoxManager_vv1_12_R11;

    public PacketContainer() {
        nmsCorpses_vv1_12_R11 = new NMSCorpses_v1_12_R1();
        nmsFakeBow_vv1_12_R11 = new NMSFakeBow_v1_12_R1();
        nmsHitBoxManager_vv1_12_R11 = new NMSHitBoxManager_v1_12_R1();
    }

    public NMSCorpses_v1_12_R1 getNmsCorpses_vv1_12_R11() {
        return nmsCorpses_vv1_12_R11;
    }

    public NMSFakeBow_v1_12_R1 getNmsFakeBow_vv1_12_R11() {
        return nmsFakeBow_vv1_12_R11;
    }

    public static NMSHitBoxManager_v1_12_R1 getNmsHitBoxManager_vv1_12_R11() {
        return nmsHitBoxManager_vv1_12_R11;
    }
}
