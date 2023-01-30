package com.se.sample.utils;

import com.se.sample.config.ProjectConstants;
import com.se.sample.model.payload.StringDiff;
import org.junit.jupiter.api.Test;
import org.springframework.util.StringUtils;

import static org.junit.jupiter.api.Assertions.*;

class ComparerUtilsTest {

    @Test
    public void emptyInputStringShouldBeEmpty() {
        String left = "";
        String right = "";

        final StringDiff stringDiff = ComparerUtils.checkDiff(left, right);

        assertNotNull(stringDiff);
        assertFalse(StringUtils.hasLength(stringDiff.getOldString()));
        assertFalse(StringUtils.hasLength(stringDiff.getNewString()));

        assertNotNull(stringDiff.getOldString());
        assertNotNull(stringDiff.getNewString());
    }


    @Test
    public void testEqualStringShouldBeEmpty() {
        String left = "test string";

        final StringDiff stringDiff = ComparerUtils.checkDiff(left, left);

        assertNotNull(stringDiff);
        assertTrue(StringUtils.hasLength(stringDiff.getOldString()));
        assertTrue(StringUtils.hasLength(stringDiff.getNewString()));

        assertNotNull(stringDiff.getOldString());
        assertNotNull(stringDiff.getNewString());

        assertEquals(left, stringDiff.getNewString());
        assertEquals(left, stringDiff.getOldString());
    }

    @Test
    public void oneWorkShouldWorkCorrect() {
        String left = "ab";
        String right = "ac";

        final StringDiff stringDiff = ComparerUtils.checkDiff(left, right);

        String leftExpected = "a" + String.format(ProjectConstants.OLD_DIFFERENCE_FORMAT, "c");
        String rightExpected = "a" + String.format(ProjectConstants.NEW_DIFFERENCE_FORMAT, "b");

        assertEquals(leftExpected, stringDiff.getOldString());
        assertEquals(rightExpected, stringDiff.getNewString());
    }

    @Test
    public void oneSpaceShouldWorkCorrect() {
        String left =  "a 1 t";
        String right = "a 2 t";

        final StringDiff stringDiff = ComparerUtils.checkDiff(left, right);

        String leftExpected = "a " + String.format(ProjectConstants.OLD_DIFFERENCE_FORMAT, "2") + " t"  ;
        String rightExpected = "a " + String.format(ProjectConstants.NEW_DIFFERENCE_FORMAT, "1") + " t" ;

        assertEquals(leftExpected, stringDiff.getOldString());
        assertEquals(rightExpected, stringDiff.getNewString());
    }

    @Test
    public void oneWorkBeforeEqualsShouldWorkCorrect() {
        String left = "abc 111 test";
        String right = "abc 222 test";

        final StringDiff stringDiff = ComparerUtils.checkDiff(left, right);

        String leftExpected = "abc " + String.format(ProjectConstants.OLD_DIFFERENCE_FORMAT, "222") + " test"  ;
        String rightExpected = "abc " + String.format(ProjectConstants.NEW_DIFFERENCE_FORMAT, "111") + " test" ;

        assertEquals(leftExpected, stringDiff.getOldString());
        assertEquals(rightExpected, stringDiff.getNewString());
    }


    @Test
    public void checkLeftSideBiggestShouldWorkCorrect() {
        String left = "21";
        String right = "2";

        final StringDiff stringDiff = ComparerUtils.checkDiff(left, right);

        String leftExpected = "21" + String.format(ProjectConstants.OLD_DIFFERENCE_FORMAT, "")  ;
        String rightExpected = "2" + String.format(ProjectConstants.NEW_DIFFERENCE_FORMAT, "1")  ;

        assertEquals(leftExpected, stringDiff.getOldString());
        assertEquals(rightExpected, stringDiff.getNewString());
    }

    @Test
    public void checkRightSideBiggestShouldWorkCorrect() {
        String left = "abc 111 test";
        String right = "abc 222 test";

        final StringDiff stringDiff = ComparerUtils.checkDiff(left, right);

        String leftExpected = "abc" + String.format(ProjectConstants.OLD_DIFFERENCE_FORMAT, "222") + " test"  ;
        String rightExpected = "abc" + String.format(ProjectConstants.NEW_DIFFERENCE_FORMAT, "111") + " test" ;

        assertEquals(leftExpected, stringDiff.getOldString());
        assertEquals(rightExpected, stringDiff.getNewString());
    }

    @Test
    public void compareLettersAndNumbersShouldWorkCorrect() {
        String left = "ab";
        String right = "ac";

        final StringDiff stringDiff = ComparerUtils.checkDiff(left, right);

        String leftExpected = "a" + String.format(ProjectConstants.OLD_DIFFERENCE_FORMAT, "c");
        String rightExpected = "a" + String.format(ProjectConstants.NEW_DIFFERENCE_FORMAT, "b");

        assertEquals(leftExpected, stringDiff.getOldString());
        assertEquals(rightExpected, stringDiff.getNewString());
    }

}