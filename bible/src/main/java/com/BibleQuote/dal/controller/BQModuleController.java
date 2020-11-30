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
 * File: BQModuleController.java
 *
 * Created by Vladimir Yakushev at 9/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.dal.controller;

import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import android.util.Log;

import com.BibleQuote.dal.repository.BQModuleRepository;
import com.BibleQuote.domain.controller.IModuleController;
import com.BibleQuote.domain.entity.Book;
import com.BibleQuote.domain.entity.Chapter;
import com.BibleQuote.domain.exceptions.BookNotFoundException;
import com.BibleQuote.domain.search.MultiThreadSearchProcessor;
import com.BibleQuote.entity.modules.BQModule;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 *
 */
public class BQModuleController implements IModuleController {

    private static final String TAG = BQModuleController.class.getSimpleName();

    private BQModule module;
    private BQModuleRepository repository;

    public BQModuleController(BQModule module, BQModuleRepository repository) {
        this.module = module;
        this.repository = repository;
    }

    @Override
    public List<Book> getBooks() {
        return new ArrayList<>(module.getBooks().values());
    }

    @Override
    public Bitmap getBitmap(String path) {
        return repository.getBitmap(module, path);
    }

    @Override
    @NonNull
    public Book getBookByID(String bookId) throws BookNotFoundException {
        Map<String, Book> books = module.getBooks();
        Book result = books.get(bookId);
        if (result == null) {
            throw new BookNotFoundException(module.getID(), bookId);
        }
        return result;
    }

    @Override
    public Book getNextBook(String bookId) throws BookNotFoundException {
        Book result = getBookByID(bookId);
        List<Book> books = getBooks();
        int pos = books.indexOf(result);
        if (books.size() > ++pos) {
            return books.get(pos);
        }
        return null;
    }

    @Override
    public Book getPrevBook(String bookId) throws BookNotFoundException {
        Book result = getBookByID(bookId);
        List<Book> books = getBooks();
        int pos = books.indexOf(result);
        if (pos > 0) {
            return books.get(--pos);
        }
        return null;
    }

    @Override
    public List<String> getChapterNumbers(String bookId) throws BookNotFoundException {
        ArrayList<String> result = new ArrayList<>();
        Book book = getBookByID(bookId);
        for (int i = 0; i < book.getChapterQty(); i++) {
            result.add("" + (i + (module.isChapterZero() ? 0 : 1)));
        }
        return result;
    }

    @Override
    public Chapter getChapter(String bookId, int chapter) throws BookNotFoundException {
        return repository.loadChapter(module, bookId, chapter);
    }

    @Override
    public Map<String, String> search(List<String> bookList, String searchQuery) {
        long searchStart = System.currentTimeMillis();
        Map<String, String> result = new MultiThreadSearchProcessor<>(repository).search(module, bookList, searchQuery);
        long searchEnd = System.currentTimeMillis();
        Log.d(TAG, String.format(Locale.US, "Search '%s' completed in %d ms", searchQuery, searchEnd - searchStart));
        return result;
    }
}
