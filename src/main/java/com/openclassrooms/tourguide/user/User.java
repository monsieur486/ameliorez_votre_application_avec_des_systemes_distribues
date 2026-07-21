package com.openclassrooms.tourguide.user;

import gpsUtil.location.VisitedLocation;
import tripPricer.Provider;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Utilisateur du guide touristique et son état : identité, positions, récompenses et préférences.
 *
 * <p><b>Exemple :</b> un utilisateur interne « internalUser0 » agrège son historique de
 * localisations, ses récompenses et ses offres de voyage.</p>
 */
public class User {
    private final UUID userId;
    private final String userName;
    private String phoneNumber;
    private String emailAddress;
    private Date latestLocationTimestamp;
    private final List<VisitedLocation> visitedLocations = new CopyOnWriteArrayList<>();
    private final List<UserReward> userRewards = new CopyOnWriteArrayList<>();
    private UserPreferences userPreferences = new UserPreferences();
    private List<Provider> tripDeals = new ArrayList<>();

    /**
     * Construit un utilisateur avec son identité.
     *
     * @param userId       l'identifiant unique de l'utilisateur
     * @param userName     le nom de l'utilisateur
     * @param phoneNumber  le numéro de téléphone
     * @param emailAddress l'adresse e-mail
     */
    public User(UUID userId, String userName, String phoneNumber, String emailAddress) {
        this.userId = userId;
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
    }

    /**
     * Renvoie l'identifiant unique de l'utilisateur.
     *
     * @return l'identifiant de l'utilisateur
     */
    public UUID getUserId() {
        return userId;
    }

    /**
     * Renvoie le nom de l'utilisateur.
     *
     * @return le nom de l'utilisateur
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Renvoie le numéro de téléphone de l'utilisateur.
     *
     * @return le numéro de téléphone
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Définit le numéro de téléphone de l'utilisateur.
     *
     * @param phoneNumber le numéro de téléphone
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Définit l'adresse e-mail de l'utilisateur.
     *
     * @param emailAddress l'adresse e-mail
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    /**
     * Renvoie l'adresse e-mail de l'utilisateur.
     *
     * @return l'adresse e-mail
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * Renvoie l'horodatage de la dernière localisation connue.
     *
     * @return l'horodatage de la dernière localisation
     */
    public Date getLatestLocationTimestamp() {
        return latestLocationTimestamp;
    }

    /**
     * Définit l'horodatage de la dernière localisation connue.
     *
     * @param latestLocationTimestamp l'horodatage de la dernière localisation
     */
    public void setLatestLocationTimestamp(Date latestLocationTimestamp) {
        this.latestLocationTimestamp = latestLocationTimestamp;
    }

    /**
     * Ajoute une localisation à l'historique de l'utilisateur.
     *
     * @param visitedLocation la localisation visitée à ajouter
     */
    public void addToVisitedLocations(VisitedLocation visitedLocation) {
        visitedLocations.add(visitedLocation);
    }

    /**
     * Renvoie l'historique des localisations visitées.
     *
     * @return la liste des localisations visitées
     */
    public List<VisitedLocation> getVisitedLocations() {
        return visitedLocations;
    }

    /**
     * Vide l'historique des localisations visitées.
     */
    public void clearVisitedLocations() {
        visitedLocations.clear();
    }

    /**
     * Ajoute une récompense à l'utilisateur si elle ne concerne pas une attraction déjà récompensée.
     *
     * @param userReward la récompense à ajouter
     */
    public void addUserReward(UserReward userReward) {
        boolean alreadyExists = userRewards.stream()
            .anyMatch(r -> r.attraction.attractionName.equals(userReward.attraction.attractionName));
        if (!alreadyExists) {
            userRewards.add(userReward);
        }
    }

    /**
     * Renvoie les récompenses de l'utilisateur.
     *
     * @return la liste des récompenses
     */
    public List<UserReward> getUserRewards() {
        return userRewards;
    }

    /**
     * Renvoie les préférences de voyage de l'utilisateur.
     *
     * @return les préférences de l'utilisateur
     */
    public UserPreferences getUserPreferences() {
        return userPreferences;
    }

    /**
     * Définit les préférences de voyage de l'utilisateur.
     *
     * @param userPreferences les préférences à affecter
     */
    public void setUserPreferences(UserPreferences userPreferences) {
        this.userPreferences = userPreferences;
    }

    /**
     * Renvoie la dernière localisation visitée par l'utilisateur.
     *
     * @return la dernière localisation visitée
     */
    public VisitedLocation getLastVisitedLocation() {
        return visitedLocations.get(visitedLocations.size() - 1);
    }

    /**
     * Renvoie les offres de voyage associées à l'utilisateur.
     *
     * @return la liste des offres de voyage
     */
    public List<Provider> getTripDeals() {
        return tripDeals;
    }

    /**
     * Définit les offres de voyage associées à l'utilisateur.
     *
     * @param tripDeals la liste des offres de voyage
     */
    public void setTripDeals(List<Provider> tripDeals) {
        this.tripDeals = tripDeals;
    }

}
