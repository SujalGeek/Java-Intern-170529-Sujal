package com.example.Spring_security_Ldap.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.LdapShaPasswordEncoder;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.List;

@Configuration
public class SpringConfig {

    // 1. Context Source: Use the base DN of the LDAP instance, setting the search base to empty ("")
    // to allow absolute paths in the next step.
    @Bean
    public DefaultSpringSecurityContextSource contextSource() {
        return new DefaultSpringSecurityContextSource(
                List.of("ldap://localhost:8389/"), "");
    }

    @Bean
    public AuthenticationManager authenticationManager(DefaultSpringSecurityContextSource contextSource) {

        // LDAP Bind Authenticator
        BindAuthenticator bindAuthenticator = new BindAuthenticator(contextSource);

        // FIX: Define the full DN of the organization (dc=springframework,dc=org) as the search base.
        // Then, the filter must specify the full RELATIVE path from that base.
        bindAuthenticator.setUserSearch(
                new FilterBasedLdapUserSearch(
                        "dc=springframework,dc=org", // Search Base: The highest node that actually exists.
                        "(uid={0})",
                        contextSource
                )
        );

        // LDAP Authorities Populator
        // FIX: The group search is more complex, requiring setting the group search base to the highest
        // existing group OU and specifying the full DN of the member in the filter.
        DefaultLdapAuthoritiesPopulator authoritiesPopulator = new DefaultLdapAuthoritiesPopulator(
                contextSource,
                "ou=groups,dc=springframework,dc=org" // This is the search base for groups
        );
        authoritiesPopulator.setGroupSearchFilter("uniqueMember={0}"); // Note: Removed parentheses
        authoritiesPopulator.setSearchSubtree(true); // Ensure groups are found

        // LDAP Authentication Provider
        LdapAuthenticationProvider provider = new LdapAuthenticationProvider(bindAuthenticator, authoritiesPopulator);

        // Map LDAP user attributes to a Spring Security UserDetails object.
        provider.setUserDetailsContextMapper(new UserDetailsContextMapper() {
            @Override
            public org.springframework.security.core.userdetails.UserDetails mapUserFromContext(
                    org.springframework.ldap.core.DirContextOperations ctx, String username, Collection<? extends GrantedAuthority> authorities) {
                return User.withUsername(username)
                        .password("")
                        .authorities(authorities.toArray(new GrantedAuthority[0]))
                        .build();
            }

            @Override
            public void mapUserToContext(org.springframework.security.core.userdetails.UserDetails user, org.springframework.ldap.core.DirContextAdapter ctx) {
                throw new UnsupportedOperationException("Not supported.");
            }
        });

        return new ProviderManager(provider);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(Customizer.withDefaults())
                .logout(Customizer.withDefaults());

        return http.build();
    }


}
