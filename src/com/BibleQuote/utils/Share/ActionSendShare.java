package com.BibleQuote.utils.Share;

import java.util.LinkedHashMap;

import android.content.Context;
import android.content.Intent;

import com.BibleQuote.R;
import com.BibleQuote.models.Book;
import com.BibleQuote.models.Chapter;
import com.BibleQuote.models.Module;

public class ActionSendShare extends BaseShareBuilder {
	
	public ActionSendShare(Context context, Module module, Book book,
			Chapter chapter, LinkedHashMap<Integer, String> verses) {
		this.context = context;
		this.module = module;
		this.book = book;
		this.chapter = chapter;
		this.verses = verses;
	}

	@Override
	public void share() {
		InitFormatters();
		if (textFormater == null || referenceFormatter == null) {
			return;
		}
		
		final String share = context.getResources().getString(R.string.share);
		Intent send = new Intent(Intent.ACTION_SEND);
		send.setType("text/plain");
		send.putExtra(Intent.EXTRA_TEXT, getShareText());
		context.startActivity(Intent.createChooser(send, share));;
	}

}
