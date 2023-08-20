package co.gongzh.procbridge.model;

import org.jetbrains.annotations.Nullable;

public enum StatusCode {

    REQUEST(0), GOOD_RESPONSE(1), BAD_RESPONSE(2);

    public int rawValue;

    StatusCode(int rawValue) {
        this.rawValue = rawValue;
    }

    @Nullable
    public static StatusCode fromRawValue(int rawValue) {
        for (StatusCode sc : StatusCode.values()) {
            if (sc.rawValue == rawValue) {
                return sc;
            }
        }
        return null;
    }

}
