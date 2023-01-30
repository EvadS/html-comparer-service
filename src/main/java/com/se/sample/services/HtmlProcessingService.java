package com.se.sample.services;

import com.se.sample.model.payload.DifferenceFiles;
import com.se.sample.model.payload.StringDiff;
import com.se.sample.utils.ComparerUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;


@Service
@Slf4j
@RequiredArgsConstructor
public class HtmlProcessingService {
    private final static String DIFF_FILE_SUFFIX = "diff_";
    final String mainTag = "mainTag";


    public DifferenceFiles process(Path oldFilePath, Path newFilePath) throws IOException {
        File newHtmlFile = oldFilePath.toFile();
        File oldHtmlFile = newFilePath.toFile();


        File htmlOldDiffFile =  new File(newHtmlFile.getParent() + "\\" + buildDiffFileName(newHtmlFile.getName()));
        File htmlNewDiffFile = new File(oldHtmlFile.getParent() + "\\" +buildDiffFileName(oldHtmlFile.getName()));

        //clear file
        clearFile(htmlOldDiffFile);
        clearFile(htmlNewDiffFile);

        Document documentLeft = Jsoup.parse(newHtmlFile, StandardCharsets.UTF_8.name());
        Document documentRight = Jsoup.parse(oldHtmlFile, StandardCharsets.UTF_8.name());

        Elements leftElements = documentLeft.getElementsByClass(mainTag);
        Elements rightElements = documentRight.getElementsByClass(mainTag);

        final int sizeLeft = leftElements.size();
        final int sizeRight = leftElements.size();

        int rowNumber = 0;
        for (; rowNumber < sizeLeft && rowNumber < sizeRight; rowNumber++) {

            Element oldValue = leftElements.get(rowNumber);
            Element newValue = rightElements.get(rowNumber);

            final String textValueOld = oldValue.text();
            final String textValueNew = newValue.text();

            final StringDiff stringDiff = ComparerUtils.checkDiff(textValueOld, textValueNew);
            oldValue.text(org.jsoup.parser.Parser.unescapeEntities(stringDiff.getNewString(), true));
            newValue.text(org.jsoup.parser.Parser.unescapeEntities(stringDiff.getOldString(), true));

            appendDifferenceToFile(htmlOldDiffFile, oldValue);
            appendDifferenceToFile(htmlNewDiffFile, newValue);

        }

        return new DifferenceFiles(htmlOldDiffFile.getName(), htmlNewDiffFile.getName());

    }

    private String buildDiffFileName(String filename) {
           return DIFF_FILE_SUFFIX+  filename;
    }

    private void clearFile(File htmlOldDiffFile) throws FileNotFoundException {
        try (PrintWriter writer = new PrintWriter(htmlOldDiffFile)) {
            writer.print("");
        }
    }

    /**
     * write text to file
     *
     * @param file    full file path
     * @param textRow string for put to file
     * @throws IOException
     */
    public void appendDifferenceToFile(File file, Element textRow) throws IOException {
        try (FileWriter fw = new FileWriter(file.getAbsoluteFile(), true)
             //BufferedWriter bw = new BufferedWriter(fw);
             //PrintWriter out = new PrintWriter(bw))
        ) {
            //TODO:
            fw.write(textRow.toString());
        }
    }
}
