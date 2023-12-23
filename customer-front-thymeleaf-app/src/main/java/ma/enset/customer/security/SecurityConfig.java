package ma.enset.customer.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.web.SecurityFilterChain;

import java.util.*;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) //pour protéger les méthodes
public class SecurityConfig {
    private ClientRegistrationRepository clientRegistrationRepository;

    public SecurityConfig(ClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(Customizer.withDefaults()) //html rendu coté serveur -> auth statefull-> Cette ligne configure la protection CSRF par défaut. Elle génère et vérifie les jetons CSRF pour prévenir les attaques de type CSRF.
                .authorizeHttpRequests(ar->ar.requestMatchers("/","/oauth2Login/**","/webjars/**","/h2-console/**").permitAll()) //autorise l'accès à certaines URL sans nécessiter d'authentification.
                .authorizeHttpRequests(ar->ar.anyRequest().authenticated()) //spécifie que toutes les autres requêtes (anyRequest()) doivent être authentifiées.
                .headers(h->h.frameOptions(fo->fo.disable())) //X-Frame-Options est un en-tête HTTP qui permet à un site web de contrôler si ses pages peuvent être chargées dans un <frame>, <iframe>, <embed>, ou <object>.
                .csrf(csrf->csrf.ignoringRequestMatchers("/h2-console/**"))//ignorer la protection CSRF (Cross-Site Request Forgery)
                .oauth2Login(
                        //Cette ligne configure le support de connexion via OAuth2
                        //il cherche dans fichier.proprties les providers
                        al->al.loginPage("/oauth2Login").defaultSuccessUrl("/",true)
                        //Définit l'URL personnalisée vers laquelle l'utilisateur sera redirigé pour se connecter via OAuth2.
                )
                //pour logout + form html
                //Cette partie configure le processus de déconnexion (logout)
                .logout((logout) -> logout
                    .logoutSuccessHandler(oidcLogoutSuccessHandler())
                    .logoutSuccessUrl("/").permitAll()
                    .clearAuthentication(true)
                    .deleteCookies("JSESSIONID")
                )
                .exceptionHandling(eh->eh.accessDeniedPage("/notAutorized"))//rediriger les utilisateurs vers la page "/notAuthorized" en cas d'accès refusé (Access Denied).
                .build();
    }

    //il prend l'objet client registration repository qui contient les providers ( google, github et keycloak)
    private OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler() {
        final OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler =
                new OidcClientInitiatedLogoutSuccessHandler(this.clientRegistrationRepository);
        oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}?logoutsuccess=true");
        return oidcLogoutSuccessHandler;
    }

    @Bean
    public GrantedAuthoritiesMapper userAuthoritiesMapper() {
        return (authorities) -> {
            final Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
            authorities.forEach((authority) -> {
                if (authority instanceof OidcUserAuthority oidcAuth) {
                    mappedAuthorities.addAll(mapAuthorities(oidcAuth.getIdToken().getClaims()));
                    System.out.println(oidcAuth.getAttributes());
                } else if (authority instanceof OAuth2UserAuthority oauth2Auth) {
                    mappedAuthorities.addAll(mapAuthorities(oauth2Auth.getAttributes()));
                }
            });
            return mappedAuthorities;
        };
    }
    private List<SimpleGrantedAuthority> mapAuthorities(final Map<String, Object> attributes) {
        final Map<String, Object> realmAccess = ((Map<String, Object>)attributes.getOrDefault("realm_access", Collections.emptyMap()));
        final Collection<String> roles = ((Collection<String>)realmAccess.getOrDefault("roles", Collections.emptyList()));
        System.out.println("roles\n "+roles.toString());
        return roles.stream()
                .map((role) -> new SimpleGrantedAuthority(role))
                .toList();
    }
}
