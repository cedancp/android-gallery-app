package com.cedancp.gallery;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.rule.ActivityTestRule;

import com.cedancp.gallery.ui.MainActivity;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasData;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;

public class MainActivityTest {

    private Instrumentation.ActivityResult activityResultGallery;
    private Instrumentation.ActivityResult activityResultPhoto;

    @Rule
    public final ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setupActivityResutlGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // Accepted mimetypes jpg and png
        String[] mimetypes = {"image/jpeg\", \"image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        File dir = rule.getActivity().getExternalCacheDir();
        File file = new File(dir.getPath(), "pickImageResult.jpeg");
        Uri uri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        activityResultGallery = new Instrumentation.ActivityResult(Activity.RESULT_OK, intent);

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
        intending(expectedIntent).respondWith(activityResultGallery);

        //Click on floating action button
        onView(withId(R.id.fab_gallery)).perform(click());
        intended(expectedIntent);
        Intents.release();
    }
}
