package com.example.gui;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestGUI {

    @Rule
    public ActivityScenarioRule<MainMenu> activityRule
            = new ActivityScenarioRule<>(MainMenu.class);

    @Test
    public void setAllColumnsOnceByUsingButtons() {
        // Type text and then press the button.
        onView(withId(R.id.twoplayers)).perform(click());
        onView(withId(R.id.onDevice)).perform(click());

        onView(withId(R.id.button_put0)).perform(click());
        onView(withId(R.id.matchfield_00)).check(matches(withText(("0"))));
        onView(withId(R.id.matchfield_00)).check(matches(withTextColor(Color.RED)));

        onView(withId(R.id.button_put1)).perform(click());
        onView(withId(R.id.matchfield_10)).check(matches(withText(("0"))));
        onView(withId(R.id.matchfield_10)).check(matches(withTextColor(Color.BLUE)));

        onView(withId(R.id.button_put2)).perform(click());
        onView(withId(R.id.matchfield_20)).check(matches(withText(("0"))));
        onView(withId(R.id.matchfield_20)).check(matches(withTextColor(Color.RED)));

        onView(withId(R.id.button_put3)).perform(click());
        onView(withId(R.id.matchfield_30)).check(matches(withText(("0"))));
        onView(withId(R.id.matchfield_30)).check(matches(withTextColor(Color.BLUE)));

        onView(withId(R.id.button_put4)).perform(click());
        onView(withId(R.id.matchfield_40)).check(matches(withText(("0"))));
        onView(withId(R.id.matchfield_40)).check(matches(withTextColor(Color.RED)));

        onView(withId(R.id.button_put5)).perform(click());
        onView(withId(R.id.matchfield_50)).check(matches(withText(("0"))));
        onView(withId(R.id.matchfield_50)).check(matches(withTextColor(Color.BLUE)));

        onView(withId(R.id.button_put6)).perform(click());
        onView(withId(R.id.matchfield_60)).check(matches(withText(("0"))));
        onView(withId(R.id.matchfield_60)).check(matches(withTextColor(Color.RED)));
    }

    public static Matcher<View> withTextColor(final int expectedId) {
        return new BoundedMatcher<View, TextView>(TextView.class) {

            @Override
            protected boolean matchesSafely(TextView textView) {
                return expectedId == textView.getCurrentTextColor();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with text color: ");
                description.appendValue(expectedId);
            }
        };
    }
}
