package dk.gundmann.adminserver.config;

import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import dk.gundmann.adminserver.config.condition.SpringBootAdminInsecureConditional;

@Conditional(SpringBootAdminInsecureConditional.class)
@Configuration
public class InsecureConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                    .anyRequest()
                    .permitAll()
                .and()
                    .csrf()
                    .disable();
    }
}
