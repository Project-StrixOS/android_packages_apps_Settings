package com.android.settings.strix;

import android.content.Context;
import android.util.AttributeSet;
import android.provider.Settings;

import androidx.preference.SwitchPreferenceCompat;

public class GlobalSettingSwitchPreference extends SwitchPreferenceCompat {

    public GlobalSettingSwitchPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyle) {
        super(context, attrs, defStyleAttr, defStyle);
    }

    public GlobalSettingSwitchPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public GlobalSettingSwitchPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GlobalSettingSwitchPreference(Context context) {
        super(context);
    }

    @Override
    protected boolean persistBoolean(boolean value) {
        String key = getKey();
        putBoolean(key, value);
        return true;
    }

    @Override
    protected boolean getPersistedBoolean(boolean defaultValue) {
        String key = getKey();
        return getBoolean(key, defaultValue);
    }

    private void putBoolean(String key, boolean value) {
        Settings.Global.putInt(getContext().getContentResolver(), key, value ? 1 : 0);
    }

    private boolean getBoolean(String key, boolean defaultValue) {
        return Settings.Global.getInt(getContext().getContentResolver(),
                key, defaultValue ? 1 : 0) != 0;
    }
}
