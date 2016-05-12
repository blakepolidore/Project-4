package blake.com.project4;

import org.junit.Test;

import blake.com.project4.models.cardsModel.Cards;

import static org.junit.Assert.assertEquals;

/**
 * Created by Raiders on 5/12/16.
 */
public class UnitTests {
    @Test
    public void cardsModelTitle() {
        Cards cards = new Cards();
        cards.setTitle("Title");
        String expected = "Title";
        String actual = cards.getTitle();
        assertEquals(expected, actual);
    }

    @Test
    public void cardsModelCategory() {
        Cards cards = new Cards();
        cards.setCategory("Pizza");
        String expected = "Pizza";
        String actual = cards.getCategory();
        assertEquals(expected, actual);
    }

    @Test
    public void cardsModelLocation() {
        Cards cards = new Cards();
        cards.setLocation("New York");
        String expected = "New York";
        String actual = cards.getLocation();
        assertEquals(expected, actual);
    }

    @Test
    public void cardsModelPhone() {
        Cards cards = new Cards();
        cards.setPhone("1234567");
        String expected = "1234567";
        String actual = cards.getPhone();
        assertEquals(expected, actual);
    }
}
