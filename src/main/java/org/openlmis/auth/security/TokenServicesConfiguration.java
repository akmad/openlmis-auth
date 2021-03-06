/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2017 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Affero General Public License for more details. You should have received a copy of
 * the GNU Affero General Public License along with this program. If not, see
 * http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.auth.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;

@Configuration
@EnableAuthorizationServer
public class TokenServicesConfiguration {

  private static final Logger LOGGER = LoggerFactory.getLogger(TokenServicesConfiguration.class);

  @Autowired
  private TokenStore tokenStore;

  @Autowired
  @Qualifier("clientDetailsServiceImpl")
  private ClientDetailsService clientDetailsService;

  @Autowired
  private AuthenticationManager authenticationManager;

  @Value("${token.validitySeconds}")
  private Integer tokenValiditySeconds;

  @Bean
  public TokenEnhancer tokenEnhancer() {
    return new AccessTokenEnhancer();
  }

  /**
   * Default token services bean initializer.
   * @return custom token services
   */
  @Primary
  @Bean
  public DefaultTokenServices defaultTokenServices() {
    LOGGER.debug("Using {} seconds as the token validity time", tokenValiditySeconds);

    DefaultTokenServices tokenServices = new CustomTokenServices();
    tokenServices.setTokenStore(tokenStore);
    tokenServices.setSupportRefreshToken(true);
    tokenServices.setClientDetailsService(clientDetailsService);
    tokenServices.setTokenEnhancer(tokenEnhancer());
    tokenServices.setAccessTokenValiditySeconds(tokenValiditySeconds);
    tokenServices.setAuthenticationManager(authenticationManager);

    return tokenServices;
  }
}
