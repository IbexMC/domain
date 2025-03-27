package com.ibexmc.domain.domainblock;

public class FieldCommand {
    private int placedID;
    private boolean onEnter;
    private boolean onLeave;
    private String command;

    public FieldCommand(int placedID, boolean onEnter, boolean onLeave, String command) {
        this.placedID = placedID;
        this.onEnter = onEnter;
        this.onLeave = onLeave;
        this.command = command;
    }
    public int getPlacedID() {
        return placedID;
    }
    public boolean getOnEnter() {
        return onEnter;
    }
    public boolean getOnLeave() {
        return onLeave;
    }
    public String getCommand() {
        return command;
    }
}
