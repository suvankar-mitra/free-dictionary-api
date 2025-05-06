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

package cc.suvankar.dictionaryapi.utils;

import org.springframework.stereotype.Component;

@Component
public class DictionaryEntryMapper {
    public cc.suvankar.dictionaryapi.data.DictionaryEntry toEntity(cc.suvankar.dictionaryapi.models.DictionaryEntry pojo) {
        cc.suvankar.dictionaryapi.data.DictionaryEntry entity = new cc.suvankar.dictionaryapi.data.DictionaryEntry();
        entity.setEntryWord(pojo.getEntryWord());
        entity.setHeadWord(pojo.getHeadWord());

        for(cc.suvankar.dictionaryapi.models.Definition pojoDef : pojo.getDefinitions()) {
            cc.suvankar.dictionaryapi.data.Definition entityDef = new cc.suvankar.dictionaryapi.data.Definition();
            entityDef.setDefinition(pojoDef.getDefinition());
            entityDef.setMark(pojoDef.getMark());
            entityDef.setSource(pojoDef.getSource());
            entity.getDefinitions().add(entityDef);
        }

        entity.setPartsOfSpeech(pojo.getPartsOfSpeech());

        cc.suvankar.dictionaryapi.data.Synonym entitySyn = new cc.suvankar.dictionaryapi.data.Synonym();
        entitySyn.setSource(pojo.getSynonym().getSource());
        entitySyn.setSynonymList(pojo.getSynonym().getSynonymList());
        entity.setSynonym(entitySyn);

        for(cc.suvankar.dictionaryapi.models.VerbMorphologyEntry pojoVerb: pojo.getVerbMorphologyEntries()) {
            cc.suvankar.dictionaryapi.data.VerbMorphologyEntry entityVerb = new cc.suvankar.dictionaryapi.data.VerbMorphologyEntry();
            entityVerb.setConjugatedForm(pojoVerb.getConjugatedForm());
            entityVerb.getPartsOfSpeech().addAll(pojoVerb.getPartsOfSpeech());
            entity.getVerbMorphologyEntries().add(entityVerb);
        }

        for(cc.suvankar.dictionaryapi.models.Quote pojoQuote : pojo.getQuotes()) {
            cc.suvankar.dictionaryapi.data.Quote entityQuote = new cc.suvankar.dictionaryapi.data.Quote();
            entityQuote.setSource(pojoQuote.getSource());
            entityQuote.setAuthor(pojoQuote.getAuthor());
            entityQuote.setText(pojoQuote.getText());
            entity.getQuotes().add(entityQuote);
        }

        return entity;
    }

    public cc.suvankar.dictionaryapi.models.DictionaryEntry toPojo(cc.suvankar.dictionaryapi.data.DictionaryEntry pojo) {
        cc.suvankar.dictionaryapi.models.DictionaryEntry entity = new cc.suvankar.dictionaryapi.models.DictionaryEntry();
        entity.setEntryWord(pojo.getEntryWord());
        entity.setHeadWord(pojo.getHeadWord());

        for(cc.suvankar.dictionaryapi.data.Definition pojoDef : pojo.getDefinitions()) {
            cc.suvankar.dictionaryapi.models.Definition entityDef = new cc.suvankar.dictionaryapi.models.Definition();
            entityDef.setDefinition(pojoDef.getDefinition());
            entityDef.setMark(pojoDef.getMark());
            entityDef.setSource(pojoDef.getSource());
            entity.getDefinitions().add(entityDef);
        }

        entity.setPartsOfSpeech(pojo.getPartsOfSpeech());

        cc.suvankar.dictionaryapi.models.Synonym entitySyn = new cc.suvankar.dictionaryapi.models.Synonym();
        entitySyn.setSource(pojo.getSynonym().getSource());
        entitySyn.setSynonymList(pojo.getSynonym().getSynonymList());
        entity.setSynonym(entitySyn);

        for(cc.suvankar.dictionaryapi.data.VerbMorphologyEntry pojoVerb: pojo.getVerbMorphologyEntries()) {
            cc.suvankar.dictionaryapi.models.VerbMorphologyEntry entityVerb = new cc.suvankar.dictionaryapi.models.VerbMorphologyEntry();
            entityVerb.setConjugatedForm(pojoVerb.getConjugatedForm());
            entityVerb.getPartsOfSpeech().addAll(pojoVerb.getPartsOfSpeech());
            entity.getVerbMorphologyEntries().add(entityVerb);
        }

        for(cc.suvankar.dictionaryapi.data.Quote pojoQuote : pojo.getQuotes()) {
            cc.suvankar.dictionaryapi.models.Quote entityQuote = new cc.suvankar.dictionaryapi.models.Quote();
            entityQuote.setSource(pojoQuote.getSource());
            entityQuote.setAuthor(pojoQuote.getAuthor());
            entityQuote.setText(pojoQuote.getText());
            entity.getQuotes().add(entityQuote);
        }

        return entity;
    }
}
