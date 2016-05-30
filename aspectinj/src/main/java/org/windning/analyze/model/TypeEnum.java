package org.windning.analyze.model;

/**
 * Used to represents a Type in Java
 * Created by yw_sun on 2016/5/30.
 */
public enum TypeEnum {
    VOID(0, "VOID"),
    BOOLEAN(1, "BOOLEAN"),
    BYTE(2, "BYTE"),
    SHORT(3, "SHORT"),
    INT(4, "INT"),
    CHAR(5, "CHAR"),
    FLOAT(6, "FLOAT"),
    DOUBLE(7, "DOUBLE"),
    BOT(8, "BOT"),
    CLASS(9, "CLASS"),
    LONG(10, "LONG");


    private String mName = "";
    TypeEnum(int val, String desc) {
        mName = desc;
    }

    public String getName() {
        return mName;
    }

}
