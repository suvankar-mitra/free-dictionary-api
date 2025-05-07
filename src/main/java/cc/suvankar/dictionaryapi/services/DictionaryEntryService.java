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

package cc.suvankar.dictionaryapi.services;

import cc.suvankar.dictionaryapi.repositories.DictionaryEntryRepository;
import cc.suvankar.dictionaryapi.utils.DictionaryEntryMapper;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class DictionaryEntryService {

    private final DictionaryEntryRepository repository;
    private final DictionaryEntryMapper mapper;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DictionaryEntryService.class);

    public DictionaryEntryService(DictionaryEntryRepository repository, DictionaryEntryMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public cc.suvankar.dictionaryapi.models.DictionaryEntry save(final cc.suvankar.dictionaryapi.models.DictionaryEntry pojo) {

        // // check if same entry is already present, if so skip insert
        // if(repository.existsByEntryWord(pojo.getEntryWord())) {
        // return null;
        // }

        cc.suvankar.dictionaryapi.data.DictionaryEntry entity = mapper.toEntity(pojo);
        cc.suvankar.dictionaryapi.data.DictionaryEntry savedEntity = repository.save(entity);
        return mapper.toPojo(savedEntity);
    }

    @Transactional
    public List<cc.suvankar.dictionaryapi.models.DictionaryEntry> findEntriesByWord(final String word) {
        LOG.info("Finding entries for word {}", word);

        List<cc.suvankar.dictionaryapi.data.DictionaryEntry> entityEntries = repository.findByEntryWord(word);

        if (entityEntries.isEmpty() || entityEntries == null) {
            return null;
        }

        entityEntries.forEach(entry -> Hibernate.initialize(entry.getDefinitions()));
        entityEntries.forEach(entry -> Hibernate.initialize(entry.getPartsOfSpeech()));
        entityEntries.forEach(entry -> Hibernate.initialize(entry.getSynonym()));
        entityEntries.forEach(entry -> Hibernate.initialize(entry.getVerbMorphologyEntries()));
        entityEntries.forEach(entry -> Hibernate.initialize(entry.getQuotes()));

        List<cc.suvankar.dictionaryapi.models.DictionaryEntry> pojoEntries = new LinkedList<>();

        entityEntries.forEach(entity -> pojoEntries.add(mapper.toPojo(entity)));

        return pojoEntries;
    }

    @Transactional
    public long getTotalEntriesCount() {
        LOG.info("Fetching total count of dictionary entries.");
        return repository.countTotalEntries();
    }
}
