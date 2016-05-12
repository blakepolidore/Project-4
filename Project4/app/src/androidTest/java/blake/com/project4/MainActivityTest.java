package blake.com.project4;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import blake.com.project4.activities.MainActivity;

/**
 * Created by Raiders on 5/12/16.
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule =
            new ActivityTestRule<MainActivity>(MainActivity.class);

    @Test
    public void testLikeButton() {
//        onView(withId(R.id.card_title)).check(matches(onView(withId(R.id.frame)).perform(click()).check(withText(onView(withId(R.id.venue_title)).toString()))));
//
//        onView(withId(R.id.frame)).check(ViewAssertions.)
    }
}
