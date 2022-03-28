package com.qcri.farasa.normalize;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import com.qcri.farasa.diacritize.DiacritizeText;


public class ImprovedDiacritic {


	public static void main(String[] args) throws Exception {
		System.err.println("Initializing the system ....");
		DiacritizeText diacritizeText = DiacritizeText.getInstance();
		System.err.println("System ready ...");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
		String line = "";
		while ((line = br.readLine()) != null) {
			bw.write(diacritizeText.diacritize(line, true) + "\n");
			bw.flush();
		}
		br.close();
		bw.close();

	}
	public ImprovedDiacritic() throws Exception {
		diacritizeText = DiacritizeText.getInstance();
	}

	private DiacritizeText diacritizeText;


	/**
	 * main method
	 * @param text
	 * @return
	 * @throws Exception
	 */
	public String getArabicWord(String text) throws Exception {

		try {
			return diacritizeText.diacritize(processOfDiacritics(text));
		} catch (Exception e) {
			return null;
		}

	}


	public String processOfDiacritics(String text)
			throws FileNotFoundException, InterruptedException, Exception, IOException {
		String line = "";
		line = Normaliz(text);
		line = diacritizeText.diacritize(line, true);
		line = AllUpDate(line);
		return line;
	}

	/*
	 * -----------------------------------------------------------------------------
	 * -- Name: AllUpDate Purpose: function to call all function up date diacritcs
	 */
	private String AllUpDate(String text) throws FileNotFoundException, InterruptedException, Exception, IOException {

		if (text.trim().isEmpty()) {
			return "";
		}

		String line = "";
		line = DiacriticsTanwin(text);
		line = DiacriticsShadah(line);
		line = UpDateHamzah(line);
		line = UpDateDii(line);
		line = UpDate(line);
		line = UpDateDiacriticsAWE(line);
		return line;
	}


	private String Normaliz(String Dtext) {
		String Text = Dtext;
		Text = Text.replaceAll("١", "1");
		Text = Text.replaceAll("٢", "2");
		Text = Text.replaceAll("\\s١\\s", "واحد");
		Text = Text.replaceAll("\\s٢\\s", "اثنين");
		Text = Text.replaceAll("\\s1\\s", "واحد");
		Text = Text.replaceAll("\\s2\\s", "اثنين");
		Text = Text.replaceAll("٣", "3");
		Text = Text.replaceAll("٤", "4");
		Text = Text.replaceAll("٥", "5");
		Text = Text.replaceAll("٦", "6");
		Text = Text.replaceAll("٧", "7");
		Text = Text.replaceAll("٨", "8");
		Text = Text.replaceAll("٩", "9");
		Text = Text.replaceAll("٠", "0");
		Text = Text.replaceAll("ااا*", "ا");
		Text = Text.replaceAll("ببب*", "ب");
		Text = Text.replaceAll("تتت*", "ت");
		Text = Text.replaceAll("ههه*", "ه");
		Text = Text.replaceAll("ثثث*", "ث");
		Text = Text.replaceAll("ججج*", "ج");
		Text = Text.replaceAll("ححح*", "ح");
		Text = Text.replaceAll("خخخ*", "خ");
		Text = Text.replaceAll("ددد*", "د");
		Text = Text.replaceAll("ررر*", "ر");
		Text = Text.replaceAll("ككك*", "ك");
		Text = Text.replaceAll("\\sي\\s", " يا ");
		Text = Text.replaceAll("\\sف\\s", " في ");
		Text = Text.replaceAll("\\sع\\s", " على ");
		Text = Text.replaceAll("\\s\\s\\s*", " ");
		Text = Text.replaceAll("\\n\\n*", "\\n");
		Text = Text.replaceAll("[a-zA-Z]", "");
		String spliit[] = Text.split("\\s", 0);
		String word;
		for (int i = 0; i < spliit.length; i++) {
			word = spliit[i];
			if (word.endsWith("ه")) {
				word = word.substring(0, word.length() - 1) + "ة";

			}

			if (i == 0) {

				StringBuilder sb = new StringBuilder(word);

				if (isProbablyArabicChar(word.charAt(1))) {

					sb.insert(1, "ْ");

				} else {
					sb.replace(1, 2, "ْ");
				}
				word = sb.toString();
			}

			boolean tt = word.matches("-?\\d+(\\.\\d+)?");

			spliit[i] = word;
		}
		Text = String.join(" ", spliit);
		return Text;
	}

	private boolean isProbablyArabicChar(char s) {
		int c = s;
		if (c >= 0x0600 && c <= 0x064A)
			return true;

		if (c >= 0x066E && c <= 0x0D5)
			return true;

		return false;
	}


	private String DiacriticsTanwin(String Dtext) {
		String Text = Dtext;
		String spliteText[];
		String TextUpDate = " ";
		String word = "";
		spliteText = Text.split("\\s", 0);
		for (int i = 0; i < spliteText.length; i++) {
			word = spliteText[i];
			char[] chars = word.toCharArray();
			for (int j = 0; j < chars.length; j++) {
				if (word.charAt(j) == 'ً' && !(j == word.length() - 1)) {
					if (word.charAt(j) == 'ً' && word.charAt(j + 1) == 'ا') {
						chars[j] = 'ا';
						chars[j + 1] = 'ً';
					}
				}
			}
			word = String.valueOf(chars);
			spliteText[i] = word;
		}
		TextUpDate = String.join(" ", spliteText);
		return TextUpDate;
	}


	private String DiacriticsShadah(String Dtext) {
		String Text = Dtext;
		String spliteText[];
		String TextUpDate = " ";
		String word = "";
		spliteText = Text.split("\\s", 0);
		for (int i = 0; i < spliteText.length; i++) {
			word = spliteText[i];
			int lastchar = word.length() - 1;
			if (word.charAt(lastchar) == 'ّ') {
				word = word.substring(0, lastchar);
			}
			spliteText[i] = word;
		}
		TextUpDate = String.join(" ", spliteText);
		return TextUpDate;
	}

	/*
	 * -----------------------------------------------------------------------------
	 * -- Name: DiacriticsShadah Purpose: replace tanwin to add in last letter in
	 * word Author: Rawan Alsaaran (r.alsaaran@gmail.com) Copyright: (c) Rawan
	 * Alsaaran & Imam Mohammed Ibn Saud Islamic university 2017
	 * -----------------------------------------------------------------------------
	 * --
	 */
	private String UpDateHamzah(String DiText)
			throws FileNotFoundException, InterruptedException, Exception, IOException {
		String Text = DiText;
		String spliteText[];
		String TextUpDate = " ";
		String word = "";
		int Shadah = 0;
		int shadah = 0;
		int Hamzah = 0;
		spliteText = Text.split("\\s", 0);
		for (int i = 0; i < spliteText.length; i++) {
			word = spliteText[i];
			char[] chars = word.toCharArray();
			for (int j = 0; j < word.length(); j++) {
				if (Character.isLetter(word.charAt(j)) && Shadah < 3) {
					Shadah++;
					if (Shadah == 3) {
						shadah = j;
					}
				}
			}
			int length = word.length();
			char t[] = new char[length];
			if (shadah + 1 < word.length()) {
				if (word.charAt(shadah + 1) == 'ّ' && word.charAt(Hamzah) == 'أ') {
					if (Character.isLetter(word.charAt(1)) || Character.isDigit(word.charAt(1))) {
						if (Character.isLetter(word.charAt(1))) {
							t[0] = 'أ';
							t[1] = 'ُ';
							for (int j = 2; j < t.length; j++) {
								t[j] = word.charAt(j - 1);
							}
							word = String.valueOf(t);
						}
					} else {
						chars[1] = 'ُ';
						word = String.valueOf(chars);
					}
				}
			}
			spliteText[i] = word;
		}
		TextUpDate = String.join(" ", spliteText);
		return TextUpDate;
	}

	/*
	 * -----------------------------------------------------------------------------
	 * -- Name: UpDate Purpose: Add fatha in first letter in first word for each
	 * sentens also add sqone in last letter for each word Author: Rawan Alsaaran
	 * (r.alsaaran@gmail.com) Copyright: (c) Rawan Alsaaran & Imam Mohammed Ibn Saud
	 * Islamic university 2017
	 * -----------------------------------------------------------------------------
	 * --
	 */
	private String UpDate(String DiText) throws FileNotFoundException, InterruptedException, Exception, IOException {
		String Text = DiText;
		String spliteText[];
		String TextUpDate = " ";
		String word = "";
		int fatha = 1;
		int squen = 0;
		spliteText = Text.split("\\s", 0);
		for (int i = 0; i < spliteText.length; i++) {
			word = spliteText[i];
			char[] chars = word.toCharArray();
			squen = word.length() - 1;
			if (i == 0) {
				if (fatha < word.length()) {
					if (Character.isLetter(word.charAt(fatha)) || Character.isDigit(word.charAt(fatha))) {
						if (Character.isLetter(word.charAt(fatha))) {
							char t[] = new char[squen + 2];
							if (word.charAt(0) != 'ب' && word.charAt(0) != 'ل' && word.charAt(0) != 'أ') {
								t[0] = word.charAt(0);
								t[fatha] = 'َ';
								for (int j = 2; j < t.length; j++) {
									t[j] = word.charAt(j - 1);
								}
								word = String.valueOf(t);
							}
							spliteText[i] = word;
						}
					} else {
						if (word.charAt(0) != 'ب' && word.charAt(0) != 'ل' && word.charAt(0) != 'أ') {
							chars[fatha] = 'َ';
							word = String.valueOf(chars);
						}
						spliteText[i] = word;
					}
				}
			}

			squen = word.length() - 1;
			chars = word.toCharArray();
			if (Character.isLetter(word.charAt(squen)) || Character.isDigit(word.charAt(squen))) {
				if (Character.isLetter(word.charAt(squen))) {
					if (word.charAt(squen) == 'ا' || word.charAt(squen) == 'ي' || word.charAt(squen) == 'و'
							|| word.charAt(squen) == 'ء' || word.charAt(squen) == 'ى') {
						spliteText[i] = word;
					} else {
						word = word.concat("ْ");
						spliteText[i] = word;
					}
				}
			} else {
				if (squen - 1 > 0) {
					if (word.charAt(squen - 1) == 'ا' || word.charAt(squen - 1) == 'ي' || word.charAt(squen - 1) == 'و'
							|| word.charAt(squen - 1) == 'ء' || word.charAt(squen - 1) == 'ى') {
						spliteText[i] = word;
					} else {
						chars[squen] = 'ْ';
						word = String.valueOf(chars);
						spliteText[i] = word;
					}
				}
			}
		}
		TextUpDate = String.join(" ", spliteText);
		return TextUpDate;
	}

	/*
	 * -----------------------------------------------------------------------------
	 * -- Name: UpDateDii Purpose: Add damah befor و , add fatha befor ا , and add
	 * kasra befor ي Author: Rawan Alsaaran (r.alsaaran@gmail.com) Copyright: (c)
	 * Rawan Alsaaran & Imam Mohammed Ibn Saud Islamic university 2017
	 * -----------------------------------------------------------------------------
	 * --
	 */
	private String UpDateDii(String DiText) throws FileNotFoundException, InterruptedException, Exception, IOException {
		String Text = DiText;
		String spliteText[];
		String TextUpDate = " ";
		String word = "";
		spliteText = Text.split("\\s", 0);
		for (int i = 0; i < spliteText.length; i++) {
			word = spliteText[i];
			char[] chars = word.toCharArray();
			char[] LetterofWord = new char[word.length() * 2];
			int count = 0;
			for (int j = 0; j < word.length(); j++) {
				if (chars[j] == 'ا' || chars[j] == 'و' || chars[j] == 'ي') {
					if (chars[j] == 'ا' && j != 0) {
						if (Character.isLetter(word.charAt(j - 1)) || Character.isDigit(word.charAt(j - 1))) {
							if (Character.isLetter(word.charAt(j - 1))) {
								if (chars[j - 1] != 'ب' && chars[j - 1] != 'ل') {
									LetterofWord[count] = 'َ';
									count++;
									LetterofWord[count] = word.charAt(j);
									count++;
								} else {
									if (j - 1 == 0) {
										LetterofWord[count] = word.charAt(j);
										count++;
									} else {
										LetterofWord[count] = 'َ';
										count++;
										LetterofWord[count] = word.charAt(j);
										count++;
									}
								}
							}
						} else {
							if (chars[j - 2] != 'ب' && chars[j - 2] != 'ل') {
								LetterofWord[count - 1] = 'َ';
								count++;
								LetterofWord[count] = word.charAt(j);
								count++;
							} else {
								if (j - 2 == 0) {
									LetterofWord[count] = word.charAt(j);
									count++;
								} else {
									LetterofWord[count - 1] = 'َ';
									count++;
									LetterofWord[count] = word.charAt(j);
									count++;
								}
							}
						}
					} else {
						if (chars[j] == 'ا' && j == 0) {
							LetterofWord[count] = word.charAt(j);
							count++;
						}
					}

					if (chars[j] == 'و' && j != 0) {
						if (Character.isLetter(word.charAt(j - 1)) || Character.isDigit(word.charAt(j - 1))) {
							if (Character.isLetter(word.charAt(j - 1))) {
								if (chars[j - 1] != 'ب' && chars[j - 1] != 'ل') {
									LetterofWord[count] = 'ُ';
									count++;
									LetterofWord[count] = word.charAt(j);
									count++;
								} else {
									if (j - 1 == 0) {
										LetterofWord[count] = word.charAt(j);
										count++;
									} else {
										LetterofWord[count] = 'ُ';
										count++;
										LetterofWord[count] = word.charAt(j);
										count++;
									}
								}
							}
						} else {
							if (chars[j - 2] != 'ب' && chars[j - 2] != 'ل') {
								LetterofWord[count - 1] = 'ُ';
								count++;
								LetterofWord[count] = word.charAt(j);
								count++;
							} else {
								if (j - 2 == 0) {
									LetterofWord[count] = word.charAt(j);
									count++;
								} else {
									LetterofWord[count - 1] = 'ُ';
									count++;
									LetterofWord[count] = word.charAt(j);
									count++;
								}
							}
						}
					} else {
						if (chars[j] == 'و' && j == 0) {
							LetterofWord[count] = word.charAt(j);
							count++;
						}
					}

					if (chars[j] == 'ي' && j != 0) {
						if (Character.isLetter(word.charAt(j - 1)) || Character.isDigit(word.charAt(j - 1))) {
							if (Character.isLetter(word.charAt(j - 1))) {
								if (chars[j - 1] != 'ب' && chars[j - 1] != 'ل') {
									LetterofWord[count] = 'ِ';
									count++;
									LetterofWord[count] = word.charAt(j);
									count++;
								} else {
									if (j - 1 == 0) {
										LetterofWord[count] = word.charAt(j);
										count++;
									} else {
										LetterofWord[count] = 'ِ';
										count++;
										LetterofWord[count] = word.charAt(j);
										count++;
									}
								}
							}
						} else {
							if (chars[j - 2] != 'ب' && chars[j - 2] != 'ل') {
								LetterofWord[count - 1] = 'ِ';
								count++;
								LetterofWord[count] = word.charAt(j);
								count++;
							} else {
								if (j - 2 == 0) {
									LetterofWord[count] = word.charAt(j);
									count++;
								} else {
									LetterofWord[count - 1] = 'ِ';
									count++;
									LetterofWord[count] = word.charAt(j);
									count++;
								}
							}
						}
					} else {
						if (chars[j] == 'ي' && j == 0) {
							LetterofWord[count] = word.charAt(j);
							count++;
						}
					}
				} else {
					LetterofWord[count] = word.charAt(j);
					count++;
				}
			}
			String wordLast = "";
			for (int k = 0; k < LetterofWord.length && LetterofWord[k] != ' '; k++) {
				wordLast = wordLast + LetterofWord[k];
			}
			wordLast = wordLast.replaceAll("\0", "");
			spliteText[i] = wordLast;
		}
		TextUpDate = String.join(" ", spliteText);
		return TextUpDate;
	}

	/*
	 * -----------------------------------------------------------------------------
	 * -- Name: UpDateDiacriticsAWE Purpose: remove any diacritics on ا،و،ي Author:
	 * Rawan Alsaaran (r.alsaaran@gmail.com) Copyright: (c) Rawan Alsaaran & Imam
	 * Mohammed Ibn Saud Islamic university 2017
	 * -----------------------------------------------------------------------------
	 * --
	 */
	private String UpDateDiacriticsAWE(String DiText)
			throws FileNotFoundException, InterruptedException, Exception, IOException {
		String Text = DiText;
		String spliteText[];
		String TextUpDate = " ";
		String word = "";
		int count = 0;
		spliteText = Text.split("\\s", 0);
		for (int i = 0; i < spliteText.length; i++) {
			word = spliteText[i];
			char[] LetterofWord = new char[word.length() * 2];
			for (int j = 0; j < word.length(); j = j + 1) {
				if (word.charAt(j) == 'و' || word.charAt(j) == 'ا' || word.charAt(j) == 'ي') {
					if (j + 1 < word.length() - 1) {
						if (!Character.isLetter(word.charAt(j + 1)) && !Character.isDigit(word.charAt(j + 1))) {
							LetterofWord[count] = word.charAt(j);
							count++;
							j = j + 1;
						} else {
							LetterofWord[count] = word.charAt(j);
							count++;
						}
					} else {
						LetterofWord[count] = word.charAt(j);
						count++;
					}
				} else {
					LetterofWord[count] = word.charAt(j);
					count++;
				}
			}
			count = 0;
			String wordLast = "";
			for (int k = 0; k < LetterofWord.length && LetterofWord[k] != ' '; k++) {
				wordLast = wordLast + LetterofWord[k];
			}
			wordLast = wordLast.replaceAll("\0", "");
			spliteText[i] = wordLast;
		}
		TextUpDate = String.join(" ", spliteText);
		return TextUpDate;
	}

	public BufferedReader openFileForReading(String filename) throws FileNotFoundException {
		BufferedReader sr = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filename))));
		return sr;
	}

	public BufferedWriter openFileForWriting(String filename) throws FileNotFoundException {
		BufferedWriter sw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(filename))));
		return sw;
	}

}
