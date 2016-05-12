package blake.com.project4;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import blake.com.project4.activities.LikedActivity;

/**
 * Created by Raiders on 5/12/16.
 */
@RunWith(AndroidJUnit4.class)
public class LikedActivityTest {
    @Rule
    public ActivityTestRule<LikedActivity> activityTestRule =
            new ActivityTestRule<LikedActivity>(LikedActivity.class);

    @Test
    public void testDeleteOnRecyclerView() {
        //onView(withId(R.id.recyclerView)).perform(ViewActions.longClick()).check(ViewAssertions.matches())
    }
}
