package ca.craigthomas.yacoco3e.components;

public class DeviceSelectorSwitch
{
    protected int switchPosition;
    protected int CB2;
    protected int CA2;

    public DeviceSelectorSwitch() {
        switchPosition = 0;
        CB2 = 0;
        CA2 = 0;
    }

    public void setCB2(boolean isSet) {
        CB2 = (isSet) ? 1 : 0;
        switchPosition = (CB2 << 1) + CA2;
        System.out.println("New switch position = " + switchPosition);
    }

    public void setCA2(boolean isSet) {
        CA2 = (isSet) ? 1 : 0;
        switchPosition = (CB2 << 1) + CA2;
        System.out.println("New switch position = " + switchPosition);
    }

    public int getSwitchPosition() {
        return switchPosition;
    }
}
