package com.openclassrooms.tourguide;

import com.openclassrooms.tourguide.dto.AttractionNearbyUserDto;
import com.openclassrooms.tourguide.service.TourGuideService;
import com.openclassrooms.tourguide.user.User;
import com.openclassrooms.tourguide.user.UserReward;
import gpsUtil.location.VisitedLocation;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tripPricer.Provider;

import java.util.List;

/**
 * Contrôleur REST exposant les endpoints du guide touristique TourGuide.
 *
 * <p><b>Exemple :</b> un appel à {@code GET /getLocation?userName=internalUser0}
 * renvoie la dernière localisation connue de l'utilisateur.</p>
 */
@Validated
@RestController
public class TourGuideController {

    private final TourGuideService tourGuideService;

    /**
     * Construit le contrôleur avec le service applicatif TourGuide.
     *
     * @param tourGuideService le service exposant les cas d'usage du guide
     */
    public TourGuideController(TourGuideService tourGuideService) {
        this.tourGuideService = tourGuideService;
    }

    /**
     * Renvoie le message d'accueil de l'application.
     *
     * <p><b>Exemple :</b> {@code GET /} renvoie « Greetings from TourGuide! ».</p>
     *
     * @return le message d'accueil
     */
    @RequestMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }

    /**
     * Renvoie la localisation courante de l'utilisateur.
     *
     * <p><b>Exemple :</b> {@code GET /getLocation?userName=internalUser0} renvoie les
     * coordonnées de la dernière position visitée.</p>
     *
     * @param userName le nom de l'utilisateur recherché
     * @return la localisation visitée de l'utilisateur
     */
    @RequestMapping("/getLocation")
    public VisitedLocation getLocation(@RequestParam String userName) {
        return tourGuideService.getUserLocation(getUser(userName));
    }

    /**
     * Renvoie les cinq attractions les plus proches de l'utilisateur.
     *
     * <p><b>Exemple :</b> {@code GET /getNearbyAttractions?userName=internalUser0}
     * renvoie les cinq attractions les plus proches avec leur distance et leurs points.</p>
     *
     * @param userName le nom de l'utilisateur recherché
     * @return la liste des cinq attractions les plus proches
     */
    @RequestMapping("/getNearbyAttractions")
    public List<AttractionNearbyUserDto> getNearbyAttractions(@RequestParam @NotBlank String userName) {
        User user = getUser(userName);
        VisitedLocation visitedLocation = tourGuideService.getUserLocation(user);
        return tourGuideService.getNearByAttractions(visitedLocation, user);
    }

    /**
     * Renvoie les récompenses accumulées par l'utilisateur.
     *
     * <p><b>Exemple :</b> {@code GET /getRewards?userName=internalUser0} renvoie la liste
     * des récompenses obtenues lors de la visite d'attractions.</p>
     *
     * @param userName le nom de l'utilisateur recherché
     * @return la liste des récompenses de l'utilisateur
     */
    @RequestMapping("/getRewards")
    public List<UserReward> getRewards(@RequestParam String userName) {
        return tourGuideService.getUserRewards(getUser(userName));
    }

    /**
     * Renvoie les offres de voyage proposées à l'utilisateur.
     *
     * <p><b>Exemple :</b> {@code GET /getTripDeals?userName=internalUser0} renvoie la liste
     * des fournisseurs et des prix adaptés à ses points de récompense.</p>
     *
     * @param userName le nom de l'utilisateur recherché
     * @return la liste des offres de voyage des fournisseurs
     */
    @RequestMapping("/getTripDeals")
    public List<Provider> getTripDeals(@RequestParam String userName) {
        return tourGuideService.getTripDeals(getUser(userName));
    }

    // Récupère l'utilisateur correspondant au nom fourni via le service.
    private User getUser(String userName) {
        return tourGuideService.getUser(userName);
    }


}
