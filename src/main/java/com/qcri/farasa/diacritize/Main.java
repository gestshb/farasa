/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qcri.farasa.diacritize;

import com.qcri.farasa.pos.FarasaPOSTagger;
import com.qcri.farasa.segmenter.ArabicUtils;
import com.qcri.farasa.segmenter.Farasa;

import java.io.*;
import java.util.ArrayList;

/**
 * @author kareemdarwish
 */
public class Main {

    public static Farasa farasaSegmenter = null;
    public static FarasaPOSTagger farasaPOSTagger = null;

    public static void main(String[] args) throws Exception {
        int i = 0;
        String arg;
        String infile = "";
        String outfile = "";
        int args_flag = 0; // correct set of arguments

        while (i < args.length) {
            arg = args[i++];
            // 
            if (arg.equals("--help") || arg.equals("-h") || (args.length != 0 && args.length != 4)) {
                System.out.println("Usage: FarasaDiacritize <--help|-h> <[-i|--input] [in-filename]> <[-o|--output] [out-filename]>");
                System.exit(-1);
            }

            if (arg.equals("--input") || arg.equals("-i")) {
                args_flag++;
                infile = args[i];
            }
            if (arg.equals("--output") || arg.equals("-o")) {
                args_flag++;
                outfile = args[i];
            }

        }

//        System.out.println((new Date()).toString());
        System.err.println("Initializing the system ....");
        farasaSegmenter = new Farasa();
//        System.out.println((new Date()).toString());
        farasaPOSTagger = new FarasaPOSTagger(farasaSegmenter);

//        System.out.println((new Date()).toString());
        DiacritizeText dt = new DiacritizeText(farasaSegmenter, farasaPOSTagger);


//        System.out.println((new Date()).toString());
        System.err.print("\r");
        System.err.println("System ready!               ");
        if (args_flag == 0) {
            processFile(dt);
        } else {
            processFile(infile, outfile, dt);
        }
        //System.out.println((new Date()).toString());
    }

    private static void processFile(DiacritizeText tagger) throws Exception {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
        processBuffer(br, bw, tagger);
        br.close();
        bw.close();
    }

    private static void processFile(String filename, String outfilename, DiacritizeText tagger) throws Exception {
        BufferedReader br = openFileForReading(filename);
        BufferedWriter bw = openFileForWriting(outfilename);
        processBuffer(br, bw, tagger);
        //processBufferEval(br,bw,tagger);
        br.close();
        bw.close();
    }

    private static void processBuffer(BufferedReader br, BufferedWriter bw, DiacritizeText tagger) throws Exception {

        String line = "";
        while ((line = br.readLine()) != null) {
            bw.write(tagger.diacritize(line, true) + "\n");
            bw.flush();
        }
    }

    private static void processBufferEval(BufferedReader br, BufferedWriter bw, DiacritizeText tagger) throws Exception {
        boolean stemBased = true;
        String line = "";
        double all = 0d;
        double error = 0d;
        while ((line = br.readLine()) != null) {
            ArrayList<String> tmpWords = ArabicUtils.tokenizeWithoutProcessing(line);
            String tmpLine = "";
            for (String w : tmpWords)
                tmpLine += w + " ";
            String[] words = tmpLine.trim().split(" +");
            String diacritizedLine = tagger.diacritize(line);
            String[] diacritizedWords = diacritizedLine.trim().split(" +");
            for (int i = 0; i < Math.min(words.length, diacritizedWords.length); i++) {
                if (!stemBased) {
                    if (!AuxFunctions.removeDefaultDiac(AuxFunctions.normalizeDiac(DiacritizeText.standardizeDiacritics(tagger.removeCaseEnding(words[i])))).trim()
                            .equals(AuxFunctions.removeDefaultDiac(AuxFunctions.normalizeDiac(DiacritizeText.standardizeDiacritics(tagger.removeCaseEnding(diacritizedWords[i])))).trim())) {
                        System.err.println(DiacritizeText.standardizeDiacritics(words[i]) + "\tAA" + DiacritizeText.standardizeDiacritics(diacritizedWords[i]));
                        error++;
                    }
                } else {
                    if (!AuxFunctions.removeDefaultDiac(AuxFunctions.normalizeDiac(DiacritizeText.standardizeDiacritics(words[i]))).trim().equals(AuxFunctions.removeDefaultDiac(AuxFunctions.normalizeDiac(DiacritizeText.standardizeDiacritics(diacritizedWords[i]))).trim())) {
                        System.err.println(DiacritizeText.standardizeDiacritics(words[i]) + "\tBB" + DiacritizeText.standardizeDiacritics(diacritizedWords[i]));
                        error++;
                    }

                }
                all++;
            }
            bw.write(diacritizedLine + "\n");
            System.err.println(error / (0.01 * all));
        }
    }

    public static BufferedReader openFileForReading(String filename) throws FileNotFoundException {
        BufferedReader sr = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filename))));
        return sr;
    }

    public static BufferedWriter openFileForWriting(String filename) throws FileNotFoundException {
        BufferedWriter sw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(filename))));
        return sw;
    }

}