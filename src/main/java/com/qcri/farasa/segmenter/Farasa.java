/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qcri.farasa.segmenter;

import static com.qcri.farasa.segmenter.ArabicUtils.prefixes;
import static com.qcri.farasa.segmenter.ArabicUtils.removeDiacritics;
import static com.qcri.farasa.segmenter.ArabicUtils.suffixes;
import static com.qcri.farasa.segmenter.ArabicUtils.tokenize;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @author kareemdarwish
 */
public class Farasa implements java.io.Serializable {
    public HashMap<String, ArrayList<String>> hmPreviouslySeenTokenizations = new HashMap<String, ArrayList<String>>();
    private static HashMap<String, ArrayList<String>> hmWordPossibleSplits = new HashMap<String, ArrayList<String>>();
    private static HashMap<String, Integer> hmListMorph = new HashMap<String, Integer>();
    private static HashMap<String, Integer> hmListGaz = new HashMap<String, Integer>();
    private static HashMap<String, Integer> hmAraLexCom = new HashMap<String, Integer>();
    public HashMap<String, Integer> hmBuck = new HashMap<String, Integer>();
    private static HashMap<String, Integer> hmLocations = new HashMap<String, Integer>();
    private static HashMap<String, Integer> hmPeople = new HashMap<String, Integer>();
    private static HashMap<String, Integer> hmStop = new HashMap<String, Integer>();
    private static HashMap<String, Integer> hPrefixes = new HashMap<String, Integer>();
    private static HashMap<String, Integer> hSuffixes = new HashMap<String, Integer>();
    private static HashMap<String, Boolean> hmValidSuffixes = new HashMap<String, Boolean>();
    private static HashMap<String, Boolean> hmValidPrefixes = new HashMap<String, Boolean>();
    private static HashMap<String, Double> hmTemplateCount = new HashMap<String, Double>();
    public HashMap<String, String> hmSeenBefore = new HashMap<String, String>();

    private static HashMap<String, Boolean> hmValidSuffixesSegmented = new HashMap<String, Boolean>();
    private static HashMap<String, Boolean> hmValidPrefixesSegmented = new HashMap<String, Boolean>();
    private static HashMap<String, Double> wordCount = new HashMap<String, Double>();

    private static HashMap<String, Double> probPrefixes = new HashMap<String, Double>();
    private static HashMap<String, Double> probSuffixes = new HashMap<String, Double>();

    private static HashMap<String, Double> probCondPrefixes = new HashMap<String, Double>();
    private static HashMap<String, Double> probCondSuffixes = new HashMap<String, Double>();

    private static HashMap<String, Double> seenTemplates = new HashMap<String, Double>();

    private static HashMap<String, HashMap<String, Double>> probPrefixSuffix = new HashMap<String, HashMap<String, Double>>();
    private static HashMap<String, HashMap<String, Double>> probSuffixPrefix = new HashMap<String, HashMap<String, Double>>();

    private static HashMap<String, Double> generalVariables = new HashMap<String, Double>();

    public static HashMap<String, String> hmWordDiacTokPOSLemma = new HashMap<String, String>();

    public static FitTemplateClass ft = null;
    public static Denormalize dn = null;

    /**
     *
     * @param BinDir
     */
    public Farasa() throws IOException, ClassNotFoundException {
        ft = new FitTemplateClass();
        loadStoredData();
        hSuffixes.put("تم", 1);
        hSuffixes.put("ما", 1);
        hSuffixes.put("و", 1);
        hSuffixes.put("ني", 1);
        hSuffixes.put("تا", 1);
        hSuffixes.put("من", 1);
    }

    public static FitTemplateClass getFitTemplate() {
        return ft;
    }
    
    public ArrayList<String> segmentLine(String line) throws IOException
    {
        boolean autoCorrect = false;
        ArrayList<String> output = new ArrayList<String>();
        ArrayList<String> words = tokenize(removeDiacritics(line));
        for (String w : words) {
            if (autoCorrect)
            {
                if (dn == null)
                    dn = new Denormalize();
                w = dn.correctWord(w);
            }
            // mostLikelyPartition((w), 1);
            if (!hmSeenBefore.containsKey(w)) {
                //TreeMap<Double, String> solutions = nbt.mostLikelyPartition(buck2utf8(w), 1);
                TreeMap<Double, String> solutions = mostLikelyPartition((w), 1);
                String topSolution = w;
                if (solutions.size() > 0)
                    topSolution = solutions.get(solutions.firstKey());
                topSolution = topSolution.replace(";", "").replace("++", "+");
                if (topSolution.startsWith("+"))
                    topSolution = topSolution.substring(1);
                if (topSolution.endsWith("+"))
                    topSolution = topSolution.substring(0, topSolution.length() - 1);
                // topSolution = getProperSegmentation(topSolution);
                hmSeenBefore.put(w, topSolution);
                output.add(topSolution.replace("++", "+"));

            }
            else
            {
                String topSolution = hmSeenBefore.get(w).replace(";", "").replace("++", "+");
                if (topSolution.startsWith("+"))
                    topSolution = topSolution.substring(1);
                if (topSolution.endsWith("+"))
                    topSolution = topSolution.substring(0, topSolution.length() - 1);
                // topSolution = getProperSegmentation(topSolution);
                output.add(topSolution);
            }
        }
        return output;
    }

    // Hamdy
    public ArrayList<String> lemmatizeLine(String line)
    {
        //ArrayList<String> segmentation = new ArrayList<String>();
        int i, firstSuffixIndex;
        boolean lemmaFound, emptyLemmas;
        ArrayList<String> lemmas = new ArrayList<String>();
        String diacTokPOSLemma, diacTokPOSLemma2, uniqueUndiacLemmas, topSolution, stem, stem2, lastPrefix, firstSuffix;
        String[] wordInfo, lemmaList, segments;

        // normalize Farsi letter
        line = ArabicUtils.replaceFarsiCharacters(line);
        ArrayList<String> words = tokenize(removeDiacritics(line));

        for (String w : words)
        {
            //lineLemmas += String.format("%s:", w);

            // Format of the file: Word Diac    Tokenization    POS Lemma   UniqueUndiacLemma
            diacTokPOSLemma = hmWordDiacTokPOSLemma.get(w);

            lemmaFound = false;
            emptyLemmas = false;

            if (diacTokPOSLemma != null)
            {
                wordInfo = diacTokPOSLemma.split("\t");
                if (wordInfo.length == 5)
                {
                    uniqueUndiacLemmas = wordInfo[4];
                    lemmaList = uniqueUndiacLemmas.split("،");

                    if (lemmaList.length >= 1)
                    {
                        // Take the first lemma (ordered by frequemcy)
                        lemmas.add(lemmaList[0]);
                        lemmaFound = true;
                    }
                    else
                    {
                        emptyLemmas = true;
                    }       
                }
            }

            if (!lemmaFound)
            {
                List<String> prefixes = Arrays.asList("و", "ب", "ل", "ال", "ف", "س", "ك"); // f, w, l, b, k, Al, s

                topSolution = w;

                if (!hmSeenBefore.containsKey(w))
                {
                    TreeMap<Double, String> solutions = mostLikelyPartition(w, 1);
                    topSolution = w;
                    if (solutions.size() > 0)
                    {
                        topSolution = solutions.get(solutions.firstKey());
                    }

                    topSolution = topSolution.replace(";", "").replace("++", "+");
                    hmSeenBefore.put(w, topSolution);

                    //if (norm)
                    //{
                    //    topSolution = normalizeFull(topSolution);
                    //}

                    hmSeenBefore.put(w, topSolution);

                    //bw.write(topSolution.replace(";", "").replace("++", "+") + " ");
                    //bw.flush();
                }
                else
                {
                    topSolution = hmSeenBefore.get(w).replace(";", "").replace("++", "+");
                    //if (norm)
                    //{
                    //    topSolution = normalizeFull(topSolution);
                    //}

                    //bw.write(topSolution + " ");
                }

                segments = topSolution.split("\\+");

                if (segments.length == 1)
                {
                    lemmas.add(segments[0]);
                }
                else
                {
                    stem = "";
                    lastPrefix = "";
                    firstSuffix = "";
                    firstSuffixIndex = segments.length;
                    for (i = 0; i < segments.length; i++)
                    {
                        if (prefixes.contains(segments[i]))
                        {
                            lastPrefix = segments[i];
                            continue;
                        }
                        stem = segments[i];

                        if (i < segments.length - 1)
                        {
                            firstSuffix = segments[i + 1];
                            firstSuffixIndex = i;
                        }
                        break;
                    }

                    if (stem.isEmpty())
                    {
                        stem = lastPrefix;
                    }

                    diacTokPOSLemma = null;
                    if (!firstSuffix.isEmpty())
                    {
                        diacTokPOSLemma2 = null;
                        stem2 = "";
                        if ((firstSuffixIndex < segments.length - 1) && firstSuffix.equals("ت"))
                        {
                            // Try taa marbouta first
                            stem2 = String.format("%s%s", stem, "ة");
                            diacTokPOSLemma2 = hmWordDiacTokPOSLemma.get(stem2);
                        }

                        if (diacTokPOSLemma2 == null)
                        {
                            stem2 = String.format("%s%s", stem, firstSuffix);

                            diacTokPOSLemma2 = hmWordDiacTokPOSLemma.get(stem2);
                        }

                        if (diacTokPOSLemma2 != null)
                        {
                            diacTokPOSLemma = diacTokPOSLemma2;
                            stem = stem2;
                        }
                    }

                    if (diacTokPOSLemma == null)
                    {
                        diacTokPOSLemma = hmWordDiacTokPOSLemma.get(stem);
                    }

                    if (diacTokPOSLemma == null)
                    {
                        if (stem.endsWith("ؤ") || stem.endsWith("ئ"))
                        {
                            stem2 = stem.substring(0, stem.length() - 1);
                            stem2 += "ء";

                            diacTokPOSLemma2 = hmWordDiacTokPOSLemma.get(stem2);

                            if (diacTokPOSLemma2 != null)
                            {
                                diacTokPOSLemma = diacTokPOSLemma2;
                                stem = stem2;
                            }
                        }
                    }

                    lemmaFound = false;
                    emptyLemmas = false;

                    if (diacTokPOSLemma != null)
                    {
                        wordInfo = diacTokPOSLemma.split("\t");
                        if (wordInfo.length == 5)
                        {
                            uniqueUndiacLemmas = wordInfo[4];
                            lemmaList = uniqueUndiacLemmas.split("،");

                            if (lemmaList.length >= 1)
                            {
                                // Take the first lemma (ordered by frequemcy)
                                lemmas.add(lemmaList[0]);
                                lemmaFound = true;
                            }
                            else
                            {
                                emptyLemmas = true;
                            }       
                        }
                    }
                    else
                    {
                        lemmas.add(stem);
                    }
                }                    
            }
        }

        return lemmas;
    }

    public void removeNLLFromSeenSegmentations() throws IOException
    {
        HashMap<String, String> hmSeenBeforeTemp = new HashMap<String, String>();
        for (String s : hmSeenBefore.keySet())
        {
            hmSeenBeforeTemp.put(s, hmSeenBefore.get(s).replace("(نلل)", "").trim());
        }
        serializeMap("c:/RESEARCH/FromMac/work/CLASSIC/DIACRITIZE/NEW-RDI/", "hmSeenBefore", hmSeenBeforeTemp);
    }
    
    public void serializeMap(String BinDir, String MapName, HashMap input) throws IOException {
        FileOutputStream fos
                = new FileOutputStream(BinDir + "NTBdata." + MapName + ".ser");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(input);
        oos.close();
        fos.close();
    }
    
    public void storeDataItemsFile(String BinDir) throws IOException
    {
        serializeMap(BinDir, "hmListMorph", hmListMorph);
        serializeMap(BinDir, "hmListGaz", hmListGaz);
        serializeMap(BinDir, "hmAraLexCom", hmAraLexCom);
        serializeMap(BinDir, "hmBuck", hmBuck);
        serializeMap(BinDir, "hmLocations", hmLocations);
        serializeMap(BinDir, "hmPeople", hmPeople);
        serializeMap(BinDir, "hmStop", hmStop);
        serializeMap(BinDir, "hPrefixes", hPrefixes);
        serializeMap(BinDir, "hSuffixes", hSuffixes);
        serializeMap(BinDir, "hmValidSuffixes", hmValidSuffixes);
        serializeMap(BinDir, "hmValidPrefixes", hmValidPrefixes);
        serializeMap(BinDir, "hmTemplateCount", hmTemplateCount);
        serializeMap(BinDir, "hmValidSuffixesSegmented", hmValidSuffixesSegmented);
        serializeMap(BinDir, "hmValidPrefixesSegmented", hmValidPrefixesSegmented);
        serializeMap(BinDir, "wordCount", wordCount);
        serializeMap(BinDir, "probPrefixes", probPrefixes);
        serializeMap(BinDir, "probSuffixes", probSuffixes);
        serializeMap(BinDir, "probCondPrefixes", probCondPrefixes);
        serializeMap(BinDir, "probCondSuffixes", probCondSuffixes);
        serializeMap(BinDir, "seenTemplates", seenTemplates);
        serializeMap(BinDir, "hmPreviouslySeenTokenizations", hmPreviouslySeenTokenizations);
        serializeMap(BinDir, "hmWordPossibleSplits", hmWordPossibleSplits);
        serializeMap(BinDir, "probPrefixSuffix", probPrefixSuffix);
        serializeMap(BinDir, "probSuffixPrefix", probSuffixPrefix);
        serializeMap(BinDir, "generalVariables", generalVariables);
        hmValidSuffixes.put("تم", Boolean.TRUE);
        hmValidSuffixesSegmented.put("تم", Boolean.TRUE);

    }
    
    private InputStream resolveName(String name) {
        if (name == null) {
            return null;
        }
        if (!name.startsWith("/")) {
            String baseName = this.getClass().getName();
            int index = baseName.lastIndexOf('.');
            if (index != -1) {
                name = baseName.substring(0, index).replace('.', '/') + "/" + name;
            }
        } else {
            name = name.substring(1);
        }
	// return name;
	ClassLoader cl = this.getClass().getClassLoader();
	return cl.getResourceAsStream(name);
    }
    
    public HashMap deserializeMap(String MapName) throws IOException, ClassNotFoundException
    {
         ObjectInputStream ois = new ObjectInputStream(resolveName("/FarasaData/NTBdata." + MapName + ".ser"));
         HashMap map = (HashMap) ois.readObject();
         ois.close();
         return map;
    }
    
    public void loadStoredData() throws IOException, ClassNotFoundException
    {
        hmListMorph = deserializeMap("hmListMorph");
        hmListGaz = deserializeMap("hmListGaz");
        hmAraLexCom = deserializeMap("hmAraLexCom");
        hmBuck = deserializeMap("hmBuck");
        hmLocations = deserializeMap("hmLocations");
        hmPeople = deserializeMap("hmPeople");
        hmStop = deserializeMap("hmStop");
        hPrefixes = deserializeMap("hPrefixes");
        hSuffixes = deserializeMap("hSuffixes");
        hmValidSuffixes = deserializeMap("hmValidSuffixes");
        hmValidPrefixes = deserializeMap("hmValidPrefixes");
        hmTemplateCount = deserializeMap("hmTemplateCount");
        hmValidSuffixesSegmented = deserializeMap("hmValidSuffixesSegmented");
        hmValidPrefixesSegmented = deserializeMap("hmValidPrefixesSegmented");
        wordCount = deserializeMap("wordCount");
        probPrefixes = deserializeMap("probPrefixes");
        probSuffixes = deserializeMap("probSuffixes");
        probCondPrefixes = deserializeMap("probCondPrefixes");
        probCondSuffixes = deserializeMap("probCondSuffixes");
        seenTemplates = deserializeMap("seenTemplates"); // ??
        //hmPreviouslySeenTokenizations = deserializeMap("hmPreviouslySeenTokenizations");
        hmWordPossibleSplits = deserializeMap("hmWordPossibleSplits");
        probPrefixSuffix = deserializeMap("probPrefixSuffix");
        probSuffixPrefix = deserializeMap("probSuffixPrefix");
        generalVariables = deserializeMap("generalVariables");
        hmSeenBefore = deserializeMap("SeenBefore");

        hmWordDiacTokPOSLemma = deserializeMap("hmWordDiacTokPOSLemma");
    }
    
    public double scorePartition(String[] parts) {
        double score = 0;
        String prefix = parts[0].trim();
        String suffix = parts[2].trim();
        String stem = parts[1].trim();
                // assemble score

        // String[] magicNumbers = "1:-0.23482376 2:-0.21097635 3:0.25787985 4:0.16191271 5:0.13404779 6:0.79553878 7:0.27828842 8:-0.21669699 9:0.65872103 10:0.63085192 11:-0.10308913 12:0.10140695".split(" +");
        String[] magicNumbers = "1:-0.097825818 2:-0.03893654 3:0.13109569 4:0.18436976 5:0.11448806 6:0.53001714 7:0.21098258 8:-0.17760228 9:0.44223878 10:0.26183113 11:-0.05603376 12:0.055829503 13:-0.17745291 14:0.015865559 15:0.66909122 16:0.16948195 17:0.15397599 18:0.60355717".split(" +");
        ArrayList<Double> magicNo = new ArrayList<Double>();
        for (String m : magicNumbers) {
            magicNo.add(Double.parseDouble(m.substring(m.indexOf(":") + 1)));
        }

        if (probPrefixes.containsKey(prefix)) {
            score += magicNo.get(0) * Math.log(probPrefixes.get(prefix));
        } else {
            score += magicNo.get(0) * -10;
        }

        if (probSuffixes.containsKey(suffix)) {
            score += magicNo.get(1) * Math.log(probSuffixes.get(suffix));
        } else {
            score += magicNo.get(1) * -10;
        }

        String trimmedTemp = suffix.replace("+", "").replace(";", "").replace(",", "");
        String altStem = "";
        if (trimmedTemp.startsWith("ت") && trimmedTemp.length() > 1) {
            altStem = stem + "ة";
        }

        double stemWordCount = -10;
        if (wordCount.containsKey(stem)) {
            stemWordCount = wordCount.get(stem);
        } else if (altStem.length() > 1 && wordCount.containsKey(altStem)) // && wordCount.get(altStem) > stemWordCount)
        {
            stemWordCount = wordCount.get(altStem);
        }

        //if (wordCount.containsKey(stem))
        score += magicNo.get(2) * stemWordCount;
                //else
        //    score += 0.19470689 * -10;

        if (probPrefixSuffix.containsKey(prefix) && probPrefixSuffix.get(prefix).containsKey(suffix)) {
            score += magicNo.get(3) * Math.log(probPrefixSuffix.get(prefix).get(suffix));
        } else {
            score += magicNo.get(3) * -20;
        }

        if (probSuffixPrefix.containsKey(suffix) && probSuffixPrefix.get(suffix).containsKey(prefix)) {
            score += magicNo.get(4) * Math.log(probSuffixPrefix.get(suffix).get(prefix));
        } else {
            score += magicNo.get(4) * -20;
        }

        if (!ft.fitTemplate(stem).equals("Y")) {
            score += magicNo.get(5) * Math.log(generalVariables.get("hasTemplate"));
            // score += magicNo.get(5) * Math.log(hasTemplate);
        } else {
            score += magicNo.get(5) * Math.log(1 - generalVariables.get("hasTemplate"));
            // score += magicNo.get(5) * Math.log(1 - hasTemplate);
        }

        if (hmListMorph.containsKey(stem) || (stem.endsWith("ي") && hmListMorph.containsKey(stem.substring(0, stem.length() - 1) + "ى"))) {
            score += magicNo.get(6) * Math.log(generalVariables.get("inMorphList"));
            // score += magicNo.get(6) * Math.log(inMorphList);
        } else {
            score += magicNo.get(6) * Math.log(1 - generalVariables.get("inMorphList"));
            // score += magicNo.get(6) * Math.log(1 - inMorphList);
        }

        if (hmListGaz.containsKey(stem) || (stem.endsWith("ي") && hmListGaz.containsKey(stem.substring(0, stem.length() - 1) + "ى"))) {
            score += magicNo.get(7) * Math.log(generalVariables.get("inGazList"));
            // score += magicNo.get(7) * Math.log(inGazList);
        } else {
            score += magicNo.get(7) * Math.log(1 - generalVariables.get("inGazList"));
            // score += magicNo.get(7) * Math.log(1 - inGazList);
        }

        if (probCondPrefixes.containsKey(prefix)) {
            score += magicNo.get(8) * Math.log(probCondPrefixes.get(prefix));
        } else {
            score += magicNo.get(8) * -20;
        }

        if (probCondSuffixes.containsKey(suffix)) {
            score += magicNo.get(9) * Math.log(probCondSuffixes.get(suffix));
        } else {
            score += magicNo.get(9) * -20;
        }

        // get probability with first suffix . for example xT + p would produce xTp 
        String stemPlusFirstSuffix = stem;
        if (suffix.indexOf("+", 1) > 0) {
            stemPlusFirstSuffix += suffix.substring(1, suffix.indexOf("+", 1));
        } else {
            stemPlusFirstSuffix += suffix;
        }
        trimmedTemp = stemPlusFirstSuffix.replace("+", "").replace(";", "").replace(",", "");
        stemWordCount = -10;
        if (wordCount.containsKey(stemPlusFirstSuffix)) {
            stemWordCount = wordCount.get(stemPlusFirstSuffix);
        } else if (stem.endsWith("ي") && wordCount.containsKey(stem.substring(0, stem.length() - 1) + "ى")) {
            stemWordCount = wordCount.get(stem.substring(0, stem.length() - 1) + "ى");
        } else if (stemPlusFirstSuffix.endsWith("ت") && wordCount.containsKey(stemPlusFirstSuffix.substring(0, stemPlusFirstSuffix.length() - 1) + "ة")) {
            stemWordCount = wordCount.get(stemPlusFirstSuffix.substring(0, stemPlusFirstSuffix.length() - 1) + "ة");
        }
        score += magicNo.get(10) * stemWordCount;
        
        // put template feature
        String template = ft.fitTemplate(stem);
        if (hmTemplateCount.containsKey(template))
            score += magicNo.get(11) * Math.log(hmTemplateCount.get(template));
        else
            score += magicNo.get(11) * -10;
        
        // difference from average length
        score += magicNo.get(12) * Math.log(Math.abs(stem.length() - generalVariables.get("averageStemLength")));
        // score += magicNo.get(12) * Math.log(Math.abs(stem.length() - averageStemLength));
        
        
        trimmedTemp = suffix.replace("+", "").replace(";", "").replace(",", "");
        altStem = "";
        if (trimmedTemp.startsWith("ت") && trimmedTemp.length() > 1)
            altStem = stem + "ة";

        if (wordCount.containsKey(stem))
            stemWordCount = wordCount.get(stem);
        else if (stem.endsWith("ي") && wordCount.containsKey(stem.substring(0, stem.length() - 1) + "ى"))
            stemWordCount = wordCount.get(stem.substring(0, stem.length() - 1) + "ى");
        else if (altStem.trim().length() > 0 && wordCount.containsKey(altStem)) // && wordCount.get(altStem) > stemWordCount)
            stemWordCount = wordCount.get(altStem);

        if (hmAraLexCom.containsKey(stem))
        {
            if (wordCount.containsKey(stem))
                score += magicNo.get(13) *  wordCount.get(stem);
            else
                score += magicNo.get(13) * -10;
        }
        else if (stem.endsWith("ي") && hmAraLexCom.containsKey(stem.substring(0, stem.length() - 1) + "ى"))
        {
            if (wordCount.containsKey(stem.substring(0, stem.length() - 1) + "ى"))
                score += magicNo.get(13) *  wordCount.get(stem.substring(0, stem.length() - 1) + "ى");
            else
                score += magicNo.get(13) * -10;
        }
        else if (altStem.trim().length() > 0 && hmAraLexCom.containsKey(altStem))
        {
            if (wordCount.containsKey(altStem))
                score += magicNo.get(13) * wordCount.get(altStem);
            else
                score += magicNo.get(13) * -10;
        }
        else
        {
            score += magicNo.get(13) * -20;
        }
        
        if (hmBuck.containsKey(stem))
        {
            score += magicNo.get(14);
        }
        else if (stem.endsWith("ي") && hmBuck.containsKey(stem.substring(0, stem.length() - 1) + "ى"))
        {
            score += magicNo.get(14);
        }
        else
        {
            score += -1 * magicNo.get(14);
        }
        
        if (hmLocations.containsKey(stem))
        {
            score += magicNo.get(15);
        }
        else
        {
            score += -1 * magicNo.get(15);
        }
        
        if (hmPeople.containsKey(stem))
        {
            score += magicNo.get(16);
        }
        else
        {
            score += -1 * magicNo.get(16);
        }
        
        if (hmStop.containsKey(stem))
        {
            score += magicNo.get(17);
        }
        else if (stem.endsWith("ي") && hmStop.containsKey(stem.substring(0, stem.length() - 1) + "ى"))
        {
            score += magicNo.get(17);
        }
        else
        {
            score += -1 * magicNo.get(17);
        }
        return score;
    }
    
    public TreeMap<Double, String> mostLikelyPartition(String word, int numberOfSolutions)
    {
        word = word.trim();
        ArrayList<String> possiblePartitions = getAllPossiblePartitionsOfString(word);
        if (word.startsWith("لل"))
            possiblePartitions.addAll(getAllPossiblePartitionsOfString("لال" + word.substring(2)));
        else if (word.startsWith("ولل"))
            possiblePartitions.addAll(getAllPossiblePartitionsOfString("ولال" + word.substring(3)));
        else if (word.startsWith("فلل"))
            possiblePartitions.addAll(getAllPossiblePartitionsOfString("فلال" + word.substring(3)));
        // score all the different options
        TreeMap<Double, String> scores = new TreeMap<Double, String>();
        
        if (hmPreviouslySeenTokenizations.containsKey(word.replace("+", "")))
        {
            for (String p : hmPreviouslySeenTokenizations.get(word.replace("+", "")))
            {
                String pp = getProperSegmentation(p.replace(";", ""));
                String[] parts = (" " + pp + " ").split(";");
                double score = scorePartition(parts);
                while (scores.containsKey(score))
                    score -= 0.00001;
                scores.put(score, pp);
            }
        }
        else 
        {
            for (String p : possiblePartitions)
            {
                String pp = getProperSegmentation(p.replace(";", ""));
                String[] parts = (" " + pp + " ").split(";");
                if (parts.length == 3)
                {
                    if (parts[0].trim().length() == 0 && parts[1].trim().length() == 0)
                    {
                        // this means that there are suffixes only and that is impossible
                    }
                    else
                    {
                        double score = scorePartition(parts);
                        while (scores.containsKey(score))
                            score -= 0.00001;
                        scores.put(score, pp);
                    }
                }
            }
        }
        // keep the top 3 segmentations and throw away the rest
        int scoresSize = scores.size() - numberOfSolutions;
        TreeMap<Double, String> scoresFinal = new TreeMap<Double, String>();
        int i = 0;
        for (double d : scores.keySet())
        {
            if (i >= scoresSize)
                scoresFinal.put(d, scores.get(d));
            i++;
        }
        return scoresFinal;
    }
    
    public ArrayList<String> getAllPossiblePartitionsOfString(String s)
    {
        ArrayList<String> output = new ArrayList<String>();
        if (s.length() > 20)
            output.add(s);
        else
        {
            s = s.trim();
            if (s.length() > 0)
            {
                String fullPartition = s.substring(0, 1);
                for (int i = 1; i < s.length(); i++)
                    fullPartition += "," + s.charAt(i);
                String correctFullPartition = getProperSegmentation(fullPartition.replace(",", "+").replaceAll("\\++", "+"));
                String[] parts = (" " + correctFullPartition + " ").split(";");
                if (!output.contains(correctFullPartition))
                {
                    if (parts[1].length() != 1 || s.length() == 1)
                        output.add(correctFullPartition);
                }
                // output.add(fullPartition);
                if (fullPartition.contains(","))
                    output = getSubPartitions(fullPartition, output);
            }
        }
        return output;
    }
    
    private ArrayList<String> getSubPartitions(String s, ArrayList<String> output)
    {
        // ArrayList<String> output = new ArrayList<String>();
        if (s.contains(","))
        {
            String[] parts = s.split(",");
            for (int i = 0; i < parts.length - 1; i++)
            {
                String ss = "";
                // construct string with 1 units until i
                for (int j = 0; j < i; j++)
                {
                    if (j == 0)
                        ss = parts[j];
                    else
                        ss += "," + parts[j];
                }
                // put 2 units
                if (i == 0)
                    ss = parts[i] + parts[i+1]; 
                else
                    ss += "," + parts[i] + parts[i+1];
                // put remaining 1 units until end of string
                for (int k = i + 2; k < parts.length; k++)
                {
                    if (k == 0)
                        ss = parts[k];
                    else
                        ss += "," + parts[k];
                }
                if (!output.contains(getProperSegmentation(ss.replace(",", "+").replaceAll("\\++", "+"))))
                {
                    output.add(getProperSegmentation(ss.replace(",", "+").replaceAll("\\++", "+")));
                    if (ss.contains(","))
                        output = getSubPartitions(ss, output);
                }
            }
        }
        return output;
    }
    
    public ArrayList<String> findAllPossibleSplits(String input, HashMap<String, Integer> list1, HashMap<String, Integer> list2)
    {
        if (hmWordPossibleSplits.containsKey(input))
            return hmWordPossibleSplits.get(input);

        ArrayList<String> possibleSplits = possibleSplits = new ArrayList<String>();
        
        if (list1.containsKey(input) || list2.containsKey(input))
            possibleSplits.add(input);
        
        for (int j = 1; j < input.length(); j++)
        {
            String head = input.substring(0, j);
            String trail = input.substring(j);

            if (checkIfLeadingLettersCouldBePrefixes(head)) {
                // get prefix split
                String prefixSplits = getPrefixSplit(head);
                // check if the rest is stem + suffixes
                if (trail.length() >= 2) {
                    for (int i = 0; i <= trail.length(); i++) {
                        String tok = trail.substring(0, i);
                        String remain = trail.substring(i);
                        String key = "";
                        if (tok.length() > 0)
                        {
                            if (remain.trim().length() == 0)
                            {
                                if (tok.endsWith("ة"))
                                    key = prefixSplits + ";" + tok.substring(0, tok.length() - 1) + ";" + "ة";
                                else
                                    key = prefixSplits + ";" + tok + ";";
                            }
                            else
                                key = prefixSplits + ";" + tok + ";" + checkIfRemainingLettersCouldBeSuffixesString(remain);
                        }
                        else
                        {
                            if (remain.trim().length() == 0)
                                key = prefixSplits;
                            else
                                key = prefixSplits + ";;" + checkIfRemainingLettersCouldBeSuffixesString(remain);
                        }
                        if ((list1.containsKey(tok) || list2.containsKey(tok)) && 
                                (checkIfRemainingLettersCouldBeSuffixes(remain) || remain.trim().length() == 0) &&
                                !possibleSplits.contains(key)
                                ) {
                            possibleSplits.add(key);
                        }
                    }
                }
            }
            else if (checkIfRemainingLettersCouldBeSuffixes(trail))
            {
                // check if rest is prefixes + stem
                if (head.length() >= 2)
                {
                    for (int i = 0; i <= head.length(); i++)
                    {
                        if (i == 0 && (list1.containsKey(head) || list2.containsKey(head))
                                && !possibleSplits.contains(head + ";" + checkIfRemainingLettersCouldBeSuffixesString(trail))
                                )
                            possibleSplits.add(";" + head + ";" + checkIfRemainingLettersCouldBeSuffixesString(trail));
                        else
                        {
                            String prefix = head.substring(0, i);
                            String tok = head.substring(i);
                            String key = "";
                            if (tok.length() > 0)
                                key = getPrefixSplit(prefix) + ";" + tok + ";" + checkIfRemainingLettersCouldBeSuffixesString(trail);
                            else
                                key = getPrefixSplit(prefix) + ";;" + checkIfRemainingLettersCouldBeSuffixesString(trail);
                            if ((list1.containsKey(tok) || list2.containsKey(tok)) 
                                    && checkIfLeadingLettersCouldBePrefixes(prefix) 
                                    && !possibleSplits.contains(key))
                                possibleSplits.add(key);
                        }
                    }
                }
            }
        }
        hmWordPossibleSplits.put(input, possibleSplits);
        return possibleSplits;
    }
    
        private static boolean checkIfLeadingLettersCouldBePrefixes(String head)
    {
        return head.matches("(و|ف)?(ب|ك|ل)?(ال)?") || head.equals("س") || head.equals("وس") || head.equals("فس");
    }
    
    private static String getPrefixSplit(String head)
    {
        String output = "";
        if (head.startsWith("و") || head.startsWith("ف"))
        {
            output += head.charAt(0) + ",";
            head = head.substring(1);
        }
        if (head.startsWith("ب") || head.startsWith("ك") || head.startsWith("ل") || head.startsWith("س"))
        {
            output += head.charAt(0) + ",";
            head = head.substring(1);
        }
        if (head.startsWith("ال"))
        {
            output += head.substring(0, 2) + ",";
        }
        output = output.replaceFirst(",$", "");
        return output;
    }
    
    private static boolean checkIfRemainingLettersCouldBeSuffixes(String trail)
    {
        return hmValidSuffixes.containsKey(trail);
    }
    
    private String checkIfRemainingLettersCouldBeSuffixesString(String trail)
    {
        String output = "notFound";
        if (!checkIfRemainingLettersCouldBeSuffixes(trail))
            return output;
        ArrayList<String> parts = getAllPossiblePartitionsOfString(trail);
        for (String p : parts)
        {
            if (hmValidSuffixesSegmented.containsKey(p))
                output = p;
        }
        return output;
    }
    
    public String getProperSegmentation(String input)
    {
        if (!input.contains("+"))
            return ";" + input + ";";
        if (hPrefixes.isEmpty()) {
            for (int i = 0; i < prefixes.length; i++) {
                hPrefixes.put(prefixes[i], 1);
            }
        }
        if (hSuffixes.isEmpty()) {
            for (int i = 0; i < suffixes.length; i++) {
                hSuffixes.put(suffixes[i], 1);
            }
        }
        String output = "";
        String[] word = input.split("\\+");
        String currentPrefix = "";
        String currentSuffix = "";
        int iValidPrefix = -1;
        while (iValidPrefix + 1 < word.length && hPrefixes.containsKey(word[iValidPrefix + 1])) {
            iValidPrefix++;
        }

        int iValidSuffix = word.length;

        while (iValidSuffix > Math.max(iValidPrefix, 0)
                && iValidSuffix > 1
                && (hSuffixes.containsKey(word[iValidSuffix - 1])
                || word[iValidSuffix - 1].equals("_"))) {
            iValidSuffix--;
        }

        for (int i = 0; i <= iValidPrefix; i++) {
            currentPrefix += word[i] + "+";
        }
        String stemPart = "";
        for (int i = iValidPrefix + 1; i < iValidSuffix; i++) {
            stemPart += word[i];
        }

        if (iValidSuffix == iValidPrefix) {
            iValidSuffix++;
        }

        for (int i = iValidSuffix; i < word.length && iValidSuffix != iValidPrefix; i++) {
            currentSuffix += "+" + word[i];
        }

        if (currentPrefix.endsWith("س+") && !stemPart.matches("^[ينأت].*"))
        {
            currentPrefix = currentPrefix.substring(0, currentPrefix.length() - 2);
            stemPart = "س" + stemPart;
        }
        output = currentPrefix + ";" + stemPart + ";" + currentSuffix;
        output = output.replaceFirst("^\\+", "");
        output = output.replaceFirst("\\+$", "");
        return output.replace("++", "+");
    }    
    
    public BufferedReader openFileForReading(String filename) throws FileNotFoundException {
        BufferedReader sr = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filename))));
        return sr;
    }

    public BufferedWriter openFileForWriting(String filename) throws FileNotFoundException {
        BufferedWriter sw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(filename))));
        return sw;
    }  
    
    private static void populatePossibleAffixes () 
    {
        // populate prefixes
        // if (head.matches("(و|ف)?(ب|ك|ل)?(ال)?") || head.equals("س") || head.equals("وس") || head.equals("فس"))
        
        String[] SetPreA = {"", "و", "ف"};
        String[] SetPreB = {"", "ب", "ك", "ل"};
        String[] SetPreC = {"", "ال"};
        
        for (String a : SetPreA)
        {
            for (String b : SetPreB)
            {
                for (String c : SetPreC)
                {
                    String suf = a + b + c;
                    if (suf.trim().length() > 0)
                        hmValidPrefixes.put(suf, Boolean.TRUE);
                }
            }
        }
        hmValidPrefixes.put("", Boolean.TRUE);
        hmValidPrefixes.put("س", Boolean.TRUE);
        hmValidPrefixes.put("وس", Boolean.TRUE);
        hmValidPrefixes.put("فس", Boolean.TRUE);
        
//            p.matches("(ات|ون|ين)?,(ه|ها|هما|هم|هن|ك|كما|كم|كن|نا|ي)?")   -- done
//            || p.matches("[ويا],(ه|ها|هما|هم|هن|ك|كما|كم|كن|نا|ي)?")      -- done
//            || p.matches("(ن|ت),(ه|ها|هما|هم|هن|ك|كما|كم|كن|كي|ي)")       -- done
//            || p.matches("(نا),(ه|ها|هما|هم|هن|ك|كما|كم|كن|كي|ي)")        -- done
//            || p.matches("(ون|ين|ات|ان|ا|ي|و|ت|ة|ن|وا)")               -- done
//            || p.matches("(ه|ها|هما|هم|هن|ك|كما|كم|كن|نا|ي)")             -- done
//            || p.matches("(ون|ين|ي|و|ا),(نا|ن),(ه|ها|هما|هم|هن|ك|كما|كم|كن)")
        
        String[] SetA1 = {"", "ات", "ون", "ين", "ان"};
        String[] SetA2 = {"", "ه", "ها", "هما", "هم", "هن", "ك", "كما", "كم", "كن", "نا", "ي"};
        
        for (String s : SetA1)
        {
            for (String ss : SetA2)
            {
                hmValidSuffixes.put(s + ss, Boolean.TRUE);
            }
        }
        
        String[] SetB1 = {"ا", "و", "ي"};
        for (String s : SetB1)
        {
            for (String ss : SetA2)
            {
                hmValidSuffixes.put(s + ss, Boolean.TRUE);
            }
        }
        
        String[] SetC1 = {"", "ن", "ت", "نا"};
        String[] SetC2 = {"", "ه", "ها", "هما", "هم", "هن", "ك", "كما", "كم", "كن", "كي", "ي"};
        for (String s : SetC1)
        {
            for (String ss : SetC2)
            {
                hmValidSuffixes.put(s + ss, Boolean.TRUE);
            }
        }
        
        String[] SetD1 = {"ون", "ين", "ات", "ان", "ا", "ي", "و", "ت", "ة", "ن", "وا", "ي", "و", "ا"};
        for (String s : SetD1)
        {
            hmValidSuffixes.put(s, Boolean.TRUE);
        }

        String[] setA = {"ا", "و", "ي", "ين", "ون"};
        String[] setB = {"ن", "نا"};
        String[] setC = {"ه", "ها", "هما", "هم", "هن", "ك", "كما", "كم", "كن"};
        for (String a : setA)
        {
            for (String b : setB)
            {
                for (String c : setC)
                {
                    String suf = a + b + c;
                    hmValidSuffixes.put(suf, Boolean.TRUE);
                }
            }
        }
        hmValidSuffixes.put("", Boolean.TRUE);
    }
    
    private static void populatePossibleAffixesSegmented() 
    {
        // populate prefixes
        // if (head.matches("(و|ف)?(ب|ك|ل)?(ال)?") || head.equals("س") || head.equals("وس") || head.equals("فس"))
        
        String[] SetPreA = {"", "و", "ف"};
        String[] SetPreB = {"", "ب", "ك", "ل"};
        String[] SetPreC = {"", "ال"};
        
        for (String a : SetPreA)
        {
            for (String b : SetPreB)
            {
                for (String c : SetPreC)
                {
                    String separator1 = "";
                    String separator2 = "";
                    if (a.length() > 0 && b.length() > 0)
                        separator1 = ",";
                    if (b.length() > 0 && c.length() > 0)
                        separator2 = ",";
                    String suf = a + separator1 + b + separator2 + c;
                    if (suf.trim().length() > 0)
                    {
                        hmValidPrefixesSegmented.put(suf, Boolean.TRUE);
                        hmValidPrefixes.put(suf.replace(",", ""), Boolean.TRUE);
                    }
                }
            }
        }
        hmValidPrefixesSegmented.put("", Boolean.TRUE);
        hmValidPrefixesSegmented.put("س", Boolean.TRUE);
        hmValidPrefixesSegmented.put("و,س", Boolean.TRUE);
        hmValidPrefixesSegmented.put("ف,س", Boolean.TRUE);
        hmValidPrefixes.put("س", Boolean.TRUE);
        hmValidPrefixes.put("وس", Boolean.TRUE);
        hmValidPrefixes.put("فس", Boolean.TRUE);
        
//            p.matches("(ات|ون|ين)?,(ه|ها|هما|هم|هن|ك|كما|كم|كن|نا|ي)?")   -- done
//            || p.matches("[ويا],(ه|ها|هما|هم|هن|ك|كما|كم|كن|نا|ي)?")      -- done
//            || p.matches("(ن|ت),(ه|ها|هما|هم|هن|ك|كما|كم|كن|كي|ي)")       -- done
//            || p.matches("(نا),(ه|ها|هما|هم|هن|ك|كما|كم|كن|كي|ي)")        -- done
//            || p.matches("(ون|ين|ات|ان|ا|ي|و|ت|ة|ن|وا)")               -- done
//            || p.matches("(ه|ها|هما|هم|هن|ك|كما|كم|كن|نا|ي)")             -- done
//            || p.matches("(ون|ين|ي|و|ا),(نا|ن),(ه|ها|هما|هم|هن|ك|كما|كم|كن)")
        
        String[] SetA1 = {"", "ات", "ون", "ين", "ان"};
        String[] SetA2 = {"", "ه", "ها", "هما", "هم", "هن", "ك", "كما", "كم", "كن", "نا", "ي"};
        
        for (String s : SetA1)
        {
            for (String ss : SetA2)
            {
                String separator = "";
                if (s.trim().length() > 0 && s.trim().length() > 0)
                    separator = ",";
                hmValidSuffixesSegmented.put(s + separator + ss, Boolean.TRUE);
                hmValidSuffixes.put(s + ss, Boolean.TRUE);
            }
        }
        
        String[] SetB1 = {"ا", "و", "ي"};
        for (String s : SetB1)
        {
            for (String ss : SetA2)
            {
                String separator = "";
                if (s.trim().length() > 0 && s.trim().length() > 0)
                    separator = ",";
                hmValidSuffixesSegmented.put(s + separator + ss, Boolean.TRUE);
                hmValidSuffixes.put(s + ss, Boolean.TRUE);
            }
        }
        
        String[] SetC1 = {"", "ن", "ت", "نا"};
        String[] SetC2 = {"", "ه", "ها", "هما", "هم", "هن", "ك", "كما", "كم", "كن", "كي", "ي"};
        for (String s : SetC1)
        {
            for (String ss : SetC2)
            {
                String separator = "";
                if (s.trim().length() > 0 && s.trim().length() > 0)
                    separator = ",";
                hmValidSuffixesSegmented.put(s + separator + ss, Boolean.TRUE);
                hmValidSuffixes.put(s + ss, Boolean.TRUE);
            }
        }
        
        String[] SetD1 = {"ون", "ين", "ات", "ان", "ا", "ي", "و", "ت", "ة", "ن", "وا", "ي", "و", "ا"};
        for (String s : SetD1)
        {
            hmValidSuffixesSegmented.put(s, Boolean.TRUE);
        }

        String[] setA = {"ا", "و", "ي", "ين", "ون"};
        String[] setB = {"ن", "نا"};
        String[] setC = {"ه", "ها", "هما", "هم", "هن", "ك", "كما", "كم", "كن"};
        for (String a : setA)
        {
            for (String b : setB)
            {
                for (String c : setC)
                {
                    String separator1 = "";
                    String separator2 = "";
                    if (a.length() > 0 && b.length() > 0)
                        separator1 = ",";
                    if (b.length() > 0 && c.length() > 0)
                        separator2 = ",";
                    String suf = a + separator1 + b + separator2 + c;
                    hmValidSuffixesSegmented.put(suf, Boolean.TRUE);
                    hmValidSuffixes.put(suf.replace(",", ""), Boolean.TRUE);
                }
            }
        }
        
        String[] setA3 = {"ت"};
        String[] setB3 = {"", "ا", "ي", "ين", "ان"};
        String[] setC3 = {"", "ه", "ها", "هما", "هم", "هن", "ك", "كما", "كم", "كن"};
        for (String a : setA3)
        {
            for (String b : setB3)
            {
                for (String c : setC3)
                {
                    String separator1 = "";
                    String separator2 = "";
                    if (a.length() > 0 && b.length() > 0)
                        separator1 = ",";
                    if (b.length() > 0 && c.length() > 0)
                        separator2 = ",";
                    String suf = a + separator1 + b + separator2 + c;
                    hmValidSuffixesSegmented.put(suf, Boolean.TRUE);
                    hmValidSuffixes.put(suf.replace(",", ""), Boolean.TRUE);
                }
            }
        }
        hmValidSuffixesSegmented.put("", Boolean.TRUE);
        // add missing suffixes
        hmValidSuffixes.put("تم", Boolean.TRUE);
        hmValidSuffixesSegmented.put("تم", Boolean.TRUE);
        hSuffixes.put("تم", 1);
        hSuffixes.put("ما", 1);
    }
    
    public String getStemTempate(String word)
    {
        return ft.fitTemplate(word);
    }
    
    public String getStemRoot(String word)
    {
        return ft.getRootFitTemplate(word);
    }
}
