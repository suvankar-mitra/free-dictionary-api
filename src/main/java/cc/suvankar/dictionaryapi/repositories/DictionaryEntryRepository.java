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

package cc.suvankar.dictionaryapi.repositories;

import cc.suvankar.dictionaryapi.data.DictionaryEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DictionaryEntryRepository extends JpaRepository<DictionaryEntry, Long> {

    @Query("SELECT e FROM DictionaryEntry e WHERE lower(e.entryWord) = :entryWord")
    List<DictionaryEntry> findByEntryWordIgnoreCase(@Param("entryWord") String entryWord);

    @Query("SELECT e FROM DictionaryEntry e WHERE e.entryWord = :entryWord")
    List<DictionaryEntry> findByEntryWord(String entryWord);

    @Query("SELECT e FROM DictionaryEntry e WHERE lower(e.entryWord) LIKE %:entryWord%")
    List<DictionaryEntry> findByEntryWordContaining(@Param("entryWord") String entryWord);

    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM DictionaryEntry e WHERE LOWER(e.entryWord) = LOWER(:entryWord)")
    boolean existsByEntryWord(String entryWord);

    @Query("SELECT COUNT(e) FROM DictionaryEntry e")
    long countTotalEntries();

    @Query("SELECT e FROM DictionaryEntry e JOIN e.verbMorphologyEntries v WHERE lower(v.conjugatedForm) = :word")
    List<DictionaryEntry> findByVerbMorphologyEntry(@Param("word") String word);
}
