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

package cc.suvankar.dictionaryapi.configs;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseVerifier {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static Logger LOG = LoggerFactory
            .getLogger(DatabaseVerifier.class);

    @PostConstruct
    public void verifyTables() {
        try {
            jdbcTemplate.queryForObject("SELECT COUNT(*) FROM dictionary_entry", Integer.class);
            LOG.info("✅ Table dictionary_entry exist.");

            jdbcTemplate.queryForObject("SELECT COUNT(*) FROM dictionary_users", Integer.class);
            LOG.info("✅ Table dictionary_users exist.");
        } catch (Exception e) {
            LOG.error("❌ Table not found: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
