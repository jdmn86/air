package pt.ipleiria.dei.iair.US5;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import pt.ipleiria.dei.iair.MasterTest;
import pt.ipleiria.dei.iair.R;
import pt.ipleiria.dei.iair.controller.IAirManager;
import pt.ipleiria.dei.iair.model.CityAssociation;
import pt.ipleiria.dei.iair.view.DashboardActivity;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.StringContains.containsString;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class US5_AT4 extends MasterTest{

    @Rule
    public ActivityTestRule<DashboardActivity> mActivityTestRule = new ActivityTestRule<>(DashboardActivity.class);

    @Test
    public void uS5_AT4() {
        /*ViewInteraction editText = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.custom),
                                childAtPosition(
                                        withId(R.id.customPanel),
                                        0)),
                        0),
                        isDisplayed()));
        editText.perform(click());

        ViewInteraction editText2 = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.custom),
                                childAtPosition(
                                        withId(R.id.customPanel),
                                        0)),
                        0),
                        isDisplayed()));
        editText2.perform(replaceText("z"), closeSoftKeyboard());

        ViewInteraction appCompatButton = onView(
                allOf(withId(android.R.id.button2), withText("My Current Location"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.buttonPanel),
                                        0),
                                2)));
        appCompatButton.perform(scrollTo(), click());*/
        try {
            onView(isRoot()).perform(waitId(R.id.menu_dashboard, TimeUnit.SECONDS.toMillis(10)));

        } catch (Exception e) {
            System.out.println(e.getMessage());
            //fail(e.toString());
        }
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        ViewInteraction appCompatTextView = onView(
                allOf(withId(R.id.title), withText("Location list"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.support.v7.view.menu.ListMenuItemView")),
                                        0),
                                0),
                        isDisplayed()));
        appCompatTextView.perform(click());
        try {
            onView(isRoot()).perform(waitId(R.id.menu_dashboard, TimeUnit.SECONDS.toMillis(10)));

        } catch (Exception e) {
            System.out.println(e.getMessage());
            //fail(e.toString());
        }

        List<String> cityNames;
        cityNames = new ArrayList<String>();
        for (CityAssociation cityAssociation: IAirManager.INSTANCE.getAllCityAssociations())
        {
            cityNames.add(cityAssociation.getREGION_NAME());
        }

        onView(withId(R.id.spinnerLocationList)).perform(click());
        for(int i= 0; i< cityNames.size()-1;i++){
            Log.d("debj",cityNames.get(i)+ " :TESTE AT4 US5");
            onData(hasToString(cityNames.get(i))).perform(click());//IAirManager.INSTANCE.getCurrentLocationName())).perform(click());
            onView(withId(R.id.spinnerLocationList)).check(matches(withSpinnerText(containsString(cityNames.get(i)))));
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
