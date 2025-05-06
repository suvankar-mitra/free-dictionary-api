-- for concurrency
PRAGMA journal_mode = WAL;

-- SQLite equivalent of schema-h2.sql

-----------------------------
-- Cleanup: Drop existing tables
-----------------------------
DROP TABLE IF EXISTS definition;
DROP TABLE IF EXISTS synonyms;
DROP TABLE IF EXISTS dictionary_entry;
DROP TABLE IF EXISTS dictionary_entry_parts_of_speech;
DROP TABLE IF EXISTS quote;
DROP TABLE IF EXISTS verb_morphology_entry;
DROP TABLE IF EXISTS verb_morphology_entry_parts_of_speech;
DROP TABLE IF EXISTS dictionary_users;

-----------------------------
-- Table Definitions
-----------------------------
CREATE TABLE dictionary_entry (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    entry_word TEXT,
    head_word TEXT,
    source TEXT
);

CREATE TABLE synonyms (
    dictionary_entry_id INTEGER,
    synonym TEXT,
    FOREIGN KEY (dictionary_entry_id) REFERENCES dictionary_entry(id)
);

CREATE TABLE dictionary_entry_parts_of_speech (
    dictionary_entry_id INTEGER,
    parts_of_speech TEXT,
    FOREIGN KEY (dictionary_entry_id) REFERENCES dictionary_entry(id)
);

CREATE TABLE definition (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    dictionary_entry_id INTEGER,
    mark TEXT,
    source TEXT,
    definition TEXT,
    FOREIGN KEY (dictionary_entry_id) REFERENCES dictionary_entry(id)
);

CREATE TABLE quote (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    dictionary_entry_id INTEGER,
    author TEXT,
    source TEXT,
    text TEXT,
    FOREIGN KEY (dictionary_entry_id) REFERENCES dictionary_entry(id)
);

CREATE TABLE verb_morphology_entry (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    dictionary_entry_id INTEGER,
    conjugated_form TEXT,
    FOREIGN KEY (dictionary_entry_id) REFERENCES dictionary_entry(id)
);

CREATE TABLE verb_morphology_entry_parts_of_speech (
    verb_morphology_entry_id INTEGER,
    parts_of_speech TEXT,
    FOREIGN KEY (verb_morphology_entry_id) REFERENCES verb_morphology_entry(id)
);

CREATE TABLE dictionary_users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL,
    password TEXT NOT NULL,
    role TEXT DEFAULT 'USER'
);

-----------------------------
-- Enable Foreign Key Constraints
-----------------------------
PRAGMA foreign_keys = ON;