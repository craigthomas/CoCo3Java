package ca.craigthomas.yacoco3e.datatypes;

public class DMKHeader
{
    public static final int HEADER_LENGTH = 16;
    private UnsignedByte [] header;

    public DMKHeader() {
        header = new UnsignedByte[HEADER_LENGTH];
        for (int i = 0; i < HEADER_LENGTH; i++) {
            header[i] = new UnsignedByte();
        }
    }

    public void setWriteProtect(UnsignedByte value) {
        header[0].set(value);
    }

    public UnsignedByte getWriteProtect() {
        return header[0];
    }

    public void setNumTracks(UnsignedByte value) {
        header[1].set(value);
    }

    public UnsignedByte getNumTracks() {
        return header[1];
    }

    public void setTrackLength(int length) {
        UnsignedWord trackLength = new UnsignedWord(length);
        header[2] = trackLength.getLow();
        header[3] = trackLength.getHigh();
    }

    public UnsignedWord getTrackLength() {
        return new UnsignedWord(header[3], header[4]);
    }


}
