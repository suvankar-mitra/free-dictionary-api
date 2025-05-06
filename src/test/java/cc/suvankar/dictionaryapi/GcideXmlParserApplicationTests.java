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

package cc.suvankar.dictionaryapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import cc.suvankar.dictionaryapi.data.DictionaryEntry;
import cc.suvankar.dictionaryapi.repositories.DictionaryEntryRepository;
import cc.suvankar.dictionaryapi.services.XmlProcessor;

@SpringBootTest(classes = FreeDictionaryApiApplication.class)
@ActiveProfiles("test")
class FreeDictionaryApiApplicationTests {

	@Autowired
	private DictionaryEntryRepository dictionaryEntryRepository;

	@Autowired
	private XmlProcessor xmlProcessor;

	@Test
	void testRetrieveDictionaryEntry() {

		xmlProcessor.processAndPersistXml("CIDE.A.xml");

		List<DictionaryEntry> entries = dictionaryEntryRepository.findByEntryWord("abandon");
		assertEquals("abandon", entries.get(0).getEntryWord().toLowerCase(Locale.ENGLISH).trim());
	}

}
