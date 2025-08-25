package livemarket.gateway.filter;

import livemarket.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {
    private final JwtProvider jwtProvider;

    AntPathMatcher matcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        String path = exchange.getRequest().getURI().getPath();
        String method = exchange.getRequest().getMethod().name();

        List<String> getWhiteList = List.of(
                "/v1/articles/**",
                "/v1/article-views/**",
                "/v1/hot-articles/**",
                "/v1/comments/articles/**",
                "/v1/comments/infinite-scroll",
                "/v1/article-images/**",
                "/v1/article-likes/**"
        );

        List<String> allMethodWhiteList = List.of(
                "/v1/member/signup",
                "/v1/member/login"
        );

        boolean isWhiteListed = getWhiteList.stream()
                .anyMatch(pattern -> matcher.match(pattern, path) && "GET".equalsIgnoreCase(method))
                ||
                allMethodWhiteList.stream()
                        .anyMatch(pattern -> matcher.match(pattern, path));

        if (isWhiteListed) {
            return chain.filter(exchange);
        }

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);

            if (!jwtProvider.validateToken(token)) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String userId = jwtProvider.getMemberIdFromToken(token);

            ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                    .header("X-User-Id", userId)
                    .build();

            exchange = exchange.mutate().request(modifiedRequest).build();
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
