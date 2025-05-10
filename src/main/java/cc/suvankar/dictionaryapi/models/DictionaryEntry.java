/*
 * Copyright (C) 2025 Suvankar Mitra
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package cc.suvankar.dictionaryapi.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DictionaryEntry {
    private String entryWord;
    private String headWord;
    private List<Definition> definitions = new LinkedList<>();
    private Set<String> partsOfSpeech = new LinkedHashSet<>();
    private Synonym synonym = new Synonym();

    private List<VerbMorphologyEntry> verbMorphologyEntries = new LinkedList<>();
    private List<Quote> quotes = new LinkedList<>();
    private String etymology;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DictionaryEntry that = (DictionaryEntry) o;
        return Objects.equals(entryWord, that.entryWord) &&
                Objects.equals(headWord, that.headWord) &&
                Objects.equals(definitions, that.definitions) &&
                Objects.equals(partsOfSpeech, that.partsOfSpeech) &&
                Objects.equals(synonym, that.synonym) &&
                Objects.equals(verbMorphologyEntries, that.verbMorphologyEntries) &&
                Objects.equals(quotes, that.quotes) &&
                Objects.equals(etymology, that.etymology);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entryWord, headWord, definitions, partsOfSpeech, synonym, verbMorphologyEntries, quotes);
    }

}
