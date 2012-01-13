package com.BibleQuote.dal.repository;

import java.util.Collection;

import com.BibleQuote.exceptions.BookNotFoundException;
import com.BibleQuote.models.Chapter;

public interface IChapterRepository<TBook> {
    
	/*
	 * Data source related methods
	 * 
	 */
	Collection<Chapter> loadChapters(TBook book) throws BookNotFoundException;
	
	Chapter loadChapter(TBook book, Integer chapterNumber) throws BookNotFoundException;
	
	void insertChapter(Chapter chapter);
    
	void deleteChapter(Chapter chapter);
	
	void updateChapter(Chapter chapter);
	
	/*
	 * Internal cache related methods
	 *
	 */
	Collection<Chapter> getChapters(TBook book);
	
	Chapter getChapterByNumber(TBook book, Integer chapterNumber);

}
