package com.BibleQuote.dal.repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;

import com.BibleQuote.controllers.CacheModuleController;
import com.BibleQuote.dal.FsLibraryContext;
import com.BibleQuote.exceptions.BookNotFoundException;
import com.BibleQuote.exceptions.FileAccessException;
import com.BibleQuote.exceptions.ModuleNotFoundException;
import com.BibleQuote.models.Book;
import com.BibleQuote.models.FsBook;
import com.BibleQuote.models.FsModule;
import com.BibleQuote.utils.Log;

public class FsBookRepository implements IBookRepository<FsModule, FsBook> {
	
	private String TAG = "FsBookRepository";
	private FsLibraryContext context;
	private CacheModuleController<FsModule> cache;
	
    public FsBookRepository(FsLibraryContext context) {
    	this.context = context;
    	this.cache = context.getCache();
    }
    
    
	public Collection<FsBook> loadBooks(FsModule module) throws ModuleNotFoundException {
		module.Books = context.bookSet = new LinkedHashMap<String, Book>();
		BufferedReader reader = null;
		String moduleID = "";
		try {
			moduleID = module.getID();
			reader = context.getModuleReader(module); 
			context.fillBooks(module, reader);
			
		} catch (FileAccessException e) {
			Log.e(TAG, "Can't load books from module with ID=" + moduleID);
			throw new ModuleNotFoundException(moduleID);
			
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace(); 
			}
		}

		// Update cache with just added books
		//cache.saveModuleList(context.getModuleList(context.moduleSet));

		return context.getBookList(module.Books); 	
	}
	
	
	public Collection<FsBook> getBooks(FsModule module) {
		return context.getBookList(module.Books); 
	}

	
	public FsBook getBookByID(FsModule module, String bookID) {
		return (FsBook)module.Books.get(bookID);
	}


	public LinkedHashMap<String, String> searchInBook(FsModule module, String bookID, String regQuery) throws BookNotFoundException {
		FsBook book = getBookByID((FsModule)module, bookID);
		LinkedHashMap<String, String> searchRes = null;
		BufferedReader bReader = null;
		String moduleID = "";
		try {
			moduleID = module.getID();
			bReader = context.getBookReader(book);
			searchRes = context.searchInBook(module, bookID, regQuery, bReader);
		} catch (FileAccessException e) {
			Log.e(TAG, "Can't load books from module with ID=" + moduleID);
			throw new BookNotFoundException(moduleID, bookID);
			
		} finally {
			try {
				if (bReader != null) {
					bReader.close();
				}
			} catch (IOException e) {
				e.printStackTrace(); 
			}
		}
		return searchRes;
	}


}
