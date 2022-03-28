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
public class Clitic {
    public String surface = "";
    public String template = "";
    public ArrayList<String> possiblePOS = null;
    public String truthPOS = "";
    public String position = "";
    public String guessPOS = "";
    public String genderNumber = ""; // Gender: F/M, Number: S/D/P -- ex.: FS
    public String det = "n";     //does it have a determiner 
    public String isStem = "n";  // is it a stem of the word form

    public Clitic(String s, String t, ArrayList<String> possP, String truth, String BIO, String dummyGuess) {
        surface = s;
        possiblePOS = possP;
        template = t;
        truthPOS = truth;
        position = BIO; // beginning "B", middle "I"
        guessPOS = dummyGuess;
    }

    public void setGuessPOS(String guessPOS) {
        this.guessPOS = guessPOS;
    }
}
