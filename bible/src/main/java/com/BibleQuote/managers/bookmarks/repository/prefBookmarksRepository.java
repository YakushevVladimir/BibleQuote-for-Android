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

package com.BibleQuote.managers.bookmarks.repository;

import com.BibleQuote.managers.tags.Tag;
import com.BibleQuote.utils.PreferenceHelper;
import com.BibleQuote.managers.bookmarks.Bookmark;

import java.util.ArrayList;

/**
 * User: Vladimir Yakushev
 * Date: 09.04.13
 * Time: 0:26
 */
public class prefBookmarksRepository implements IBookmarksRepository {

	private static final Byte BOOKMARK_DELIMITER = (byte) 0xFE;
	private static final Byte BOOKMARK_PATH_DELIMITER = (byte) 0xFF;
	private static final String FAVORITS = "Favorits";

	@Override
	public long add(Bookmark bookmark) {
		String fav = PreferenceHelper.restoreStateString(FAVORITS);
		PreferenceHelper.saveStateString(FAVORITS, bookmark.humanLink + BOOKMARK_PATH_DELIMITER + bookmark.OSISLink + BOOKMARK_DELIMITER + fav);
		return 0;
	}

	@Override
	public void delete(Bookmark bookmark) {
		String fav = PreferenceHelper.restoreStateString(FAVORITS);
		fav = fav.replaceAll(bookmark.humanLink + "(.)+?" + BOOKMARK_DELIMITER, "");
		PreferenceHelper.saveStateString(FAVORITS, fav);
	}

	@Override
	public void deleteAll() {
		PreferenceHelper.saveStateString(FAVORITS, "");
	}

	@Override
	public ArrayList<Bookmark> getAll() {
		ArrayList<Bookmark> result = new ArrayList<Bookmark>();

		String fav = PreferenceHelper.restoreStateString(FAVORITS);
		if (fav.equals("")) {
			return result;
		}

		String[] favs = fav.split(BOOKMARK_DELIMITER.toString());
		for (String currFav : favs) {
			String[] parts = currFav.split(BOOKMARK_PATH_DELIMITER.toString());
			if (parts.length == 2) result.add(new Bookmark(parts[1], parts[0]));
		}
		return result;
	}

	@Override
	public ArrayList<Bookmark> getAll(Tag tag) {
		return new ArrayList<Bookmark>();
	}
}
