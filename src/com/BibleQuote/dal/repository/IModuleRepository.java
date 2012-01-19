package com.BibleQuote.dal.repository;

import java.util.Collection;

import com.BibleQuote.exceptions.ModuleNotFoundException;

public interface IModuleRepository<TModuleId, TModule> {
    
	/*
	 * Data source related methods
	 * 
	 */
	Collection<TModule> loadModules();
	
	TModule loadModuleById(TModuleId moduleDataSourceId) throws ModuleNotFoundException;
	
    void insertModule(TModule module);
    
    void deleteModule(TModule module);

    void updateModule(TModule module);
    
	
	/*
	 * Internal cache related methods
	 *
	 */
	Collection<TModule> getModules();
    
	TModule getModuleByID(String moduleID);
	
	TModule getModuleByDatasourceID(String moduleDatasourceID);
	
	public TModule getClosedModule();
    
}
