package com.BibleQuote.managers;

import com.BibleQuote.exceptions.BookDefinitionException;
import com.BibleQuote.exceptions.BooksDefinitionException;
import com.BibleQuote.exceptions.OpenModuleException;
import com.BibleQuote.models.Module;
import com.BibleQuote.utils.Log;
import com.BibleQuote.utils.OSISLink;
import com.BibleQuote.utils.Task;

public class AsyncOpenBooks extends Task {
	private final String TAG = "AsyncTaskChapterOpen";
	
	private Librarian librarian;
	private OSISLink link;
	private Exception exception;
	private Boolean isSuccess;
	private Module module;
	
	
	public AsyncOpenBooks(String message, Boolean isHidden, Librarian librarian, OSISLink link) {
		super(message, isHidden);
		this.librarian = librarian;
		this.link = link;
	}

	
	@Override
	protected Boolean doInBackground(String... arg0) {
		isSuccess = false;
		try {
			Log.i(TAG, String.format("Open OSIS link with moduleID=%1$s", link.getModuleID()));
			module = librarian.openModule(link.getModuleID(), link.getModuleDatasourceID());
			
			Log.i(TAG, String.format("Load books for module with moduleID=%1$s", module.getID()));
			librarian.getBookList(module);

			isSuccess = true;
		} catch (OpenModuleException e) {
			//Log.e(TAG, String.format("AsyncOpenBooks(): ", e.toString()), e);
			exception = e;
		} catch (BooksDefinitionException e) {
			exception = e;
		} catch (BookDefinitionException e) {
			exception = e;
		}
		
		return true;
	}
	
	
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
	}

	public Exception getException() {
		return exception;
	}

	public Boolean isSuccess() {
		return isSuccess;
	}

	public Module getModule() {
		return module;
	}

}
