package com.BibleQuote._new_.dal.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.BibleQuote._new_.dal.DbLibraryContext;
import com.BibleQuote._new_.models.DbBook;
import com.BibleQuote._new_.models.DbModule;

public class DbBookRepository implements IBookRepository<DbModule, DbBook> {

	private SQLiteDatabase db;
	
    public DbBookRepository(DbLibraryContext context)
    {
        db = context.getDB();
    }
    
	@Override
	public Collection<DbBook> loadBooks(DbModule module) {
		Cursor cursor = db.rawQuery("SELECT * FROM Book WHERE `moduleId` = " + module.getID(), null);
		final ArrayList<DbBook> bookList = new ArrayList<DbBook>();
		while (cursor.moveToNext()) {
			final long id = cursor.getLong(0);
			final DbBook book = new DbBook(module, "", "", "", 0, id);   
			bookList.add(book);
		}
		cursor.close();
		return bookList;
	}
	

	@Override
	public Collection<DbBook> getBooks(DbModule module) {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public DbBook getBookByID(DbModule module, String bookID) {
		// TODO Auto-generated method stub
//		DbBook book = null;
//		final Cursor cursor = db.rawQuery("SELECT * FROM Book WHERE book_id = ? ", new String[] {"" + bookId});
//		if (cursor.moveToNext()) {
//			final long id = cursor.getLong(0);
//			book = new DbBook("", "", "", 0, id);   
//		}
//		cursor.close();
//		return book;	
		return null;
	}

	@Override
	public LinkedHashMap<String, String> searchInBook(DbModule module,
			String bookID, String regQuery) {
		// TODO Auto-generated method stub
		return null;
	}
	

}
