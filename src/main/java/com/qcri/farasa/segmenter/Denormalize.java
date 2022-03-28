/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qcri.farasa.segmenter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

/**
 *
 * @author Kareem
 */
public class Denormalize {
    HashMap<String, String> corrections = new HashMap<String, String>();

    public Denormalize() throws IOException {
        BufferedReader br = ArabicUtils.openFileForReading("C:\\RESEARCH\\FromMac\\RESEARCH\\FARASA\\AutoCorrect\\aj.lm.words.uni.trim2");
        String line = "";
        while ((line = br.readLine()) != null)
        {
            String[] parts = line.trim().split("\t");
            if (parts.length == 2)
            {
                if (parts[0].trim().matches("^[" + ArabicUtils.AllArabicLetters + "]+$"))
                {
                    String[] choices = parts[1].trim().split(" +");
                    // get totals
                    double totals = 0;
                    for (String c : choices)
                    {
                        totals += Double.parseDouble(c.substring(c.indexOf("_") + 1));
                    }
                    for (String c : choices)
                    {
                        double score = Double.parseDouble(c.substring(c.indexOf("_") + 1)) / totals;
                        if (score > 0.90)
                            corrections.put(parts[0].trim(), parts[1].trim().substring(0, parts[1].indexOf("_")));
                    }
                }
            }
        }
        serializeMap("C:\\RESEARCH\\FromMac\\RESEARCH\\FARASA\\AutoCorrect\\", "corrections", corrections);
    }
    
    public String correctWord(String s)
    {
        if (corrections.containsKey(ArabicUtils.normalizeFull(s)))
            return corrections.get(ArabicUtils.normalizeFull(s));
        else
            return s;
    }
    
    public void serializeMap(String BinDir, String MapName, HashMap input) throws IOException {
        FileOutputStream fos
                = new FileOutputStream(BinDir + "NTBdata." + MapName + ".ser");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(input);
        oos.close();
        fos.close();
    }
    
    public HashMap deserializeMap(String MapName) throws IOException, ClassNotFoundException
    {
         ObjectInputStream ois = new ObjectInputStream(resolveName("/FarasaData/NTBdata." + MapName + ".ser"));
         HashMap map = (HashMap) ois.readObject();
         ois.close();
         return map;
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
}
