package com.BibleQuote._new_.controllers;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.BibleQuote._new_.dal.DbLibraryUnitOfWork;
import com.BibleQuote._new_.dal.repository.IBookRepository;
import com.BibleQuote._new_.models.Book;
import com.BibleQuote._new_.models.Chapter;
import com.BibleQuote._new_.models.DbBook;
import com.BibleQuote._new_.models.DbModule;
import com.BibleQuote._new_.models.Verse;

public class DbChapterController implements IChapterController {
	private final String TAG = "DbChapterController";
	
	private IBookRepository<DbModule, DbBook> br;

	public DbChapterController(DbLibraryUnitOfWork unit) {
		br = unit.getBookRepository();
    }
	
	@Override
	public Chapter getChapter(Book book, Integer chapterNumber) {
		ArrayList<String> verseNumbers = new ArrayList<String>();
		verseNumbers.add("1");
		verseNumbers.add("2");
		verseNumbers.add("3");
		
		ArrayList<Verse> verseList = new ArrayList<Verse>();
		verseList.add(new Verse(1, "This is a verse number 1"));
		verseList.add(new Verse(2, "This is a verse number 2"));
		verseList.add(new Verse(3, "This is a verse number 3"));
		
		//Chapter chapter = new Chapter(chapterNumber, verseNumbers, verseList);
		//chapter.Text = " This is a chapter #" + chapterNumber; 
		return null;
	}
	
	@Override
	public ArrayList<Integer> getVerseNumbers(Book book, Integer chapterNumber) {
		Chapter chapter = getChapter(book, chapterNumber);
		return chapter.getVerseNumbers();
	}


	@Override
	public LinkedHashMap<Integer, Chapter> loadChapters(Book book) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void loadChaptersAsync(Book book) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public Chapter loadChapter(Book book, Integer chapterNumber) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void loadChapterAsync(Book book, Integer chapterNumber) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public ArrayList<Chapter> getChapterList(Book book) {
		// TODO Auto-generated method stub
		return null;
	}
}
