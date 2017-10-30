/*
 * Copyright (C) 2011 Scripture Software
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * Project: BibleQuote-for-Android
 * File: FsLibraryLoader.java
 *
 * Created by Vladimir Yakushev at 10/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.dal.repository;

import android.support.annotation.NonNull;

import com.BibleQuote.domain.entity.BaseModule;
import com.BibleQuote.domain.exceptions.BookDefinitionException;
import com.BibleQuote.domain.exceptions.BooksDefinitionException;
import com.BibleQuote.domain.exceptions.OpenModuleException;
import com.BibleQuote.domain.logger.StaticLogger;
import com.BibleQuote.domain.repository.LibraryLoader;
import com.BibleQuote.entity.modules.BQModule;
import com.BibleQuote.utils.DataConstants;
import com.BibleQuote.utils.FsUtils;
import com.BibleQuote.utils.FsUtilsWrapper;
import com.BibleQuote.utils.OnlyBQIni;
import com.BibleQuote.utils.OnlyBQZipIni;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class FsLibraryLoader implements LibraryLoader<BQModule> {

    private File libraryDir;
    private BQModuleRepository repository;

    public FsLibraryLoader(FsUtilsWrapper fsUtils) {
        this.libraryDir = getLibraryDir();
        this.repository = new BQModuleRepository(fsUtils);
    }

    @NonNull
    private File getLibraryDir() {
        File result = new File(DataConstants.getLibraryPath());
        if (!result.exists()) {
            boolean deleted = result.mkdir();
            if (!deleted) {
                StaticLogger.error(this, "Don't remove library directory");
            }
        }
        return result;
    }

    @Override
    public synchronized Map<String, BaseModule> loadFileModules() {
        StaticLogger.info(this, "Load modules from sd-card:");

        Map<String, BaseModule> result = new TreeMap<>();

		// Load zip-compressed BQ-modules
        StaticLogger.info(this, "Search zip-modules");
        List<String> bqZipIniFiles = searchModules(new OnlyBQZipIni());
        for (String bqZipIniFile : bqZipIniFiles) {
            try {
                BaseModule module = loadFileModule(getZipDataSourceId(bqZipIniFile));
                result.put(module.getID(), module);
            } catch (OpenModuleException | BookDefinitionException | BooksDefinitionException e) {
                e.printStackTrace();
            }
        }

		// Load standard BQ-modules
        StaticLogger.info(this, "Search standard modules");
        List<String> bqIniFiles = searchModules(new OnlyBQIni());
        for (String moduleDataSourceId : bqIniFiles) {
            try {
                BaseModule module = loadFileModule(moduleDataSourceId);
                result.put(module.getID(), module);
            } catch (OpenModuleException | BookDefinitionException | BooksDefinitionException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

	@Override
    public BaseModule loadModule(String path) throws OpenModuleException, BooksDefinitionException, BookDefinitionException {
        if (path.endsWith("zip")) {
            path = getZipDataSourceId(path);
		}
        return loadFileModule(path);
    }

    private String getZipDataSourceId(String path) {
        return path + File.separator + "bibleqt.ini";
    }

    private boolean isLibraryExist() {
        return libraryDir != null && libraryDir.exists();
    }

    private BaseModule loadFileModule(String moduleDataSourceId)
            throws OpenModuleException, BooksDefinitionException, BookDefinitionException {
        return repository.loadModule(moduleDataSourceId);
    }

    /**
     * Выполняет поиск папок с модулями Цитаты на внешнем носителе устройства
     *
     * @return Возвращает ArrayList со списком ini-файлов модулей
     */
    private List<String> searchModules(FileFilter filter) {
        ArrayList<String> iniFiles = new ArrayList<>();
        if (!isLibraryExist()) {
            StaticLogger.error(this, "Module library folder not found");
            return iniFiles;
        }

        try {
            // Рекурсивная функция проходит по всем каталогам в поисках ini-файлов Цитаты
            FsUtils.searchByFilter(libraryDir, iniFiles, filter);
        } catch (Exception e) {
            StaticLogger.error(this, "searchModules()", e);
        }

        return iniFiles;
    }
}
