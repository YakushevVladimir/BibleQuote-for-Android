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
package com.BibleQuote.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.webkit.*;
import com.BibleQuote.listeners.IReaderViewListener;
import com.BibleQuote.utils.PreferenceHelper;

import java.util.ArrayList;
import java.util.TreeSet;

@SuppressLint("SetJavaScriptEnabled")
public class ReaderWebView extends WebView
		implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

	static final String TAG = "ReaderWebView";
	private static final String COLOR = "color: ";
	private static final String BACKGROUND = "background: ";
	private static final String CHAR_SET = "}\r\n";
	private GestureDetector mGestureScanner;
	private JavaScriptInterface jsInterface;
	private Mode currMode = Mode.Read;
	
	private ArrayList<IReaderViewListener> listeners = new ArrayList<IReaderViewListener>();

	protected TreeSet<Integer> selectedVerse = new TreeSet<Integer>();

	public boolean mPageLoaded;

	@SuppressLint("AddJavascriptInterface")
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

	public TreeSet<Integer> getSelectedVerses() {
		return this.selectedVerse;
	}

	public void setSelectedVerse(TreeSet<Integer> selectedVerse) {
		jsInterface.clearSelectedVerse();
		this.selectedVerse = selectedVerse;
		for (Integer verse : selectedVerse) {
			jsInterface.selectVerse(verse);
		}
	}

	public void gotoVerse(int verse) {
		jsInterface.gotoVerse(verse);
	}

	public enum Mode {
		Read, Study, Speak
	}

	public void setMode(Mode mode) {
		currMode = mode;
		if (currMode != Mode.Study) {
			clearSelectedVerse();
		}
		notifyListeners(IReaderViewListener.ChangeCode.onChangeReaderMode);
	}

	public Mode getMode() {
		return currMode;
	}

	public void setOnReaderViewListener(IReaderViewListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	private void notifyListeners(IReaderViewListener.ChangeCode code) {
		for (IReaderViewListener listener : listeners) {
			listener.onReaderViewChange(code);
		}
	}

	public void setText(String baseUrl, String text, int currVerse, Boolean nightMode, Boolean isBible) {
		mPageLoaded = false;
		String modStyle = isBible ? "bible_style.css" : "book_style.css";

		StringBuilder html = new StringBuilder();
		html.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">\r\n")
				.append("<html>\r\n")
				.append("<head>\r\n")
				.append("<meta http-equiv=Content-Type content=\"text/html; charset=UTF-8\">\r\n")
				.append("<script language=\"JavaScript\" src=\"file:///android_asset/reader.js\" type=\"text/javascript\"></script>\r\n")
				.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/").append(modStyle).append("\">\r\n")
				.append(getStyle(nightMode))
				.append("</head>\r\n")
				.append("<body").append(currVerse > 1 ? (" onLoad=\"document.location.href='#verse_" + currVerse + "';\"") : "").append(">\r\n")
				.append(text)
				.append("</body>\r\n")
				.append("</html>");

		loadDataWithBaseURL("file://" + baseUrl, html.toString(), "text/html", "UTF-8", "about:config");
		jsInterface.clearSelectedVerse();
	}

	public boolean isScrollToBottom() {
		int scrollY = getScrollY();
		int scrollExtent = computeVerticalScrollExtent();
		int scrollPos = scrollY + scrollExtent;
		return (scrollPos >= (computeVerticalScrollRange() - 10));
	}

	public void computeScroll() {
		super.computeScroll();
		if (mPageLoaded && isScrollToBottom()) {
			notifyListeners(IReaderViewListener.ChangeCode.onScroll);
		}
	}

	public void clearSelectedVerse() {
		if (selectedVerse.size() == 0) {
			return;
		}
		jsInterface.clearSelectedVerse();
		if (currMode == Mode.Study) {
			notifyListeners(IReaderViewListener.ChangeCode.onChangeSelection);
		}
	}

	private String getStyle(Boolean nightMode) {
		String textColor;
		String backColor;
		String selTextColor;
		String selTextBack;

		getSettings().setStandardFontFamily(PreferenceHelper.getFontFamily());

		if (!nightMode) {
			backColor = PreferenceHelper.getTextBackground();
			textColor = PreferenceHelper.getTextColor();
			selTextColor = PreferenceHelper.getTextColorSelected();
			selTextBack = PreferenceHelper.getTextBackgroundSelected();
		} else {
			textColor = "#EEEEEE";
			backColor = "#000000";
			selTextColor = "#EEEEEE";
			selTextBack = "#562000";
		}
		String textSize = PreferenceHelper.getTextSize();

		StringBuilder style = new StringBuilder();
		style.append("<style type=\"text/css\">\r\n")
				.append("body {\r\n")
				.append("padding-bottom: 50px;\r\n");
		if (PreferenceHelper.textAlignJustify()) {
			style.append("text-align: justify;\r\n");
		}
		style.append(COLOR).append(textColor).append(";\r\n")
				.append("font-size: ").append(textSize).append("pt;\r\n")
				.append("line-height: 1.25;\r\n")
				.append(BACKGROUND).append(backColor).append(";\r\n")
				.append(CHAR_SET)
				.append(".verse {\r\n")
				.append(BACKGROUND).append(backColor).append(";\r\n")
				.append(CHAR_SET)
				.append(".selectedVerse {\r\n")
				.append(COLOR).append(selTextColor).append(";\r\n")
				.append(BACKGROUND).append(selTextBack).append(";\r\n")
				.append(CHAR_SET)
				.append("img {\r\n")
				.append("max-width: 100%;\r\n")
				.append(CHAR_SET)
				.append("</style>\r\n");

		return style.toString();
	}

	final class webClient extends WebViewClient {
		webClient() {
		}

		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Log.i(TAG, "shouldOverrideUrlLoading(" + url + ")");
			return true;
		}

		public void onPageFinished(WebView paramWebView, String paramString) {
			super.onPageFinished(paramWebView, paramString);
			mPageLoaded = true;
		}
	}

	public boolean onTouchEvent(MotionEvent event) {
		return mGestureScanner.onTouchEvent(event) || (event != null && super.onTouchEvent(event));
	}

	public boolean onSingleTapUp(MotionEvent event) {
		return false;
	}

	public boolean onSingleTapConfirmed(MotionEvent event) {
		int x = (int) event.getX();
		int y = (int) event.getY();
		if (currMode == Mode.Study) {
			if (Build.VERSION.SDK_INT < 8) {
				y += getScrollY();
			}
			float density = getContext().getResources().getDisplayMetrics().density;
			x = (int) (x / density);
			y = (int) (y / density);

			loadUrl("javascript:handleClick(" + x + ", " + y + ");");
			notifyListeners(IReaderViewListener.ChangeCode.onChangeSelection);
		} else if (currMode == Mode.Read) {
			int width = this.getWidth();
			int height = this.getHeight();

			if (((float) y / height) <= 0.33) {
				notifyListeners(IReaderViewListener.ChangeCode.onUpNavigation);
			} else if (((float) y / height) > 0.67) {
				notifyListeners(IReaderViewListener.ChangeCode.onDownNavigation);
			} else if (((float) x / width) <= 0.33) {
				notifyListeners(IReaderViewListener.ChangeCode.onLeftNavigation);
			} else if (((float) x / width) > 0.67) {
				notifyListeners(IReaderViewListener.ChangeCode.onRightNavigation);
			}
		}
		return false;
	}

	public boolean onDown(MotionEvent event) {
		return false;
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2,
						   float velocityX, float velocityY) {
		notifyListeners(IReaderViewListener.ChangeCode.onScroll);
		return false;
	}

	public void onLongPress(MotionEvent event) {
		notifyListeners(IReaderViewListener.ChangeCode.onLongPress);
	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2,
							float distanceX, float distanceY) {
		notifyListeners(IReaderViewListener.ChangeCode.onScroll);
		return false;
	}

	public void onShowPress(MotionEvent event) {
	}

	public boolean onDoubleTap(MotionEvent event) {
		if (currMode != Mode.Speak) {
			setMode(currMode == Mode.Study ? Mode.Read : Mode.Study);
		}
		return false;
	}

	public boolean onDoubleTapEvent(MotionEvent event) {
		return false;
	}

	final class chromeClient extends WebChromeClient {
		chromeClient() {
		}

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

        @JavascriptInterface
		public void clearSelectedVerse() {
            for (Integer verse : selectedVerse) {
                deselectVerse(verse);
            }
			selectedVerse.clear();
		}

        @JavascriptInterface
        public void onClickVerse(String id) {
			if (currMode != Mode.Study || !id.contains("verse")) {
				return;
			}

			try {
                Integer verse = Integer.parseInt(id.split("_")[1]);
                if (selectedVerse.contains(verse)) {
                    selectedVerse.remove(verse);
                    deselectVerse(verse);
                } else {
                    selectedVerse.add(verse);
                    selectVerse(verse);
                }

                try {
                    Handler mHandler = getHandler();
                    mHandler.post(new Runnable() {
                        public void run() {
                            notifyListeners(IReaderViewListener.ChangeCode.onChangeSelection);
                        }
                    });
                } catch (NullPointerException e) {
                    Log.e(TAG, "Error when notifying clients ReaderWebView");
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
		}

        private void deselectVerse(final Integer verse) {
            ReaderWebView.this.post(new Runnable() {
                @Override
                public void run() {
                    loadUrl("javascript: deselectVerse('verse_" + verse + "');");
                }
            });
        }

        private void selectVerse(final int verse) {
            ReaderWebView.this.post(new Runnable() {
                @Override
                public void run() {
                    loadUrl("javascript: selectVerse('verse_" + verse + "');");
                }
            });
		}

		public void gotoVerse(int verse) {
			loadUrl("javascript: gotoVerse(" + verse + ");");
		}

		public void alert(final String message) {
			Log.i(TAG, "JavaScriptInterface.alert()");
		}
	}
}
