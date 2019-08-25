package com.cedancp.gallery;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.rule.ActivityTestRule;

import com.cedancp.gallery.ui.MainActivity;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasData;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;

public class MainActivityTest {

    private Instrumentation.ActivityResult activityResult;

    @Rule
    public final ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class);


    @Before
    public void setupImageUri() {

        Resources resources = ApplicationProvider.getApplicationContext().getResources();
        Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + resources
                .getResourcePackageName(R.mipmap.ic_launcher) + '/' + resources.getResourceTypeName(
                                        R.mipmap.ic_launcher) + '/' + resources.getResourceEntryName(
                                        R.mipmap.ic_launcher));
        
        Intent resultData = new Intent();
        resultData.setData(imageUri);
        activityResult = new Instrumentation.ActivityResult(
                Activity.RESULT_OK, resultData);

    }

    /**
     * Testing if floating action triggers fab_gallery opens gallery intent
     */
    @Test
    public void buttonTriggersGalleryIntent() {
        //Setting up the intent
        Intents.init();
        Matcher<Intent> expectedIntent = allOf(hasAction(Intent.ACTION_PICK),
                hasData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
        intending(expectedIntent).respondWith(activityResult);

        //Click on floating action button
        onView(withId(R.id.fab_gallery)).perform(click());
        intended(expectedIntent);
        Intents.release();
    }
}
