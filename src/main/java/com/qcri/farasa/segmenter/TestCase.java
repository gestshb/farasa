/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qcri.farasa.segmenter;

import static com.qcri.farasa.segmenter.ArabicUtils.buck2utf8;
import static com.qcri.farasa.segmenter.ArabicUtils.normalizeFull;
import static com.qcri.farasa.segmenter.ArabicUtils.normalizeatb;
import static com.qcri.farasa.segmenter.ArabicUtils.removeDiacritics;
import static com.qcri.farasa.segmenter.ArabicUtils.tokenize;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.List;

/**
 *
 * @author kareemdarwish
 */
public class TestCase
{
	private enum schemes{q1, q0, atb, def}

    public static void main(String[] args) throws Exception
    {

	int i = 0;
	String arg;
	String infile = "";
	String outfile = "";
	String scheme = "def";
	Boolean norm = false; // no normalization Default
    Boolean lemma = false;
	int args_flag = 0; // correct set of arguments

	while (i < args.length)
	{
	    arg = args[i++];
	    // 
	    if (arg.equals("--help") || arg.equals("-h") || (args.length != 0 && args.length != 2 && args.length != 4 && args.length != 6 && args.length != 8))
	    {
		System.out.println("Usage: Farasa <--help|-h> <[-c|--scheme] atb> <[-n|--norm] true|false> <[-l|--lemma] true|false> <[-i|--input] [in-filename]> <[-o|--output] [out-filename]>");
		System.exit(-1);
	    }

	    if (arg.equals("--input") || arg.equals("-i"))
	    {
		args_flag++;
		infile = args[i];   
	    }
	    if (arg.equals("--output") || arg.equals("-o"))
	    {
		args_flag++;
		outfile = args[i];
	    }
	    if (arg.equals("--scheme") || arg.equals("-c"))
	    {
		args_flag++;
		scheme = args[i];
		//System.out.println("Scheme Value:\""+scheme+"\"");
	    }
	    if (arg.equals("--norm") || arg.equals("-n"))
	    {
		args_flag++;
		if (args[i].equalsIgnoreCase("true"))
		{
		    norm = true;
		}
		//System.out.println("Scheme Value:\""+normalization+"\"");
	    }
	    if (arg.equals("--lemma") || arg.equals("-l"))
	    {
		args_flag++;
		lemma = true;
	    }
	}

	System.err.print("Initializing the system ....");

	Farasa nbt = new Farasa();
        // nbt.removeNLLFromSeenSegmentations();
	//processFile("/work/test.txt", nbt);

	System.err.print("\r");
	System.err.println("System ready!               ");
	if (args_flag == 0)
	{
	    processFile(nbt, scheme, norm);
	}
	else
	{
            if (!lemma)
            {
                processFile(infile, outfile, nbt, scheme, norm);
            }
            else
            {
                lemmatizeFile(infile, outfile, nbt, scheme, norm);
            }
	}
    }

    private static void processFile(Farasa nbt, String sch_str, boolean norm) throws IOException
    {

	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
	processBuffer(br, bw, nbt, sch_str, norm);
    }

    private static void processFile(String filename, String outfilename, Farasa nbt, String sch_str, boolean norm) throws IOException
    {
	BufferedReader br;//= openFileForReading(filename);
	BufferedWriter bw; //= openFileForWriting(outfilename);
	if (!filename.equals(""))
	{
	    br = openFileForReading(filename);
	}
	else
	{
	    br = new BufferedReader(new InputStreamReader(System.in));
	}

	if (!outfilename.equals(""))
	{
	    bw = openFileForWriting(outfilename);
	}
	else
	{
	    bw = new BufferedWriter(new OutputStreamWriter(System.out));
	}

	processBuffer(br, bw, nbt, sch_str, norm);
    }

    private static void processBuffer(BufferedReader br, BufferedWriter bw, Farasa nbt, String sch_str, boolean norm) throws IOException
    {

	String line = "";
	String topSolution;
	schemes sch;
	// HashMap<String, String> seenBefore = new HashMap<String, String>();
	try {
            sch = schemes.valueOf(sch_str);
    } catch (IllegalArgumentException e) {
            sch = schemes.valueOf("def");
    }
	
	//System.err.println("\nNorm:"+norm+"\tScheme:"+sch);
   	
   	if(sch_str.equals("q1"))
   		norm = false;

	while ((line = br.readLine()) != null)
	{
	 	//ArrayList<String> words = tokenize(removeDiacritics(line));
	    // normalize Farsi letter
	    line = ArabicUtils.replaceFarsiCharacters(line);
	    ArrayList<String> words = tokenize(line);
	    for (String w : words)
	    {	
	    	//System.err.println("\nWord:"+w);
			if (!nbt.hmSeenBefore.containsKey(w))
			{
			    TreeMap<Double, String> solutions = nbt.mostLikelyPartition(w, 1); //buck2utf8(w),1 ??
			    topSolution = w;
			    if (solutions.size() > 0)
			    {
				topSolution = solutions.get(solutions.firstKey());
			    }

			    topSolution = topSolution.replace(";", "").replace("++", "+").replace("(نلل)", "");
			    
			    nbt.hmSeenBefore.put(w, topSolution);

			    		//System.err.println("\nScheme: atb\t"+nbt.hmSeenBefore.get(w)); 
		                switch(sch) {
		                    case atb: topSolution = produceSpecialSegmentation_atb(nbt.hmSeenBefore.get(w).replace(";", "").replace("++", "+"), nbt); break;
		                    case q0 : topSolution = produceSpecialSegmentation_q0(nbt.hmSeenBefore.get(w).replace(";", "").replace("++", "+"), nbt,norm); break;
		                    case q1 : topSolution = produceSpecialSegmentation_atb(nbt.hmSeenBefore.get(w).replace(";", "").replace("++", "+"), nbt); topSolution = normalizeatb(topSolution); break;
		                    default   : topSolution = nbt.hmSeenBefore.get(w).replace(";", "").replace("++", "+");
		                }
		                if (norm)
							topSolution = normalizeFull(topSolution);
		                
			    nbt.hmSeenBefore.put(w, topSolution);
			    bw.write(topSolution.replace(";", "").replace("++", "+") + " ");
			    bw.flush();

			}
			else
			{
		                switch(sch) {
		                    case atb: topSolution = produceSpecialSegmentation_atb(nbt.hmSeenBefore.get(w).replace(";", "").replace("++", "+"), nbt); break;
		                    case q0 : topSolution = produceSpecialSegmentation_q0(nbt.hmSeenBefore.get(w).replace(";", "").replace("++", "+"), nbt,norm); break;
		                    case q1 : topSolution = produceSpecialSegmentation_atb(nbt.hmSeenBefore.get(w).replace(";", "").replace("++", "+"), nbt); topSolution = normalizeatb(topSolution); break;
		                    default   : topSolution = nbt.hmSeenBefore.get(w).replace(";", "").replace("++", "+");
		                }

		                topSolution = topSolution.replace("(نلل)", "");

		                if (norm)
							topSolution = normalizeFull(topSolution);

		                bw.write(topSolution + " ");
                        bw.flush();
			}
	    }
	    bw.write("\n");
        bw.flush();
	}
	bw.close();
    }

    private static void lemmatizeFile(String filename, String outfilename, Farasa nbt, String sch_str, boolean norm) throws IOException
    {
	BufferedReader br;//= openFileForReading(filename);
	BufferedWriter bw; //= openFileForWriting(outfilename);
	if (!filename.equals(""))
	{
	    br = openFileForReading(filename);
	}
	else
	{
	    br = new BufferedReader(new InputStreamReader(System.in));
	}

	if (!outfilename.equals(""))
	{
	    bw = openFileForWriting(outfilename);
	}
	else
	{
	    bw = new BufferedWriter(new OutputStreamWriter(System.out));
	}

	lemmatizeBuffer(br, bw, nbt, sch_str, norm);
    }

    private static void lemmatizeBuffer(BufferedReader br, BufferedWriter bw, Farasa nbt, String sch_str, boolean norm) throws IOException
    {
        int i, firstSuffixIndex;
        boolean lemmaFound, emptyLemmas;
        ArrayList<String> lemmas = new ArrayList<String>();
        String line, diacTokPOSLemma, diacTokPOSLemma2, uniqueUndiacLemmas, topSolution, stem, stem2, lastPrefix, firstSuffix, lineLemmas;
        String[] wordInfo, lemmaList, segments;
	// HashMap<String, String> seenBefore = new HashMap<String, String>();

	while ((line = br.readLine()) != null)
	{
	    // normalize Farsi letter
	    line = ArabicUtils.replaceFarsiCharacters(line);
	    ArrayList<String> words = tokenize(removeDiacritics(line));
            
            lineLemmas = "";
            
            for (String w : words)
	    {
                //lineLemmas += String.format("%s:", w);
                
                // Format of the file: Word	Diac	Tokenization	POS	Lemma	UniqueUndiacLemma
                diacTokPOSLemma = Farasa.hmWordDiacTokPOSLemma.get(w);

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
                            lineLemmas += String.format("%s ", lemmaList[0]);
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

                    if (!nbt.hmSeenBefore.containsKey(w))
                    {
                        TreeMap<Double, String> solutions = nbt.mostLikelyPartition(w, 1);
                        topSolution = w;
                        if (solutions.size() > 0)
                        {
                            topSolution = solutions.get(solutions.firstKey());
                        }

                        topSolution = topSolution.replace(";", "").replace("++", "+");
                        nbt.hmSeenBefore.put(w, topSolution);

                        //if (norm)
                        //{
                        //    topSolution = normalizeFull(topSolution);
                        //}

                        nbt.hmSeenBefore.put(w, topSolution);

                        //bw.write(topSolution.replace(";", "").replace("++", "+") + " ");
                        //bw.flush();
                    }
                    else
                    {
                        topSolution = nbt.hmSeenBefore.get(w).replace(";", "").replace("++", "+");
                        //if (norm)
                        //{
                        //    topSolution = normalizeFull(topSolution);
                        //}

                        //bw.write(topSolution + " ");
                    }
                    
                    segments = topSolution.split("\\+");

                    if (segments.length == 1)
                    {
                        lineLemmas += String.format("%s ", segments[0]);
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
                                diacTokPOSLemma2 = Farasa.hmWordDiacTokPOSLemma.get(stem2);
                            }

                            if (diacTokPOSLemma2 == null)
                            {
                                stem2 = String.format("%s%s", stem, firstSuffix);

                                diacTokPOSLemma2 = Farasa.hmWordDiacTokPOSLemma.get(stem2);
                            }

                            if (diacTokPOSLemma2 != null)
                            {
                                diacTokPOSLemma = diacTokPOSLemma2;
                                stem = stem2;
                            }
                        
                        }
                        
                        if (diacTokPOSLemma == null)
                        {
                            diacTokPOSLemma = Farasa.hmWordDiacTokPOSLemma.get(stem);
                        }
                        
                        if (diacTokPOSLemma == null)
                        {
                            if (stem.endsWith("ؤ") || stem.endsWith("ئ"))
                            {
                                stem2 = stem.substring(0, stem.length() - 1);
                                stem2 += "ء";
                                
                                diacTokPOSLemma2 = Farasa.hmWordDiacTokPOSLemma.get(stem2);
                                
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
                                    lineLemmas += String.format("%s ", lemmaList[0]);
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
                            lineLemmas += String.format("%s ", stem);
                        }
                    }                    
                }
	    }

            lineLemmas = lineLemmas.trim();
            
            if (norm)
            {
                lineLemmas = normalizeFull(lineLemmas);
            }
            lineLemmas += "\n";
            bw.write(lineLemmas);
            bw.flush();
	}

        bw.close();
    }

    public static String produceSpecialSegmentation(String segmentedWord, Farasa nbt, boolean norm)
    {
    	String output = "";

    	String tmp = nbt.getProperSegmentation(segmentedWord);

		// attach Al to the word
    	tmp = tmp.replace("ال+;", ";ال").replace("(نلل)", "");

		// attach ta marbouta
    	tmp = tmp.replace(";+ة", "ة;");

		// normalize output        
    	if (norm)
    	{
    		tmp = normalizeFull(tmp);
    	}

		// concat all prefixes and all suffixes
    	String[] parts = (" " + tmp + " ").split(";");

		// handle prefix
    	tmp = parts[0].replace("+", "").trim();
    	if (tmp.length() > 0)
    	{
    		output += tmp + "+ ";
    	}

		// handle stem
    	output += parts[1].trim();

		// handle suffix
    	tmp = parts[2].replace("+", "").trim();
    	if (tmp.length() > 0)
    	{
    		output += " +" + tmp;
    	}

    	output = output.trim();
    	while (output.startsWith("+"))
    	{
    		output = output.substring(1);
    	}
    	while (output.endsWith("+"))
    	{
    		output = output.substring(0, output.length() - 1);
    	}

    	return output;
    }

    public static String produceSpecialSegmentation_q0(String segmentedWord, Farasa nbt, boolean norm)
    {
        String output = "";

        String tmp = nbt.getProperSegmentation(segmentedWord);

        // attach Al to the word
        tmp = tmp.replace("ال+;", ";ال").replace("(نلل)", "");

        // attach ta marbouta
        tmp = tmp.replace(";+ة", "ة;");

        // normalize output        
        if (norm)
        {
            tmp = normalizeFull(tmp);
        }

        // concat all prefixes and all suffixes
        String[] parts = (" " + tmp + " ").split(";");

        // handle prefix
        tmp = parts[0].replace("+", "").trim();
        if (tmp.length() > 0)
        {
            output += tmp + "+ ";
        }

        // handle stem
        output += parts[1].trim();

        // handle suffix
        tmp = parts[2].replace("+", "").trim();
        if (tmp.length() > 0)
        {
            output += " +" + tmp;
        }

        output = output.trim();
        while (output.startsWith("+"))
        {
            output = output.substring(1);
        }
        while (output.endsWith("+"))
        {
            output = output.substring(0, output.length() - 1);
        }

        return output;
    }


    public static String produceSpecialSegmentation_atb(String segmentedWord, Farasa nbt)
    {
    	String output = "";

    	String tmp = nbt.getProperSegmentation(segmentedWord);

    	tmp = tmp.replace("(نلل)", "");
        //System.out.println("Sgs:"+tmp);
		// attach Al to the word
    	tmp = tmp.replace("ال+;", ";ال");

		// attach ta marbouta
    	tmp = tmp.replace(";+ة", "ة;");
		// attach feminine plural marker
    	tmp = tmp.replace(";+ات", "ات;");
		// attach 1 person/2person marker
    	tmp = tmp.replace(";+ت", "ت;");
		// attach feminine plural marker
    	tmp = tmp.replace(";+ون", "ون;");
		// attach feminine plural marker
    	tmp = tmp.replace(";+ين", "ين;");
        // attach dual marker
    	tmp = tmp.replace(";+ان", "ان;");
        // attach verb plural marker
    	tmp = tmp.replace(";+وا", "وا;");
        // tmp = tmp.replace(";+و+", "وا;");
		// attach tanweet fatha case
    	tmp = tmp.replace(";+ا", "ا;");

		// concat all prefixes and all suffixes
    	String[] parts = (" " + tmp + " ").split(";");

		// handle prefix
    	tmp = parts[0].replace("+", "").trim();
    	if (tmp.length() > 0)
    	{
    		output += tmp + "+ ";
    	}

		// handle stem
    	output += parts[1].trim();

		// handle suffix
    	tmp = parts[2].replace("+", "").trim();
    	if (tmp.length() > 0)
    	{
    		output += " +" + tmp;
    	}

    	output = output.trim();
    	while (output.startsWith("+"))
    	{
    		output = output.substring(1);
    	}
    	while (output.endsWith("+"))
    	{
    		output = output.substring(0, output.length() - 1);
    	}

    	return output;
    }

    public static BufferedReader openFileForReading(String filename) throws FileNotFoundException
    {
	BufferedReader sr = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filename))));
	return sr;
    }

    public static BufferedWriter openFileForWriting(String filename) throws FileNotFoundException
    {
	BufferedWriter sw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(filename))));
	return sw;
    }
}
