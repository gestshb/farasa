/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qcri.farasa.pos;

import static com.qcri.farasa.pos.FarasaPOSTagger.binDir;
import com.qcri.farasa.segmenter.ArabicUtils;
import com.qcri.farasa.segmenter.Farasa;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 *
 * @author kareemdarwish
 */
public class FarasaPOS {

    /**
     * @param args the command line arguments
     */

    public static Farasa farasaSegmenter = null;
    public static FarasaPOSTagger farasaPOSTagger = null;

    public static void main(String[] args) throws Exception {

        
        int i=0;
        String arg;
        String infile="";
        String outfile="";
        int args_flag = 0; // correct set of arguments
                        
        while (i < args.length) {
            arg = args[i++];
            // 
            if (arg.equals("--help") || arg.equals("-h") || (args.length!=0 && args.length!=4)) {
                System.out.println("Usage: FarasaPOS <--help|-h> <[-i|--input] [in-filename]> <[-o|--output] [out-filename]>");
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

        System.err.print("Initializing the system ....");

        farasaSegmenter = new Farasa();
        farasaPOSTagger = new FarasaPOSTagger(farasaSegmenter);

        System.err.print("\r");
        System.err.println("System ready!               ");
        if(args_flag==0) {
           processFile(farasaPOSTagger, farasaSegmenter);
        }else {
           processFile(infile, outfile, farasaPOSTagger, farasaSegmenter);
        }
    }   
  
    private static void processFile(FarasaPOSTagger tagger, Farasa farasa) throws Exception {
         
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
        processBuffer(br,bw,tagger, farasa);
        br.close();
        bw.close();
     }
     
    private static void processFile(String filename, String outfilename, FarasaPOSTagger tagger, Farasa farasa) throws Exception {
        BufferedReader br = openFileForReading(filename);
        BufferedWriter bw = openFileForWriting(outfilename);
        processBuffer(br,bw,tagger,farasa);
        br.close();
        bw.close();
    }
     
    private static void processBuffer(BufferedReader br, BufferedWriter bw, FarasaPOSTagger tagger, Farasa farasa) throws Exception {
 
        String line = "";
        while ((line = br.readLine()) != null)
        {
            //Sentence s = tagger.tagLine(tokenize(line));
            Sentence s = tagger.tagLine(farasa.segmentLine(line));
            for (Clitic w : s.clitics) {
                //(((w.position.equals("I"))?"#":""))+
                bw.write(w.surface + "/" + w.guessPOS + ((w.genderNumber!="")?"-"+w.genderNumber:"")+" ");
            }
            bw.write("\n");
            bw.flush();
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
    
    public static void fixTrainingFile(String filename) throws IOException
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filename)), StandardCharsets.UTF_8));
        BufferedWriter bw = ArabicUtils.openFileForWriting(filename + ".correct");
        
        ArrayList<String> lines = new ArrayList<String>();
        
        String line = "";
        while ((line = br.readLine()) != null)
        {
            lines.add(line);
        }
        
        for (int i = 0; i < lines.size(); i++)
        {
            line = lines.get(i).trim();
            if (line.equals("ـــ	Y	NOT	O"))
            {
                boolean print = true;
                // check if next line has 
                if (i + 1 < lines.size() && lines.get(i + 1).trim().matches("(ها|ه|ك|ا|ي|ني|نا)\tY\tNOT\tPRON"))
                    print = false;
                else if (i > 0 && lines.get(i - 1).trim().matches("[لكفوب]\tY\tNOT\t(PREP|CONJ)") && !lines.get(i+1).trim().contains("PUNC") && !lines.get(i+1).trim().contains("NUM")
                        && !lines.get(i+1).trim().contains("ABBREV"))
                    print = false;
                else if (i > 0 && lines.get(i - 1).trim().matches(".*\tY\tNOT\tNSUFF") && lines.get(i + 1).trim().matches("(ها|ه|ك|ا|ي|ني|نا|هم|كم|كن|كما|هما)\tY\tNOT\tPRON") 
                        && !lines.get(i-1).trim().contains("ة") && !lines.get(i-1).trim().startsWith("تان\t"))
                    print = false;
                else if (i > 0 && lines.get(i-1).trim().equals("ت\tY\tNOT\tNSUFF"))
                    print = false;
                
                if (print)
                    bw.write(line + "\n");
            }
            else
            {
                bw.write(line + "\n");
            }
        }
        bw.close();
    }
    
}
