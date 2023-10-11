package com.example.pruebaenviogmap;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class Preferences {

    private static final String PREF_NAME = "MyPreferences";
    private static final String KEY_DIRECCIONES = "addresses";

    public static boolean saveAddresses(Context context, String[] addresses) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        List<String> directionList = Arrays.asList(addresses);
        String directionJson = new Gson().toJson(directionList);

        editor.putString(KEY_DIRECCIONES, directionJson);
        editor.apply();
        return true;
    }

    public static String[] getDirection(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String directionJson = sharedPreferences.getString(KEY_DIRECCIONES, null);

        if (directionJson != null) {
            List<String> addressesList = new Gson().fromJson(directionJson, new TypeToken<List<String>>() {}.getType());

            return addressesList.toArray(new String[0]);
        } else {
            return new String[0];
        }
    }
}
