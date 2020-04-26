/*
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.wastelesscorp.platform.app.security.auth.bearer;

import io.wastelesscorp.platform.app.security.auth.jwt.AuthorizationHeaderPayload;
import io.wastelesscorp.platform.app.security.auth.jwt.JwtTokenService;
import io.wastelesscorp.platform.app.security.auth.jwt.UsernamePasswordAuthenticationBearer;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * This converter extracts a bearer token from a WebExchange and returns an Authentication object if
 * the JWT token is valid. Validity means is well formed and signature is correct.
 */
public class ServerHttpBearerAuthenticationConverter implements ServerAuthenticationConverter {
    private static final String BEARER = "Bearer ";

    private final JwtTokenService jwtVerifier;

    public ServerHttpBearerAuthenticationConverter(JwtTokenService jwtTokenService) {
        this.jwtVerifier = jwtTokenService;
    }

    /**
     * Apply this function to the current WebExchange, an Authentication object is returned when
     * completed.
     *
     * @param exchange
     * @return
     */
    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return Mono.justOrEmpty(exchange)
                .flatMap(AuthorizationHeaderPayload::extract)
                .filter(authValue -> authValue.length() > BEARER.length())
                .map(authValue -> authValue.substring(BEARER.length()))
                .flatMap(jwtVerifier::verifyToken)
                .flatMap(UsernamePasswordAuthenticationBearer::create)
                .log();
    }
}
