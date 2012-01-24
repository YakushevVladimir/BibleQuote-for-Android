package com.BibleQuote.controllers;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import android.util.Log;

import com.BibleQuote.dal.FsLibraryUnitOfWork;
import com.BibleQuote.dal.repository.IBookRepository;
import com.BibleQuote.dal.repository.IModuleRepository;
import com.BibleQuote.exceptions.BookDefinitionException;
import com.BibleQuote.exceptions.BookNotFoundException;
import com.BibleQuote.exceptions.BooksDefinitionException;
import com.BibleQuote.exceptions.OpenModuleException;
import com.BibleQuote.models.Book;
import com.BibleQuote.models.FsBook;
import com.BibleQuote.models.FsModule;
import com.BibleQuote.models.Module;

public class FsBookController implements IBookController {
	private final String TAG = "FsBookController";

	private IBookRepository<FsModule, FsBook> bRepository;
	private IModuleRepository<String, FsModule> mRepository;

	public FsBookController(FsLibraryUnitOfWork unit) {
		bRepository = unit.getBookRepository();
		mRepository = unit.getModuleRepository();
    }
	
	
	public LinkedHashMap<String, Book> getBooks(Module module) throws OpenModuleException, BooksDefinitionException, BookDefinitionException {
		module = getValidModule(module);
		
		LinkedHashMap<String, Book> result = new LinkedHashMap<String, Book>();
		ArrayList<Book> bookList = getBookList(module);
		for (Book book : bookList) {
			result.put(book.getID(), book);
		}
		
		return result;		
	}
	
	
	public ArrayList<Book> getBookList(Module module) throws OpenModuleException, BooksDefinitionException, BookDefinitionException {
		module = getValidModule(module);

		ArrayList<FsBook> bookList = (ArrayList<FsBook>) bRepository.getBooks((FsModule)module);
		if (bookList.size() == 0) {
			bookList = (ArrayList<FsBook>) bRepository.loadBooks((FsModule)module);
		}
		
		return new ArrayList<Book>(bookList);
	}	
	
	
	public Book getBookByID(Module module, String bookID) throws BookNotFoundException, OpenModuleException {
		module = getValidModule(module);

		Book book = bRepository.getBookByID((FsModule)module, bookID);
		if (book == null) {
			try {
				bRepository.loadBooks((FsModule) module);
			} catch (BooksDefinitionException e) {
				Log.e(TAG, e.getMessage());
			} catch (BookDefinitionException e) {
				Log.e(TAG, e.getMessage());
			}
			book = bRepository.getBookByID((FsModule)module, bookID);
		}
		if (book == null) {
			throw new BookNotFoundException(module.getID(), bookID);
		}		
		return book;
	}		

	
	public LinkedHashMap<String, String> search(Module module, String query, String fromBookID, String toBookID) 
			throws OpenModuleException, BookNotFoundException {
		LinkedHashMap<String, String> searchRes = new LinkedHashMap<String, String>();
	
		if (query.trim().equals("")) {
			// Передана пустая строка
			return searchRes;
		}
		
		// Подготовим регулярное выражение для поиска
		String regQuery = "";
		String[] words = query.toLowerCase().split("\\s+");
		for (String currWord : words) {
			regQuery += (regQuery.equals("") ? "" : "\\s(.)*?") + currWord;
		}
		regQuery = ".*?" + regQuery + ".*?"; // любые символы в начале и конце
	
		boolean startSearch = false;
		try {
			for (String bookID : getBooks(module).keySet()) {
				if (!startSearch) {
					startSearch = bookID.equals(fromBookID);
					if (!startSearch) {
						continue;
					}
				} 
				searchRes.putAll(bRepository.searchInBook((FsModule)module, bookID, regQuery));
				if (bookID.equals(toBookID)) {
					break;
				}
			}
		} catch (BooksDefinitionException e) {
			Log.e(TAG, e.getMessage());
		} catch (BookDefinitionException e) {
			Log.e(TAG, e.getMessage());
		}

		return searchRes;
	}
	
	
	private Module getValidModule(Module module) throws OpenModuleException {
		if (module == null) {
			throw new OpenModuleException("", "");
		}
		
		String moduleID = module.getID();
		String moduleDatasourceID = module.getDataSourceID();
		if (module.getIsClosed()) {
			module = mRepository.loadModuleById(moduleDatasourceID);
			if (module != null) {
				moduleID = module.getID();
			}
		}
		
		module = mRepository.getModuleByID(moduleID);
		if (module == null) {
			throw new OpenModuleException(moduleID, moduleDatasourceID);
		}
		return module;
	}
	
}
