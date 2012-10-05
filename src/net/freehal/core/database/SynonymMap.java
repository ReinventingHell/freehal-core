/*******************************************************************************
 * Copyright (c) 2006 - 2012 Tobias Schulz and Contributors.
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/gpl.html>.
 ******************************************************************************/
package net.freehal.core.database;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.freehal.core.util.StringUtils;
import net.freehal.core.xml.Word;

public class SynonymMap implements net.freehal.core.xml.SynonymProvider {

	List<Set<String>> map = new ArrayList<Set<String>>();

	public Collection<String> getSynonyms(final String text) {
		for (final Set<String> synonyms : map) {
			if (synonyms.contains(text))
				return synonyms;
		}
		return new HashSet<String>();
	}

	public Collection<Word> getSynonyms(final Word word) {
		Collection<String> strings = getSynonyms(word.getWord());

		List<Word> words = new ArrayList<Word>();
		for (String str : strings) {
			words.add(new Word(word, str, null));
		}
		return words;
	}

	public void add(String word1, String word2) {
		Integer index1 = null;
		Integer index2 = null;
		for (int i = 0; i < map.size(); ++i) {
			if (map.get(i).contains(word1))
				index1 = i;
			if (map.get(i).contains(word2))
				index2 = i;

		}
		if (index1 == null && index2 == null) {
			Set<String> synonyms = new HashSet<String>();
			synonyms.add(word1);
			synonyms.add(word2);
			map.add(synonyms);
		} else if (index1 != null && index2 == null) {
			map.get(index1).add(word2);
		} else if (index1 == null && index2 != null) {
			map.get(index2).add(word1);
		} else if (index1 != null && index2 != null) {
			map.get(index1).addAll(map.get(index2));
			map.remove(index2);
		}

	}

	public void write() {
		DiskStorage.getCacheDirectory("database", "synonyms").getChild("synonyms.csv").write(this.print());
	}

	private String print() {
		StringBuilder sb = new StringBuilder();
		for (final Set<String> synonyms : map) {
			sb.append(StringUtils.join("|", synonyms)).append("\n");
		}
		return sb.toString();
	}

	public void read() {
		Iterable<String> lines = DiskStorage.getCacheDirectory("database", "synonyms")
				.getChild("synonyms.csv").readLines();
		for (String line : lines) {
			String[] csv = line.split("[|]");
			Set<String> set = new HashSet<String>();
			Collections.addAll(set, csv);
			map.add(set);
		}
	}
}
