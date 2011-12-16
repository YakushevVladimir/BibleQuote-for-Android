package com.BibleQuote._new_.controllers;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.BibleQuote._new_.dal.DbLibraryUnitOfWork;
import com.BibleQuote._new_.dal.repository.IBookRepository;
import com.BibleQuote._new_.managers.EventManager;
import com.BibleQuote._new_.models.Book;
import com.BibleQuote._new_.models.DbBook;
import com.BibleQuote._new_.models.DbModule;
import com.BibleQuote._new_.models.Module;

public class DbBookController implements IBookController {
	private final String TAG = "DbBookController";
	
	//private EventManager eventManager;
	private IBookRepository<DbModule, DbBook> br;
	
    public DbBookController(DbLibraryUnitOfWork unit, EventManager eventManager)
    {
		//this.eventManager = eventManager;
		br = unit.getBookRepository();
    }

    
	public LinkedHashMap<String, Book> loadBooks(Module module) {
		android.util.Log.i(TAG, "Loading books from a DB storage.");
		LinkedHashMap<String, Book> result = new LinkedHashMap<String, Book>();
		
		ArrayList<Book> bookList = new ArrayList<Book>();
		bookList.addAll(br.loadBooks((DbModule)module));
		for (Book book : bookList) {
			result.put(book.Name, book);
		}
		
		return result;
	}


	@Override
	public void loadBooksAsync(Module module) {
		// TODO Auto-generated method stub
	}
	

	@Override
	public LinkedHashMap<String, Book> getBooks(Module module) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public ArrayList<Book> getBookList(Module module) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public Book getBook(Module module, String bookName) {
		return br.getBookByName((DbModule)module, bookName);
	}

}
