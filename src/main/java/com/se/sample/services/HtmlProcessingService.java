package com.se.sample.services;

import com.se.sample.config.ProjectConstants;
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
    public static final String DIV_MAIN_TAG_H_2_B = "div.mainTag > h2 > b";
    public static final String DIV_MAIN_TAG_H_3_B = "div.mainTag > h3 > b";
    public static final String  MAIN_TAG_SELECTOR = "div.mainTag";
    public static final String DIV_MAIN_TAG_P = "div.mainTag > p";


    public DifferenceFiles process(Path oldFilePath, Path newFilePath) throws IOException {
        File newHtmlFile = oldFilePath.toFile();
        File oldHtmlFile = newFilePath.toFile();


        File htmlOldDiffFile = new File(newHtmlFile.getParent() + "\\" + buildDiffFileName(newHtmlFile.getName()));
        File htmlNewDiffFile = new File(oldHtmlFile.getParent() + "\\" + buildDiffFileName(oldHtmlFile.getName()));

        //clear file
        clearFile(htmlOldDiffFile);
        clearFile(htmlNewDiffFile);

        Document documentLeft = Jsoup.parse(newHtmlFile, StandardCharsets.UTF_8.name());
        Document documentRight = Jsoup.parse(oldHtmlFile, StandardCharsets.UTF_8.name());

        //  Elements leftElements = documentLeft.getElementsByClass(mainTag);

        Elements leftElements = documentLeft.select(MAIN_TAG_SELECTOR);
        Elements rightElements = documentRight.select(MAIN_TAG_SELECTOR);

        final int sizeLeft = leftElements.size();
        log.info("size left: {}", sizeLeft);

        final int sizeRight = rightElements.size();
        log.info("size right: {}", sizeRight);

        int rowNumber = 0;
        for (; rowNumber < sizeLeft && rowNumber < sizeRight; rowNumber++) {

            try {
                Element leftElement = leftElements.get(rowNumber);
                Element rightElement = rightElements.get(rowNumber);

                final Element oldElement = leftElement.select(DIV_MAIN_TAG_H_2_B).first();
                final Element newElement = rightElement.select(DIV_MAIN_TAG_H_2_B).first();
                comparisonH2Elements(oldElement,newElement);

                //General part
                // Chapter1
                final Element oldElementH3 = leftElement.select(DIV_MAIN_TAG_H_3_B).first();
                final Element newElementH3 = rightElement.select(DIV_MAIN_TAG_H_3_B).first();
                comparisonH3Elements(oldElementH3,newElementH3);


                //Articles level
                final Element articlesOld = leftElement.select(DIV_MAIN_TAG_P).first();
                final Element articlesNew = rightElement.select(DIV_MAIN_TAG_P).first();
                if (articlesOld != null && articlesNew!= null)  {
                    log.debug("Compare articles level ");
                    try {
                        // TODO: could be different row number
                        if (articlesOld.children().size() > 0 && articlesNew.children().size() > 0) {
                            final Element articlesOldText = articlesOld.child(0);
                            final Element articlesNewText = articlesNew.child(0);

                            StringDiff stringDiff = ComparerUtils.checkDiff(articlesOldText.text(), articlesNewText.text());
                            articlesOldText.text(stringDiff.getNewString());
                            articlesNewText.text(stringDiff.getOldString());
                        }
                        else if (articlesOld.children().size() > 0 && articlesNew.children()== null){
                            StringDiff stringDiff = ComparerUtils.checkDiff(articlesOld.children().first().text(), null);
                            articlesOld.text(stringDiff.getNewString());
                            articlesNew.text(stringDiff.getOldString());
                        }
                        else if (articlesNew.children().size() > 0 && articlesOld.children() == null){
                            StringDiff stringDiff = ComparerUtils.checkDiff(null, articlesNew.children().first().text());
                            articlesOld.text(stringDiff.getNewString());
                            articlesNew.text(stringDiff.getOldString());
                        }
                        else if (articlesNew.children()== null && articlesOld.children() == null) {
                            StringDiff stringDiff = ComparerUtils.checkDiff(articlesOld.text(), articlesNew.text());
                            articlesOld.text(stringDiff.getNewString());
                            articlesNew.text(stringDiff.getOldString());
                        }

                    }catch (Exception ex){
                        log.error("Articles compare error. articlesOld:{}, articlesNew:{}",articlesOld,  articlesNew);
                    }
                    }


                appendDifferenceToFile(htmlOldDiffFile, leftElement.toString());
                appendDifferenceToFile(htmlNewDiffFile, rightElement.toString());
                //   appendDifferenceToFile(htmlNewDiffFile, newValue);
            } catch (Exception ex) {
                log.error("An error while file comparison. Row number: {}", rowNumber);
                log.error(ex.getMessage());
            }
        }
        log.info("Comparison completed. The row numbers: {}",rowNumber );

        if(rowNumber < leftElements.size() ){
            //
            int i = rowNumber;
            for(;i <leftElements.size() ;i++){
                log.debug("Put nested old element [{}} to file ", i);
                appendDifferenceToFile(htmlNewDiffFile,
                        String.format(ProjectConstants.OLD_DIFFERENCE_FORMAT, leftElements.get(i).toString()));
            }

            log.debug("Right elements number: {}", i);
        }

        if(rowNumber < rightElements.size()){
            int i = rowNumber;
            for(;i <rightElements.size() ;i++){
                log.debug("Put nested new html element [{}} to file ", i);
                appendDifferenceToFile(htmlOldDiffFile,
                        String.format(ProjectConstants.NEW_DIFFERENCE_FORMAT, rightElements.get(i).toString()));
            }

            log.debug("Left elements number: {}", i);
        }
        return new DifferenceFiles(htmlOldDiffFile.getName(), htmlNewDiffFile.getName());

    }

    private void comparisonH3Elements(Element oldElement, Element newElement) {
        log.debug(" Compare h3 level");
        if (oldElement != null && newElement != null) {

            String leftText = oldElement.html();
            log.debug("Old text: {}",leftText );

            String rightText = newElement.html();
            log.debug("New text: {}",rightText );

            StringDiff stringDiff = ComparerUtils.checkDiff(leftText, rightText);

            oldElement.html(stringDiff.getNewString());
            newElement.html(stringDiff.getOldString());
        }
    }

    private void comparisonH2Elements(Element oldElement, Element newElement) {
        log.debug("Compare h2 level");
        if (oldElement != null && newElement != null) {
            String leftText = oldElement.html();
            log.debug("Old text: {}",leftText );

            String rightText = newElement.html();
            log.debug("New text: {}",rightText );

            StringDiff stringDiff = ComparerUtils.checkDiff(leftText, rightText);

            oldElement.html(stringDiff.getNewString());
            newElement.html(stringDiff.getOldString());
        }
    }

    private String buildDiffFileName(String filename) {
        return DIFF_FILE_SUFFIX + filename;
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
    public void appendDifferenceToFile(File file, String textRow) throws IOException {
        try (FileWriter fw = new FileWriter(file.getAbsoluteFile(), true)
             //BufferedWriter bw = new BufferedWriter(fw);
             //PrintWriter out = new PrintWriter(bw))
        ) {
            //TODO:
            fw.write(textRow);
        }
    }
}
