package batua.sikka.co.in.batua;

import org.junit.Test;
import in.co.block.kaoru.utils.StringUtils;
import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void nameValidator_CorrentName1_ReturnsTrue() {
        assertTrue(StringUtils.getInstance().validName("Amardeep Kaur"));
    }

    @Test
    public void nameValidator_CorrectName2_ReturnsTrue() {
        assertTrue(StringUtils.getInstance().validName("Amardeep"));
    }

    @Test
    public void nameValidator_IncorrectName1_ReturnsTrue() {
        assertFalse(StringUtils.getInstance().validName(""));
    }

    @Test
    public void nameValidator_IncorrectName2_ReturnsTrue() {
        assertFalse(StringUtils.getInstance().validName(null));
    }

    @Test
    public void uuidValidator_CorrectUUID_ReturnTrue(){
        String uuid = StringUtils.getInstance().getUUID();
        assertTrue(uuid != null && uuid.length() != 0);
    }
}