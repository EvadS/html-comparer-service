package com.se.sample.utils;

import com.se.sample.config.ProjectConstants;
import com.se.sample.model.payload.StringDiff;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

@Slf4j
public class ComparerUtils {

    private static final String EMPTY_STRING = "";

    public static StringDiff checkDiff(String oldStr, String newString) {

        log.debug("Check difference between: '{}' and '{}' ", oldStr, newString);

        if (!StringUtils.hasLength(oldStr) && !StringUtils.hasLength(oldStr)) {
            return new StringDiff("", "");
        }

        if (oldStr.equals(newString)) {
            log.debug("The string are equal");
            return new StringDiff(oldStr, newString);
        }


        log.debug("Started old and new symbols comparing, old:{}, new:{}", oldStr, newString);
        if (oldStr.length() >= newString.length()) {
            return checkDiffByBiggestArray(oldStr, newString);
        } else {
            return checkDiffByBiggestArray(newString, oldStr);
        }
    }

    private static StringDiff checkDiffByBiggestArray(String leftStr, String rightStr) {
        StringDiff stringDiff = new StringDiff();
        final char[] leftArray = leftStr.toCharArray();
        final char[] rightArray = rightStr.toCharArray();

        StringBuilder sbLeft = new StringBuilder();
        StringBuilder sbRight = new StringBuilder();
        boolean isDifferent = false;

        // initial value
        char leftItem = '\0';
        char rightItem = '\0';

        StringBuilder leftDifference = new StringBuilder();
        StringBuilder rightDifference = new StringBuilder();

        int currPosition = 0;

        for ( ; currPosition < leftArray.length && currPosition < rightArray.length; currPosition++) {

            leftItem = leftArray[currPosition];
            rightItem = rightArray[currPosition];

            int compareOneTwo = Character.compare(leftItem, rightItem);
            if (compareOneTwo == 0) {

                // have difference between old and new
                // but now the both symbol have the same value
                if (isDifferent) {
                    log.debug("The difference was found before. Write diff to result");
                    log.debug("Append difference");
                    sbLeft.append(String.format(ProjectConstants.OLD_DIFFERENCE_FORMAT, leftDifference.toString().trim()));
                    sbRight.append(String.format(ProjectConstants.NEW_DIFFERENCE_FORMAT, rightDifference.toString()).trim());

                    leftDifference.setLength(0);
                    rightDifference.setLength(0);
                }

                sbLeft.append(leftItem);
                sbRight.append(rightItem);
                isDifferent = false;
            } else {
                log.debug("Found difference between new and old symbols. left:{}, right:{} ", leftItem, rightItem);
                isDifferent = true;

                // save difference to buffer
                leftDifference.append(rightItem);
                rightDifference.append(leftItem);
            }
        }

        if (isDifferent) {
            log.debug("Comparable text has difference");
            sbLeft.append(String.format(ProjectConstants.OLD_DIFFERENCE_FORMAT, rightItem).trim());
            sbRight.append(String.format(ProjectConstants.NEW_DIFFERENCE_FORMAT, leftItem).trim());
        }

        // check not presented on right
        if (currPosition < leftArray.length) {
            String notExistsORight = leftStr.substring(currPosition, leftStr.length());
            sbRight.append(String.format(ProjectConstants.NEW_DIFFERENCE_FORMAT, notExistsORight));

            sbLeft.append(notExistsORight);
            sbLeft.append(String.format(ProjectConstants.OLD_DIFFERENCE_FORMAT, ""));
        }

        stringDiff.setOldString(sbLeft.toString());
        stringDiff.setNewString(sbRight.toString());

        log.debug("The old text compared with new, result:{}", stringDiff.toString());
        return stringDiff;
    }
}