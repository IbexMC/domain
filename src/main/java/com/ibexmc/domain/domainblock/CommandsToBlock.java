package com.ibexmc.domain.domainblock;

import com.ibexmc.domain.flag.Flag;

import java.util.List;

public class CommandsToBlock {
    private Flag.Type flagType;
    private List<String> commands;

    public CommandsToBlock(Flag.Type flagType, List<String> commands) {
        this.flagType = flagType;
        this.commands = commands;
    }

    public Flag.Type getFlagType() {
        return flagType;
    }

    public List<String> getCommands() {
        return commands;
    }

}
