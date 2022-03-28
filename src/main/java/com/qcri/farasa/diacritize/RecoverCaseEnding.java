package com.qcri.farasa.diacritize;

import static com.qcri.farasa.diacritize.DiacritizeText.bigramsWithSingleDiacritizations;
import static com.qcri.farasa.diacritize.DiacritizeText.unigramsWithSingleDiacritization;
import static com.qcri.farasa.diacritize.Main.openFileForReading;
import static com.qcri.farasa.diacritize.Main.openFileForWriting;
import com.qcri.farasa.pos.Clitic;
import com.qcri.farasa.pos.FarasaPOSTagger;
import com.qcri.farasa.pos.Sentence;
import com.qcri.farasa.segmenter.ArabicUtils;
import com.qcri.farasa.segmenter.Farasa;
import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author kareemdarwish
 */
public class RecoverCaseEnding
{
    Farasa farasaSegmenter = null;
    FarasaPOSTagger farasaPOS = null;
    DiacritizeText dt = null;
    // public static TMap<String, String> wordsWithSingleDiacritizations = new THashMap<String, String>();
    
    // for SVM Training
    public static HashMap<String, Double> hmDiacritic = new HashMap<String, Double>();
    public static HashMap<String, Double> hmWord = new HashMap<String, Double>(5000000);
    public static HashMap<String, Double> hmPOS = new HashMap<String, Double>();
    public static HashMap<String, Double> hmPrefix = new HashMap<String, Double>();
    public static HashMap<String, Double> hmPrefixPOS = new HashMap<String, Double>();
    public static HashMap<String, Double> hmSuffix = new HashMap<String, Double>();
    public static HashMap<String, Double> hmSuffixPOS = new HashMap<String, Double>();
    public static HashMap<String, Double> hmTemplate = new HashMap<String, Double>();
    public static HashMap<String, Double> hmStem = new HashMap<String, Double>(5000000);
    public static HashMap<String, Double> hmStemPOS = new HashMap<String, Double>();
    public static HashMap<String, Double> hmLastLetter = new HashMap<String, Double>();
    public static HashMap<String, Double> hmGenderNumber = new HashMap<String, Double>();
    public static HashMap<String, Double> hmDiacriticGivenPOS = new HashMap<String, Double>();
    public static HashMap<String, Double> hmDiacriticGivenPrevPOS = new HashMap<String, Double>();
    public static HashMap<String, Double> hmDiacriticGivenNextPOS = new HashMap<String, Double>();
    public static HashMap<String, Double> hmDiacriticGivenWord = new HashMap<String, Double>(5000000);
    public static HashMap<String, Double> hmDiacriticGivenPrevWord = new HashMap<String, Double>(5000000);
    public static HashMap<String, Double> hmDiacriticGivenNextWord = new HashMap<String, Double>(5000000);
    public static HashMap<String, Double> hmDiacriticGivenStem = new HashMap<String, Double>(5000000);
    public static HashMap<String, Double> hmDiacriticGivenPrevStem = new HashMap<String, Double>(5000000);
    public static HashMap<String, Double> hmDiacriticGivenNextStem = new HashMap<String, Double>(5000000);
    public static HashMap<String, Double> hmDiacriticGivenStemPOS = new HashMap<String, Double>();
    public static HashMap<String, Double> hmDiacriticGivenPrevStemPOS = new HashMap<String, Double>();
    public static HashMap<String, Double> hmDiacriticGivenNextStemPOS = new HashMap<String, Double>();
    public static HashMap<String, Double> hmDiacriticGivenGenderNumber = new HashMap<String, Double>();
    public static HashMap<String, Double> hmDiacriticGivenPrevGenderNumber = new HashMap<String, Double>();
    public static HashMap<String, Double> hmDiacriticGivenNextGenderNumber = new HashMap<String, Double>();
    public static HashMap<String, Double> hmDiacriticGivenPrefix = new HashMap<String, Double>();
    public static HashMap<String, Double> hmDiacriticGivenSuffix = new HashMap<String, Double>();
    public static HashMap<String, Double> hmDiacriticGivenPrefixPOS = new HashMap<String, Double>();
    public static HashMap<String, Double> hmDiacriticGivenSuffixPOS = new HashMap<String, Double>();
    public static HashMap<String, Double> hmDiacriticGivenTemplate = new HashMap<String, Double>();
    public static HashMap<String, Double> hmDiacriticGivenLastLetter = new HashMap<String, Double>();
    
    public static HashMap<String, Double> hmCurrentPrevPOSAndPrevDiacritic = new HashMap<String, Double>();
    public static HashMap<String, Double> hmDiacriticGivenCurrentPrevPOSAndPrevDiacritic = new HashMap<String, Double>(5000000);
    
    public static HashMap<String, Double> hmCurrent2PrevPOS = new HashMap<String, Double>(100000);
    public static HashMap<String, Double> hmDiacriticGivenCurrent2PrevPOS = new HashMap<String, Double>(5000000);
    
    public static HashMap<String, Double> hmCurrentPrevWord = new HashMap<String, Double>(1000000);
    public static HashMap<String, Double> hmDiacriticGivenCurrentPrevWord = new HashMap<String, Double>(5000000);
    
        public static HashMap<String, Double> hmCurrentPrevNextPOS = new HashMap<String, Double>(100000);
    public static HashMap<String, Double> hmDiacriticGivenCurrentPrevNextPOS = new HashMap<String, Double>(5000000);
    
    
    public static HashMap<String, Boolean> sukunWords = new HashMap<String, Boolean>();
    public static HashMap<String, Boolean> bareWords = new HashMap<String, Boolean>();
    public static HashMap<String, Boolean> sukunStems = new HashMap<String, Boolean>();
    public static HashMap<String, Boolean> shaddaStems = new HashMap<String, Boolean>();
    public static HashMap<String, Boolean> pastTenseVerbs = new HashMap<String, Boolean>();
    public static HashMap<String, ArrayList<String>> templateDiacriticFull = new HashMap<String, ArrayList<String>>();
    public static HashMap<String, ArrayList<String>> templateDiacriticStem = new HashMap<String, ArrayList<String>>();

    private static final ArrayList<Double> model = new ArrayList<Double>();
    // public static String modelVals = "1:-0.040844474 2:-0.052953929 3:-0.0032482333 4:0.071600951 5:-0.0012433092 6:0.1287304 7:0.045270093 8:-0.0017858743 9:0.043382626 10:-0.039660402 11:-0.041277859 12:-0.017437568 13:-0.024551863 14:-0.024105994 15:-0.023908934 16:-0.016090138 17:-0.020217065 18:-0.017220551 19:-0.022402365 20:-0.025735673 21:-0.025581017 22:0.038178787 23:0.22242694 24:0.11088069 25:0.11440907";
    public static String modelVals = "1:-0.0045846282 2:-0.0019523823 3:-0.0019520306 4:0.019709324 5:0.013574635 6:0.013919395 7:0.010214916 8:0.006256598 9:0.0039817486 10:-0.0050665005 11:-0.0025337148 12:-0.0036718016 13:-0.0029112925 14:-0.0035375676 15:-0.004742207 16:-0.0038544629 17:-0.004510941 18:-0.0040450627 19:-0.0047958791 20:-0.0037260016 21:-0.0033097605 22:0.0078869415 23:0.11973543 24:0.011616438 25:0.011713957";
    public RecoverCaseEnding(DiacritizeText diacritizer, String dataDir) throws IOException, ClassNotFoundException
    {
        dt = diacritizer;
        farasaSegmenter = DiacritizeText.farasaSegmenter;
        farasaPOS = DiacritizeText.farasaPOSTagger;
        for (String s : modelVals.split(" +")) {
            model.add(Double.parseDouble(s.substring(s.indexOf(":") + 1)));
        }
        deserializeDataStructures();
        
//        BufferedReader br = openFileForReading("c:/RESEARCH/FromMac/work/CLASSIC/DIACRITIZE/NEW-RDI/all-text.txt.tok.diacTemplates.full");
//        String line = "";
//        while ((line = br.readLine()) != null)
//        {
//            String[] parts = line.split("\t");
//            if (parts.length >= 2)
//            {
//                String template = parts[0];
//                String[] diacs = parts[1].trim().split(" +");
//                
//                // get totals first
//                double total = 0;
//                for (String s : diacs)
//                {
//                    total += Double.parseDouble(s.replaceFirst(".*_", ""));
//                }
//                String templateNoDiacritics = template.replaceAll("[aiouNKF~]+", "");
//                if (bestDiacritizedTemplateFull.containsKey(templateNoDiacritics))
//                {
//                    double d = Double.parseDouble(bestDiacritizedTemplateFull.get(templateNoDiacritics).split(" +")[1]);
//                    if (total > d)
//                        bestDiacritizedTemplateFull.put(templateNoDiacritics, template + " " + String.valueOf(total));
//                }
//                else
//                {
//                    bestDiacritizedTemplateFull.put(templateNoDiacritics, template + " " + String.valueOf(total));
//                }
//                if (total > 1000)
//                {
//                    if (!templateDiacriticFull.containsKey(template))
//                        templateDiacriticFull.put(template, new ArrayList<String>());
//                    for (String s : diacs)
//                    {
//                        String diac = s.substring(0, s.indexOf("_"));
//                        double count = Double.parseDouble(s.substring(s.indexOf("_") + 1));
//                        if (count/total > 0.001)
//                            templateDiacriticFull.get(template).add(diac);
//                    }
//                }
//            }
//        }
//        serializeMap("c:/RESEARCH/FromMac/work/CLASSIC/DIACRITIZE/NEW-RDI/", "templateDiacriticFull", templateDiacriticFull);
//        serializeMap("c:/RESEARCH/FromMac/work/CLASSIC/DIACRITIZE/NEW-RDI/", "bestDiacritizedTemplateFull", bestDiacritizedTemplateFull);
//        br = openFileForReading("c:/RESEARCH/FromMac/work/CLASSIC/DIACRITIZE/NEW-RDI/all-text.txt.tok.diacTemplates.stem");
//        line = "";
//        while ((line = br.readLine()) != null)
//        {
//            String[] parts = line.split("\t");
//            if (parts.length >= 2)
//            {
//                String template = parts[0];
//                String[] diacs = parts[1].trim().split(" +");
//                
//                // get totals first
//                double total = 0;
//                for (String s : diacs)
//                {
//                    total += Double.parseDouble(s.replaceFirst(".*_", ""));
//                }
//                String templateNoDiacritics = template.replaceAll("[aiouNKF~]+", "");
//                if (bestDiacritizedTemplateStem.containsKey(templateNoDiacritics))
//                {
//                    double d = Double.parseDouble(bestDiacritizedTemplateStem.get(templateNoDiacritics).split(" +")[1]);
//                    if (total > d)
//                        bestDiacritizedTemplateStem.put(templateNoDiacritics, template + " " + String.valueOf(total));
//                }
//                else
//                {
//                    bestDiacritizedTemplateStem.put(templateNoDiacritics, template + " " + String.valueOf(total));
//                }
//
//                if (total > 20)
//                {
//                    if (!templateDiacriticStem.containsKey(template))
//                        templateDiacriticStem.put(template, new ArrayList<String>());
//                    for (String s : diacs)
//                    {
//                        String diac = s.substring(0, s.indexOf("_"));
//                        double count = Double.parseDouble(s.substring(s.indexOf("_") + 1));
//                        if (count/total > 0.001)
//                            templateDiacriticStem.get(template).add(diac);
//                    }
//                }
//            }
//        }
//        serializeMap("c:/RESEARCH/FromMac/work/CLASSIC/DIACRITIZE/NEW-RDI/", "templateDiacriticStem", templateDiacriticStem);
//        serializeMap("c:/RESEARCH/FromMac/work/CLASSIC/DIACRITIZE/NEW-RDI/", "bestDiacritizedTemplateStem", bestDiacritizedTemplateStem);
    }
    
    public SentenceClass createCaseEndingTrainingDataOld(String line) throws Exception
    {
        SentenceClass sc = new SentenceClass();
        ArrayList<String> segmentedWords = farasaSegmenter.segmentLine(line);
        // ArrayList<String> head = new ArrayList<String>();
        Sentence sentence = farasaPOS.tagLine(segmentedWords);
        
        // FIXME: Hamdy, 2016-05-01
        Clitic cl;
        String s = "", out, root;
        for (int i = 0; i < sentence.clitics.size(); i++)
        {
            cl = sentence.clitics.get(i);
            if (cl.surface.equals("S") || cl.surface.equals("E"))
            {
                continue;
            }
            
            root =  "Y";
            if (!cl.template.equals("Y"))
            {
                root = farasaSegmenter.getStemRoot(cl.surface);
                //root = ArabicUtils.buck2morph(root);
            }
            // s += String.format("%s/%s/%s/%s+", cl.surface, cl.template, cl.guessPOS, root);
        }
        // s = s.replace("++", "+");
        // out = String.format("%s\t%s\r\n", line, s);
        // System.out.print(out);

        int pos = 0;
        /*
            word
            stem 
            stemPOS 
            prefix
            prefixPOS 
            suffix
            suffixPOS
            Suff
            firstLetter
            lastLetter 
            template
            genderNumberTag
            diacritic
        */
        String word = ""; String wordPOS = ""; String stem = ""; String stemPOS = "Y"; String prefix = ""; String prefixPOS = ""; String suffix = ""; String suffixPOS = "";
        String firstLetter = "#"; String lastLetter = "#"; String stemTemplate = "Y"; String lastVerb = "VerbNotSeen"; String genderNumber = "Y";
        for (int i = 0; i < sentence.clitics.size(); i++)
        {
            Clitic clitic = sentence.clitics.get(i);
            if (clitic.position.equals("B") && word.length() > 0 && !word.equals("S") && !word.equals("E"))
            {
                // empty existing aggregators and reset
                if (prefix.trim().length() == 0)
                {
                    prefix = "noPrefixFound";
                    prefixPOS = "Y";
                }
                if (suffix.trim().length() == 0)
                {
                    suffix = "noSuffixFound";
                    suffixPOS = "Y";
                }
                if (stem.trim().length() == 0)
                    stem = "#";
                
                word = word.replace("++", "+");
                stem = stem.replace("++", "+");
                
                if (genderNumber.trim().length() == 0)
                    genderNumber = "#";
                sc.addWord(new WordClass(word, stem, wordPOS, stemPOS, stemTemplate, pos, genderNumber, prefix, prefixPOS, suffix, suffixPOS, lastVerb));
                word = ""; wordPOS = ""; stem = ""; stemPOS = "Y"; prefix = ""; prefixPOS = ""; suffix = ""; suffixPOS = "";
                firstLetter = "#"; lastLetter = "#"; stemTemplate = "Y"; genderNumber = "Y";
                pos++;
            }

            if (clitic.surface.startsWith("+"))
            {
                if (clitic.guessPOS.equals("NSUFF")
                        ||
                        (sentence.clitics.size() > i + 1 && sentence.clitics.get(i+1).guessPOS.equals("NSUFF") && sentence.clitics.get(i+1).position.equals("I"))
                        ) // attach to stem
                {
                    if (stem.length() > 0)
                        stemPOS += "+";
                    stem += clitic.surface;
                    stemPOS += clitic.guessPOS;
                }
                else
                {
                    suffix += clitic.surface;
                    if (suffixPOS.length() > 0)
                        suffixPOS += "+";
                    suffixPOS += clitic.guessPOS;
                }
            }
            else if (clitic.surface.endsWith("+"))
            {
                prefix += clitic.surface;
                if (prefixPOS.length() > 0)
                    prefixPOS += "+";
                prefixPOS += clitic.guessPOS;
            }
            else if (!clitic.surface.equals("S"))
            {
                genderNumber = clitic.genderNumber;
                if (!stem.equals("#"))
                    stem = clitic.surface;
                stemPOS = clitic.guessPOS;
                if (clitic.guessPOS.equals("V"))
                    lastVerb = clitic.surface;
                stemTemplate = clitic.template;
                firstLetter = clitic.surface.substring(0,1);
                lastLetter = clitic.surface.substring(clitic.surface.length() - 1);
            }
            if (!clitic.surface.equals("S") && !clitic.surface.equals("E"))
            {
                word += clitic.surface;
                if (wordPOS.length() > 0)
                    wordPOS += "+";
                wordPOS += clitic.guessPOS;
            }
        }
        if (word.trim().length() > 0 && !word.equals("S") && !word.equals("E"))
        {
            if (prefix.trim().length() == 0)
                {
                    prefix = "#";
                    prefixPOS = "Y";
                }
                if (suffix.trim().length() == 0)
                {
                    suffix = "#";
                    suffixPOS = "Y";
                }
                
                sc.addWord(new WordClass(word, stem, wordPOS, stemPOS, stemTemplate, pos, genderNumber, prefix, prefixPOS, suffix, suffixPOS, lastVerb));
        }
        sc.addWord(new WordClass("E", "E", "E", "E", "Y", 0, "Y", "#", "#", "#", "#", "#"));
        return sc;
    }
    
    public SentenceClass createCaseEndingTrainingData(String line) throws Exception
    {
        SentenceClass sc = new SentenceClass();
        ArrayList<String> segmentedWords = farasaSegmenter.segmentLine(line);
        // ArrayList<String> head = new ArrayList<String>();
        Sentence sentence = farasaPOS.tagLine(segmentedWords);
        
        // FIXME: Hamdy, 2016-05-01
        Clitic cl;
        String s = "", out, root;
        for (int i = 0; i < sentence.clitics.size(); i++)
        {
            cl = sentence.clitics.get(i);
            if (cl.surface.equals("S") || cl.surface.equals("E"))
            {
                continue;
            }
            
            root =  "Y";
            if (!cl.template.equals("Y"))
            {
                root = farasaSegmenter.getStemRoot(cl.surface);
                //root = ArabicUtils.buck2morph(root);
            }
            // s += String.format("%s/%s/%s/%s+", cl.surface, cl.template, cl.guessPOS, root);
        }
        // s = s.replace("++", "+");
        // out = String.format("%s\t%s\r\n", line, s);
        // System.out.print(out);

        int pos = 0;
        /*
            word
            stem 
            stemPOS 
            prefix
            prefixPOS 
            suffix
            suffixPOS
            Suff
            firstLetter
            lastLetter 
            template
            genderNumberTag
            diacritic
        */
        String word = ""; String wordPOS = ""; String stem = ""; String stemPOS = "Y"; String prefix = ""; String prefixPOS = ""; String suffix = ""; String suffixPOS = "";
        String firstLetter = "#"; String lastLetter = "#"; String stemTemplate = "Y"; String lastVerb = "VerbNotSeen"; String genderNumber = "Y";
        for (int i = 0; i < sentence.clitics.size(); i++)
        {
            Clitic clitic = sentence.clitics.get(i);
            if (clitic.position.equals("B") && word.length() > 0 && !word.equals("S") && !word.equals("E"))
            {
                // empty existing aggregators and reset
                if (prefix.trim().length() == 0)
                {
                    prefix = "noPrefixFound";
                    prefixPOS = "Y";
                }
                if (suffix.trim().length() == 0)
                {
                    suffix = "noSuffixFound";
                    suffixPOS = "Y";
                }
                if (stem.trim().length() == 0)
                    stem = "#";
                
                word = word.replace("++", "+");
                stem = stem.replace("++", "+");
                
                if (genderNumber.trim().length() == 0)
                    genderNumber = "#";
//                if (stemTemplate.contains("+"))
//                {
//                    String tmp = "";
//                    String[] STParts = stemTemplate.split("\\+");
//                    for (String st : STParts)
//                    {
//                        if (st.contains("f") || st.contains("E") || st.contains("l"))
//                            stemTemplate = st;
//                    }
//                }
//                WordClass tmpWC = transformFromFullWordPOS2CliticLevelPOS(new WordClass(word, stem, wordPOS, stemPOS, stemTemplate, pos, genderNumber, prefix, prefixPOS, suffix, suffixPOS, lastVerb));
//                System.err.println(word + "\t" + tmpWC.word + "\t" + 
//                        stem + "\t" + tmpWC.stem + "\t" + 
//                        wordPOS + "\t" + tmpWC.POS + "\t" + 
//                        stemPOS + "\t" + tmpWC.stemPOS + "\t" + 
//                        stemTemplate + "\t" + tmpWC.stemTemplate + "\t" + 
//                        pos + "\t" + tmpWC.position + "\t" + 
//                        genderNumber + "\t" + tmpWC.genderNumber + "\t" + 
//                        prefix + "\t" + tmpWC.prefix + "\t" + 
//                        prefixPOS + "\t" + tmpWC.prefixPOS + "\t" + 
//                        suffix + "\t" + tmpWC.suffix + "\t" + 
//                        suffixPOS + "\t" + tmpWC.suffixPOS + "\t" + 
//                        lastVerb + "\t" + tmpWC.lastVerb);
                sc.addWord(transformFromFullWordPOS2CliticLevelPOS(new WordClass(word, stem, wordPOS, stemPOS, stemTemplate, pos, genderNumber, prefix, prefixPOS, suffix, suffixPOS, lastVerb)));
                // sc.addWord((new WordClass(word, stem, wordPOS, stemPOS, stemTemplate, pos, genderNumber, prefix, prefixPOS, suffix, suffixPOS, lastVerb)));
                word = ""; wordPOS = ""; stem = ""; stemPOS = "Y"; prefix = ""; prefixPOS = ""; suffix = ""; suffixPOS = "";
                firstLetter = "#"; lastLetter = "#"; stemTemplate = "Y"; genderNumber = "Y";
                pos++;
            }

            if (clitic.surface.startsWith("+"))
            {
                if (clitic.guessPOS.equals("NSUFF")
                        ||
                        (sentence.clitics.size() > i + 1 && sentence.clitics.get(i+1).guessPOS.equals("NSUFF") && sentence.clitics.get(i+1).position.equals("I"))
                        ) // attach to stem
                {
                    if (stem.length() > 0)
                        stemPOS += "+";
                    stem += clitic.surface;
                    stemPOS += clitic.guessPOS;
                }
                else
                {
                    suffix += clitic.surface;
                    if (suffixPOS.length() > 0)
                        suffixPOS += "+";
                    suffixPOS += clitic.guessPOS;
                }
            }
            else if (clitic.surface.endsWith("+"))
            {
                prefix += clitic.surface;
                if (prefixPOS.length() > 0)
                    prefixPOS += "+";
                prefixPOS += clitic.guessPOS;
            }
            else if (!clitic.surface.equals("S"))
            {
                genderNumber = clitic.genderNumber;
                if (!stem.equals("#"))
                    stem = clitic.surface;
                stemPOS = clitic.guessPOS;
                if (clitic.guessPOS.equals("V"))
                    lastVerb = clitic.surface;
                stemTemplate = clitic.template;
//                if (stemTemplate.contains("+"))
//                {
//                    String tmp = "";
//                    String[] STParts = stemTemplate.split("\\+");
//                    for (String st : STParts)
//                    {
//                        if (st.contains("f") || st.contains("E") || st.contains("l"))
//                            stemTemplate = st;
//                    }
//                }
                firstLetter = clitic.surface.substring(0,1);
                lastLetter = clitic.surface.substring(clitic.surface.length() - 1);
            }
            if (!clitic.surface.equals("S") && !clitic.surface.equals("E"))
            {
                word += clitic.surface;
                if (wordPOS.length() > 0)
                    wordPOS += "+";
                wordPOS += clitic.guessPOS;
            }
        }
        if (word.trim().length() > 0 && !word.equals("S") && !word.equals("E"))
        {
            if (prefix.trim().length() == 0)
                {
                    prefix = "#";
                    prefixPOS = "Y";
                }
                if (suffix.trim().length() == 0)
                {
                    suffix = "#";
                    suffixPOS = "Y";
                }
                // System.err.println(word.replace(" ", "") + "\t" + stem.replace(" ", "") + "\t" +wordPOS + "\t" +stemPOS + "\t" +stemTemplate + "\t" +pos + "\t" +genderNumber + "\t" +prefix + "\t" +prefixPOS + "\t" +suffix + "\t" +suffixPOS + "\t" +lastVerb);
                sc.addWord(transformFromFullWordPOS2CliticLevelPOS(new WordClass(word, stem, wordPOS, stemPOS, stemTemplate, pos, genderNumber, prefix, prefixPOS, suffix, suffixPOS, lastVerb)));
        }
        sc.addWord(new WordClass("E", "E", "E", "E", "Y", 0, "Y", "#", "#", "#", "#", "#"));
        return sc;
    }
    
    public WordClass transformFromFullWordPOS2CliticLevelPOS(WordClass word)
    {
        word.word = word.word.replace(" ", "").trim();
        String[] w = (" " + farasaSegmenter.getProperSegmentation(word.word) + " ").split(";");
        String[] pos = word.POS.split("\\+");
        String pre = w[0].trim();
        if (pre.endsWith("+"))
            pre = pre.substring(0, pre.length() - 1);
        String suf = w[2].trim();
        if (suf.startsWith("+"))
            suf = suf.substring(1);
        String stem = w[1].trim();
        int preLen = 0;
        if (pre.length() > 0)
            preLen = pre.split("\\+").length;
        if (preLen == 0)
        {
            word.prefix = "noPrefixFound";
            word.prefixPOS = "Y";
        }
        else
        {
            word.prefix = pre + "+";
            String prePOS = "";
            for (int i = 0; i < preLen; i++)
                prePOS += pos[i] + "+";
            word.prefixPOS = prePOS;
        }
        int stemLen = 0;
        if (stem.length() > 0)
            stemLen = 1;
        if (stemLen == 0)
        {
            word.stem = "#";
            word.stemPOS = "Y";
            word.stemTemplate = "Y";
        }
        else
        {
            word.stem = stem;
            word.stemPOS = pos[preLen];
            if (word.stemTemplate.contains("+"))
                {
                    String tmp = "";
                    String[] STParts = word.stemTemplate.split("\\+");
                    for (String st : STParts)
                    {
                        if (st.contains("f") || st.contains("E") || st.contains("l"))
                            word.stemTemplate = st;
                    }
                }
        }
        
        int sufLen = 0;
        if (suf.length() > 0)
            sufLen = suf.split("\\+").length;
        if (sufLen == 0)
        {
            word.suffix = "noSuffixFound";
            word.suffixPOS = "Y";
        }
        else
        {
            // check if NSUFF exists
            while (pos.length > preLen+stemLen && pos[preLen+stemLen].equals("NSUFF"))
            {
                word.stem += "+" + suf.split("\\+")[0];
                word.stemPOS += "+NSUFF";
                sufLen--;
                stemLen++;
                if (suf.contains("+"))
                    suf = suf.substring(suf.indexOf("+") + 1);
                else
                    suf = "";
                if (sufLen == 0)
                {
                    word.suffix = "noSuffixFound";
                    word.suffixPOS = "Y";
                }
                else
                {
                    word.suffix = "+" + suf;
                    String suffixPOS = "";
                    for (int j = preLen + stemLen; j < pos.length; j++)
                        suffixPOS += "+" + pos[j];
                    word.suffixPOS = suffixPOS;
                }
            }
        }
        if (word.prefixPOS.endsWith("+"))
            word.prefixPOS = word.prefixPOS.substring(0, word.prefixPOS.length() - 1);
        if (word.suffixPOS.startsWith("+"))
            word.suffixPOS = word.suffixPOS.substring(1);
        
        return word;
    }
    
    public SentenceClass putCaseEnding(String line) throws Exception
    {
        line = line.replace("+", "XplusY");
        line = line.replace(";", "XsemicolonY");
        line = line.replace("-", "XdashY");
        line = line.replace("_", "XunderscoreY");
        SentenceClass sentence = createCaseEndingTrainingData(line);
        ArrayList<String> words = ArabicUtils.tokenizeWithoutProcessing(line);
        // diacritized word in to sentence
        if (sentence.words.size() == words.size() + 2)
        {
            for (int i = 1; i < sentence.words.size() - 1; i++)
            {
                sentence.words.get(i).wordDiacritizedWOCase = words.get(i-1);
            }
            sentence = guessCaseEnding(sentence);
            sentence = putCaseEnding(sentence);
            for (int i = 1; i < sentence.words.size() - 1; i++)
            {
                if (sentence.words.get(i).word.equals("XdashY"))
                {
                    sentence.words.get(i).word = "-";
                    sentence.words.get(i).wordDiacritizedWOCase = "-";
                    sentence.words.get(i).wordFullyDiacritized = "-";
                }
                else if (sentence.words.get(i).word.equals("XsemicolonY"))
                {
                    sentence.words.get(i).word = ";";
                    sentence.words.get(i).wordDiacritizedWOCase = ";";
                    sentence.words.get(i).wordFullyDiacritized = ";";
                }
                else if (sentence.words.get(i).word.equals("XplusY"))
                {
                    sentence.words.get(i).word = "+";
                    sentence.words.get(i).wordDiacritizedWOCase = "+";
                    sentence.words.get(i).wordFullyDiacritized = "+";
                }
                else if (sentence.words.get(i).word.equals("XunderscoreY"))
                {
                    sentence.words.get(i).word = "_";
                    sentence.words.get(i).wordDiacritizedWOCase = "_";
                    sentence.words.get(i).wordFullyDiacritized = "_";
                }
            }
        }
        else
        {
            // System.err.println("ERROR:" + line);
        }
        
        return sentence;
    }
        
    private String getWordStem(String word)
    {
        String s ="";
        try {
            s = farasaSegmenter.segmentLine(word).get(0);
        } catch (IOException ex) {
                    Logger.getLogger(RecoverCaseEnding.class.getName()).log(Level.SEVERE, null, ex);
        }
        s = farasaSegmenter.getProperSegmentation(s);
        return (" " + s + " ").split(" +")[1].trim();
    }
    
    public ArrayList<String> createCaseEndingCRFInput(ArrayList<String> lines) throws Exception
    {
        ArrayList<String> output = new ArrayList<String>();
       
        /*
            word
            stem 
            stemPOS 
            prefix
            prefixPOS 
            suffix
            suffixPOS
            Suff
            firstLetter
            lastLetter 
            template
            genderNumberTag
            diacritic
        */
        String lastSeenVerb = "";
        int lastSeenVerbLoc = -1;
        int currentPosInSentence = 0;
        
        ArrayList<String> wordParts = new ArrayList<String>();
        for (int i = 0; i < lines.size(); i++)
        {
            if (lines.get(i).trim().equals("-"))
            {
                if (wordParts.size() > 0)
                {
                    currentPosInSentence++;
                    // get word
                    String word = "";
                    for (String s : wordParts)
                    {
                        if (word.length() > 0)
                        {
                            word += "+";
                        }
                        word += s.replaceFirst("\\/.*", "");
                    }
                    // get stem
                    // if (word.startsWith("ب"))
                    //    System.err.println();
                    String PrefixStemSuffix = getWordStem(word);
                    if (!PrefixStemSuffix.contains(";"))
                        PrefixStemSuffix = "#;" + PrefixStemSuffix + ";#";
                    if (PrefixStemSuffix.endsWith(";"))
                        PrefixStemSuffix = PrefixStemSuffix + "#";
                    if (PrefixStemSuffix.startsWith(";"))
                        PrefixStemSuffix = "#" + PrefixStemSuffix;
                    if (PrefixStemSuffix.contains(";;"))
                        PrefixStemSuffix = PrefixStemSuffix.replace(";;", ";#;");

                    String stem = PrefixStemSuffix.substring(PrefixStemSuffix.indexOf(";") + 1, 
                            PrefixStemSuffix.lastIndexOf(";")).trim(); // .replaceFirst(".*?;", "").replaceFirst(";.*?", "");
                    String template = "Y"; // template of verbs only
                    // get stemPOS
                    String stemPOS = "";
                    // get genderNumber
                    String genderNumberTag = "O";
                    if (stem.equals("#"))
                    {
                        stemPOS = "Y";
                    }
                    else
                    {
                        for (String s : wordParts)
                        {
                            if (s.startsWith(stem + "/"))
                            {
                                stemPOS = s.replaceFirst(".*\\/", "").trim();
                                if (stemPOS.contains("-"))
                                {
                                    genderNumberTag = stemPOS.substring(stemPOS.indexOf("-") + 1).trim();
                                    stemPOS = stemPOS.substring(0, stemPOS.indexOf("-")).trim();
                                }
                                if (stemPOS.trim().equals("V") || stemPOS.trim().startsWith("V+"))
                                {
                                    template = s.substring(s.indexOf("/") + 1, s.lastIndexOf("/"));
                                }
                            }
                        }
                    }

                    // attach if NSUFF to stem and stemPOS
                    // get prefix and suffix POS
                    String prefixPOS = "";
                    String suffixPOS = "";
                    // get prefix & suffix
                    String prefix = ""; // PrefixStemSuffix.replaceFirst(";.*", "");
                    String suffix = ""; // PrefixStemSuffix.replaceFirst(".*;", "");
                    
                    // add verb feature
                    String lastVerb = "VerbNotSeen";
                    if (stemPOS.equals("V"))
                    {
                        // this is a verb
                        lastSeenVerb = stem;
                        lastSeenVerbLoc = currentPosInSentence;
                    }
                    else if (lastSeenVerbLoc != -1 && currentPosInSentence - lastSeenVerbLoc < 7)
                    {
                        lastVerb = lastSeenVerb;
                    }
                    
                    int positionInPrefixStemSuffix = 0;
                    for (String ss : wordParts)
                    {
                        if (ss.startsWith(stem + "/"))
                        {
                            positionInPrefixStemSuffix = 2;
                        } 
                        else if (ss.contains("NSUFF"))
                        {
                            stem += "+" + ss.replaceFirst("\\/.*", "").trim();
                            stemPOS += "+NSUFF";
                        }
                        else if (positionInPrefixStemSuffix == 0)
                        {
                            prefixPOS += ss.replaceFirst(".*\\/", "").trim() + "+";
                            prefix += ss.substring(0, ss.indexOf("/")).trim() + "+";
                        }
                        else if (positionInPrefixStemSuffix == 2)
                        {
                            suffixPOS += "+" + ss.replaceFirst(".*\\/", "").trim();
                            suffix += "+" + ss.substring(0, ss.indexOf("/")).trim();
                        }
                    }



                    // correct prefix and suffix if empty

                    if (prefix.contains("#") || prefix.isEmpty())
                    {
                        prefixPOS = "Y";
                        prefix = "#";
                    }
                    if (suffix.contains("#") || suffix.isEmpty())
                    {
                        suffixPOS = "Y";
                        suffix = "#";
                    }

                    String Suff = "#"; // if word has NSUFF -- show surface form
                    if (stem.contains("+"))
                        Suff = stem.substring(stem.indexOf("+"));

                    // first and last letter
                    String firstLetter = stem.substring(0, 1);
                    String lastLetter = stem.substring(stem.length() - 1);

                    output.add(word + "\t" + stem + "\t" + stemPOS + "\t" + 
                                prefix + "\t" + prefixPOS + "\t" + 
                                suffix + "\t" + suffixPOS + "\t" + 
                                Suff + "\t" + firstLetter + "\t" + lastLetter + "\t" + 
                                template + "\t" + genderNumberTag + "\t" + lastVerb);
                    wordParts.clear();
                }
            }
            else
            {
                wordParts.add(lines.get(i));
            }
        }
        
        return output;
    }

    private double logWithBase(double x, double base) {
        return Math.log(x) / Math.log(base);
    }
    
    private double getFeatureValueSmoothed(HashMap<String, Double> map, HashMap<String, Double> mapNorm, String word, String tag, double maxVal)
    {
        // get total seen count
        double score = -10d;
        if (map.containsKey(word + "\t" + tag))
        {
            score = map.get(word + "\t" + tag);
            if (mapNorm.get(word) > maxVal)
            {
                return score;
            }
            else
            {
                score = logWithBase(mapNorm.get(word)/maxVal, 10) + score;
                if (score < -10)
                    score = -10;
                return score;
            }
            
        }
        return -10d;
    }
    
    private String getDiacriticFromWordOld(String word, String suffix)
    {
        String output = word;
        for (int i = suffix.length() - 1; i >= 0; i--)
        {
            int pos = output.lastIndexOf(suffix.substring(i, i+1));
            //if (pos == -1)
            //    System.err.println(word + "\t" + suffix);
            output = output.substring(0, pos);
        }
        String diacritics = "";
        while (output.length() > 0 && output.substring(output.length() - 1).matches("[" + ArabicUtils.buck2utf8("aiou~NKF") + "]+"))
        {
            diacritics = output.substring(output.length() - 1) + diacritics;
            output = output.substring(0, output.length() - 1);
        }
        return diacritics;
    }
    
        private String getDiacriticFromWord(String word, String suffix)
    {
        String output = word;
        for (int i = suffix.length() - 1; i >= 0; i--)
        {
            int pos = output.lastIndexOf(suffix.substring(i, i+1));
            //if (pos == -1)
            //    System.err.println(word + "\t" + suffix);
            if (pos >= 0)
                // System.err.println(word + "\t" + suffix);
                output = output.substring(0, pos);
        }
        String diacritics = "";
        while (output.length() > 0 && output.substring(output.length() - 1).matches("[" + ArabicUtils.buck2utf8("aiou~NKF") + "]+"))
        {
            diacritics = output.substring(output.length() - 1) + diacritics;
            output = output.substring(0, output.length() - 1);
        }
        return diacritics;
    }
    
    private String getDiacritizedFormWithoutCaseEnding(String word, String suffix)
    {
        String output = word;
        for (int i = suffix.length() - 1; i >= 0; i--)
        {
            int pos = output.lastIndexOf(suffix.substring(i, i+1));
            //if (pos == -1)
            //    System.err.println(word + "\t" + suffix);
            if (pos >= 0)
                // System.err.println(word + "\t" + suffix);
                output = output.substring(0, pos);
        }
        String diacritics = "";
        while (output.length() > 0 && output.substring(output.length() - 1).matches("[" + ArabicUtils.buck2utf8("aiou~NKF") + "]+"))
        {
            // diacritics = output.substring(output.length() - 1) + diacritics;
            output = output.substring(0, output.length() - 1);
        }
        return output;
    }
    
    public void train(String filename) throws Exception
    {
        BufferedReader br = openFileForReading(filename);
        BufferedWriter bw = openFileForWriting(filename + ".case");

        String line = "";
        long wordCount = 0;
        while ((line = br.readLine()) != null)
        {
            if (line.trim().length() > 0)
            {
                line = line.replace("+", "plus");
                line = line.replace(";", "semicolon");
                SentenceClass training = createCaseEndingTrainingData(line);
                ArrayList<String> words = ArabicUtils.tokenizeWithoutProcessing(line);
                if (training.words.size() == words.size() + 2)
                {
                    for (int i = 0; i < training.words.size(); i++)
                    {
                        wordCount++;
                        if (String.valueOf(wordCount).endsWith("0000"))
                            System.err.println(wordCount);
                        WordClass tWord = training.words.get(i);
                        if (tWord.POS.equals("S") || tWord.POS.equals("E"))
                        {
                            incrementValGivenKey(hmWord, tWord.word, 1);
                            incrementValGivenKey(hmPOS, tWord.POS, 1);
                            incrementValGivenKey(hmStem, tWord.stem, 1);
                            incrementValGivenKey(hmStemPOS, tWord.stemPOS, 1);
                            incrementValGivenKey(hmTemplate, tWord.stemTemplate, 1);
                            incrementValGivenKey(hmPrefix, tWord.prefix, 1);
                            incrementValGivenKey(hmPrefixPOS, tWord.prefixPOS, 1);
                            incrementValGivenKey(hmSuffix, tWord.suffix, 1);
                            incrementValGivenKey(hmSuffixPOS, tWord.suffixPOS, 1);
                            incrementValGivenKey(hmGenderNumber, tWord.genderNumber, 1);
                            incrementValGivenKey(hmLastLetter, tWord.stem.substring(tWord.stem.length() - 1), 1);
                        }
                        else // if (!tWord.POS.equals("S") && !tWord.POS.equals("E"))
                        {
                            String word = tWord.word;
                            String stem = tWord.stem;
                            String diacritic = "";
                            if (stem.equals("#"))
                                diacritic = "#";
                            else
                            {
                                String suffix = tWord.suffix;
                                if (suffix == "noSuffixFound")
                                    suffix = "";
                                diacritic = ArabicUtils.utf82buck(getDiacriticFromWord(words.get(i-1), suffix.replace("+", "")));
                                if (diacritic.trim().length() == 0)
                                    diacritic = "#";
                                tWord.guessDiacritic = diacritic;
                                tWord.truthDiacritic = diacritic;
                            }
                            incrementValGivenKey(hmDiacritic, diacritic, 1);
                            incrementValGivenKey(hmWord, tWord.word, 1);
                            incrementValGivenKey(hmPOS, tWord.POS, 1);
                            incrementValGivenKey(hmStem, tWord.stem, 1);
                            incrementValGivenKey(hmStemPOS, tWord.stemPOS, 1);
                            incrementValGivenKey(hmTemplate, tWord.stemTemplate, 1);
                            incrementValGivenKey(hmPrefix, tWord.prefix, 1);
                            incrementValGivenKey(hmPrefixPOS, tWord.prefixPOS, 1);
                            incrementValGivenKey(hmSuffix, tWord.suffix, 1);
                            incrementValGivenKey(hmSuffixPOS, tWord.suffixPOS, 1);
                            incrementValGivenKey(hmGenderNumber, tWord.genderNumber, 1);
                            incrementValGivenKey(hmLastLetter, tWord.stem.substring(tWord.stem.length() - 1), 1);

                            String tD = "\t" + diacritic;

                            WordClass tPWord = training.words.get(i-1);
                            WordClass tNWord = training.words.get(i+1);

                            incrementValGivenKey(hmDiacriticGivenPOS, tWord.POS + tD, 1);
                            incrementValGivenKey(hmDiacriticGivenPrevPOS, tPWord.POS + tD, 1);
                            incrementValGivenKey(hmDiacriticGivenNextPOS, tNWord.POS + tD, 1);
                            incrementValGivenKey(hmDiacriticGivenWord, tWord.word + tD, 1);
                            incrementValGivenKey(hmDiacriticGivenPrevWord, tPWord.word + tD, 1);
                            incrementValGivenKey(hmDiacriticGivenNextWord, tNWord.word + tD, 1);
                            incrementValGivenKey(hmDiacriticGivenStem, tWord.stem + tD, 1);
                            incrementValGivenKey(hmDiacriticGivenPrevStem, tPWord.stem + tD, 1);
                            incrementValGivenKey(hmDiacriticGivenNextStem, tNWord.stem + tD, 1);
                            incrementValGivenKey(hmDiacriticGivenStemPOS, tWord.stemPOS + tD, 1);
                            incrementValGivenKey(hmDiacriticGivenPrevStemPOS, tPWord.stemPOS + tD, 1);
                            incrementValGivenKey(hmDiacriticGivenNextStemPOS, tNWord.stemPOS + tD, 1);
                            incrementValGivenKey(hmDiacriticGivenGenderNumber, tWord.genderNumber + tD, 1);
                            incrementValGivenKey(hmDiacriticGivenPrevGenderNumber, tPWord.genderNumber  + tD, 1);
                            incrementValGivenKey(hmDiacriticGivenNextGenderNumber, tNWord.genderNumber + tD, 1);
                            incrementValGivenKey(hmDiacriticGivenPrefix, tWord.prefix + tD, 1);
                            incrementValGivenKey(hmDiacriticGivenSuffix, tWord.suffix + tD, 1);
                            incrementValGivenKey(hmDiacriticGivenPrefixPOS, tWord.prefixPOS + tD, 1);
                            incrementValGivenKey(hmDiacriticGivenSuffixPOS, tWord.suffixPOS + tD, 1);
                            incrementValGivenKey(hmDiacriticGivenTemplate, tWord.stemTemplate + tD, 1);
                            incrementValGivenKey(hmDiacriticGivenLastLetter, tWord.stem.substring(tWord.stem.length() - 1) + tD, 1);

                            String tKey = tPWord.guessDiacritic + " " + tPWord.POS+ " " + tWord.POS;
                            incrementValGivenKey(hmCurrentPrevPOSAndPrevDiacritic, tKey, 1d);
                            incrementValGivenKey(hmDiacriticGivenCurrentPrevPOSAndPrevDiacritic, tKey + tD, 1d);
                            
                            tKey = tPWord.word + " " + tWord.word;
                            incrementValGivenKey(hmCurrentPrevWord, tKey, 1d);
                            incrementValGivenKey(hmDiacriticGivenCurrentPrevWord, tKey + tD, 1d);
                            
                            if (i >= 2)
                                tKey = training.words.get(i-2).POS + " " + tPWord.POS + " " + tWord.POS;
                            else
                                tKey = tPWord.POS + " " + tWord.POS;
                            incrementValGivenKey(hmCurrent2PrevPOS, tKey, 1d);
                            incrementValGivenKey(hmDiacriticGivenCurrent2PrevPOS, tKey + tD, 1d);
                        }
                    }
                }
                training.clear();
            }
        }
        // normalize values
        normalizeHashMapVals(hmDiacriticGivenPOS, hmPOS);
        normalizeHashMapVals(hmDiacriticGivenPrevPOS, hmPOS);
        normalizeHashMapVals(hmDiacriticGivenNextPOS, hmPOS);
        normalizeHashMapVals(hmDiacriticGivenWord, hmWord);
        normalizeHashMapVals(hmDiacriticGivenPrevWord, hmWord);
        normalizeHashMapVals(hmDiacriticGivenNextWord, hmWord);
        normalizeHashMapVals(hmDiacriticGivenStem, hmStem);
        normalizeHashMapVals(hmDiacriticGivenPrevStem, hmStem);
        normalizeHashMapVals(hmDiacriticGivenNextStem, hmStem);
        normalizeHashMapVals(hmDiacriticGivenStemPOS, hmStemPOS);
        normalizeHashMapVals(hmDiacriticGivenPrevStemPOS, hmStemPOS);
        normalizeHashMapVals(hmDiacriticGivenNextStemPOS, hmStemPOS);
        normalizeHashMapVals(hmDiacriticGivenGenderNumber, hmGenderNumber);
        normalizeHashMapVals(hmDiacriticGivenPrevGenderNumber, hmGenderNumber);
        normalizeHashMapVals(hmDiacriticGivenNextGenderNumber, hmGenderNumber);
        normalizeHashMapVals(hmDiacriticGivenPrefix, hmPrefix);
        normalizeHashMapVals(hmDiacriticGivenSuffix, hmSuffix);
        normalizeHashMapVals(hmDiacriticGivenPrefixPOS, hmPrefixPOS);
        normalizeHashMapVals(hmDiacriticGivenSuffixPOS, hmSuffixPOS);
        normalizeHashMapVals(hmDiacriticGivenTemplate, hmTemplate);
        normalizeHashMapVals(hmDiacriticGivenLastLetter, hmLastLetter);
        normalizeHashMapVals(hmDiacriticGivenCurrentPrevPOSAndPrevDiacritic, hmCurrentPrevPOSAndPrevDiacritic);
        normalizeHashMapVals(hmDiacriticGivenCurrent2PrevPOS, hmCurrent2PrevPOS);
        normalizeHashMapVals(hmDiacriticGivenCurrentPrevWord, hmCurrentPrevWord);

        // normalize diacritic
    //                for (String s : hmDiacritic.keySet())
    //                    hmDiacritic.put(s, hmDiacritic.get(s)/wordCount);
    }
    
    private double getFeatureValue(HashMap<String, Double> map, String key, double notFound)
    {
        if (map.containsKey(key))
            return map.get(key);
        else
            return notFound;
    }
    
    public SentenceClass guessCaseEnding(SentenceClass sentence)
    {
        for (int i = 0; i < sentence.words.size(); i++)
        {
            WordClass tWord = sentence.words.get(i);
            if (!tWord.POS.equals("S") && !tWord.POS.equals("E"))
            {
                String bestDiacritic = "";
                double bestScore = -10000000;
                for (String d : getPossibleDiacritics(sentence, i))
                {
                    ArrayList<Double> features = getFeatureValues(sentence, i, d);
                    double score = 0;
                    for (int fv = 0; fv < features.size(); fv++) {
                        score += features.get(fv) * model.get(fv);
                    }
                    if (bestScore < score) {
                        bestScore = score;
                        bestDiacritic = d;
                    }
                }
                tWord.guessDiacritic = bestDiacritic;
            }
        }
        return sentence;
    }
    
    public WordClass fixPrefixIssues(WordClass tWord)
    {
        String shamsiyya = "تثدذرزسشصضطظنل";
        String qamariyya = "أبجحخعغفقكمهويآإ";
        
        // correct lam prefix before verb
        if (tWord.prefix.contains("ل+") && tWord.stemPOS.equals("V"))
        {

        }
        // correct lam shamsiya wa lam qamariyya

        if (tWord.prefix.contains("ال+") && !tWord.stem.equals("#") && tWord.stem.length() > 0
                &&
                !(
                ArabicUtils.removeDiacritics(tWord.wordDiacritizedWOCase).startsWith("لال") ||
                ArabicUtils.removeDiacritics(tWord.wordDiacritizedWOCase).startsWith("ولال") ||
                ArabicUtils.removeDiacritics(tWord.wordDiacritizedWOCase).startsWith("فلال")
                )
                
                )
        {
            // if (shamsiyya.contains(tWord.stem.substring(0, 1)))
            {
                String prefix = "";
//                if (ArabicUtils.removeDiacritics(tWord.wordFullyDiacritized).contains(tWord.prefix.replace("+", "")))
//                    prefix = tWord.prefix.replace("+", "");
//                else if (ArabicUtils.removeDiacritics(tWord.wordFullyDiacritized).contains(tWord.prefix.replace("ل+ال+", "لل").replace("+", "")))
                    prefix = tWord.prefix.replace("ل+ال+", "لل").replace("+", "");
                // else
                //    System.err.print("");
                String head = tWord.wordFullyDiacritized;
                // get diacritized diacritic and remove from beginning of word
                int k = 0;
                while (k < head.length() && !ArabicUtils.removeDiacritics(head.substring(0, k)).equals(prefix))
                {
                    k++;
                }
                prefix = head.substring(0, k);
                head = head.substring(k);
//                if (head.length() == 0)
//                    System.err.println();
                
                while (ArabicUtils.AllArabicDiacritics.contains(head.substring(0, 1)))
                {
                    // prefix += head.substring(0, 1);
                    head = head.substring(1);
                }
                // get the first letter in the stem with its diacritic
                String firstLetter = head.substring(0, 1);
                head = head.substring(1);
                String firstLetterDiacritic = "";
                if (ArabicUtils.removeDiacritics(head).trim().length() == 0)
                {
                    firstLetterDiacritic = head;
                    head = "";
                }
                else
                {
                    while (ArabicUtils.AllArabicDiacritics.contains(head.substring(0, 1)))
                    {
                        firstLetterDiacritic += head.substring(0, 1);
                        head = head.substring(1);
                    }
                }
                if (shamsiyya.contains(tWord.stem.substring(0, 1)))
                {
                    if (firstLetterDiacritic.contains(ArabicUtils.buck2utf8("~")))
                    {
                        // don't do anything
                    }
                    else
                    {
                        firstLetterDiacritic = ArabicUtils.buck2utf8("~") + firstLetterDiacritic;
                    }
                }
                else if (qamariyya.contains(tWord.stem.substring(0, 1)))
                {
                    if (firstLetterDiacritic.contains(ArabicUtils.buck2utf8("~")))
                    {
                        firstLetterDiacritic = firstLetterDiacritic.replace(ArabicUtils.buck2utf8("~"), "");
                    }
                }
                if (firstLetter.equals("ا"))
                    firstLetterDiacritic = "";
                tWord.wordFullyDiacritized = diacritizePrefix(tWord.prefix, firstLetter) + firstLetter + firstLetterDiacritic + head;
            }
//            else if (qamariyya.contains(tWord.stem.substring(0, 1)))
//            {
//
//            }
        }   
        return tWord;
    }
    
    public String diacritizePrefix(String prefix, String firstLetter)
    {
        String shamsiyya = "تثدذرزسشصضطظنل";
        String qamariyya = "إآأبجحخعغفقكمهوي";
        String output = "";
        String[] parts = prefix.replace("ل+ال+", "لل+").split("\\+");
        for (String p : parts)
        {
            if (p.equals("و") || p.equals("ف") || p.equals("ك") || p.equals("س"))
                output += p + ArabicUtils.buck2utf8("a");
            else if (p.equals("ب") || p.equals("ل"))
                output += p + ArabicUtils.buck2utf8("i");
            else if (p.equals("ال"))
            {
                if (shamsiyya.contains(firstLetter))
                    output += p;
                else if (qamariyya.contains(firstLetter))
                    output += p + ArabicUtils.buck2utf8("o");
                else if (firstLetter.equals("ا"))
                    output += p + ArabicUtils.buck2utf8("i");
            }
            else if (p.equals("لل"))
            {
                if (shamsiyya.contains(firstLetter))
                    output += ArabicUtils.buck2utf8("lil");
                else if (qamariyya.contains(firstLetter))
                    output += ArabicUtils.buck2utf8("lilo");
                else if (firstLetter.equals("ا"))
                    output += ArabicUtils.buck2utf8("lili");
            }   
        }
        return output;
    }
    
    public SentenceClass putCaseEnding(SentenceClass sentence)
    {
        for (int i = 0; i < sentence.words.size(); i++)
        {
            WordClass tWord = sentence.words.get(i);
            if (!tWord.POS.equals("S") && !tWord.POS.equals("E"))
            {
                if (tWord.guessDiacritic.equals("#"))
                {
                    tWord.wordFullyDiacritized = tWord.wordDiacritizedWOCase;
                    if (tWord.prefix.contains("ال+") && tWord.stem.length() > 0 && !tWord.stem.equals("#"))
                        tWord = fixPrefixIssues(tWord);
                }
                else
                {
                    String head = tWord.wordDiacritizedWOCase;
                    String tail = "";
                    String suffix = tWord.suffix.replace("noSuffixFound", "").replace("+", "");
                    String prefix = tWord.prefix.replace("noPrefixFound", "");
                    prefix = prefix.replace("ل+ال+", "لل").replace("+", "");
                    String suffixDiac = diacritizeSuffixes(tWord.suffix.replace("noSuffixFound", ""), tWord.guessDiacritic, tWord.suffixPOS, tWord.genderNumber);
                    
                    for (int j = suffix.length() - 1; j >= 0; j--)
                    {
                        int pos = head.lastIndexOf(suffix.substring(j, j+1));
//                        if (pos == -1)
//                            System.err.println(tWord.wordDiacritizedWOCase + "\t" + suffix);
                        if (pos == -1)
                        {
//                            System.err.println(tWord.word);
                        }
                        else
                        {
                            tail = head.substring(pos) + tail;
                            head = head.substring(0, pos);
                        }
                    }
                    /*
                    tWord.wordFullyDiacritized = head.replaceFirst("[" + ArabicUtils.buck2utf8("aiou~NFK") + "]+$", "") + ArabicUtils.buck2utf8(tWord.guessDiacritic) + tail;
                    */
                    // check if OOV ... If OOV, put sukun
                    if (!dt.candidatesUnigram.containsKey(tWord.word.replace("+", "")) 
                            && !dt.candidatesUnigram.containsKey(tWord.stem.replace("+", ""))
                            && !head.matches(".*[" + ArabicUtils.AllArabicDiacritics + "]+.*")
                            )
                    {
                        if (
                                ArabicUtils.removeDiacritics(head).endsWith("ي") ||
                                ArabicUtils.removeDiacritics(head).endsWith("ا") ||
                                ArabicUtils.removeDiacritics(head).endsWith("ى") ||
                                ArabicUtils.removeDiacritics(head).endsWith("و") ||
                                tWord.suffix.replace("+", "").startsWith("ا") ||
                                tWord.suffix.replace("+", "").startsWith("ي") ||
                                tWord.suffix.replace("+", "").startsWith("و") ||
                                tWord.suffix.replace("+", "").startsWith("ى")
                                )
                            tWord.guessDiacritic = "";
                        else
                            tWord.guessDiacritic = "o";
                    }
                    
                    if (tWord.suffix.replace("+", "").startsWith("ون") || tWord.suffix.replace("+", "").startsWith("ين") || tWord.suffix.replace("+", "").startsWith("ان"))
                    {
                        // if (ArabicUtils.removeDiacritics(head).endsWith("ي"))
                        if (tWord.genderNumber.endsWith("D") && tWord.suffix.replace("+", "").startsWith("ين"))
                        {
                            if (tWord.guessDiacritic.contains("~"))
                                tWord.guessDiacritic = "~a";
                            else
                                tWord.guessDiacritic = "a";
                        }
                        else if (tWord.guessDiacritic.contains("~"))
                            tWord.guessDiacritic = "~";
                        else
                            tWord.guessDiacritic = "";
                    }
                    else if (tWord.word.endsWith("+ين"))
                    {
                        boolean dual = false;
                        // remove prefixes
                        int k = 0;
                        while (k < head.length() && !ArabicUtils.removeDiacritics(head.substring(0, k)).equals(prefix))
                        {
                            k++;
                        }
                        String headWithoutPrefix = head.substring(k);
                        headWithoutPrefix = headWithoutPrefix.replaceFirst("^[" + ArabicUtils.AllArabicDiacritics + "]+", "");
                        if (dt.dualPlural.containsKey(ArabicUtils.removeDiacritics(headWithoutPrefix)))
                        {
                            if (dt.dualPlural.get(ArabicUtils.removeDiacritics(headWithoutPrefix)))
                            {
                                // most likely dual
                                dual = true;
                            }
                        }
                        else if (tWord.genderNumber.endsWith("D"))
                        {
                            dual = true;
                        }
                        // remove yn off the end of the word
                        k = 1;
                        while (!ArabicUtils.removeDiacritics(tWord.wordDiacritizedWOCase.substring(tWord.wordDiacritizedWOCase.length() - k)).equals("ين"))
                        {
                            k++;
                        }
                        String temp = tWord.wordDiacritizedWOCase.substring(0, tWord.wordDiacritizedWOCase.length() - k);
                        temp = temp.replaceFirst("[" + ArabicUtils.buck2utf8("aiouNFK") + "]+$", "");
                        // System.err.println("**" + "\t" + tWord.wordDiacritizedWOCase + "\t" +  temp + ArabicUtils.buck2utf8("ayoni"));
                        if (dual)
                        {
                            head = temp + ArabicUtils.buck2utf8("ayoni");
                            tWord.guessDiacritic = "i";
                        }
                        else
                        {
                            head = temp + ArabicUtils.buck2utf8("yna");
                            tWord.guessDiacritic = "a";
                        }
                        
                    }
                    
                    if (tWord.guessDiacritic.equals("F") && ArabicUtils.removeDiacritics(head).endsWith("ي"))
                        tWord.guessDiacritic = "~F";
                    if (head.endsWith(ArabicUtils.buck2utf8("~")))
                    {
                        if (!tWord.guessDiacritic.contains("~"))
                            tWord.guessDiacritic = "~" + tWord.guessDiacritic;
                    }
                    tWord.wordFullyDiacritized = head.replaceFirst("[" + ArabicUtils.buck2utf8("aiou~NFK") + "]+$", "") + ArabicUtils.buck2utf8(tWord.guessDiacritic) + suffixDiac;
                    tWord = fixPrefixIssues(tWord);
                }
            }
        }
        return sentence;
    }
    
    public String diacritizeSuffixes(String suffix, String caseEnding, String suffixPOS, String genderNumber)
    {
        String output = "";
        if (suffix.trim().length() == 0)
            return output;
        String[] parts = suffix.replace("+", " ").trim().split(" +");
        String[] partsPOS = suffixPOS.replace("+", " ").trim().split(" +");

        for (int i  = 0; i < Math.min(parts.length,partsPOS.length); i++)
        {
            //System.out.println(parts[i].toString()+" :: "+partsPOS[i].toString());
            if (partsPOS[i].equals("NSUFF") && (parts[i].equals("ة") || parts[i].equals("ت")))
            {
                // do nothing
            }
            else
            {
                if (parts[i].equals("ه"))
                {
                    if (i == 0 && ";#;i;~i;".contains(caseEnding))
                        output += ArabicUtils.buck2utf8("hi");
                    else
                        output += ArabicUtils.buck2utf8("hu");
                }
                else if (parts[i].equals("هم"))
                {       
                    if (i == 0 && ";#;i;~i;".contains(caseEnding))
                        output += ArabicUtils.buck2utf8("himo");
                    else
                        output += ArabicUtils.buck2utf8("humo");
                }
                else if (parts[i].equals("هما"))
                {
                    if (i == 0 && ";#;i;~i;".contains(caseEnding))
                        output += ArabicUtils.buck2utf8("himA");
                    else
                        output += ArabicUtils.buck2utf8("humA");
                }
                else if (parts[i].equals("هن"))
                {
                    if (i == 0 && ";#;i;~i;".contains(caseEnding))
                        output += ArabicUtils.buck2utf8("hin~a");
                    else
                        output += ArabicUtils.buck2utf8("hun~a");
                }
                else if (parts[i].equals("ك"))
                {
                    output += ArabicUtils.buck2utf8("ka");
                }
                else if (parts[i].equals("كم"))
                {
                    output += ArabicUtils.buck2utf8("kumo");
                }
                else if (parts[i].equals("كما"))
                {
                    output += ArabicUtils.buck2utf8("kumA");
                }
                else if (parts[i].equals("كن"))
                {
                    output += ArabicUtils.buck2utf8("kun~a");
                }
                else if (parts[i].equals("تم"))
                {
                    output += ArabicUtils.buck2utf8("tumo");
                }
                else if (parts[i].equals("ين"))
                {
                    if (genderNumber.endsWith("D"))
                        output += ArabicUtils.buck2utf8("yoni");
                    else
                        output += ArabicUtils.buck2utf8("yna");
                }
                else if (parts[i].equals("ون"))
                {
                    output += ArabicUtils.buck2utf8("wna");
                }
                else if (parts[i].equals("ان"))
                {
                    output += ArabicUtils.buck2utf8("Ani");
                }
                else if (parts[i].equals("ت"))
                {
                    if (caseEnding.equals("o"))
                        output += ArabicUtils.buck2utf8("ta");
                    else
                        output += ArabicUtils.buck2utf8("to");
                }
                else
                {
                    output += parts[i];
                }
            }
        }
        return output;
    }
    
    public void generateSVM(String filename) throws Exception {
        BufferedReader br = openFileForReading(filename);
        BufferedWriter bw = openFileForWriting(filename + ".svm.case");

        String line = "";
        long wordCount = 0;
        long qid = 0;
        while ((line = br.readLine()) != null)
        {
            if (line.trim().length() > 0)
            {
                line = line.replace("+", "plus");
                line = line.replace(";", "semicolon");
                SentenceClass training = createCaseEndingTrainingData(line);
                ArrayList<String> words = ArabicUtils.tokenizeWithoutProcessing(line);
                if (training.words.size() == words.size() + 2)
                {
                    for (int i = 0; i < training.words.size(); i++)
                    {
                        
                        wordCount++;
                        if (String.valueOf(wordCount).endsWith("0000"))
                            System.err.println(wordCount);
                        WordClass tWord = training.words.get(i);
                        if (!tWord.POS.equals("S") && !tWord.POS.equals("E"))
                        {
                            qid++;
                            String word = tWord.word;
                            String stem = tWord.stem;
                            String diacritic = "";
                            if (stem.equals("#"))
                                diacritic = "#";
                            else
                            {
                                String suffix = tWord.suffix;
                                if (suffix == "noSuffixFound")
                                    suffix = "";
                                diacritic = ArabicUtils.utf82buck(getDiacriticFromWord(words.get(i-1), suffix.replace("+", "")));
                                if (diacritic.trim().length() == 0)
                                    diacritic = "#";
                            }
                            tWord.truthDiacritic = diacritic;
                            tWord.guessDiacritic = diacritic;
                            WordClass tPWord = training.words.get(i-1);
                            WordClass tNWord = training.words.get(i+1);
                            ArrayList<String> possibleDiacritics = getPossibleDiacritics(training, i);
                            if (!possibleDiacritics.contains(diacritic))
                                possibleDiacritics.add(diacritic);
                            for (String d : possibleDiacritics)
                            {
                                // if (hmDiacritic.get(d) > 30)
                                {
                                    ArrayList<Double> features = getFeatureValues(training, i, d);// new ArrayList<Double>();
                                    int rank = 1;
                                    if (d.equals(tWord.truthDiacritic))
                                        rank = 2;
                                    bw.write(rank + " qid:" + qid);
                                    for (int k = 1; k <= features.size(); k++)
                                    {
                                        bw.write(" " + k + ":" + features.get(k - 1));
                                    }
                                    bw.write("\n");
                                }
                            }
                        }
                    }
                }
                training.clear();
            }
        }
        bw.close();
    }
    
    private ArrayList<Double> getFeatureValues(SentenceClass sentence, int j, String tag)
    {
        ArrayList<Double> features = new ArrayList<Double>();
        WordClass tWord = sentence.words.get(j);
        WordClass tPWord = sentence.words.get(j - 1);
        WordClass tNWord = sentence.words.get(j + 1);
        features.add(getFeatureValueSmoothed(hmDiacriticGivenPOS, hmPOS, tWord.POS, tag, 1000));
        features.add(getFeatureValueSmoothed(hmDiacriticGivenPrevPOS, hmPOS, tPWord.POS, tag, 1000));
        features.add(getFeatureValueSmoothed(hmDiacriticGivenNextPOS, hmPOS, tNWord.POS, tag, 1000));
        features.add(getFeatureValueSmoothed(hmDiacriticGivenWord, hmWord, tWord.word, tag, 1000));
        features.add(getFeatureValueSmoothed(hmDiacriticGivenPrevWord, hmWord, tPWord.word, tag, 1000));
        features.add(getFeatureValueSmoothed(hmDiacriticGivenNextWord, hmWord, tNWord.word, tag, 1000));
        features.add(getFeatureValueSmoothed(hmDiacriticGivenStem, hmStem, tWord.stem, tag, 1000));
        features.add(getFeatureValueSmoothed(hmDiacriticGivenPrevStem, hmStem, tPWord.stem, tag, 1000));
        features.add(getFeatureValueSmoothed(hmDiacriticGivenNextStem, hmStem, tNWord.stem, tag, 1000));
        features.add(getFeatureValueSmoothed(hmDiacriticGivenStemPOS, hmStemPOS, tWord.stemPOS, tag, 1000));
        features.add(getFeatureValueSmoothed(hmDiacriticGivenPrevStemPOS, hmStemPOS, tPWord.stemPOS, tag, 1000));
        features.add(getFeatureValueSmoothed(hmDiacriticGivenNextStemPOS, hmStemPOS, tNWord.stemPOS, tag, 1000));
        features.add(getFeatureValueSmoothed(hmDiacriticGivenGenderNumber, hmGenderNumber, tWord.genderNumber, tag, 1000));
        features.add(getFeatureValueSmoothed(hmDiacriticGivenPrevGenderNumber, hmGenderNumber, tPWord.genderNumber, tag, 1000));
        features.add(getFeatureValueSmoothed(hmDiacriticGivenNextGenderNumber, hmGenderNumber, tNWord.genderNumber, tag, 1000));
        features.add(getFeatureValueSmoothed(hmDiacriticGivenPrefix, hmPrefix, tWord.prefix, tag, 1000));
        features.add(getFeatureValueSmoothed(hmDiacriticGivenSuffix, hmSuffix, tWord.suffix, tag, 1000));
        features.add(getFeatureValueSmoothed(hmDiacriticGivenPrefixPOS, hmPrefixPOS, tWord.prefixPOS, tag, 1000));
        features.add(getFeatureValueSmoothed(hmDiacriticGivenSuffixPOS, hmSuffixPOS, tWord.suffixPOS, tag, 1000));
        features.add(getFeatureValueSmoothed(hmDiacriticGivenTemplate, hmTemplate, tWord.stemTemplate, tag, 1000));
        features.add(getFeatureValueSmoothed(hmDiacriticGivenLastLetter, hmLastLetter, tWord.stem.substring(tWord.stem.length() - 1), tag, 1000));
        String tKey = tPWord.guessDiacritic + " " + tPWord.POS+ " " + tWord.POS;
        features.add(getFeatureValueSmoothed(hmDiacriticGivenCurrentPrevPOSAndPrevDiacritic, hmCurrentPrevPOSAndPrevDiacritic, tKey, tag, 1000));
        
        tKey = tPWord.word + " " + tWord.word;
        features.add(getFeatureValueSmoothed(hmDiacriticGivenCurrentPrevWord, hmCurrentPrevWord, tKey, tag, 1000));
        
        tKey = tPWord.POS + " " + tWord.POS + " " + tNWord.POS;
        features.add(getFeatureValueSmoothed(hmDiacriticGivenCurrentPrevNextPOS, hmCurrentPrevNextPOS, tKey, tag, 1000));
        
        if (j >= 2) tKey = sentence.words.get(j-2).POS + " " + tPWord.POS + " " + tWord.POS;
        else tKey = tPWord.POS + " " + tWord.POS;
        features.add(getFeatureValueSmoothed(hmDiacriticGivenCurrent2PrevPOS, hmCurrent2PrevPOS, tKey, tag, 1000));
        return features;
    }

    public ArrayList<String> getPossibleDiacritics(SentenceClass sentence, int j)
    {
        ArrayList<String> possibleDiacritics = new ArrayList<String>();
        
        // filter based template
        String diacritizedTemplateStem = AuxFunctions.getDiacritizedTemplate(sentence.words.get(j).wordDiacritizedWOCase, farasaSegmenter);
        String diacritizedTemplateFull = AuxFunctions.getDiacritizedTemplateExp(sentence.words.get(j).wordDiacritizedWOCase, farasaSegmenter);
        diacritizedTemplateFull = diacritizedTemplateFull.replaceFirst(" .*", "");
        // diacritizedTemplate = diacritizedTemplate.replaceFirst(" .*", "");
        if (!diacritizedTemplateFull.equals("Y") && !diacritizedTemplateFull.endsWith("*") && templateDiacriticFull.containsKey(diacritizedTemplateFull))
        {
            possibleDiacritics = new ArrayList<String>(templateDiacriticFull.get(diacritizedTemplateFull));
        }
        else if (!diacritizedTemplateStem.equals("Y") && !diacritizedTemplateStem.endsWith("*") && templateDiacriticStem.containsKey(diacritizedTemplateStem))
        {
            possibleDiacritics = new ArrayList<String>(templateDiacriticStem.get(diacritizedTemplateStem));
        }
        // add possible diacritics based on word and stem
        else if (hmWord.containsKey(sentence.words.get(j).word) && hmWord.get(sentence.words.get(j).word) > 1000) // if a word appears more than 50 times, get possible diacritics
        {
            for (String d : hmDiacritic.keySet())
            {
                if (hmDiacriticGivenWord.containsKey(sentence.words.get(j).word + "\t" + d) && hmDiacriticGivenWord.get(sentence.words.get(j).word + "\t" + d) > -5.3)
                {
                    possibleDiacritics.add(d);
                }
            }
        }
        else if (hmStem.containsKey(sentence.words.get(j).stem) && hmStem.get(sentence.words.get(j).stem) > 1000) // if a stem appears more than 50 times, get possible diacritics
        {
            for (String d : hmDiacritic.keySet())
            {
                if (hmDiacriticGivenStem.containsKey(sentence.words.get(j).stem + "\t" + d) && hmDiacriticGivenStem.get(sentence.words.get(j).stem + "\t" + d) > -5.3)
                {
                    possibleDiacritics.add(d);
                }
            }
        }
        else // (possibleDiacritics.size() == 0)
        {
            if (sentence.words.get(j).stemPOS.equals("ADJ") || sentence.words.get(j).stemPOS.equals("NOUN"))
            {
                for (String dd : ("a;i;u;~a;~i;~u;N;K;F;~N;~K;~F;#").split(";"))
                    possibleDiacritics.add(dd);
            }
            else if (sentence.words.get(j).stemPOS.equals("V"))
            {
                for (String dd : ("a;o;u;~a;~u;#").split(";"))
                    possibleDiacritics.add(dd);
            }
            else
            {
                for (String d : hmDiacritic.keySet())
                {
                    if (hmDiacritic.get(d) > 100)
                        possibleDiacritics.add(d);
                }
            }
        }
//        if (!sentence.words.get(j).suffixPOS.contains("CASE") && !sentence.words.get(j).suffix.contains("+ا"))
//        {   
//            if (possibleDiacritics.contains("F"))
//            {
//                possibleDiacritics.remove("F");
//            }
//            else if (possibleDiacritics.contains("~F"))
//            {
//                possibleDiacritics.remove("~F");
//            }
//        }
//        // filter based template
//        String diacritizedTemplate = AuxFunctions.getDiacritizedTemplate(sentence.words.get(j).wordDiacritizedWOCase, farasaSegmenter, ft);
//        // diacritizedTemplate = diacritizedTemplate.replaceFirst(" .*", "");
//        if (!diacritizedTemplate.equals("Y") && !diacritizedTemplate.endsWith("*") && templateDiacriticStem.containsKey(diacritizedTemplate))
//        {
//            ArrayList<String> tempPossibleDiacritics = new ArrayList<String>();
//            for (String s : possibleDiacritics)
//            {
//                if (templateDiacriticStem.get(diacritizedTemplate).contains(s))
//                    tempPossibleDiacritics.add(s);
//            }
//            possibleDiacritics = new ArrayList<String>(tempPossibleDiacritics);
//        }
//        
//        diacritizedTemplate = AuxFunctions.getDiacritizedTemplateExp(sentence.words.get(j).wordDiacritizedWOCase, farasaSegmenter, ft);
//        diacritizedTemplate = diacritizedTemplate.replaceFirst(" .*", "");
//        if (!diacritizedTemplate.equals("Y") && !diacritizedTemplate.endsWith("*") && templateDiacriticFull.containsKey(diacritizedTemplate))
//        {
//            ArrayList<String> tempPossibleDiacritics = new ArrayList<String>();
//            for (String s : possibleDiacritics)
//            {
//                if (templateDiacriticFull.get(diacritizedTemplate).contains(s))
//                    tempPossibleDiacritics.add(s);
//            }
//            possibleDiacritics = new ArrayList<String>(tempPossibleDiacritics);
//        }
        
        // check if the word has a suffix of +yn or +wn or +An
        if (sentence.words.get(j).word.endsWith("+ون") || sentence.words.get(j).word.endsWith("+ين"))
        {
            possibleDiacritics.clear();
            possibleDiacritics.add("a");
        }
        else if (sentence.words.get(j).word.endsWith("+ان"))
        {
            possibleDiacritics.clear();
            possibleDiacritics.add("i");
        }
        
        // check if word always has sukun
        if ((sukunWords.containsKey(sentence.words.get(j).word.replace("+", "")) || sukunStems.containsKey(sentence.words.get(j).stem.replace("+", ""))) 
                && sentence.words.get(j).POS.contains("NOUN"))
        {
            possibleDiacritics.clear();
            possibleDiacritics.add("o");
        }
        
        // check if word is from Aux List of Transliterations
        if (sentence.words.get(j).wordDiacritizedWOCase.startsWith("@@@"))
        {
            possibleDiacritics.clear();
            if (sentence.words.get(j).word.endsWith("و") || sentence.words.get(j).word.endsWith("ا") || sentence.words.get(j).word.endsWith("ي") || sentence.words.get(j).word.endsWith("ى"))
                possibleDiacritics.add("#");
            else
                possibleDiacritics.add("o");
            sentence.words.get(j).wordDiacritizedWOCase = sentence.words.get(j).wordDiacritizedWOCase.replace("@@@", "");
            sentence.words.get(j).word = sentence.words.get(j).word.replace("@@@", "");
        }
        
        // check if past tense verb
        if (sentence.words.get(j).stemPOS.equals("V") && !sentence.words.get(j).stem.matches("^[أنيت].*") && !sentence.words.get(j).suffix.replace("+", "").startsWith("و"))
        {
            String[] notAllowed = "u;~u;K;N;F".split(";");
                for (String sh : notAllowed)
                {
                    possibleDiacritics.remove(sh);
                }
        }
        
        // check the first letter in suffix
//        if (sentence.words.get(j).suffix.replace("+", "").startsWith("و") || sentence.words.get(j).suffix.replace("+", "").startsWith("ي"))
//        {
//            boolean addShadda = false;
//            for (String s : possibleDiacritics)
//            {
//                if (s.contains("~"))
//                    addShadda = true;
//            }
//            possibleDiacritics.clear();
//            possibleDiacritics.add("#");
//            possibleDiacritics.add("~");
//        } 
//        else 
        if (sentence.words.get(j).suffix.replace("+", "").startsWith("ا"))
        {
            boolean addTanween = possibleDiacritics.contains("F");
            boolean addTanweenShadda = possibleDiacritics.contains("~F");
            possibleDiacritics.clear();
            possibleDiacritics.add("#");
            if (addTanween)
                possibleDiacritics.add("F");
            if (addTanweenShadda)
                possibleDiacritics.add("~F");
        }
        
//        if (pastTenseVerbs.containsKey(sentence.words.get(j).stem.replace("+", "").trim()) && sentence.words.get(j).POS.equals("V"))
//        {
//            String[] PV = "~i;~u;K;N;F;i;u;o".split(";");
//            for (String sh : PV)
//            {
//                if (possibleDiacritics.contains(sh))
//                    possibleDiacritics.remove(sh);
//            }
//        }
        
        // check if word always bare
        if ((bareWords.containsKey(sentence.words.get(j).word.replace("+", "")) || bareWords.containsKey(sentence.words.get(j).stem.replace("+", ""))) 
                && sentence.words.get(j).POS.contains("NOUN"))
        {
            possibleDiacritics.clear();
            possibleDiacritics.add("#");
        }
        
        // check if it would get shadda
        if (shaddaStems.containsKey(sentence.words.get(j).stem.replace("+", ""))
                && sentence.words.get(j).POS.contains("NOUN")
                )
        {
            boolean state = shaddaStems.get(sentence.words.get(j).stem.replace("+", ""));
            if (state) // needs a shadda
            {
                String[] shadda = "a;i;u;o".split(";");
                for (String sh : shadda)
                {
                    possibleDiacritics.remove(sh);
                }
            }
            else // does not take shadda
            {
                String[] shadda = "~a;~i;~u;~".split(";");
                for (String sh : shadda)
                {
                    possibleDiacritics.remove(sh);
                }
            }
        }
        
        // remove diacritics from latin words
        if (!ArabicUtils.AllArabicLetters.contains(sentence.words.get(j).stem.substring(ArabicUtils.removeDiacritics(sentence.words.get(j).stem).length() - 1)))
        {
            possibleDiacritics.clear();
            possibleDiacritics.add("#");
        }
        
        // make sure Consonants get a diacritic
        if (!"اويى".contains(sentence.words.get(j).stem.substring(sentence.words.get(j).stem.length() - 1))
                && (sentence.words.get(j).suffix.equals("noSuffixFound") || !"اويى".contains(sentence.words.get(j).suffix.replace("+", "").substring(0,1)))
                )
        {
            if (possibleDiacritics.size() > 1)
                possibleDiacritics.remove("#");
        }
        
        // remove diacritics from long vowels
        // alef maqsoura
        if (sentence.words.get(j).suffix.equals("noSuffixFound") && sentence.words.get(j).stem.endsWith("ى"))
        {
            possibleDiacritics.clear();
            possibleDiacritics.add("#");
            possibleDiacritics.add("F");
        }
        
        if (sentence.words.get(j).suffix.equals("noSuffixFound") && sentence.words.get(j).stem.endsWith("ي"))
        {
            possibleDiacritics.remove("i");
        }
        
        if (sentence.words.get(j).suffix.equals("noSuffixFound") && sentence.words.get(j).stem.endsWith("و"))
        {
            possibleDiacritics.remove("u");
        }
        
        if (sentence.words.get(j).suffix.equals("noSuffixFound") && sentence.words.get(j).stem.endsWith("ا"))
        {
            possibleDiacritics.clear();
            possibleDiacritics.add("#");
        }
        
        // remove impossible diacritics based on POS and stemPOS
        if (sentence.words.get(j).prefixPOS.contains("DET") ||  // sentence.words.get(j).prefixPOS.contains("PREP") || 
                sentence.words.get(j).stemTemplate.equals("fwAEl") ||
                sentence.words.get(j).stemTemplate.equals("mfAEyl") ||
                sentence.words.get(j).stemTemplate.equals("mfAEl")
                )
        {
            String[] excludeTanween = "F;N;K;~F;~N;~K".split(";");
            for (String e : excludeTanween)
            {
                possibleDiacritics.remove(e);
            }
        }
        if (hmPOS.containsKey(sentence.words.get(j).POS) && hmPOS.get(sentence.words.get(j).POS) > 50) // if a POS appears more than 50 times, get possible diacritics
        {
            for (String d : hmDiacritic.keySet())
            {
                if (possibleDiacritics.contains(d) && ((!hmDiacriticGivenPOS.containsKey(sentence.words.get(j).POS + "\t" + d)) || hmDiacritic.get(d) < 10)) // || hmDiacriticGivenPOS.get(sentence.words.get(j).POS + "\t" + d) <= -5.3))
                {
                    possibleDiacritics.remove(d);
                }
            }
        }
        if (hmStemPOS.containsKey(sentence.words.get(j).stemPOS) && hmStemPOS.get(sentence.words.get(j).stemPOS) > 50) // if a stemPOS appears more than 50 times, get possible diacritics
        {
            for (String d : hmDiacritic.keySet())
            {
                if (possibleDiacritics.contains(d) && (!hmDiacriticGivenStemPOS.containsKey(sentence.words.get(j).stemPOS + "\t" + d))) // || hmDiacriticGivenStemPOS.get(sentence.words.get(j).stemPOS + "\t" + d) <= -5.3))
                {
                    possibleDiacritics.remove(d);
                }
            }
        }
        // remove impossible diacritics based on prefix and last letter
        String lastletter = sentence.words.get(j).word.substring(sentence.words.get(j).word.length() - 1);
        if (hmLastLetter.containsKey(lastletter) && hmLastLetter.get(lastletter) > 50) // if a POS appears more than 50 times, get possible diacritics
        {
            for (String d : hmDiacritic.keySet())
            {
                if (possibleDiacritics.contains(d) && (!hmDiacriticGivenLastLetter.containsKey(lastletter + "\t" + d))) // || hmDiacriticGivenLastLetter.get(lastletter + "\t" + d) <= -5.3))
                {
                    possibleDiacritics.remove(d);
                }
            }
        }
        if (!sentence.words.get(j).prefix.equals("noPrefixFound") && hmPrefix.containsKey(sentence.words.get(j).prefix) && hmPrefix.get(sentence.words.get(j).prefix) > 50) // if a stemPOS appears more than 50 times, get possible diacritics
        {
            for (String d : hmDiacritic.keySet())
            {
                if (possibleDiacritics.contains(d) && (!hmDiacriticGivenPrefix.containsKey(sentence.words.get(j).prefix + "\t" + d) || hmDiacriticGivenPrefix.get(sentence.words.get(j).prefix + "\t" + d) <= -5.3))
                {
                    possibleDiacritics.remove(d);
                }
            }
        }
        
        if (hmWord.containsKey(sentence.words.get(j-1).word) && hmWord.get(sentence.words.get(j-1).word) > 1000)
        {
            for (String d : hmDiacritic.keySet())
            {
                if (!hmDiacriticGivenPrevWord.containsKey(sentence.words.get(j-1).word + "\t" + d))
                {
                    possibleDiacritics.remove(d);
                }
            }
        }
        
        // specify which sequences that are impossible to happen
        WordClass tWord = sentence.words.get(j);
        WordClass tPWord = sentence.words.get(j - 1);
        String tKey = tPWord.guessDiacritic + " " + tPWord.POS+ " " + tWord.POS;
        ArrayList<String> tmp = new ArrayList<String>();
        for (String d : possibleDiacritics)
        {
            if (!hmDiacriticGivenCurrentPrevPOSAndPrevDiacritic.containsKey(tKey + "\t" + d) || hmDiacriticGivenCurrentPrevPOSAndPrevDiacritic.get(tKey + "\t" + d) < -4.6)
            {
                // do nothing
            }
            else
            {
                tmp.add(d);
            }
        }
        if (tmp.size() > 0)
            possibleDiacritics = new ArrayList<String>(tmp);
        
        // specifically handle tanween with two simple rules
        // RULE 1: tanween is not allowed if the following word is followed with idafa
        
        if (!sentence.words.get(j).prefix.contains("ال") && sentence.words.get(j).POS.contains("NOUN") 
                && (sentence.words.get(j+1).prefix.contains("ال") || (sentence.words.get(j+1).POS.contains("NOUN") && sentence.words.get(j+1).POS.contains("PRON"))))
        {
            // remove tanween
            possibleDiacritics.remove("N");
            possibleDiacritics.remove("F");
            possibleDiacritics.remove("K");
        }
        // RULE 2: >n, <n, lkn would get a sokun only if followed by a verb.  Otherwise, it gets a shada-fatha
        if (sentence.words.get(j).prefix.matches("[فبلوك](أن|لكن)") 
                && sentence.words.get(j+1).stemPOS.equals("V"))
        {
            // remove tanween
            possibleDiacritics.remove("~a");
        }
        
        
        if (possibleDiacritics.size() == 0)
        {
            for (String d : hmDiacritic.keySet())
            {
                if (hmDiacritic.get(d) > 100)
                    possibleDiacritics.add(d);
            }
        }
        return possibleDiacritics;
    }
    
    public HashMap<String, Double> normalizeHashMapVals(HashMap<String, Double> fullHash, HashMap<String, Double> normHash)
    {
        for (String s : fullHash.keySet()) {
            String w = s.substring(0, s.indexOf("\t"));
            String p = s.substring(s.indexOf("\t") + 1);
            // if (hmPosGivenSuffix.containsKey(s) && hmSuffix.containsKey(w))
            {
                double score = Math.log(fullHash.get(s) / normHash.get(w));
                fullHash.put(s, score);
            }
        }
        return fullHash;
    }
    
    public HashMap<String,Double> incrementValGivenKey(HashMap<String,Double> input, String key, double increment)
    {
        if (!input.containsKey(key))
            input.put(key, increment);
        else
            input.put(key, input.get(key) + increment);
        return input;
    }
        
    public void serializeMap(String BinDir, String MapName, HashMap input) throws IOException {
        FileOutputStream fos
                = new FileOutputStream(BinDir + "FDTdata." + MapName + ".ser");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(input);
        oos.close();
        fos.close();
    }

    public HashMap deserializeMap(String BinDir, String MapName) throws IOException, ClassNotFoundException
    {
        FileInputStream fis = new FileInputStream(BinDir + "FDTdata." + MapName + ".ser");
         ObjectInputStream ois = new ObjectInputStream(fis);
         HashMap map = (HashMap) ois.readObject();
         ois.close();
         fis.close();
         return map;
    }
    
    public void serializeDataStructures(String binDir) throws IOException {
        serializeMap(binDir, "hmDiacritic", hmDiacritic);
        serializeMap(binDir, "hmWord", hmWord);
        serializeMap(binDir, "hmPOS", hmPOS);
        serializeMap(binDir, "hmPrefix", hmPrefix);
        serializeMap(binDir, "hmPrefixPOS", hmPrefixPOS);
        serializeMap(binDir, "hmSuffix", hmSuffix);
        serializeMap(binDir, "hmSuffixPOS", hmSuffixPOS);
        serializeMap(binDir, "hmTemplate", hmTemplate);
        serializeMap(binDir, "hmStem", hmStem);
        serializeMap(binDir, "hmStemPOS", hmStemPOS);
        serializeMap(binDir, "hmLastLetter", hmLastLetter);
        serializeMap(binDir, "hmGenderNumber", hmGenderNumber);
        serializeMap(binDir, "hmDiacriticGivenPOS", hmDiacriticGivenPOS);
        serializeMap(binDir, "hmDiacriticGivenPrevPOS", hmDiacriticGivenPrevPOS);
        serializeMap(binDir, "hmDiacriticGivenNextPOS", hmDiacriticGivenNextPOS);
        serializeMap(binDir, "hmDiacriticGivenWord", hmDiacriticGivenWord);
        serializeMap(binDir, "hmDiacriticGivenPrevWord", hmDiacriticGivenPrevWord);
        serializeMap(binDir, "hmDiacriticGivenNextWord", hmDiacriticGivenNextWord);
        serializeMap(binDir, "hmDiacriticGivenStem", hmDiacriticGivenStem);
        serializeMap(binDir, "hmDiacriticGivenPrevStem", hmDiacriticGivenPrevStem);        
        serializeMap(binDir, "hmDiacriticGivenNextStem", hmDiacriticGivenNextStem);
        serializeMap(binDir, "hmDiacriticGivenStemPOS", hmDiacriticGivenStemPOS);
        serializeMap(binDir, "hmDiacriticGivenPrevStemPOS", hmDiacriticGivenPrevStemPOS);
        serializeMap(binDir, "hmDiacriticGivenNextStemPOS", hmDiacriticGivenNextStemPOS);
        serializeMap(binDir, "hmDiacriticGivenGenderNumber", hmDiacriticGivenGenderNumber);
        serializeMap(binDir, "hmDiacriticGivenPrevGenderNumber", hmDiacriticGivenPrevGenderNumber);
        serializeMap(binDir, "hmDiacriticGivenNextGenderNumber", hmDiacriticGivenNextGenderNumber);
        serializeMap(binDir, "hmDiacriticGivenPrefix", hmDiacriticGivenPrefix);
        serializeMap(binDir, "hmDiacriticGivenSuffix", hmDiacriticGivenSuffix);
        serializeMap(binDir, "hmDiacriticGivenPrefixPOS", hmDiacriticGivenPrefixPOS);
        serializeMap(binDir, "hmDiacriticGivenSuffixPOS", hmDiacriticGivenSuffixPOS);
        serializeMap(binDir, "hmDiacriticGivenTemplate", hmDiacriticGivenTemplate);
        serializeMap(binDir, "hmDiacriticGivenLastLetter", hmDiacriticGivenLastLetter);
        serializeMap(binDir, "hmCurrentPrevPOSAndPrevDiacritic", hmCurrentPrevPOSAndPrevDiacritic);
        serializeMap(binDir, "hmDiacriticGivenCurrentPrevPOSAndPrevDiacritic", hmDiacriticGivenCurrentPrevPOSAndPrevDiacritic);
        serializeMap(binDir, "hmCurrent2PrevPOS", hmCurrent2PrevPOS);
        serializeMap(binDir, "hmDiacriticGivenCurrent2PrevPOS", hmDiacriticGivenCurrent2PrevPOS);
        
        serializeMap(binDir, "hmCurrentPrevWord", hmCurrentPrevWord);
        serializeMap(binDir, "hmDiacriticGivenCurrentPrevWord", hmDiacriticGivenCurrentPrevWord);
        
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
         ObjectInputStream ois = new ObjectInputStream(this.getClass().getResourceAsStream("/data/FDTdata." + MapName + ".ser"));
         HashMap map = (HashMap) ois.readObject();
         ois.close();
         return map;
    }
    
    public void deserializeDataStructures() throws IOException, ClassNotFoundException {
            bareWords = deserializeMap("bareWords");
            sukunWords = deserializeMap("sukunWords");
            sukunStems = deserializeMap("sukunStems");
            shaddaStems = deserializeMap("shaddaStems");
            hmDiacritic = deserializeMap("hmDiacritic");
            hmWord = deserializeMap("hmWord");
            hmPOS = deserializeMap("hmPOS");
            hmPrefix = deserializeMap("hmPrefix");
            hmPrefixPOS = deserializeMap("hmPrefixPOS");
            hmSuffix = deserializeMap("hmSuffix");
            hmSuffixPOS = deserializeMap("hmSuffixPOS");
            hmTemplate = deserializeMap("hmTemplate");
            hmStem = deserializeMap("hmStem");
            hmStemPOS = deserializeMap("hmStemPOS");
            hmLastLetter = deserializeMap("hmLastLetter");
            hmGenderNumber = deserializeMap("hmGenderNumber");
            hmDiacriticGivenPOS = deserializeMap("hmDiacriticGivenPOS");
            hmDiacriticGivenPrevPOS = deserializeMap("hmDiacriticGivenPrevPOS");
            hmDiacriticGivenNextPOS = deserializeMap("hmDiacriticGivenNextPOS");
            hmDiacriticGivenWord = deserializeMap("hmDiacriticGivenWord");
            hmDiacriticGivenPrevWord = deserializeMap("hmDiacriticGivenPrevWord");
            hmDiacriticGivenNextWord = deserializeMap("hmDiacriticGivenNextWord");
            hmDiacriticGivenStem = deserializeMap("hmDiacriticGivenStem");
            hmDiacriticGivenPrevStem = deserializeMap("hmDiacriticGivenPrevStem");        
            hmDiacriticGivenNextStem = deserializeMap("hmDiacriticGivenNextStem");
            hmDiacriticGivenStemPOS = deserializeMap("hmDiacriticGivenStemPOS");
            hmDiacriticGivenPrevStemPOS = deserializeMap("hmDiacriticGivenPrevStemPOS");
            hmDiacriticGivenNextStemPOS = deserializeMap("hmDiacriticGivenNextStemPOS");
            hmDiacriticGivenGenderNumber = deserializeMap("hmDiacriticGivenGenderNumber");
            hmDiacriticGivenPrevGenderNumber = deserializeMap("hmDiacriticGivenPrevGenderNumber");
            hmDiacriticGivenNextGenderNumber = deserializeMap("hmDiacriticGivenNextGenderNumber");
            hmDiacriticGivenPrefix = deserializeMap("hmDiacriticGivenPrefix");
            hmDiacriticGivenSuffix = deserializeMap("hmDiacriticGivenSuffix");
            hmDiacriticGivenPrefixPOS = deserializeMap("hmDiacriticGivenPrefixPOS");
            hmDiacriticGivenSuffixPOS = deserializeMap("hmDiacriticGivenSuffixPOS");
            hmDiacriticGivenTemplate = deserializeMap("hmDiacriticGivenTemplate");
            hmDiacriticGivenLastLetter = deserializeMap("hmDiacriticGivenLastLetter");
            hmCurrentPrevPOSAndPrevDiacritic = deserializeMap("hmCurrentPrevPOSAndPrevDiacritic");
            hmDiacriticGivenCurrentPrevPOSAndPrevDiacritic = deserializeMap("hmDiacriticGivenCurrentPrevPOSAndPrevDiacritic");
            hmCurrent2PrevPOS = deserializeMap("hmCurrent2PrevPOS");
            hmDiacriticGivenCurrent2PrevPOS = deserializeMap("hmDiacriticGivenCurrent2PrevPOS");
            hmCurrentPrevWord = deserializeMap("hmCurrentPrevWord");
            hmDiacriticGivenCurrentPrevWord = deserializeMap("hmDiacriticGivenCurrentPrevWord");

            hmCurrentPrevNextPOS = deserializeMap("hmCurrentPrevNextPOS");
            hmDiacriticGivenCurrentPrevNextPOS = deserializeMap("hmDiacriticGivenCurrentPrevNextPOS"); 
            templateDiacriticFull = deserializeMap("templateDiacriticFull");
            templateDiacriticStem = deserializeMap("templateDiacriticStem");
            
//p            
//            String binDir = "C:\\RESEARCH\\FromMac\\work\\CLASSIC\\DIACRITIZE\\NEW-RDI\\";
//            File file = new File(binDir + "FDTdata.bareWords.ser");
//            if (file.exists())
//            {
//                bareWords = deserializeMap(binDir, "bareWords");
//            }
//            else
//            {
//                BufferedReader br = openFileForReading("c:/RESEARCH/FromMac/work/CLASSIC/DIACRITIZE/NEW-RDI/all-text.txt.tok.bare");
//                String line = "";
//                while ((line = br.readLine()) != null)
//                {
//                    String[] parts = line.split("\t");
//                    if (parts.length == 4)
//                    {
//                        String word = parts[0].trim();
//                        double d = Double.parseDouble(parts[3]);
//                        if ((word.endsWith("ي") || word.endsWith("ى") || word.endsWith("و") || word.endsWith("ا")) && d > 0.9)
//                            bareWords.put(parts[0].trim(), true); 
//                    }
//                }
//                serializeMap(binDir, "bareWords", bareWords);
//            }
            
//            File file = new File(binDir + "FDTdata.sukunWords.ser");
//            if (file.exists())
//            {
//                sukunWords = deserializeMap(binDir, "sukunWords");
//            }
//            else
//            {
//                BufferedReader br = openFileForReading("c:/RESEARCH/FromMac/work/CLASSIC/DIACRITIZE/NEW-RDI/sukunWords.txt");
//                String line = "";
//                while ((line = br.readLine()) != null)
//                {
//                    if (line.trim().length() > 0)
//                    {
//                        sukunWords.put(line.trim(), true); 
//                    }
//                }
//                serializeMap(binDir, "sukunWords", sukunWords);
//            }
//            
//            file = new File(binDir + "FDTdata.sukunStems.ser");
//            if (file.exists())
//            {
//                sukunStems = deserializeMap(binDir, "sukunStems");
//            }
//            else
//            {
//                BufferedReader br = openFileForReading("c:/RESEARCH/FromMac/work/CLASSIC/DIACRITIZE/NEW-RDI/sukunStems.txt");
//                String line = "";
//                while ((line = br.readLine()) != null)
//                {
//                    if (line.trim().length() > 0)
//                    {
//                        sukunStems.put(line.trim(), true); 
//                    }
//                }
//                serializeMap(binDir, "sukunStems", sukunStems);
//            }
//
//            file = new File(binDir + "FDTdata.shaddaStems.ser");
//            if (file.exists())
//            {
//                shaddaStems = deserializeMap(binDir, "shaddaStems");
//            }
//            else
//            {
//                BufferedReader br = openFileForReading("c:/RESEARCH/FromMac/work/CLASSIC/DIACRITIZE/NEW-RDI/shaddaStems.txt");
//                String line = "";
//                while ((line = br.readLine()) != null)
//                {
//                    String[] parts = line.split("\t");
//                    if (parts.length == 2)
//                    {
//                        if (Double.parseDouble(parts[1]) > 0.9)
//                            shaddaStems.put(parts[0].trim(), true); 
//                        else if (Double.parseDouble(parts[1]) < 0.1 )
//                            shaddaStems.put(parts[0] .trim(), false); 
//                    }
//                }
//                serializeMap(binDir, "shaddaStems", shaddaStems);
//            }
//            hmDiacritic = deserializeMap(binDir, "hmDiacritic");
//            hmWord = deserializeMap(binDir, "hmWord");
//            hmPOS = deserializeMap(binDir, "hmPOS");
//            hmPrefix = deserializeMap(binDir, "hmPrefix");
//            hmPrefixPOS = deserializeMap(binDir, "hmPrefixPOS");
//            hmSuffix = deserializeMap(binDir, "hmSuffix");
//            hmSuffixPOS = deserializeMap(binDir, "hmSuffixPOS");
//            hmTemplate = deserializeMap(binDir, "hmTemplate");
//            hmStem = deserializeMap(binDir, "hmStem");
//            hmStemPOS = deserializeMap(binDir, "hmStemPOS");
//            hmLastLetter = deserializeMap(binDir, "hmLastLetter");
//            hmGenderNumber = deserializeMap(binDir, "hmGenderNumber");
//            hmDiacriticGivenPOS = deserializeMap(binDir, "hmDiacriticGivenPOS");
//            hmDiacriticGivenPrevPOS = deserializeMap(binDir, "hmDiacriticGivenPrevPOS");
//            hmDiacriticGivenNextPOS = deserializeMap(binDir, "hmDiacriticGivenNextPOS");
//            hmDiacriticGivenWord = deserializeMap(binDir, "hmDiacriticGivenWord");
//            hmDiacriticGivenPrevWord = deserializeMap(binDir, "hmDiacriticGivenPrevWord");
//            hmDiacriticGivenNextWord = deserializeMap(binDir, "hmDiacriticGivenNextWord");
//            hmDiacriticGivenStem = deserializeMap(binDir, "hmDiacriticGivenStem");
//            hmDiacriticGivenPrevStem = deserializeMap(binDir, "hmDiacriticGivenPrevStem");        
//            hmDiacriticGivenNextStem = deserializeMap(binDir, "hmDiacriticGivenNextStem");
//            hmDiacriticGivenStemPOS = deserializeMap(binDir, "hmDiacriticGivenStemPOS");
//            hmDiacriticGivenPrevStemPOS = deserializeMap(binDir, "hmDiacriticGivenPrevStemPOS");
//            hmDiacriticGivenNextStemPOS = deserializeMap(binDir, "hmDiacriticGivenNextStemPOS");
//            hmDiacriticGivenGenderNumber = deserializeMap(binDir, "hmDiacriticGivenGenderNumber");
//            hmDiacriticGivenPrevGenderNumber = deserializeMap(binDir, "hmDiacriticGivenPrevGenderNumber");
//            hmDiacriticGivenNextGenderNumber = deserializeMap(binDir, "hmDiacriticGivenNextGenderNumber");
//            hmDiacriticGivenPrefix = deserializeMap(binDir, "hmDiacriticGivenPrefix");
//            hmDiacriticGivenSuffix = deserializeMap(binDir, "hmDiacriticGivenSuffix");
//            hmDiacriticGivenPrefixPOS = deserializeMap(binDir, "hmDiacriticGivenPrefixPOS");
//            hmDiacriticGivenSuffixPOS = deserializeMap(binDir, "hmDiacriticGivenSuffixPOS");
//            hmDiacriticGivenTemplate = deserializeMap(binDir, "hmDiacriticGivenTemplate");
//            hmDiacriticGivenLastLetter = deserializeMap(binDir, "hmDiacriticGivenLastLetter");
//            hmCurrentPrevPOSAndPrevDiacritic = deserializeMap(binDir, "hmCurrentPrevPOSAndPrevDiacritic");
//            hmDiacriticGivenCurrentPrevPOSAndPrevDiacritic = deserializeMap(binDir, "hmDiacriticGivenCurrentPrevPOSAndPrevDiacritic");
//            hmCurrent2PrevPOS = deserializeMap(binDir, "hmCurrent2PrevPOS");
//            hmDiacriticGivenCurrent2PrevPOS = deserializeMap(binDir, "hmDiacriticGivenCurrent2PrevPOS");
//            hmCurrentPrevWord = deserializeMap(binDir, "hmCurrentPrevWord");
//            hmDiacriticGivenCurrentPrevWord = deserializeMap(binDir, "hmDiacriticGivenCurrentPrevWord");
//
//            hmCurrentPrevNextPOS = deserializeMap(binDir, "hmCurrentPrevNextPOS");
//            hmDiacriticGivenCurrentPrevNextPOS = deserializeMap(binDir, "hmDiacriticGivenCurrentPrevNextPOS"); 
    }

}
