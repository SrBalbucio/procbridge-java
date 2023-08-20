package co.gongzh.procbridge.model;

public final class Versions {

    public static final byte[] V1_0 = { 1, 0 };
    public static final byte[] V1_1 = { 1, 1 };
    public static final byte[] CURRENT = V1_1;

    public static byte[] getCurrent() {
        return CURRENT.clone();
    }


}
