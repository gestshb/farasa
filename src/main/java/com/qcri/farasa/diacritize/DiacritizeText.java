    /*
     * To change this license header, choose License Headers in Project Properties.
     * To change this template file, choose Tools | Templates
     * and open the template in the editor.
     */
    package com.qcri.farasa.diacritize;

    import com.qcri.farasa.pos.FarasaPOSTagger;
    import com.qcri.farasa.segmenter.ArabicUtils;
    import com.qcri.farasa.segmenter.Farasa;
    import gnu.trove.map.TMap;
    import gnu.trove.map.hash.THashMap;

    import java.io.*;
    import java.util.ArrayList;
    import java.util.HashMap;

    /**
     * @author kareemdarwish
     */
    public class DiacritizeText {

        private static final String kenlmDir = "";
        private static final String dataDirectory = "";
        private static final Process process = null;
        private static final Process process2ndLM = null;
        private static final BufferedReader brLM = null;
        private static final BufferedWriter bwLM = null;
        private static final BufferedReader brLM2ndLM = null;
        private static final BufferedWriter bwLM2ndLM = null;
        private static final HashMap<String, Integer> hPrefixes = new HashMap<String, Integer>();
        private static final HashMap<String, Integer> hSuffixes = new HashMap<String, Integer>();
        private static final HashMap<String, String> diacritizedPrefixes = new HashMap<String, String>();
        private static final HashMap<String, String> diacritizedSuffixes = new HashMap<String, String>();
        public static HashMap<String, Integer> seenWordsMap = new HashMap<String, Integer>();
        public static HashMap<String, String> unigramsWithSingleDiacritization = new HashMap<String, String>();
        public static TMap<String, String> bigramsWithSingleDiacritizations = new THashMap<String, String>();
        public static Farasa farasaSegmenter;
        public static FarasaPOSTagger farasaPOSTagger;
        public static HashMap<String, String> defaultDiacritizationBasedOnTemplateProbability = new HashMap<String, String>();
        //public LanguageModel languageModel = null;
        private static HashMap<String, Double> ngrams = new HashMap<String, Double>();
        private static DiacritizeText instance;
        public HashMap<String, String> candidatesUnigram = new HashMap<String, String>();
        public HashMap<String, Boolean> dualPlural = new HashMap<String, Boolean>();
        public HashMap<String, String> bestDiacritizedTemplateFull = new HashMap<String, String>();
        public HashMap<String, String> bestDiacritizedTemplateStem = new HashMap<String, String>();
        public HashMap<String, String> auxWordsTransliteration = new HashMap<String, String>();
        public RecoverCaseEnding rce;

        public DiacritizeText(final Farasa fr, final FarasaPOSTagger frPOS) throws IOException, ClassNotFoundException, InterruptedException {
            DiacritizeText.farasaSegmenter = fr;
            DiacritizeText.farasaPOSTagger = frPOS;

            for (final String prefixe : com.qcri.farasa.segmenter.ArabicUtils.prefixes) {
                DiacritizeText.hPrefixes.put(prefixe, 1);
            }
            for (final String suffixe : com.qcri.farasa.segmenter.ArabicUtils.suffixes) {
                DiacritizeText.hSuffixes.put(suffixe, 1);
            }

            DiacritizeText.diacritizedPrefixes.put(ArabicUtils.buck2utf8("w"), ArabicUtils.buck2utf8("wa"));
            DiacritizeText.diacritizedPrefixes.put(ArabicUtils.buck2utf8("s"), ArabicUtils.buck2utf8("sa"));
            DiacritizeText.diacritizedPrefixes.put(ArabicUtils.buck2utf8("f"), ArabicUtils.buck2utf8("fa"));
            DiacritizeText.diacritizedPrefixes.put(ArabicUtils.buck2utf8("k"), ArabicUtils.buck2utf8("ka"));
            DiacritizeText.diacritizedPrefixes.put(ArabicUtils.buck2utf8("b"), ArabicUtils.buck2utf8("bi"));
            DiacritizeText.diacritizedPrefixes.put(ArabicUtils.buck2utf8("l"), ArabicUtils.buck2utf8("li"));
            DiacritizeText.diacritizedPrefixes.put(ArabicUtils.buck2utf8("ll"), ArabicUtils.buck2utf8("lilo"));
            DiacritizeText.diacritizedPrefixes.put(ArabicUtils.buck2utf8("Al"), ArabicUtils.buck2utf8("Aalo"));

            DiacritizeText.diacritizedSuffixes.put(ArabicUtils.buck2utf8("hmA"), ArabicUtils.buck2utf8("humA"));
            DiacritizeText.diacritizedSuffixes.put(ArabicUtils.buck2utf8("km"), ArabicUtils.buck2utf8("kumo"));
            DiacritizeText.diacritizedSuffixes.put(ArabicUtils.buck2utf8("hm"), ArabicUtils.buck2utf8("humo"));
            DiacritizeText.diacritizedSuffixes.put(ArabicUtils.buck2utf8("hn"), ArabicUtils.buck2utf8("hun~a "));

            DiacritizeText.defaultDiacritizationBasedOnTemplateProbability = this.deserializeMap("defaultDiacritizationBasedOnTemplateProbability");
            this.candidatesUnigram = this.deserializeMap("candidatesUnigram");
            DiacritizeText.unigramsWithSingleDiacritization = this.deserializeMap("oneChoice");
            this.dualPlural = this.deserializeMap("dualPlural"); // dualPlural
            this.bestDiacritizedTemplateFull = this.deserializeMap("bestDiacritizedTemplateFull");
            this.bestDiacritizedTemplateStem = this.deserializeMap("bestDiacritizedTemplateStem");
            this.auxWordsTransliteration = this.deserializeMap("auxWordsTransliteration");

            final String line = "";
            this.loadLM();

            this.rce = new RecoverCaseEnding(this, DiacritizeText.dataDirectory);
        }

        public static synchronized DiacritizeText getInstance() throws Exception {
            if (DiacritizeText.instance == null) {
                DiacritizeText.farasaSegmenter = new Farasa();
                DiacritizeText.farasaPOSTagger = new FarasaPOSTagger(DiacritizeText.farasaSegmenter);
                DiacritizeText.instance = new DiacritizeText(DiacritizeText.farasaSegmenter, DiacritizeText.farasaPOSTagger);
            }
            return DiacritizeText.instance;
        }

        private static String correctLamAlefLamNoPrefixes(final String input, final boolean withDiacritics) {
            String output = "";
            if (ArabicUtils.removeDiacritics(input).startsWith("لال") || ArabicUtils.removeDiacritics(input).startsWith("ل+ال")) {
                int i = 0;
                while (!(ArabicUtils.removeDiacritics(input.substring(0, i)).replace("+", "").equals("لال"))) {
                    i++;
                }
                if (withDiacritics)
                    output = ArabicUtils.buck2utf8("lilo") + input.substring(i).replaceFirst("^[" + ArabicUtils.buck2utf8("aiouNKF~") + "]+", "");
                else
                    output = ArabicUtils.buck2utf8("ll") + input.substring(i).replaceFirst("^[" + ArabicUtils.buck2utf8("aiouNKF~") + "]+", "");
            } else {
                output = input;
            }
            return output;
        }

        private static String correctLamAlefLam(String input, final boolean withDiacritics) {


            if (ArabicUtils.removeDiacritics(input).startsWith("لال")) {
                return correctLamAlefLamNoPrefixes(input, withDiacritics);
            } else if (ArabicUtils.removeDiacritics(input).startsWith("ولال") || ArabicUtils.removeDiacritics(input).startsWith("فلال")) {
                String firstLetter = input.substring(0, 1);
                input = input.substring(input.indexOf("ل"));
                if (withDiacritics) {
                    firstLetter += ArabicUtils.buck2utf8("a");
                }
                return firstLetter + correctLamAlefLamNoPrefixes(input, withDiacritics);
            }
            else {
                return input;
            }
        }

        private static String combineDiacritizedWordWithCaseEnding(final DiacritizeText dt, final ArrayList<String> caseEndings, final String[] diacritizedWords) {
            String output = "";
            // if (caseEndings.size() != diacritizedWords.length)
            //    System.err.println();
            for (int i = 0; i < caseEndings.size(); i++) {
                if (!caseEndings.get(i).trim().startsWith("# 0") && !caseEndings.get(i).trim().startsWith("# 1.")) {
                    final String[] parts = caseEndings.get(i).split("[ \t]+");
                    final String word = parts[0];
                    String stem = parts[1];
                    String ending = parts[parts.length - 1];
                    final String dWord = DiacritizeText.correctLamAlefLam(diacritizedWords[i], true);
                    Double endingScore = 0d;
                    if (ending.contains("/")) {
                        endingScore = Double.parseDouble(ending.substring(ending.indexOf("/") + 1));
                        ending = ending.substring(0, ending.indexOf("/")).trim();
                    } else {
                        // System.err.println();
                    }
                    if (endingScore < 0.8 || ending.equals("null")) {
                        ending = "";
                    } else if ((stem.trim().equals("#") && !word.endsWith("+ه")) || ending.trim().equals("null") || ending.trim().equals("Maad")) {
                        output += dWord + " ";
                    } else {
                        // get prefix
                        int pos = word.indexOf(stem);
                        if (pos == -1) {
                            if (stem.startsWith("#")) {
                                stem = stem.substring(1);
                                pos = word.indexOf(stem);
                            }
                        }

                        String prefixPlusStem = "";
                        if (pos == -1) {
                            if (stem.contains("+"))
                                stem = stem.substring(0, stem.indexOf("+"));
                            pos = word.indexOf(stem);
                            if (pos > -1) {
                                prefixPlusStem = DiacritizeText.correctLamAlefLam((word.substring(0, pos) + stem).replace("+", ""), false);
                            } else {
                                prefixPlusStem = stem;
                                // System.err.println(word + "\t" + stem);
                            }

                        } else
                            prefixPlusStem = DiacritizeText.correctLamAlefLam((word.substring(0, pos) + stem).replace("+", ""), false);
                        int j = 0;
                        // if (ArabicUtils.removeDiacritics(dWord).contains("كما"))
                        //    System.err.println(dWord);
                        // get the position of the last letter in the diacritized word without diacritics
                        if (!ArabicUtils.removeDiacritics(dWord).equals(prefixPlusStem)) {
                            // System.err.println();
                        }
                        while (!(ArabicUtils.removeDiacritics(dWord.substring(0, j)).equals(prefixPlusStem))) {
                            j++;
                        }
                        String fullDiacritization = "";
                        if (ending.equals("oi")) {
                            if (stem.endsWith("+ين")) {
                                // find the last diacritic before yn,
                                // if it is null, then put a at the end of the word
                                // else put a i

                                final String stemWithoutYn = stem.substring(0, stem.indexOf("+ين"));
                                int positionInWord = 0;
                                for (int k = 0; k < stemWithoutYn.length(); k++) {
                                    positionInWord = dWord.indexOf(stemWithoutYn.substring(k, k + 1), positionInWord);
                                }
                                if (dWord.substring(positionInWord + 1, positionInWord + 2).matches("[" + ArabicUtils.buck2utf8("aiouNKF~") + "]")) {
                                    ending = "i";
                                } else {
                                    ending = "a";
                                }
                                fullDiacritization = dWord.substring(0, j) + ArabicUtils.buck2utf8(ending) + dWord.substring(j).replaceFirst("^[" + ArabicUtils.buck2utf8("aiouNKF~") + "]+", "");
                            } else {
                                fullDiacritization = dWord.substring(0, j) + ArabicUtils.buck2utf8("i") + dWord.substring(j).replaceFirst("^[" + ArabicUtils.buck2utf8("aiouNKF~") + "]+", "");
                            }
                        } else {
                            if (!ending.isEmpty()) {
                                fullDiacritization = dWord.substring(0, j) + ArabicUtils.buck2utf8(ending);
                                if (DiacritizeText.diacritizedSuffixes.containsKey(dWord.substring(j).replaceFirst("^[" + ArabicUtils.buck2utf8("aiouNKF~") + "]+", ""))) {
                                    fullDiacritization += DiacritizeText.diacritizedSuffixes.get(dWord.substring(j).replaceFirst("^[" + ArabicUtils.buck2utf8("aiouNKF~") + "]+", ""));
                                } else {
                                    fullDiacritization += dWord.substring(j).replaceFirst("^[" + ArabicUtils.buck2utf8("aiouNKF~") + "]+", "");
                                }
                                if (word.endsWith("+ه")) {
                                    if (ending.contains("i")) {
                                        fullDiacritization += ArabicUtils.buck2utf8("i");
                                    } else {
                                        fullDiacritization += ArabicUtils.buck2utf8("u");
                                    }
                                }
                            } else {
                                if (word.contains("+ه")) {
                                    String tmpdWord = dWord.replaceFirst("^[" + ArabicUtils.buck2utf8("aiouNKF~") + "]+", "");
                                    if (tmpdWord.endsWith("ه")) {
                                        tmpdWord = tmpdWord.substring(0, tmpdWord.length() - 1);
                                        // find last diacritic
                                        if (tmpdWord.matches(".*[" + ArabicUtils.buck2utf8("aiouNKF~") + "]+$")) {
                                            if (tmpdWord.endsWith(ArabicUtils.buck2utf8("i")))
                                                // ends with i
                                                ending = "i";
                                            else
                                                ending = "u";
                                            fullDiacritization = tmpdWord + "ه" + ArabicUtils.buck2utf8(ending);
                                        } else if (tmpdWord.endsWith("ي")) {
                                            ending = "i";
                                            fullDiacritization = dWord.substring(0, j) + ArabicUtils.buck2utf8(ending) + dWord.substring(j).replaceFirst("^[" + ArabicUtils.buck2utf8("aiouNKF~") + "]+", "");
                                        } else {
                                            fullDiacritization = tmpdWord + ArabicUtils.buck2utf8("a") + "ه" + ArabicUtils.buck2utf8("u");
                                        }
                                    } else {
                                        fullDiacritization = dWord.substring(0, j) + ArabicUtils.buck2utf8(ending) + dWord.substring(j).replaceFirst("^[" + ArabicUtils.buck2utf8("aiouNKF~") + "]+", "");
                                    }
                                } else {
                                    fullDiacritization = dWord.substring(0, j) + ArabicUtils.buck2utf8(ending) + dWord.substring(j).replaceFirst("^[" + ArabicUtils.buck2utf8("aiouNKF~") + "]+", "");
                                }
                            }
                        }
                        output += fullDiacritization + " ";
                    }
                }
            }
            // output = putBigramsWithSingleDiacritization(output, dt);
            return output.trim();
        }

        public static SentenceClass correctLamAlefLam(final SentenceClass sentence) {
            for (int i = 0; i < sentence.words.size(); i++) {
                final WordClass tWord = sentence.words.get(i);
                if (tWord.prefix.contains("ل+ال") || tWord.prefix.contains("لال")) {
                    tWord.wordFullyDiacritized = tWord.wordFullyDiacritized.replaceFirst("لِا", "لِ");
                }
            }
            return sentence;
        }

        public static String standardizeDiacritics(String word) {
            final String diacrtics = "[\u064e\u064b\u064f\u064c\u0650\u064d\u0652\u0651]";
            final String sokun = "\u0652";
            final String fatha = "\u064e";
            word = word.replaceFirst("^" + diacrtics + "+", "");
            word = word.replace("َا", "ا").replace("ُو", "و").replace("ِي", "ي").replace("آَ", "آ");
            int pos = word.indexOf("و");
            while (pos > 0 && pos < word.length() - 1) {
                if (!word.substring(pos - 1, pos).matches(diacrtics) && word.substring(pos + 1, pos + 2).equals(sokun)) {
                    word = word.substring(0, pos + 1) + word.substring(pos + 2);
                }
                pos = word.indexOf("و", pos + 1);
            }
            pos = word.indexOf("ي");
            while (pos > 0 && pos < word.length() - 1) {
                if (!word.substring(pos - 1, pos).matches(diacrtics) && word.substring(pos + 1, pos + 2).equals(sokun)) {
                    word = word.substring(0, pos + 1) + word.substring(pos + 2);
                }
                pos = word.indexOf("ي", pos + 1);
            }
            pos = word.indexOf("ا");
            while (pos > 0 && pos < word.length() - 1) {
                if (!word.substring(pos - 1, pos).matches(diacrtics)
                        && (word.substring(pos + 1, pos + 2).equals(sokun) || word.substring(pos + 1, pos + 2).equals(fatha))) {
                    word = word.substring(0, pos + 1) + word.substring(pos + 2);
                }
                pos = word.indexOf("ا", pos + 1);
            }
            if (word.startsWith("الْ")) {
                word = word.replaceFirst("الْ", "ال");
            }
            // word = word.replaceFirst(diacrtics + "+$", "");
            return word;
        }

        public static String transferDiacriticsFromWordToSegmentedVersion(String diacritizedWord, String stemmedWord) {
            final boolean startsWithLamLam = false;
            final boolean startsWithWaLamLam = false;
            final boolean startsWithFaLamLam = false;
            if (
                    (stemmedWord.startsWith("ل+ال") &&
                            ArabicUtils.removeDiacritics(diacritizedWord).startsWith("لل")) ||
                            (stemmedWord.startsWith("و+ل+ال") &&
                                    ArabicUtils.removeDiacritics(diacritizedWord).startsWith("ولل")) ||
                            (stemmedWord.startsWith("ف+ل+ال") &&
                                    ArabicUtils.removeDiacritics(diacritizedWord).startsWith("فلل"))
            ) {
                // startsWithLamLam = true;
                final int posFirstLam = diacritizedWord.indexOf("ل");
                final int posSecondLam = diacritizedWord.indexOf("ل", posFirstLam + 1);
                diacritizedWord = diacritizedWord.substring(0, posSecondLam) + "ا" + diacritizedWord.substring(posSecondLam);
            }

            String output = "";
            stemmedWord = stemmedWord.replace(" ", "");
            stemmedWord = stemmedWord.replaceFirst("\\+$", "");
            if (diacritizedWord.equals(stemmedWord) || !stemmedWord.contains("+"))
                return diacritizedWord;

            int pos = 0;
            for (int i = 0; i < stemmedWord.length(); i++) {
                if (stemmedWord.charAt(i) == '+' || stemmedWord.charAt(i) == ';') {
                    {
                        output += stemmedWord.substring(i, i + 1);
                    }
                } else {
                    int loc = diacritizedWord.indexOf(stemmedWord.substring(i, i + 1), pos);
                    if (loc >= 0) {
                        final String diacritics = diacritizedWord.substring(pos, loc);
                        output += diacritics + stemmedWord.charAt(i);
                        // add trailing diacritics
                        loc++;
                        while (loc < diacritizedWord.length() && diacritizedWord.substring(loc, loc + 1).matches("[" +
                                ArabicUtils.buck2utf8("aiouNKF~") + "]")) {
                            output += diacritizedWord.substring(loc, loc + 1);
                            loc++;
                        }
                        pos = loc;
                    } else {
                        // System.err.println(diacritizedWord + "\t" + stemmedWord);
                    }
                }
            }
            return output;
        }

        public void trainAndTestSVMCaseEnding(final String filename) throws Exception {
            this.rce.train(filename);
            this.rce.generateSVM(filename);
        }

        private void loadLM() throws IOException, ClassNotFoundException {
            DiacritizeText.ngrams = this.deserializeMap("ngrams");
//        BufferedReader br = Main.openFileForReading("c:/RESEARCH/FromMac/work/CLASSIC/DIACRITIZE/NEW-RDI/all-text.txt.nocase.3.arpa");
//        String line = "";
//        int gram = 0;
//        while ((line = br.readLine()) != null)
//        {
//            if (line.startsWith("\\") && line.contains("-grams:"))
//            {
//                gram = Integer.parseInt(line.substring(1).replace("-grams:", "").trim());
//                // ngrams.put(gram, new HashMap<String, Double>());
//            }
//            String[] parts = line.split("\t");
//            if (parts.length >= 2)
//            {
//                String token = parts[1];
//                double score = Double.parseDouble(parts[0]);
//                ngrams.put(token, score);
//            }
//        }
//        serializeMap(dir, "ngrams", ngrams);
        }

        public double scoreSequence(final String s) {
            double output = 0;
            final String[] parts = s.split("[ \t]+");
            for (int i = 0; i < parts.length; i++) {
                if (i == 0) {
                    if (DiacritizeText.ngrams.containsKey(parts[0]))
                        output += DiacritizeText.ngrams.get(parts[i]);
                    else
                        output += DiacritizeText.ngrams.get("<unk>");
                } else {
                    final String key = parts[i - 1] + " " + parts[i];
                    if (DiacritizeText.ngrams.containsKey(key))
                        output += DiacritizeText.ngrams.get(key);
                    else if (DiacritizeText.ngrams.containsKey(parts[i]))
                        output += DiacritizeText.ngrams.get(parts[i]);
                    else
                        output += DiacritizeText.ngrams.get("<unk>");
                }
            }
            return output;
        }

        public double scoreSequenceTrigram(final String s) {
            double output = 0;
            final String[] parts = s.split("[ \t]+");
            for (int i = 0; i < parts.length; i++) {
                if (i == 0) {
                    if (DiacritizeText.ngrams.containsKey(parts[0]))
                        output += DiacritizeText.ngrams.get(parts[i]);
                    else
                        output += DiacritizeText.ngrams.get("<unk>");
                } else if (i == 1) {
                    final String key = parts[1 - 1] + " " + parts[i];
                    if (DiacritizeText.ngrams.containsKey(key))
                        output += DiacritizeText.ngrams.get(key);
                    else if (DiacritizeText.ngrams.containsKey(parts[i]))
                        output += DiacritizeText.ngrams.get(parts[i]);
                    else
                        output += DiacritizeText.ngrams.get("<unk>");
                } else {
                    final String key = parts[i - 2] + " " + parts[i - 1] + " " + parts[i];
                    if (DiacritizeText.ngrams.containsKey(key))
                        output += DiacritizeText.ngrams.get(key);
                    else if (DiacritizeText.ngrams.containsKey(parts[i - 1] + " " + parts[i]))
                        output += DiacritizeText.ngrams.get(parts[i - 1] + " " + parts[i]);
                    else if (DiacritizeText.ngrams.containsKey(parts[i]))
                        output += DiacritizeText.ngrams.get(parts[i]);
                    else
                        output += DiacritizeText.ngrams.get("<unk>");
                }
            }
            return output;
        }

        public HashMap deserializeMap(final String MapName) throws IOException, ClassNotFoundException {
            final ObjectInputStream ois = new ObjectInputStream(getClass().getResourceAsStream("/data/FDTdata." + MapName + ".ser"));
            final HashMap map = (HashMap) ois.readObject();
            ois.close();
            return map;
        }

        public void serializeMap(final String BinDir, final String MapName, final HashMap input) throws IOException {
            final FileOutputStream fos
                    = new FileOutputStream(BinDir + "FDTdata." + MapName + ".ser");
            final ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(input);
            oos.close();
            fos.close();
        }

        public HashMap deserializeMap(final String BinDir, final String MapName) throws IOException, ClassNotFoundException {
            final FileInputStream fis = new FileInputStream(BinDir + "FDTdata." + MapName + ".ser");
            final ObjectInputStream ois = new ObjectInputStream(fis);
            final HashMap map = (HashMap) ois.readObject();
            ois.close();
            fis.close();
            return map;
        }

        public String removeCaseEnding(final String input) throws IOException {
            if (input.trim().length() == 0)
                return input;
            String output = "";
            final String segmented = DiacritizeText.farasaSegmenter.getProperSegmentation(DiacritizeText.farasaSegmenter.segmentLine(input).get(0));
            output = this.removeCaseEnding(segmented, input);
            return output.trim();
        }

        private String removeCaseEnding(final String stemmed, final String diacritized) {
            String output = "";
            final String[] parts = (" " + stemmed + " ").split(";");
            if (parts.length != 3)
                return diacritized;
            String suffixes = parts[2].trim();
            if (suffixes.startsWith("+ة"))
                suffixes = suffixes.substring(2);
            else if (suffixes.startsWith("+ت+"))
                suffixes = suffixes.substring(2);
            else if (suffixes.startsWith("+ات") || suffixes.startsWith("+ون") || suffixes.startsWith("+ين") || suffixes.startsWith("+ان"))
                suffixes = suffixes.substring(3);

            if (suffixes.length() == 0) {
                // remove last diacritic
                output = diacritized.replaceFirst("[" + ArabicUtils.buck2utf8("aiouNFK") + "]+$", "");
            } else {
                int i = diacritized.length();
                while (i > 0 && !ArabicUtils.removeDiacritics(diacritized.substring(i)).equals(suffixes.replace("+", ""))) {
                    i--;
                }
                if (i == 0) {
                    return diacritized;
                } else {
                    final String head = diacritized.substring(0, i);
                    final String tail = diacritized.substring(i);
                    output = head.replaceFirst("[" + ArabicUtils.buck2utf8("aiouNFK") + "]+$", "") + tail;
                }
            }
            return output;
        }

        public TMap<String, String> getbigramsWithSingleDiacritizations() {
            return DiacritizeText.bigramsWithSingleDiacritizations;
        }

        public ArrayList<String> tagWords(final String inputText) throws Exception {
            // ArrayList<String> output = tagger.tag(inputText, false, true);
            final ArrayList<String> output = DiacritizeText.farasaSegmenter.segmentLine(inputText);
            // Sentence output = farasaPOSTagger.tagLine(segmentedWords);
            return output;
        }

        public String diacritize(final String input, final boolean keepOrgDiacritics) throws Exception {
            final ArrayList<String> words = ArabicUtils.tokenize(input);
            if (keepOrgDiacritics) {

                final ArrayList<String> rawWords = ArabicUtils.tokenizeWithoutProcessing(input);
                String output = this.diacritize(words);

                final String[] outputWords = output.split(" +");
                if (outputWords.length == rawWords.size()) {
                    output = "";
                    for (int i = 0; i < outputWords.length; i++) {
                        if (rawWords.get(i).matches(".*[" + ArabicUtils.AllArabicDiacritics + "]+.*")) {
                            output += rawWords.get(i) + " ";
                        } else {
                            output += outputWords[i] + " ";
                        }
                    }
                    return output.trim();
                } else {
                    return output.trim();
                }
            } else {
                return this.diacritize(words);
            }
        }

        public String diacritize(final String input) throws Exception {
            final ArrayList<String> words = ArabicUtils.tokenize(input);
            // split line into sentences
            final ArrayList<ArrayList<String>> sentences = new ArrayList<ArrayList<String>>();
            ArrayList<String> temp = new ArrayList<String>();
            for (int i = 0; i < words.size(); i++) {
                temp.add(words.get(i));
                if (words.get(i).equals(".")) {
                    if (i < words.size() - 2 && !words.get(i + 1).substring(0, 1).matches("[" + ArabicUtils.AllDigits + "]") && !words.get(i + 1).equals(".")) {
                        // split here
                        sentences.add(temp);
                        temp = new ArrayList<String>();
                    }
                } else if (words.get(i).equals("?") || words.get(i).equals("؟") || words.get(i).equals("!")) {
                    // split here
                    sentences.add(temp);
                    temp = new ArrayList<String>();
                }
            }
            if (temp.size() > 0)
                sentences.add(temp);
            String output = "";
            for (final ArrayList<String> t : sentences)
                output += " " + this.diacritize(t);

            return output.trim();
        }

        public String diacritize(final ArrayList<String> input) throws Exception {
            final HashMap<Integer, ArrayList<String>> latice = this.buildLaticeStem(input);
            SentenceClass sentence = this.rce.putCaseEnding(this.findBestPath(latice));
            // sentence = correctLamAlefLam(sentence);
            // sentence = diacritizePrefixesAndSuffixes(sentence);
            sentence = this.applyUnigramsWithSingleDiacritization(sentence);
            String output = "";
            for (int i = 1; i < sentence.words.size() - 1; i++) {
                // if (!sentence.words.get(i).POS.equals("S") && !sentence.words.get(i).POS.equals("E"))
                {
                    output += DiacritizeText.standardizeDiacritics(sentence.words.get(i).wordFullyDiacritized) + " ";
                }
            }
            // return standardizeDiacritics(rce.putCaseEnding(findBestPath(latice))).replace("  ", " ").trim();
            return output.trim();
        }

        public SentenceClass applyUnigramsWithSingleDiacritization(final SentenceClass sentence) {
            for (final WordClass word : sentence.words) {
                final String key = ArabicUtils.removeDiacritics(word.wordFullyDiacritized.replace("+", "")).trim();
                if (DiacritizeText.unigramsWithSingleDiacritization.containsKey(key))
                    word.wordFullyDiacritized = DiacritizeText.unigramsWithSingleDiacritization.get(key);
            }
            return sentence;
        }

        public SentenceClass diacritizePrefixesAndSuffixes(final SentenceClass sentence) throws Exception {
            for (final WordClass word : sentence.words) {
                // String[] seg = getProperSegmentation(" " + word.word + " ").split(";");
                String prefix = "";
                if (!word.prefix.contains("no") && !word.prefix.equals("") && !word.prefix.equals("#"))
                    prefix = this.diacritizePrefixes(word.prefix);
                String suffix = "";
                if (!word.suffix.contains("no") && !word.suffix.equals("") && !word.suffix.equals("#"))
                    suffix = this.diacritizeSuffixes(word.suffix, word.guessDiacritic);
                final String stem = this.getWordStem(word.wordFullyDiacritized, false);
                final String wholeWord = prefix + stem + suffix;
                word.wordFullyDiacritized = wholeWord;
            }
            return sentence;
        }

        public String limitNumberOfChoices(final String input, final int max) {
            String output = ";";
            final String[] parts = input.split(";");
            for (int i = 0; i <= Math.min(parts.length - 1, max); i++) {
                output += parts[i] + ";";
            }
            return output;
        }

        private TMap<String, String> loadCandidates(final String filePath) throws IOException, ClassNotFoundException {
            TMap<String, String> candidates = new THashMap<String, String>();

            String line = "";
            final File file = new File(filePath + ".ser");
            if (file.exists()) {
                final ObjectInputStream ios = new ObjectInputStream(new FileInputStream(file));
                candidates = (THashMap) ios.readObject();
            } else {
                final BufferedReader sr = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filePath))));
                while ((line = sr.readLine()) != null) {
                    if (line.length() > 0) {
                        final String[] lineParts = line.split("\t");
                        if (line.length() > 0 && lineParts.length > 0) // && Regex.IsMatch("^[0-9\\.\\-]$"))
                        {
                            candidates.put(lineParts[0], this.limitNumberOfChoices(lineParts[1], 100));
                        }
                    }
                }
                final ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
                oos.writeObject(candidates);
                oos.close();
            }
            return candidates;
        }

        private boolean checkIfAllChoicesHaveTheSameScore(final ArrayList<String> unigramChoices) throws IOException {
            double bestScore = -1000;
            boolean sameScore = true;
            for (int i = 0; i < unigramChoices.size(); i++) { // s : paths) {
                // double finalScore = scoreUsingLM(unigramChoices.get(i));
                // double finalScore = scoreUsingTwoLMs(getTheTrailingNWords(unigramChoices.get(i), 5));
                final double finalScore = this.scoreSequence(this.getTheTrailingNWords(unigramChoices.get(i), 5));
                if (bestScore != finalScore && i > 0) {
                    sameScore = false;
                }
                if (bestScore < finalScore) {
                    bestScore = finalScore;
                }
            }
            return sameScore;
        }

        private String GetBestChoiceUnigram(final ArrayList<String> unigramChoices) throws IOException {
            double bestScore = -1000;
            String bestChoice = "";
            for (int i = 0; i < unigramChoices.size(); i++) { // s : paths) {
                // double finalScore = scoreUsingLM(unigramChoices.get(i));
                // double finalScore = scoreUsingTwoLMs(getTheTrailingNWords(unigramChoices.get(i), 5));
                final double finalScore = this.scoreSequence(this.getTheTrailingNWords(unigramChoices.get(i), 5));
                if (bestScore < finalScore) {
                    bestScore = finalScore;
                    bestChoice = unigramChoices.get(i);
                }
            }
            return bestChoice;
        }

        private String getWordStem(final String w, final boolean withAffixes) throws Exception {
            final String lastDiacriticRegex = "[" + ArabicUtils.buck2utf8("aiou") + "]+$";
            // try stemming
            final ArrayList<String> clitics = DiacritizeText.farasaSegmenter.segmentLine(w);// tagger.tag(w, true, false);
            String cliticSplit = "";
            for (int c = 0; c < clitics.size(); c++) {
                if (!clitics.get(c).equals("_")) {
                    if (cliticSplit.trim().length() > 0) {
                        cliticSplit += "+";
                    }
                    cliticSplit += clitics.get(c);
                }
            }

            cliticSplit = this.getProperSegmentation(cliticSplit);
            cliticSplit = DiacritizeText.transferDiacriticsFromWordToSegmentedVersion(w, cliticSplit);
            // prefixes & stem & suffixes
            if (withAffixes) {
                return cliticSplit;
            } else {
                if (cliticSplit.contains(";")) {
                    String taMarbouta = "";
                    if (cliticSplit.contains(";+ة") || cliticSplit.contains(";+ت")) {
                        if (!cliticSplit.endsWith(";+ت")) {
                            taMarbouta = cliticSplit.substring(cliticSplit.indexOf(";+ة") + 2);
                            // get diacritic on ta marbouta
//                        int pos = cliticSplit.indexOf(";+ة");
//                        for (int k = pos + 3; k < cliticSplit.length(); k++)
//                        {
//
//                        }
                        }
//                    else if (cliticSplit.contains(";+ت"))
//                    {
//                        int pos = cliticSplit.indexOf(";+ت");
//                        taMarbouta = cliticSplit.substring(pos + 2, cliticSplit.indexOf("+", pos + 2));
//                    }
                    }
                    String stem = cliticSplit.substring(cliticSplit.indexOf(";") + 1);
                    stem = stem.substring(0, stem.indexOf(";"));
                    stem = stem.replace(";", "");
                    if (taMarbouta.length() > 0) {
                        stem += taMarbouta;
                    } else {
                        stem = stem.replaceFirst(lastDiacriticRegex, "");
                    }
                    return stem;
                } else {
                    return cliticSplit;
                }
            }
        }

        private String getWordStem(final WordClass w, final boolean withAffixes) throws Exception {
            // this code needs debugging, because it may have some errors
            final String lastDiacriticRegex = "[" + ArabicUtils.buck2utf8("aiou") + "]+$";
            // try stemming
            final ArrayList<String> clitics = DiacritizeText.farasaSegmenter.segmentLine(w.word);// tagger.tag(w, true, false);
            String cliticSplit = "";
            for (int c = 0; c < clitics.size(); c++) {
                if (!clitics.get(c).equals("_")) {
                    if (cliticSplit.trim().length() > 0) {
                        cliticSplit += "+";
                    }
                    cliticSplit += clitics.get(c);
                }
            }

            cliticSplit = this.getProperSegmentation(cliticSplit);
            cliticSplit = DiacritizeText.transferDiacriticsFromWordToSegmentedVersion(w.word, cliticSplit);
            // prefixes & stem & suffixes
            if (withAffixes) {
                return cliticSplit;
            } else {
                if (cliticSplit.contains(";")) {
                    String taMarbouta = "";
                    if (cliticSplit.contains(";+ة") || cliticSplit.contains(";+ت")) {
                        if (!cliticSplit.endsWith(";+ت")) {
                            taMarbouta = cliticSplit.substring(cliticSplit.indexOf(";+ة") + 2);
                            // get diacritic on ta marbouta
//                        int pos = cliticSplit.indexOf(";+ة");
//                        for (int k = pos + 3; k < cliticSplit.length(); k++)
//                        {
//
//                        }
                        }
//                    else if (cliticSplit.contains(";+ت"))
//                    {
//                        int pos = cliticSplit.indexOf(";+ت");
//                        taMarbouta = cliticSplit.substring(pos + 2, cliticSplit.indexOf("+", pos + 2));
//                    }
                    }
                    String stem = cliticSplit.substring(cliticSplit.indexOf(";") + 1);
                    stem = stem.substring(0, stem.indexOf(";"));
                    stem = stem.replace(";", "");
                    if (taMarbouta.length() > 0) {
                        stem += taMarbouta;
                    } else {
                        stem = stem.replaceFirst(lastDiacriticRegex, "");
                    }
                    return stem;
                } else {
                    return cliticSplit;
                }
            }
        }

        private String checkIfOOVandGetMostLikelyUnigramSolution(final ArrayList<String> unigramChoices) throws Exception {

            String output = "***";
            if (unigramChoices.size() == 1) {
                return unigramChoices.get(0);
            } else {
                // check if they have same score
                final boolean sameScore = this.checkIfAllChoicesHaveTheSameScore(unigramChoices);
                if (sameScore) {
                    // System.err.println(unigramChoices.get(0));

                    // attempt to stem and find the best solution
                    final ArrayList<String> stems = new ArrayList<String>();
                    final HashMap<String, String> stemToWordMap = new HashMap<String, String>();
                    for (final String s : unigramChoices) {
                        final String stem = this.getWordStem(s, false);
                        stems.add(stem.replaceFirst("[" + ArabicUtils.buck2utf8("aiou") + "]+$", ""));
                        stemToWordMap.put(stem, s);
                    }
//                if (ArabicUtils.removeDiacritics(stems.get(0)).equals("حياة"))
//                    System.err.println();
                    if (this.checkIfAllChoicesHaveTheSameScore(stems)) {
                        // System.err.println("**" + unigramChoices.get(0) + "\t" + stems.get(0));
                        // revert to the most commonly used template
                        if (DiacritizeText.defaultDiacritizationBasedOnTemplateProbability.containsKey(ArabicUtils.removeDiacritics(stems.get(0)))) {
                            output = DiacritizeText.defaultDiacritizationBasedOnTemplateProbability.get(ArabicUtils.removeDiacritics(stems.get(0)));
                            if (stemToWordMap.containsKey(output)) {
                                output = stemToWordMap.get(output);
                            } else {
                                // get original prefixes and suffixes
                                final String stemWithPrefixesAndSuffixes = this.getWordStem(ArabicUtils.removeDiacritics(unigramChoices.get(0)), true);
                                if (stemWithPrefixesAndSuffixes.contains(";")) {
                                    final String Prefixes = stemWithPrefixesAndSuffixes.replaceFirst(";.*", "");
                                    String Suffixes = stemWithPrefixesAndSuffixes.replaceFirst(".*;", "");
                                    if (Suffixes.startsWith("+ة")) {
                                        Suffixes = Suffixes.substring(2);
                                    } else if (Suffixes.startsWith("+ت") && !Suffixes.endsWith("+ت")) {
                                        Suffixes = Suffixes.substring(2);
                                        if (output.endsWith("ة")) {
                                            output = output.replace("ة", "ت");
                                        }
                                    }
                                    output = this.diacritizePrefixes(Prefixes) + output + this.diacritizeSuffixes(Suffixes, "");
                                }
                            }
                        }
                        //FIXME: System.err.println("**" + unigramChoices.get(0) + "\t" + stems.get(0) + "\tusing: " + output);
                    } else {
                        final String bestChoice = this.GetBestChoiceUnigram(stems);
                        output = stemToWordMap.get(bestChoice);
                        if (output == null) {
                            output = (String) unigramChoices.toArray()[0];
                        }
                    }
                }
                return output;
            }
        }

        private String findBestPath(final HashMap<Integer, ArrayList<String>> latice) throws Exception {
            final String space = " +";
            final HashMap<Integer, String> finalAnswer = new HashMap<Integer, String>();

            for (int i = 1; i <= latice.keySet().size() - 1; i++) {
                String sBase = "";
                finalAnswer.get(0);
                for (int j = 1; j < i; j++) {
                    sBase += " " + finalAnswer.get(j);
                }

                final ArrayList<String> paths = new ArrayList<String>();
//            if (latice == null || latice.get(i) == null || checkIfOOVandGetMostLikelyUnigramSolution(latice.get(i)) == null)
//                System.err.println();
                if (this.checkIfOOVandGetMostLikelyUnigramSolution(latice.get(i)).equals("***")) {
                    // add options for current node
                    for (final String sol : latice.get(i)) {
                        paths.add(sBase + " " + sol);
                    }
                } else {
                    paths.add(sBase + " " + this.checkIfOOVandGetMostLikelyUnigramSolution(latice.get(i)));
                }

                final ArrayList<String> pathsNext = new ArrayList<String>();
                // add options for next node
                for (final String s : paths) {
                    // System.err.println(i);
                    for (final String sol : latice.get(i + 1)) {
                        pathsNext.add(s + " " + sol);
                    }
                }

                // determine best option for current word
                // this would be done using the language model
                final String bestPathOutput = this.findBestPathLM(pathsNext).trim();

                final String[] bestPath = bestPathOutput.split(" +");

                if (bestPath.length == i + 1 || bestPath.length == i) { // + 2) {
                    finalAnswer.put(i, bestPath[i - 1]);
                } else {
                    // System.err.println("ERROR");
                }
            }
            String sBest = ""; // finalAnswer.get(1);
            for (int k = 1; k <= finalAnswer.keySet().size(); k++) {
                sBest += " " + finalAnswer.get(k);
            }
            return sBest.replaceAll(" +", " ").trim();
        }

        private String correctLeadingLamAlefLam(String s) {
            if (s.startsWith("لال")) {
                s = "لل" + s.substring(3);
            }
            return s;
        }

        public double scoreUsingLM(final String s) throws IOException {
            DiacritizeText.bwLM.write(s + "\n");
            DiacritizeText.bwLM.flush();
            String stemp = DiacritizeText.brLM.readLine();
            if (stemp.contains("Total:")) {
                stemp = stemp.replaceFirst(".*Total\\:", "").trim();
                stemp = stemp.replaceFirst("OOV.*", "").trim();
            } else {
                stemp = "-1000";
            }
            if (stemp.contains("inf")) {
                return -1000f;
            }

            final double finalScore = Double.parseDouble(stemp);
            return finalScore;
        }

        public double scoreUsingTwoLMs(final String s) throws IOException {
            // limit scoring to last 5 words only and skip previous
            String stemp = "0";
            if (DiacritizeText.bwLM != null && DiacritizeText.brLM != null) {
                DiacritizeText.bwLM.write(s + "\n");
                DiacritizeText.bwLM.flush();

                stemp = DiacritizeText.brLM.readLine();
                if (stemp.contains("Total:")) {
                    stemp = stemp.replaceFirst(".*Total\\:", "").trim();
                    stemp = stemp.replaceFirst("OOV.*", "").trim();
                } else {
                    stemp = "-100";
                }
            }
            double firstScore = 0d;
            if (stemp.contains("inf")) {
                firstScore = -100f;
            } else {
                firstScore = Double.parseDouble(stemp);
            }

            String stemp2ndLM = "0";
            if (DiacritizeText.bwLM2ndLM != null && DiacritizeText.brLM2ndLM != null) {
                DiacritizeText.bwLM2ndLM.write(s + "\n");
                DiacritizeText.bwLM2ndLM.flush();
                // System.err.println(s);
                stemp2ndLM = DiacritizeText.brLM2ndLM.readLine();
                if (stemp2ndLM.contains("Total:")) {
                    stemp2ndLM = stemp2ndLM.replaceFirst(".*Total\\:", "").trim();
                    stemp2ndLM = stemp2ndLM.replaceFirst("OOV.*", "").trim();
                } else {
                    stemp2ndLM = "-100";
                }
            }
            double secondScore = 0d;
            if (stemp2ndLM.contains("inf")) {
                secondScore = -100f;
            } else {
                secondScore = Double.parseDouble(stemp2ndLM);
            }

            double finalScore = 0d;
            if (firstScore > -100 && secondScore > -100) {
                finalScore = 0.1 * firstScore + 0.9 * secondScore;
            } else {
                finalScore = Math.min(firstScore, secondScore);
            }

            return finalScore;
        }

        private String findBestPathLM(final ArrayList<String> paths) throws IOException {
            if (paths.size() == 1) {
                return paths.get(0);
            } else {
                double bestScore = -1000;
                boolean sameScore = true;
                String bestPath = "";
                for (int i = 0; i < paths.size(); i++) { // s : paths) {
                    // only score the last n words
                    final String s = paths.get(i);
                    final String ss = this.getTheTrailingNWords(s, 5);
                    // double finalScore = scoreUsingLM(ss);
                    // double finalScore = scoreUsingTwoLMs(ss);
                    final double finalScore = this.scoreSequence(ss);
                    if (bestScore != finalScore && i > 0) {
                        sameScore = false;
                    }
                    if (bestScore < finalScore) {
                        bestScore = finalScore;
                        bestPath = s;
                    }
                }
                // if (sameScore)
                //    bestPath += "***";
                return bestPath;
            }
        }

        private String getTheTrailingNWords(final String s, final int n) {
            String ss = "";
            final String[] parts = s.split(" +");
            if (parts.length <= n) {
                return s;
            } else {
                for (int i = parts.length - n; i < parts.length; i++) {
                    ss += parts[i] + " ";
                }
            }
            return ss.trim();
        }

        private HashMap<Integer, ArrayList<String>> buildLatice(final ArrayList<String> words) {
            final HashMap<Integer, ArrayList<String>> latice = new HashMap<Integer, ArrayList<String>>();
            int i = 0;

            ArrayList<String> temp = new ArrayList<String>();
            // temp.add("<s>");
            temp.add(" ");
            i++;
            latice.put(i, temp);

            for (final String w : words) {
                // if (bStem == false) {
                final String norm = ArabicUtils.removeDiacritics(w); // correctLeadingLamAlefLam(normalizeFull(w));
                if (this.candidatesUnigram.containsKey(norm) && this.candidatesUnigram.get(norm).split(";").length > 0) {
                    temp = new ArrayList<String>();
                    for (final String s : this.candidatesUnigram.get(norm).split(";")) {
                        if (s.length() > 0) {
                            if (!temp.contains(s)) {
                                temp.add(s);
                            }
                        }
                    }

                    // if multiple candidates exist and one does not have diacritics, then remove it
                    if (temp.size() > 1) {
                        final ArrayList<String> ttemp = new ArrayList<String>(temp);
                        for (final String t : temp) {
                            //                    if (!t.matches(".*[ًٌٍُِّْ].*"))
                            {
                                // put phoney dicritics and see if they get removed
                                String tt = "";
                                for (int k = 0; k < t.length(); k++) {
                                    if (t.substring(k, k + 1).matches("[ايو]")) {
                                        tt += t.substring(k, k + 1);
                                    } else {
                                        if (k < t.length() - 1) {
                                            if (t.substring(k + 1, k + 2).matches("[ايو]")) {
                                                tt += t.substring(k, k + 1);
                                            } else {
                                                tt += t.charAt(k) + ArabicUtils.buck2utf8("a");
                                            }
                                        } else {
                                            tt += t.charAt(k) + ArabicUtils.buck2utf8("a");
                                        }
                                    }
                                }
                                if (tt.matches(".*[ًٌٍَُِّْ].*")) {
                                    ttemp.remove(t);
                                }
                            }
                        }
                        if (ttemp.size() > 0 && temp.size() != ttemp.size()) {
                            temp = new ArrayList<String>(ttemp);
                        }
                    }

                    if (temp.size() > 0) {
                        latice.put(i, temp);
                    }
                } else {
                    temp = new ArrayList<String>();
                    temp.add(w);
                    latice.put(i, temp);
                }
                i++;
            }
            temp = new ArrayList<String>();
            // temp.add("</s>");
            temp.add(" ");
            latice.put(i, temp);
            return latice;
        }

        public String diacritizePrefixes(final String prefixString) {
            final String[] tmpP = prefixString.split("\\+");
            final ArrayList<String> prefixes = new ArrayList<String>();
            for (final String p : tmpP) {
                if (p.length() > 0) {
                    prefixes.add(p);
                }
            }
            return this.diacritizePrefixes(prefixes);
        }

        public String diacritizePrefixes(final ArrayList<String> prefixes) {
            String diacritizedWord = "";
            for (final String p : prefixes) {
                if (p.length() > 0) {
                    diacritizedWord += DiacritizeText.diacritizedPrefixes.get(p);
                }
            }
            return diacritizedWord;
        }

        public String diacritizeSuffixes(final String suffixString, final String caseEnding) {
            final String[] tmpS = suffixString.split("\\+");
            final ArrayList<String> suffixes = new ArrayList<String>();
            for (final String s : tmpS) {
                if (s.length() > 0) {
                    suffixes.add(s);
                }
            }
            return this.diacritizeSuffixes(suffixes, caseEnding);
        }

        public String diacritizeSuffixes(final ArrayList<String> suffixes, final String caseEnding) {
            String diacritizedWord = "";
            for (final String p : suffixes) {
                if (p.length() > 0) {
                    if (p.equals("ة") || p.equals("ت")) {
                        diacritizedWord += p + caseEnding;
                    } else if (DiacritizeText.diacritizedSuffixes.containsKey(p)) {
                        diacritizedWord += DiacritizeText.diacritizedSuffixes.get(p);
                    } else {
                        diacritizedWord += p;
                    }
                }
            }
            return diacritizedWord;
        }

        public HashMap<Integer, ArrayList<String>> buildLaticeStem(final ArrayList<String> words) throws Exception {
            final HashMap<Integer, ArrayList<String>> latice = new HashMap<Integer, ArrayList<String>>();
            int i = 0;

            final String[] diacritics =
                    {
                            ""
                    }; // a", "i", "o", "u", "N", "K", "F", ""};

            ArrayList<String> temp = new ArrayList<String>();
            // temp.add("<s>");
            temp.add(" ");
            i++;
            latice.put(i, temp);

            for (final String w : words) {
                if (w.length() > 0) {
                    final String norm = ArabicUtils.removeDiacritics(w); // correctLeadingLamAlefLam(normalizeFull(w));
                    if (this.candidatesUnigram.containsKey(norm) && this.candidatesUnigram.get(norm).split(";").length > 0) {
                        temp = new ArrayList<String>();
                        for (final String s : this.candidatesUnigram.get(norm).split(";")) {
                            if (s.length() > 0) {
                                if (!temp.contains(s)) {
                                    for (final String di : diacritics) {
                                        if (!(di.matches("[NKF]") && s.endsWith("ّ")) && !temp.contains(s + ArabicUtils.buck2utf8(di))) // && seenWordsMap.containsKey(s)) // don't put tanween with shadda
                                        {
                                            temp.add(s + ArabicUtils.buck2utf8(di));
                                        }
                                    }
                                }
                            }
                        }
                        if (temp.size() > 0) {
                            latice.put(i, temp);
                        }
                    } else {
                        // try stemming
                        final ArrayList<String> clitics = DiacritizeText.farasaSegmenter.segmentLine(w);// tagger.tag(w, true, false);
                        String cliticSplit = "";
                        for (int c = 0; c < clitics.size(); c++) {
                            if (!clitics.get(c).equals("_")) {
                                if (cliticSplit.trim().length() > 0) {
                                    cliticSplit += "+";
                                }
                                cliticSplit += clitics.get(c);
                            }
                        }

                        cliticSplit = this.getProperSegmentation(cliticSplit);


                        // prefixes & stem & suffixes
                        cliticSplit = cliticSplit.replace("ل+ال", "لل");





                        final String[] tprefix = (" " + cliticSplit + " ").split(";")[0].trim().split("\\+");
                        final ArrayList<String> prefixes = new ArrayList<String>();
                        for (final String p : tprefix) {
                            if (p.trim().length() > 0) {
                                prefixes.add(p);
                            }
                        }

                        final String[] tsuffix = (" " + cliticSplit + " ").split(";")[2].trim().split("\\+");
                        final ArrayList<String> suffixes = new ArrayList<String>();
                        for (final String p : tsuffix) {
                            if (p.trim().length() > 0) {
                                suffixes.add(p);
                            }
                        }

                        String stem = (" " + cliticSplit + " ").split(";")[1].trim();
                        if(stem.contains("الله"))
                            stem =  stem.replace("الله","لله");

                        // first suffix is ta marbouta, add it to the stem
                        if ((suffixes.size() > 0 && suffixes.get(0).equals("ة"))
                                || (suffixes.size() > 1 && suffixes.get(0).equals("ت"))) {
                            stem += "ة";
                            suffixes.remove(0);
                        } else if (suffixes.size() > 0 && suffixes.get(0).equals("ات")) {
                            stem += "ات";
                            suffixes.remove(0);
                        }

                        if (this.candidatesUnigram.containsKey(stem) && this.candidatesUnigram.get(stem).split(";").length > 0) {
                            temp = new ArrayList<String>();
                            // we want to get the highest scoring stem and we omit the others
                            String topChoice = "";
                            double topChoiceScore = -1000d;
                            for (final String s : this.candidatesUnigram.get(stem).split(";")) {
                                if (s.trim().length() > 0) {
                                    // double tmpScore = scoreUsingTwoLMs(getTheTrailingNWords(s, 5));
                                    final double tmpScore = this.scoreSequence(this.getTheTrailingNWords(s, 5));
                                    if (topChoiceScore < tmpScore) {
                                        topChoiceScore = tmpScore;
                                        topChoice = s;
                                    }
                                }
                            }
                            if (suffixes.size() > 0 && topChoice.endsWith("ة")) {
                                topChoice = topChoice.substring(0, topChoice.length() - 1) + "ت";
                            }
                            final String diacritizedWord = this.diacritizePrefixes(prefixes) + topChoice + this.diacritizeSuffixes(suffixes, "");
                            temp.add(diacritizedWord);
                            latice.put(i, temp);
                        } else if (!this.diacritizeBasedOnTemplate(prefixes, suffixes, stem).equals("Y")) {
                            final String diacritizedWord = this.diacritizeBasedOnTemplate(prefixes, suffixes, stem);
                            temp = new ArrayList<String>();

                            if (ArabicUtils.removeDiacritics(diacritizedWord).equals(w))
                                temp.add(diacritizedWord);
                            else
                                temp.add(w);
                            latice.put(i, temp);
                        } else if (this.auxWordsTransliteration.containsKey(ArabicUtils.normalize(stem))) {
                            temp = new ArrayList<String>();
                            temp.add("@@@" + this.diacritizePrefixes(prefixes) + DiacritizeText.standardizeDiacritics(this.auxWordsTransliteration.get(ArabicUtils.normalize(stem))) + this.diacritizeSuffixes(suffixes, ""));
                            // System.err.println("******" + w + "\t" + stem + "\t" + auxWordsTransliteration.get(ArabicUtils.normalize(stem)));
                            latice.put(i, temp);
                        } else {
                            temp = new ArrayList<String>();
                            temp.add(w);
                            latice.put(i, temp);
                        }
                    }
                } else {
                    temp = new ArrayList<String>();
                    temp.add("");
                    latice.put(i, temp);
                }
                i++;
            }
            temp = new ArrayList<String>();
            // temp.add("</s>");
            temp.add(" ");
            latice.put(i, temp);


            return latice;
        }

        public String getProperSegmentation(final String input) {
            if (DiacritizeText.hPrefixes.isEmpty()) {
                for (int i = 0; i < ArabicUtils.prefixes.length; i++) {
                    DiacritizeText.hPrefixes.put(ArabicUtils.prefixes[i], 1);
                }
            }
            if (DiacritizeText.hSuffixes.isEmpty()) {
                for (int i = 0; i < ArabicUtils.suffixes.length; i++) {
                    DiacritizeText.hSuffixes.put(ArabicUtils.suffixes[i], 1);
                }
            }
            String output = "";
            final String[] word = input.split("\\+");
            String currentPrefix = "";
            String currentSuffix = "";
            int iValidPrefix = -1;
            while (iValidPrefix + 1 < word.length && DiacritizeText.hPrefixes.containsKey(word[iValidPrefix + 1])) {
                iValidPrefix++;
            }

            int iValidSuffix = word.length;

            while (iValidSuffix > Math.max(iValidPrefix, 0) && (DiacritizeText.hSuffixes.containsKey(word[iValidSuffix - 1])
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

            output = currentPrefix + ";" + stemPart + ";" + currentSuffix;
            return output.replace("++", "+");
        }

        public String diacritizeBasedOnTemplate(final ArrayList<String> prefixes, final ArrayList<String> suffixes, final String stem) {
            String prefix = "";
            for (final String s : prefixes) {
                prefix += s;
            }
            prefix = ArabicUtils.utf82buck(prefix);
            String suffix = "";
            for (final String s : suffixes) {
                suffix += s;
            }
            suffix = ArabicUtils.utf82buck(suffix);

            String template = DiacritizeText.farasaSegmenter.getStemTempate(stem);
            if (template.length() == stem.length() - 1 && stem.endsWith("ي")) {
                template += "y";
            } else if (template.length() == stem.length() - 1 && stem.endsWith("ة")) {
                template += "p";
            } else if (template.length() == stem.length() - 2 && stem.endsWith("ية")) {
                template += "yp";
            } else if (template.length() == stem.length() - 2 && stem.endsWith("ات")) {
                template += "At";
            }
            if (!template.equals("Y") && this.bestDiacritizedTemplateFull.containsKey(prefix + template + suffix)) {
                template = this.bestDiacritizedTemplateFull.get(prefix + template + suffix).replaceFirst(" .*", "");
                template = template.substring(prefix.length());
                template = template.substring(0, template.length() - suffix.length());
                final String root = DiacritizeText.farasaSegmenter.getStemRoot(stem); // ft.getRootFitTemplate(stem);

                final int posF = template.indexOf("f");
                final int posE = template.indexOf("E");
                final int posL = template.indexOf("l");
                for (int k = 0; k < root.length(); k++) {
                    if (k == 0 && posF > -1) {
                        template = template.substring(0, posF) + root.charAt(0) + template.substring(posF + 1);
                    } else if (k == 1 && posE > -1) {
                        template = template.substring(0, posE) + root.charAt(1) + template.substring(posE + 1);
                    } else if (k == 2 && posL > -1) {
                        template = template.substring(0, posL) + root.charAt(2) + template.substring(posL + 1);
                    } else {
                        template = template.replace("C", root.substring(k, k + 1));
                    }
                }
                template = template.replace("P", "$").replace("O", "*");
                template = ArabicUtils.buck2utf8(prefix + template + suffix);
                template = template.replaceAll("[" + ArabicUtils.buck2utf8("aiuo") + "]و", "و").replaceAll("[" + ArabicUtils.buck2utf8("aiuo") + "]ي", "ي")
                        .replaceAll("[" + ArabicUtils.buck2utf8("aiuo") + "]ا", "ا");
                return template;
            } else if (!template.equals("Y") && this.bestDiacritizedTemplateStem.containsKey(template)) {
                template = this.bestDiacritizedTemplateStem.get(template).replaceFirst(" .*", "");
                final String root = DiacritizeText.farasaSegmenter.getStemRoot(stem); // ft.getRootFitTemplate(stem);

                final int posF = template.indexOf("f");
                final int posE = template.indexOf("E");
                final int posL = template.indexOf("l");
                for (int k = 0; k < root.length(); k++) {
                    if (k == 0 && posF > -1) {
                        template = template.substring(0, posF) + root.charAt(0) + template.substring(posF + 1);
                    } else if (k == 1 && posE > -1) {
                        template = template.substring(0, posE) + root.charAt(1) + template.substring(posE + 1);
                    } else if (k == 2 && posL > -1) {
                        template = template.substring(0, posL) + root.charAt(2) + template.substring(posL + 1);
                    } else {
                        template = template.replace("C", root.substring(k, k + 1));
                    }
                }
                template = template.replace("P", "$").replace("O", "*");
                template = ArabicUtils.buck2utf8(prefix + template + suffix);
                template = template.replaceAll("[" + ArabicUtils.buck2utf8("aiu") + "]و", "و").replaceAll("[" + ArabicUtils.buck2utf8("aiu") + "]ي", "ي")
                        .replaceAll("[" + ArabicUtils.buck2utf8("aiu") + "]ا", "ا");
                return template;
            } else {
                return "Y";
            }
        }
    }
