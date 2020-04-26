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
package io.wastelesscorp.platform.app.security.auth.jwt;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static io.wastelesscorp.platform.support.exceptions.ExceptionUtils.silent;

import com.google.common.collect.ImmutableSet;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import reactor.core.publisher.Mono;

/**
 * This converter takes a SignedJWT and extracts all information contained to build an
 * Authentication Object. The signed JWT has already been verified.
 */
public class UsernamePasswordAuthenticationBearer {

  public static Mono<Authentication> create(SignedJWT signedJWT) {
    return Mono.just(signedJWT)
        .map(silent(SignedJWT::getJWTClaimsSet))
        .zipWhen(
            UsernamePasswordAuthenticationBearer::getAuthorities,
            UsernamePasswordAuthenticationBearer::toAuthentication);
  }

  private static UsernamePasswordAuthenticationToken toAuthentication(
      JWTClaimsSet claim, List<SimpleGrantedAuthority> authorities) {
    return new UsernamePasswordAuthenticationToken(claim.getSubject(), null, authorities);
  }

  private static Mono<List<SimpleGrantedAuthority>> getAuthorities(JWTClaimsSet jwtClaimsSet) {
    return Mono.just(jwtClaimsSet)
        .map(s -> s.getClaim("roles"))
        .cast(String.class)
        .flatMapIterable(s -> ImmutableSet.copyOf(s.split(",")))
        .map(SimpleGrantedAuthority::new)
        .collect(toImmutableList());
  }
}
