/*
 * Copyright (C) 2011 Scripture Software (http://scripturesoftware.org/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.BibleQuote.controls;

import java.util.TreeSet;

import com.BibleQuote.activity.Reader;
import com.BibleQuote.utils.PreferenceHelper;

import android.content.Context;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class ReaderWebView extends WebView
	implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener{

	final String TAG = "ReaderWebView";
	
	private GestureDetector mGestureScanner;
	private JavaScriptInterface jsInterface;
	protected TreeSet<Integer> selectedVerse = new TreeSet<Integer>();
	private boolean isStudyMode = false;

	public boolean mPageLoaded = false;

	public ReaderWebView(Context mContext, AttributeSet attributeSet) {
		super(mContext, attributeSet);

		WebSettings settings = getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setNeedInitialFocus(false);
		settings.setBuiltInZoomControls(false);
		settings.setSupportZoom(false);

	    setFocusable(true);
	    setFocusableInTouchMode(true);
		setWebViewClient(new webClient());
		setWebChromeClient(new chromeClient());
		
		this.jsInterface = new JavaScriptInterface();
		addJavascriptInterface(this.jsInterface, "reader");
		
		setVerticalScrollbarOverlay(true);

		mGestureScanner = new GestureDetector(mContext, this);
		mGestureScanner.setIsLongpressEnabled(true);
		mGestureScanner.setOnDoubleTapListener(this);
	}
	
	public void setText(String text, int currVerse, Boolean nightMode, Boolean isBible) {
		mPageLoaded = false;
		String modStyle = isBible ? "bible_style.css" : "book_style.css";
		String html = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\"> "
				+ "<html>\r\n"
				+ "<head>\r\n"
				+ "<meta http-equiv=Content-Type content=\"text/html; charset=UTF-8\">\r\n"
				+ "<script language=\"JavaScript\" src=\"file:///android_asset/reader.js\" type=\"text/javascript\"></script>\r\n"
				+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/reader.css\">"
				+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/" + modStyle + "\">"
				+ getStyle(nightMode)
				+ "</head>\r\n"
				+ "<body" + (currVerse > 1 ? (" onLoad=\"document.location.href='#verse_" + currVerse + "';\"") : "")
				+ ">\r\n"
				+ text
				+ "</body>\r\n" + "</html>";

		loadDataWithBaseURL("file:///url_initial_load", html, "text/html", "UTF-8", "about:config");
		jsInterface.clearSelectedVerse();
	}

	public void setReadingMode(boolean readModeByDefault) {
		isStudyMode = !readModeByDefault;
	}
	
	public boolean isStudyMode() {
		return isStudyMode;
	}

	public boolean isScrollToBottom() {
	    int scrollY = getScrollY();
	    int scrollExtent = computeVerticalScrollExtent();
	    int scrollPos = scrollY + scrollExtent;
	    if (scrollPos >= (computeVerticalScrollRange() - 10)) {
			return true;
		}
		return false;
	}

	public void computeScroll() {
		super.computeScroll();
		if (mPageLoaded && isScrollToBottom()) {
			viewChapterNav();
		}
	}

	public TreeSet<Integer> getSelectVerses() {
		return this.selectedVerse;
	}
	
	public void clearSelectedVerse() {
		jsInterface.clearSelectedVerse();
		((Reader) getContext()).setTextActionVisibility(false);
	}
	
	private String getStyle(Boolean nightMode) {
		String textColor;
		String backColor;
		String selTextColor;
		
		if (!nightMode) {
			textColor = PreferenceHelper.getTextColor();
			selTextColor = "#FEF8C4";
			backColor = PreferenceHelper.getTextBackground();
		} else {
			textColor = "#FFFFFF";
			selTextColor = "#562000";
			backColor = "#000000";
		}
		String textSize = PreferenceHelper.getTextSize();

		StringBuilder style = new StringBuilder();
		style.append("<style type=\"text/css\">\r\n");
		style.append("body {\r\n");
		style.append("padding-bottom: 50px;\r\n");
		style.append("color: " + textColor + ";\r\n");
		style.append("font-size: " + textSize + "pt;\r\n");
		style.append("background: " + backColor + ";\r\n");
		style.append("}\r\n");
		style.append(".verse {\r\n");
		style.append("background: " + backColor + ";\r\n");
		style.append("}\r\n");
		style.append(".selectedVerse {\r\n");
		style.append("background: " + selTextColor + ";\r\n");
		style.append("}\r\n");
		style.append("</style>\r\n");
		
		return style.toString();
	}

	final class webClient extends WebViewClient {
		webClient() {}
		
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Log.i(TAG, "shouldOverrideUrlLoading(" + url + ")");
			return true;
		}	

	    public void onPageFinished(WebView paramWebView, String paramString)
	    {
	      super.onPageFinished(paramWebView, paramString);
	      mPageLoaded = true;
	    }
	}

	private void viewChapterNav() {
		if (!isStudyMode) {
			return;
		}
		Handler mHandler = getHandler();
		mHandler.post(new Runnable() {
			public void run() {
				((Reader) getContext()).viewChapterNav();
			}
		});
	}
	
	public boolean onTouchEvent(MotionEvent event) {
		if (mGestureScanner.onTouchEvent(event)) {
			return true;
		} else if (event == null) {
			return false;
		} else {
			return super.onTouchEvent(event);
		}
	}

	public boolean onSingleTapUp(MotionEvent event) {
		return false;
	}

	public boolean onSingleTapConfirmed(MotionEvent event) {
		int x = (int) event.getX();
		int y = (int) event.getY();
		if (isStudyMode) {
			if (Build.VERSION.SDK_INT < 8) {
				y += getScrollY();
			}
			float density = getContext().getResources().getDisplayMetrics().density;
			x = (int) (x / density);
			y = (int) (y / density);
	
			loadUrl("javascript:handleClick(" + x + ", " + y + ");");
	
			viewChapterNav();
		} else {
			int width = this.getWidth();
			int height = this.getHeight();
			if (((float) y / height) <= 0.33) {
				pageUp(false);
			} else if (((float) y / height) > 0.67) {
				pageDown(false);
			} else if (((float) x / width) <= 0.33) {
				((Reader)getContext()).prevChapter();
			} else if (((float) x / width) > 0.67) {
				((Reader)getContext()).nextChapter();
			}
		}
		return false;
	}
	
	public boolean onDown(MotionEvent event) {
		return false;
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, 
			float velocityX, float velocityY) {
		viewChapterNav();
		return false;
	}

	public void onLongPress(MotionEvent event) {
		if (isStudyMode) {
			viewChapterNav();
		} else {
			((Reader)getContext()).onChooseChapterClick();
		}
	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, 
			float distanceX, float distanceY) {
		return false;
	}

	public void onShowPress(MotionEvent event) {
	}

	public boolean onDoubleTap(MotionEvent event) {
		isStudyMode = !isStudyMode;
		((Reader)getContext()).updateActivityMode();
		if (!isStudyMode) {
			clearSelectedVerse();
		}
		return false;
	}

	public boolean onDoubleTapEvent(MotionEvent event) {
		return false;
	}

	final class chromeClient extends WebChromeClient {
		chromeClient() {}

		public boolean onJsAlert(WebView webView, String url, String message, JsResult result) {
			Log.i(TAG, message);
			if (result != null)
				result.confirm();
			return true;
		}
	}
	
	final class JavaScriptInterface {
		
		public JavaScriptInterface() {
			clearSelectedVerse();
		}
		
		public void clearSelectedVerse() {
			for (Integer verse : selectedVerse) {
				loadUrl("javascript: deselectVerse('verse_" + verse + "');");
			}
			selectedVerse.clear();
		}

		public void onClickVerse(String id) {
			if (!isStudyMode || !id.contains("verse")) {
				return;
			}
			
			Integer verse = Integer.parseInt(id.split("_")[1]);
			if (selectedVerse.contains(verse)) {
				selectedVerse.remove(verse);
				loadUrl("javascript: deselectVerse('verse_" + verse + "');");
			} else {
				selectedVerse.add(verse);
				loadUrl("javascript: selectVerse('verse_" + verse + "');");
			}
			
			Handler mHandler = getHandler();
			mHandler.post(new Runnable() {
				public void run() {
					((Reader) getContext()).setTextActionVisibility(selectedVerse.size() != 0);
				}
			});
		}
		
		public void alert(final String message) {
			Log.i(TAG, "JavaScriptInterface.alert()");
		}
	}
}
