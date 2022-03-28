/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qcri.farasa.pos;

import java.util.ArrayList;

/**
 *
 * @author kareemdarwish
 */
public class Sentence {
    public ArrayList<Clitic> clitics = null;
    private final Clitic seperator = new Clitic("§§", "§", new ArrayList<String>(), "§", "B", "§");

    public Sentence() {
        clitics = new ArrayList<Clitic>();
    }
    
    public void addClitic(Clitic c)
    {
        if (clitics.size() == 0)
            clitics.add(new Clitic("S", "S", new ArrayList<String>(), "S", "B", "S"));
        clitics.add(c);
    }
    
    public void addWord(Word w)
    {
//        if (clitics.size() > 0)
//            clitics.add(seperator);
//        else
        if (clitics.size() == 0)
            clitics.add(new Clitic("S", "S", new ArrayList<String>(), "S", "B", "S"));
        for (Clitic c : w.clitics)
            clitics.add(c);
    }
    
    public void clear()
    {
        clitics = new ArrayList<Clitic>();
    }
}
