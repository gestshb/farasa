///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.qcri.farasa.pos;
//
//import com.qcri.farasa.segmenter.ArabicUtils;
//import static com.qcri.farasa.segmenter.ArabicUtils.ALLDelimiters;
//import static com.qcri.farasa.segmenter.ArabicUtils.AllArabicLetters;
//import static com.qcri.farasa.segmenter.ArabicUtils.AllHindiDigits;
//import static com.qcri.farasa.segmenter.ArabicUtils.prefixes;
//import static com.qcri.farasa.segmenter.ArabicUtils.suffixes;
//import com.qcri.farasa.segmenter.Farasa;
//import java.beans.Beans;
//import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.io.UnsupportedEncodingException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//import javax.swing.text.DefaultEditorKit;
//import javax.swing.text.Position;
//
///**
// *
// * @author kareemdarwish
// */
//public class FarasaPOSTagger {
//
//    public static HashMap<String, Double> hmPos = new HashMap<String, Double>();
//    public static HashMap<String, Boolean> hmPosNormal = new HashMap<String, Boolean>();
//    public static HashMap<String, Double> hmWord = new HashMap<String, Double>();
//    public static HashMap<String, Double> hmPosGivenWord = new HashMap<String, Double>(100000);
//    public static HashMap<String, Double> hmPosGivenPrevWord = new HashMap<String, Double>(100000);
//    public static HashMap<String, Double> hmPosGivenNextWord = new HashMap<String, Double>(100000);
//    
//    
//    // newly added features -- Nov. 14, 2016
//    public static HashMap<String, Double> hmCurrentWordPrevWord = new HashMap<String, Double>(100000);
//    public static HashMap<String, Double> hmCurrentWordNextWord = new HashMap<String, Double>(100000);
//    public static HashMap<String, Double> hmPosGivenCurrentWordPrevWord = new HashMap<String, Double>(100000);
//    public static HashMap<String, Double> hmPosGivenCurrentWordNextWord = new HashMap<String, Double>(100000);
//    public static HashMap<String, Double> hmCurrentWord2PrevWord = new HashMap<String, Double>(100000);
//    public static HashMap<String, Double> hmCurrentWord2NextWord = new HashMap<String, Double>(100000);
//    public static HashMap<String, Double> hmPosGivenCurrentWord2PrevWord = new HashMap<String, Double>(100000);
//    public static HashMap<String, Double> hmPosGivenCurrentWord2NextWord = new HashMap<String, Double>(100000);
//
//    public static HashMap<String, Double> hmBiGramPrev = new HashMap<String, Double>(50000);
//    public static HashMap<String, Double> hmPosGivenPrev2Words = new HashMap<String, Double>(100000);
//    public static HashMap<String, Double> hmBiGramNext = new HashMap<String, Double>(50000);
//    public static HashMap<String, Double> hmPosGivenNext2Words = new HashMap<String, Double>(100000);
//
//    public static HashMap<String, Double> hmTriGramPrev = new HashMap<String, Double>(500000);
//    public static HashMap<String, Double> hmPosGivenPrev3Words = new HashMap<String, Double>(500000);
//    public static HashMap<String, Double> hmTriGramNext = new HashMap<String, Double>(500000);
//    public static HashMap<String, Double> hmPosGivenNext3Words = new HashMap<String, Double>(500000);
//
//    public static HashMap<String, Double> hmTriGramPosPrev = new HashMap<String, Double>();
//    public static HashMap<String, Double> hmPosGivenPrev3Pos = new HashMap<String, Double>(50000);
//    
//    public static HashMap<String, Double> hmTriGramTemplatePrev = new HashMap<String, Double>();
//    public static HashMap<String, Double> hmPosGivenPrev3Template = new HashMap<String, Double>(50000);
//    
//    public static HashMap<String, Double> hm4GramPosPrev = new HashMap<String, Double>();
//    public static HashMap<String, Double> hmPosGivenPrev4Pos = new HashMap<String, Double>(50000);
//    
//    public static HashMap<String, Double> hmTriGramPosNext = new HashMap<String, Double>();
//    public static HashMap<String, Double> hmPosGivenNext3Pos = new HashMap<String, Double>(50000);
//
//    public static HashMap<String, Double> hm4GramPosNext = new HashMap<String, Double>();
//    public static HashMap<String, Double> hmPosGivenNext4Pos = new HashMap<String, Double>(50000);
//    
//    public static HashMap<String, Double> hmBiGramPosPrev = new HashMap<String, Double>();
//    public static HashMap<String, Double> hmPosGivenPrev2Pos = new HashMap<String, Double>();
//    public static HashMap<String, Double> hmBiGramPosNext = new HashMap<String, Double>();
//    public static HashMap<String, Double> hmPosGivenNext2Pos = new HashMap<String, Double>();
//
//    public static HashMap<String, Double> hmUniGramPosContext = new HashMap<String, Double>();
//    public static HashMap<String, Double> hmPosGivenContext1Pos = new HashMap<String, Double>();
//    public static HashMap<String, Double> hmBiGramPosContext = new HashMap<String, Double>();
//    public static HashMap<String, Double> hmPosGivenContext2Pos = new HashMap<String, Double>();
//
//    public static HashMap<String, Double> hmPosGivenMetaType = new HashMap<String, Double>();
//    public static HashMap<String, Double> hmWordGivenPos = new HashMap<String, Double>();
//    public static HashMap<String, Double> hmPosGivenSuffix = new HashMap<String, Double>();
//    public static HashMap<String, Double> hmPosGivenPrefix = new HashMap<String, Double>();
//    public static HashMap<String, Double> hmPrevPrefixandCurrentPrefix = new HashMap<String, Double>();
//    public static HashMap<String, Double> hmPosGivenPrevPrefixandCurrentPrefix = new HashMap<String, Double>();
//    public static HashMap<String, Double> hmPosGivenTemplate = new HashMap<String, Double>();
//    public static HashMap<String, Double> hmTemplate = new HashMap<String, Double>();
//    public static HashMap<String, Double> hmPosGivenPos = new HashMap<String, Double>();
//    public static HashMap<String, Double> hmPrefix = new HashMap<String, Double>();
//    public static HashMap<String, Double> hmSuffix = new HashMap<String, Double>();
//    public static HashMap<String, Double> hmPrevSuffixes = new HashMap<String, Double>();
//    public static HashMap<String, Double> hmMetaType = new HashMap<String, Double>();
//    public static HashMap<String, String> hmOnePossiblePos = new HashMap<String, String>();
//    private static HashMap<String, Integer> hmNumber = new HashMap<String, Integer>();
//    
//    private static HashMap<String, Double> hmCombinedPOS = new HashMap<String, Double>();
//    private static HashMap<String, Double> hmPosGivenPrevWordSuffix = new HashMap<String, Double>();
//    
//    private static HashMap<String, Double> hmPrevWordPOS = new HashMap<String, Double>();
//    private static HashMap<String, Double> hmPosGivenPrevWordPOS = new HashMap<String, Double>();
//    
//    private static HashMap<String, String> hmMada = new HashMap<String, String>();
//    
//    // private static HashMap<String, Double> hmContextPOSwithGenderNumberTags = new HashMap<String, Double>();
//    // private static HashMap<String, Double> hmPosGivenContextPOSwithGenderNumberTags = new HashMap<String, Double>();
//    
//    private static HashMap<String, Double> hmPreps = new HashMap<String, Double>();
//    private static ArrayList<Double> model = new ArrayList<Double>();
//    public static String binDir = "";
//    // private static FitTemplateClass ft = null;
//    private static Farasa farasa = null;
//    genderNumberTags gnt = null;
//    private static HashMap<String, Integer> hPrefixes = new HashMap<String, Integer>();
//    private static HashMap<String, Integer> hSuffixes = new HashMap<String, Integer>();
//    private static HashMap<String, Boolean> hStrictlyPrefix = new HashMap<String, Boolean>();
//    private static HashMap<String, Boolean> hStrictlySuffix = new HashMap<String, Boolean>();
//    private static HashMap<String, Boolean> hPossiblePrefix = new HashMap<String, Boolean>();
//    private static HashMap<String, Boolean> hPossibleSuffix = new HashMap<String, Boolean>();
//    
//    private static Pattern rAllArabicLetters = Pattern.compile("[" + AllArabicLetters + "]+");
//    private static Pattern rEnglishLetters = Pattern.compile(".*[a-zA-z]+.*");
//    private static Pattern rAllDelimiters = Pattern.compile(".*[" + ALLDelimiters + "]+.*");
//    private static Pattern rAllNumbers = Pattern.compile("[" + AllHindiDigits + "0-9\\.,\u00BC-\u00BE]+");
//
//    public FarasaPOSTagger(Farasa farasaInstance) throws IOException, FileNotFoundException, ClassNotFoundException, UnsupportedEncodingException, InterruptedException
//    {
//        // binDir = dataDir;
//        // binDir = dataDir;
//        if (farasaInstance == null)
//        {
//            farasa = new Farasa(); // "/Users/kareemdarwish/RESEARCH/FARASA/FarasaData/"
//        }
//        else
//        {
//            farasa = farasaInstance;
//        }
//        gnt = new genderNumberTags(farasa); // "/Users/kareemdarwish/RESEARCH/ArabicProcessingTools-master/POSandNERData/"
//        for (String pp : "من إلى عن في على ب ل ك حتى مذ منذ و ت رب خلا حاشا إلي علي ل+ ب+ ك+".split(" +"))
//        {
//            hmPreps.put(pp, 0d);
//        }
//        String[] modelVals = "1:0.1641309 2:0.21368973 3:-0.032040793 4:-0.060479183 5:-0.045209616 6:-0.0013929944 7:-0.045138083 8:0.0027416311 9:0.13443293 10:0.1317763 11:-0.053899568 12:0.0093294811 13:0.092907488 14:0.17465037 15:-0.0087724049 16:-0.014008488 17:0.0070360508 18:0.0045469049".split(" +");
//
//        
//        // String[] modelVals = "1:0.1641309 2:0.21368973 3:-0.032040793 4:-0.060479183 5:-0.045209616 6:-0.0013929944 7:-0.045138083 8:0.0027416311 9:0.13443293 10:0.1317763 11:-0.053899568 12:0.0093294811 13:0.092907488 14:0.17465037 15:-0.0087724049 16:-0.014008488 17:0.0070360508 18:0.0045469049".split(" +");
//        // String[] modelVals = "1:-0.049147725 2:0.040290944 3:-0.029710934 4:-0.033095803 5:-0.030762995 6:0.027074149 7:-0.017359206 8:0.026087306 9:-0.01829887 10:-0.0051791626 11:-0.059506763 12:0.0096180346 13:0.18738306 14:0.15546252 15:0.079615958 16:0.17222893 17:-0.0010195597 18:-0.0068834466 19:0.0079786815 20:-0.004930316".split(" +");
//        // String[] modelVals = "1:-0.055487063 2:0.033507597 3:-0.035014853 4:-0.034080483 5:-0.033455253 6:0.024716772 7:-0.019201176 8:0.026799956 9:-0.020931769 10:-0.0074091628 11:-0.060898136 12:0.011330462 13:0.18721573 14:0.15495247 15:0.084133327 16:0.17885382 17:-0.0044882097 18:-0.0098290863 19:0.0048639826 20:-0.0052856863".split(" +");
//        // String[] modelVals = "1:-0.45946959 2:0.079145424 3:-0.000978166 4:-0.013722993 5:-0.083382092 6:-0.00089322467 7:0.02694416 8:0.012428692 9:-0.3183884 10:0.025460854 11:0.45353383 12:0.40149832 13:0.097730771 14:0.33198598 15:0.033351809 16:-0.03856631 17:0.029688064 18:-0.0318981".split(" +");        
//        
//        for (String s : modelVals) {
//            model.add(Double.parseDouble(s.substring(s.indexOf(":") + 1)));
//        }
//
//        if (true)
//        {
//            deserializeDataStructures();
//        }
//        else
//        {
//            BufferedReader brNum = new BufferedReader(new InputStreamReader(new FileInputStream(new File("c:/RESEARCH/FromMac/RESEARCH/ArabicProcessingTools-master/POSandNERData/" + "number-gaz.txt")), "UTF8"));
//            // BufferedReader brNum = new BufferedReader(new InputStrea mReader(new FileInputStream(new File(binDir + "number-gaz.txt")), "UTF8"));
//            String line = "";
//            while ((line = brNum.readLine()) != null)
//            {
//                if (!hmNumber.containsKey(line.trim()))
//                {
//                    hmNumber.put(line.trim(), 1);
//                }
//            }
//        }
//            /*
//             * CASE NSUFF VSUFF
//             */
//            hStrictlySuffix.put("CASE", Boolean.TRUE);
//            hStrictlySuffix.put("NSUFF", Boolean.TRUE);
//            hStrictlySuffix.put("VSUFF", Boolean.TRUE);
//            /*
//             * DET JUS
//             */
//            hStrictlyPrefix.put("DET", Boolean.TRUE);
//            hStrictlyPrefix.put("JUS", Boolean.TRUE);
//            /*
//             * CONJ DET FUT_PART JUS PREP PART
//             */
//            hPossiblePrefix.put("CONJ", Boolean.TRUE);
//            hPossiblePrefix.put("DET", Boolean.TRUE);
//            hPossiblePrefix.put("FUT_PART", Boolean.TRUE);
//            hPossiblePrefix.put("JUS", Boolean.TRUE);
//            hPossiblePrefix.put("PREP", Boolean.TRUE);
//            hPossiblePrefix.put("PART", Boolean.TRUE);
//            /*
//             * CASE NSUFF VSUFF PART PRON
//             */
//            hPossibleSuffix.put("CASE", Boolean.TRUE);
//            hPossibleSuffix.put("NSUFF", Boolean.TRUE);
//            hPossibleSuffix.put("VSUFF", Boolean.TRUE);
//            hPossibleSuffix.put("PART", Boolean.TRUE);
//            hPossibleSuffix.put("PRON", Boolean.TRUE);
//            hPossibleSuffix.put("PREP", Boolean.TRUE);
//            /*
//             * ADJ ADV NOUN V
//             */
//            hmPosNormal.put("ADJ", Boolean.TRUE);
//            hmPosNormal.put("ADV", Boolean.TRUE);
//            hmPosNormal.put("NOUN", Boolean.TRUE);
//            hmPosNormal.put("V", Boolean.TRUE);
//        
////            BufferedReader br = ArabicUtils.openFileForReading("C:\\RESEARCH\\FromMac\\RESEARCH\\ArabicProcessingTools-master\\DATA\\word-pos-tag.clitic.txt");
////            String line = "";
////            while ((line = br.readLine()) != null)
////            {
////                String[] parts = line.split("\t");
////                if (parts.length == 2 && (parts[1].equals("NOUN") || parts[1].equals("ADJ")))
////                    hmMada.put(parts[0], parts[1]);
////            }
//
//    }
//
//    public void serializeMap(String BinDir, String MapName, HashMap input) throws FileNotFoundException, IOException {
//        FileOutputStream fos
//                = new FileOutputStream(BinDir + "FarasaPOSdata." + MapName + ".ser");
//        ObjectOutputStream oos = new ObjectOutputStream(fos);
//        oos.writeObject(input);
//        oos.close();
//        fos.close();
//    }
//
//    private InputStream resolveName(String name) {
//        if (name == null) {
//            return null;
//        }
//        if (!name.startsWith("/")) {
//            String baseName = this.getClass().getName();
//            int index = baseName.lastIndexOf('.');
//            if (index != -1) {
//                name = baseName.substring(0, index).replace('.', '/') + "/" + name;
//            }
//        } else {
//            name = name.substring(1);
//        }
//	// return name;
//	ClassLoader cl = this.getClass().getClassLoader();
//	return cl.getResourceAsStream(name);
//    }
//    
//    public HashMap deserializeMap(String BinDir, String MapName) throws FileNotFoundException, IOException, ClassNotFoundException
//    {
//	ObjectInputStream ois = new ObjectInputStream(resolveName("/FarasaData/FarasaPOSdata." + MapName + ".ser"));
//         HashMap map = (HashMap) ois.readObject();
//         ois.close();
//         return map;
////	 
////        FileInputStream fis = new FileInputStream(BinDir + "FarasaPOSdata." + MapName + ".ser");
////         ObjectInputStream ois = new ObjectInputStream(fis);
////         HashMap map = (HashMap) ois.readObject();
////         ois.close();
////         fis.close();
////         return map;
//    }
//    
//    public void serializeDataStructures(String dir) throws IOException {
//        serializeMap(binDir, "hmPos", hmPos);
//        serializeMap(binDir, "hmPosNormal", hmPosNormal);
//        serializeMap(binDir, "hmWord", hmWord);
//        serializeMap(binDir, "hmPosGivenWord", hmPosGivenWord);
//        serializeMap(binDir, "hmPosGivenPrevWord", hmPosGivenPrevWord);
//        serializeMap(binDir, "hmPosGivenNextWord", hmPosGivenNextWord);
//        serializeMap(binDir, "hmBiGramPrev", hmBiGramPrev);
//        serializeMap(binDir, "hmPosGivenPrev2Words", hmPosGivenPrev2Words);
//        serializeMap(binDir, "hmBiGramNext", hmBiGramNext);
//        serializeMap(binDir, "hmPosGivenNext2Words", hmPosGivenNext2Words);
//        serializeMap(binDir, "hmTriGramPrev", hmTriGramPrev);
//        serializeMap(binDir, "hmPosGivenPrev3Words", hmPosGivenPrev3Words);
//        serializeMap(binDir, "hmTriGramNext", hmTriGramNext);
//        serializeMap(binDir, "hmPosGivenNext3Words", hmPosGivenNext3Words);
//        serializeMap(binDir, "hmTriGramPosPrev", hmTriGramPosPrev);
//        serializeMap(binDir, "hmPosGivenPrev3Pos", hmPosGivenPrev3Pos);
//        
//        // serializeMap(binDir, "hmContextPOSwithGenderNumberTags", hmContextPOSwithGenderNumberTags);
//        // serializeMap(binDir, "hmPosGivenContextPOSwithGenderNumberTags", hmPosGivenContextPOSwithGenderNumberTags);
//        
//        serializeMap(binDir, "hmTriGramTemplatePrev", hmTriGramTemplatePrev);
//        serializeMap(binDir, "hmPosGivenPrev3Template", hmPosGivenPrev3Template);
//        
//        serializeMap(binDir, "hm4GramPosPrev", hm4GramPosPrev);
//        serializeMap(binDir, "hmPosGivenPrev4Pos", hmPosGivenPrev4Pos);
//        serializeMap(binDir, "hmTriGramPosNext", hmTriGramPosNext);
//        serializeMap(binDir, "hmPosGivenNext3Pos", hmPosGivenNext3Pos);
//        
//        serializeMap(binDir, "hm4GramPosNext", hm4GramPosNext);
//        serializeMap(binDir, "hmPosGivenNext4Pos", hmPosGivenNext4Pos);
//        
//        serializeMap(binDir, "hmBiGramPosPrev", hmBiGramPosPrev);
//        serializeMap(binDir, "hmPosGivenPrev2Pos", hmPosGivenPrev2Pos);
//        serializeMap(binDir, "hmBiGramPosNext", hmBiGramPosNext);
//        serializeMap(binDir, "hmPosGivenNext2Pos", hmPosGivenNext2Pos);
//        serializeMap(binDir, "hmUniGramPosContext", hmUniGramPosContext);
//        serializeMap(binDir, "hmPosGivenContext1Pos", hmPosGivenContext1Pos);
//        serializeMap(binDir, "hmBiGramPosContext", hmBiGramPosContext);
//        serializeMap(binDir, "hmPosGivenContext2Pos", hmPosGivenContext2Pos);
//        serializeMap(binDir, "hmPosGivenMetaType", hmPosGivenMetaType);
//        serializeMap(binDir, "hmWordGivenPos", hmWordGivenPos);
//        serializeMap(binDir, "hmPosGivenSuffix", hmPosGivenSuffix);
//        serializeMap(binDir, "hmPosGivenPrefix", hmPosGivenPrefix);
//        serializeMap(binDir, "hmPrevPrefixandCurrentPrefix", hmPrevPrefixandCurrentPrefix);
//        serializeMap(binDir, "hmPosGivenPrevPrefixandCurrentPrefix", hmPosGivenPrevPrefixandCurrentPrefix);
//        serializeMap(binDir, "hmPosGivenTemplate", hmPosGivenTemplate);
//        serializeMap(binDir, "hmTemplate", hmTemplate);
//        serializeMap(binDir, "hmPosGivenPos", hmPosGivenPos);
//        serializeMap(binDir, "hmPrefix", hmPrefix);
//        serializeMap(binDir, "hmSuffix", hmSuffix);
//        serializeMap(binDir, "hmMetaType", hmMetaType);
//        serializeMap(binDir, "hmOnePossiblePos", hmOnePossiblePos);
//        serializeMap(binDir, "hmNumber", hmNumber);
//        serializeMap(binDir, "hPrefixes", hPrefixes);
//        serializeMap(binDir, "hSuffixes", hSuffixes);
//        serializeMap(binDir, "hStrictlyPrefix", hStrictlyPrefix);
//        serializeMap(binDir, "hStrictlySuffix", hStrictlySuffix);
//        serializeMap(binDir, "hPossiblePrefix", hPossiblePrefix);
//        serializeMap(binDir, "hPossibleSuffix", hPossibleSuffix);
//        serializeMap(binDir, "hmPosGivenPrevWordSuffix", hmPosGivenPrevWordSuffix);
//    }
//
//    public void deserializeDataStructures() throws IOException, FileNotFoundException, ClassNotFoundException {
//        hmPos = deserializeMap(binDir, "hmPos");
//        // hmPosNormal = deserializeMap(binDir, "hmPosNormal");
//        hmWord = deserializeMap(binDir, "hmWord");
//        hmPosGivenWord = deserializeMap(binDir, "hmPosGivenWord");
//        hmPosGivenPrevWord = deserializeMap(binDir, "hmPosGivenPrevWord");
//        hmPosGivenNextWord = deserializeMap(binDir, "hmPosGivenNextWord");
//        // hmBiGramPrev = deserializeMap(binDir, "hmBiGramPrev");
//        // hmPosGivenPrev2Words = deserializeMap(binDir, "hmPosGivenPrev2Words");
//        // hmBiGramNext = deserializeMap(binDir, "hmBiGramNext");
//        // hmPosGivenNext2Words = deserializeMap(binDir, "hmPosGivenNext2Words");
//        // hmTriGramPrev = deserializeMap(binDir, "hmTriGramPrev");
//        // hmPosGivenPrev3Words = deserializeMap(binDir, "hmPosGivenPrev3Words");
//        // hmTriGramNext = deserializeMap(binDir, "hmTriGramNext");
//        // hmPosGivenNext3Words = deserializeMap(binDir, "hmPosGivenNext3Words");
//        // hmTriGramPosPrev = deserializeMap(binDir, "hmTriGramPosPrev");
//        hmPosGivenPrev3Pos = deserializeMap(binDir, "hmPosGivenPrev3Pos");
//        
//        // hmContextPOSwithGenderNumberTags = deserializeMap(binDir, "hmContextPOSwithGenderNumberTags");
//        // hmPosGivenContextPOSwithGenderNumberTags = deserializeMap(binDir, "hmPosGivenContextPOSwithGenderNumberTags");
//        
//        // hmTriGramTemplatePrev = deserializeMap(binDir, "hmTriGramTemplatePrev");
//        // hmPosGivenPrev3Template = deserializeMap(binDir, "hmPosGivenPrev3Template");
//        
//        // hm4GramPosPrev = deserializeMap(binDir, "hm4GramPosPrev");
//        hmPosGivenPrev4Pos = deserializeMap(binDir, "hmPosGivenPrev4Pos");
//        // hmTriGramPosNext = deserializeMap(binDir, "hmTriGramPosNext");
//        hmPosGivenNext3Pos = deserializeMap(binDir, "hmPosGivenNext3Pos");
//        
//        // hm4GramPosNext = deserializeMap(binDir, "hm4GramPosNext");
//        // hmPosGivenNext4Pos = deserializeMap(binDir, "hmPosGivenNext4Pos");
//        
//        // hmBiGramPosPrev = deserializeMap(binDir, "hmBiGramPosPrev");
//        hmPosGivenPrev2Pos = deserializeMap(binDir, "hmPosGivenPrev2Pos");
//        // hmBiGramPosNext = deserializeMap(binDir, "hmBiGramPosNext");
//        hmPosGivenNext2Pos = deserializeMap(binDir, "hmPosGivenNext2Pos");
//        // hmUniGramPosContext = deserializeMap(binDir, "hmUniGramPosContext");
//        hmPosGivenContext1Pos = deserializeMap(binDir, "hmPosGivenContext1Pos");
//        // hmBiGramPosContext = deserializeMap(binDir, "hmBiGramPosContext");
//        // hmPosGivenContext2Pos = deserializeMap(binDir, "hmPosGivenContext2Pos");
//        // hmPosGivenMetaType = deserializeMap(binDir, "hmPosGivenMetaType");
//        hmWordGivenPos = deserializeMap(binDir, "hmWordGivenPos");
//        hmPosGivenSuffix = deserializeMap(binDir, "hmPosGivenSuffix");
//        hmPosGivenPrefix = deserializeMap(binDir, "hmPosGivenPrefix");
//        // hmPrevPrefixandCurrentPrefix = deserializeMap(binDir, "hmPrevPrefixandCurrentPrefix");
//        hmPosGivenPrevPrefixandCurrentPrefix = deserializeMap(binDir, "hmPosGivenPrevPrefixandCurrentPrefix");
//        hmPosGivenTemplate = deserializeMap(binDir, "hmPosGivenTemplate");
//        // hmTemplate = deserializeMap(binDir, "hmTemplate");
//        hmPosGivenPos = deserializeMap(binDir, "hmPosGivenPos");
//        hmPrefix = deserializeMap(binDir, "hmPrefix");
//        hmSuffix = deserializeMap(binDir, "hmSuffix");
//        // hmMetaType = deserializeMap(binDir, "hmMetaType");
//        // hmOnePossiblePos = deserializeMap(binDir, "hmOnePossiblePos");
//        hmNumber = deserializeMap(binDir, "hmNumber");
//        hPrefixes = deserializeMap(binDir, "hPrefixes");
//        hSuffixes = deserializeMap(binDir, "hSuffixes");
//        hStrictlyPrefix = deserializeMap(binDir, "hStrictlyPrefix");
//        hStrictlySuffix = deserializeMap(binDir, "hStrictlySuffix");
//        hPossiblePrefix = deserializeMap(binDir, "hPossiblePrefix");
//        hPossibleSuffix = deserializeMap(binDir, "hPossibleSuffix");
//        hmPosGivenPrevWordSuffix = deserializeMap(binDir, "hmPosGivenPrevWordSuffix");
//    }
//
//    public Word getWordRepresentation(ArrayList<String> word) {
//        Word output = new Word();
//        String segmented = "";
//        ArrayList<String> posTags = new ArrayList<String>();
//        for (int i = 0; i < word.size(); i++) {
//            String s = word.get(i).substring(0, word.get(i).indexOf("\t"));
//            String pos = word.get(i).substring(word.get(i).lastIndexOf("\t") + 1);
//            posTags.add(pos);
//            if (i > 0) {
//                segmented += "+";
//            }
//            segmented += s;
//        }
//        ArrayList<String> segmentedWord = getWordParts(segmented);
//        for (int j = 0; j < segmentedWord.size(); j++) {
//            String position = "";
//            if (j == 0) {
//                position = "B";
//            } else {
//                position = "I";
//            }
//            String ss = segmentedWord.get(j);
//            if (j == 0 && ss.startsWith("+"))
//                ss = ss.substring(1);
//            if (j == segmentedWord.size() -1 && ss.endsWith("+"))
//                ss = ss.substring(0, ss.length() - 1);
//            // ArrayList<String> possiblePOS = new ArrayList<String>();// possiblePOSTags(ss);
//            String template = farasa.getStemTempate(ss); // ft.fitTemplate(ss);
//            
//            // correct prepositions -- basically restrict to a closed set
//            if (posTags.get(j).trim().equals("PREP") && !hmPreps.containsKey(ss.trim()))
//            {
//                if (ss.trim().equals("إن") || ss.trim().equals("إلا"))
//                    posTags.set(j, "PART");
//                else
//                    posTags.set(j, "NOUN");
//            }
//            
//            Clitic c = new Clitic(ss.trim(), template.trim(), null, posTags.get(j).trim(), position, "");
//            output.add(c);
//        }
//        return output;
//    }
//
//    public void train(String filename) throws FileNotFoundException, IOException, InterruptedException, Exception {
//        BufferedReader br = ArabicUtils.openFileForReading(filename);
//        BufferedWriter bw = ArabicUtils.openFileForWriting(filename + ".deleteMe");
//        Sentence fullSentence = new Sentence();
//        String line = "";
//        hmPos.put("§", 0d);
//        hmWord.put("§§", 0d);
//        hmPos.put("S", 0d);
//        hmWord.put("S", 0d);
//        hmPos.put("E", 0d);
//        hmWord.put("E", 0d);
//        // HashMap<String, Double> stats = new HashMap<String, Double>();
//        ArrayList<ArrayList<String>> sentence = new ArrayList<ArrayList<String>>();
//        ArrayList<String> word = new ArrayList<String>();
//        int lineNumber = 0;
//        while ((line = br.readLine()) != null) {
//            lineNumber++;
//            line = line.trim();
//            if (line.endsWith("\tO")) {
//                if (word.size() > 0) {
//                    word = fixPotentialSegmentationMismatch(word);
//                    fullSentence.addWord(getWordRepresentation(word));
//                    sentence.add(new ArrayList<String>(word));
//                }
//                word.clear();
//            } else if (line.length() == 0) {
//                if (word.size() > 0) {
//                    word = fixPotentialSegmentationMismatch(word);
//                    fullSentence.addWord(getWordRepresentation(word));
//                    sentence.add(new ArrayList<String>(word));
//                }
//                if (fullSentence.clitics.size() > 0) // put end of sentence marker
//                {
//                    fullSentence.addClitic(new Clitic("E", "E", new ArrayList<String>(), "E", "B", "E"));
//                }
//
//                if (fullSentence.clitics.size() > 0) {
//                    // gender and number tags to all NOUN, ADJ, NUM
//                    fullSentence = setAllGuessTagsToTruthTags(fullSentence);
//                    fullSentence = addGenderNumberFeatures(fullSentence);
//                    // get combined POS tags
//                    String combinedPOSTags = "";
//                    for (int j = 0; j < fullSentence.clitics.size(); j++)
//                    {
//                        if (fullSentence.clitics.get(j).position == "B")
//                            combinedPOSTags += " ";
//                        combinedPOSTags += fullSentence.clitics.get(j).truthPOS;
//                        if (j < fullSentence.clitics.size() - 1 && fullSentence.clitics.get(j+1).position.equals("I"))
//                            combinedPOSTags += "+";
//                    }
//                    String[] cmb = combinedPOSTags.split(" +");
//                    for (int k = 0; k < cmb.length - 1; k++)
//                    {
//                        String key = cmb[k] + " " + cmb[k+1];
//                        incrementValGivenKey(hmCombinedPOS, key, 1d);
//                    }
//                    for (int j = 0; j < fullSentence.clitics.size(); j++) {
//                        Clitic clitic = fullSentence.clitics.get(j);
//
//                        if (clitic.surface.equals("§§") || clitic.surface.equals("S") || clitic.surface.equals("E")) // if not a seperator
//                        {
//                            hmPos.put(clitic.truthPOS, hmPos.get(clitic.truthPOS) + 1);
//                            hmWord.put(clitic.surface, hmWord.get(clitic.surface) + 1);
//                        } else {
//                            // POS count
//                            incrementValGivenKey(hmPos, clitic.truthPOS, 1d);
//                            // word count
//                            incrementValGivenKey(hmWord, clitic.surface, 1d);                           
//                            // word_POS count
//                            // keep word_POS count
//                            incrementValGivenKey(hmPosGivenWord, clitic.surface + "\t" + clitic.truthPOS, 1d);                           
//                            // word_POS given previous clitic
//                            ArrayList<Integer> Positions = getSurroundingNClitics(fullSentence, j, -1);
//                            String previousClitic = fullSentence.clitics.get(Positions.get(0)).surface;
//                            incrementValGivenKey(hmPosGivenPrevWord, previousClitic + "\t" + clitic.truthPOS, 1d);
//                            // word_POS given next clitic
//                            Positions = getSurroundingNClitics(fullSentence, j, 1);
//                            String nextClitic = fullSentence.clitics.get(Positions.get(0)).surface;
//                            incrementValGivenKey(hmPosGivenNextWord, nextClitic + "\t" + clitic.truthPOS, 1d);
//                            
//                            // newly added features -- Nov. 14, 2016
//                            incrementValGivenKey(hmCurrentWordNextWord, clitic.surface + " " + nextClitic, 1d);
//                            incrementValGivenKey(hmCurrentWordPrevWord, clitic.surface + " " + previousClitic, 1d);
//                            incrementValGivenKey(hmPosGivenCurrentWordNextWord, clitic.surface + " " + nextClitic + "\t" + clitic.truthPOS, 1d);
//                            incrementValGivenKey(hmPosGivenCurrentWordPrevWord, clitic.surface + " " + previousClitic + "\t" + clitic.truthPOS, 1d);
//                            Positions = getSurroundingNClitics(fullSentence, j, -2);
//                            if (Positions.size() == 2) {
//                                String prev2Clitics = fullSentence.clitics.get(Positions.get(0)).surface + " " + fullSentence.clitics.get(Positions.get(1)).surface;
//                                incrementValGivenKey(hmCurrentWord2PrevWord, clitic.surface + " " + prev2Clitics, 1d);
//                                incrementValGivenKey(hmPosGivenCurrentWord2PrevWord, clitic.surface + " " + prev2Clitics + "\t" + clitic.truthPOS, 1d);
//                            }
//                            Positions = getSurroundingNClitics(fullSentence, j, 2);
//                            if (Positions.size() == 2) {
//                                String next2Clitics = fullSentence.clitics.get(Positions.get(0)).surface + " " + fullSentence.clitics.get(Positions.get(1)).surface;
//                                incrementValGivenKey(hmCurrentWord2NextWord, clitic.surface + " " + next2Clitics, 1d);
//                                incrementValGivenKey(hmPosGivenCurrentWord2NextWord, clitic.surface + " " + next2Clitics + "\t" + clitic.truthPOS, 1d);
//                            }
//                            
//                            // given preceeding 2 words
//                            Positions = getSurroundingNClitics(fullSentence, j, -2);
//                            if (Positions.size() == 2) {
//                                String prev2Clitics = fullSentence.clitics.get(Positions.get(0)).surface + " " + fullSentence.clitics.get(Positions.get(1)).surface;
//                                incrementValGivenKey(hmBiGramPrev, prev2Clitics, 1d);
//                                incrementValGivenKey(hmPosGivenPrev2Words, prev2Clitics + "\t" + clitic.truthPOS, 1d);
//                            }
//                            // given next 2 words
//                            Positions = getSurroundingNClitics(fullSentence, j, 2);
//                            if (Positions.size() == 2) {
//                                String next2Clitics = fullSentence.clitics.get(Positions.get(0)).surface + " " + fullSentence.clitics.get(Positions.get(1)).surface;
//                                incrementValGivenKey(hmBiGramNext, next2Clitics, 1d);
//                                incrementValGivenKey(hmPosGivenNext2Words, next2Clitics + "\t" + clitic.truthPOS, 1d);
//                            }
//                            // given context 1 POS
//                            String context1Pos = "";
//                            Positions = getSurroundingNClitics(fullSentence, j, -1);
//                            context1Pos += fullSentence.clitics.get(Positions.get(0)).truthPOS;
//                            Positions = getSurroundingNClitics(fullSentence, j, 1);
//                            context1Pos += " " + fullSentence.clitics.get(Positions.get(0)).truthPOS;
//                            incrementValGivenKey(hmUniGramPosContext, context1Pos, 1d);
//                            incrementValGivenKey(hmPosGivenContext1Pos, context1Pos + "\t" + clitic.truthPOS, 1d);
//                            // given context 2 POS
//                            String context2Pos = "";
//                            Positions = getSurroundingNClitics(fullSentence, j, -2);
//                            if (Positions.size() == 2) {
//                                context1Pos += fullSentence.clitics.get(Positions.get(0)).truthPOS + " " + fullSentence.clitics.get(Positions.get(1)).truthPOS;
//                                Positions = getSurroundingNClitics(fullSentence, j, 2);
//                                if (Positions.size() == 2) {
//                                    context1Pos += " " + fullSentence.clitics.get(Positions.get(0)).truthPOS + " " + fullSentence.clitics.get(Positions.get(1)).truthPOS;
//                                    incrementValGivenKey(hmBiGramPosContext, context2Pos, 1d);
//                                    incrementValGivenKey(hmPosGivenContext2Pos, context2Pos + "\t" + clitic.truthPOS, 1d);
//                                }
//                            }
//                            // given preceeding 2 POS
//                            Positions = getSurroundingNClitics(fullSentence, j, -2);
//                            if (Positions.size() == 2) {
//                                String prev2Pos = fullSentence.clitics.get(Positions.get(0)).truthPOS + " " + fullSentence.clitics.get(Positions.get(1)).truthPOS;
//                                incrementValGivenKey(hmBiGramPosPrev, prev2Pos, 1d);
//                                incrementValGivenKey(hmPosGivenPrev2Pos, prev2Pos + "\t" + clitic.truthPOS, 1d);
//                            }
//                            // given next 2 POS
//                            Positions = getSurroundingNClitics(fullSentence, j, 2);
//                            if (Positions.size() == 2) {
//                                String next2Pos = fullSentence.clitics.get(Positions.get(0)).truthPOS + " " + fullSentence.clitics.get(Positions.get(1)).truthPOS;
//                                incrementValGivenKey(hmBiGramPosNext, next2Pos, 1d);
//                                incrementValGivenKey(hmPosGivenNext2Pos, next2Pos + "\t" + clitic.truthPOS, 1d);
//                            }
//                            
//                            // given preceeding 3 POS
//                            Positions = getSurroundingNClitics(fullSentence, j, -3);
//                            if (Positions.size() == 3) {
//                                String prev3Pos = fullSentence.clitics.get(Positions.get(0)).truthPOS;
//                                for (int cnt = 1; cnt < 3; cnt++)
//                                    prev3Pos += " " + fullSentence.clitics.get(Positions.get(cnt)).truthPOS;
//                                incrementValGivenKey(hmTriGramPosPrev, prev3Pos, 1d);
//                                incrementValGivenKey(hmPosGivenPrev3Pos, prev3Pos + "\t" + clitic.truthPOS, 1d);
//                            }
//
//                            // given preceeding 3 Template
//                            Positions = getSurroundingNClitics(fullSentence, j, -3);
//                            // if (Positions.size() == 3) {
//                            String prev3Template = "";
//                            for (int cnt = 0; cnt < Positions.size(); cnt++)
//                            {
//                                if (fullSentence.clitics.get(Positions.get(cnt)).surface.endsWith("+") || fullSentence.clitics.get(Positions.get(cnt)).surface.startsWith("+"))
//                                    prev3Template += " " + fullSentence.clitics.get(Positions.get(cnt)).truthPOS;
//                                else
//                                    prev3Template += " " + fullSentence.clitics.get(Positions.get(cnt)).template;
//                            }
//                            prev3Template = prev3Template.trim();
//                            incrementValGivenKey(hmTriGramTemplatePrev, prev3Template, 1d);
//                            incrementValGivenKey(hmPosGivenPrev3Template, prev3Template + "\t" + clitic.truthPOS, 1d);
//                            
//                            // given preceeding 4 POS
//                            Positions = getSurroundingNClitics(fullSentence, j, -4);
//                            if (Positions.size() == 4) {
//                                String prev4Pos = fullSentence.clitics.get(Positions.get(0)).truthPOS;
//                                for (int cnt = 1; cnt < 4; cnt++)
//                                    prev4Pos += " " + fullSentence.clitics.get(Positions.get(cnt)).truthPOS;
//                                incrementValGivenKey(hm4GramPosPrev, prev4Pos, 1d);
//                                incrementValGivenKey(hmPosGivenPrev4Pos, prev4Pos + "\t" + clitic.truthPOS, 1d);
//                            }
//                            
//                            
//                            // given next 3 POS
//                            Positions = getSurroundingNClitics(fullSentence, j, 3);
//                            if (Positions.size() == 3) {
//                                String next3Pos = fullSentence.clitics.get(Positions.get(0)).truthPOS + " " + fullSentence.clitics.get(Positions.get(1)).truthPOS + " " + fullSentence.clitics.get(Positions.get(2)).truthPOS;
//                                incrementValGivenKey(hmTriGramPosNext, next3Pos, 1d);
//                                incrementValGivenKey(hmPosGivenNext3Pos, next3Pos + "\t" + clitic.truthPOS, 1d);
//                            }
//                            
//                            // given next 4 POS
//                            Positions = getSurroundingNClitics(fullSentence, j, 4);
//                            if (Positions.size() == 4) {
//                                String next4Pos = fullSentence.clitics.get(Positions.get(0)).truthPOS + " " + fullSentence.clitics.get(Positions.get(1)).truthPOS + " " + fullSentence.clitics.get(Positions.get(2)).truthPOS + " " + fullSentence.clitics.get(Positions.get(3)).truthPOS;
//                                incrementValGivenKey(hm4GramPosNext, next4Pos, 1d);
//                                incrementValGivenKey(hmPosGivenNext4Pos, next4Pos + "\t" + clitic.truthPOS, 1d);
//                            }
//                            
//                            // given metaType
//                            String metaType = getMetaType(clitic.surface);
//                            incrementValGivenKey(hmMetaType, metaType, 1d);
//                            incrementValGivenKey(hmPosGivenMetaType, metaType + "\t" + clitic.truthPOS, 1d);
//
//                            // get prefix and suffix counts
//                            if (clitic.surface.endsWith("+")) {
//                                incrementValGivenKey(hmPrefix, clitic.surface, 1d);
//                            } else if (clitic.surface.startsWith("+")) {
//                                incrementValGivenKey(hmSuffix, clitic.surface, 1d);
//                            }
//                            if (!clitic.surface.contains("+")) {
//                                // put template
//                                incrementValGivenKey(hmTemplate, clitic.template, 1d);
//                                // put POS given template
//                                incrementValGivenKey(hmPosGivenTemplate, clitic.template + "\t" + clitic.truthPOS, 1d);
//
//                                // put prefix with POS
//                                // get prefixes
//                                String headPrefixes = getCurrentWordPrefix(fullSentence, j);
//                                incrementValGivenKey(hmPrefix, headPrefixes, 1d);
//                                incrementValGivenKey(hmPosGivenPrefix, headPrefixes + "\t" + clitic.truthPOS, 1d);
//
//                                // POS given previous word suffix
//                                String prevWordSuffix = getPreviousWordSuffix(fullSentence, j);
//                                incrementValGivenKey(hmPrevSuffixes, prevWordSuffix, 1d);
//                                incrementValGivenKey(hmPosGivenPrevWordSuffix, prevWordSuffix + "\t" + clitic.truthPOS, 1d);
//                                
//                                // POS given previous word combined POS tag
//                                ArrayList<Integer> previousWordClitics = getPreviousWordClitics(fullSentence, j);
//                                String previousWordCombinedPOS = "";
//                                for (int ip : previousWordClitics)
//                                    previousWordCombinedPOS = fullSentence.clitics.get(ip).truthPOS + " " + previousWordCombinedPOS;
//                                previousWordCombinedPOS = previousWordCombinedPOS.trim();
//                                
//                                incrementValGivenKey(hmPrevWordPOS, previousWordCombinedPOS, 1d);
//                                incrementValGivenKey(hmPosGivenPrevWordPOS, previousWordCombinedPOS + "\t" + clitic.truthPOS, 1d);
//                                
//                                // put current Prefix and previous Prefix
//                                String prevPrefix = getPreviousWordPrefix(fullSentence, j);
//                                String prevAndCurrentPrefix = prevPrefix + "-" + headPrefixes;
//                                incrementValGivenKey(hmPrevPrefixandCurrentPrefix, prevAndCurrentPrefix, 1d);
//                                incrementValGivenKey(hmPosGivenPrevPrefixandCurrentPrefix, prevAndCurrentPrefix + "\t" + clitic.truthPOS, 1d);
//
//                                // put suffix with POS
//                                // get suffixes
//                                String tailSuffixes = getCurrentWordSuffix(fullSentence, j);
//                                incrementValGivenKey(hmSuffix, tailSuffixes, 1d);
//                                incrementValGivenKey(hmPosGivenSuffix, tailSuffixes + "\t" + clitic.truthPOS, 1d);
//                                /*
//                                 * 
//                                 * The code that takes into account gender and number features is deprecated
//                                 * because it did not improve results
//                                 * 
//                                 *
//                                // check if noun, adj, or num => which means it has a gender number tag
//                                if (clitic.genderNumber.trim().length() > 1)
//                                {
//                                    // if so add new feature that has POS tags of 4 prev w/ GenderNumber tags + GenderNumber of next 3 tags
//                                    Positions = getSurroundingNClitics(fullSentence, j, -3);
//                                    String posWithGenderNumber = "";
//                                    for (int pwgn = 0; pwgn < Positions.size(); pwgn++)
//                                    {
//                                        String tmpPOS = fullSentence.clitics.get(Positions.get(pwgn)).truthPOS;
//                                        if (fullSentence.clitics.get(Positions.get(pwgn)).genderNumber.trim().length() > 0)
//                                            tmpPOS = fullSentence.clitics.get(Positions.get(pwgn)).genderNumber;
//                                        posWithGenderNumber += tmpPOS + " ";
//                                    }
////                                    posWithGenderNumber += ",";
////                                    Positions = getSurroundingNClitics(fullSentence, j, 1);
////                                    for (int pwgn = 0; pwgn < Positions.size(); pwgn++)
////                                    {
////                                        String tmpPOS = fullSentence.clitics.get(Positions.get(pwgn)).truthPOS;
////                                        if (fullSentence.clitics.get(Positions.get(pwgn)).genderNumber.trim().length() > 0)
////                                            tmpPOS = fullSentence.clitics.get(Positions.get(pwgn)).genderNumber;
////                                        posWithGenderNumber += " " + tmpPOS;
////                                    }
//                                    posWithGenderNumber = posWithGenderNumber.trim();
//                                    incrementValGivenKey(hmContextPOSwithGenderNumberTags, posWithGenderNumber, 1d);
//                                    incrementValGivenKey(hmPosGivenContextPOSwithGenderNumberTags, posWithGenderNumber + "\t" + clitic.truthPOS, 1d);
//                                }
//                                */
//                            }
//                            String PosTagSequence = "";
//                            if (j == 0) // first clitic in the word
//                            {
//                                PosTagSequence = "S\t" + clitic.truthPOS;
//                            } else {
//                                PosTagSequence = fullSentence.clitics.get(j - 1).truthPOS + "\t" + clitic.truthPOS;
//                            }
//                            incrementValGivenKey(hmPosGivenPos, PosTagSequence, 1d);
//                        }
//                    }
//                }
//                word.clear();
//                sentence.clear();
//                fullSentence.clear();
//            } else {
//                String entry = entryToAdd(line).trim();
//                if (entry.length() > 0) {
//                    word.add(entry);
//                }
//            }
//        }
//        // now normalize all learnt counts
//
//        // normalize POS given Word & Word given POS
//        for (String s : hmPosGivenWord.keySet()) {
//            String w = s.substring(0, s.indexOf("\t"));
//            String p = s.substring(s.indexOf("\t") + 1);
//            double pGivenW = Math.log(hmPosGivenWord.get(s) / hmWord.get(w));
//            double wGivenp = Math.log(hmPosGivenWord.get(s) / hmPos.get(p));
//
//            hmPosGivenWord.put(s, pGivenW);
//            hmWordGivenPos.put(s, wGivenp);
//            if (s.contains("و+")) {
//                System.err.println(s + "\t" + pGivenW + "\t" + wGivenp);
//            }
//        }
//
//        // normalize POS given prefix & suffix
//        hmPosGivenPrefix = normalizeHashMapVals(hmPosGivenPrefix, hmPrefix);
//        hmPosGivenSuffix = normalizeHashMapVals(hmPosGivenSuffix, hmSuffix);
//        // normalize POS give template
//        hmPosGivenTemplate = normalizeHashMapVals(hmPosGivenTemplate, hmTemplate);
//        // normalize POS given POS
//        hmPosGivenPos = normalizeHashMapVals(hmPosGivenPos, hmPos);
//        // normalize POS given previousClitic
//        hmPosGivenPrevWord = normalizeHashMapVals(hmPosGivenPrevWord, hmWord);
//        // normalize POS given nextClitic
//        hmPosGivenNextWord = normalizeHashMapVals(hmPosGivenNextWord, hmWord);
//        
//        // newly added features -- Nov. 14, 2016
//        hmPosGivenCurrentWordNextWord = normalizeHashMapVals(hmPosGivenCurrentWordNextWord, hmCurrentWordNextWord);
//        hmPosGivenCurrentWordPrevWord = normalizeHashMapVals(hmPosGivenCurrentWordPrevWord, hmCurrentWordPrevWord);
//        hmPosGivenCurrentWord2NextWord = normalizeHashMapVals(hmPosGivenCurrentWord2NextWord, hmCurrentWord2NextWord);
//        hmPosGivenCurrentWord2PrevWord = normalizeHashMapVals(hmPosGivenCurrentWord2PrevWord, hmCurrentWord2PrevWord);
//        
//        // normalize POS given metaType
//        hmPosGivenMetaType = normalizeHashMapVals(hmPosGivenMetaType, hmMetaType);
//        // normalize POS given prev & curr prefixes
//        hmPosGivenPrevPrefixandCurrentPrefix = 
//                normalizeHashMapVals(hmPosGivenPrevPrefixandCurrentPrefix, hmPrevPrefixandCurrentPrefix);
//        // normalize POS given prev 2 clitics
//        hmPosGivenPrev2Words = normalizeHashMapVals(hmPosGivenPrev2Words, hmBiGramPrev);
//        // normalize POS given next 2 clitics
//        hmPosGivenNext2Words = normalizeHashMapVals(hmPosGivenNext2Words, hmBiGramNext);        
//        // normalize POS given prev 2 POS
//        hmPosGivenPrev2Pos = normalizeHashMapVals(hmPosGivenPrev2Pos, hmBiGramPosPrev);        
//        // normalize POS given next 2 POS
//        hmPosGivenNext2Pos = normalizeHashMapVals(hmPosGivenNext2Pos, hmBiGramPosNext);        
//        // normalize POS given prev 3 POS
//        hmPosGivenPrev3Pos = normalizeHashMapVals(hmPosGivenPrev3Pos, hmTriGramPosPrev);
//        hmPosGivenPrev4Pos = normalizeHashMapVals(hmPosGivenPrev4Pos, hm4GramPosPrev);
//        hmPosGivenPrev3Template = normalizeHashMapVals(hmPosGivenPrev3Template, hmTriGramTemplatePrev);
//        hmPosGivenNext3Pos = normalizeHashMapVals(hmPosGivenNext3Pos, hmTriGramPosNext);
//        hmPosGivenNext4Pos = normalizeHashMapVals(hmPosGivenNext4Pos, hm4GramPosNext);
//        // hmPosGivenContextPOSwithGenderNumberTags = normalizeHashMapVals(hmPosGivenContextPOSwithGenderNumberTags, hmContextPOSwithGenderNumberTags);
//        // normalize POS given Context 1 POS
//        hmPosGivenContext1Pos = normalizeHashMapVals(hmPosGivenContext1Pos, hmUniGramPosContext);
//        // normalize POS given Context 2 POS
//        hmPosGivenContext2Pos = normalizeHashMapVals(hmPosGivenContext2Pos, hmBiGramPosContext);
//        // normalize previous word suffix
//        hmPosGivenPrevWordSuffix = normalizeHashMapVals(hmPosGivenPrevWordSuffix, hmPrevSuffixes);
//        // normalize previous word combined POS tag
//        hmPosGivenPrevWordPOS = normalizeHashMapVals(hmPosGivenPrevWordPOS, hmPrevWordPOS);
//        
//        for (String s : hmCombinedPOS.keySet())
//        {
//            hmCombinedPOS.put(s, Math.log(hmCombinedPOS.get(s)));
//        }
//
//        br.close();
//        // now we are ready to generate training files
//    }
//
//    public HashMap<String, Double> normalizeHashMapVals(HashMap<String, Double> fullHash, HashMap<String, Double> normHash)
//    {
//        for (String s : fullHash.keySet()) {
//            String w = s.substring(0, s.indexOf("\t"));
//            String p = s.substring(s.indexOf("\t") + 1);
//            // if (hmPosGivenSuffix.containsKey(s) && hmSuffix.containsKey(w))
//            {
//                if (normHash.get(w) == null)
//                    System.err.println(w);
//                double score = Math.log(fullHash.get(s) / normHash.get(w));
//                fullHash.put(s, score);
//            }
//        }
//        return fullHash;
//    }
//    
//    public HashMap<String,Double> incrementValGivenKey(HashMap<String,Double> input, String key, double increment)
//    {
//        if (!input.containsKey(key))
//            input.put(key, increment);
//        else
//            input.put(key, input.get(key) + increment);
//        return input;
//    }
//    
//    public ArrayList<Integer> getPreviousWordClitics(Sentence sentence, int position)
//    {
//        ArrayList<Integer> output = new ArrayList<Integer>();
//        int j = position;
//
//        // get the beginning of the current word
//        while (!sentence.clitics.get(j).position.equals("B")) {
//            j--;
//        }
//        j--;
//        output.add(j);
//            
//        if (j > 0) // find seperator in previous word
//        {
//            while (!sentence.clitics.get(j).position.equals("B")) {
//                j--;
//                output.add(j);
//            }
//        }
//        return output;
//    }
//    
//    public ArrayList<Integer> getSurroundingNClitics(Sentence sentence, int position, int len) // type 0 = surface form; type 1 = truthPOS
//    {
//        ArrayList<Integer> output = new ArrayList<Integer>();
//        int start = Math.min(position, position + len);
//        start = Math.max(0, start);
//
//        int end = Math.max(position, position + len);
//        end = Math.min(end, sentence.clitics.size() - 1);
//
//        if (len < 0) {
//            end--;
//        }
//        if (len > 0) {
//            start++;
//        }
//
//        for (int i = start; i <= end; i++) {
//            output.add(i);
//        }
//
//        return output;
//    }
//
//    private String getCurrentWordPrefix(Sentence sentence, int position) {
//        String output = "";
//        int j = position;
//        while (j - 1 > 0 && sentence.clitics.get(j - 1).surface.endsWith("+")) {
//            j--;
//            output = sentence.clitics.get(j).surface + output;
//        }
//        if (output.trim().length() == 0) {
//            output = "#";
//        }
//        return output.trim();
//    }
//
//    private String getPreviousWordPrefix(Sentence sentence, int position) {
//        String output = "";
//        int j = position;
//
//        // get the beginning of the current word
//        while (!sentence.clitics.get(j).position.equals("B")) {
//            j--;
//        }
//        j--;
//        if (j == 0) {
//            output = "S";
//        } else // find seperator in previous word
//        {
//            while (!sentence.clitics.get(j).position.equals("B")) {
//                j--;
//                if (sentence.clitics.get(j).surface.endsWith("+")) {
//                    output = sentence.clitics.get(j).surface + output;
//                }
//            }
//        }
//
//        if (output.trim().length() == 0) {
//            output = "#";
//        }
//        return output;
//    }
//    
//    private String getPreviousWordSuffix(Sentence sentence, int position) {
//        String output = "";
//        int j = position;
//
//        // get the beginning of the current word
//        while (!sentence.clitics.get(j).position.equals("B")) {
//            j--;
//        }
//        j--;
//        if (j == 0) {
//            output = "S";
//        } else // find seperator in previous word
//        {
//            while (!sentence.clitics.get(j).position.equals("B")) {
//                if (sentence.clitics.get(j).surface.startsWith("+")) {
//                    output = sentence.clitics.get(j).surface + output;
//                }
//                j--;
//            }
//        }
//
//        if (output.trim().length() == 0) {
//            output = "#";
//        }
//        return output;
//    }
//
//    private String getCurrentWordSuffix(Sentence sentence, int position) {
//        String output = "";
//        int j = position;
//        while (j + 1 < sentence.clitics.size() && sentence.clitics.get(j + 1).surface.startsWith("+")) {
//            j++;
//            output += sentence.clitics.get(j).surface;
//        }
//        if (output.trim().length() == 0) {
//            output = "#";
//        }
//        return output;
//    }
//
//    private ArrayList<String> fixPotentialSegmentationMismatch(ArrayList<String> word) {
//        if (word.size() >= 2 && word.get(word.size() - 1).startsWith("لا") && word.get(word.size() - 1).endsWith("PART") && word.get(word.size() - 2).startsWith("أن") && word.get(word.size() - 2).endsWith("PART")) {
//            word.set(word.size() - 2, "ألا\tY\tNOT\tPART");
//            word.remove(word.size() - 1);
//        } else if (word.size() >= 2 && word.get(word.size() - 1).startsWith("ما") && word.get(word.size() - 1).endsWith("PART") && word.get(word.size() - 2).startsWith("عند") && word.get(word.size() - 2).endsWith("NOUN")) {
//            word.set(word.size() - 2, "عندما\tY\tNOT\tNOUN");
//            word.remove(word.size() - 1);
//        }
//        return word;
//    }
//
//    public void generateSVM(String filename) throws FileNotFoundException, IOException, InterruptedException, Exception {
//        BufferedReader br = ArabicUtils.openFileForReading(filename);
//        BufferedWriter bw = ArabicUtils.openFileForWriting(filename + ".nov.14.2016.svm");
//        BufferedWriter bwGN = ArabicUtils.openFileForWriting(filename + ".nov.14.2016.gn.svm");
//        Sentence fullSentence = new Sentence();
//        String line = "";
//
//        // HashMap<String, Double> stats = new HashMap<String, Double>();
//        ArrayList<ArrayList<String>> sentence = new ArrayList<ArrayList<String>>();
//        ArrayList<String> word = new ArrayList<String>();
//        int qid = 1;
//        int lineNumber = 0;
//        while ((line = br.readLine()) != null) {
//            lineNumber++;
//            line = line.trim();
//            if (line.endsWith("\tO")) {
//                if (word.size() > 0) {
//                    word = fixPotentialSegmentationMismatch(word);
//                    fullSentence.addWord(getWordRepresentation(word));
//                    sentence.add(new ArrayList<String>(word));
//                }
//                word.clear();
//            } else if (line.length() == 0) {
//                if (word.size() > 0) {
//                    word = fixPotentialSegmentationMismatch(word);
//                    fullSentence.addWord(getWordRepresentation(word));
//                    sentence.add(new ArrayList<String>(word));
//                }
//                if (fullSentence.clitics.size() > 0) // put end of sentence marker
//                {
//                    fullSentence.addClitic(new Clitic("E", "E", new ArrayList<String>(), "E", "B", "E"));
//                }
//                // check if we have a word
//
//                if (fullSentence.clitics.size() > 0) {
//                    fullSentence = setAllGuessTagsToTruthTags(fullSentence);
//                    fullSentence = addGenderNumberFeatures(fullSentence);
//                    for (int j = 0; j < fullSentence.clitics.size(); j++) {
//                        Clitic clitic = fullSentence.clitics.get(j);
//                        
//                        if (clitic.surface.equals("§§") || clitic.surface.equals("S") || clitic.surface.equals("E")) {
//                            // do nothing
//                        } else {
//                            ArrayList<String> possibleTags = possiblePOSTags(fullSentence, j);
//                            if (!possibleTags.contains(clitic.truthPOS))
//                                possibleTags.add(clitic.truthPOS);
//                            
//                            // generate training data w/o gender and number
//                            for (String tag : possibleTags) {
//                                ArrayList<Double> features = getFeaturesValues(fullSentence, j, tag);
//                                // rank 2 is the correct tag.  rank 1 is the wrong tag
//                                String rank = "1";
//                                if (tag.equals(clitic.truthPOS)) {
//                                    rank = "2";
//                                }
//
//                                bw.write(rank + " qid:" + qid);
//                                for (int k = 1; k <= features.size(); k++) {
//                                    if (features.get(k - 1) > -20) {
//                                        bw.write(" " + k + ":" + features.get(k - 1));
//                                    } else {
//                                        bw.write(" " + k + ":-10");
//                                    }
//                                }
//                                bw.write("\n");
//                            }
//                            
//                            // 
//                            /*
//                             * Due to failure to produce better results, this path is deprecated
//                             * 
//                            if (clitic.truthPOS.equals("NOUN") || clitic.truthPOS.equals("ADJ") || clitic.truthPOS.equals("NUM"))
//                            {
//                                for (String tag : ("NOUN ADJ NUM").split(" +"))
//                                {
//                                    if (possibleTags.contains(tag))
//                                    {
//                                        ArrayList<Double> features = getFeaturesValues(fullSentence, j, tag);
//                                        features = getExtraFeaturesValues(fullSentence, j, tag, features);
//                                        String rank = "1";
//                                        if (tag.equals(clitic.truthPOS)) {
//                                            rank = "2";
//                                        }
//
//                                        bwGN.write(rank + " qid:" + qid);
//                                        for (int k = 1; k <= features.size(); k++) {
//                                            if (features.get(k - 1) > -20) {
//                                                bwGN.write(" " + k + ":" + features.get(k - 1));
//                                            } else {
//                                                bwGN.write(" " + k + ":-10");
//                                            }
//                                        }
//                                        bwGN.write("\n");
//                                    }
//                                }
//                            }
//                            */
//                            // since is done for training and testing only
//                            // we set the guessPOS to equal the truthPOS
//                            clitic.guessPOS = clitic.truthPOS;
//                            qid++;
//                        }
//                    }
//                }
//
//                word.clear();
//                sentence.clear();
//                fullSentence.clear();
//            } else {
//                // sub part to word
//                String entry = entryToAdd(line).trim();
//                if (entry.length() > 0) {
//                    word.add(entry);
//                }
//            }
//        }
//        bw.close();
//        bwGN.close();
//    }
//    
//    private Sentence setAllGuessTagsToTruthTags (Sentence sentence)
//    {
//        for (int i = 0; i < sentence.clitics.size(); i++)
//            sentence.clitics.get(i).guessPOS = sentence.clitics.get(i).truthPOS;
//        return sentence;
//    }
//
//    public void generateGenderNumberTrainingFiles() throws IOException, ClassNotFoundException, FileNotFoundException, UnsupportedEncodingException, InterruptedException, Exception
//    {
//        gnt.generateTrainingFile("/Users/kareemdarwish/RESEARCH/ArabicProcessingTools-master/POSandNERData/truth.txt.train");
//    }
//    
//    public Sentence decodeSentence(Sentence fullSentence, ArrayList<Double> model)
//    {
//        if (fullSentence.clitics.size() > 0) {
//            for (int j = 0; j < fullSentence.clitics.size(); j++) {
//                Clitic clitic = fullSentence.clitics.get(j);
//                if (clitic.surface.equals("§§") || clitic.surface.equals("S") || clitic.surface.equals("E")) {
//                    // do nothing
//                } else {
//                    String bestTag = getBestTag(fullSentence, j, model);
//                    clitic.guessPOS = bestTag;
//                }
//            }
//        }
//        return fullSentence;
//    }
//    
//    private String getBestTag(Sentence fullSentence, int j, ArrayList<Double> model)
//    {
//        String bestTag = "";
//        Double bestTagScore = -100000000d;
//        ArrayList<Double> winningFeatures = new ArrayList<Double>();
//        ArrayList<ArrayList<Double>> agg = new ArrayList<ArrayList<Double>>();
//        for (String tag : possiblePOSTags(fullSentence, j)) {
//            ArrayList<Double> features = getFeaturesValues(fullSentence, j, tag);
//            double score = 0;
//            for (int fv = 0; fv < features.size(); fv++) {
//                score += features.get(fv) * model.get(fv);
//            }
//            agg.add(new ArrayList<Double>(features));
//            if (features.get(features.size() - 1) >= 1d) {
//                bestTagScore = 1000d;
//                bestTag = tag;
//                winningFeatures = new ArrayList<Double>(features);
//            } else if (bestTagScore < score) {
//                bestTagScore = score;
//                bestTag = tag;
//                winningFeatures = new ArrayList<Double>(features);
//            }
//        }
//        return bestTag;
//    }
//    
//    private void printToGuessFile(Sentence fullSentence, ArrayList<Double> model, BufferedWriter bw) throws IOException
//    {
//        if (fullSentence.clitics.size() > 0) {
//            for (int j = 0; j < fullSentence.clitics.size(); j++) {
//                Clitic clitic = fullSentence.clitics.get(j);
//                if (clitic.surface.equals("§§") || clitic.surface.equals("S") || clitic.surface.equals("E")) {
//                    // do nothing
//                } else {
//                    String gn = clitic.genderNumber;
//                    if (gn.trim().length() > 0)
//                        gn = "-" + gn;
//                    bw.write(clitic.surface + "\tT:" + clitic.truthPOS + "\tG:" + clitic.guessPOS + gn);
//                    if (!clitic.truthPOS.equals(clitic.guessPOS)) {
//                        bw.write("\t*");
//                    }
//                    if (clitic.truthPOS.equals(clitic.guessPOS)) {
//                        // do nothing
//                    } else {
//                        for (String tag : possiblePOSTags(fullSentence, j)) {
//                            ArrayList<Double> features = getFeaturesValues(fullSentence, j, tag);
//                            bw.write("\n" + tag + "\t");
//                            for (int l = 1; l <= features.size(); l++) {
//                                bw.write("\t" + l + ":" + features.get(l - 1));
//                            }
//                        }
//                    }
//                    bw.write("\n");
//                    bw.write("-----------------\n");
//                }
//            }
//        }
//    }
//    
//    public Sentence tagLine(ArrayList<String> input) throws InterruptedException, Exception
//    {
//        Sentence sentence = new Sentence();
//        
//        // load clitics into sentence
//        for (String word : input)
//        {
//            ArrayList<Clitic> wordParts = getWordParts2(word);
//            for(Clitic c : wordParts)
//            {
//                sentence.addClitic(c);
//            }
//
//        }
//        if (sentence.clitics.size() > 0)
//            sentence.addClitic(new Clitic("E", "E", new ArrayList<String>(), "E", "B", "E"));
//        
//        sentence = decodeSentence(sentence, model);
//        sentence = addGenderNumberFeatures(sentence);
//        
//        return sentence;
//    }
//    
//    public void generateSVMAndDecode(String filename) throws FileNotFoundException, IOException, InterruptedException, Exception {
//        BufferedReader br = ArabicUtils.openFileForReading(filename);
//        BufferedWriter bw = ArabicUtils.openFileForWriting(filename + ".guess");
//        Sentence fullSentence = new Sentence();
//        String line = "";
//
//        // feature values from training
////        String[] modelVals = "1:0.14491846 2:0.19861394 3:-0.025112482 4:-0.059626497 5:-0.043406118 6:0.0065765232 7:-0.043859985 8:0.0045893718 9:0.13653383 10:0.13349338 11:-0.053536385 12:0.0087099085 13:0.091892704 14:0.17454976 15:-0.01085883 16:-0.019442562 17:-0.0074988208 18:0.0040499675".split(" +");
////        ArrayList<Double> model = new ArrayList<Double>();
////        for (String s : modelVals) {
////            model.add(Double.parseDouble(s.substring(s.indexOf(":") + 1)));
////        }
//
//        // HashMap<String, Double> stats = new HashMap<String, Double>();
//        ArrayList<ArrayList<String>> sentence = new ArrayList<ArrayList<String>>();
//        ArrayList<String> word = new ArrayList<String>();
//        int qid = 1;
//        int lineNumber = 0;
//        while ((line = br.readLine()) != null) {
//            lineNumber++;
//            line = line.trim();
//            if (line.endsWith("\tO")) {
//                if (word.size() > 0) {
//                    word = fixPotentialSegmentationMismatch(word);
//                    fullSentence.addWord(getWordRepresentation(word));
//                    sentence.add(new ArrayList<String>(word));
//                }
//                word.clear();
//            } else if (line.length() == 0) {
//                if (word.size() > 0) {
//                    word = fixPotentialSegmentationMismatch(word);
//                    fullSentence.addWord(getWordRepresentation(word));
//                    sentence.add(new ArrayList<String>(word));
//                }
//                if (fullSentence.clitics.size() > 0) // put end of sentence marker
//                {
//                    fullSentence.addClitic(new Clitic("E", "E", new ArrayList<String>(), "E", "B", "E"));
//                }
//                // check if we have a word
//                if (fullSentence.clitics.size() > 0) {
//                    fullSentence = decodeSentence(fullSentence, model);
//                    fullSentence = addGenderNumberFeatures(fullSentence);
//                    printToGuessFile(fullSentence, model, bw);
//                    word.clear();
//                    sentence.clear();
//                    fullSentence.clear();
//                }
//            } else {
//                // sub part to word
//                String entry = entryToAdd(line).trim();
//                if (entry.length() > 0) {
//                    word.add(entry);
//                }
//            }
//        }
//        bw.close();
//    }
//
//    private Sentence addGenderNumberFeatures(Sentence sentence) throws ClassNotFoundException, InterruptedException, Exception // this adds the gender and number features
//    {
//        for (int j = 0; j < sentence.clitics.size(); j++)
//        {
//            Clitic clitic = sentence.clitics.get(j);
//            if (clitic.guessPOS.equals("NOUN") || clitic.guessPOS.equals("ADJ") || clitic.guessPOS.equals("NUM")) // guess gender and number attributes
//            {
//                // get suffixes that are nsuff only
//                String suffix = "";
//                String POS = clitic.guessPOS;
//                if (POS.equals("ADJ"))
//                    POS = "NOUN";
//                for (int k = j+1; k < sentence.clitics.size() && !sentence.clitics.get(k).position.equals("B"); k++)
//                {
//                    if (sentence.clitics.get(k).guessPOS.contains("NSUFF"))
//                        suffix += "+" + sentence.clitics.get(k).surface;
//                }
//                if (suffix.trim().length() > 0)
//                    clitic.genderNumber = gnt.getGenderTagRandomForest(clitic.surface + "+" + suffix.replace("+", ""), POS + "+NSUFF", clitic.template, suffix.replace("+", ""));
//                else
//                    clitic.genderNumber = gnt.getGenderTagRandomForest(clitic.surface, POS, clitic.template, "#");
//            }
//        }
//        return sentence;
//    }
//    
//    private String entryToAdd(String line) {
//        String output = "";
//        line = line.replace("VSUFF", "PRON");
//        if (line.trim().startsWith("تين\t") && line.trim().endsWith("NSUFF")) {
//            output = "ت\tY\tNOT\tNSUFF";
//            output = "ين\tY\tNOT\tNSUFF";
//        } else if (line.trim().startsWith("تان\t") && line.trim().endsWith("NSUFF")) {
//            output = "ت\tY\tNOT\tNSUFF";
//            output = "ان\tY\tNOT\tNSUFF";
//        } else if (line.trim().startsWith("تي\t") && line.trim().endsWith("NSUFF")) {
//            output = "ت\tY\tNOT\tNSUFF";
//            output = "ي\tY\tNOT\tNSUFF";
//        } else if (!line.contains("(نلل)") && line.trim().length() > 0) {
//            output = line.replace("[ \t]+.*[ \t]+", "\t");
//        }
//        return output;
//    }
//
//    private ArrayList<Double> getFeaturesValues(Sentence sentence, int j, String tag) {
//        Clitic clitic = sentence.clitics.get(j);
//        ArrayList<Integer> previousClitic = getSurroundingNClitics(sentence, j, -1);
//        ArrayList<Integer> nextClitic = getSurroundingNClitics(sentence, j, 1);
//        String prevPrefix = getPreviousWordPrefix(sentence, j);
//        String headPrefixes = getCurrentWordPrefix(sentence, j);
//        String tailSuffixes = getCurrentWordSuffix(sentence, j);
//        String prevSuffixes = getPreviousWordSuffix(sentence, j);
//        ArrayList<Integer> prev2Clitics = getSurroundingNClitics(sentence, j, -2);
//        ArrayList<Integer> next2Clitics = getSurroundingNClitics(sentence, j, 2);
//        ArrayList<Integer> prev3Clitics = getSurroundingNClitics(sentence, j, -3);
//        ArrayList<Integer> next3Clitics = getSurroundingNClitics(sentence, j, 3);
//        ArrayList<Integer> prev4Clitics = getSurroundingNClitics(sentence, j, -4);
//        ArrayList<Integer> next4Clitics = getSurroundingNClitics(sentence, j, 4);
//        ArrayList<Double> features = new ArrayList<Double>();
//        double highCondProb = 0d;
//        double threshold = -0.025d;
//        // POS given word prob
//        if (hmPosGivenWord.containsKey(clitic.surface + "\t" + tag) && hmPosGivenWord.get(clitic.surface + "\t" + tag) > -4d) {
//            features.add(hmPosGivenWord.get(clitic.surface + "\t" + tag));
//            if (hmPosGivenWord.get(clitic.surface + "\t" + tag) > threshold) {
//                highCondProb++;
//            }
//        } else {
//            features.add(-10d);
//        }
//        
//        // Word given POS prob
//        if (hmWordGivenPos.containsKey(clitic.surface + "\t" + tag)) {
//            features.add(hmWordGivenPos.get(clitic.surface + "\t" + tag));
//        } else {
//            features.add(-10d);
//        }
//
//        // get template for stem
//        if (!clitic.surface.contains("+")) {
//            // POS given template
//            if (hmPosGivenTemplate.containsKey(clitic.template + "\t" + tag) && hmPosGivenTemplate.get(clitic.template + "\t" + tag) > -4d) {
//                features.add(hmPosGivenTemplate.get(clitic.template + "\t" + tag));
//                if (hmPosGivenTemplate.get(clitic.template + "\t" + tag) > threshold) {
//                    highCondProb++;
//                }
//            } else {
//                features.add(-10d);
//            }
//            // POS given prefixex
//            if (hmPosGivenPrefix.containsKey(headPrefixes + "\t" + tag)) {
//                features.add(hmPosGivenPrefix.get(headPrefixes + "\t" + tag));
//                if (hmPosGivenPrefix.get(headPrefixes + "\t" + tag) > threshold) {
//                    highCondProb++;
//                }
//            } else {
//                features.add(-10d);
//            }
//
//            // POS given first suffix
//            if (hmPosGivenSuffix.containsKey(tailSuffixes + "\t" + tag)) {
//                features.add(hmPosGivenSuffix.get(tailSuffixes + "\t" + tag));
//                if (hmPosGivenSuffix.get(tailSuffixes + "\t" + tag) > threshold) {
//                    highCondProb++;
//                }
//            } else {
//                features.add(-10d);
//            }
//
//            // POS given prev & curr Prefixes
//            String prevAndCurrentPrefixes = headPrefixes + "-" + prevPrefix;
//            if (hmPosGivenPrevPrefixandCurrentPrefix.containsKey(prevAndCurrentPrefixes + "\t" + tag)) {
//                features.add(hmPosGivenPrevPrefixandCurrentPrefix.get(prevAndCurrentPrefixes + "\t" + tag));
//                if (hmPosGivenPrevPrefixandCurrentPrefix.get(prevAndCurrentPrefixes + "\t" + tag) > threshold) {
//                    highCondProb++;
//                }
//            } else {
//                features.add(-10d);
//            }
//            
//            // POS given prev suffix
//            if (hmPosGivenPrevWordSuffix.containsKey(prevSuffixes + "\t" + tag))
//            {
//                features.add(hmPosGivenPrevWordSuffix.get(prevSuffixes + "\t" + tag));
//            }
//            else
//            {
//                features.add(-10d);
//            }
//            
//            // POS given prev word
//            ArrayList<Integer> previousWordClitics = getPreviousWordClitics(sentence, j);
//            String previousWordCombinedPOS = "";
//            for (int ip : previousWordClitics)
//                previousWordCombinedPOS = sentence.clitics.get(ip).guessPOS + " " + previousWordCombinedPOS;
//            previousWordCombinedPOS = previousWordCombinedPOS.trim();
//            if (hmPosGivenPrevWordPOS.containsKey(previousWordCombinedPOS + "\t" + tag))
//                features.add(hmPosGivenPrevWordPOS.get(previousWordCombinedPOS + "\t" + tag));
//            else
//                features.add(-10d);
//            
//        } else {
//            // add dummy entries for template, prefix and suffix
//            features.add(-10d);
//            features.add(-10d);
//            features.add(-10d);
//            features.add(-10d);
//            features.add(-10d);
//            features.add(-10d);
//        }
//        /**
//        if (hmPosGivenPrevWord.containsKey(sentence.clitics.get(previousClitic.get(0)).surface + "\t" + tag)) {
//            features.add(hmPosGivenPrevWord.get(sentence.clitics.get(previousClitic.get(0)).surface + "\t" + tag));
//        } else {
//            features.add(-10d);
//        }
//
//        if (hmPosGivenNextWord.containsKey(sentence.clitics.get(nextClitic.get(0)).surface + "\t" + tag)) {
//            features.add(hmPosGivenNextWord.get(sentence.clitics.get(nextClitic.get(0)).surface + "\t" + tag));
//        } else {
//            features.add(-10d);
//        }
//        */
//
//        /*        
//         if (hmPosGivenPrev2Words.containsKey(prev2Clitics + "\t" + tag))
//         features.add(hmPosGivenPrev2Words.get(prev2Clitics + "\t" + tag));
//         else
//         features.add(-10d);
//        
//         if (hmPosGivenNext2Words.containsKey(next2Clitics + "\t" + tag))
//         features.add(hmPosGivenNext2Words.get(next2Clitics + "\t" + tag));
//         else
//         features.add(-10d);
//       
//         if (hmPosGivenPrev3Words.containsKey(prev3Clitics + "\t" + tag))
//         features.add(hmPosGivenPrev3Words.get(prev3Clitics + "\t" + tag));
//         else
//         features.add(-10d);
//        
//         if (hmPosGivenNext3Words.containsKey(next3Clitics + "\t" + tag))
//         features.add(hmPosGivenNext3Words.get(next3Clitics + "\t" + tag));
//         else
//         features.add(-10d);
//         */
//        /*
//         String metaType = getMetaType(clitics.get(i));
//         if (hmPosGivenMetaType.containsKey(metaType + "\t" + tag))
//         features.add(hmPosGivenMetaType.get(metaType + "\t" + tag));
//         else
//         features.add(-10d);
//         */
//        double score = 0d;
//        // get POS given previous POS
//        // add a feature for every tag -- basically we assume that previous word can take any tag
//        // then we compute the probability(current tag | previous tag) * probability (previous tag | previous word)
//        // for (String tagPrev : possiblePOSTags(sentence, previousClitic.get(0))) {
//        //    String PosTagSequence = tagPrev + "\t" + tag;
//            String PosTagSequence = sentence.clitics.get(previousClitic.get(0)).guessPOS + "\t" + tag;
//            if (hmPosGivenPos.containsKey(PosTagSequence)) {
//                score += hmPosGivenPos.get(PosTagSequence);
//            }
//        //}
//        if (score == 0) {
//            score = -10d;
//        }
//        features.add(score);
//
//        // score with prev 2 POS
//        score = 0d;
//        // String[] prevWords = prev2Clitics.split(" +");
//        if (prev2Clitics.size() == 2) {
//            String tag0 = sentence.clitics.get(prev2Clitics.get(0)).guessPOS;
//            String tag1 = sentence.clitics.get(prev2Clitics.get(1)).guessPOS;
//            //for (String tag0 : possiblePOSTags(sentence, prev2Clitics.get(0))) {
//            //    for (String tag1 : possiblePOSTags(sentence, prev2Clitics.get(1))) {
//                    // String PosTagSequence = tag0 + " " + tag1 + "\t" + tag;
//            PosTagSequence = tag0 + " " + tag1 + "\t" + tag;
//            if (hmPosGivenPrev2Pos.containsKey(PosTagSequence)) {
//                score += hmPosGivenPrev2Pos.get(PosTagSequence);
//            }
//            //    }
//            //}
//        }
//        else
//        {
//            PosTagSequence = sentence.clitics.get(previousClitic.get(0)).guessPOS + "\t" + tag;
//            if (hmPosGivenPos.containsKey(PosTagSequence)) {
//                score += hmPosGivenPos.get(PosTagSequence);
//            }
//        }
//        if (score == 0) {
//            score = -10d;
//        }
//        features.add(score);
//        
//        // new Features Nov. 14, 2016
//        String key = clitic.surface + " " + sentence.clitics.get(nextClitic.get(0)).surface + "\t" + tag;
//        if (hmPosGivenCurrentWordNextWord.containsKey(key))
//            features.add(hmPosGivenCurrentWordNextWord.get(key));
//        else
//            features.add(-10d);
//        key = clitic.surface + " " + sentence.clitics.get(previousClitic.get(0)).surface + "\t" + tag;
//        if (hmPosGivenCurrentWordPrevWord.containsKey(key))
//            features.add(hmPosGivenCurrentWordPrevWord.get(key));
//        else
//            features.add(-10d);
//        /**
//        ArrayList<Integer> Positions = getSurroundingNClitics(sentence, j, -2);
//        if (Positions.size() == 2) {
//            key = clitic.surface + " " + sentence.clitics.get(Positions.get(0)).surface + " " + sentence.clitics.get(Positions.get(1)).surface + "\t" + tag;
//            if (hmPosGivenCurrentWord2NextWord.containsKey(key)) {
//                features.add(hmPosGivenCurrentWord2NextWord.get(key));
//            } else {
//                features.add(-10d);
//            }
//        } else {
//            features.add(-10d);
//        }
//        Positions = getSurroundingNClitics(sentence, j, 2);
//        if (Positions.size() == 2) {
//            key = clitic.surface + " " + sentence.clitics.get(Positions.get(0)).surface + " " + sentence.clitics.get(Positions.get(1)).surface + "\t" + tag;
//            if (hmPosGivenCurrentWord2PrevWord.containsKey(key)) {
//                features.add(hmPosGivenCurrentWord2PrevWord.get(key));
//            } else {
//                features.add(-10d);
//            }
//        } else {
//            features.add(-10d);
//        }
//        */
//        // score with next 3 POS
//        score = 0d;
//
//
//        
////        score = 0d;
////        // String[] nextWords = next2Clitics.split(" +");
////        if (next2Clitics.size() == 2) {
////            for (String tag0 : possiblePOSTags(sentence, next2Clitics.get(0))) {
////                for (String tag1 : possiblePOSTags(sentence, next2Clitics.get(1))) {
////                    // String PosTagSequence = tag0 + " " + tag1 + "\t" + tag;
////                    PosTagSequence = tag0 + " " + tag1 + "\t" + tag;
////                    if (hmPosGivenNext2Pos.containsKey(PosTagSequence)) {
////                        score += hmPosGivenNext2Pos.get(PosTagSequence);
////                    }
////                }
////            }
////        }
////        
////        if (score == 0) {
////            score = -10d;
////        }
////        features.add(score);
//
//        // score with POS trigrams
//        
//        score = 0d;
//        // prevWords = prev3Clitics.split(" +");
//        if (prev3Clitics.size() == 3) {
//            String tag0 = sentence.clitics.get(prev3Clitics.get(0)).guessPOS;
//            String tag1 = sentence.clitics.get(prev3Clitics.get(1)).guessPOS;
//            String tag2 = sentence.clitics.get(prev3Clitics.get(2)).guessPOS;
//            //for (String tag0 : possiblePOSTags(sentence, prev3Clitics.get(0))) {
//            //    for (String tag1 : possiblePOSTags(sentence, prev3Clitics.get(1))) {
//            //        for (String tag2 : possiblePOSTags(sentence, prev3Clitics.get(2))) {
//                        PosTagSequence = tag0 + " " + tag1 + " " + tag2 + "\t" + tag;
//                        if (hmPosGivenPrev3Pos.containsKey(PosTagSequence)) {
//                            score += hmPosGivenPrev3Pos.get(PosTagSequence);
//                        }
//            //        }
//            //    }
//            //}
//        }
//        else if (prev2Clitics.size() == 2) {
//            String tag0 = sentence.clitics.get(prev2Clitics.get(0)).guessPOS;
//            String tag1 = sentence.clitics.get(prev2Clitics.get(1)).guessPOS;
//            //for (String tag0 : possiblePOSTags(sentence, prev2Clitics.get(0))) {
//            //    for (String tag1 : possiblePOSTags(sentence, prev2Clitics.get(1))) {
//                    // String PosTagSequence = tag0 + " " + tag1 + "\t" + tag;
//            PosTagSequence = tag0 + " " + tag1 + "\t" + tag;
//            if (hmPosGivenPrev2Pos.containsKey(PosTagSequence)) {
//                score += hmPosGivenPrev2Pos.get(PosTagSequence);
//            }
//            //    }
//            //}
//        }
//        else
//        {
//            PosTagSequence = sentence.clitics.get(previousClitic.get(0)).guessPOS + "\t" + tag;
//            if (hmPosGivenPos.containsKey(PosTagSequence)) {
//                score += hmPosGivenPos.get(PosTagSequence);
//            }
//        }
//        
//        if (score == 0) {
//            score = -10d;
//        }
//        features.add(score);
//        
//        score = 0d;
//        // prevWords = prev3Clitics.split(" +");
//        if (prev4Clitics.size() == 4) {
//            String tag0 = sentence.clitics.get(prev4Clitics.get(0)).guessPOS;
//            String tag1 = sentence.clitics.get(prev4Clitics.get(1)).guessPOS;
//            String tag2 = sentence.clitics.get(prev4Clitics.get(2)).guessPOS;
//            String tag3 = sentence.clitics.get(prev4Clitics.get(3)).guessPOS;
//            PosTagSequence = tag0 + " " + tag1 + " " + tag2 + " " + tag3 + "\t" + tag;
//            if (hmPosGivenPrev4Pos.containsKey(PosTagSequence)) {
//                score += hmPosGivenPrev4Pos.get(PosTagSequence);
//            }
//        }
//        else if (prev3Clitics.size() == 3) {
//            String tag0 = sentence.clitics.get(prev3Clitics.get(0)).guessPOS;
//            String tag1 = sentence.clitics.get(prev3Clitics.get(1)).guessPOS;
//            String tag2 = sentence.clitics.get(prev3Clitics.get(2)).guessPOS;
//            PosTagSequence = tag0 + " " + tag1 + " " + tag2 + "\t" + tag;
//            if (hmPosGivenPrev3Pos.containsKey(PosTagSequence)) {
//                score += hmPosGivenPrev3Pos.get(PosTagSequence);
//            }
//        }
//        else if (prev2Clitics.size() == 2) {
//            String tag0 = sentence.clitics.get(prev2Clitics.get(0)).guessPOS;
//            String tag1 = sentence.clitics.get(prev2Clitics.get(1)).guessPOS;
//            PosTagSequence = tag0 + " " + tag1 + "\t" + tag;
//            if (hmPosGivenPrev2Pos.containsKey(PosTagSequence)) {
//                score += hmPosGivenPrev2Pos.get(PosTagSequence);
//            }
//        }
//        else
//        {
//            PosTagSequence = sentence.clitics.get(previousClitic.get(0)).guessPOS + "\t" + tag;
//            if (hmPosGivenPos.containsKey(PosTagSequence)) {
//                score += hmPosGivenPos.get(PosTagSequence);
//            }
//        }
//        
//        if (score == 0) {
//            score = -10d;
//        }
//        features.add(score);
//        
//        
//        
////        score = 0d;
////        
////        String prev3Template = "";
////        for (int cnt = 0; cnt < prev3Clitics.size(); cnt++)
////        {
////            if (sentence.clitics.get(prev3Clitics.get(cnt)).surface.endsWith("+") || sentence.clitics.get(prev3Clitics.get(cnt)).surface.startsWith("+"))
////                prev3Template += " " + sentence.clitics.get(prev3Clitics.get(cnt)).guessPOS;
////            else
////                prev3Template += " " + sentence.clitics.get(prev3Clitics.get(cnt)).template;
////        }
////        prev3Template = prev3Template.trim();
////        if (hmPosGivenPrev3Template.containsKey(prev3Template + "\t" + clitic.guessPOS))
////            score = hmPosGivenPrev3Template.get(prev3Template + "\t" + clitic.guessPOS);
////        else
////            score = -10d;
////        features.add(score);
//        /*
//        score = 0d;
//        // nextWords = next3Clitics.split(" +");
//        if (next3Clitics.size() == 3) {
//            for (String tag0 : possiblePOSTags(sentence, next3Clitics.get(0))) {
//                for (String tag1 : possiblePOSTags(sentence, next3Clitics.get(1))) {
//                    for (String tag2 : possiblePOSTags(sentence, next3Clitics.get(2))) {
//                        String PosTagSequence = tag0 + " " + tag1 + " " + tag2 + "\t" + tag;
//                        if (hmPosGivenNext3Pos.containsKey(PosTagSequence)) {
//                            score += hmPosGivenNext3Pos.get(PosTagSequence);
//                        }
//                    }
//                }
//            }
//        }
//        if (score == 0) {
//            score = -10d;
//        }
//        features.add(score);
//        */
//        
//        // prev and next POS tags
//        score = 0d;
//        // for (String tag0 : possiblePOSTags(sentence, previousClitic.get(0))) {
//        String tag0 = sentence.clitics.get(previousClitic.get(0)).guessPOS;
//        for (String tag1 : possiblePOSTags(sentence, nextClitic.get(0))) {
//            PosTagSequence = tag0 + " " + tag1 + "\t" + tag;
//            if (hmPosGivenContext1Pos.containsKey(PosTagSequence)) {
//                score += hmPosGivenContext1Pos.get(PosTagSequence);
//            }
//        }
//        if (score == 0) {
//            score = -10d;
//        }
//        features.add(score);
//
//        score = 0d;
//        // nextWords = next3Clitics.split(" +");
//        if (next2Clitics.size() == 2) {
//            for (String tagA : possiblePOSTags(sentence, next2Clitics.get(0))) {
//                for (String tagB : possiblePOSTags(sentence, next2Clitics.get(1))) {
//                    PosTagSequence = tagA + " " + tagB + "\t" + tag;
//                    if (hmPosGivenNext2Pos.containsKey(PosTagSequence)) {
//                        score += hmPosGivenNext2Pos.get(PosTagSequence);
//                    }
//                }
//            }
//        }
//        if (score == 0) {
//            score = -10d;
//        }
//        features.add(score);
//        
//        score = 0d;
//        // nextWords = next3Clitics.split(" +");
//        if (next3Clitics.size() == 3) {
//            for (String tagA : possiblePOSTags(sentence, next3Clitics.get(0))) {
//                for (String tagB : possiblePOSTags(sentence, next3Clitics.get(1))) {
//                    for (String tagC : possiblePOSTags(sentence, next3Clitics.get(2))) {
//                        PosTagSequence = tagA + " " + tagB + " " + tagC + "\t" + tag;
//                        if (hmPosGivenNext3Pos.containsKey(PosTagSequence)) {
//                            score += hmPosGivenNext3Pos.get(PosTagSequence);
//                        }
//                    }
//                }
//            }
//        }
//        else if (next2Clitics.size() == 2) {
//            for (String tagA : possiblePOSTags(sentence, next2Clitics.get(0))) {
//                for (String tagB : possiblePOSTags(sentence, next2Clitics.get(1))) {
//                    PosTagSequence = tagA + " " + tagB + "\t" + tag;
//                    if (hmPosGivenNext2Pos.containsKey(PosTagSequence)) {
//                        score += hmPosGivenNext2Pos.get(PosTagSequence);
//                    }
//                }
//            }
//        }
//        if (score == 0) {
//            score = -10d;
//        }
//        features.add(score);
///*        
//        score = 0d;
//        // nextWords = next3Clitics.split(" +");
//        if (next4Clitics.size() == 4) {
//            for (String tagA : possiblePOSTags(sentence, next4Clitics.get(0))) {
//                for (String tagB : possiblePOSTags(sentence, next4Clitics.get(1))) {
//                    for (String tagC : possiblePOSTags(sentence, next4Clitics.get(2))) {
//                        for (String tagD : possiblePOSTags(sentence, next4Clitics.get(3))) {
//                            PosTagSequence = tagA + " " + tagB + " " + tagC + " " + tagD + "\t" + tag;
//                            if (hmPosGivenNext3Pos.containsKey(PosTagSequence)) {
//                                score += hmPosGivenNext3Pos.get(PosTagSequence);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        else if (next3Clitics.size() == 3) {
//            for (String tagA : possiblePOSTags(sentence, next3Clitics.get(0))) {
//                for (String tagB : possiblePOSTags(sentence, next3Clitics.get(1))) {
//                    for (String tagC : possiblePOSTags(sentence, next3Clitics.get(2))) {
//                        PosTagSequence = tagA + " " + tagB + " " + tagC + "\t" + tag;
//                        if (hmPosGivenNext3Pos.containsKey(PosTagSequence)) {
//                            score += hmPosGivenNext3Pos.get(PosTagSequence);
//                        }
//                    }
//                }
//            }
//        }
//        else if (next2Clitics.size() == 2) {
//            for (String tagA : possiblePOSTags(sentence, next2Clitics.get(0))) {
//                for (String tagB : possiblePOSTags(sentence, next2Clitics.get(1))) {
//                    PosTagSequence = tagA + " " + tagB + "\t" + tag;
//                    if (hmPosGivenNext2Pos.containsKey(PosTagSequence)) {
//                        score += hmPosGivenNext2Pos.get(PosTagSequence);
//                    }
//                }
//            }
//        }
//        if (score == 0) {
//            score = -10d;
//        }
//        features.add(score);
//*/        
//        
////        } else if (score > 0) {
////            score = Math.log(score);
////        }
//        // features.add(score);
//        if (highCondProb > 0) {
//            features.add(1 + Math.log(highCondProb));
//        } else {
//            features.add(0d);
//        }
//
//        return features;
//    }
//    
//    private ArrayList<Double> getExtraFeaturesValues(Sentence sentence, int j, String tag, ArrayList<Double> features) {
//        /*
//         * 
//         * There was hope that adding the gender and number features would improve POS tagging
//         * but unfortunately, it kept results the same when used causiously
//         * when used extensively, it made things significantly worse.
//         * 
//         * THIS IS DEPRECATED
//         * 
//         *
//        Clitic clitic = sentence.clitics.get(j);
//        ArrayList<Integer> prev4Clitics = getSurroundingNClitics(sentence, j, -3);
//        ArrayList<Integer> next3Clitics = getSurroundingNClitics(sentence, j, 1);
//
//        // get surrounding POS tags
//        String posWithGenderNumber = "";
//        for (int pwgn = 0; pwgn < prev4Clitics.size(); pwgn++)
//        {
//            String tmpPOS = sentence.clitics.get(prev4Clitics.get(pwgn)).guessPOS;
//            if (sentence.clitics.get(prev4Clitics.get(pwgn)).genderNumber.trim().length() > 0)
//                tmpPOS = sentence.clitics.get(prev4Clitics.get(pwgn)).genderNumber;
//            posWithGenderNumber += tmpPOS + " ";
//        }
////        posWithGenderNumber += ",";
////        for (int pwgn = 0; pwgn < next3Clitics.size(); pwgn++)
////        {
////            String tmpPOS = sentence.clitics.get(next3Clitics.get(pwgn)).guessPOS;
////            if (sentence.clitics.get(next3Clitics.get(pwgn)).genderNumber.trim().length() > 0)
////                tmpPOS = sentence.clitics.get(next3Clitics.get(pwgn)).genderNumber;
////            posWithGenderNumber += " " + tmpPOS;
////        }
//        posWithGenderNumber = posWithGenderNumber.trim();
//        
//        if (hmPosGivenContextPOSwithGenderNumberTags.containsKey(posWithGenderNumber + "\t" + tag))
//            features.add(hmPosGivenContextPOSwithGenderNumberTags.get(posWithGenderNumber + "\t" + tag));
//        else
//            features.add(-10d);
//            */
//        return features;
//    }
//
//    private String getPreviousClitic(ArrayList<ArrayList<String>> sentence, ArrayList<String> clitics, int i, int j) {
//        String previousClitic = "";
//        if (i == 0) // first clitic in the word
//        {
//            if (j == 0) // first word in the sentence
//            {
//                previousClitic = "S";
//            } else {
//                ArrayList<String> tmp = getWordParts(sentence.get(j - 1));
//                previousClitic = tmp.get(tmp.size() - 1);
//            }
//        } else {
//            previousClitic = clitics.get(i - 1);
//        }
//        return previousClitic;
//    }
//
//    private String getPreviousWordPrefix(ArrayList<ArrayList<String>> sentence, ArrayList<String> clitics, int i, int j) {
//        String prevPrefix = "";
//        if (j == 0) // first word in sentence
//        {
//            prevPrefix = "S";
//        } else {
//            prevPrefix = getWordPrefix(sentence.get(j - 1));
//            if (prevPrefix.equals("")) {
//                prevPrefix = "#";
//            }
//        }
//        return prevPrefix;
//    }
//
//    private String getNextClitic(ArrayList<ArrayList<String>> sentence, ArrayList<String> clitics, int i, int j) {
//        String nextClitic = "";
//        if (i == clitics.size() - 1) // last clitic in the word
//        {
//            if (j == sentence.size() - 1) // last word in the sentence
//            {
//                nextClitic = "E";
//            } else {
//                ArrayList<String> tmp = getWordParts(sentence.get(j + 1));
//                nextClitic = tmp.get(0);
//            }
//        } else {
//            nextClitic = clitics.get(i + 1);
//        }
//        return nextClitic;
//    }
//
//    private ArrayList<String> possiblePOSTags(Sentence sentence, int j) {
//        ArrayList<String> output = new ArrayList<String>();
//        Clitic clitic = sentence.clitics.get(j);
//        if (clitic.surface.equals("S")) {
//            output.add("S");
//            return output;
//        }
//        
//        double highestProb = -10000d;
//        
//        for (String p : hmPos.keySet()) {
//
//            if (hmPosGivenWord.containsKey(clitic.surface + "\t" + p)) {
//                output.add(p);
//                if (highestProb < hmPosGivenWord.get(clitic.surface + "\t" + p))
//                    highestProb = hmPosGivenWord.get(clitic.surface + "\t" + p);
//            }
//        }
//        
//        // trim low probabilities
//        for (String p : new ArrayList<String>(output))
//        {
//            if (hmPosGivenWord.get(clitic.surface + "\t" + p) - highestProb < -3)
//            {
//                output.remove(p);
//            }
//        }
//        
//        if (output.size() == 0 || hmWord.get(clitic.surface) < 2) {
//            output.clear();
//            /*
//             ABBREV
//             NUM
//             FOREIGN
//             PUNC
//             */
//            String metaTag = getMetaType(clitic.surface);
//            if (clitic.surface.equals("§§")) {
//                output.add("§");
//            } else if (metaTag.equals("NUM")) {
//                output.add("NUM");
//            } else if (metaTag.equals("PUNC")) {
//                output.add("PUNC");
//            } else if (metaTag.equals("FOREIGN")) {
//                output.add("FOREIGN");
//            } else {
//                output.addAll(hmPosNormal.keySet());
//                if (clitic.surface.length() <= 2 && !clitic.surface.contains("+")) {
//                    output.add("ABBREV");
//                }
//            }
//
//        }
//        
//        ArrayList<String> tmp = new ArrayList<String>();
//        if (clitic.surface.startsWith("+"))
//        {
//            for (String t : output) {
//                if (hPossibleSuffix.containsKey(t.trim()))
//                    tmp.add(t.trim());
//            }
//        }
//        else if (clitic.surface.endsWith("+"))
//        {
//            for (String t : output) {
//                if (hPossiblePrefix.containsKey(t.trim()))
//                    tmp.add(t.trim());
//            }
//        }
//        else
//        {
//            for (String t : output) {
//                boolean addTag = true;
//                boolean extremeCase = true;
//                if (!hmPosGivenTemplate.containsKey(clitic.template + "\t" + t.trim())) // check if tag can have this template
//                    addTag = false;
//                else if (hmPosGivenTemplate.get(clitic.template + "\t" + t.trim()) < -5) // check if the prob of tag given template is high enough
//                    addTag = false;
//                else if (!hmPosGivenPrefix.containsKey(getCurrentWordPrefix(sentence, j) + "\t" + t.trim()))
//                    addTag = false;
//                else if (hmPosGivenPrefix.get(getCurrentWordPrefix(sentence, j) + "\t" + t.trim()) < -5)
//                    addTag = false;
//                else if (!hmPosGivenSuffix.containsKey(getCurrentWordSuffix(sentence, j) + "\t" + t.trim()))
//                {
//                    addTag = false;
//                    extremeCase = false;
//                }
//                else if (hmPosGivenSuffix.get(getCurrentWordSuffix(sentence, j) + "\t" + t.trim()) < -5)
//                    addTag = false;
//                else if (!hmPosGivenPos.containsKey(sentence.clitics.get(j-1).guessPOS + "\t" + t.trim()))
//                {
//                    addTag = false;
//                    extremeCase = false;
//                }
// 
//                // heuristic to prohibit an ADJ without a NOUN/NUM/ADJ before it
////                if (t.trim().equals("ADJ"))
////                {
////                    ArrayList<String> previousWordPOS = new ArrayList<String>();
////                    int prevWordPos = j;
////                    for (int pwc : getPreviousWordClitics(sentence, j))
////                    {
////                        previousWordPOS.add(sentence.clitics.get(pwc).guessPOS);
////                        prevWordPos = pwc;
////                    }   
////                    if (previousWordPOS.contains("PUNC"))
////                    {
////                    previousWordPOS.clear();
////                    for (int pwc : getPreviousWordClitics(sentence, prevWordPos))
////                    {
////                        previousWordPOS.add(sentence.clitics.get(pwc).guessPOS);
////                    }   
////                    }
////                    
////                    if (previousWordPOS.contains("V"))
//////                            !previousWordPOS.contains("NOUN") 
//////                            && !previousWordPOS.contains("NUM") 
//////                            && !previousWordPOS.contains("ADJ"))
////                    {
////                        extremeCase = false;
////                    }
////                }
//                
//                
//                if (addTag == false 
//                        && hmPosGivenWord.containsKey(clitic.surface + "\t" + t.trim()) 
//                        && hmPosGivenWord.get(clitic.surface + "\t" + t.trim()) > -2.3)
//                    addTag = true;
//                
//                if (addTag && extremeCase)
//                tmp.add(t.trim());
//            }
//        }
//        
//        if (tmp.size() == 0)
//        {
//            for (String t : output) {
//                if (clitic.surface.trim().startsWith("+") && !hPossibleSuffix.containsKey(t.trim())) {
//                    // do nothing
//                } else if (clitic.surface.trim().endsWith("+") && !hPossiblePrefix.containsKey(t.trim())) {
//                    // do nothing
//                } else {
//                    tmp.add(t.trim());
//                }
//            }
//        }
//        if (clitic.surface.equals("+ت") && j > 0 && sentence.clitics.get(j-1).guessPOS.contains("NOUN") 
//                && tmp.contains("PRON") && j < sentence.clitics.size() - 1 && sentence.clitics.get(j+1).surface.startsWith("+"))
//            tmp.remove("PRON");
//        
////        if (tmp.contains("NOUN") && !tmp.contains("ADJ") && hmMada.containsKey(clitic.surface) && hmMada.get(clitic.surface).equals("ADJ"))
////            tmp.add("ADJ");
////        if (tmp.contains("ADJ") && !tmp.contains("NOUN") && hmMada.containsKey(clitic.surface) && hmMada.get(clitic.surface).equals("NOUN"))
////            tmp.add("NOUN");
//        
////        if (hmMada.containsKey(clitic.surface))
////        {
////            if (tmp.size() == 2 && tmp.contains("NOUN") && tmp.contains("ADJ")) // tmp.contains(hmMada.get(clitic.surface)))
////                tmp = new ArrayList<String>();
////            tmp.add(hmMada.get(clitic.surface));
////        }
//        
//        return tmp;
//    }
//
//    private String getStemFromWordParts(ArrayList<String> segmentedWord) {
//        String output = "";
//        for (String s : segmentedWord) {
//            if (!s.contains("+")) {
//                output = s;
//            }
//        }
//        return output;
//    }
//
//    private ArrayList<String> getWordParts(String s) {
//        ArrayList<String> output = new ArrayList<String>();
//        String segmentedWord = s;
//
//        segmentedWord = getProperSegmentation(segmentedWord);
//        String[] parts = (" " + segmentedWord + " ").split(";");
//        for (String p : parts[0].split("\\+")) {
//            if (p.trim().length() > 0) {
//                output.add(p + "+");
//            }
//        }
//        if (parts[1].trim().length() > 0) {
//            output.add(parts[1].trim());
//        }
//        for (String p : parts[2].split("\\+")) {
//            if (p.trim().length() > 0) {
//                output.add("+" + p);
//            }
//        }
//        return output;
//    }
//
//    private ArrayList<Clitic> getWordParts2(String s) {
//        //this method has been added by Mohamed Eldesouki
//        //it does the same thing as method getWordParts but
//        //the main reason for creating it is to get rid of
//        // semicolon as away of separating the stem from affixes of the word
//        // this method is only used by the method tagLine
//        ArrayList<Clitic> output = new ArrayList<Clitic>();
//        String segmentedWord = s;
//
//        segmentedWord = farasa.getProperSegmentation(segmentedWord);
//        String[] parts = (" " + segmentedWord + " ").split(";");
//        String position = "B";
//        for (String p : parts[0].split("\\+")) {
//            if (p.trim().length() > 0) {
//                Clitic clitic = new Clitic(p.trim()+"+", farasa.getStemTempate(p.trim()), null, "", position, "");
//                position = "I";
//                if (p.trim().equals("ال"))
//                        clitic.det = "y";
//                //output.add(p + "+");
//                output.add(clitic);
//            }
//        }
//        if (parts[1].trim().length() > 0) {
//            Clitic clitic = new Clitic(parts[1].trim(), farasa.getStemTempate(parts[1].trim()), null, "", position, "");
//            clitic.isStem = "y";
//            position = "I";
//            output.add(clitic);
//            //output.add(parts[1].trim());
//        }
//        for (String p : parts[2].split("\\+")) {
//            if (p.trim().length() > 0) {
//                Clitic clitic = new Clitic("+"+p.trim(), farasa.getStemTempate(p.trim()), null, "", position, "");
//                position = "I";
//                output.add(clitic);
//                //output.add("+" + p);
//            }
//        }
//        return output;
//    }
//    
//    private ArrayList<String> getWordParts(ArrayList<String> wordPOS) {
//        ArrayList<String> output = new ArrayList<String>();
//        String segmentedWord = wordPOS.get(0).substring(0, wordPOS.get(0).indexOf("\t"));
//        for (int i = 1; i < wordPOS.size(); i++) {
//            segmentedWord += "+" + wordPOS.get(i).substring(0, wordPOS.get(i).indexOf("\t"));
//        }
//        segmentedWord = farasa.getProperSegmentation(segmentedWord);
//        String[] parts = (" " + segmentedWord + " ").split(";");
//        for (String p : parts[0].split("\\+")) {
//            if (p.trim().length() > 0) {
//                output.add(p + "+");
//            }
//        }
//        if (parts[1].trim().length() > 0) {
//            output.add(parts[1].trim());
//        }
//        for (String p : parts[2].split("\\+")) {
//            if (p.trim().length() > 0) {
//                output.add("+" + p);
//            }
//        }
//        return output;
//    }
//
//    private String getWordPrefix(ArrayList<String> wordPOS) {
//        ArrayList<String> output = new ArrayList<String>();
//        String segmentedWord = wordPOS.get(0).substring(0, wordPOS.get(0).indexOf("\t"));
//        for (int i = 1; i < wordPOS.size(); i++) {
//            segmentedWord += "+" + wordPOS.get(i).substring(0, wordPOS.get(i).indexOf("\t"));
//        }
//        segmentedWord = getProperSegmentation(segmentedWord);
//        String[] parts = (" " + segmentedWord + " ").split(";");
//        return parts[0].trim();
//    }
//
//    private ArrayList<String> getPosParts(ArrayList<String> wordPOS) {
//        ArrayList<String> output = new ArrayList<String>();
//
//        for (int i = 0; i < wordPOS.size(); i++) {
//            output.add(wordPOS.get(i).substring(wordPOS.get(i).lastIndexOf("\t") + 1));
//        }
//        return output;
//    }
//
//    public String getProperSegmentation(String input) {
//        return farasa.getProperSegmentation(input);
////        if (hPrefixes.isEmpty()) {
////            for (int i = 0; i < prefixes.length; i++) {
////                hPrefixes.put(prefixes[i].toString(), 1);
////            }
////        }
////        if (hSuffixes.isEmpty()) {
////            for (int i = 0; i < suffixes.length; i++) {
////                hSuffixes.put(suffixes[i].toString(), 1);
////            }
////        }
////        hSuffixes.put("ما", 1);
////        hSuffixes.put("و", 1);
////        hSuffixes.put("تا", 1);
////        hSuffixes.put("من", 1);
////        hSuffixes.put("ي", 1);
////        hSuffixes.put("ني", 1);
////
////        String output = "";
////        String[] word = input.split("\\+");
////        String currentPrefix = "";
////        String currentSuffix = "";
////        int iValidPrefix = -1;
////        while (iValidPrefix + 1 < word.length && hPrefixes.containsKey(word[iValidPrefix + 1])) {
////            iValidPrefix++;
////        }
////
////        int iValidSuffix = word.length;
////
////        while (iValidSuffix > Math.max(iValidPrefix, 0) && (hSuffixes.containsKey(word[iValidSuffix - 1])
////                || word[iValidSuffix - 1].equals("_"))) {
////            iValidSuffix--;
////        }
////
////        for (int i = 0; i <= iValidPrefix; i++) {
////            currentPrefix += word[i] + "+";
////        }
////        String stemPart = "";
////        for (int i = iValidPrefix + 1; i < iValidSuffix; i++) {
////            stemPart += word[i];
////        }
////
////        if (iValidSuffix == iValidPrefix) {
////            iValidSuffix++;
////        }
////
////        for (int i = iValidSuffix; i < word.length && iValidSuffix != iValidPrefix; i++) {
////            currentSuffix += "+" + word[i];
////        }
////
////        if (currentPrefix.endsWith("س+") && !stemPart.matches("^[ينأت].*")) {
////            currentPrefix = currentPrefix.substring(0, currentPrefix.length() - 2);
////            stemPart = "س" + stemPart;
////        }
////        if (currentPrefix.trim().length() == 0 && stemPart.trim().length() == 0)
////            output = ";" + currentSuffix.replace("+", "") + ";";
////        else
////            output = currentPrefix + ";" + stemPart + ";" + currentSuffix;
////        output = output.replaceFirst("^\\+", "");
////        output = output.replaceFirst("\\+$", "");
////
////        return output.replace("++", "+");
//    }
//
//    private String isNumber(String input) {
//        // if (hmNumber.containsKey(input.trim()) || input.matches("[" + AllHindiDigits + "0-9\\.,\u00BC-\u00BE]+")) {
//        if (hmNumber.containsKey(input.trim()) || rAllNumbers.matcher(input).matches()) {
//            return "NUM";
//        } else {
//            return "NOT";
//        }
//    }
//
//    private String getMetaType(String input) {
//        if (isNumber(input.trim()).equals("NUM")) {
//            return "NUM";
//        } else if (input.trim().startsWith("+") && input.trim().length() > 1) {
//            return "PREFIX";
//        } else if (input.trim().endsWith("+") && input.trim().length() > 1) {
//            return "SUFFIX";
//        // } else if (input.trim().matches(".*[a-zA-z]+.*")) {
//        } else if (rEnglishLetters.matcher(input.trim()).matches()) {
//            return "FOREIGN";
//        // } else if (input.trim().matches("[" + AllArabicLetters + "]+")) {
//        } else if (rAllArabicLetters.matcher(input.trim()).matches())
//        {
//            return "ARAB";
//        // } else if (input.trim().matches(".*[" + ALLDelimiters + "]+.*"))
//        } else if (rAllDelimiters.matcher(input.trim()).matches())
//        {
//            return "PUNC";
//        } 
//        else {
//            return "OTHER";
//        }
//    }
//
//}

/*

 * To change this license header, choose License Headers in Project Properties.

 * To change this template file, choose Tools | Templates

 * and open the template in the editor.

 */

package com.qcri.farasa.pos;



import com.qcri.farasa.segmenter.ArabicUtils;
import static com.qcri.farasa.segmenter.ArabicUtils.ALLDelimiters;
import static com.qcri.farasa.segmenter.ArabicUtils.AllArabicLetters;
import static com.qcri.farasa.segmenter.ArabicUtils.AllHindiDigits;
import static com.qcri.farasa.segmenter.ArabicUtils.prefixes;
import static com.qcri.farasa.segmenter.ArabicUtils.suffixes;
import com.qcri.farasa.segmenter.Farasa;
import java.beans.Beans;
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
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Position;

/**
 *
 * @author kareemdarwish
 */
public class FarasaPOSTagger {

    public static HashMap<String, Double> hmPos = new HashMap<String, Double>();
    public static HashMap<String, Boolean> hmPosNormal = new HashMap<String, Boolean>();
    public static HashMap<String, Double> hmWord = new HashMap<String, Double>();
    public static HashMap<String, Double> hmPosGivenWord = new HashMap<String, Double>(100000);
    public static HashMap<String, Double> hmPosGivenPrevWord = new HashMap<String, Double>(100000);
    public static HashMap<String, Double> hmPosGivenNextWord = new HashMap<String, Double>(100000);

    public static HashMap<String, Double> hmBiGramPrev = new HashMap<String, Double>(50000);
    public static HashMap<String, Double> hmPosGivenPrev2Words = new HashMap<String, Double>(100000);
    public static HashMap<String, Double> hmBiGramNext = new HashMap<String, Double>(50000);
    public static HashMap<String, Double> hmPosGivenNext2Words = new HashMap<String, Double>(100000);

    public static HashMap<String, Double> hmTriGramPrev = new HashMap<String, Double>(500000);
    public static HashMap<String, Double> hmPosGivenPrev3Words = new HashMap<String, Double>(500000);
    public static HashMap<String, Double> hmTriGramNext = new HashMap<String, Double>(500000);
    public static HashMap<String, Double> hmPosGivenNext3Words = new HashMap<String, Double>(500000);

    public static HashMap<String, Double> hmTriGramPosPrev = new HashMap<String, Double>();
    public static HashMap<String, Double> hmPosGivenPrev3Pos = new HashMap<String, Double>(50000);
    
    public static HashMap<String, Double> hmTriGramTemplatePrev = new HashMap<String, Double>();
    public static HashMap<String, Double> hmPosGivenPrev3Template = new HashMap<String, Double>(50000);
    
    public static HashMap<String, Double> hm4GramPosPrev = new HashMap<String, Double>();
    public static HashMap<String, Double> hmPosGivenPrev4Pos = new HashMap<String, Double>(50000);
    
    public static HashMap<String, Double> hmTriGramPosNext = new HashMap<String, Double>();
    public static HashMap<String, Double> hmPosGivenNext3Pos = new HashMap<String, Double>(50000);

    public static HashMap<String, Double> hm4GramPosNext = new HashMap<String, Double>();
    public static HashMap<String, Double> hmPosGivenNext4Pos = new HashMap<String, Double>(50000);
    
    public static HashMap<String, Double> hmBiGramPosPrev = new HashMap<String, Double>();
    public static HashMap<String, Double> hmPosGivenPrev2Pos = new HashMap<String, Double>();
    public static HashMap<String, Double> hmBiGramPosNext = new HashMap<String, Double>();
    public static HashMap<String, Double> hmPosGivenNext2Pos = new HashMap<String, Double>();

    public static HashMap<String, Double> hmUniGramPosContext = new HashMap<String, Double>();
    public static HashMap<String, Double> hmPosGivenContext1Pos = new HashMap<String, Double>();
    public static HashMap<String, Double> hmBiGramPosContext = new HashMap<String, Double>();
    public static HashMap<String, Double> hmPosGivenContext2Pos = new HashMap<String, Double>();

    public static HashMap<String, Double> hmPosGivenMetaType = new HashMap<String, Double>();
    public static HashMap<String, Double> hmWordGivenPos = new HashMap<String, Double>();
    public static HashMap<String, Double> hmPosGivenSuffix = new HashMap<String, Double>();
    public static HashMap<String, Double> hmPosGivenPrefix = new HashMap<String, Double>();
    public static HashMap<String, Double> hmPrevPrefixandCurrentPrefix = new HashMap<String, Double>();
    public static HashMap<String, Double> hmPosGivenPrevPrefixandCurrentPrefix = new HashMap<String, Double>();
    public static HashMap<String, Double> hmPosGivenTemplate = new HashMap<String, Double>();
    public static HashMap<String, Double> hmTemplate = new HashMap<String, Double>();
    public static HashMap<String, Double> hmPosGivenPos = new HashMap<String, Double>();
    public static HashMap<String, Double> hmPrefix = new HashMap<String, Double>();
    public static HashMap<String, Double> hmSuffix = new HashMap<String, Double>();
    public static HashMap<String, Double> hmPrevSuffixes = new HashMap<String, Double>();
   public static HashMap<String, Double> hmMetaType = new HashMap<String, Double>();
    public static HashMap<String, String> hmOnePossiblePos = new HashMap<String, String>();
    private static HashMap<String, Integer> hmNumber = new HashMap<String, Integer>();
    
    private static final HashMap<String, Double> hmCombinedPOS = new HashMap<String, Double>();
    private static HashMap<String, Double> hmPosGivenPrevWordSuffix = new HashMap<String, Double>();
    
    private static final HashMap<String, Double> hmPrevWordPOS = new HashMap<String, Double>();
    private static HashMap<String, Double> hmPosGivenPrevWordPOS = new HashMap<String, Double>();
    private static final HashMap<String, String> verbOrNot = new HashMap<String, String>();
    private static final HashMap<String, String> nounOrNot = new HashMap<String, String>();
    // private static HashMap<String, Double> hmContextPOSwithGenderNumberTags = new HashMap<String, Double>();
    // private static HashMap<String, Double> hmPosGivenContextPOSwithGenderNumberTags = new HashMap<String, Double>();

    private static final HashMap<String, Double> hmPreps = new HashMap<String, Double>();

    private static final ArrayList<Double> model = new ArrayList<Double>();

    public static String binDir = "";

    // private static FitTemplateClass ft = null;

    private static Farasa farasa = null;

    genderNumberTags gnt = null;

    private static HashMap<String, Integer> hPrefixes = new HashMap<String, Integer>();

    private static HashMap<String, Integer> hSuffixes = new HashMap<String, Integer>();

    private static HashMap<String, Boolean> hStrictlyPrefix = new HashMap<String, Boolean>();

    private static HashMap<String, Boolean> hStrictlySuffix = new HashMap<String, Boolean>();

    private static HashMap<String, Boolean> hPossiblePrefix = new HashMap<String, Boolean>();

    private static HashMap<String, Boolean> hPossibleSuffix = new HashMap<String, Boolean>();

    

    private static final Pattern rAllArabicLetters = Pattern.compile("[" + AllArabicLetters + "]+");

    private static final Pattern rEnglishLetters = Pattern.compile(".*[a-zA-z]+.*");

    private static final Pattern rAllDelimiters = Pattern.compile(".*[" + ALLDelimiters + "]+.*");

    private static final Pattern rAllNumbers = Pattern.compile("[" + AllHindiDigits + "0-9\\.,\u00BC-\u00BE]+");



    public FarasaPOSTagger(Farasa farasaInstance) throws IOException, ClassNotFoundException, InterruptedException
    {
        // binDir = dataDir;
        // binDir = dataDir;
        if (farasaInstance == null)
        {
            farasa = new Farasa(); // "/Users/kareemdarwish/RESEARCH/FARASA/FarasaData/"
        }
        else
        {
            farasa = farasaInstance;
        }

        gnt = new genderNumberTags(farasa); // "/Users/kareemdarwish/RESEARCH/ArabicProcessingTools-master/POSandNERData/"
        for (String pp : "من إلى عن في على ب ل ك حتى مذ منذ و ت رب خلا حاشا إلي علي ل+ ب+ ك+".split(" +"))
        {
            hmPreps.put(pp, 0d);
        }

        String[] modelVals = "1:0.1641309 2:0.21368973 3:-0.032040793 4:-0.060479183 5:-0.045209616 6:-0.0013929944 7:-0.045138083 8:0.0027416311 9:0.13443293 10:0.1317763 11:-0.053899568 12:0.0093294811 13:0.092907488 14:0.17465037 15:-0.0087724049 16:-0.014008488 17:0.0070360508 18:0.0045469049".split(" +");

        for (String s : modelVals) {
            model.add(Double.parseDouble(s.substring(s.indexOf(":") + 1)));
        }

        if (true)
        {
            deserializeDataStructures();
        }
        else
        {
            BufferedReader brNum = new BufferedReader(new InputStreamReader(new FileInputStream(new File(binDir + "number-gaz.txt")), StandardCharsets.UTF_8));
            String line = "";
            while ((line = brNum.readLine()) != null)
            {
                if (!hmNumber.containsKey(line.trim()))
                {
                    hmNumber.put(line.trim(), 1);
                }
            }
        }

            /*
             * CASE NSUFF VSUFF
             */
            hStrictlySuffix.put("CASE", Boolean.TRUE);
            hStrictlySuffix.put("NSUFF", Boolean.TRUE);
            hStrictlySuffix.put("VSUFF", Boolean.TRUE);

            /*
             * DET JUS
             */
            hStrictlyPrefix.put("DET", Boolean.TRUE);
            hStrictlyPrefix.put("JUS", Boolean.TRUE);

            /*
             * CONJ DET FUT_PART JUS PREP PART
             */
            hPossiblePrefix.put("CONJ", Boolean.TRUE);
            hPossiblePrefix.put("DET", Boolean.TRUE);
            hPossiblePrefix.put("FUT_PART", Boolean.TRUE);
            hPossiblePrefix.put("JUS", Boolean.TRUE);
            hPossiblePrefix.put("PREP", Boolean.TRUE);
            hPossiblePrefix.put("PART", Boolean.TRUE);

            /*
             * CASE NSUFF VSUFF PART PRON
             */
            hPossibleSuffix.put("CASE", Boolean.TRUE);
            hPossibleSuffix.put("NSUFF", Boolean.TRUE);
            hPossibleSuffix.put("VSUFF", Boolean.TRUE);
            hPossibleSuffix.put("PART", Boolean.TRUE);
            hPossibleSuffix.put("PRON", Boolean.TRUE);
            hPossibleSuffix.put("PREP", Boolean.TRUE);
            /*
             * ADJ ADV NOUN V
             */
            hmPosNormal.put("ADJ", Boolean.TRUE);
            hmPosNormal.put("ADV", Boolean.TRUE);
            hmPosNormal.put("NOUN", Boolean.TRUE);
            hmPosNormal.put("V", Boolean.TRUE);

        
            // BufferedReader br = ArabicUtils.openFileForReading("C:\\RESEARCH\\FromMac\\RESEARCH\\ArabicProcessingTools-master\\DATA\\MADA-analysis-of-Aljazeera-words.raw.txt.out.verb");
            // String line = "";
            // while ((line = br.readLine()) != null)
            // {
            //     String[] parts = line.split("\t");
            //     if (parts.length == 2 && parts[0].length() > 1) // && (parts[1].equals("NOUN") || parts[1].equals("ADJ")))
            //         verbOrNot.put(parts[0], parts[1]);
            // }
            // br = ArabicUtils.openFileForReading("C:\\RESEARCH\\FromMac\\RESEARCH\\ArabicProcessingTools-master\\DATA\\MADA-analysis-of-Aljazeera-words.raw.txt.out.noun");
            // line = "";
            // while ((line = br.readLine()) != null)
            // {
            //     String[] parts = line.split("\t");
            //     if (parts.length == 2 && parts[0].length() > 1) // && (parts[1].equals("NOUN") || parts[1].equals("ADJ")))
            //         nounOrNot.put(parts[0], parts[1]);
            // }
            // System.err.println();
    }



    public void serializeMap(String BinDir, String MapName, HashMap input) throws IOException {

        FileOutputStream fos

                = new FileOutputStream(BinDir + "FarasaPOSdata." + MapName + ".ser");

        ObjectOutputStream oos = new ObjectOutputStream(fos);

        oos.writeObject(input);

        oos.close();

        fos.close();

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

    

    public HashMap deserializeMap(String BinDir, String MapName) throws IOException, ClassNotFoundException

    {

	ObjectInputStream ois = new ObjectInputStream(resolveName("/FarasaData/FarasaPOSdata." + MapName + ".ser"));

         HashMap map = (HashMap) ois.readObject();

         ois.close();

         return map;

//	 

//        FileInputStream fis = new FileInputStream(BinDir + "FarasaPOSdata." + MapName + ".ser");

//         ObjectInputStream ois = new ObjectInputStream(fis);

//         HashMap map = (HashMap) ois.readObject();

//         ois.close();

//         fis.close();

//         return map;

    }

    

    public void serializeDataStructures(String dir) throws IOException {

        serializeMap(binDir, "hmPos", hmPos);

        serializeMap(binDir, "hmPosNormal", hmPosNormal);

        serializeMap(binDir, "hmWord", hmWord);

        serializeMap(binDir, "hmPosGivenWord", hmPosGivenWord);

        serializeMap(binDir, "hmPosGivenPrevWord", hmPosGivenPrevWord);

        serializeMap(binDir, "hmPosGivenNextWord", hmPosGivenNextWord);

        serializeMap(binDir, "hmBiGramPrev", hmBiGramPrev);

        serializeMap(binDir, "hmPosGivenPrev2Words", hmPosGivenPrev2Words);

        serializeMap(binDir, "hmBiGramNext", hmBiGramNext);

        serializeMap(binDir, "hmPosGivenNext2Words", hmPosGivenNext2Words);

        serializeMap(binDir, "hmTriGramPrev", hmTriGramPrev);

        serializeMap(binDir, "hmPosGivenPrev3Words", hmPosGivenPrev3Words);

        serializeMap(binDir, "hmTriGramNext", hmTriGramNext);

        serializeMap(binDir, "hmPosGivenNext3Words", hmPosGivenNext3Words);

        serializeMap(binDir, "hmTriGramPosPrev", hmTriGramPosPrev);

        serializeMap(binDir, "hmPosGivenPrev3Pos", hmPosGivenPrev3Pos);

        

        // serializeMap(binDir, "hmContextPOSwithGenderNumberTags", hmContextPOSwithGenderNumberTags);

        // serializeMap(binDir, "hmPosGivenContextPOSwithGenderNumberTags", hmPosGivenContextPOSwithGenderNumberTags);

        

        serializeMap(binDir, "hmTriGramTemplatePrev", hmTriGramTemplatePrev);

        serializeMap(binDir, "hmPosGivenPrev3Template", hmPosGivenPrev3Template);

        

        serializeMap(binDir, "hm4GramPosPrev", hm4GramPosPrev);

        serializeMap(binDir, "hmPosGivenPrev4Pos", hmPosGivenPrev4Pos);

        serializeMap(binDir, "hmTriGramPosNext", hmTriGramPosNext);

        serializeMap(binDir, "hmPosGivenNext3Pos", hmPosGivenNext3Pos);

        

        serializeMap(binDir, "hm4GramPosNext", hm4GramPosNext);

        serializeMap(binDir, "hmPosGivenNext4Pos", hmPosGivenNext4Pos);

        

        serializeMap(binDir, "hmBiGramPosPrev", hmBiGramPosPrev);

        serializeMap(binDir, "hmPosGivenPrev2Pos", hmPosGivenPrev2Pos);

        serializeMap(binDir, "hmBiGramPosNext", hmBiGramPosNext);

        serializeMap(binDir, "hmPosGivenNext2Pos", hmPosGivenNext2Pos);

        serializeMap(binDir, "hmUniGramPosContext", hmUniGramPosContext);

        serializeMap(binDir, "hmPosGivenContext1Pos", hmPosGivenContext1Pos);

        serializeMap(binDir, "hmBiGramPosContext", hmBiGramPosContext);

        serializeMap(binDir, "hmPosGivenContext2Pos", hmPosGivenContext2Pos);

        serializeMap(binDir, "hmPosGivenMetaType", hmPosGivenMetaType);

        serializeMap(binDir, "hmWordGivenPos", hmWordGivenPos);

        serializeMap(binDir, "hmPosGivenSuffix", hmPosGivenSuffix);

        serializeMap(binDir, "hmPosGivenPrefix", hmPosGivenPrefix);

        serializeMap(binDir, "hmPrevPrefixandCurrentPrefix", hmPrevPrefixandCurrentPrefix);

        serializeMap(binDir, "hmPosGivenPrevPrefixandCurrentPrefix", hmPosGivenPrevPrefixandCurrentPrefix);

        serializeMap(binDir, "hmPosGivenTemplate", hmPosGivenTemplate);

        serializeMap(binDir, "hmTemplate", hmTemplate);

        serializeMap(binDir, "hmPosGivenPos", hmPosGivenPos);

        serializeMap(binDir, "hmPrefix", hmPrefix);

        serializeMap(binDir, "hmSuffix", hmSuffix);

        serializeMap(binDir, "hmMetaType", hmMetaType);

        serializeMap(binDir, "hmOnePossiblePos", hmOnePossiblePos);

        serializeMap(binDir, "hmNumber", hmNumber);

        serializeMap(binDir, "hPrefixes", hPrefixes);

        serializeMap(binDir, "hSuffixes", hSuffixes);

        serializeMap(binDir, "hStrictlyPrefix", hStrictlyPrefix);

        serializeMap(binDir, "hStrictlySuffix", hStrictlySuffix);

        serializeMap(binDir, "hPossiblePrefix", hPossiblePrefix);

        serializeMap(binDir, "hPossibleSuffix", hPossibleSuffix);

        serializeMap(binDir, "hmPosGivenPrevWordSuffix", hmPosGivenPrevWordSuffix);

    }



    public void deserializeDataStructures() throws IOException, ClassNotFoundException {

        hmPos = deserializeMap(binDir, "hmPos");

        // hmPosNormal = deserializeMap(binDir, "hmPosNormal");

        hmWord = deserializeMap(binDir, "hmWord");

        hmPosGivenWord = deserializeMap(binDir, "hmPosGivenWord");

        hmPosGivenPrevWord = deserializeMap(binDir, "hmPosGivenPrevWord");

        hmPosGivenNextWord = deserializeMap(binDir, "hmPosGivenNextWord");

        // hmBiGramPrev = deserializeMap(binDir, "hmBiGramPrev");

        // hmPosGivenPrev2Words = deserializeMap(binDir, "hmPosGivenPrev2Words");

        // hmBiGramNext = deserializeMap(binDir, "hmBiGramNext");

        // hmPosGivenNext2Words = deserializeMap(binDir, "hmPosGivenNext2Words");

        // hmTriGramPrev = deserializeMap(binDir, "hmTriGramPrev");

        // hmPosGivenPrev3Words = deserializeMap(binDir, "hmPosGivenPrev3Words");

        // hmTriGramNext = deserializeMap(binDir, "hmTriGramNext");

        // hmPosGivenNext3Words = deserializeMap(binDir, "hmPosGivenNext3Words");

        // hmTriGramPosPrev = deserializeMap(binDir, "hmTriGramPosPrev");

        hmPosGivenPrev3Pos = deserializeMap(binDir, "hmPosGivenPrev3Pos");

        

        // hmContextPOSwithGenderNumberTags = deserializeMap(binDir, "hmContextPOSwithGenderNumberTags");

        // hmPosGivenContextPOSwithGenderNumberTags = deserializeMap(binDir, "hmPosGivenContextPOSwithGenderNumberTags");

        

        // hmTriGramTemplatePrev = deserializeMap(binDir, "hmTriGramTemplatePrev");

        // hmPosGivenPrev3Template = deserializeMap(binDir, "hmPosGivenPrev3Template");

        

        // hm4GramPosPrev = deserializeMap(binDir, "hm4GramPosPrev");

        hmPosGivenPrev4Pos = deserializeMap(binDir, "hmPosGivenPrev4Pos");

        // hmTriGramPosNext = deserializeMap(binDir, "hmTriGramPosNext");

        hmPosGivenNext3Pos = deserializeMap(binDir, "hmPosGivenNext3Pos");

        

        // hm4GramPosNext = deserializeMap(binDir, "hm4GramPosNext");

        // hmPosGivenNext4Pos = deserializeMap(binDir, "hmPosGivenNext4Pos");

        

        // hmBiGramPosPrev = deserializeMap(binDir, "hmBiGramPosPrev");

        hmPosGivenPrev2Pos = deserializeMap(binDir, "hmPosGivenPrev2Pos");

        // hmBiGramPosNext = deserializeMap(binDir, "hmBiGramPosNext");

        hmPosGivenNext2Pos = deserializeMap(binDir, "hmPosGivenNext2Pos");

        // hmUniGramPosContext = deserializeMap(binDir, "hmUniGramPosContext");

        hmPosGivenContext1Pos = deserializeMap(binDir, "hmPosGivenContext1Pos");

        // hmBiGramPosContext = deserializeMap(binDir, "hmBiGramPosContext");

        // hmPosGivenContext2Pos = deserializeMap(binDir, "hmPosGivenContext2Pos");

        // hmPosGivenMetaType = deserializeMap(binDir, "hmPosGivenMetaType");

        hmWordGivenPos = deserializeMap(binDir, "hmWordGivenPos");

        hmPosGivenSuffix = deserializeMap(binDir, "hmPosGivenSuffix");

        hmPosGivenPrefix = deserializeMap(binDir, "hmPosGivenPrefix");

        // hmPrevPrefixandCurrentPrefix = deserializeMap(binDir, "hmPrevPrefixandCurrentPrefix");

        hmPosGivenPrevPrefixandCurrentPrefix = deserializeMap(binDir, "hmPosGivenPrevPrefixandCurrentPrefix");

        hmPosGivenTemplate = deserializeMap(binDir, "hmPosGivenTemplate");

        // hmTemplate = deserializeMap(binDir, "hmTemplate");

        hmPosGivenPos = deserializeMap(binDir, "hmPosGivenPos");

        hmPrefix = deserializeMap(binDir, "hmPrefix");

        hmSuffix = deserializeMap(binDir, "hmSuffix");

        // hmMetaType = deserializeMap(binDir, "hmMetaType");

        // hmOnePossiblePos = deserializeMap(binDir, "hmOnePossiblePos");

        hmNumber = deserializeMap(binDir, "hmNumber");

        hPrefixes = deserializeMap(binDir, "hPrefixes");

        hSuffixes = deserializeMap(binDir, "hSuffixes");

        hStrictlyPrefix = deserializeMap(binDir, "hStrictlyPrefix");

        hStrictlySuffix = deserializeMap(binDir, "hStrictlySuffix");

        hPossiblePrefix = deserializeMap(binDir, "hPossiblePrefix");

        hPossibleSuffix = deserializeMap(binDir, "hPossibleSuffix");

        hmPosGivenPrevWordSuffix = deserializeMap(binDir, "hmPosGivenPrevWordSuffix");

    }



    public Word getWordRepresentation(ArrayList<String> word) {

        Word output = new Word();

        String segmented = "";

        ArrayList<String> posTags = new ArrayList<String>();

        for (int i = 0; i < word.size(); i++) {

            String s = word.get(i).substring(0, word.get(i).indexOf("\t"));

            String pos = word.get(i).substring(word.get(i).lastIndexOf("\t") + 1);

            posTags.add(pos);

            if (i > 0) {

                segmented += "+";

            }

            segmented += s;

        }

        ArrayList<String> segmentedWord = getWordParts(segmented);

        for (int j = 0; j < segmentedWord.size(); j++) {

            String position = "";

            if (j == 0) {

                position = "B";

            } else {

                position = "I";

            }

            String ss = segmentedWord.get(j);

            if (j == 0 && ss.startsWith("+"))

                ss = ss.substring(1);

            if (j == segmentedWord.size() -1 && ss.endsWith("+"))

                ss = ss.substring(0, ss.length() - 1);

            // ArrayList<String> possiblePOS = new ArrayList<String>();// possiblePOSTags(ss);

            String template = farasa.getStemTempate(ss); // ft.fitTemplate(ss);

            

            // correct prepositions -- basically restrict to a closed set

            if (posTags.get(j).trim().equals("PREP") && !hmPreps.containsKey(ss.trim()))

            {

                if (ss.trim().equals("إن") || ss.trim().equals("إلا"))

                    posTags.set(j, "PART");

                else

                    posTags.set(j, "NOUN");

            }

            

            Clitic c = new Clitic(ss.trim(), template.trim(), null, posTags.get(j).trim(), position, "");

            output.add(c);

        }

        return output;

    }



    public void train(String filename) throws Exception {

        BufferedReader br = ArabicUtils.openFileForReading(filename);

        BufferedWriter bw = ArabicUtils.openFileForWriting(filename + ".deleteMe");

        Sentence fullSentence = new Sentence();

        String line = "";

        hmPos.put("§", 0d);

        hmWord.put("§§", 0d);

        hmPos.put("S", 0d);

        hmWord.put("S", 0d);

        hmPos.put("E", 0d);

        hmWord.put("E", 0d);

        // HashMap<String, Double> stats = new HashMap<String, Double>();

        ArrayList<ArrayList<String>> sentence = new ArrayList<ArrayList<String>>();

        ArrayList<String> word = new ArrayList<String>();

        int lineNumber = 0;

        while ((line = br.readLine()) != null) {

            lineNumber++;

            line = line.trim();

            if (line.endsWith("\tO")) {

                if (word.size() > 0) {

                    word = fixPotentialSegmentationMismatch(word);

                    fullSentence.addWord(getWordRepresentation(word));

                    sentence.add(new ArrayList<String>(word));

                }

                word.clear();

            } else if (line.length() == 0) {

                if (word.size() > 0) {

                    word = fixPotentialSegmentationMismatch(word);

                    fullSentence.addWord(getWordRepresentation(word));

                    sentence.add(new ArrayList<String>(word));

                }

                if (fullSentence.clitics.size() > 0) // put end of sentence marker

                {

                    fullSentence.addClitic(new Clitic("E", "E", new ArrayList<String>(), "E", "B", "E"));

                }



                if (fullSentence.clitics.size() > 0) {

                    // gender and number tags to all NOUN, ADJ, NUM

                    fullSentence = setAllGuessTagsToTruthTags(fullSentence);

                    fullSentence = addGenderNumberFeatures(fullSentence);

                    // get combined POS tags

                    String combinedPOSTags = "";

                    for (int j = 0; j < fullSentence.clitics.size(); j++)

                    {

                        if (fullSentence.clitics.get(j).position == "B")

                            combinedPOSTags += " ";

                        combinedPOSTags += fullSentence.clitics.get(j).truthPOS;

                        if (j < fullSentence.clitics.size() - 1 && fullSentence.clitics.get(j+1).position.equals("I"))

                            combinedPOSTags += "+";

                    }

                    String[] cmb = combinedPOSTags.split(" +");

                    for (int k = 0; k < cmb.length - 1; k++)

                    {

                        String key = cmb[k] + " " + cmb[k+1];

                        incrementValGivenKey(hmCombinedPOS, key, 1d);

                    }

                    for (int j = 0; j < fullSentence.clitics.size(); j++) {

                        Clitic clitic = fullSentence.clitics.get(j);



                        if (clitic.surface.equals("§§") || clitic.surface.equals("S") || clitic.surface.equals("E")) // if not a seperator

                        {

                            hmPos.put(clitic.truthPOS, hmPos.get(clitic.truthPOS) + 1);

                            hmWord.put(clitic.surface, hmWord.get(clitic.surface) + 1);

                        } else {

                            // POS count

                            incrementValGivenKey(hmPos, clitic.truthPOS, 1d);

                            // word count

                            incrementValGivenKey(hmWord, clitic.surface, 1d);                           

                            // word_POS count

                            // keep word_POS count

                            incrementValGivenKey(hmPosGivenWord, clitic.surface + "\t" + clitic.truthPOS, 1d);                           

                            // word_POS given previous clitic

                            ArrayList<Integer> Positions = getSurroundingNClitics(fullSentence, j, -1);

                            String previousClitic = fullSentence.clitics.get(Positions.get(0)).surface;

                            incrementValGivenKey(hmPosGivenPrevWord, previousClitic + "\t" + clitic.truthPOS, 1d);

                            // word_POS given next clitic

                            Positions = getSurroundingNClitics(fullSentence, j, 1);

                            String nextClitic = fullSentence.clitics.get(Positions.get(0)).surface;

                            incrementValGivenKey(hmPosGivenNextWord, nextClitic + "\t" + clitic.truthPOS, 1d);

                            // given preceeding 2 words

                            Positions = getSurroundingNClitics(fullSentence, j, -2);

                            if (Positions.size() == 2) {

                                String prev2Clitics = fullSentence.clitics.get(Positions.get(0)).surface + " " + fullSentence.clitics.get(Positions.get(1)).surface;

                                incrementValGivenKey(hmBiGramPrev, prev2Clitics, 1d);

                                incrementValGivenKey(hmPosGivenPrev2Words, prev2Clitics + "\t" + clitic.truthPOS, 1d);

                            }

                            // given next 2 words

                            Positions = getSurroundingNClitics(fullSentence, j, 2);

                            if (Positions.size() == 2) {

                                String next2Clitics = fullSentence.clitics.get(Positions.get(0)).surface + " " + fullSentence.clitics.get(Positions.get(1)).surface;

                                incrementValGivenKey(hmBiGramNext, next2Clitics, 1d);

                                incrementValGivenKey(hmPosGivenNext2Words, next2Clitics + "\t" + clitic.truthPOS, 1d);

                            }

                            // given context 1 POS

                            String context1Pos = "";

                            Positions = getSurroundingNClitics(fullSentence, j, -1);

                            context1Pos += fullSentence.clitics.get(Positions.get(0)).truthPOS;

                            Positions = getSurroundingNClitics(fullSentence, j, 1);

                            context1Pos += " " + fullSentence.clitics.get(Positions.get(0)).truthPOS;

                            incrementValGivenKey(hmUniGramPosContext, context1Pos, 1d);

                            incrementValGivenKey(hmPosGivenContext1Pos, context1Pos + "\t" + clitic.truthPOS, 1d);

                            // given context 2 POS

                            String context2Pos = "";

                            Positions = getSurroundingNClitics(fullSentence, j, -2);

                            if (Positions.size() == 2) {

                                context1Pos += fullSentence.clitics.get(Positions.get(0)).truthPOS + " " + fullSentence.clitics.get(Positions.get(1)).truthPOS;

                                Positions = getSurroundingNClitics(fullSentence, j, 2);

                                if (Positions.size() == 2) {

                                    context1Pos += " " + fullSentence.clitics.get(Positions.get(0)).truthPOS + " " + fullSentence.clitics.get(Positions.get(1)).truthPOS;

                                    incrementValGivenKey(hmBiGramPosContext, context2Pos, 1d);

                                    incrementValGivenKey(hmPosGivenContext2Pos, context2Pos + "\t" + clitic.truthPOS, 1d);

                                }

                            }

                            // given preceeding 2 POS

                            Positions = getSurroundingNClitics(fullSentence, j, -2);

                            if (Positions.size() == 2) {

                                String prev2Pos = fullSentence.clitics.get(Positions.get(0)).truthPOS + " " + fullSentence.clitics.get(Positions.get(1)).truthPOS;

                                incrementValGivenKey(hmBiGramPosPrev, prev2Pos, 1d);

                                incrementValGivenKey(hmPosGivenPrev2Pos, prev2Pos + "\t" + clitic.truthPOS, 1d);

                            }

                            // given next 2 POS

                            Positions = getSurroundingNClitics(fullSentence, j, 2);

                            if (Positions.size() == 2) {

                                String next2Pos = fullSentence.clitics.get(Positions.get(0)).truthPOS + " " + fullSentence.clitics.get(Positions.get(1)).truthPOS;

                                incrementValGivenKey(hmBiGramPosNext, next2Pos, 1d);

                                incrementValGivenKey(hmPosGivenNext2Pos, next2Pos + "\t" + clitic.truthPOS, 1d);

                            }

                            

                            // given preceeding 3 POS

                            Positions = getSurroundingNClitics(fullSentence, j, -3);

                            if (Positions.size() == 3) {

                                String prev3Pos = fullSentence.clitics.get(Positions.get(0)).truthPOS;

                                for (int cnt = 1; cnt < 3; cnt++)

                                    prev3Pos += " " + fullSentence.clitics.get(Positions.get(cnt)).truthPOS;

                                incrementValGivenKey(hmTriGramPosPrev, prev3Pos, 1d);

                                incrementValGivenKey(hmPosGivenPrev3Pos, prev3Pos + "\t" + clitic.truthPOS, 1d);

                            }



                            // given preceeding 3 Template

                            Positions = getSurroundingNClitics(fullSentence, j, -3);

                            // if (Positions.size() == 3) {

                            String prev3Template = "";

                            for (int cnt = 0; cnt < Positions.size(); cnt++)

                            {

                                if (fullSentence.clitics.get(Positions.get(cnt)).surface.endsWith("+") || fullSentence.clitics.get(Positions.get(cnt)).surface.startsWith("+"))

                                    prev3Template += " " + fullSentence.clitics.get(Positions.get(cnt)).truthPOS;

                                else

                                    prev3Template += " " + fullSentence.clitics.get(Positions.get(cnt)).template;

                            }

                            prev3Template = prev3Template.trim();

                            incrementValGivenKey(hmTriGramTemplatePrev, prev3Template, 1d);

                            incrementValGivenKey(hmPosGivenPrev3Template, prev3Template + "\t" + clitic.truthPOS, 1d);

                            

                            // given preceeding 4 POS

                            Positions = getSurroundingNClitics(fullSentence, j, -4);

                            if (Positions.size() == 4) {

                                String prev4Pos = fullSentence.clitics.get(Positions.get(0)).truthPOS;

                                for (int cnt = 1; cnt < 4; cnt++)

                                    prev4Pos += " " + fullSentence.clitics.get(Positions.get(cnt)).truthPOS;

                                incrementValGivenKey(hm4GramPosPrev, prev4Pos, 1d);

                                incrementValGivenKey(hmPosGivenPrev4Pos, prev4Pos + "\t" + clitic.truthPOS, 1d);

                            }

                            

                            

                            // given next 3 POS

                            Positions = getSurroundingNClitics(fullSentence, j, 3);

                            if (Positions.size() == 3) {

                                String next3Pos = fullSentence.clitics.get(Positions.get(0)).truthPOS + " " + fullSentence.clitics.get(Positions.get(1)).truthPOS + " " + fullSentence.clitics.get(Positions.get(2)).truthPOS;

                                incrementValGivenKey(hmTriGramPosNext, next3Pos, 1d);

                                incrementValGivenKey(hmPosGivenNext3Pos, next3Pos + "\t" + clitic.truthPOS, 1d);

                            }

                            

                            // given next 4 POS

                            Positions = getSurroundingNClitics(fullSentence, j, 4);

                            if (Positions.size() == 4) {

                                String next4Pos = fullSentence.clitics.get(Positions.get(0)).truthPOS + " " + fullSentence.clitics.get(Positions.get(1)).truthPOS + " " + fullSentence.clitics.get(Positions.get(2)).truthPOS + " " + fullSentence.clitics.get(Positions.get(3)).truthPOS;

                                incrementValGivenKey(hm4GramPosNext, next4Pos, 1d);

                                incrementValGivenKey(hmPosGivenNext4Pos, next4Pos + "\t" + clitic.truthPOS, 1d);

                            }

                            

                            // given metaType

                            String metaType = getMetaType(clitic.surface);

                            incrementValGivenKey(hmMetaType, metaType, 1d);

                            incrementValGivenKey(hmPosGivenMetaType, metaType + "\t" + clitic.truthPOS, 1d);



                            // get prefix and suffix counts

                            if (clitic.surface.endsWith("+")) {

                                incrementValGivenKey(hmPrefix, clitic.surface, 1d);

                            } else if (clitic.surface.startsWith("+")) {

                                incrementValGivenKey(hmSuffix, clitic.surface, 1d);

                            }

                            if (!clitic.surface.contains("+")) {

                                // put template

                                incrementValGivenKey(hmTemplate, clitic.template, 1d);

                                // put POS given template

                                incrementValGivenKey(hmPosGivenTemplate, clitic.template + "\t" + clitic.truthPOS, 1d);



                                // put prefix with POS

                                // get prefixes

                                String headPrefixes = getCurrentWordPrefix(fullSentence, j);

                                incrementValGivenKey(hmPrefix, headPrefixes, 1d);

                                incrementValGivenKey(hmPosGivenPrefix, headPrefixes + "\t" + clitic.truthPOS, 1d);



                                // POS given previous word suffix

                                String prevWordSuffix = getPreviousWordSuffix(fullSentence, j);

                                incrementValGivenKey(hmPrevSuffixes, prevWordSuffix, 1d);

                                incrementValGivenKey(hmPosGivenPrevWordSuffix, prevWordSuffix + "\t" + clitic.truthPOS, 1d);

                                

                                // POS given previous word combined POS tag

                                ArrayList<Integer> previousWordClitics = getPreviousWordClitics(fullSentence, j);

                                String previousWordCombinedPOS = "";

                                for (int ip : previousWordClitics)

                                    previousWordCombinedPOS = fullSentence.clitics.get(ip).truthPOS + " " + previousWordCombinedPOS;

                                previousWordCombinedPOS = previousWordCombinedPOS.trim();

                                

                                incrementValGivenKey(hmPrevWordPOS, previousWordCombinedPOS, 1d);

                                incrementValGivenKey(hmPosGivenPrevWordPOS, previousWordCombinedPOS + "\t" + clitic.truthPOS, 1d);

                                

                                // put current Prefix and previous Prefix

                                String prevPrefix = getPreviousWordPrefix(fullSentence, j);

                                String prevAndCurrentPrefix = prevPrefix + "-" + headPrefixes;

                                incrementValGivenKey(hmPrevPrefixandCurrentPrefix, prevAndCurrentPrefix, 1d);

                                incrementValGivenKey(hmPosGivenPrevPrefixandCurrentPrefix, prevAndCurrentPrefix + "\t" + clitic.truthPOS, 1d);



                                // put suffix with POS

                                // get suffixes

                                String tailSuffixes = getCurrentWordSuffix(fullSentence, j);

                                incrementValGivenKey(hmSuffix, tailSuffixes, 1d);

                                incrementValGivenKey(hmPosGivenSuffix, tailSuffixes + "\t" + clitic.truthPOS, 1d);

                                /*

                                 * 

                                 * The code that takes into account gender and number features is deprecated

                                 * because it did not improve results

                                 * 

                                 *

                                // check if noun, adj, or num => which means it has a gender number tag

                                if (clitic.genderNumber.trim().length() > 1)

                                {

                                    // if so add new feature that has POS tags of 4 prev w/ GenderNumber tags + GenderNumber of next 3 tags

                                    Positions = getSurroundingNClitics(fullSentence, j, -3);

                                    String posWithGenderNumber = "";

                                    for (int pwgn = 0; pwgn < Positions.size(); pwgn++)

                                    {

                                        String tmpPOS = fullSentence.clitics.get(Positions.get(pwgn)).truthPOS;

                                        if (fullSentence.clitics.get(Positions.get(pwgn)).genderNumber.trim().length() > 0)

                                            tmpPOS = fullSentence.clitics.get(Positions.get(pwgn)).genderNumber;

                                        posWithGenderNumber += tmpPOS + " ";

                                    }

//                                    posWithGenderNumber += ",";

//                                    Positions = getSurroundingNClitics(fullSentence, j, 1);

//                                    for (int pwgn = 0; pwgn < Positions.size(); pwgn++)

//                                    {

//                                        String tmpPOS = fullSentence.clitics.get(Positions.get(pwgn)).truthPOS;

//                                        if (fullSentence.clitics.get(Positions.get(pwgn)).genderNumber.trim().length() > 0)

//                                            tmpPOS = fullSentence.clitics.get(Positions.get(pwgn)).genderNumber;

//                                        posWithGenderNumber += " " + tmpPOS;

//                                    }

                                    posWithGenderNumber = posWithGenderNumber.trim();

                                    incrementValGivenKey(hmContextPOSwithGenderNumberTags, posWithGenderNumber, 1d);

                                    incrementValGivenKey(hmPosGivenContextPOSwithGenderNumberTags, posWithGenderNumber + "\t" + clitic.truthPOS, 1d);

                                }

                                */

                            }

                            String PosTagSequence = "";

                            if (j == 0) // first clitic in the word

                            {

                                PosTagSequence = "S\t" + clitic.truthPOS;

                            } else {

                                PosTagSequence = fullSentence.clitics.get(j - 1).truthPOS + "\t" + clitic.truthPOS;

                            }

                            incrementValGivenKey(hmPosGivenPos, PosTagSequence, 1d);

                        }

                    }

                }

                word.clear();

                sentence.clear();

                fullSentence.clear();

            } else {

                String entry = entryToAdd(line).trim();

                if (entry.length() > 0) {

                    word.add(entry);

                }

            }

        }

        // now normalize all learnt counts



        // normalize POS given Word & Word given POS

        for (String s : hmPosGivenWord.keySet()) {

            String w = s.substring(0, s.indexOf("\t"));

            String p = s.substring(s.indexOf("\t") + 1);

            double pGivenW = Math.log(hmPosGivenWord.get(s) / hmWord.get(w));

            double wGivenp = Math.log(hmPosGivenWord.get(s) / hmPos.get(p));



            hmPosGivenWord.put(s, pGivenW);

            hmWordGivenPos.put(s, wGivenp);

            if (s.contains("و+")) {

                System.err.println(s + "\t" + pGivenW + "\t" + wGivenp);

            }

        }



        // normalize POS given prefix & suffix

        hmPosGivenPrefix = normalizeHashMapVals(hmPosGivenPrefix, hmPrefix);

        hmPosGivenSuffix = normalizeHashMapVals(hmPosGivenSuffix, hmSuffix);

        // normalize POS give template

        hmPosGivenTemplate = normalizeHashMapVals(hmPosGivenTemplate, hmTemplate);

        // normalize POS given POS

        hmPosGivenPos = normalizeHashMapVals(hmPosGivenPos, hmPos);

        // normalize POS given previousClitic

        hmPosGivenPrevWord = normalizeHashMapVals(hmPosGivenPrevWord, hmWord);

        // normalize POS given nextClitic

        hmPosGivenNextWord = normalizeHashMapVals(hmPosGivenNextWord, hmWord);

        // normalize POS given metaType

        hmPosGivenMetaType = normalizeHashMapVals(hmPosGivenMetaType, hmMetaType);

        // normalize POS given prev & curr prefixes

        hmPosGivenPrevPrefixandCurrentPrefix = 

                normalizeHashMapVals(hmPosGivenPrevPrefixandCurrentPrefix, hmPrevPrefixandCurrentPrefix);

        // normalize POS given prev 2 clitics

        hmPosGivenPrev2Words = normalizeHashMapVals(hmPosGivenPrev2Words, hmBiGramPrev);

        // normalize POS given next 2 clitics

        hmPosGivenNext2Words = normalizeHashMapVals(hmPosGivenNext2Words, hmBiGramNext);        

        // normalize POS given prev 2 POS

        hmPosGivenPrev2Pos = normalizeHashMapVals(hmPosGivenPrev2Pos, hmBiGramPosPrev);        

        // normalize POS given next 2 POS

        hmPosGivenNext2Pos = normalizeHashMapVals(hmPosGivenNext2Pos, hmBiGramPosNext);        

        // normalize POS given prev 3 POS

        hmPosGivenPrev3Pos = normalizeHashMapVals(hmPosGivenPrev3Pos, hmTriGramPosPrev);

        hmPosGivenPrev4Pos = normalizeHashMapVals(hmPosGivenPrev4Pos, hm4GramPosPrev);

        hmPosGivenPrev3Template = normalizeHashMapVals(hmPosGivenPrev3Template, hmTriGramTemplatePrev);

        hmPosGivenNext3Pos = normalizeHashMapVals(hmPosGivenNext3Pos, hmTriGramPosNext);

        hmPosGivenNext4Pos = normalizeHashMapVals(hmPosGivenNext4Pos, hm4GramPosNext);

        // hmPosGivenContextPOSwithGenderNumberTags = normalizeHashMapVals(hmPosGivenContextPOSwithGenderNumberTags, hmContextPOSwithGenderNumberTags);

        // normalize POS given Context 1 POS

        hmPosGivenContext1Pos = normalizeHashMapVals(hmPosGivenContext1Pos, hmUniGramPosContext);

        // normalize POS given Context 2 POS

        hmPosGivenContext2Pos = normalizeHashMapVals(hmPosGivenContext2Pos, hmBiGramPosContext);

        // normalize previous word suffix

        hmPosGivenPrevWordSuffix = normalizeHashMapVals(hmPosGivenPrevWordSuffix, hmPrevSuffixes);

        // normalize previous word combined POS tag

        hmPosGivenPrevWordPOS = normalizeHashMapVals(hmPosGivenPrevWordPOS, hmPrevWordPOS);

        

        for (String s : hmCombinedPOS.keySet())

        {

            hmCombinedPOS.put(s, Math.log(hmCombinedPOS.get(s)));

        }



        br.close();

        // now we are ready to generate training files

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

    

    public ArrayList<Integer> getPreviousWordClitics(Sentence sentence, int position)

    {

        ArrayList<Integer> output = new ArrayList<Integer>();

        int j = position;



        // get the beginning of the current word

        while (!sentence.clitics.get(j).position.equals("B")) {

            j--;

        }

        j--;

        output.add(j);

            

        if (j > 0) // find seperator in previous word

        {

            while (!sentence.clitics.get(j).position.equals("B")) {

                j--;

                output.add(j);

            }

        }

        return output;

    }

    

    public ArrayList<Integer> getSurroundingNClitics(Sentence sentence, int position, int len) // type 0 = surface form; type 1 = truthPOS

    {

        ArrayList<Integer> output = new ArrayList<Integer>();

        int start = Math.min(position, position + len);

        start = Math.max(0, start);



        int end = Math.max(position, position + len);

        end = Math.min(end, sentence.clitics.size() - 1);



        if (len < 0) {

            end--;

        }

        if (len > 0) {

            start++;

        }



        for (int i = start; i <= end; i++) {

            output.add(i);

        }



        return output;

    }



    private String getCurrentWordPrefix(Sentence sentence, int position) {

        String output = "";

        int j = position;

        while (j - 1 > 0 && sentence.clitics.get(j - 1).surface.endsWith("+")) {

            j--;

            output = sentence.clitics.get(j).surface + output;

        }

        if (output.trim().length() == 0) {

            output = "#";

        }

        return output.trim();

    }



    private String getPreviousWordPrefix(Sentence sentence, int position) {

        String output = "";

        int j = position;



        // get the beginning of the current word

        while (!sentence.clitics.get(j).position.equals("B")) {

            j--;

        }

        j--;

        if (j == 0) {

            output = "S";

        } else // find seperator in previous word

        {

            while (!sentence.clitics.get(j).position.equals("B")) {

                j--;

                if (sentence.clitics.get(j).surface.endsWith("+")) {

                    output = sentence.clitics.get(j).surface + output;

                }

            }

        }



        if (output.trim().length() == 0) {

            output = "#";

        }

        return output;

    }

    

    private String getPreviousWordSuffix(Sentence sentence, int position) {

        String output = "";

        int j = position;



        // get the beginning of the current word

        while (!sentence.clitics.get(j).position.equals("B")) {

            j--;

        }

        j--;

        if (j == 0) {

            output = "S";

        } else // find seperator in previous word

        {

            while (!sentence.clitics.get(j).position.equals("B")) {

                if (sentence.clitics.get(j).surface.startsWith("+")) {

                    output = sentence.clitics.get(j).surface + output;

                }

                j--;

            }

        }



        if (output.trim().length() == 0) {

            output = "#";

        }

        return output;

    }



    private String getCurrentWordSuffix(Sentence sentence, int position) {

        String output = "";

        int j = position;

        while (j + 1 < sentence.clitics.size() && sentence.clitics.get(j + 1).surface.startsWith("+")) {

            j++;

            output += sentence.clitics.get(j).surface;

        }

        if (output.trim().length() == 0) {

            output = "#";

        }

        return output;

    }



    private ArrayList<String> fixPotentialSegmentationMismatch(ArrayList<String> word) {

        if (word.size() >= 2 && word.get(word.size() - 1).startsWith("لا") && word.get(word.size() - 1).endsWith("PART") && word.get(word.size() - 2).startsWith("أن") && word.get(word.size() - 2).endsWith("PART")) {

            word.set(word.size() - 2, "ألا\tY\tNOT\tPART");

            word.remove(word.size() - 1);

        } else if (word.size() >= 2 && word.get(word.size() - 1).startsWith("ما") && word.get(word.size() - 1).endsWith("PART") && word.get(word.size() - 2).startsWith("عند") && word.get(word.size() - 2).endsWith("NOUN")) {

            word.set(word.size() - 2, "عندما\tY\tNOT\tNOUN");

            word.remove(word.size() - 1);

        }

        return word;

    }



    public void generateSVM(String filename) throws Exception {

        BufferedReader br = ArabicUtils.openFileForReading(filename);

        BufferedWriter bw = ArabicUtils.openFileForWriting(filename + ".svm");

        BufferedWriter bwGN = ArabicUtils.openFileForWriting(filename + ".gn.svm");

        Sentence fullSentence = new Sentence();

        String line = "";



        // HashMap<String, Double> stats = new HashMap<String, Double>();

        ArrayList<ArrayList<String>> sentence = new ArrayList<ArrayList<String>>();

        ArrayList<String> word = new ArrayList<String>();

        int qid = 1;

        int lineNumber = 0;

        while ((line = br.readLine()) != null) {

            lineNumber++;

            line = line.trim();

            if (line.endsWith("\tO")) {

                if (word.size() > 0) {

                    word = fixPotentialSegmentationMismatch(word);

                    fullSentence.addWord(getWordRepresentation(word));

                    sentence.add(new ArrayList<String>(word));

                }

                word.clear();

            } else if (line.length() == 0) {

                if (word.size() > 0) {

                    word = fixPotentialSegmentationMismatch(word);

                    fullSentence.addWord(getWordRepresentation(word));

                    sentence.add(new ArrayList<String>(word));

                }

                if (fullSentence.clitics.size() > 0) // put end of sentence marker

                {

                    fullSentence.addClitic(new Clitic("E", "E", new ArrayList<String>(), "E", "B", "E"));

                }

                // check if we have a word



                if (fullSentence.clitics.size() > 0) {

                    fullSentence = setAllGuessTagsToTruthTags(fullSentence);

                    fullSentence = addGenderNumberFeatures(fullSentence);

                    for (int j = 0; j < fullSentence.clitics.size(); j++) {

                        Clitic clitic = fullSentence.clitics.get(j);

                        

                        if (clitic.surface.equals("§§") || clitic.surface.equals("S") || clitic.surface.equals("E")) {

                            // do nothing

                        } else {

                            ArrayList<String> possibleTags = possiblePOSTags(fullSentence, j);

                            if (!possibleTags.contains(clitic.truthPOS))

                                possibleTags.add(clitic.truthPOS);

                            

                            // generate training data w/o gender and number

                            for (String tag : possibleTags) {

                                ArrayList<Double> features = getFeaturesValues(fullSentence, j, tag);

                                // rank 2 is the correct tag.  rank 1 is the wrong tag

                                String rank = "1";

                                if (tag.equals(clitic.truthPOS)) {

                                    rank = "2";

                                }



                                bw.write(rank + " qid:" + qid);

                                for (int k = 1; k <= features.size(); k++) {

                                    if (features.get(k - 1) > -20) {

                                        bw.write(" " + k + ":" + features.get(k - 1));

                                    } else {

                                        bw.write(" " + k + ":-10");

                                    }

                                }

                                bw.write("\n");

                            }

                            

                            // 

                            /*

                             * Due to failure to produce better results, this path is deprecated

                             * 

                            if (clitic.truthPOS.equals("NOUN") || clitic.truthPOS.equals("ADJ") || clitic.truthPOS.equals("NUM"))

                            {

                                for (String tag : ("NOUN ADJ NUM").split(" +"))

                                {

                                    if (possibleTags.contains(tag))

                                    {

                                        ArrayList<Double> features = getFeaturesValues(fullSentence, j, tag);

                                        features = getExtraFeaturesValues(fullSentence, j, tag, features);

                                        String rank = "1";

                                        if (tag.equals(clitic.truthPOS)) {

                                            rank = "2";

                                        }



                                        bwGN.write(rank + " qid:" + qid);

                                        for (int k = 1; k <= features.size(); k++) {

                                            if (features.get(k - 1) > -20) {

                                                bwGN.write(" " + k + ":" + features.get(k - 1));

                                            } else {

                                                bwGN.write(" " + k + ":-10");

                                            }

                                        }

                                        bwGN.write("\n");

                                    }

                                }

                            }

                            */

                            // since is done for training and testing only

                            // we set the guessPOS to equal the truthPOS

                            clitic.guessPOS = clitic.truthPOS;

                            qid++;

                        }

                    }

                }



                word.clear();

                sentence.clear();

                fullSentence.clear();

            } else {

                // sub part to word

                String entry = entryToAdd(line).trim();

                if (entry.length() > 0) {

                    word.add(entry);

                }

            }

        }

        bw.close();

        bwGN.close();

    }

    

    private Sentence setAllGuessTagsToTruthTags (Sentence sentence)

    {

        for (int i = 0; i < sentence.clitics.size(); i++)

            sentence.clitics.get(i).guessPOS = sentence.clitics.get(i).truthPOS;

        return sentence;

    }



    public void generateGenderNumberTrainingFiles() throws Exception

    {

        gnt.generateTrainingFile("/Users/kareemdarwish/RESEARCH/ArabicProcessingTools-master/POSandNERData/truth.txt.train");

    }

    

    public Sentence decodeSentence(Sentence fullSentence, ArrayList<Double> model)

    {

        if (fullSentence.clitics.size() > 0) {

            for (int j = 0; j < fullSentence.clitics.size(); j++) {

                Clitic clitic = fullSentence.clitics.get(j);

                if (clitic.surface.equals("§§") || clitic.surface.equals("S") || clitic.surface.equals("E")) {

                    // do nothing

                } else {

                    String bestTag = getBestTag(fullSentence, j, model);

                    clitic.guessPOS = bestTag;

                }

            }

        }

        return fullSentence;

    }

    

    private String getBestTag(Sentence fullSentence, int j, ArrayList<Double> model)

    {

        String bestTag = "";

        Double bestTagScore = -100000000d;

        ArrayList<Double> winningFeatures = new ArrayList<Double>();

        ArrayList<ArrayList<Double>> agg = new ArrayList<ArrayList<Double>>();

        for (String tag : possiblePOSTags(fullSentence, j)) {

            ArrayList<Double> features = getFeaturesValues(fullSentence, j, tag);

            double score = 0;

            for (int fv = 0; fv < features.size(); fv++) {

                score += features.get(fv) * model.get(fv);

            }

            agg.add(new ArrayList<Double>(features));

            if (features.get(features.size() - 1) >= 1d) {

                bestTagScore = 1000d;

                bestTag = tag;

                winningFeatures = new ArrayList<Double>(features);

            } else if (bestTagScore < score) {

                bestTagScore = score;

                bestTag = tag;

                winningFeatures = new ArrayList<Double>(features);

            }

        }

        return bestTag;

    }

    

    private void printToGuessFile(Sentence fullSentence, ArrayList<Double> model, BufferedWriter bw) throws IOException

    {

        if (fullSentence.clitics.size() > 0) {

            for (int j = 0; j < fullSentence.clitics.size(); j++) {

                Clitic clitic = fullSentence.clitics.get(j);

                if (clitic.surface.equals("§§") || clitic.surface.equals("S") || clitic.surface.equals("E")) {

                    // do nothing

                } else {

                    String gn = clitic.genderNumber;

                    if (gn.trim().length() > 0)

                        gn = "-" + gn;

                    bw.write(clitic.surface + "\tT:" + clitic.truthPOS + "\tG:" + clitic.guessPOS + gn);

                    if (!clitic.truthPOS.equals(clitic.guessPOS)) {

                        bw.write("\t*");

                    }

                    if (clitic.truthPOS.equals(clitic.guessPOS)) {

                        // do nothing

                    } else {

                        for (String tag : possiblePOSTags(fullSentence, j)) {

                            ArrayList<Double> features = getFeaturesValues(fullSentence, j, tag);

                            bw.write("\n" + tag + "\t");

                            for (int l = 1; l <= features.size(); l++) {

                                bw.write("\t" + l + ":" + features.get(l - 1));

                            }

                        }

                    }

                    bw.write("\n");

                    bw.write("-----------------\n");

                }

            }

        }

    }

    

    public Sentence tagLine(ArrayList<String> input) throws Exception

    {

        Sentence sentence = new Sentence();

        

        // load clitics into sentence

        for (String word : input)

        {

            ArrayList<Clitic> wordParts = getWordParts2(word);

            for(Clitic c : wordParts)

            {

                sentence.addClitic(c);

            }



        }

        if (sentence.clitics.size() > 0)

            sentence.addClitic(new Clitic("E", "E", new ArrayList<String>(), "E", "B", "E"));

        

        sentence = decodeSentence(sentence, model);

        sentence = addGenderNumberFeatures(sentence);

        

        return sentence;

    }

    

    public void generateSVMAndDecode(String filename) throws Exception {

        BufferedReader br = ArabicUtils.openFileForReading(filename);

        BufferedWriter bw = ArabicUtils.openFileForWriting(filename + ".guess");

        Sentence fullSentence = new Sentence();

        String line = "";



        // feature values from training

//        String[] modelVals = "1:0.14491846 2:0.19861394 3:-0.025112482 4:-0.059626497 5:-0.043406118 6:0.0065765232 7:-0.043859985 8:0.0045893718 9:0.13653383 10:0.13349338 11:-0.053536385 12:0.0087099085 13:0.091892704 14:0.17454976 15:-0.01085883 16:-0.019442562 17:-0.0074988208 18:0.0040499675".split(" +");

//        ArrayList<Double> model = new ArrayList<Double>();

//        for (String s : modelVals) {

//            model.add(Double.parseDouble(s.substring(s.indexOf(":") + 1)));

//        }



        // HashMap<String, Double> stats = new HashMap<String, Double>();

        ArrayList<ArrayList<String>> sentence = new ArrayList<ArrayList<String>>();

        ArrayList<String> word = new ArrayList<String>();

        int qid = 1;

        int lineNumber = 0;

        while ((line = br.readLine()) != null) {

            lineNumber++;

            line = line.trim();

            if (line.endsWith("\tO")) {

                if (word.size() > 0) {

                    word = fixPotentialSegmentationMismatch(word);

                    fullSentence.addWord(getWordRepresentation(word));

                    sentence.add(new ArrayList<String>(word));

                }

                word.clear();

            } else if (line.length() == 0) {

                if (word.size() > 0) {

                    word = fixPotentialSegmentationMismatch(word);

                    fullSentence.addWord(getWordRepresentation(word));

                    sentence.add(new ArrayList<String>(word));

                }

                if (fullSentence.clitics.size() > 0) // put end of sentence marker

                {

                    fullSentence.addClitic(new Clitic("E", "E", new ArrayList<String>(), "E", "B", "E"));

                }

                // check if we have a word

                if (fullSentence.clitics.size() > 0) {

                    fullSentence = decodeSentence(fullSentence, model);

                    fullSentence = addGenderNumberFeatures(fullSentence);

                    printToGuessFile(fullSentence, model, bw);

                    word.clear();

                    sentence.clear();

                    fullSentence.clear();

                }

            } else {

                // sub part to word

                String entry = entryToAdd(line).trim();

                if (entry.length() > 0) {

                    word.add(entry);

                }

            }

        }

        bw.close();

    }



    private Sentence addGenderNumberFeatures(Sentence sentence) throws Exception // this adds the gender and number features

    {

        for (int j = 0; j < sentence.clitics.size(); j++)

        {

            Clitic clitic = sentence.clitics.get(j);

            if (clitic.guessPOS.equals("NOUN") || clitic.guessPOS.equals("ADJ") || clitic.guessPOS.equals("NUM")) // guess gender and number attributes

            {

                // get suffixes that are nsuff only

                String suffix = "";

                String POS = clitic.guessPOS;

                if (POS.equals("ADJ"))

                    POS = "NOUN";

                for (int k = j+1; k < sentence.clitics.size() && !sentence.clitics.get(k).position.equals("B"); k++)

                {

                    if (sentence.clitics.get(k).guessPOS.contains("NSUFF"))

                        suffix += "+" + sentence.clitics.get(k).surface;

                }

                if (suffix.trim().length() > 0)

                    clitic.genderNumber = gnt.getGenderTagRandomForest(clitic.surface + "+" + suffix.replace("+", ""), POS + "+NSUFF", clitic.template, suffix.replace("+", ""));

                else

                    clitic.genderNumber = gnt.getGenderTagRandomForest(clitic.surface, POS, clitic.template, "#");

            }

        }

        return sentence;

    }

    

    private String entryToAdd(String line) {

        String output = "";

        line = line.replace("VSUFF", "PRON");

        if (line.trim().startsWith("تين\t") && line.trim().endsWith("NSUFF")) {

            output = "ت\tY\tNOT\tNSUFF";

            output = "ين\tY\tNOT\tNSUFF";

        } else if (line.trim().startsWith("تان\t") && line.trim().endsWith("NSUFF")) {

            output = "ت\tY\tNOT\tNSUFF";

            output = "ان\tY\tNOT\tNSUFF";

        } else if (line.trim().startsWith("تي\t") && line.trim().endsWith("NSUFF")) {

            output = "ت\tY\tNOT\tNSUFF";

            output = "ي\tY\tNOT\tNSUFF";

        } else if (!line.contains("(نلل)") && line.trim().length() > 0) {

            output = line.replace("[ \t]+.*[ \t]+", "\t");

        }

        return output;

    }



    private ArrayList<Double> getFeaturesValues(Sentence sentence, int j, String tag) {

        Clitic clitic = sentence.clitics.get(j);

        ArrayList<Integer> previousClitic = getSurroundingNClitics(sentence, j, -1);

        ArrayList<Integer> nextClitic = getSurroundingNClitics(sentence, j, 1);

        String prevPrefix = getPreviousWordPrefix(sentence, j);

        String headPrefixes = getCurrentWordPrefix(sentence, j);

        String tailSuffixes = getCurrentWordSuffix(sentence, j);

        String prevSuffixes = getPreviousWordSuffix(sentence, j);

        ArrayList<Integer> prev2Clitics = getSurroundingNClitics(sentence, j, -2);

        ArrayList<Integer> next2Clitics = getSurroundingNClitics(sentence, j, 2);

        ArrayList<Integer> prev3Clitics = getSurroundingNClitics(sentence, j, -3);

        ArrayList<Integer> next3Clitics = getSurroundingNClitics(sentence, j, 3);

        ArrayList<Integer> prev4Clitics = getSurroundingNClitics(sentence, j, -4);

        ArrayList<Integer> next4Clitics = getSurroundingNClitics(sentence, j, 4);

        ArrayList<Double> features = new ArrayList<Double>();

        double highCondProb = 0d;

        double threshold = -0.025d;

        // POS given word prob

        if (hmPosGivenWord.containsKey(clitic.surface + "\t" + tag) && hmPosGivenWord.get(clitic.surface + "\t" + tag) > -4d) {

            features.add(hmPosGivenWord.get(clitic.surface + "\t" + tag));

            if (hmPosGivenWord.get(clitic.surface + "\t" + tag) > threshold) {

                highCondProb++;

            }

        } else {

            features.add(-10d);

        }

        

        // Word given POS prob

        if (hmWordGivenPos.containsKey(clitic.surface + "\t" + tag)) {

            features.add(hmWordGivenPos.get(clitic.surface + "\t" + tag));

        } else {

            features.add(-10d);

        }



        // get template for stem

        if (!clitic.surface.contains("+")) {

            // POS given template

            if (hmPosGivenTemplate.containsKey(clitic.template + "\t" + tag) && hmPosGivenTemplate.get(clitic.template + "\t" + tag) > -4d) {

                features.add(hmPosGivenTemplate.get(clitic.template + "\t" + tag));

                if (hmPosGivenTemplate.get(clitic.template + "\t" + tag) > threshold) {

                    highCondProb++;

                }

            } else {

                features.add(-10d);

            }

            // POS given prefixex

            if (hmPosGivenPrefix.containsKey(headPrefixes + "\t" + tag)) {

                features.add(hmPosGivenPrefix.get(headPrefixes + "\t" + tag));

                if (hmPosGivenPrefix.get(headPrefixes + "\t" + tag) > threshold) {

                    highCondProb++;

                }

            } else {

                features.add(-10d);

            }



            // POS given first suffix

            if (hmPosGivenSuffix.containsKey(tailSuffixes + "\t" + tag)) {

                features.add(hmPosGivenSuffix.get(tailSuffixes + "\t" + tag));

                if (hmPosGivenSuffix.get(tailSuffixes + "\t" + tag) > threshold) {

                    highCondProb++;

                }

            } else {

                features.add(-10d);

            }



            // POS given prev & curr Prefixes

            String prevAndCurrentPrefixes = headPrefixes + "-" + prevPrefix;

            if (hmPosGivenPrevPrefixandCurrentPrefix.containsKey(prevAndCurrentPrefixes + "\t" + tag)) {

                features.add(hmPosGivenPrevPrefixandCurrentPrefix.get(prevAndCurrentPrefixes + "\t" + tag));

                if (hmPosGivenPrevPrefixandCurrentPrefix.get(prevAndCurrentPrefixes + "\t" + tag) > threshold) {

                    highCondProb++;

                }

            } else {

                features.add(-10d);

            }

            

            // POS given prev suffix

            if (hmPosGivenPrevWordSuffix.containsKey(prevSuffixes + "\t" + tag))

            {

                features.add(hmPosGivenPrevWordSuffix.get(prevSuffixes + "\t" + tag));

            }

            else

            {

                features.add(-10d);

            }

            

            // POS given prev word

            ArrayList<Integer> previousWordClitics = getPreviousWordClitics(sentence, j);

            String previousWordCombinedPOS = "";

            for (int ip : previousWordClitics)

                previousWordCombinedPOS = sentence.clitics.get(ip).guessPOS + " " + previousWordCombinedPOS;

            previousWordCombinedPOS = previousWordCombinedPOS.trim();

            if (hmPosGivenPrevWordPOS.containsKey(previousWordCombinedPOS + "\t" + tag))

                features.add(hmPosGivenPrevWordPOS.get(previousWordCombinedPOS + "\t" + tag));

            else

                features.add(-10d);

            

        } else {

            // add dummy entries for template, prefix and suffix

            features.add(-10d);

            features.add(-10d);

            features.add(-10d);

            features.add(-10d);

            features.add(-10d);

            features.add(-10d);

        }



        if (hmPosGivenPrevWord.containsKey(sentence.clitics.get(previousClitic.get(0)).surface + "\t" + tag)) {

            features.add(hmPosGivenPrevWord.get(sentence.clitics.get(previousClitic.get(0)).surface + "\t" + tag));

        } else {

            features.add(-10d);

        }



        if (hmPosGivenNextWord.containsKey(sentence.clitics.get(nextClitic.get(0)).surface + "\t" + tag)) {

            features.add(hmPosGivenNextWord.get(sentence.clitics.get(nextClitic.get(0)).surface + "\t" + tag));

        } else {

            features.add(-10d);

        }



        /*        

         if (hmPosGivenPrev2Words.containsKey(prev2Clitics + "\t" + tag))

         features.add(hmPosGivenPrev2Words.get(prev2Clitics + "\t" + tag));

         else

         features.add(-10d);

        

         if (hmPosGivenNext2Words.containsKey(next2Clitics + "\t" + tag))

         features.add(hmPosGivenNext2Words.get(next2Clitics + "\t" + tag));

         else

         features.add(-10d);

       

         if (hmPosGivenPrev3Words.containsKey(prev3Clitics + "\t" + tag))

         features.add(hmPosGivenPrev3Words.get(prev3Clitics + "\t" + tag));

         else

         features.add(-10d);

        

         if (hmPosGivenNext3Words.containsKey(next3Clitics + "\t" + tag))

         features.add(hmPosGivenNext3Words.get(next3Clitics + "\t" + tag));

         else

         features.add(-10d);

         */

        /*

         String metaType = getMetaType(clitics.get(i));

         if (hmPosGivenMetaType.containsKey(metaType + "\t" + tag))

         features.add(hmPosGivenMetaType.get(metaType + "\t" + tag));

         else

         features.add(-10d);

         */

        double score = 0d;

        // get POS given previous POS

        // add a feature for every tag -- basically we assume that previous word can take any tag

        // then we compute the probability(current tag | previous tag) * probability (previous tag | previous word)

        // for (String tagPrev : possiblePOSTags(sentence, previousClitic.get(0))) {

        //    String PosTagSequence = tagPrev + "\t" + tag;

            String PosTagSequence = sentence.clitics.get(previousClitic.get(0)).guessPOS + "\t" + tag;

            if (hmPosGivenPos.containsKey(PosTagSequence)) {

                score += hmPosGivenPos.get(PosTagSequence);

            }

        //}

        if (score == 0) {

            score = -10d;

        }

        features.add(score);



        // score with prev 2 POS

        score = 0d;

        // String[] prevWords = prev2Clitics.split(" +");

        if (prev2Clitics.size() == 2) {

            String tag0 = sentence.clitics.get(prev2Clitics.get(0)).guessPOS;

            String tag1 = sentence.clitics.get(prev2Clitics.get(1)).guessPOS;

            //for (String tag0 : possiblePOSTags(sentence, prev2Clitics.get(0))) {

            //    for (String tag1 : possiblePOSTags(sentence, prev2Clitics.get(1))) {

                    // String PosTagSequence = tag0 + " " + tag1 + "\t" + tag;

            PosTagSequence = tag0 + " " + tag1 + "\t" + tag;

            if (hmPosGivenPrev2Pos.containsKey(PosTagSequence)) {

                score += hmPosGivenPrev2Pos.get(PosTagSequence);

            }

            //    }

            //}

        }

        else

        {

            PosTagSequence = sentence.clitics.get(previousClitic.get(0)).guessPOS + "\t" + tag;

            if (hmPosGivenPos.containsKey(PosTagSequence)) {

                score += hmPosGivenPos.get(PosTagSequence);

            }

        }

        if (score == 0) {

            score = -10d;

        }

        features.add(score);

        

        // score with next 3 POS

        score = 0d;





        

//        score = 0d;

//        // String[] nextWords = next2Clitics.split(" +");

//        if (next2Clitics.size() == 2) {

//            for (String tag0 : possiblePOSTags(sentence, next2Clitics.get(0))) {

//                for (String tag1 : possiblePOSTags(sentence, next2Clitics.get(1))) {

//                    // String PosTagSequence = tag0 + " " + tag1 + "\t" + tag;

//                    PosTagSequence = tag0 + " " + tag1 + "\t" + tag;

//                    if (hmPosGivenNext2Pos.containsKey(PosTagSequence)) {

//                        score += hmPosGivenNext2Pos.get(PosTagSequence);

//                    }

//                }

//            }

//        }

//        

//        if (score == 0) {

//            score = -10d;

//        }

//        features.add(score);



        // score with POS trigrams

        

        score = 0d;

        // prevWords = prev3Clitics.split(" +");

        if (prev3Clitics.size() == 3) {

            String tag0 = sentence.clitics.get(prev3Clitics.get(0)).guessPOS;

            String tag1 = sentence.clitics.get(prev3Clitics.get(1)).guessPOS;

            String tag2 = sentence.clitics.get(prev3Clitics.get(2)).guessPOS;

            //for (String tag0 : possiblePOSTags(sentence, prev3Clitics.get(0))) {

            //    for (String tag1 : possiblePOSTags(sentence, prev3Clitics.get(1))) {

            //        for (String tag2 : possiblePOSTags(sentence, prev3Clitics.get(2))) {

                        PosTagSequence = tag0 + " " + tag1 + " " + tag2 + "\t" + tag;

                        if (hmPosGivenPrev3Pos.containsKey(PosTagSequence)) {

                            score += hmPosGivenPrev3Pos.get(PosTagSequence);

                        }

            //        }

            //    }

            //}

        }

        else if (prev2Clitics.size() == 2) {

            String tag0 = sentence.clitics.get(prev2Clitics.get(0)).guessPOS;

            String tag1 = sentence.clitics.get(prev2Clitics.get(1)).guessPOS;

            //for (String tag0 : possiblePOSTags(sentence, prev2Clitics.get(0))) {

            //    for (String tag1 : possiblePOSTags(sentence, prev2Clitics.get(1))) {

                    // String PosTagSequence = tag0 + " " + tag1 + "\t" + tag;

            PosTagSequence = tag0 + " " + tag1 + "\t" + tag;

            if (hmPosGivenPrev2Pos.containsKey(PosTagSequence)) {

                score += hmPosGivenPrev2Pos.get(PosTagSequence);

            }

            //    }

            //}

        }

        else

        {

            PosTagSequence = sentence.clitics.get(previousClitic.get(0)).guessPOS + "\t" + tag;

            if (hmPosGivenPos.containsKey(PosTagSequence)) {

                score += hmPosGivenPos.get(PosTagSequence);

            }

        }

        

        if (score == 0) {

            score = -10d;

        }

        features.add(score);

        

        score = 0d;

        // prevWords = prev3Clitics.split(" +");

        if (prev4Clitics.size() == 4) {

            String tag0 = sentence.clitics.get(prev4Clitics.get(0)).guessPOS;

            String tag1 = sentence.clitics.get(prev4Clitics.get(1)).guessPOS;

            String tag2 = sentence.clitics.get(prev4Clitics.get(2)).guessPOS;

            String tag3 = sentence.clitics.get(prev4Clitics.get(3)).guessPOS;

            PosTagSequence = tag0 + " " + tag1 + " " + tag2 + " " + tag3 + "\t" + tag;

            if (hmPosGivenPrev4Pos.containsKey(PosTagSequence)) {

                score += hmPosGivenPrev4Pos.get(PosTagSequence);

            }

        }

        else if (prev3Clitics.size() == 3) {

            String tag0 = sentence.clitics.get(prev3Clitics.get(0)).guessPOS;

            String tag1 = sentence.clitics.get(prev3Clitics.get(1)).guessPOS;

            String tag2 = sentence.clitics.get(prev3Clitics.get(2)).guessPOS;

            PosTagSequence = tag0 + " " + tag1 + " " + tag2 + "\t" + tag;

            if (hmPosGivenPrev3Pos.containsKey(PosTagSequence)) {

                score += hmPosGivenPrev3Pos.get(PosTagSequence);

            }

        }

        else if (prev2Clitics.size() == 2) {

            String tag0 = sentence.clitics.get(prev2Clitics.get(0)).guessPOS;

            String tag1 = sentence.clitics.get(prev2Clitics.get(1)).guessPOS;

            PosTagSequence = tag0 + " " + tag1 + "\t" + tag;

            if (hmPosGivenPrev2Pos.containsKey(PosTagSequence)) {

                score += hmPosGivenPrev2Pos.get(PosTagSequence);

            }

        }

        else

        {

            PosTagSequence = sentence.clitics.get(previousClitic.get(0)).guessPOS + "\t" + tag;

            if (hmPosGivenPos.containsKey(PosTagSequence)) {

                score += hmPosGivenPos.get(PosTagSequence);

            }

        }

        

        if (score == 0) {

            score = -10d;

        }

        features.add(score);

        

        

        

//        score = 0d;

//        

//        String prev3Template = "";

//        for (int cnt = 0; cnt < prev3Clitics.size(); cnt++)

//        {

//            if (sentence.clitics.get(prev3Clitics.get(cnt)).surface.endsWith("+") || sentence.clitics.get(prev3Clitics.get(cnt)).surface.startsWith("+"))

//                prev3Template += " " + sentence.clitics.get(prev3Clitics.get(cnt)).guessPOS;

//            else

//                prev3Template += " " + sentence.clitics.get(prev3Clitics.get(cnt)).template;

//        }

//        prev3Template = prev3Template.trim();

//        if (hmPosGivenPrev3Template.containsKey(prev3Template + "\t" + clitic.guessPOS))

//            score = hmPosGivenPrev3Template.get(prev3Template + "\t" + clitic.guessPOS);

//        else

//            score = -10d;

//        features.add(score);

        /*

        score = 0d;

        // nextWords = next3Clitics.split(" +");

        if (next3Clitics.size() == 3) {

            for (String tag0 : possiblePOSTags(sentence, next3Clitics.get(0))) {

                for (String tag1 : possiblePOSTags(sentence, next3Clitics.get(1))) {

                    for (String tag2 : possiblePOSTags(sentence, next3Clitics.get(2))) {

                        String PosTagSequence = tag0 + " " + tag1 + " " + tag2 + "\t" + tag;

                        if (hmPosGivenNext3Pos.containsKey(PosTagSequence)) {

                            score += hmPosGivenNext3Pos.get(PosTagSequence);

                        }

                    }

                }

            }

        }

        if (score == 0) {

            score = -10d;

        }

        features.add(score);

        */

        

        // prev and next POS tags

        score = 0d;

        // for (String tag0 : possiblePOSTags(sentence, previousClitic.get(0))) {

        String tag0 = sentence.clitics.get(previousClitic.get(0)).guessPOS;

        for (String tag1 : possiblePOSTags(sentence, nextClitic.get(0))) {

            PosTagSequence = tag0 + " " + tag1 + "\t" + tag;

            if (hmPosGivenContext1Pos.containsKey(PosTagSequence)) {

                score += hmPosGivenContext1Pos.get(PosTagSequence);

            }

        }

        if (score == 0) {

            score = -10d;

        }

        features.add(score);



        score = 0d;

        // nextWords = next3Clitics.split(" +");

        if (next2Clitics.size() == 2) {

            for (String tagA : possiblePOSTags(sentence, next2Clitics.get(0))) {

                for (String tagB : possiblePOSTags(sentence, next2Clitics.get(1))) {

                    PosTagSequence = tagA + " " + tagB + "\t" + tag;

                    if (hmPosGivenNext2Pos.containsKey(PosTagSequence)) {

                        score += hmPosGivenNext2Pos.get(PosTagSequence);

                    }

                }

            }

        }

        if (score == 0) {

            score = -10d;

        }

        features.add(score);

        

        score = 0d;

        // nextWords = next3Clitics.split(" +");

        if (next3Clitics.size() == 3) {

            for (String tagA : possiblePOSTags(sentence, next3Clitics.get(0))) {

                for (String tagB : possiblePOSTags(sentence, next3Clitics.get(1))) {

                    for (String tagC : possiblePOSTags(sentence, next3Clitics.get(2))) {

                        PosTagSequence = tagA + " " + tagB + " " + tagC + "\t" + tag;

                        if (hmPosGivenNext3Pos.containsKey(PosTagSequence)) {

                            score += hmPosGivenNext3Pos.get(PosTagSequence);

                        }

                    }

                }

            }

        }

        else if (next2Clitics.size() == 2) {

            for (String tagA : possiblePOSTags(sentence, next2Clitics.get(0))) {

                for (String tagB : possiblePOSTags(sentence, next2Clitics.get(1))) {

                    PosTagSequence = tagA + " " + tagB + "\t" + tag;

                    if (hmPosGivenNext2Pos.containsKey(PosTagSequence)) {

                        score += hmPosGivenNext2Pos.get(PosTagSequence);

                    }

                }

            }

        }

        if (score == 0) {

            score = -10d;

        }

        features.add(score);

/*        

        score = 0d;

        // nextWords = next3Clitics.split(" +");

        if (next4Clitics.size() == 4) {

            for (String tagA : possiblePOSTags(sentence, next4Clitics.get(0))) {

                for (String tagB : possiblePOSTags(sentence, next4Clitics.get(1))) {

                    for (String tagC : possiblePOSTags(sentence, next4Clitics.get(2))) {

                        for (String tagD : possiblePOSTags(sentence, next4Clitics.get(3))) {

                            PosTagSequence = tagA + " " + tagB + " " + tagC + " " + tagD + "\t" + tag;

                            if (hmPosGivenNext3Pos.containsKey(PosTagSequence)) {

                                score += hmPosGivenNext3Pos.get(PosTagSequence);

                            }

                        }

                    }

                }

            }

        }

        else if (next3Clitics.size() == 3) {

            for (String tagA : possiblePOSTags(sentence, next3Clitics.get(0))) {

                for (String tagB : possiblePOSTags(sentence, next3Clitics.get(1))) {

                    for (String tagC : possiblePOSTags(sentence, next3Clitics.get(2))) {

                        PosTagSequence = tagA + " " + tagB + " " + tagC + "\t" + tag;

                        if (hmPosGivenNext3Pos.containsKey(PosTagSequence)) {

                            score += hmPosGivenNext3Pos.get(PosTagSequence);

                        }

                    }

                }

            }

        }

        else if (next2Clitics.size() == 2) {

            for (String tagA : possiblePOSTags(sentence, next2Clitics.get(0))) {

                for (String tagB : possiblePOSTags(sentence, next2Clitics.get(1))) {

                    PosTagSequence = tagA + " " + tagB + "\t" + tag;

                    if (hmPosGivenNext2Pos.containsKey(PosTagSequence)) {

                        score += hmPosGivenNext2Pos.get(PosTagSequence);

                    }

                }

            }

        }

        if (score == 0) {

            score = -10d;

        }

        features.add(score);

*/        

        

//        } else if (score > 0) {

//            score = Math.log(score);

//        }

        // features.add(score);

        if (highCondProb > 0) {

            features.add(1 + Math.log(highCondProb));

        } else {

            features.add(0d);

        }



        return features;

    }

    

    private ArrayList<Double> getExtraFeaturesValues(Sentence sentence, int j, String tag, ArrayList<Double> features) {

        /*

         * 

         * There was hope that adding the gender and number features would improve POS tagging

         * but unfortunately, it kept results the same when used causiously

         * when used extensively, it made things significantly worse.

         * 

         * THIS IS DEPRECATED

         * 

         *

        Clitic clitic = sentence.clitics.get(j);

        ArrayList<Integer> prev4Clitics = getSurroundingNClitics(sentence, j, -3);

        ArrayList<Integer> next3Clitics = getSurroundingNClitics(sentence, j, 1);



        // get surrounding POS tags

        String posWithGenderNumber = "";

        for (int pwgn = 0; pwgn < prev4Clitics.size(); pwgn++)

        {

            String tmpPOS = sentence.clitics.get(prev4Clitics.get(pwgn)).guessPOS;

            if (sentence.clitics.get(prev4Clitics.get(pwgn)).genderNumber.trim().length() > 0)

                tmpPOS = sentence.clitics.get(prev4Clitics.get(pwgn)).genderNumber;

            posWithGenderNumber += tmpPOS + " ";

        }

//        posWithGenderNumber += ",";

//        for (int pwgn = 0; pwgn < next3Clitics.size(); pwgn++)

//        {

//            String tmpPOS = sentence.clitics.get(next3Clitics.get(pwgn)).guessPOS;

//            if (sentence.clitics.get(next3Clitics.get(pwgn)).genderNumber.trim().length() > 0)

//                tmpPOS = sentence.clitics.get(next3Clitics.get(pwgn)).genderNumber;

//            posWithGenderNumber += " " + tmpPOS;

//        }

        posWithGenderNumber = posWithGenderNumber.trim();

        

        if (hmPosGivenContextPOSwithGenderNumberTags.containsKey(posWithGenderNumber + "\t" + tag))

            features.add(hmPosGivenContextPOSwithGenderNumberTags.get(posWithGenderNumber + "\t" + tag));

        else

            features.add(-10d);

            */

        return features;

    }



    private String getPreviousClitic(ArrayList<ArrayList<String>> sentence, ArrayList<String> clitics, int i, int j) {

        String previousClitic = "";

        if (i == 0) // first clitic in the word

        {

            if (j == 0) // first word in the sentence

            {

                previousClitic = "S";

            } else {

                ArrayList<String> tmp = getWordParts(sentence.get(j - 1));

                previousClitic = tmp.get(tmp.size() - 1);

            }

        } else {

            previousClitic = clitics.get(i - 1);

        }

        return previousClitic;

    }



    private String getPreviousWordPrefix(ArrayList<ArrayList<String>> sentence, ArrayList<String> clitics, int i, int j) {

        String prevPrefix = "";

        if (j == 0) // first word in sentence

        {

            prevPrefix = "S";

        } else {

            prevPrefix = getWordPrefix(sentence.get(j - 1));

            if (prevPrefix.equals("")) {

                prevPrefix = "#";

            }

        }

        return prevPrefix;

    }



    private String getNextClitic(ArrayList<ArrayList<String>> sentence, ArrayList<String> clitics, int i, int j) {

        String nextClitic = "";

        if (i == clitics.size() - 1) // last clitic in the word

        {

            if (j == sentence.size() - 1) // last word in the sentence

            {

                nextClitic = "E";

            } else {

                ArrayList<String> tmp = getWordParts(sentence.get(j + 1));

                nextClitic = tmp.get(0);

            }

        } else {

            nextClitic = clitics.get(i + 1);

        }

        return nextClitic;

    }



    private ArrayList<String> possiblePOSTags(Sentence sentence, int j) {
        ArrayList<String> output = new ArrayList<String>();
        Clitic clitic = sentence.clitics.get(j);
        if (clitic.surface.equals("S")) {
            output.add("S");
            return output;
        }

        for (String p : hmPos.keySet()) {
            if (hmPosGivenWord.containsKey(clitic.surface + "\t" + p)) {
                output.add(p);
            }
        }

        if (output.size() == 0 || hmWord.get(clitic.surface) < 2) {
            output.clear();
            /*
             ABBREV
             NUM
             FOREIGN
             PUNC
             */
            String metaTag = getMetaType(clitic.surface);
            if (clitic.surface.equals("§§")) {
                output.add("§");
            } else if (metaTag.equals("NUM")) {
                output.add("NUM");
            } else if (metaTag.equals("PUNC")) {
                output.add("PUNC");
            } else if (metaTag.equals("FOREIGN")) {
                output.add("FOREIGN");
            } else {
                output.addAll(hmPosNormal.keySet());
                if (clitic.surface.length() <= 2 && !clitic.surface.contains("+")) {
                    output.add("ABBREV");
                }
            }
        }

        ArrayList<String> tmp = new ArrayList<String>();
        if (clitic.surface.startsWith("+"))
        {
            for (String t : output) {
                if (hPossibleSuffix.containsKey(t.trim()))
                    tmp.add(t.trim());
            }
        }
        else if (clitic.surface.endsWith("+"))
        {
            for (String t : output) {
                if (hPossiblePrefix.containsKey(t.trim()))
                    tmp.add(t.trim());
            }
        }
        else
        {
            for (String t : output) {
                boolean addTag = true;
                boolean extremeCase = true;
                if (!hmPosGivenTemplate.containsKey(clitic.template + "\t" + t.trim())) // check if tag can have this template
                    addTag = false;
                else if (hmPosGivenTemplate.get(clitic.template + "\t" + t.trim()) < -5) // check if the prob of tag given template is high enough
                    addTag = false;
                else if (!hmPosGivenPrefix.containsKey(getCurrentWordPrefix(sentence, j) + "\t" + t.trim()))
                    addTag = false;
                else if (hmPosGivenPrefix.get(getCurrentWordPrefix(sentence, j) + "\t" + t.trim()) < -5)
                    addTag = false;
                else if (!hmPosGivenSuffix.containsKey(getCurrentWordSuffix(sentence, j) + "\t" + t.trim()))
                {
                    addTag = false;
                    extremeCase = false;
                }
                else if (hmPosGivenSuffix.get(getCurrentWordSuffix(sentence, j) + "\t" + t.trim()) < -5)
                    addTag = false;
                else if (!hmPosGivenPos.containsKey(sentence.clitics.get(j-1).guessPOS + "\t" + t.trim()))
                {
                    addTag = false;
                    extremeCase = false;
                }

                // heuristic to prohibit an ADJ without a NOUN/NUM/ADJ before it

//                if (t.trim().equals("ADJ"))

//                {

//                    ArrayList<String> previousWordPOS = new ArrayList<String>();

//                    int prevWordPos = j;

//                    for (int pwc : getPreviousWordClitics(sentence, j))

//                    {

//                        previousWordPOS.add(sentence.clitics.get(pwc).guessPOS);

//                        prevWordPos = pwc;

//                    }   

//                    if (previousWordPOS.contains("PUNC"))

//                    {

//                    previousWordPOS.clear();

//                    for (int pwc : getPreviousWordClitics(sentence, prevWordPos))

//                    {

//                        previousWordPOS.add(sentence.clitics.get(pwc).guessPOS);

//                    }   

//                    }

//                    

//                    if (previousWordPOS.contains("V"))

////                            !previousWordPOS.contains("NOUN") 

////                            && !previousWordPOS.contains("NUM") 

////                            && !previousWordPOS.contains("ADJ"))

//                    {

//                        extremeCase = false;

//                    }

//                }

                if (addTag == false 
                        && hmPosGivenWord.containsKey(clitic.surface + "\t" + t.trim()) 
                        && hmPosGivenWord.get(clitic.surface + "\t" + t.trim()) > -2.3)
                    addTag = true;
                if (addTag && extremeCase)
                tmp.add(t.trim());
            }
        }

        if (tmp.size() == 0)

        {

            for (String t : output) {
                if (clitic.surface.trim().startsWith("+") && !hPossibleSuffix.containsKey(t.trim())) {
                    // do nothing
                } else if (clitic.surface.trim().endsWith("+") && !hPossiblePrefix.containsKey(t.trim())) {
                    // do nothing
                } else {
                    tmp.add(t.trim());
                }
            }
        }

        if (clitic.surface.equals("+ت") && j > 0 && sentence.clitics.get(j-1).guessPOS.contains("NOUN") 
                && tmp.contains("PRON") && j < sentence.clitics.size() - 1 && sentence.clitics.get(j+1).surface.startsWith("+"))
            tmp.remove("PRON");

        if (verbOrNot.containsKey(clitic.surface) && !hmWord.containsKey(clitic.surface))
        {
            if (verbOrNot.get(clitic.surface).equals("V"))
            {
                tmp.clear();
                tmp.add("V");
            }
            else if (verbOrNot.get(clitic.surface).equals("not-V"))
            {
                tmp.remove("V");
            }
            else if (verbOrNot.get(clitic.surface).equals("possible-V"))
            {
                if (!tmp.contains("V"))
                    tmp.add("V");
            }
        }
        
        if (clitic.surface.equals("أول")
                || clitic.surface.equals("حادي")
                || clitic.surface.equals("ثاني")
                || clitic.surface.equals("ثالث")
                || clitic.surface.equals("رابع")
                || clitic.surface.equals("خامس")
                || clitic.surface.equals("سادس")
                || clitic.surface.equals("سابع")
                || clitic.surface.equals("ثامن")
                || clitic.surface.equals("تاسع")
                || clitic.surface.equals("عاشر")
                )
        {
            tmp.clear();
            tmp.add("ADJ");
        }
        
        if (nounOrNot.containsKey(clitic.surface) && !hmWord.containsKey(clitic.surface))
        {
            if (nounOrNot.get(clitic.surface).equals("NOUN"))
            {
                tmp.clear();
                tmp.add("NOUN");
            }
            else if (nounOrNot.get(clitic.surface).equals("not-NOUN"))
            {
            //    if (tmp.contains("NOUN"))
            //        tmp.remove("NOUN");
            }
            else if (nounOrNot.get(clitic.surface).equals("possible-NOUN"))
            {
                if (!tmp.contains("NOUN"))
                    tmp.add("NOUN");
            }
        }
        
        if (tmp.size() > 1)
            tmp.remove("FOREIGN");
        
        return tmp;

    }



    private String getStemFromWordParts(ArrayList<String> segmentedWord) {

        String output = "";

        for (String s : segmentedWord) {

            if (!s.contains("+")) {

                output = s;

            }

        }

        return output;

    }



    private ArrayList<String> getWordParts(String s) {

        ArrayList<String> output = new ArrayList<String>();

        String segmentedWord = s;



        segmentedWord = getProperSegmentation(segmentedWord);

        String[] parts = (" " + segmentedWord + " ").split(";");

        for (String p : parts[0].split("\\+")) {

            if (p.trim().length() > 0) {

                output.add(p + "+");

            }

        }

        if (parts[1].trim().length() > 0) {

            output.add(parts[1].trim());

        }

        for (String p : parts[2].split("\\+")) {

            if (p.trim().length() > 0) {

                output.add("+" + p);

            }

        }

        return output;

    }



    private ArrayList<Clitic> getWordParts2(String s) {

        //this method has been added by Mohamed Eldesouki

        //it does the same thing as method getWordParts but

        //the main reason for creating it is to get rid of

        // semicolon as away of separating the stem from affixes of the word

        // this method is only used by the method tagLine

        ArrayList<Clitic> output = new ArrayList<Clitic>();

        String segmentedWord = s;



        segmentedWord = farasa.getProperSegmentation(segmentedWord);

        String[] parts = (" " + segmentedWord + " ").split(";");

        String position = "B";

        for (String p : parts[0].split("\\+")) {

            if (p.trim().length() > 0) {

                Clitic clitic = new Clitic(p.trim()+"+", farasa.getStemTempate(p.trim()), null, "", position, "");

                position = "I";

                if (p.trim().equals("ال"))

                        clitic.det = "y";

                //output.add(p + "+");

                output.add(clitic);

            }

        }

        if (parts[1].trim().length() > 0) {

            Clitic clitic = new Clitic(parts[1].trim(), farasa.getStemTempate(parts[1].trim()), null, "", position, "");

            clitic.isStem = "y";

            position = "I";

            output.add(clitic);

            //output.add(parts[1].trim());

        }

        for (String p : parts[2].split("\\+")) {

            if (p.trim().length() > 0) {

                Clitic clitic = new Clitic("+"+p.trim(), farasa.getStemTempate(p.trim()), null, "", position, "");

                position = "I";

                output.add(clitic);

                //output.add("+" + p);

            }

        }

        return output;

    }

    

    private ArrayList<String> getWordParts(ArrayList<String> wordPOS) {

        ArrayList<String> output = new ArrayList<String>();

        String segmentedWord = wordPOS.get(0).substring(0, wordPOS.get(0).indexOf("\t"));

        for (int i = 1; i < wordPOS.size(); i++) {

            segmentedWord += "+" + wordPOS.get(i).substring(0, wordPOS.get(i).indexOf("\t"));

        }

        segmentedWord = farasa.getProperSegmentation(segmentedWord);

        String[] parts = (" " + segmentedWord + " ").split(";");

        for (String p : parts[0].split("\\+")) {

            if (p.trim().length() > 0) {

                output.add(p + "+");

            }

        }

        if (parts[1].trim().length() > 0) {

            output.add(parts[1].trim());

        }

        for (String p : parts[2].split("\\+")) {

            if (p.trim().length() > 0) {

                output.add("+" + p);

            }

        }

        return output;

    }



    private String getWordPrefix(ArrayList<String> wordPOS) {

        ArrayList<String> output = new ArrayList<String>();

        String segmentedWord = wordPOS.get(0).substring(0, wordPOS.get(0).indexOf("\t"));

        for (int i = 1; i < wordPOS.size(); i++) {

            segmentedWord += "+" + wordPOS.get(i).substring(0, wordPOS.get(i).indexOf("\t"));

        }

        segmentedWord = getProperSegmentation(segmentedWord);

        String[] parts = (" " + segmentedWord + " ").split(";");

        return parts[0].trim();

    }



    private ArrayList<String> getPosParts(ArrayList<String> wordPOS) {

        ArrayList<String> output = new ArrayList<String>();



        for (int i = 0; i < wordPOS.size(); i++) {

            output.add(wordPOS.get(i).substring(wordPOS.get(i).lastIndexOf("\t") + 1));

        }

        return output;

    }



    public String getProperSegmentation(String input) {

        return farasa.getProperSegmentation(input);

//        if (hPrefixes.isEmpty()) {

//            for (int i = 0; i < prefixes.length; i++) {

//                hPrefixes.put(prefixes[i].toString(), 1);

//            }

//        }

//        if (hSuffixes.isEmpty()) {

//            for (int i = 0; i < suffixes.length; i++) {

//                hSuffixes.put(suffixes[i].toString(), 1);

//            }

//        }

//        hSuffixes.put("ما", 1);

//        hSuffixes.put("و", 1);

//        hSuffixes.put("تا", 1);

//        hSuffixes.put("من", 1);

//        hSuffixes.put("ي", 1);

//        hSuffixes.put("ني", 1);

//

//        String output = "";

//        String[] word = input.split("\\+");

//        String currentPrefix = "";

//        String currentSuffix = "";

//        int iValidPrefix = -1;

//        while (iValidPrefix + 1 < word.length && hPrefixes.containsKey(word[iValidPrefix + 1])) {

//            iValidPrefix++;

//        }

//

//        int iValidSuffix = word.length;

//

//        while (iValidSuffix > Math.max(iValidPrefix, 0) && (hSuffixes.containsKey(word[iValidSuffix - 1])

//                || word[iValidSuffix - 1].equals("_"))) {

//            iValidSuffix--;

//        }

//

//        for (int i = 0; i <= iValidPrefix; i++) {

//            currentPrefix += word[i] + "+";

//        }

//        String stemPart = "";

//        for (int i = iValidPrefix + 1; i < iValidSuffix; i++) {

//            stemPart += word[i];

//        }

//

//        if (iValidSuffix == iValidPrefix) {

//            iValidSuffix++;

//        }

//

//        for (int i = iValidSuffix; i < word.length && iValidSuffix != iValidPrefix; i++) {

//            currentSuffix += "+" + word[i];

//        }

//

//        if (currentPrefix.endsWith("س+") && !stemPart.matches("^[ينأت].*")) {

//            currentPrefix = currentPrefix.substring(0, currentPrefix.length() - 2);

//            stemPart = "س" + stemPart;

//        }

//        if (currentPrefix.trim().length() == 0 && stemPart.trim().length() == 0)

//            output = ";" + currentSuffix.replace("+", "") + ";";

//        else

//            output = currentPrefix + ";" + stemPart + ";" + currentSuffix;

//        output = output.replaceFirst("^\\+", "");

//        output = output.replaceFirst("\\+$", "");

//

//        return output.replace("++", "+");

    }



    private String isNumber(String input) {

        // if (hmNumber.containsKey(input.trim()) || input.matches("[" + AllHindiDigits + "0-9\\.,\u00BC-\u00BE]+")) {

        if (hmNumber.containsKey(input.trim()) || rAllNumbers.matcher(input).matches()) {

            return "NUM";

        } else {

            return "NOT";

        }

    }



    private String getMetaType(String input) {

        if (isNumber(input.trim()).equals("NUM")) {

            return "NUM";

        } else if (input.trim().startsWith("+") && input.trim().length() > 1) {

            return "PREFIX";

        } else if (input.trim().endsWith("+") && input.trim().length() > 1) {

            return "SUFFIX";

        // } else if (input.trim().matches(".*[a-zA-z]+.*")) {

        } else if (rEnglishLetters.matcher(input.trim()).matches()) {

            return "FOREIGN";

        // } else if (input.trim().matches("[" + AllArabicLetters + "]+")) {

        } else if (rAllArabicLetters.matcher(input.trim()).matches())

        {

            return "ARAB";

        // } else if (input.trim().matches(".*[" + ALLDelimiters + "]+.*"))

        } else if (rAllDelimiters.matcher(input.trim()).matches())

        {

            return "PUNC";

        } 

        else {

            return "OTHER";

        }

    }



}