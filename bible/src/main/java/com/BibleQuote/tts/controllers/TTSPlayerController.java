/*
 * Copyright (C) 2011 Scripture Software (http://scripturesoftware.org/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.BibleQuote.tts.controllers;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import com.BibleQuote.tts.exceptions.LanguageNotSupportedException;
import com.BibleQuote.tts.exceptions.OnInitException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * User: Vladimir
 * Date: 08.02.13
 * Time: 23:01
 */
public class TTSPlayerController extends UtteranceProgressListener implements TextToSpeech.OnInitListener {

	private static final String TAG = "TTSPlayerController";

	TextToSpeech ttsEngine;
	HashMap<String, String> ttsParams;
	Context cont;
	boolean isPaused;

    private int currText;
    private Locale locale;
    private Exception error;
    private ArrayList<String> textList;
    private ArrayList<OnEventListener> listeners = new ArrayList<>();

    public TTSPlayerController(Context cont, Locale locale) {
        this.cont = cont;
        this.locale = locale;
        initTTS(cont);
    }

    public TTSPlayerController(Context cont, Locale locale, ArrayList<String> textList) {
        this(cont, locale);
        this.textList = textList;
    }

    @Override
    public void onStart(String utteranceId) {

    }

    @Override
    public void onDone(String utteranceId) {
        Log.i(TAG, "onUtteranceCompleted() -> " + currText);
        if (isPaused) {
            return;
        } else if (currText == textList.size() - 1) {
            currText = 0;
            isPaused = true;
            notifyListeners(Event.PauseSpeak);
            return;
        }
        currText++;
        speakTTS(currText);
    }

    @Override
    public void onError(String utteranceId) {

    }

	@Override
	public void onInit(int initStatus) {
		Log.i(TAG, "onInit()");
		if (initStatus == TextToSpeech.SUCCESS) {
			ttsParams = new HashMap<>();
			ttsParams.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "TTSPlayerController");
			setLanguage();
			ttsEngine.setOnUtteranceProgressListener(this);
		} else if (initStatus == TextToSpeech.ERROR) {
			error = new OnInitException();
			notifyListeners(Event.Error);
		}

		currText = 0;
		speakTTS(currText);
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		ttsEngine.shutdown();
		ttsEngine = null;
	}

    public int getCurrText() {
        return currText;
    }

    public Exception getError() {
        return error;
    }

	public void setOnInitListener(OnEventListener listener) {
		if (!listeners.contains(listener)) listeners.add(listener);
	}

    public void play() {
        Log.i(TAG, "play() -> " + currText);
        isPaused = false;
        speakTTS(currText);
    }

    public void replay() {
        currText = 0;
        Log.i(TAG, "replay() -> " + currText);
        stopTTS();
        play();
    }

    public void pause() {
        Log.i(TAG, "pause() -> " + currText);
        stopTTS();
    }

    public void stop() {
        Log.i(TAG, "stop() -> " + currText);
        stopTTS();
        currText = 0;
        notifyListeners(Event.PauseSpeak);
    }

    public void moveNext() {
        stopTTS();
        if (currText < textList.size() - 1) currText++;
        Log.i(TAG, "moveNext() -> " + currText);
        notifyListeners(Event.ChangeTextIndex);
    }

    public void movePrevious() {
        stopTTS();
        if (currText != 0) currText--;
        Log.i(TAG, "movePrevious() -> " + currText);
        notifyListeners(Event.ChangeTextIndex);
    }

    private void notifyListeners(Event ev) {
		for (OnEventListener listener : listeners) listener.onEvent(ev);
	}

	private void setLanguage() {
        if (locale == null) {
            eventNotSupportedLanguage();
        }

		int initStatus = ttsEngine.isLanguageAvailable(locale);
		if (initStatus == TextToSpeech.LANG_MISSING_DATA || initStatus == TextToSpeech.LANG_NOT_SUPPORTED) {
            eventNotSupportedLanguage();
        } else {
            ttsEngine.setLanguage(locale);
		}
	}

    private void eventNotSupportedLanguage() {
        error = new LanguageNotSupportedException(locale);
        notifyListeners(Event.Error);
    }

    private void initTTS(Context cont) {
        if (ttsEngine == null) {
            ttsEngine = new TextToSpeech(cont, this);
		}
	}

    private void speakTTS(int verseIndex) {
        if (error != null) {
            return;
        } else if (verseIndex > textList.size() - 1) {
            return;
        }

        Log.i(TAG, "speakTTS() -> " + verseIndex);
        ttsEngine.speak(textList.get(verseIndex), TextToSpeech.QUEUE_FLUSH, ttsParams);
        notifyListeners(Event.ChangeTextIndex);
    }

    private void stopTTS() {
        Log.i(TAG, "stopTTS() -> " + currText);
        isPaused = true;
        if (ttsEngine.isSpeaking()) ttsEngine.stop();
    }

    public enum Event {
        Error, ChangeTextIndex, PauseSpeak
    }

    public interface OnEventListener {
        void onEvent(Event ev);
    }
}
