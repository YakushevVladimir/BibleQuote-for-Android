package com.BibleQuote.managers;

import java.util.TreeMap;

import com.BibleQuote.exceptions.ModuleNotFoundException;
import com.BibleQuote.listeners.ChangeModulesEvent;
import com.BibleQuote.listeners.ChangeModulesEvent.ChangeCode;
import com.BibleQuote.models.Module;
import com.BibleQuote.utils.Log;
import com.BibleQuote.utils.Task;

public class AsyncOpenModules extends Task {
	private final String TAG = "AsyncTaskChapterOpen";
	
	private ChangeModulesEvent event;
	private Librarian librarian;
	private Module nextClosedModule = null;
	private Boolean isReload = false;
	
	public AsyncOpenModules(String message, Boolean isHidden, Librarian librarian, Boolean isReload) {
		super(message, isHidden);
		this.librarian = librarian;
		this.isReload = isReload;
	}

	
	@Override
	protected Boolean doInBackground(String... arg0) {
		try {
			if (isReload) {
				librarian.loadModules();
				isReload = false;
			}
			Module module = librarian.getClosedModule();
			if (module != null) {
				Log.i(TAG, String.format("Open module with moduleID=%1$s", module.getID()));
				module = librarian.openModule(module.getID(), module.getDataSourceID());
				TreeMap<String, Module> modules = new TreeMap<String, Module>();
				modules.put(module.getID(), module);
				event = new ChangeModulesEvent(ChangeCode.ModulesChanged, modules);
				nextClosedModule = librarian.getClosedModule();
			}
		} catch (ModuleNotFoundException e) {
			Log.e(TAG, String.format("doInBackground(): %1$s", e.toString()), e);
		}
		return true;
	}
	
	
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
	}

	
	public ChangeModulesEvent getEvent() {
		return event;
	}
	
	
	public Module getNextClosedModule() {
		return nextClosedModule;
	}
}
