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
import static com.qcri.farasa.segmenter.ArabicUtils.buck2morph;
import static com.qcri.farasa.segmenter.ArabicUtils.utf82buck;
import com.qcri.farasa.segmenter.Farasa;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import weka.core.Instances;

/**
 *
 * @author kareemdarwish
 */
public class genderNumberTags {
    private static String BinDir;
    private static final HashMap<String, Integer> hPrefixes = new HashMap<String, Integer>();
    private static final HashMap<String, Integer> hSuffixes = new HashMap<String, Integer>();
    
    private static final HashMap<String, Double> hmRoot = new HashMap<String, Double>();
    private static final HashMap<String, Double> hmTemplate = new HashMap<String, Double>();
    private static final HashMap<Integer, ArrayList<String>> Templates = new HashMap<Integer, ArrayList<String>>();
    private static final HashMap<String, Integer> hmNumber = new HashMap<String, Integer>();
    private static final HashMap<String, Integer> hmGender = new HashMap<String, Integer>();
    private static Integer iGenderCountPublic = 0;
    private static final HashMap<String, Integer> hmFeatureLabelCount = new HashMap<String, Integer>();
    private static final HashMap<String, String> hmGenderGaz = new HashMap<String, String>();
    private static final HashMap<String, String> hmGenderNEGaz = new HashMap<String, String>();
    
    private static final HashMap<String, ArrayList<String>> hmPreviouslySeenTokenizations = new HashMap<String, ArrayList<String>>();
    private static final HashMap<String, Integer> hmListMorph = new HashMap<String, Integer>();
    private static final HashMap<String, Integer> hmListGaz = new HashMap<String, Integer>();
    
    // this data structure stores previously seen segmentations
    // eventually it should be serialized to avoid running the segmenter on previously seen words
    private static final HashMap<String, String> hmWordSegmentation = new HashMap<String, String>();
    
    private static final HashMap<String, ArrayList<String>> hmWordPossibleSplits = new HashMap<String, ArrayList<String>>();
    
    private static final HashMap<String, Double> hmAsmaaIsharaLM = new HashMap<String, Double>(1000000);
    private static final HashMap<String, String> hmSeenBefore = new HashMap<String, String>(1000000);
    
    private static final String dataHeaderGender = "@RELATION guessGender\n"
                + "@ATTRIBUTE tafeel {\"fElY\",\"tfElC\",\"fElC\",\">fEl\",\"nfAEl\",\"mfElE\",\">stfEl\",\"Y\",\"ntfEl\",\"mfElp\",\"AftEl\",\">fElp\",\"fEAlp\",\"fEyEl\",\"AfEnll\",\"fEll\",\"yfEl\",\"ystfEl\",\"ytfEl\",\"fAElp\",\"fE\",\"AftEAl\",\"AnfEAl\",\"ntfAEl\",\"tfElp\",\"fEyl\",\"fEl\",\"fElAnp\",\"tfAEl\",\"mfEyl\",\">fAEl\",\"AfEwEl\",\"tstfEl\",\"ytfAEl\",\"fElAn\",\"AfEAl\",\"fElAl\",\"nstfEl\",\">tfEl\",\"mstfEl\",\">fEAl\",\"fEAly\",\"fAEwl\",\"mfAEyl\",\"fEAll\",\"tfEl\",\"AstfEAl\",\"mfEl\",\"fEA}l\",\"AstfEl\",\">tfAEl\",\"AfEwl\",\"<fEAl\",\"AfEl\",\"ttfEl\",\"yfElC\",\"yfEll\",\"fEyEyl\",\"mfAElp\",\"AfEnlAl\",\"fEwlp\",\"tfEyl\",\"<fElp\",\"mfAEl\",\">fElC\",\"fEAl\",\"fElp\",\"mfEAl\",\"ttfAEl\",\"fwAEl\",\"AfEyEAl\",\"mfEwl\",\"AfEylAl\",\"yfAEl\",\"tfEll\",\"fEly\",\"mfElC\",\"fEAlA\",\"mftEl\",\"AnfEl\",\"fElCC\",\"fAEl\",\">fElA'\",\"fElA'\",\"AfElAl\",\"fEllp\",\"nfElC\",\"fEwl\",\"AfEwAl\",\"AfEll\",\"nfEl\",\">fAEyl\",\">fEwl\",\"fEAlC\",\"tfAEyl\",\"fEAlyC\",\"fElAC\",\"fwAEyl\",\"mtfAEl\",\"mtfEl\",\"mnfEl\",\"tftEl\",\"fElwC\",\"mfElAn\",\">nfEl\",\"fwEl\",\"fAEyl\",\"mmfEl\",\"tfEAl\",\"ynfEl\"}\n"
                + "@ATTRIBUTE pos {DET+NOUN,DET+NOUN+NSUFF,DET+NUM,DET+NUM+NSUFF,NOUN,NOUN+NSUFF,NUM,NUM+NSUFF}\n"
                + "@ATTRIBUTE lastTwoLetter {#,ا,ة,ت,ي,ات,ان,ون,ين,ك,تي,تا,تين}\n"
                + "@ATTRIBUTE length {1,2,3,4,5,6,7,8,9,10}\n"
                + "@ATTRIBUTE Fem {YES,NO}\n"
                + "@ATTRIBUTE isGender {OOV,FeminineMasculine,FEMININE,MASCULINE}\n"
                + "@ATTRIBUTE isNumber {OOV,PluralSingular,PLURAL,SINGULAR}\n"
                + "@ATTRIBUTE hvh NUMERIC\n"
                + "@ATTRIBUTE hvA NUMERIC\n"
                + "@ATTRIBUTE hvAn NUMERIC\n"
                + "@ATTRIBUTE hAtAn NUMERIC\n"
                + "@ATTRIBUTE hAlAA NUMERIC\n"
                + "@ATTRIBUTE hvyn NUMERIC\n"
                + "@ATTRIBUTE hAtyn NUMERIC\n"
                + "@ATTRIBUTE hAvyn NUMERIC\n"
                + "@ATTRIBUTE class {M,F}\n"
                + "@DATA\n";

    private static final String dataHeaderNumber = "@RELATION guessGender\n"
                + "@ATTRIBUTE tafeel {\"fElY\",\"tfElC\",\"fElC\",\">fEl\",\"nfAEl\",\"mfElE\",\">stfEl\",\"Y\",\"ntfEl\",\"mfElp\",\"AftEl\",\">fElp\",\"fEAlp\",\"fEyEl\",\"AfEnll\",\"fEll\",\"yfEl\",\"ystfEl\",\"ytfEl\",\"fAElp\",\"fE\",\"AftEAl\",\"AnfEAl\",\"ntfAEl\",\"tfElp\",\"fEyl\",\"fEl\",\"fElAnp\",\"tfAEl\",\"mfEyl\",\">fAEl\",\"AfEwEl\",\"tstfEl\",\"ytfAEl\",\"fElAn\",\"AfEAl\",\"fElAl\",\"nstfEl\",\">tfEl\",\"mstfEl\",\">fEAl\",\"fEAly\",\"fAEwl\",\"mfAEyl\",\"fEAll\",\"tfEl\",\"AstfEAl\",\"mfEl\",\"fEA}l\",\"AstfEl\",\">tfAEl\",\"AfEwl\",\"<fEAl\",\"AfEl\",\"ttfEl\",\"yfElC\",\"yfEll\",\"fEyEyl\",\"mfAElp\",\"AfEnlAl\",\"fEwlp\",\"tfEyl\",\"<fElp\",\"mfAEl\",\">fElC\",\"fEAl\",\"fElp\",\"mfEAl\",\"ttfAEl\",\"fwAEl\",\"AfEyEAl\",\"mfEwl\",\"AfEylAl\",\"yfAEl\",\"tfEll\",\"fEly\",\"mfElC\",\"fEAlA\",\"mftEl\",\"AnfEl\",\"fElCC\",\"fAEl\",\">fElA'\",\"fElA'\",\"AfElAl\",\"fEllp\",\"nfElC\",\"fEwl\",\"AfEwAl\",\"AfEll\",\"nfEl\",\">fAEyl\",\">fEwl\",\"fEAlC\",\"tfAEyl\",\"fEAlyC\",\"fElAC\",\"fwAEyl\",\"mtfAEl\",\"mtfEl\",\"mnfEl\",\"tftEl\",\"fElwC\",\"mfElAn\",\">nfEl\",\"fwEl\",\"fAEyl\",\"mmfEl\",\"tfEAl\",\"ynfEl\"}\n"
                + "@ATTRIBUTE pos {DET+NOUN,DET+NOUN+NSUFF,DET+NUM,DET+NUM+NSUFF,NOUN,NOUN+NSUFF,NUM,NUM+NSUFF}\n"
                + "@ATTRIBUTE lastTwoLetter {#,ا,ة,ت,ي,ات,ان,ون,ين,ك,تي,تا,تين}\n"
                + "@ATTRIBUTE length {1,2,3,4,5,6,7,8,9,10}\n"
                + "@ATTRIBUTE Fem {YES,NO}\n"
                + "@ATTRIBUTE isGender {OOV,FeminineMasculine,FEMININE,MASCULINE}\n"
                + "@ATTRIBUTE isNumber {OOV,PluralSingular,PLURAL,SINGULAR}\n"
                + "@ATTRIBUTE hvh NUMERIC\n"
                + "@ATTRIBUTE hvA NUMERIC\n"
                + "@ATTRIBUTE hvAn NUMERIC\n"
                + "@ATTRIBUTE hAtAn NUMERIC\n"
                + "@ATTRIBUTE hAlAA NUMERIC\n"
                + "@ATTRIBUTE hvyn NUMERIC\n"
                + "@ATTRIBUTE hAtyn NUMERIC\n"
                + "@ATTRIBUTE hAvyn NUMERIC\n"
                + "@ATTRIBUTE class {S,D,P}\n"
                + "@DATA\n";
    
    private static Farasa farasa = null;
    
    private static weka.classifiers.trees.RandomForest rfGender = new weka.classifiers.trees.RandomForest();
    private static weka.classifiers.trees.RandomForest rfNumber = new weka.classifiers.trees.RandomForest();
    
    private static final HashMap<String, Boolean> validTemplates = new HashMap<String, Boolean>();

    private String isNumber(String input) {
        if (hmNumber.containsKey(input.trim()) || input.matches("[" + AllHindiDigits + "0-9\\.,\u00BC-\u00BE]+")) {
            return "NUM";
        } else {
            return "NOT";
        }
    }

    private String isForeign(String input) {
        if (isNumber(input.trim()).equals("NUM")) {
            return "NUM";
        } else if (input.trim().equals("-")) {
            return "B";
        } else if (input.trim().matches(".*[a-zA-z]+.*")) {
            return "FOREIGN";
        } else if (input.trim().matches("[" + AllArabicLetters + "]+")) {
            return "ARAB";
        } else if (input.trim().matches(".*[" + ALLDelimiters + "]+.*")) {
            return "PUNC";
        } else {
            return "OTHER";
        }
    }
    
    public String tagWord(String stemPart, String POS, String template) throws Exception
    {
        String features = stemPart + "\t"
                + fitTemplate(stemPart.replaceFirst("\\+.*", "")) + "\t"
                + isForeign(stemPart) + "\t"
                + "#-#\t1\t" + POS;
        ArrayList<String> feats = new ArrayList<String>();
        feats.add(features);
        feats.add("-");
        ArrayList<String> output = getGenderTagsRandomForest(feats, true);
        return output.get(0).trim().replaceFirst("^.*\\-", "");
    }
    
    public genderNumberTags(Farasa fr) throws IOException, ClassNotFoundException, InterruptedException
    {
        farasa = fr;
        genderNumberTags();
    }
    
    private InputStream resolveName(String name) {
       /* if (name == null) {
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
        }*/
	// return name;
	ClassLoader cl = this.getClass().getClassLoader();
	return cl.getResourceAsStream(name);
    }
    
    public void genderNumberTags() throws IOException, ClassNotFoundException, InterruptedException {
        // BinDir = dir;
        if (farasa == null)
            farasa = new Farasa();
        BufferedReader brRoot = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/FarasaData/roots.txt")));
	BufferedReader brTemplate = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/FarasaData/template-count.txt")));
	BufferedReader brNum = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/FarasaData/number-gaz.txt")));
        
        String line = "";
        while ((line = brRoot.readLine()) != null) {
            String[] parts = line.split("\t");
            if (parts.length == 2) {
                hmRoot.put(parts[0], Double.parseDouble(parts[1]));
            }
        }

        while ((line = brTemplate.readLine()) != null) {
            String[] parts = line.split("\t");
            if (parts.length == 2) {
                int len = parts[0].length();
                if (!Templates.containsKey(len)) {
                    Templates.put(len, new ArrayList<String>());
                }
                Templates.get(len).add(parts[0]);
                if (!hmTemplate.containsKey(parts[0])) {
                    hmTemplate.put(parts[0], Double.parseDouble(parts[1]));
                }
            }
        }

        while ((line = brNum.readLine()) != null) {
            if (!hmNumber.containsKey(line.trim())) {
                hmNumber.put(line.trim(), 1);
            }
        }

        brRoot.close();
        brTemplate.close();
        brNum.close();
        
        // load gender and number classifiers
	ObjectInputStream ois = new ObjectInputStream(this.getClass().getResourceAsStream("/FarasaData/truth.txt.train.gender.train.model"));
	rfGender = (weka.classifiers.trees.RandomForest) ois.readObject();
        
	ois = new ObjectInputStream(this.getClass().getResourceAsStream("/FarasaData/truth.txt.train.number.train.model"));
	rfNumber = (weka.classifiers.trees.RandomForest) ois.readObject();
        
        LoadGenderGazeteer();
        LoadGenderTrain();
        
        // populate validTemplates
        String validTemp = "fElY,tfElC,fElC,>fEl,nfAEl,mfElE,>stfEl,Y,ntfEl,mfElp,AftEl,>fElp,fEAlp,fEyEl,AfEnll,fEll,yfEl,ystfEl,ytfEl,fAElp,fE,AftEAl,AnfEAl,ntfAEl,tfElp,fEyl,fEl,fElAnp,tfAEl,mfEyl,>fAEl,AfEwEl,tstfEl,ytfAEl,fElAn,AfEAl,fElAl,nstfEl,>tfEl,mstfEl,>fEAl,fEAly,fAEwl,mfAEyl,fEAll,tfEl,AstfEAl,mfEl,fEA}l,AstfEl,>tfAEl,AfEwl,<fEAl,AfEl,ttfEl,yfElC,yfEll,fEyEyl,mfAElp,AfEnlAl,fEwlp,tfEyl,<fElp,mfAEl,>fElC,fEAl,fElp,mfEAl,ttfAEl,fwAEl,AfEyEAl,mfEwl,AfEylAl,yfAEl,tfEll,fEly,mfElC,fEAlA,mftEl,AnfEl,fElCC,fAEl,>fElA',fElA',AfElAl,fEllp,nfElC,fEwl,AfEwAl,AfEll,nfEl,>fAEyl,>fEwl,fEAlC,tfAEyl,fEAlyC,fElAC,fwAEyl,mtfAEl,mtfEl,mnfEl,tftEl,fElwC,mfElAn,>nfEl,fwEl,fAEyl,mmfEl,tfEAl,ynfEl";
        for (String vT : validTemp.split(","))
            validTemplates.put(vT, Boolean.TRUE);
        
    }
    
    public String getGenderTag(String word, String posTag, String template) throws IOException, ClassNotFoundException, InterruptedException {
        // // System.err.println(word + " " + posTag + " " + template);
        String gender = "";
        ArrayList<String> features = getGenderFeatures(word, posTag, template);
        
        // guess gender
        if (getScoreGivenFeatures("M", features) > getScoreGivenFeatures("F", features))
            gender = "M";
        else
            gender = "F";
        
        // guess number
        String[] possibleNumber = {"S", "D", "P"};
        double bestLabelProb = -1;
        String bestNumber = "";
        for (String l : possibleNumber) {
            double res = getScoreGivenFeatures(l, features);
            
            // System.err.println(res);
            if (res > bestLabelProb) {
                bestNumber = l;
                bestLabelProb = res;
            }
        }
        gender += bestNumber; 
        
        return gender;
    }
    
    private ArrayList<String> getGenderTags(ArrayList<String> outputPos, boolean printGenderTags) throws IOException, ClassNotFoundException, InterruptedException {
        ArrayList<String> output = new ArrayList<String>();
        boolean task = true;
        int frag = 0;
        ArrayList<String> wordTags = new ArrayList<String>();
        boolean getGender = false;
        for (String s : outputPos) {
            if (s.trim().length() == 0) {
                output.add("\n");
            } else if (s.startsWith("-")) {
                // do nothing
                if (task) {
                    if (getGender) {
                        String thisWord = "";
                        String thisTemplate = "";
                        String thisPosTag = "";
                        for (String w : wordTags) {
                            if (w.contains("NOUN") || w.contains("ADJ") || w.contains("NUM") || w.contains("NSUFF")) {
                                String[] feats = w.split("\t");
                                if (!w.contains("NSUFF")) {
                                    thisPosTag += "NOUN";
                                    thisTemplate = feats[1];
                                    thisWord += feats[0];
                                } else {
                                    if (thisPosTag.trim().length() > 0) {
                                        thisPosTag += "+" + feats[feats.length - 1];
                                    } else {
                                        thisPosTag += feats[feats.length - 1];
                                    }
                                    thisWord += "+" + feats[0];
                                }
                            }
                        }
                        for (String w : wordTags) {
                            if (printGenderTags && (w.contains("NOUN") || w.contains("ADJ") || w.contains("NUM"))) {
                                output.add(w.replaceFirst("\t", "/").replaceAll("\t.*\t", "/") + "-" + getGenderTag(thisWord, thisPosTag, thisTemplate) + " ");
                            } else {
                                output.add(w.replaceFirst("\t", "/").replaceAll("\t.*\t", "/") + " ");
                            }
                        }
                    } else {
                        for (String w : wordTags) {
                            output.add(w.replaceFirst("\t", "/").replaceAll("\t.*\t", "/") + " ");
                        }
                    }
                    getGender = false;
                    wordTags.clear();
                }
                frag = -1;
                output.add("-");
            } else {
                if (task) {
                    wordTags.add(s);
                    if (s.contains("NOUN") || s.contains("ADJ") || (s.contains("NUM") && !s.matches("[0-9٠-٩]+"))) {
                        getGender = true;
                    }
                    // bw.write(s.replaceAll("\t.*\t", "/") + " ");
                } else {

                    if (outputPos.indexOf(s) == 0) {
                            output.add("" + s.replaceAll("\t", "/") + "");
                    } else if (frag == 0) {
                        output.add(" " + s.replaceAll("\t", "/") + "");
                    } else {
                        output.add("/" + s.replaceAll("\t", "/") + "");
                    }
                }
            }
            frag++;
        }
        return output;
    }
    
    public ArrayList<String> getGenderTagsRandomForest(ArrayList<String> outputPos, boolean printGenderTags) throws Exception {
        ArrayList<String> output = new ArrayList<String>();
        boolean task = true;
        int frag = 0;
        ArrayList<String> wordTags = new ArrayList<String>();
        boolean getGender = false;
        for (String s : outputPos) {
            if (s.trim().length() == 0) {
                output.add("\n");
            } else if (s.startsWith("-")) {
                // do nothing
                if (task) {
                    if (getGender) {
                        String thisWord = "";
                        String thisTemplate = "";
                        String thisPosTag = "";
                        for (String w : wordTags) {
                            if (w.contains("NOUN") || w.contains("ADJ") || w.contains("NUM") || w.contains("NSUFF")) {
                                String[] feats = w.split("\t");
                                // if (!w.contains("NSUFF")) {
                                    if (thisPosTag.trim().length() > 0)
                                        thisPosTag += "+";
                                    thisPosTag += feats[feats.length - 1];
                                    thisPosTag = thisPosTag.replaceFirst("(ADJ|NUM)", "NOUN");
                                    thisTemplate = feats[1];
                                    if (thisWord.trim().length() > 0)
                                        thisWord += "+";
                                    thisWord += feats[0];
                            }
                        }
                        for (String w : wordTags) {
                            String ww = w.replaceFirst(".*\t", "");
                            if (printGenderTags && (ww.contains("NOUN") || ww.contains("ADJ") || ww.contains("NUM"))) {
                                output.add(w.replaceFirst("\t", "/").replaceAll("\t.*\t", "/") + "-" + getGenderTagRandomForest(thisWord, thisPosTag, thisTemplate) + " ");
                            } else {
                                output.add(w.replaceFirst("\t", "/").replaceAll("\t.*\t", "/") + " ");
                            }
                        }
                    } else {
                        for (String w : wordTags) {
                            output.add(w.replaceFirst("\t", "/").replaceAll("\t.*\t", "/") + " ");
                        }
                    }
                    getGender = false;
                    wordTags.clear();
                }
                frag = -1;
                output.add("-");
            } else {
                if (task) {
                    wordTags.add(s);
                    if (s.contains("NOUN") || s.contains("ADJ") || (s.contains("NUM") && !s.matches("[0-9٠-٩]+"))) {
                        getGender = true;
                    }
                    // bw.write(s.replaceAll("\t.*\t", "/") + " ");
                } else {

                    if (outputPos.indexOf(s) == 0) {
                        output.add("" + s.replaceAll("\t", "/") + "");
                    } else if (frag == 0) {
                        output.add(" " + s.replaceAll("\t", "/") + "");
                    } else {
                        output.add("/" + s.replaceAll("\t", "/") + "");
                    }
                }
            }
            frag++;
        }
        return output;
    }
    
    public String getGenderTagRandomForest(String word, String posTag, String template) throws Exception {
        // // System.err.println(word + " " + posTag + " " + template);
        String gender = "";
        String features = getGenderFeaturesRandomForest(word, posTag, template);
        String tags = classifyGenderAndNumberUsingRandomForest(features);
        return tags;
    }
    
    public String getGenderTagRandomForest(String word, String posTag, String template, String suffix) throws Exception {
        String key = word.trim() + " " + posTag.trim() + " " + template.trim() + " " + suffix.trim();
        if (hmSeenBefore.containsKey(key))
            return hmSeenBefore.get(key);
        else
        {
            String gender = "";
            if (template.equals("Y"))
                template = fitTemplate(word);
            String features = getGenderFeaturesRandomForest(word, posTag, template, suffix);
            String tags = classifyGenderAndNumberUsingRandomForest(features);
            hmSeenBefore.put(key, tags);
            return tags;
        }
    }

    public void generateTrainingFile(String inputFile) throws Exception
    {
        BufferedReader br = ArabicUtils.openFileForReading(inputFile);
        BufferedWriter bwGender = ArabicUtils.openFileForWriting(inputFile + ".gender.train.arff");
        BufferedWriter bwNumber = ArabicUtils.openFileForWriting(inputFile + ".number.train.arff");
        String line = "";
        
        bwGender.write(dataHeaderGender + "\n");
        bwNumber.write(dataHeaderNumber + "\n");
        
        while ((line = br.readLine()) != null)
        {
            String[] parts = line.split("\t");
            if (parts.length == 2) {
                String[] train = parts[0].split("/");
                if (train.length == 4) {
                    String word = train[0];
                    String posTag = train[2];
                    String gender = train[3];

                    TreeMap<Double, String> segmentedWord = farasa.mostLikelyPartition(word, 2);
                    String tmpWord = word;
                    String suffix = "";
                    if (segmentedWord.size() > 0)
                    {
                        if (posTag.contains("+") && segmentedWord.get(segmentedWord.firstKey()).contains("+"))
                            tmpWord = segmentedWord.get(segmentedWord.firstKey());
                        else
                            tmpWord = segmentedWord.get(segmentedWord.lastKey());
                        word = tmpWord.replace(";", "").trim();
                        suffix = (" "+tmpWord+" ").split(";")[2].trim();
                        tmpWord = (" "+tmpWord+" ").split(";")[1];
                        
                    }
                    
                    String template = fitTemplate(tmpWord); // ft.fitTemplate(tmpWord);
                    String feats = getGenderFeaturesRandomForest(word, posTag, template, suffix);

                    bwGender.write(feats.replaceFirst("\\?$", "") + gender.charAt(0) + "\n");
                    bwNumber.write(feats.replaceFirst("\\?$", "") + gender.substring(1) + "\n");
                }
            }
        }
        bwGender.close();
        bwNumber.close();
        
        // train models
	
        BufferedReader readerGender = new BufferedReader(new FileReader(inputFile + ".gender.train.arff"));
        Instances dataGender = new Instances(readerGender);
        readerGender.close();

        // setting class attribute
        dataGender.setClassIndex(dataGender.numAttributes() - 1);
        rfGender.setMaxDepth(7);
        rfGender.setNumFeatures(7);
        rfGender.setNumTrees(50);
        rfGender.buildClassifier(dataGender);

        ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(inputFile + ".gender.train.model"));
        oos.writeObject(rfGender);
        oos.flush();
        oos.close();
        
        BufferedReader readerNumber = new BufferedReader(new FileReader(inputFile + ".number.train.arff"));
        Instances dataNumber = new Instances(readerNumber);
        readerNumber.close();

        // setting class attribute
        dataNumber.setClassIndex(dataNumber.numAttributes() - 1);
        rfNumber.setMaxDepth(7);
        rfNumber.setNumFeatures(7);
       // rfNumber.setNumTrees(50);
        rfNumber.buildClassifier(dataNumber);

        oos = new ObjectOutputStream(
                new FileOutputStream(inputFile + ".number.train.model"));
        oos.writeObject(rfNumber);
        oos.flush();
        oos.close();
    }
    
    private String getGenderFeaturesRandomForest(String word, String posTag, String template) throws IOException, ClassNotFoundException, InterruptedException {
        String output = "";
        String suffix = "#";
        word = word.replace("++", "+");
        if (!validTemplates.containsKey(template))
            template = "Y";
        output += "\"" + template + "\",";
        // System.err.print("template:" + template);
        // System.err.print("\"" + template + "\"");
        
        if (posTag.contains("NSUFF")) {
            String typeOfNSUFF = posTag.replaceAll(".*NSUFF_", "");
            posTag = posTag.replaceAll("\\+NSUFF.*", "+NSUFF");
            int suffixPos = word.indexOf("+");
            if (suffixPos >= 0) {
                suffix = word.substring(suffixPos + 1).replace("+", "");
                if (suffix.equals("ت"))
                {
                    suffix = "ة";
                    word = word.replace("+ت", "+ة");
                }
            } else {
                if (typeOfNSUFF.equals("FEM_SG")) {
                    suffix = word.substring(word.length() - 1);
                    if (suffix.equals("ت"))
                        suffix = "ة";
                } else if (typeOfNSUFF.equals("FEM_DU") || typeOfNSUFF.equals("MASC_DU") || typeOfNSUFF.equals("MASC_PL")) {
                    if (word.endsWith("ن")) {
                        suffix = word.substring(word.length() - 2);
                    } else {
                        suffix = word.substring(word.length() - 1);
                    }
                } else if (typeOfNSUFF.equals("FEM_PL")) {
                    suffix = "\u0627\u062a"; // suffix = "ات";
                }
                else
                {
                    // extract suffix brute force using the longest possible suffix
                    suffix = getLongestPossibleAttachedSuffix(word);
                }
            }
        }
        output += posTag + ",";
        
        // double check suffix to make sure that it is correct
        if (!suffix.matches("(\\#|ا|ة|ت|ي|ات|ان|ون|ين|ك|تي|تا|تين)"))
            suffix = "#";
        output += suffix + ",";
        output += template.length() + ",";
        if (word.endsWith("\u0629") || word.endsWith("\u0627\u062a")) // ends with At or p
        {
            output += "YES,";
        } else {
            output += "NO,";
        }
                
        // check gender
        String isGender = "";// "UNKNOWNGENDER";
        if (hmGenderGaz.containsKey(word.replace("+", "")))
        {
            if (hmGenderGaz.get(word.replace("+", "")).contains("feminine") && hmGenderGaz.get(word.replace("+", "")).contains("masculine"))
                isGender = "FeminineMasculine";
            else if (hmGenderGaz.get(word.replace("+", "")).contains("feminine"))
                isGender = "FEMININE";
            else if (hmGenderGaz.get(word.replace("+", "")).contains("masculine"))
                isGender = "MASCULINE";
        }
        if (isGender.length() > 0)
            output += isGender + ",";
        else
            output += "OOV,";
        
        // check number
        String isNumber = ""; // "UNKNOWNNUM";
        if (hmGenderGaz.containsKey(word.replace("+", "")))
        {
            if (hmGenderGaz.get(word.replace("+", "")).contains("plural") && hmGenderGaz.get(word.replace("+", "")).contains("singular"))
                isNumber = "PluralSingular";
            else if (hmGenderGaz.get(word.replace("+", "")).contains("plural"))
                isNumber = "PLURAL";
            else if (hmGenderGaz.get(word.replace("+", "")).contains("singular"))
                isNumber = "SINGULAR";
        }
        if (isNumber.length() > 0)
            output += isNumber + ",";
        else
            output += "OOV,";

        // check most likely ism ishara (h*A, h*h, h*An, hAtAn, h&lA&)
        
        String[] asmaa = {"هذه", "هذا", "هذان", "هاتان", "هؤلاء", "هذين", "هاتين", "هاذين"};
        double score = 0d;
        String ishara = "";
        for (String ism : asmaa)
        {
            double tmpScore = -10000d;
            if (hmAsmaaIsharaLM.containsKey(ism + " " + word.replace("+", "").trim()))
            {
                tmpScore = hmAsmaaIsharaLM.get(ism + " " + word.replace("+", "").trim());
                tmpScore = Math.exp(tmpScore);
            }
            if (!word.replace("+", "").startsWith("ال") && hmAsmaaIsharaLM.containsKey(ism + " ال" + word.replace("+", "").trim()) && hmAsmaaIsharaLM.get(ism + " ال" + word.replace("+", "").trim()) > tmpScore)
            {
                tmpScore = hmAsmaaIsharaLM.get(ism + " ال" + word.replace("+", "").trim());
                tmpScore = Math.exp(tmpScore);
            }
            if (tmpScore > 0)
                output += tmpScore + ",";
            else
                output += "0,";
        }
        
        output += ",?";        
                
        return output;
    }

    private String getGenderFeaturesRandomForest(String word, String posTag, String template, String suffix) throws IOException, ClassNotFoundException, InterruptedException {
        String output = "";
        if (suffix.trim().length() == 0)
            suffix = "#";
        word = word.replace("++", "+");
        if (!validTemplates.containsKey(template))
            template = "Y";
        output += "\"" + template + "\",";
        if (posTag.contains("NSUFF")) {
            // String typeOfNSUFF = posTag.replaceAll(".*NSUFF_", "");
            posTag = posTag.replaceAll("\\+NSUFF.*", "+NSUFF");
        }
        output += posTag + ",";
        
        if (!suffix.matches("(\\#|ا|ة|ت|ي|ات|ان|ون|ين|ك|تي|تا|تين)"))
            suffix = "#";
        output += suffix.replace("+", "") + ",";
        output += template.length() + ",";
        if (suffix.endsWith("\u0629") || suffix.endsWith("\u0627\u062a") || suffix.endsWith("تي") || suffix.endsWith("تا")) // ends with At or p
        {
            output += "YES,";
        } else {
            output += "NO,";
        }
                
        // check gender
        String isGender = "";// "UNKNOWNGENDER";
        if (hmGenderGaz.containsKey(word.replace("+", "")))
        {
            if (hmGenderGaz.get(word.replace("+", "")).contains("feminine") && hmGenderGaz.get(word.replace("+", "")).contains("masculine"))
                isGender = "FeminineMasculine";
            else if (hmGenderGaz.get(word.replace("+", "")).contains("feminine"))
                isGender = "FEMININE";
            else if (hmGenderGaz.get(word.replace("+", "")).contains("masculine"))
                isGender = "MASCULINE";
        }
        if (isGender.length() > 0)
            output += isGender + ",";
        else
            output += "OOV,";
        
        // check number
        String isNumber = ""; // "UNKNOWNNUM";
        if (hmGenderGaz.containsKey(word.replace("+", "")))
        {
            if (hmGenderGaz.get(word.replace("+", "")).contains("plural") && hmGenderGaz.get(word.replace("+", "")).contains("singular"))
                isNumber = "PluralSingular";
            else if (hmGenderGaz.get(word.replace("+", "")).contains("plural"))
                isNumber = "PLURAL";
            else if (hmGenderGaz.get(word.replace("+", "")).contains("singular"))
                isNumber = "SINGULAR";
        }
        if (isNumber.length() > 0)
            output += isNumber + ",";
        else
            output += "OOV,";

        // check most likely ism ishara (h*A, h*h, h*An, hAtAn, h&lA&)
        
        String[] asmaa = {"هذه", "هذا", "هذان", "هاتان", "هؤلاء", "هذين", "هاتين", "هاذين"};
        double score = 0d;
        String ishara = "";
        for (String ism : asmaa)
        {
            double tmpScore = -10000d;
            if (hmAsmaaIsharaLM.containsKey(ism + " " + word.replace("+", "").trim()))
            {
                tmpScore = hmAsmaaIsharaLM.get(ism + " " + word.replace("+", "").trim());
                tmpScore = Math.exp(tmpScore);
            }
            if (!word.replace("+", "").startsWith("ال") && hmAsmaaIsharaLM.containsKey(ism + " ال" + word.replace("+", "").trim()) && hmAsmaaIsharaLM.get(ism + " ال" + word.replace("+", "").trim()) > tmpScore)
            {
                tmpScore = hmAsmaaIsharaLM.get(ism + " ال" + word.replace("+", "").trim());
                tmpScore = Math.exp(tmpScore);
            }
            if (tmpScore > 0)
                output += tmpScore + ",";
            else
                output += "0,";
        }
        output += ",?";        
        return output;
    }
    
    private String classifyGenderAndNumberUsingRandomForest(String featureVector) throws Exception {
        String example = dataHeaderGender + featureVector;
        InputStream is = new ByteArrayInputStream(example.getBytes());
        BufferedReader readerTest = new BufferedReader(new InputStreamReader(is));
        // System.err.println(example);
        Instances genderTest = new Instances(readerTest);
        readerTest.close();
        genderTest.setClassIndex(genderTest.numAttributes() - 1);

        example = dataHeaderNumber + featureVector;
        is = new ByteArrayInputStream(example.getBytes());
        readerTest = new BufferedReader(new InputStreamReader(is));
        Instances numberTest = new Instances(readerTest);
        readerTest.close();
        numberTest.setClassIndex(numberTest.numAttributes() - 1);

        // Instances genderTest = new Instances(dataTest);
        // Instances numberTest = new Instances(dataTest);
        double d = rfGender.classifyInstance(genderTest.instance(0));
        genderTest.instance(0).setClassValue(d);
        // System.out.println(genderTest.instance(0));
        String tagGender = genderTest.instance(0).toString().replaceFirst(".*,", "").trim();
        d = rfNumber.classifyInstance(numberTest.instance(0));
        numberTest.instance(0).setClassValue(d);
        // System.out.println(numberTest.instance(0));
        String tagNumber = numberTest.instance(0).toString().replaceFirst(".*,", "").trim();

        return tagGender + tagNumber;
    }
    
    private String getLongestPossibleAttachedSuffix (String word)
    {
        String possibleSuffix = "#";
        if (word.matches(".*(\u0647|\u0647\u0627|\u0643|\u064a|\u0647\u0645\u0627|\u0643\u0645\u0627|\u0646\u0627|\u0643\u0645|\u0647\u0645|\u0647\u0646|\u0643\u0646|\u0627|\u0627\u0646|\u064a\u0646|\u0648\u0646|\u0648\u0627|\u0627\u062a|\u062a|\u0646|\u0629)$"))
        {
            if (hSuffixes.containsKey(word.substring(word.length() - 3)))
            {
                possibleSuffix = word.substring(word.length() - 3);
                word = word.substring(0, word.length() - 3);
            }
            else if (hSuffixes.containsKey(word.substring(word.length() - 2)))
            {
                possibleSuffix = word.substring(word.length() - 2);
                word = word.substring(0, word.length() - 2);
            }
            else if (hSuffixes.containsKey(word.substring(word.length() - 1)))
            {
                possibleSuffix = word.substring(word.length() - 1);
                word = word.substring(0, word.length() - 1);
            }
        }
        return possibleSuffix;
    }
 
    private ArrayList<String> getGenderFeatures(String word, String posTag, String template) throws IOException, ClassNotFoundException, InterruptedException {
        ArrayList<String> output = new ArrayList<String>();
        String suffix = "#";
        if (!validTemplates.containsKey(template))
            template = "Y";
        output.add(template);
        // System.err.print("template:" + template);
        // System.err.print("\"" + template + "\"");
        
        if (posTag.contains("NSUFF")) {
            String typeOfNSUFF = posTag.replaceAll(".*NSUFF_", "");
            posTag = posTag.replaceAll("\\+NSUFF.*", "+NSUFF");
            int suffixPos = word.indexOf("+");
            if (suffixPos >= 0) {
                suffix = word.substring(suffixPos + 1).replace("+", "");
                if (suffix.equals("ت"))
                {
                    suffix = "ة";
                    word = word.replace("+ت", "+ة");
                }
            } else {
                if (typeOfNSUFF.equals("FEM_SG")) {
                    suffix = word.substring(word.length() - 1);
                    if (suffix.equals("ت"))
                        suffix = "ة";
                } else if (typeOfNSUFF.equals("FEM_DU") || typeOfNSUFF.equals("MASC_DU") || typeOfNSUFF.equals("MASC_PL")) {
                    if (word.endsWith("ن")) {
                        suffix = word.substring(word.length() - 2);
                    } else {
                        suffix = word.substring(word.length() - 1);
                    }
                } else if (typeOfNSUFF.equals("FEM_PL")) {
                    suffix = "\u0627\u062a"; // suffix = "ات";
                }
                else
                {
                    // extract suffix brute force using the longest possible suffix
                    suffix = getLongestPossibleAttachedSuffix(word);
                }
            }
        }
        output.add(posTag);
        // System.err.print(", tag:" + posTag);
        // System.err.print(", " + posTag);
        output.add(suffix);
        // System.err.print(", suffix:" + suffix);
        // System.err.print(", " + suffix);
//        output.add(word.substring(word.length()-2));
//        output.add(word.substring(word.length()-1));
        output.add(Integer.toString(template.length()));
        // System.err.print(", templateLen:" + template.length());
        // System.err.print(", " + template.length());
        if (word.endsWith("\u0629") || word.endsWith("\u0627\u062a")) // ends with At or p
        {
            output.add("YES");
            // System.err.print(", FeminineMarker:YES");
            // System.err.print(", YES");
        } else {
            output.add("NO");
            // System.err.print(", FeminineMarker:NO");
            // System.err.print(", NO");
        }

        // check information from gazeteer
        // check if particle
        String isParticle = "UNKNOWNPART";
        if (hmGenderGaz.containsKey(word))
        {
            if (hmGenderGaz.get(word).contains("particle"))
            {
                isParticle = "PARTICLE";
            }
            else
            {
                isParticle = "NOTPARTICLE";
            }
            // output.add(isParticle);
        }
        
        
        // check gender
        String isGender = "";// "UNKNOWNGENDER";
        if (hmGenderGaz.containsKey(word))
        {
            if (hmGenderGaz.get(word).contains("feminine") && hmGenderGaz.get(word).contains("masculine"))
                isGender = "FeminineMasculine";
            else if (hmGenderGaz.get(word).contains("feminine"))
                isGender = "FEMININE";
            else if (hmGenderGaz.get(word).contains("masculine"))
                isGender = "MASCULINE";
        }
//        if (isGender.length() > 0)
//            System.err.print(", " + isGender);
//        else
//            System.err.print(", OOV");
        // System.err.print(", isGender:" + isGender);
        
        // check number
        String isNumber = ""; // "UNKNOWNNUM";
        if (hmGenderGaz.containsKey(word))
        {
            if (hmGenderGaz.get(word).contains("plural") && hmGenderGaz.get(word).contains("singular"))
                isNumber = "PluralSingular";
            else if (hmGenderGaz.get(word).contains("plural"))
                isNumber = "PLURAL";
            else if (hmGenderGaz.get(word).contains("singular"))
                isNumber = "SINGULAR";
        }
//        if (isNumber.length() > 0)
//            System.err.print(", " + isNumber);
//        else
//            System.err.print(", OOV");

        //if ((isNumber.equals("PLURAL") || isNumber.equals("SINGULAR")) && (isGender.equals("FEMININE") || isGender.equals("MASCULINE")))
        //{
        //    output.clear();
        //}
                    
        if (isGender.length() > 0) {
            output.add(isGender);
        }

        if (isNumber.length() > 0) {
            output.add(isNumber);
        }

        // check most likely ism ishara (h*A, h*h, h*An, hAtAn, h&lA&)
        
        String[] asmaa = {"هذه", "هذا", "هذان", "هاتان", "هؤلاء", "هذين", "هاتين", "هاذين"};
        double score = 0d;
        String ishara = "";
        for (String ism : asmaa)
        {
            double tmpScore = -10000d;
            if (hmAsmaaIsharaLM.containsKey(ism + " " + word.trim()))
            {
                tmpScore = hmAsmaaIsharaLM.get(ism + " " + word.trim());
                tmpScore = Math.exp(tmpScore);
            }
            if (!word.startsWith("ال") && hmAsmaaIsharaLM.containsKey(ism + " ال" + word.trim()) && hmAsmaaIsharaLM.get(ism + " ال" + word.trim()) > tmpScore)
            {
                tmpScore = hmAsmaaIsharaLM.get(ism + " ال" + word.trim());
                tmpScore = Math.exp(tmpScore);
            }
            if (tmpScore > 0)
            {
                // System.err.print(", " + ism + ":" + tmpScore);
                // System.err.print(", " + tmpScore);
            }
            else
            {
                // System.err.print(", " + ism + ":0" );
                // System.err.print(", 0" );
            }
            if (tmpScore > score)
            {
                score = tmpScore;
                ishara = ism;
            }
        }
        
        if (ishara.length() > 0)
            output.add(ishara);
        
        if (hmGenderNEGaz.containsKey(word))
            output.add(hmGenderNEGaz.get(word));
        //else
        //    output.add("NENotFound");
        
        
        return output;
    }
    
    private void LoadGenderGazeteer() throws IOException
    {
	BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/FarasaData/number.txt.out")));
        String line = "";
        while ((line = br.readLine()) != null) {
            String[] parts = line.split("\t");
            if (parts.length == 2) {
                hmGenderGaz.put(parts[0], parts[1]);
            }
        }

	br = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/FarasaData/first-name-gaz.txt")));
        line = "";
        while ((line = br.readLine()) != null) {
            String[] parts = line.split("\t");
            if (parts.length == 3) {
                String name = parts[0];
                float male = Float.parseFloat(parts[1]);
                float female = Float.parseFloat(parts[2]);
                String tag = "";
                if (male > 10 * female) 
                {
                    if (male > 1)
                        tag = "NEMALE";
                    else
                        tag = "PNEMALE";
                }
                else if (female > 10)
                {
                    if (female > 1)
                        tag = "NEFEMALE";
                    else
                        tag = "PNEFEMALE";
                }
                else if (male > female)
                    tag = "PNEMALE";
                else if (female > male)
                    tag = "PNEFEMALE";
                hmGenderNEGaz.put(name, tag);
            }
        }
        
        // load asmaa ishara LM
	br = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/FarasaData/wikipedia-and-aljazeera.txt")));
        line = "";
        while ((line = br.readLine()) != null) {
            String[] parts = line.split("\t");
            if (parts.length == 2 && parts[1].trim().contains(" ") && parts[1].trim().matches("(هذه|هذا|هذان|هاتان|هؤلاء) .*"))
            {
                hmAsmaaIsharaLM.put(parts[1].trim(), Double.parseDouble(parts[0]));
            }
        }
        
    }
    
    private void LoadGenderTrain() throws IOException, ClassNotFoundException, InterruptedException {
        // BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(BinDir + "train.lang.all.utf8")), "UTF8"));
        BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/FarasaData/truth.txt.train")));
//        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(BinDir + "train.lang.all.utf8.weka")), "UTF8"));
        String line = "";
        while ((line = br.readLine()) != null) {
            String[] parts = line.split("\t");
            if (parts.length == 2) {
                String[] train = parts[0].split("/");
                if (train.length == 4) {
                    String word = train[0];
                    String posTag = train[2];
                    String gender = train[3];

                    // keep count of gender tags -- for prob priors
                    for (int i = 0; i < gender.length(); i++) {
                        String key = gender.substring(i, i + 1);
                        if (hmGender.containsKey(key)) {
                            hmGender.put(key, hmGender.get(key) + 1);
                        } else {
                            hmGender.put(key, 1);
                        }
                    }
                    iGenderCountPublic++;
                    String tmpWord = word;
                    posTag = posTag.replaceFirst("_.*", "");
                    if (posTag.endsWith("NSUFF"))
                        //tmpWord = word.replaceAll("(\u0647|\u0647\u0627|\u0643|\u064a|\u0647\u0645\u0627|\u0643\u0645\u0627|\u0646\u0627|\u0643\u0645|\u0647\u0645|\u0647\u0646|\u0643\u0646|\u0627|\u0627\u0646|\u064a\u0646|\u0648\u0646|\u0648\u0627|\u0627\u062a|\u062a|\u0646|\u0629)$", "");
                        tmpWord = word.replaceAll("(\u0647|\u0647\u0627|\u0643|\u064a|\u0647\u0645\u0627|\u0643\u0645\u0627|\u0646\u0627|\u0643\u0645|\u0647\u0645|\u0647\u0646|\u0643\u0646|\u0627|\u0627\u0646|\u064a\u0646|\u0648\u0646|\u0648\u0627|\u0627\u062a|\u062a|\u0646)$", "");
                    else if (posTag.endsWith("CASE") && word.endsWith("\u0627"))
                        tmpWord = word.substring(0, word.length() - 1);
                    if (posTag.startsWith("DET"))
                        tmpWord = tmpWord.substring(2);
                    String template = fitTemplate(tmpWord);
                    ArrayList<String> features = getGenderFeatures(word, posTag, template);

                    for (String f : features) {                
                        // train gender and number seperately
                        for (int i = 0; i < gender.length(); i++)
                        {
                            String key = gender.charAt(i) + "_" + f;
                            if (hmFeatureLabelCount.containsKey(key)) {
                                hmFeatureLabelCount.put(key, hmFeatureLabelCount.get(key) + 1);
                            } else {
                                hmFeatureLabelCount.put(key, 1);
                            }
                        }
                    }
                }
            }
        }
    }

    public String fitTemplate(String line) {
        String tmp = farasa.getStemTempate(utf82buck(line));
        if (tmp.contains("Y") && (line.endsWith("\u0629") || line.endsWith("\u064a"))) { // ends with ta marbouta or alef maqsoura
            tmp = fitStemTemplate(utf82buck(line.substring(0, line.length() - 1)));
        }
        if (tmp.contains("Y") && (line.endsWith("\u064a\u0629"))) { // ends with ya + ta marbouta
            tmp = fitStemTemplate(utf82buck(line.substring(0, line.length() - 2)));
        }
        if (tmp.contains("Y") && (line.endsWith("\u0649"))) { // ends with alef maqsoura
            tmp = fitStemTemplate(utf82buck(line.substring(0, line.length() - 1) + "\u064a"));
        }
        if (tmp.contains("Y") && (line.contains("\u0623") || line.contains("\u0622") || line.contains("\u0625"))) { // contains any form of alef
            tmp = fitStemTemplate(line.replaceAll("[\u0625\u0623\u0622]", "\u0627")); // normalize alef
        }
        if (tmp.contains("Y") & line.length() > 0) {
            tmp = fitStemTemplate(line + line.substring(line.length() - 1));
        }
        return tmp;
    }

    private String fitStemTemplate(String stem) {
        ArrayList<String> template = new ArrayList<String>();
        int len = stem.length();
        if (!Templates.containsKey(len)) {
            template.add("Y");
            return "Y";
        } else {
            if (len == 2) {
                if (hmRoot.containsKey(buck2morph(stem + stem.substring(1)))) {
                    template.add("fE");
                    return "fE";
                }
            } else {
                ArrayList<String> t = Templates.get(len);
                for (String s : t) {
                    String root = "";
                    int lastF = -1;
                    int lastL = -1;
                    boolean broken = false;
                    for (int i = 0; i < s.length() && broken == false; i++) {
                        if (s.charAt(i) == 'f') {
                            root += stem.substring(i, i + 1);
                        } else if (s.charAt(i) == 'E') {
                            // check if repeated letter in the root
                            if (lastF == -1) // letter not repeated
                            {
                                root += stem.substring(i, i + 1);
                                lastF = i;
                            } else // letter repeated
                            {
                                if (stem.substring(i, i + 1) != stem.substring(lastF, lastF + 1)) {
                                    // stem template is broken
                                    broken = true;
                                }
                            }
                        } else if (s.charAt(i) == 'l') {
                            // check if repeated letter in the root
                            if (lastL == -1) // letter not repeated
                            {
                                root += stem.substring(i, i + 1);
                                lastL = i;
                            } else // letter repeated
                            {
                                if (stem.substring(i, i + 1) != stem.substring(lastL, lastL + 1)) {
                                    // stem template is broken
                                    broken = true;
                                }
                            }
                        } else if (s.charAt(i) == 'C') {
                            root += stem.substring(i, i + 1);
                        } else {
                            if (!stem.substring(i, i + 1).equals(s.substring(i, i + 1))) {
                                // template is broken
                                broken = true;
                            }
                        }
                    }

                    root = buck2morph(root);

                    ArrayList<String> altRoot = new ArrayList<String>();
                    if (broken == false && !hmRoot.containsKey(root)) {

                        for (int j = 0; j < root.length(); j++) {
                            if (root.charAt(j) == 'y' || root.charAt(j) == 'A' || root.charAt(j) == 'w') {
                                String head = root.substring(0, j);
                                String tail = root.substring(j + 1);
                                if (hmRoot.containsKey(head + "w" + tail)) {
                                    altRoot.add(head + "w" + tail);
                                }
                                if (hmRoot.containsKey(head + "y" + tail)) {
                                    altRoot.add(head + "y" + tail);
                                }
                                if (hmRoot.containsKey(head + "A" + tail)) {
                                    altRoot.add(head + "A" + tail);
                                }
                            }
                        }
                    }
                    if (broken == false && hmRoot.containsKey(root)) {
                        template.add(s + "/" + root);
                    }
                    for (String ss : altRoot) {
                        template.add(s + "/" + ss);
                    }
                }
            }

        }
        if (template.size() == 0) {
            template.add("Y");
            return "Y";
        }

        ArrayList<String> templateWithC = new ArrayList<String>();
        ArrayList<String> templateWithoutC = new ArrayList<String>();

        for (String ss : template) {
            if (ss.contains("C")) {
                templateWithC.add(ss);
            } else {
                templateWithoutC.add(ss);
            }
        }
        if (templateWithoutC.size() == 0) {
            return getBestTemplate(template);
        } else {
            return getBestTemplate(templateWithoutC);
        }
    }

    private String getBestTemplate(ArrayList<String> template) {
        double bestScore = 0;
        String bestTemplate = "";
        for (String s : template) {
            String[] parts = s.split("/");
            if (parts.length == 2) {
                double score = hmRoot.get(parts[1]) * hmTemplate.get(parts[0]);
                if (bestScore < score) {
                    bestScore = score;
                    bestTemplate = parts[0];
                }
            }
        }
        return bestTemplate;
    }

    private double getScoreGivenFeatures(String l, ArrayList<String> features) {
        double res = (double) hmGender.get(l) / (double) iGenderCountPublic;
        for (String f : features) {
            String key = l + "_" + f;
            // System.err.print(key + "\t");
            if (hmFeatureLabelCount.containsKey(key)) {
                res *= (hmFeatureLabelCount.get(key) + 0.1d) / (double) hmGender.get(l);
                // System.err.println((double) (hmFeatureLabelCount.get(key) + 1d) / (double) hmGender.get(l));
            } else {
                res *= 0.1d / (double) hmGender.get(l);
                // System.err.println(1d / (double) hmGender.get(l));
            }
        }
        return res;
    }
}
