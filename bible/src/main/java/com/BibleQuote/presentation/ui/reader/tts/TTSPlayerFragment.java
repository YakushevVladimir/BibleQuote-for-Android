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
 * File: TTSPlayerFragment.java
 *
 * Created by Vladimir Yakushev at 10/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.presentation.ui.reader.tts;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.BibleQuote.BibleQuoteApp;
import com.BibleQuote.R;
import com.BibleQuote.managers.Librarian;
import com.BibleQuote.presentation.widget.PlayerView;
import com.BibleQuote.presentation.widget.ReaderWebView;

import java.util.TreeSet;

/**
 * User: Vladimir
 * Date: 25.01.13
 * Time: 2:40
 */
public class TTSPlayerFragment extends Fragment implements PlayerView.OnClickListener, TTSPlayerController.OnEventListener {

    OnTTSStopSpeakListener listener;
    TTSPlayerController ttsController;
    ReaderWebView webView;
    private PlayerView player;

    public void setTTSStopSpeakListener(OnTTSStopSpeakListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.tts_player_layout, container, false);

        player = (PlayerView) result.findViewById(R.id.tts_player);
        player.setOnClickListener(this);

        webView = (ReaderWebView) getActivity().findViewById(R.id.readerView);

        Librarian librarian = BibleQuoteApp.getInstance().getLibrarian();
        ttsController = new TTSPlayerController(getActivity(), librarian.getTextLocale(), librarian.getCleanedVersesText());
        ttsController.setOnInitListener(this);

        return result;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (ttsController != null) {
            ttsController = null;
        }
    }

    @Override
    public void onClick(PlayerView.Event ev) {
        if (ev == PlayerView.Event.ReplayClick) {
            ttsController.replay();
        } else if (ev == PlayerView.Event.PreviousClick) {
            ttsController.movePrevious();
        } else if (ev == PlayerView.Event.PlayClick) {
            ttsController.play();
        } else if (ev == PlayerView.Event.PauseClick) {
            ttsController.pause();
        } else if (ev == PlayerView.Event.NextClick) {
            ttsController.moveNext();
        } else if (ev == PlayerView.Event.StopClick) {
            ttsController.stop();
            if (listener != null) {
                listener.onStopSpeak();
            }
        } else {
            Toast.makeText(getActivity(), "Unknown command", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onEvent(TTSPlayerController.Event ev) {
        if (ttsController == null) {
            return;
        }

        if (ev == TTSPlayerController.Event.Error) {
            Toast.makeText(getActivity(), ttsController.getError().getMessage(), Toast.LENGTH_LONG).show();
        } else if (ev == TTSPlayerController.Event.ChangeTextIndex) {
            int nextTextIndex = ttsController.getCurrText();
            TreeSet<Integer> selected = new TreeSet<>();
            selected.add((++nextTextIndex));
            webView.setSelectedVerse(selected);
            webView.gotoVerse(nextTextIndex);
        } else if (ev == TTSPlayerController.Event.PauseSpeak) {
            TreeSet<Integer> selected = new TreeSet<>();
            webView.setSelectedVerse(selected);
            player.viewPlayButton();
        }
    }

    public interface OnTTSStopSpeakListener {
        void onStopSpeak();
    }
}
