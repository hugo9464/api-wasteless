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
package io.wastelesscorp.platform.app.security.auth.basic;

import io.wastelesscorp.platform.app.security.auth.jwt.JwtTokenService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import reactor.core.publisher.Mono;

/**
 * On success authentication a signed JWT object is serialized and added in the authorization header
 * as a bearer token
 */
public class BasicAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {
    private final JwtTokenService jwtTokenService;

    public BasicAuthenticationSuccessHandler(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    /**
     * A successful authentication object us used to create a JWT object and added in the
     * authorization header of the current WebExchange
     *
     * @param webFilterExchange
     * @param authentication
     * @return
     */
    @Override
    public Mono<Void> onAuthenticationSuccess(
            WebFilterExchange webFilterExchange, Authentication authentication) {
        webFilterExchange
                .getExchange()
                .getResponse()
                .getHeaders()
                .add(HttpHeaders.AUTHORIZATION, getHttpAuthHeaderValue(authentication));

        webFilterExchange.getExchange().getResponse().setStatusCode(HttpStatus.OK);
        return webFilterExchange.getChain().filter(webFilterExchange.getExchange());
    }

    private String getHttpAuthHeaderValue(Authentication authentication) {
        return String.join(" ", "Bearer", tokenFromAuthentication(authentication));
    }

    private String tokenFromAuthentication(Authentication authentication) {
        return jwtTokenService.generateToken(
                authentication.getName(),
                authentication.getCredentials(),
                authentication.getAuthorities());
    }
}
