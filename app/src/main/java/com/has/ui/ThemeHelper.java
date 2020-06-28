package com.has.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.has.R;

public class ThemeHelper {

    public static final int LIGHT_THEME = 0;
    public static final int DARK_THEME = 1;


    public static void setTheme(Context context) {
        int theme = getCurrentTheme(context);
        switch (theme) {
            case LIGHT_THEME:
                context.setTheme(R.style.CustomAppThemeLight);
                break;
            case DARK_THEME:
                context.setTheme(R.style.CustomAppThemeDark);
                break;
            default:
                context.setTheme(R.style.CustomAppThemeLight);
        }
    }

    public static int getCurrentTheme(Context context) {
        SharedPreferences sp = context.getSharedPreferences("theme", 0);
        return sp.getInt("theme", LIGHT_THEME);
    }

    public static void changeTheme(Activity activity, int themeId) {
        SharedPreferences sp = activity.getSharedPreferences("theme", 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("theme", themeId);
        editor.commit();
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
    }

}
