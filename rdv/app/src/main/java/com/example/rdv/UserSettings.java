package com.example.rdv;

import android.app.Application;

public class UserSettings extends Application {
    public static final String PREFERENCES = "preferences";

    public static final String CUSTOM_THEME = "customTheme";
    public static final String LIGHT_THEME = "lightTheme";
    public static final String DARK_THEME = "darkTheme";

    public static final String LOCKED_CUSTOM = "lockedCustom";
    public static final boolean LOCKED_YES = true;
    public static final boolean LOCKED_NO = false;

    private String customTheme;
    private boolean customLocked;

    public String getCustomTheme() {
        return customTheme;
    }
    public boolean getCustomLocked() {return customLocked;}

    public void setCustomTheme(String customTheme) {
        this.customTheme = customTheme;
    }

    public void setCustomLocked(boolean locked) { this.customLocked = locked; }
}
