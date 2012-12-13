package com.BibleQuote.utils.BibleReferenceFormatter;

import java.util.TreeSet;

import com.BibleQuote.models.Book;
import com.BibleQuote.models.Module;
import com.BibleQuote.utils.PreferenceHelper;

public class ShortReferenceFormatter extends ReferenceFormatter implements IBibleReferenceFormatter {

	public ShortReferenceFormatter(Module module, Book book, String chapter,
			TreeSet<Integer> verses) {
		super(module, book, chapter, verses);
	}

	@Override
	public String getLink() {
		
		String result = String.format(
				"%1$s %2$s:%3$s", 
				book.getShortName(), chapter, getVerseLink());
		if (PreferenceHelper.addModuleToBibleReference()) {
			result = String.format("%1$s | %2$s", result, module.getID());
		} 
		return result;
	}

}
