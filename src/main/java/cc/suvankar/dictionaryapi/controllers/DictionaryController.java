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

package cc.suvankar.dictionaryapi.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cc.suvankar.dictionaryapi.models.DictionaryEntry;
import cc.suvankar.dictionaryapi.services.DictionaryEntryService;

@RestController
@RequestMapping("/api/v1")
public class DictionaryController {

    private static final Logger LOG = LoggerFactory.getLogger(DictionaryController.class);

    @Autowired
    private DictionaryEntryService dictionaryEntryService;

    @GetMapping("/definitions")
    public ResponseEntity<List<DictionaryEntry>> getDefinitions(@RequestParam String word) {
        LOG.info("Getting definitions for word {}", word);

        List<DictionaryEntry> dictionaryEntries = dictionaryEntryService.findEntriesByWord(word);

        if (dictionaryEntries == null || dictionaryEntries.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(dictionaryEntries);

    }

}
