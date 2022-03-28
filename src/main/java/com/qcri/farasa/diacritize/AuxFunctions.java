/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qcri.farasa.diacritize;

import static com.qcri.farasa.diacritize.Main.farasaSegmenter;
import static com.qcri.farasa.diacritize.Main.openFileForReading;
import static com.qcri.farasa.diacritize.Main.openFileForWriting;
import com.qcri.farasa.pos.FarasaPOSTagger;
import com.qcri.farasa.segmenter.ArabicUtils;
import com.qcri.farasa.segmenter.Farasa;
import com.qcri.farasa.segmenter.FitTemplateClass;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kareem
 */
public class AuxFunctions {

    public static String getDiacritizedTemplate(String w, Farasa fr) {
        String diacritizedWord = w;
        String plainWord = ArabicUtils.removeDiacritics(w);
        String segmented ="";
        try {
            segmented = fr.segmentLine(w).get(0).replace("(نلل)", ""); // MUST CORRECT IN SEGMENTER
        } catch (IOException ex) {
            Logger.getLogger(AuxFunctions.class.getName()).log(Level.SEVERE, null, ex);
        }
        //String segmented = fr.segmentLine(w).get(0).replace("(نلل)", ""); // MUST CORRECT IN SEGMENTER
        String[] parts = (" " + fr.getProperSegmentation(segmented) + " ").split(";");
        String prefixes = parts[0].trim();
        String plainStem = parts[1].trim();
        String suffixes = parts[2].trim();
        diacritizedWord = DiacritizeText.transferDiacriticsFromWordToSegmentedVersion(diacritizedWord, segmented);
        // remove prefixes
        int k = 0;
        while (k < diacritizedWord.length() && !ArabicUtils.removeDiacritics(diacritizedWord.substring(0, k)).equals(prefixes)) {
            k++;
        }
        String stem = diacritizedWord.substring(k);
        stem = stem.replaceFirst("^[" + ArabicUtils.AllArabicDiacritics + "]+", "");
        if (suffixes.startsWith("+ات") || suffixes.startsWith("+ون") || suffixes.startsWith("+ين") || suffixes.startsWith("+ان") || suffixes.startsWith("+وا")) {
            suffixes = suffixes.substring(3);
        } 
        else if (suffixes.startsWith("+ة")) {
            suffixes = suffixes.substring(2);
        } 
//        else if (suffixes.startsWith("+ت") && suffixes.length() > 2) {
//            suffixes = suffixes.substring(2);
//        }
        // remove suffixes except NSUFF
        for (int j = suffixes.length() - 1; j >= 0; j--) {
            int pos = stem.lastIndexOf(suffixes.substring(j, j + 1));
            if (pos == -1) {
                // System.err.println(stem);
            } else {

                stem = stem.substring(0, pos);
            }
        }

        String suffixRemaining = "*";
        if (suffixes.trim().length() == 0)
            suffixRemaining = "";
        
        String root = fr.getStemRoot(ArabicUtils.removeDiacritics(plainStem));
        String template = fr.getStemTempate(ArabicUtils.removeDiacritics(plainStem));
        if (template.equals("Y"))
            return template;
        else
            return transferDiacriticsFromDiacritizedWordToTemplate(stem.replace("+", ""), root, template).replace("iy", "y").replace("aA", "A").replace("uw", "w") + suffixRemaining;
    }
    
    public static String getDiacritizedTemplateExp(String w, Farasa fr) {
        String diacritizedWord = w;
        String plainWord = ArabicUtils.removeDiacritics(w);
        String segmented ="";
        try {
            segmented = fr.segmentLine(w).get(0).replace("(نلل)", ""); // MUST CORRECT IN SEGMENTER
        } catch (IOException ex) {
            Logger.getLogger(AuxFunctions.class.getName()).log(Level.SEVERE, null, ex);
        }        
        //String segmented = fr.segmentLine(w).get(0).replace("(نلل)", ""); // MUST CORRECT IN SEGMENTER
        String[] parts = (" " + fr.getProperSegmentation(segmented) + " ").split(";");
        String prefixes = parts[0].trim();
        String plainStem = parts[1].trim();
        String suffixes = parts[2].trim();
        diacritizedWord = DiacritizeText.transferDiacriticsFromWordToSegmentedVersion(diacritizedWord, segmented);
        // remove prefixes
        int k = 0;
        while (k < diacritizedWord.length() && !ArabicUtils.removeDiacritics(diacritizedWord.substring(0, k)).equals(prefixes)) {
            k++;
        }
        String diacritizedPrefixes = diacritizedWord.substring(0, k);
        String stem = diacritizedWord.substring(k);
        while (stem.length() > 0 && ArabicUtils.AllArabicDiacritics.contains(stem.substring(0, 1)))
        {
            diacritizedPrefixes += stem.substring(0, 1);
            stem = stem.substring(1);
        }
        stem = stem.replaceFirst("^[" + ArabicUtils.AllArabicDiacritics + "]+", "");
        
        if (suffixes.startsWith("+ات") || suffixes.startsWith("+ون") || suffixes.startsWith("+ين") || suffixes.startsWith("+ان") || suffixes.startsWith("+وا")) {
            suffixes = suffixes.substring(3);
        } 
        else if (suffixes.startsWith("+ة")) {
            suffixes = suffixes.substring(2);
        } 
        else if (suffixes.startsWith("+ت") && suffixes.length() > 2) {
            suffixes = suffixes.substring(2);
        }
        // remove suffixes except NSUFF
        String diacritizedSuffixes = "";
        for (int j = suffixes.length() - 1; j >= 0; j--) {
            int pos = stem.lastIndexOf(suffixes.substring(j, j + 1));
            if (pos == -1) {
                //System.err.println(stem);
            } else {
                diacritizedSuffixes = stem.substring(pos) + diacritizedSuffixes;
                stem = stem.substring(0, pos);
            }
        }
        
        String suffixRemaining = "*";
        if (suffixes.trim().length() == 0)
            suffixRemaining = "";
        
        String root = fr.getStemRoot(ArabicUtils.removeDiacritics(plainStem));
        String template = fr.getStemTempate(ArabicUtils.removeDiacritics(plainStem));
        if (template.equals("Y"))
            return template;
        else
        {
            String output = transferDiacriticsFromDiacritizedWordToTemplate(stem.replace("+", ""), root, template).replace("iy", "y").replace("aA", "A").replace("uw", "w");
            String caseEnding = "";
            while (output.matches(".*[aiouNKF~]$")) {
                caseEnding = output.substring(output.length() - 1) + caseEnding;
                output = output.substring(0, output.length() - 1);
            }
            if (caseEnding.trim().length() == 0)
                caseEnding = "#";
            output = ArabicUtils.utf82buck(prefixes.replace("+", "")) + output + ArabicUtils.utf82buck(suffixes.replace("+", "")) + " " + caseEnding;
            return output;
        }
    }
    
    public static void buildComplexTemplates() throws IOException, ClassNotFoundException, InterruptedException {
        Farasa fr = new Farasa();
        // FarasaPOSTagger frPOS = new FarasaPOSTagger(farasaSegmenter);
        System.err.println("loaded Farasa");
        BufferedReader br = openFileForReading("C:\\RESEARCH\\FromMac\\work\\CLASSIC\\DIACRITIZE\\NEW-RDI\\all-text.txt.tok");
        String line = "";
        HashMap<String, HashMap<String, Long>> diacriticsOnTemplates = new HashMap<String, HashMap<String, Long>>();
        int cnt = 0;
        while ((line = br.readLine()) != null) {
            cnt++;
            // System.err.println(cnt);
            if (cnt % 100 == 0) {
                System.err.println(cnt);
            }
            String[] words = line.split(" +");
            for (String w : words) {
                if (ArabicUtils.removeDiacritics(w).matches("^[" + ArabicUtils.AllArabicLetters + "]+$")) {
                    String diacritizedWord = w;
                    String plainWord = ArabicUtils.removeDiacritics(w);
                    String segmented = fr.segmentLine(w).get(0).replace("(نلل)", ""); // MUST CORRECT IN SEGMENTER
                    String[] parts = (" " + fr.getProperSegmentation(segmented) + " ").split(";");
                    String prefixes = parts[0].trim();
                    String plainStem = parts[1].trim();
                    String suffixes = parts[2].trim();
                    if (plainStem.trim().length() > 0) {
                        String diacritizedTemplate = getDiacritizedTemplate(w, fr);
                        if (!diacritizedTemplate.equals("Y") && !diacritizedTemplate.endsWith("*")) {
                            // String diacritizedTemplate = transferDiacriticsFromDiacritizedWordToTemplate(stem.replace("+", ""), root, template);
                            // diacritizedTemplate = diacritizedTemplate.replace("iy", "y").replace("aA", "A").replace("uw", "w");
                            // get trailing diacritics
                            String caseEnding = "";
                            while (diacritizedTemplate.matches(".*[aiouNKF~]$")) {
                                caseEnding = diacritizedTemplate.substring(diacritizedTemplate.length() - 1) + caseEnding;
                                diacritizedTemplate = diacritizedTemplate.substring(0, diacritizedTemplate.length() - 1);
                            }
                            if (caseEnding.length() == 0) {
                                caseEnding = "#";
                            }
                            if (!diacriticsOnTemplates.containsKey(diacritizedTemplate)) {
                                diacriticsOnTemplates.put(diacritizedTemplate, new HashMap<String, Long>());
                            }
                            if (!diacriticsOnTemplates.get(diacritizedTemplate).containsKey(caseEnding)) {
                                diacriticsOnTemplates.get(diacritizedTemplate).put(caseEnding, 1l);
                            } else {
                                diacriticsOnTemplates.get(diacritizedTemplate).put(caseEnding, diacriticsOnTemplates.get(diacritizedTemplate).get(caseEnding) + 1);
                            }
                            // System.err.println(stem + "\t" + diacritizedTemplate);
                        }
                    }
                }
            }
        }
        BufferedWriter bw = openFileForWriting("C:\\RESEARCH\\FromMac\\work\\CLASSIC\\DIACRITIZE\\NEW-RDI\\all-text.txt.tok.diacTemplates.stem");
        for (String s : diacriticsOnTemplates.keySet()) {
            int count = 0;
            bw.write(s + "\t");
            for (String ss : diacriticsOnTemplates.get(s).keySet()) {
                bw.write(ss + "_" + diacriticsOnTemplates.get(s).get(ss) + " ");
                count += diacriticsOnTemplates.get(s).get(ss);
            }
            bw.write("\t" + count + "\n");
            bw.flush();
        }
        bw.close();
        // System.exit(0);
    }

    public static void buildComplexTemplatesExp() throws IOException, ClassNotFoundException, InterruptedException {
        Farasa fr = new Farasa();
        // FarasaPOSTagger frPOS = new FarasaPOSTagger(farasaSegmenter);
        System.err.println("loaded Farasa");
        System.err.println("loaded FitTemplate");
        BufferedReader br = openFileForReading("C:\\RESEARCH\\FromMac\\work\\CLASSIC\\DIACRITIZE\\NEW-RDI\\all-text.txt.tok");
        String line = "";
        HashMap<String, HashMap<String, Long>> diacriticsOnTemplates = new HashMap<String, HashMap<String, Long>>();
        int cnt = 0;
        while ((line = br.readLine()) != null) {
            cnt++;
            // System.err.println(cnt);
            if (cnt % 100 == 0) {
                System.err.println("AB"+cnt);
            }
            String[] words = line.split(" +");
            for (String w : words) {
                if (ArabicUtils.removeDiacritics(w).matches("^[" + ArabicUtils.AllArabicLetters + "]+$")) {
                    String diacritizedWord = w;
                    String plainWord = ArabicUtils.removeDiacritics(w);
                    String segmented = fr.segmentLine(w).get(0).replace("(نلل)", ""); // MUST CORRECT IN SEGMENTER
                    String[] parts = (" " + fr.getProperSegmentation(segmented) + " ").split(";");
                    String prefixes = parts[0].trim();
                    String plainStem = parts[1].trim();
                    String suffixes = parts[2].trim();
                    if (plainStem.trim().length() > 0) {
                        String diacritizedTemplate = getDiacritizedTemplateExp(w, fr);
                        if (!diacritizedTemplate.equals("Y") && !diacritizedTemplate.endsWith("*")) {
                            // String diacritizedTemplate = transferDiacriticsFromDiacritizedWordToTemplate(stem.replace("+", ""), root, template);
                            // diacritizedTemplate = diacritizedTemplate.replace("iy", "y").replace("aA", "A").replace("uw", "w");
                            // get trailing diacritics
                            String caseEnding = diacritizedTemplate.trim().substring(diacritizedTemplate.indexOf(" ") + 1);
                            diacritizedTemplate = diacritizedTemplate.trim().substring(0, diacritizedTemplate.indexOf(" "));
//                            while (diacritizedTemplate.matches(".*[aiouNKF~]$")) {
//                                caseEnding = diacritizedTemplate.substring(diacritizedTemplate.length() - 1) + caseEnding;
//                                diacritizedTemplate = diacritizedTemplate.substring(0, diacritizedTemplate.length() - 1);
//                            }
                            if (caseEnding.length() == 0) {
                                caseEnding = "#";
                            }
                            if (!diacriticsOnTemplates.containsKey(diacritizedTemplate)) {
                                diacriticsOnTemplates.put(diacritizedTemplate, new HashMap<String, Long>());
                            }
                            if (!diacriticsOnTemplates.get(diacritizedTemplate).containsKey(caseEnding)) {
                                diacriticsOnTemplates.get(diacritizedTemplate).put(caseEnding, 1l);
                            } else {
                                diacriticsOnTemplates.get(diacritizedTemplate).put(caseEnding, diacriticsOnTemplates.get(diacritizedTemplate).get(caseEnding) + 1);
                            }
                            // System.err.println(stem + "\t" + diacritizedTemplate);
                        }
                    }
                }
            }
        }
        BufferedWriter bw = openFileForWriting("C:\\RESEARCH\\FromMac\\work\\CLASSIC\\DIACRITIZE\\NEW-RDI\\all-text.txt.tok.diacTemplates.full");
        for (String s : diacriticsOnTemplates.keySet()) {
            int count = 0;
            bw.write(s + "\t");
            for (String ss : diacriticsOnTemplates.get(s).keySet()) {
                bw.write(ss + "_" + diacriticsOnTemplates.get(s).get(ss) + " ");
                count += diacriticsOnTemplates.get(s).get(ss);
            }
            bw.write("\t" + count + "\n");
            bw.flush();
        }
        bw.close();
        System.exit(0);
    }
    
    public static String transferDiacriticsFromDiacritizedWordToTemplate(String diacritizedStem, String root, String template) {
        String output = "";
        for (int i = 0; i < diacritizedStem.length(); i++) {
            if (ArabicUtils.AllArabicDiacritics.contains(diacritizedStem.substring(i, i + 1))) // if it is a diacritic, copy to output
            {
                output += ArabicUtils.utf82buck(diacritizedStem.substring(i, i + 1));
            } else if (template.length() >= 1) {
                output += template.substring(0, 1);
                template = template.substring(1);
            } else {
                output += ArabicUtils.utf82buck(diacritizedStem.substring(i, i + 1));
            }
        }
        return output;
    }

    public static String removeDefaultDiac(String s) {
        String out;

        out = s;
        out = out.replaceAll("َا", "ا");
        out = out.replaceAll("ِي", "ي");
        out = out.replaceAll("ُو", "و");
        out = out.replaceAll("الْ", "ال");

        out = out.replaceAll("ْ", "");

        out = out.replaceAll("َّ", "َّ");
        out = out.replaceAll("ِّ", "ِّ");
        out = out.replaceAll("ُّ", "ُّ");
        out = out.replaceAll("ًّ", "ًّ");
        out = out.replaceAll("ٍّ", "ٍّ");
        out = out.replaceAll("ٌّ", "ٌّ");

        out = out.replaceAll("اَ", "ا");

        // التقاء الساكنين: ref:اخْتِتَامُ, sys:اِخْتِتامُ
        out = out.replaceAll("اِ", "ا");

        return out;
    }

    public static String normalizeDiac(String s) {
        s = s.replace("ـ", "");
        int i, index;
        String s2;
        String[] CORRECT_DIAC = {"اَلْ", "ال", "اَل", "ال", "الْ", "ال", "الْ", "ال", "وَاَلْ", "وَال", "وَاَل", "وَال", "وَالْ", "وَال", "وَالْ", "وَال"};

        s2 = s;
        for (i = 0; i < CORRECT_DIAC.length / 2; i++) {
            index = s.indexOf(CORRECT_DIAC[i * 2]);
            if (index == 0) {
                s2 = String.format("%s%s", CORRECT_DIAC[i * 2 + 1], s.substring(index + CORRECT_DIAC[i * 2].length()));
                break;
            }
        }

        return s2;
    }

}
