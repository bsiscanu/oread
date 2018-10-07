package services

import javax.inject.Singleton
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm


@Singleton
class AuthService {

  val secret: String = "how_deep_is_your_love!"

  def sign(app: String): String = {
    Jwts.builder
      .setSubject(app)
      .setIssuer("@domy.io")
      .signWith(SignatureAlgorithm.HS256, secret)
      .compact()
  }

  def check(jws: String, app: String): Boolean = {
    Jwts.parser.setSigningKey(secret).parseClaimsJws(jws).getBody.getSubject == app
  }

}
