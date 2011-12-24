package com.BibleQuote._new_.listeners;

import java.util.TreeMap;

import com.BibleQuote._new_.models.Module;

public class ChangeModulesEvent {
	public static enum ChangeCode {
		ModulesAdded,
		ModulesChanged,
		ModulesDeleted
	}
	
	public ChangeCode code;
	public TreeMap<String, Module> modules;
	
	public ChangeModulesEvent(ChangeCode code, TreeMap<String, Module> modules) {
		this.code = code;
		this.modules = modules;
	}
}
