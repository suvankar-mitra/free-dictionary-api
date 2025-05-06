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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import cc.suvankar.dictionaryapi.data.Users;
import cc.suvankar.dictionaryapi.services.CustomUserDetailsService;

@Configuration
public class AdminUserInitializationConfig {

    @Value("${dict_admin_user:admin}")
    private String adminUser;

    @Value("${dict_admin_password:admin}")
    private String adminPassword;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private Environment env;

    private static final Logger LOG = LoggerFactory.getLogger(AdminUserInitializationConfig.class.getName());

    @Bean
    public CommandLineRunner initAdminUser() {
        return args -> {
            Users user = new Users();

            adminUser = env.getProperty("dict_admin_user");
            adminPassword = env.getProperty("dict_admin_password");

            if (adminUser == null || adminPassword == null) {
                LOG.error("Admin user or password not found in application.yaml or Environment variables");
                return;
            }

            try {
                customUserDetailsService.loadUserByUsername(adminUser);
                LOG.info("Admin user already exists with username: {}", adminUser);
                return;
            } catch (UsernameNotFoundException e) {
                LOG.info("Admin user does not exist. Creating one with username: {}", adminUser);
                user.setUsername(adminUser);
                user.setPassword(SecurityConfig.passwordEncoder().encode(adminPassword));
                user.setRole("ROLE_ADMIN");

                Users admin = customUserDetailsService.saveUser(user);
                LOG.info("Admin user created with username: {} and password: {}", admin.getUsername(),
                        admin.getPassword());
            }
        };
    }

}
