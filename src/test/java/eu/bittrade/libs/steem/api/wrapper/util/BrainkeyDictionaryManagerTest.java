package eu.bittrade.libs.steem.api.wrapper.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.Test;

/**
 * Test the brain key dictionary import.
 * 
 * @author <a href="http://steemit.com/@dez1337">dez1337</a>
 *
 */
public class BrainkeyDictionaryManagerTest {
    private static final int NUMBER_OF_WORDS = 49744;

    @Test
    public void testBrainkeyDictionaryManager() {
        assertThat(BrainkeyDictionaryManager.getInstance().getBrainKeyDictionary().length, equalTo(NUMBER_OF_WORDS));
    }
}
