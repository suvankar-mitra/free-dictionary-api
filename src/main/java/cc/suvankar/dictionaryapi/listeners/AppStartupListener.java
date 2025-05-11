package cc.suvankar.dictionaryapi.listeners;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import cc.suvankar.dictionaryapi.services.DictionaryEntryService;
import cc.suvankar.dictionaryapi.services.XmlProcessor;

@Component
@Profile("!test")
public class AppStartupListener {

    private static final Logger LOG = LoggerFactory
            .getLogger(AppStartupListener.class);

    @Autowired
    private XmlProcessor xmlProcessor;

    @Autowired
    private DictionaryEntryService dictionaryEntryService;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        LOG.info("Application is ready to serve requests.");
        try {
            // Check if the dictionary is already populated
            LOG.info("Checking if dictionary is already populated...");
            if (isDatabasePopulated()) {
                LOG.info("Dictionary is already populated.");
                return;
            }
            LOG.info("Dictionary is not populated. Parsing files...");
            // Automatically parse all files at startup
            parseAllFiles();
        } catch (Exception e) {
            LOG.error("Error during startup parsing: {}", e.getMessage());
        }
    }

    private boolean isDatabasePopulated() {
        long totalEntries = dictionaryEntryService.getTotalEntriesCount();
        LOG.info("Total dictionary entries found: {}", totalEntries);
        return totalEntries > 124000;
    }

    private void parseAllFiles() {
        Path directory;
        try {
            URI uri = getClass().getClassLoader().getResource("GCIDE").toURI();
            directory = Paths.get(uri);
        } catch (URISyntaxException | NullPointerException e) {
            throw new RuntimeException("Unable to locate GCIDE resource directory", e);
        }

        // Only use half of the available processors for parsing
        // to avoid overloading the system
        // and allow other processes to run smoothly.
        int numberOfThreads = Math.max(1, Runtime.getRuntime().availableProcessors() / 2);
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        try (Stream<Path> files = Files.list(directory)) {
            files.filter(Files::isRegularFile)
                    .map(Path::getFileName)
                    .forEach(filePath -> {
                        String fileName = filePath.toString();
                        if (fileName.startsWith("CIDE") && fileName.endsWith(".xml")) {
                            LOG.info("Starting thread to parse file {}", fileName);
                            executorService.execute(() -> {
                                try {
                                    xmlProcessor.processAndPersistXml(fileName);
                                } catch (Exception e) {
                                    LOG.error("Error processing file {}: {}", fileName, e.getMessage());
                                }
                            });
                        }
                    });
            executorService.shutdown();
            executorService.awaitTermination(Long.MAX_VALUE, java.util.concurrent.TimeUnit.NANOSECONDS);
            LOG.info("Parsing completed");
        } catch (Exception e) {
            LOG.error("Fatal error {}", e.getMessage());
        }

    }
}
