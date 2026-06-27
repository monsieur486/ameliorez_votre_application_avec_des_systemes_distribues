package com.openclassrooms.tourguide;

import gpsUtil.GpsUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import rewardCentral.RewardCentral;

/**
 * Configuration Spring déclarant les beans des bibliothèques externes (GpsUtil et
 * RewardCentral), injectés ensuite dans les services applicatifs annotés
 * {@code @Service}.
 *
 * <p><b>Exemple :</b> Spring injecte le {@link GpsUtil} et le {@link RewardCentral}
 * produits ici dans le service de récompenses.</p>
 */
@Configuration
public class TourGuideModule {

  /**
   * Fournit le bean {@link GpsUtil}.
   *
   * <p><b>Exemple :</b> utilisé par les services pour obtenir attractions et
   * localisations.</p>
   *
   * @return une instance de GpsUtil
   */
  @Bean
  public GpsUtil gpsUtil() {
    return new GpsUtil();
  }

  /**
   * Fournit le bean {@link RewardCentral}.
   *
   * <p><b>Exemple :</b> utilisé pour obtenir les points de récompense d'une
   * attraction.</p>
   *
   * @return une instance de RewardCentral
   */
  @Bean
  public RewardCentral rewardCentral() {
    return new RewardCentral();
  }

}
