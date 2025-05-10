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

import cc.suvankar.dictionaryapi.exceptions.XmlProcessorException;
import cc.suvankar.dictionaryapi.models.DictionaryEntry;
import cc.suvankar.dictionaryapi.parser.XmlParser;
import cc.suvankar.dictionaryapi.utils.FileReaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class XmlProcessor {
    private static final Logger LOG = LoggerFactory
            .getLogger(XmlProcessor.class);

    private final XmlParser xmlParser;
    private final DictionaryEntryService dbService;

    public XmlProcessor(XmlParser xmlParser,
            DictionaryEntryService dbService) {
        this.xmlParser = xmlParser;
        this.dbService = dbService;
    }

    public void processAndPersistXml(String fileName) {
        LOG.info("Parsing file {}", fileName);
        try {
            String xmlFileContent = FileReaderUtil.readFile(fileName);

            int pTagIndexStart = 0, pTagIndexEnd = 0;
            String pTagContent;
            DictionaryEntry prevEntry = null;

            List<DictionaryEntry> dictionaryEntries = new LinkedList<>();

            while ((pTagIndexStart = xmlFileContent.indexOf("<p>", pTagIndexStart)) != -1) {
                pTagIndexEnd = xmlFileContent.indexOf("</p>", pTagIndexStart + 3);
                if (pTagIndexEnd == -1)
                    break;

                pTagContent = xmlFileContent.substring(pTagIndexStart, pTagIndexEnd + 4);

                int pTagStartCount, pTagEndCount;
                pTagStartCount = countOccurrences(pTagContent, "<p>");
                pTagEndCount = countOccurrences(pTagContent, "</p>");

                // System.out.println(pTagContent);
                // System.out.println(pTagStartCount+" "+pTagEndCount);

                // more <p> tag inside <p></p> tag, keep reading
                while (pTagEndCount != pTagStartCount) {
                    pTagIndexEnd = xmlFileContent.indexOf("</p>", pTagIndexEnd + 4);
                    pTagContent = xmlFileContent.substring(pTagIndexStart, pTagIndexEnd + 4);
                    pTagStartCount = countOccurrences(pTagContent, "<p>");
                    pTagEndCount = countOccurrences(pTagContent, "</p>");
                }

                // pre processing to ignore some tags
                pTagContent = pTagContent
                        .replaceAll("(?s)<!--.*?-->", "") // remove all the comments
                        .replaceAll("<\\?/", "")// replace all (<?/) with ''
                        .replaceAll("<ldquo/", "\"")
                        .replaceAll("<rdquo/", "\"")
                        .replaceAll("<lsquo/", "'")
                        .replaceAll("<rsquo/", "'")
                        .replaceAll("<lt/", "&lt")
                        .replaceAll("<gt/", "&gt")
                        .replaceAll("<(\\w+)/", "{$1/}") // replace all <acr/ like patterns with /{acr/}
                        .replaceAll("(?i)\\{br\\s*/?}", "")
                        .replaceAll("&(?!\\w+;)", "&amp;") // ignore & characters
                        .replaceAll("<cs>", "").replaceAll("</cs>", "")
                        .replaceAll("<note>", "").replaceAll("</note>", "");

                Pattern incompleteCommentTagPattern = Pattern.compile("<p>.*?<!--(?!.*?-->).*?</p>",
                        Pattern.DOTALL);
                Matcher matcher = incompleteCommentTagPattern.matcher(pTagContent);
                if (matcher.find()) {
                    pTagIndexStart = pTagIndexEnd;
                    continue;
                }

                // get the definition
                DictionaryEntry entry = xmlParser.parseDefinition(pTagContent);
                if (entry != null) {
                    dictionaryEntries.add(entry);
                    prevEntry = entry;
                } else {
                    if (prevEntry != null) {
                        xmlParser.parseQuote(pTagContent, prevEntry);
                        xmlParser.parseMoreDefinition(pTagContent, prevEntry);
                        xmlParser.parseMoreSynonyms(pTagContent, prevEntry);
                    }
                }

                pTagIndexStart = pTagIndexEnd;
            }

            LOG.info("Parsing of file {} is complete, now persisting into Database.", fileName);
            // parsing is done, now persist into database
            saveIntoDatabaseMultiThread(dictionaryEntries);

            LOG.info("Database persist of {} complete.", fileName);

        } catch (IOException | URISyntaxException e) {
            throw new XmlProcessorException(e.getMessage(), e);
        }
    }

    private void saveIntoDatabaseMultiThread(List<DictionaryEntry> dictionaryEntries) {
        if (dictionaryEntries.isEmpty()) {
            LOG.warn("No entries to save into database.");
            return;
        }
        LOG.info("Saving {} entries into database.", dictionaryEntries.size());
        // Only use half of the available processors for parsing
        // to avoid overloading the system
        // and allow other processes to run smoothly.
        int numberOfThreads = Math.max(1, Runtime.getRuntime().availableProcessors() / 2);
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        for (DictionaryEntry entry : dictionaryEntries) {
            executorService.submit(() -> dbService.save(entry));
        }

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.MINUTES)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private static int countOccurrences(String str, String sub) {
        int count = 0, index = 0;

        while ((index = str.indexOf(sub, index)) != -1) {
            count++;
            index += sub.length();
        }

        return count;
    }
}
