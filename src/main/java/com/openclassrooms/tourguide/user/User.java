package com.openclassrooms.tourguide.user;

import gpsUtil.location.VisitedLocation;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Utilisateur du service TourGuide : identité, historique des localisations
 * visitées, récompenses gagnées et préférences de voyage.
 *
 * <p><b>Exemple :</b> {@code new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com")}
 * crée un utilisateur sans localisation ni récompense.</p>
 */
public class User {
  private final UUID userId;
  private final String userName;
  private String phoneNumber;
  private String emailAddress;
  private final List<VisitedLocation> visitedLocations = new CopyOnWriteArrayList<>();
  private final List<UserReward> userRewards = new CopyOnWriteArrayList<>();
  private UserPreferences userPreferences = new UserPreferences();

  /**
   * Construit un utilisateur.
   *
   * <p><b>Exemple :</b> {@code new User(id, "jon", "000", "jon@tourGuide.com")}.</p>
   *
   * @param userId       identifiant unique de l'utilisateur
   * @param userName     nom d'utilisateur (clé fonctionnelle)
   * @param phoneNumber  numéro de téléphone
   * @param emailAddress adresse e-mail
   */
  public User(UUID userId, String userName, String phoneNumber, String emailAddress) {
    this.userId = userId;
    this.userName = userName;
    this.phoneNumber = phoneNumber;
    this.emailAddress = emailAddress;
  }

  public UUID getUserId() {
    return userId;
  }

  public String getUserName() {
    return userName;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getEmailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  /**
   * Ajoute une localisation à l'historique des lieux visités.
   *
   * <p><b>Exemple :</b> {@code user.addToVisitedLocations(localisation)} ajoute la
   * localisation en fin d'historique.</p>
   *
   * @param visitedLocation localisation visitée à mémoriser
   */
  public void addToVisitedLocations(VisitedLocation visitedLocation) {
    visitedLocations.add(visitedLocation);
  }

  public List<VisitedLocation> getVisitedLocations() {
    return visitedLocations;
  }

  /**
   * Ajoute une récompense uniquement si aucune n'existe déjà pour la même attraction.
   *
   * <p><b>Exemple :</b> deux appels avec la même attraction n'enregistrent qu'une
   * seule récompense.</p>
   *
   * <p>{@code synchronized} rend le contrôle-puis-ajout atomique : cela évite les
   * doublons si le même utilisateur est mis à jour depuis plusieurs threads.</p>
   *
   * @param userReward récompense candidate à ajouter
   */
  public synchronized void addUserReward(UserReward userReward) {
    boolean alreadyExists = userRewards.stream()
            .anyMatch(r -> r.attraction.attractionName.equals(userReward.attraction.attractionName));
    if (!alreadyExists) {
      userRewards.add(userReward);
    }
  }

  public List<UserReward> getUserRewards() {
    return userRewards;
  }

  public UserPreferences getUserPreferences() {
    return userPreferences;
  }

  public void setUserPreferences(UserPreferences userPreferences) {
    this.userPreferences = userPreferences;
  }

  /**
   * Retourne la dernière localisation visitée.
   *
   * <p><b>Exemple :</b> après deux ajouts, {@code getLastVisitedLocation()} retourne
   * le second ; un appel sur un historique vide lève
   * {@link IndexOutOfBoundsException}.</p>
   *
   * @return la localisation la plus récemment ajoutée
   * @throws IndexOutOfBoundsException si aucune localisation n'a été visitée
   */
  public VisitedLocation getLastVisitedLocation() {
    return visitedLocations.get(visitedLocations.size() - 1);
  }

}
