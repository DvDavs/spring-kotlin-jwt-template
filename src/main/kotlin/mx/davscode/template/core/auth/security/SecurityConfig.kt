package mx.evolutiondev.template.core.auth.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfigurationSource

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
        private val unauthorizedHandler: AuthEntryPointJwt,
        private val accessDeniedHandler: TemplateAccessDeniedHandler,
        private val authFilter: JwtTokenFilter,
        private val corsSource: CorsConfigurationSource
) {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf {
            it.disable()
        }.cors {
            it.configurationSource(corsSource)
        }.sessionManagement {
            it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        }.exceptionHandling {
            it.authenticationEntryPoint(unauthorizedHandler)
            it.accessDeniedHandler(accessDeniedHandler)
        }.authorizeHttpRequests {
            // Public endpoints
            it.requestMatchers("/auth/**").permitAll()
            it.requestMatchers("/public/**").permitAll()
            
            // Swagger/OpenAPI endpoints
            it.requestMatchers(
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/swagger-ui.html",
                "/swagger-resources/**",
                "/webjars/**"
            ).permitAll()
            
            // Protected endpoints
            it.requestMatchers("/admin/administrators/users/**").hasRole("MASTER")
            it.requestMatchers("/admin/**").hasAnyRole("ADMIN", "MASTER")
            it.requestMatchers("/user/**").hasAnyRole("USER", "ADMIN", "MASTER")
            it.requestMatchers("/master/**").hasRole("MASTER")
            it.anyRequest().authenticated()
        }.addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}

