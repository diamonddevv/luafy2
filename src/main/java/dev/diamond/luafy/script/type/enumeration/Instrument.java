package dev.diamond.luafy.script.type.enumeration;

import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;

public enum Instrument {

    BASS(NoteBlockInstrument.BASS),
    SNARE(NoteBlockInstrument.SNARE),
    HIHAT(NoteBlockInstrument.HAT),
    BASS_DRUM(NoteBlockInstrument.BASEDRUM),
    GLOCKENSPIEL(NoteBlockInstrument.BELL),
    FLUTE(NoteBlockInstrument.FLUTE),
    CHIME(NoteBlockInstrument.CHIME),
    GUITAR(NoteBlockInstrument.GUITAR),
    XYLOPHONE(NoteBlockInstrument.XYLOPHONE),
    VIBRAPHONE(NoteBlockInstrument.IRON_XYLOPHONE),
    COWBELL(NoteBlockInstrument.COW_BELL),
    DIDGERIDOO(NoteBlockInstrument.DIDGERIDOO),
    BIT(NoteBlockInstrument.BIT),
    BANJO(NoteBlockInstrument.BANJO),
    PLING(NoteBlockInstrument.PLING),
    HARP(NoteBlockInstrument.HARP);

    private final NoteBlockInstrument instrument;
    Instrument(NoteBlockInstrument instrument) {
        this.instrument = instrument;
    }

    public NoteBlockInstrument getInstrument() {
        return instrument;
    }
}
