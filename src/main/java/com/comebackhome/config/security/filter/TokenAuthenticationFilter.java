package com.comebackhome.config.security.filter;

import com.comebackhome.authentication.domain.repository.TokenRepository;
import com.comebackhome.authentication.domain.service.TokenProvider;
import com.comebackhome.common.exception.security.TokenAuthenticationFilterException;
import com.comebackhome.common.exception.user.UserNotFoundException;
import com.comebackhome.config.security.dto.UserPrincipal;
import com.comebackhome.user.domain.User;
import com.comebackhome.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            String jwt = getJwtFromRequest(request);

            if (isValidToken(jwt)) {
                String email = tokenProvider.getUserEmailFromToken(jwt);
                User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException());
                UserPrincipal userPrincipal = UserPrincipal.from(user);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (RedisConnectionFailureException e) {
            throw new RedisConnectionFailureException("redis 커넥션에 실패했습니다.");
        } catch(Exception e){
            throw new TokenAuthenticationFilterException(e);
        }

        filterChain.doFilter(request, response);

    }

    private boolean isValidToken(String jwt) {
        return StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)
                && !tokenRepository.existsLogoutAccessTokenById(jwt) && !tokenRepository.existsLogoutRefreshTokenById(jwt);

    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(TokenProvider.TOKEN_TYPE)) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
