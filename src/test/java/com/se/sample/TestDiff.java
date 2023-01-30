package com.se.sample;

import org.assertj.core.util.diff.DiffUtils;
import org.assertj.core.util.diff.Patch;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TestDiff {

   // @Test
    public void testDiffChange() {
        final List<String> changeTestFrom = Arrays.asList("aaa", "bbb", "ccc");
        final List<String> changeTestTo = Arrays.asList("aaa", "zzz", "ccc");
        System.out.println("changeTestFrom=" + changeTestFrom);
        System.out.println("changeTestTo=" + changeTestTo);
        final Patch<String> patch0 = DiffUtils.diff(changeTestFrom, changeTestTo);
        System.out.println("patch=" + Arrays.toString(patch0.getDeltas().toArray()));

        String original = "abcdefghijk";
        String badCopy =  "abmdefghink";

        final List<String> l1 =  Arrays.asList(original);
        final List<String> l2 = Arrays.asList(badCopy);


        final Patch<String> patch2 = DiffUtils.diff(l1, l2);
        System.out.println("patch2=" + Arrays.toString(patch2.getDeltas().toArray()));


        List<Character> originalList = original
                .chars() // Convert to an IntStream
                .mapToObj(i -> (char) i) // Convert int to char, which gets boxed to Character
                .collect(Collectors.toList()); // Collect in a List<Character>
        List<Character> badCopyList = badCopy.chars().mapToObj(i -> (char) i).collect(Collectors.toList());
        System.out.println("original=" + original);
        System.out.println("badCopy=" + badCopy);
        final Patch<Character> patch = DiffUtils.diff(originalList, badCopyList);
        System.out.println("patch=" + Arrays.toString(patch.getDeltas().toArray()));

        final char c = original.charAt(2);

        int index =2+1;
        original = new StringBuilder(original).insert(index, "(+m)").toString();
        int a =0;
    }
}
