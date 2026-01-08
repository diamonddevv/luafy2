package dev.diamond.luafy.script.enumeration;

public enum Note {
    G_FLAT_LOW(0),
    G_LOW(1),
    A_FLAT_LOW(2),
    A_LOW(3),
    B_FLAT_LOW(4),
    B_LOW(5),
    C_LOW(6),
    C_SHARP_LOW(7),
    D_LOW(8),
    D_SHARP_LOW(9),
    E_LOW(10),
    F_LOW(11),
    F_SHARP_LOW(12),

    G_FLAT_HIGH(12),
    G_HIGH(13),
    A_FLAT_HIGH(14),
    A_HIGH(15),
    B_FLAT_HIGH(16),
    B_HIGH(17),
    C_HIGH(18),
    C_SHARP_HIGH(19),
    D_HIGH(20),
    D_SHARP_HIGH(21),
    E_HIGH(22),
    F_HIGH(23),
    F_SHARP_HIGH(24);

    private final int idx;
    Note(int idx) {
        this.idx = idx;
    }

    public int getIndex() {
        return idx;
    }

    public float getPitch() {
        return (float) Math.pow(2, (idx - 12f) / 12);
    }
}
