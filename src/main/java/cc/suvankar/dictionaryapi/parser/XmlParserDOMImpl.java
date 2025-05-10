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

import cc.suvankar.dictionaryapi.constants.Abbreviations;
import cc.suvankar.dictionaryapi.exceptions.XmlParserException;
import cc.suvankar.dictionaryapi.models.Definition;
import cc.suvankar.dictionaryapi.models.DictionaryEntry;
import cc.suvankar.dictionaryapi.models.Quote;
import cc.suvankar.dictionaryapi.models.VerbMorphologyEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;

@Component("xmlParserDOMImpl")
public class XmlParserDOMImpl implements XmlParser {

    private static final Logger LOG = LoggerFactory
            .getLogger(XmlParserDOMImpl.class);

    @Override
    public DictionaryEntry parseDefinition(String xmlData) {

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringComments(true); // Ignore comments
            factory.setExpandEntityReferences(false); // Prevent entity expansion

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new org.xml.sax.InputSource(new java.io.StringReader(xmlData)));
            document.getDocumentElement().normalize();

            DictionaryEntry entry = new DictionaryEntry();

            // <ent>
            if (document.getElementsByTagName("ent").getLength() > 0) {
                String entTokenValue = document.getElementsByTagName("ent").item(0).getTextContent()
                        .replaceAll("\n", " ")
                        .trim();
                        //.toLowerCase(Locale.ENGLISH);
                entry.setEntryWord(entTokenValue);
            } else {
                // no entry available, just return from here
                return null;
            }

            // <hw>
            if (document.getElementsByTagName("hw").getLength() > 0) {
                String hwRaw = document.getElementsByTagName("hw").item(0).getTextContent();
                String hwClean = normalizeHeadword(hwRaw);
                entry.setHeadWord(hwClean);
            }


            // <def>
            if (document.getElementsByTagName("def").getLength() > 0) {

                String defTokenValue = document.getElementsByTagName("def").item(0).getTextContent().replaceAll("\n", " ").trim();

                // Clean known XML-like artifacts
                defTokenValue = defTokenValue.replaceAll("\\{ldquo/\\}", "\"");
                defTokenValue = defTokenValue.replaceAll("\\{rdquo/\\}", "\"");

                Definition definition = new Definition();
                definition.setDefinition(defTokenValue);

                // <source>
                if (document.getElementsByTagName("source").getLength() > 0) {
                    String sourceTokenValue = document.getElementsByTagName("source").item(0).getTextContent()
                            .replaceAll("\n", " ");
                    definition.setSource(sourceTokenValue);
                }

                entry.getDefinitions().add(definition);
            }

            // <ety>
            if (document.getElementsByTagName("ety").getLength() > 0) {
                String etyTokenValue = document.getElementsByTagName("ety").item(0).getTextContent().replaceAll("\n", " ").trim();
                entry.setEtymology(etyTokenValue);
            }

            // <pos>
            NodeList posNodes = document.getElementsByTagName("pos");

            for (int i = 0; i < posNodes.getLength(); i++) {
                Node posNode = posNodes.item(i);

                // Skip if this <pos> is inside a <def> tag
                if (isInsideDef(posNode)) {
                    continue;
                }

                String posTokenValue = posNode.getTextContent().replaceAll("\n", " ");
                List<String> posAbbrList = List.of(posTokenValue.split("&"));
                posAbbrList.forEach(abbr -> {
                    String mapped = Abbreviations.ABBREVIATION_MAP.get(abbr.trim());
                    if (mapped != null) {
                        entry.getPartsOfSpeech().add(mapped);
                    }
                });
            }

            // <syn>
            if (document.getElementsByTagName("syn").getLength() > 0) {
                String synTokenValue = document.getElementsByTagName("syn").item(0).getTextContent().replaceAll("\n",
                        " ");

                // ignore Syn. --
                synTokenValue = synTokenValue.replaceAll("Syn. --[ ]*", "");

                entry.getSynonym().getSynonymList().addAll(List.of(synTokenValue.split(",")));
            }

            // <vmorph>
            if (document.getElementsByTagName("vmorph").getLength() > 0) {

                Element vmorphTag = (Element) document.getElementsByTagName("vmorph").item(0);

                for (int i = 0; i < vmorphTag.getElementsByTagName("conjf").getLength(); i++) {
                    VerbMorphologyEntry morphologyEntry = new VerbMorphologyEntry();
                    morphologyEntry.setConjugatedForm(vmorphTag.getElementsByTagName("conjf").item(i).getTextContent());
                    if (vmorphTag.getElementsByTagName("pos").getLength() > i) {
                        String posTokenValue2 = vmorphTag.getElementsByTagName("pos").item(i).getTextContent()
                                .replaceAll("\n", " ");
                        List<String> posAbbrList2 = List.of(posTokenValue2.split("&"));
                        posAbbrList2.forEach(abbr -> morphologyEntry.getPartsOfSpeech()
                                .add(Abbreviations.ABBREVIATION_MAP.get(abbr.trim())));
                    }
                    entry.getVerbMorphologyEntries().add(morphologyEntry);
                }
            }

            return entry;

        } catch (ParserConfigurationException | IOException | SAXException e) {
            LOG.error(xmlData);
            throw new XmlParserException(e.getMessage(), e);
        }
    }

    @Override
    public void parseQuote(final String xmlData, final DictionaryEntry entry) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringComments(true); // Ignore comments
            factory.setExpandEntityReferences(false); // Prevent entity expansion

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new org.xml.sax.InputSource(new java.io.StringReader(xmlData)));
            document.getDocumentElement().normalize();

            if (document.getElementsByTagName("q").getLength() > 0) {
                String quoteTagValue = document.getElementsByTagName("q").item(0).getTextContent().trim();

                Quote quote = new Quote();
                quote.setText(quoteTagValue);

                if (document.getElementsByTagName("qau").getLength() > 0) {
                    String authorValue = document.getElementsByTagName("qau").item(0).getTextContent()
                            .replaceAll("\n", " ").trim();
                    quote.setAuthor(authorValue);
                }

                if (document.getElementsByTagName("source").getLength() > 0) {
                    String sourceValue = document.getElementsByTagName("source").item(0).getTextContent()
                            .replaceAll("\n", " ").trim();
                    quote.setSource(sourceValue);
                }

                entry.getQuotes().add(quote);
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            LOG.error(xmlData);
            throw new XmlParserException(e.getMessage(), e);
        }
    }

    @Override
    public void parseMoreDefinition(final String xmlData, final DictionaryEntry entry) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringComments(true); // Ignore comments
            factory.setExpandEntityReferences(false); // Prevent entity expansion

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new org.xml.sax.InputSource(new java.io.StringReader(xmlData)));
            document.getDocumentElement().normalize();

            if (document.getElementsByTagName("sn").getLength() > 0) {
                if (document.getElementsByTagName("def").getLength() > 0) {
                    Definition definition = new Definition();
                    definition.setDefinition(
                            document.getElementsByTagName("def").item(0).getTextContent()
                                    .replaceAll("\n", " ")
                                    .trim());

                    if (document.getElementsByTagName("mark").getLength() > 0) {
                        definition.setMark(
                                document.getElementsByTagName("mark").item(0).getTextContent()
                                        .replaceAll("\n", " ")
                                        .trim());
                    }

                    if (document.getElementsByTagName("source").getLength() > 0) {
                        definition.setSource(
                                document.getElementsByTagName("source").item(0).getTextContent()
                                        .replaceAll("\n", " ")
                                        .trim());
                    }

                    definition.setDefinition(definition.getDefinition().replaceAll("\\{ldquo/\\}", "\""));
                    definition.setDefinition(definition.getDefinition().replaceAll("\\{rdquo/\\}", "\""));

                    entry.getDefinitions().add(definition);
                }
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            LOG.error(xmlData);
            throw new XmlParserException(e.getMessage(), e);
        }
    }

    @Override
    public void parseMoreSynonyms(final String xmlData, final DictionaryEntry entry) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringComments(true); // Ignore comments
            factory.setExpandEntityReferences(false); // Prevent entity expansion

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new org.xml.sax.InputSource(new java.io.StringReader(xmlData)));
            document.getDocumentElement().normalize();

            if (document.getElementsByTagName("syn").getLength() > 0) {
                String synTokenValue = document.getElementsByTagName("syn").item(0).getTextContent()
                        .replaceAll("\n", " ");

                // ignore Syn. --
                synTokenValue = synTokenValue.replaceAll("Syn. -- ", "")
                        .replaceAll("\\.", "");

                entry.getSynonym().getSynonymList().addAll(List.of(synTokenValue.split("; ")));

                if (document.getElementsByTagName("source").getLength() > 0) {
                    entry.getSynonym().setSource(document.getElementsByTagName("source").item(0)
                            .getTextContent().trim());
                }
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            LOG.error(xmlData);
            throw new XmlParserException(e.getMessage(), e);
        }
    }

    private String normalizeHeadword(String rawHeadword) {
        return rawHeadword
                .replace("\\\"", "\"")  // in case of escape chars
                .replace("\"", "·")
                .replace("*", "·")
                .replace("`", "")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private boolean isInsideDef(Node node) {
        Node parent = node.getParentNode();
        while (parent != null) {
            if (parent.getNodeName().equalsIgnoreCase("def")) {
                return true;
            }
            parent = parent.getParentNode();
        }
        return false;
    }
    
    
}
