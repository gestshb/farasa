/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qcri.farasa.pos;

import static com.qcri.farasa.pos.FarasaPOSTagger.hmPos;
import static com.qcri.farasa.pos.FarasaPOSTagger.hmPosGivenWord;
import static com.qcri.farasa.pos.FarasaPOSTagger.hmPosNormal;
import static com.qcri.farasa.pos.FarasaPOSTagger.hmWord;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author kareemdarwish
 */
public class Word {
    public ArrayList<Clitic> clitics = null;
    public Word()
    {
        clitics = new ArrayList<Clitic>();
    }
    
    public void add(Clitic c)
    {
        clitics.add(c);
    }
}
