package com.shrooml.services;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class MushroomHistoryServiceTest {

    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        MushroomHistoryService.reset(context);
    }

    @After
    public void tearDown() {
        MushroomHistoryService.reset(context);
    }

    @Test
    public void saveAndLoadUsedIndices_persistsAllValues() {
        Set<Integer> expected = new HashSet<>(Arrays.asList(1, 3, 8));

        MushroomHistoryService.saveUsedIndices(context, expected);
        Set<Integer> actual = MushroomHistoryService.loadUsedIndices(context);

        assertEquals(expected, actual);
    }

    @Test
    public void saveUsedIndices_mergesWithPreviousValues() {
        MushroomHistoryService.saveUsedIndices(context, new HashSet<>(Arrays.asList(2, 4)));
        MushroomHistoryService.saveUsedIndices(context, new HashSet<>(Arrays.asList(4, 7)));

        Set<Integer> actual = MushroomHistoryService.loadUsedIndices(context);

        assertEquals(3, actual.size());
        assertTrue(actual.contains(2));
        assertTrue(actual.contains(4));
        assertTrue(actual.contains(7));
    }

    @Test
    public void reset_clearsStoredIndices() {
        MushroomHistoryService.saveUsedIndices(context, new HashSet<>(Arrays.asList(5, 6)));

        MushroomHistoryService.reset(context);
        Set<Integer> actual = MushroomHistoryService.loadUsedIndices(context);

        assertTrue(actual.isEmpty());
    }
}
