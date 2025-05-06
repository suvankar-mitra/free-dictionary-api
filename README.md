# Free Dictionary API

## Overview

This project is a **Java-based tool** designed to parse **GCIDE (The GNU Collaborative International Dictionary of English) XML data** and store it in a **local H2/SQLite database** for efficient querying and usage.

## Features

- Parses and extracts dictionary data from GCIDE XML files.
- Stores structured data in an H2 relational database.
- Provides a foundation for fast lookups and efficient querying.
- Lightweight and easy to integrate into other applications.

## Technologies Used

- **Java** (JDK 11 or later)
- **H2 Database** (for local storage)
- **SQLite Database** (for local storage)
- **DOM API** (for XML parsing)
- **Spring Boot** (optional, for managing database interactions)

## Setup & Installation

### Prerequisites

- Ensure you have **Java 11+** installed.
  - The GCIDE XML dataset is downloaded from [GCIDE Project](https://gcide.gnu.org.ua/), 
    (with slight modification) and copied into resources directory.

### Steps to Run

1. Clone this repository:
   ```sh
   git clone https://github.com/suvankar-mitra/free-dictionary-api.git
   cd free-dictionary-api
   ```
2. Build the project using Maven or Gradle:
   ```sh
   mvnw clean install
   ```
3. Run the application:
   ```sh
   mvnw spring-boot:run
   ```
4. The data will be stored in an **H2 database file** or **SQLite database file**, accessible via **JDBC URL**.

### Example Screenshots of data (h2)
![Alt text](screenshots/H2/jbbc-string.png?raw=true "JDBC connection")
![Alt text](screenshots/H2/query-execution-output.png?raw=true "SQL execution")

## Database Schema

The parsed dictionary data is structured into tables, such as:

- `entries (id, word, definition, part_of_speech, example, etc.)`
- `relations (entry_id, synonym, antonym, related_word, etc.)`

## License
This project is licensed under the **GNU General Public License (GPL)**.

## Contributing

Pull requests are welcome! If you’d like to contribute, please open an issue first to discuss your changes.

---

*Project Repository:* [Free Dictionary API](https://github.com/suvankar-mitra/free-dictionary-api)

*Happy coding!* 🚀

