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

package cc.suvankar.dictionaryapi.parser;

import cc.suvankar.dictionaryapi.models.DictionaryEntry;

public interface XmlParser {
    /**
     * Parse each &lt;p&gt; ... &lt;/p&gt; section from the xml which has &lt;ent&gt; tag.
     * @param xmlData pass the text &lt;p&gt; ... &lt;/p&gt;
     * @return Returns a {@link DictionaryEntry}
     */
    DictionaryEntry parseDefinition(String xmlData);

    /**
     * If a quotation is available for the entry word, parse it.
     * @param xmlData Expects &lt;p&gt;&lt;q&gt; ... &lt;/q&gt;&lt;/p&gt; text
     */
    void parseQuote(String xmlData, DictionaryEntry dictionaryEntry);

    /**
     * If another definition is available for the entry word, parse it.
     * @param xmlData Expects &lt;p&gt;&lt;def&gt; ... &lt;/def&gt;&lt;/p&gt; text
     */
    void parseMoreDefinition(String xmlData, DictionaryEntry entry);

    /**
     * If another synonym is available for the entry word, parse it.
     * @param xmlData Expects &lt;p&gt;&lt;syn&gt; ... &lt;/syn&gt;&lt;/p&gt; text
     */
    void parseMoreSynonyms(final String xmlData, final DictionaryEntry entry);
}
