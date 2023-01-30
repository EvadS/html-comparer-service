package com.se.sample.utils;

import com.se.sample.config.ProjectConstants;
import com.se.sample.model.payload.StringDiff;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

@Slf4j
public class ComparerUtils {

    private static final String EMPTY_STRING = "";

    public static StringDiff checkDiff(String oldStr, String newString) {

        if (oldStr.equals(newString)) {
            log.debug("The string are equal");
            return new StringDiff(oldStr, newString);
        }

        if (!StringUtils.hasLength(oldStr) && !StringUtils.hasLength(newString)) {
            log.debug("incorrect request to compare. ");
            return new StringDiff("", "");
        } else if (StringUtils.hasLength(oldStr) && !StringUtils.hasLength(newString)) {
            // left present but right is empty
            StringDiff stringDiff = new StringDiff();
            stringDiff.setOldString(String.format(ProjectConstants.OLD_DIFFERENCE_FORMAT, oldStr));
            stringDiff.setNewString(String.format(ProjectConstants.NEW_DIFFERENCE_FORMAT, oldStr));
            return stringDiff;

        } else if (!StringUtils.hasLength(oldStr) && StringUtils.hasLength(newString)) {
            StringDiff stringDiff = new StringDiff();
            stringDiff.setOldString(String.format(ProjectConstants.OLD_DIFFERENCE_FORMAT, newString));
            stringDiff.setNewString(String.format(ProjectConstants.NEW_DIFFERENCE_FORMAT, newString));
            return stringDiff;
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

        boolean isRightDiff = false;
        boolean isLeftDiff = false;
        int currPosition = 0;

        for (; currPosition < leftArray.length && currPosition < rightArray.length; currPosition++) {

            leftItem = leftArray[currPosition];
            rightItem = rightArray[currPosition];

            int compareOneTwo = Character.compare(leftItem, rightItem);
            if (compareOneTwo == 0) {
                log.debug("Curr position: {}, Left part:'{}', right part:'{}', symbols is equal ", currPosition, leftItem, rightItem);

                // have difference between old and new
                // but now the both symbol have the same value
                if (isDifferent) {
                    log.debug("The difference was found before. Old difference buffer:'{}, new difference buffer:'{}'", leftDifference, rightDifference);

                    sbLeft.append(String.format(ProjectConstants.OLD_DIFFERENCE_FORMAT, leftDifference.toString().trim()));
                    sbRight.append(String.format(ProjectConstants.NEW_DIFFERENCE_FORMAT, rightDifference.toString()).trim());

                    leftDifference.setLength(0);
                    rightDifference.setLength(0);
                    log.debug("Buffer with difference cleared");
                }

                sbLeft.append(leftItem);
                sbRight.append(rightItem);
                isDifferent = false;
            } else {

                log.debug("Left part:'{}', right part:'{}', symbols is different ", leftItem, rightItem);
                isDifferent = true;

                // save difference to buffer
                leftDifference.append(rightItem);
                log.debug("Old value difference:{}", leftDifference);
                rightDifference.append(leftItem);
                log.debug("New value difference:{}", rightDifference);
            }
        }

        if (leftStr.length() > currPosition) {
            //check diff
            String substring = leftStr.substring(currPosition);
            if (!isDifferent) {
                rightDifference.append(String.format(ProjectConstants.OLD_DIFFERENCE_FORMAT, substring));
                isLeftDiff = true;
            } else {
                // append nested symbol to difference
                rightDifference.append(substring);
            }

        }


        if (rightStr.length() > currPosition) {
            //check diff
            String substring = rightStr.substring(currPosition);
            // append nested symbol to difference

            if (!isDifferent) {
                leftDifference.append(String.format(ProjectConstants.NEW_DIFFERENCE_FORMAT, substring));
                isRightDiff = true;
            } else {
                // append nested symbol to difference
                leftDifference.append(substring);
            }
        }

        if (isDifferent) {
            log.debug("Comparable text has difference");
            sbLeft.append(String.format(ProjectConstants.OLD_DIFFERENCE_FORMAT, leftDifference).trim());
            sbRight.append(String.format(ProjectConstants.NEW_DIFFERENCE_FORMAT, rightDifference).trim());
        }

        if (isRightDiff) {
            sbLeft.append(leftDifference);
        }

        if (isLeftDiff) {
            sbRight.append(rightDifference);
        }


        stringDiff.setOldString(sbLeft.toString());
        stringDiff.setNewString(sbRight.toString());

        log.debug("The old text compared with new, result:{}", stringDiff.toString());
        return stringDiff;
    }
}