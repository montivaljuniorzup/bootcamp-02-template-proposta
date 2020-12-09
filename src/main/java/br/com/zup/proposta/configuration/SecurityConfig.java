package br.com.zup.proposta.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests(authorizeRequests ->
                authorizeRequests
                        .antMatchers(HttpMethod.GET, "/v1/propostas/**").hasAuthority("propostas:read")
                        .antMatchers(HttpMethod.POST, "/v1/propostas/**").hasAuthority("propostas:write")
                        .antMatchers(HttpMethod.GET, "/v1/biometrias/**").hasAuthority("propostas:read")
                        .antMatchers(HttpMethod.POST, "/v1/biometrias/**").hasAuthority("propostas:write")
                        .anyRequest()
                        .authenticated()
        )
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
    }
}
