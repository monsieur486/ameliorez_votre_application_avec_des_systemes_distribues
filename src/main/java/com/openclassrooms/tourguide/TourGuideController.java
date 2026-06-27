package com.openclassrooms.tourguide;

import com.openclassrooms.tourguide.dto.AttractionNearbyUserDto;
import com.openclassrooms.tourguide.service.TourGuideService;
import com.openclassrooms.tourguide.user.User;
import com.openclassrooms.tourguide.user.UserReward;
import gpsUtil.location.VisitedLocation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tripPricer.Provider;

import java.util.List;

/**
 * Contrôleur REST exposant les fonctionnalités de TourGuide : localisation,
 * attractions proches, récompenses et offres de voyage.
 *
 * <p><b>Exemple :</b> {@code GET /getLocation?userName=internalUser0} retourne la
 * localisation courante de l'utilisateur.</p>
 */
@RestController
public class TourGuideController {

  private final TourGuideService tourGuideService;

  /**
   * Construit le contrôleur avec son service.
   *
   * <p><b>Exemple :</b> instancié par Spring via injection par constructeur.</p>
   *
   * @param tourGuideService service métier de TourGuide
   */
  public TourGuideController(TourGuideService tourGuideService) {
    this.tourGuideService = tourGuideService;
  }

  /**
   * Message d'accueil de l'API.
   *
   * <p><b>Exemple :</b> {@code GET /} retourne « Greetings from TourGuide! ».</p>
   *
   * @return le message d'accueil
   */
  @GetMapping("/")
  public String index() {
    return "Greetings from TourGuide!";
  }

  /**
   * Retourne la localisation courante d'un utilisateur.
   *
   * <p><b>Exemple :</b> {@code GET /getLocation?userName=jon}.</p>
   *
   * @param userName nom de l'utilisateur
   * @return la localisation courante
   */
  @GetMapping("/getLocation")
  public VisitedLocation getLocation(@RequestParam String userName) {
    return tourGuideService.getUserLocation(tourGuideService.getUser(userName));
  }

  /**
   * Retourne les cinq attractions les plus proches d'un utilisateur, avec pour
   * chacune : nom et coordonnées de l'attraction, coordonnées de l'utilisateur,
   * distance en miles et points de récompense.
   *
   * <p><b>Exemple :</b> {@code GET /getNearbyAttractions?userName=jon} retourne
   * cinq attractions triées par distance croissante.</p>
   *
   * @param userName nom de l'utilisateur
   * @return les cinq attractions les plus proches
   */
  @GetMapping("/getNearbyAttractions")
  public List<AttractionNearbyUserDto> getNearbyAttractions(@RequestParam String userName) {
    User user = tourGuideService.getUser(userName);
    VisitedLocation visitedLocation = tourGuideService.getUserLocation(user);
    return tourGuideService.getNearByAttractions(visitedLocation, user);
  }

  /**
   * Retourne les récompenses d'un utilisateur.
   *
   * <p><b>Exemple :</b> {@code GET /getRewards?userName=jon}.</p>
   *
   * @param userName nom de l'utilisateur
   * @return la liste des récompenses
   */
  @GetMapping("/getRewards")
  public List<UserReward> getRewards(@RequestParam String userName) {
    return tourGuideService.getUserRewards(tourGuideService.getUser(userName));
  }

  /**
   * Retourne les offres de voyage d'un utilisateur.
   *
   * <p><b>Exemple :</b> {@code GET /getTripDeals?userName=jon} retourne dix offres.</p>
   *
   * @param userName nom de l'utilisateur
   * @return la liste des offres
   */
  @GetMapping("/getTripDeals")
  public List<Provider> getTripDeals(@RequestParam String userName) {
    return tourGuideService.getTripDeals(tourGuideService.getUser(userName));
  }

}
