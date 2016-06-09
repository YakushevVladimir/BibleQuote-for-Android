package com.BibleQuote.modules;

import com.BibleQuote.utils.textFormatters.ITextFormatter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class Chapter {

	private Integer number;
	private String text;
	private TreeMap<Integer, Verse> verses = new TreeMap<Integer, Verse>();
	private Book book;

	public Chapter(Book book, Integer number, ArrayList<Verse> verseList) {
		this.book = book;
		this.number = number;

		Integer verseNumber = 1;
		for (Verse verse : verseList) {
			verses.put(verseNumber++, verse);
		}
	}

	public Integer getNumber() {
		return number;
	}

	public String getText() {
		if (text == null && verses.size() > 0) {
			StringBuilder buffer = new StringBuilder();
			for (Map.Entry<Integer, Verse> entry : verses.entrySet()) {
				buffer.append(entry.getValue().getText());
			}
			text = buffer.toString();
		}
		return text;
	}

	public String getText(int fromVerse, int toVerse) {
		StringBuilder buffer = new StringBuilder();
		for (int verseNumber = fromVerse; verseNumber <= toVerse; verseNumber++) {
			Verse ver = verses.get(verseNumber);
			if (ver != null) {
				buffer.append(ver.getText());
			}
		}
		return buffer.toString();
	}

	public String getText(int fromVerse, int toVerse, ITextFormatter formatter) {
		StringBuilder buffer = new StringBuilder();
		for (int verseNumber = fromVerse; verseNumber <= toVerse; verseNumber++) {
			Verse ver = verses.get(verseNumber);
			if (ver != null) {
				buffer.append(formatter.format(ver.getText()));
			}
		}
		return buffer.toString();
	}

	public ArrayList<Integer> getVerseNumbers() {
		ArrayList<Integer> verseNumbers = new ArrayList<Integer>();
		for (Integer verse : verses.keySet()) {
			verseNumbers.add(verse);
		}
		return verseNumbers;
	}

	public LinkedHashMap<Integer, String> getVerses(TreeSet<Integer> verses) {
		LinkedHashMap<Integer, String> result = new LinkedHashMap<Integer, String>();
		ArrayList<Verse> versesList = getVerseList();
		int verseListSize = versesList.size();
		for (Integer verse : verses) {
			int verseIndex = verse - 1;
			if (verseIndex > verseListSize) {
				break;
			}
			result.put(verse, versesList.get(verseIndex).getText());
		}

		return result;
	}

	public ArrayList<Verse> getVerseList() {
		ArrayList<Verse> verseList = new ArrayList<Verse>();
		for (Map.Entry<Integer, Verse> entry : verses.entrySet()) {
			verseList.add(entry.getValue());
		}
		return verseList;
	}

	public Book getBook() {
		return book;
	}
}
