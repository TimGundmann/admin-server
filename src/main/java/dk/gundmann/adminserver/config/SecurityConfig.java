package dk.gundmann.adminserver.config;

import de.codecentric.boot.admin.server.config.AdminServerProperties;
import dk.gundmann.adminserver.config.condition.SpringBootAdminSecureConditional;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.web.reactive.function.client.WebClient;

@Conditional(SpringBootAdminSecureConditional.class)
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String REDIRECT_TO = "redirectTo";
    private static final String ASSETS = "/assets/**";
    private static final String LOGIN = "/login";
    private static final String LOGOUT = "/logout";
    private final String adminContextPath;

    public SecurityConfig(AdminServerProperties adminServerProperties) {
        this.adminContextPath = adminServerProperties.getContextPath();
    }

    @Bean
    public WebClient createWebClient() throws SSLException {
        SslContext sslContext = SslContextBuilder
            .forClient()
            .trustManager(InsecureTrustManagerFactory.INSTANCE)
            .build();
        HttpClient httpClient = HttpClient.create()
            .secure(t -> t.sslContext(sslContext));
        return WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .build();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
        successHandler.setTargetUrlParameter(REDIRECT_TO);

        http.authorizeRequests().antMatchers(adminContextPath + ASSETS).permitAll()
                .antMatchers(adminContextPath + LOGIN).permitAll().requestMatchers(EndpointRequest.toAnyEndpoint())
                .permitAll().anyRequest().authenticated().and().formLogin().loginPage(adminContextPath + LOGIN)
                .successHandler(successHandler).and().logout().logoutUrl(adminContextPath + LOGOUT).and().httpBasic()
                .and().csrf().disable();
    }
}