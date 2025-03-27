package com.ibexmc.domain.domainblock.flags;

import com.ibexmc.internal.functions.StringFunctions;

public class FlagParts {

    public enum TopLevelFlag {

    }
    private String type;
    private String applyTo;
    private String status = "";
    private boolean valid = false;
    public FlagParts(String flag) {
        String[] parts = flag.split("\\.");
        if (parts.length >= 4) {
            this.type = parts[1].toLowerCase();
            this.applyTo = parts[2].toLowerCase();
            if (parts[3].equalsIgnoreCase("allow") || parts[3].equalsIgnoreCase("deny")) {
                this.status = parts[3].toLowerCase();
            } else {
                this.type = "";
                this.applyTo = "";
                this.status = "";
            }
        } else {
            this.type = "";
            this.applyTo = "";
            this.status = "";
        }
        if (!StringFunctions.isNullOrEmpty(type) && !StringFunctions.isNullOrEmpty(applyTo) && !StringFunctions.isNullOrEmpty(status)) {
            this.valid = true;
        }
    }

    public String getBreakType() {
        return type;
    }
    public String getApplyTo() {
        return applyTo;
    }
    public String getStatus() {
        return status;
    }
    public boolean isValid() {
        return valid;
    }

}
