package com.meilicat.basedemo.base;

import android.content.Context;
import android.content.SharedPreferences;

public class MeiliCatSettings extends AppSettings {

    private final SharedPreferences mGlobalPreferences;

    public MeiliCatSettings(Context context) {
        mGlobalPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_WORLD_READABLE);
    }

    @Override
    public SharedPreferences getGlobalPreferences() {
        return mGlobalPreferences;
    }

    //是否是第一次启动
    public BooleanPreference IS_FIRST_RUN = new BooleanPreference("is_first_run", true);

    public StringPreference APP_COOKIE_STR = new StringPreference("app_cookie_str", "");
}
