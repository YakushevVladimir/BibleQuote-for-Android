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
 * File: AndroidLogger.java
 *
 * Created by Vladimir Yakushev at 10/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.data.logger;

import android.support.annotation.NonNull;
import android.util.Log;

import com.BibleQuote.domain.logger.Logger;

public class AndroidLogger extends Logger {

    @Override
    public void debug(@NonNull Object tag, @NonNull String message) {
        Log.d(getTag(tag), message);
    }

    @Override
    public void error(@NonNull Object tag, @NonNull String message) {
        Log.e(getTag(tag), message);
    }

    @Override
    public void error(@NonNull Object tag, @NonNull String message, @NonNull Throwable th) {
        Log.e(getTag(tag), message, th);
    }

    @Override
    public void info(@NonNull Object tag, @NonNull String message) {
        Log.i(getTag(tag), message);
    }
}
