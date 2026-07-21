package com.openclassrooms.tourguide;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import gpsUtil.GpsUtil;
import rewardCentral.RewardCentral;
import com.openclassrooms.tourguide.service.RewardsService;

/**
 * Configuration Spring déclarant les beans partagés du module TourGuide.
 *
 * <p><b>Exemple :</b> fournit les beans {@code GpsUtil}, {@code RewardsService} et
 * {@code RewardCentral} injectés dans les services de l'application.</p>
 */
@Configuration
public class TourGuideModule {

    /**
     * Fournit le bean d'accès aux données de géolocalisation.
     *
     * <p><b>Exemple :</b> injecté dans TourGuideService pour récupérer la position des
     * utilisateurs et la liste des attractions.</p>
     *
     * @return une instance de GpsUtil
     */
    @Bean
    public GpsUtil getGpsUtil() {
        return new GpsUtil();
    }

    /**
     * Fournit le service de calcul des récompenses.
     *
     * <p><b>Exemple :</b> injecté dans TourGuideService pour attribuer les points liés
     * aux attractions visitées.</p>
     *
     * @return une instance de RewardsService configurée avec GpsUtil et RewardCentral
     */
    @Bean
    public RewardsService getRewardsService() {
        return new RewardsService(getGpsUtil(), getRewardCentral());
    }

    /**
     * Fournit le bean d'attribution des points de récompense.
     *
     * <p><b>Exemple :</b> utilisé par RewardsService pour calculer les points d'une
     * attraction donnée pour un utilisateur.</p>
     *
     * @return une instance de RewardCentral
     */
    @Bean
    public RewardCentral getRewardCentral() {
        return new RewardCentral();
    }

}
