package com.example.maptest;

import android.graphics.drawable.Icon;

import java.io.Serializable;

public class IconWrapper implements Serializable {
    Icon icon;

    public IconWrapper(Icon icon) {
        this.icon = icon;
    }

    public IconWrapper() {

    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }
}
