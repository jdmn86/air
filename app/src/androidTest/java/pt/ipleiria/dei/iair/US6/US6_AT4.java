package pt.ipleiria.dei.iair.US6;


import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import pt.ipleiria.dei.iair.R;
import pt.ipleiria.dei.iair.view.DashboardActivity;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class US6_AT4 {

    @Rule
    public ActivityTestRule<DashboardActivity> mActivityTestRule = new ActivityTestRule<>(DashboardActivity.class);

    UiDevice mDevice;

    @Before
    public void setUp() {
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    }

    @Test
    public void uS6_AT4() {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        ViewInteraction appCompatTextView = onView(
                allOf(withId(R.id.title), withText("Map"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.support.v7.view.menu.ListMenuItemView")),
                                        0),
                                0),
                        isDisplayed()));
        appCompatTextView.perform(click());


        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.place_autocomplete_search_input),
                        childAtPosition(
                                allOf(withId(R.id.place_autocomplete_fragment),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatEditText.perform(click());
        UiObject searchTextViewApp = mDevice.findObject(new UiSelector().text("Search"));

        try {
            searchTextViewApp.setText("Lisbon");
        } catch (UiObjectNotFoundException e) {
            fail("App search textView not found");
        }

        UiObject listItemToClick = mDevice.findObject(new UiSelector().text("Lisbon").instance(1));

        try {
            listItemToClick.clickAndWaitForNewWindow();
        } catch (UiObjectNotFoundException e) {
            fail("App search list item not found");
        }
        UiObject gMap = mDevice.findObject(new UiSelector().description("Google Map"));
        UiObject marker = mDevice.findObject(gMap.getSelector().childSelector(new UiSelector().instance(0)));

        try {
            marker.dragTo(marker, 2);
        } catch (UiObjectNotFoundException e) {
            fail("Marker not found");
        }

        ViewInteraction appCompatButton = onView(
                allOf(withText("Set As Favorite Location"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        appCompatButton.perform(scrollTo(), click());
        try {
            assertEquals(marker.getContentDescription().trim(),"Lisbon This Is Your Favorite Location.");
        } catch (UiObjectNotFoundException e) {
            fail("Marker not Found!");
        }

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
