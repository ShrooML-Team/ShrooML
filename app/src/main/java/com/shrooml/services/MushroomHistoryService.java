package com.shrooml.services;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class MushroomHistoryService {

    private static final String PREF_NAME = "mushroom_prefs";
    private static final String KEY_USED = "used_indices";

    public static void saveUsedIndices(Context context, Set<Integer> used) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        Set<String> stringSet = new HashSet<>();
        for (Integer i : used) {
            stringSet.add(String.valueOf(i));
        }

        prefs.edit().putStringSet(KEY_USED, stringSet).apply();
    }

    public static Set<Integer> loadUsedIndices(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        Set<String> stringSet = prefs.getStringSet(KEY_USED, new HashSet<>());

        Set<Integer> result = new HashSet<>();
        for (String s : stringSet) {
            result.add(Integer.parseInt(s));
        }

        return result;
    }

    public static void reset(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().remove(KEY_USED).apply();
    }
}
