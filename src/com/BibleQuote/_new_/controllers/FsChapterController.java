package com.BibleQuote._new_.controllers;

import java.util.ArrayList;

import com.BibleQuote._new_.dal.FsLibraryUnitOfWork;
import com.BibleQuote._new_.dal.repository.IBookRepository;
import com.BibleQuote._new_.dal.repository.IChapterRepository;
import com.BibleQuote._new_.models.Book;
import com.BibleQuote._new_.models.Chapter;
import com.BibleQuote._new_.models.FsBook;
import com.BibleQuote._new_.models.FsModule;
import com.BibleQuote._new_.models.Module;
import com.BibleQuote._new_.models.Verse;
import com.BibleQuote.exceptions.BookNotFoundException;
import com.BibleQuote.utils.StringProc;

public class FsChapterController implements IChapterController {
	//private final String TAG = "FsChapterController";
	
	private IBookRepository<FsModule, FsBook> bRepository;
	private IChapterRepository<FsBook> chRepository;
	
	
	public FsChapterController(FsLibraryUnitOfWork unit) {
		bRepository = unit.getBookRepository();
		chRepository = unit.getChapterRepository();
    }
	

	@Override
	public ArrayList<Chapter> getChapterList(Book book) throws BookNotFoundException {
		book = getValidBook(book);
		ArrayList<Chapter> chapterList = (ArrayList<Chapter>) chRepository.getChapters((FsBook)book);
		if (chapterList.size() == 0) {
			chapterList =  (ArrayList<Chapter>) chRepository.loadChapters((FsBook)book);
		}
		return chapterList;
	}

	
	@Override
	public Chapter getChapter(Book book, Integer chapterNumber) throws BookNotFoundException {
		book = getValidBook(book);
		Chapter chapter = chRepository.getChapterByNumber((FsBook)book, chapterNumber);
		if (chapter == null) {
			chapter = chRepository.loadChapter((FsBook)book, chapterNumber);
		}
		return chapter;
	}
	
	
	@Override
	public ArrayList<Integer> getVerseNumbers(Book book, Integer chapterNumber) throws BookNotFoundException {
		book = getValidBook(book);
		Chapter chapter = chRepository.getChapterByNumber((FsBook)book, chapterNumber);
		if (chapter == null) {
			chapter = chRepository.loadChapter((FsBook)book, chapterNumber);
		}
		return chapter.getVerseNumbers();
	}


	@Override
	public String getChapterHTMLView(Chapter chapter) {
		Module currModule = chapter.getBook().getModule();
		
		ArrayList<Verse> verses = chapter.getVerseList(); 
		StringBuilder chapterHTML = new StringBuilder();
		for (int verse = 1; verse <= verses.size(); verse++) {
			String verseText = verses.get(verse - 1).getText();

			if (currModule.containsStrong) {
				// убираем номера Стронга
				verseText = verseText.replaceAll("\\s(\\d)+", "");
			}
			
			verseText = StringProc.stripTags(verseText, currModule.HtmlFilter, false);
			verseText = verseText.replaceAll("<a\\s+?href=\"verse\\s\\d+?\">(\\d+?)</a>", "<b>$1</b>");
			if (currModule.isBible) {
				verseText = verseText
						.replaceAll("^(<[^/]+?>)*?(\\d+)(</(.)+?>){0,1}?\\s+",
								"$1<b>$2</b>$3 ").replaceAll(
								"null", "");
			}

			chapterHTML.append(
				"<div id=\"verse_" + verse + "\" class=\"verse\">"
				+ verseText.replaceAll("<(/)*div(.*?)>", "<$1p$2>")
				+ "</div>"
				+ "\r\n");
		}

		return chapterHTML.toString();
	}

	
	private Book getValidBook(Book book) throws BookNotFoundException {
		String moduleID = null;
		String bookID = null;
		try {
			Module module = book.getModule();
			book = bRepository.getBookByID((FsModule)module, book.getID());
			if (book == null) {
				throw new BookNotFoundException(moduleID, bookID);
			}
		} catch (Exception e) {
			throw new BookNotFoundException(moduleID, bookID);
		}
		return book;
	}
}
