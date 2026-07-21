package com.openclassrooms.tourguide.user;

/**
 * Préférences de voyage d'un utilisateur utilisées pour rechercher des offres adaptées.
 *
 * <p><b>Exemple :</b> nombre d'adultes, d'enfants et durée du séjour transmis à
 * TripPricer pour proposer des offres personnalisées.</p>
 */
public class UserPreferences {

    private int attractionProximity = Integer.MAX_VALUE;
    private int tripDuration = 1;
    private int ticketQuantity = 1;
    private int numberOfAdults = 1;
    private int numberOfChildren = 0;

    /**
     * Construit des préférences aux valeurs par défaut.
     */
    public UserPreferences() {
    }

    /**
     * Définit le rayon de proximité recherché autour des attractions.
     *
     * @param attractionProximity le rayon de proximité
     */
    public void setAttractionProximity(int attractionProximity) {
        this.attractionProximity = attractionProximity;
    }

    /**
     * Renvoie le rayon de proximité recherché autour des attractions.
     *
     * @return le rayon de proximité
     */
    public int getAttractionProximity() {
        return attractionProximity;
    }

    /**
     * Renvoie la durée du séjour souhaitée.
     *
     * @return la durée du séjour
     */
    public int getTripDuration() {
        return tripDuration;
    }

    /**
     * Définit la durée du séjour souhaitée.
     *
     * @param tripDuration la durée du séjour
     */
    public void setTripDuration(int tripDuration) {
        this.tripDuration = tripDuration;
    }

    /**
     * Renvoie le nombre de billets souhaité.
     *
     * @return le nombre de billets
     */
    public int getTicketQuantity() {
        return ticketQuantity;
    }

    /**
     * Définit le nombre de billets souhaité.
     *
     * @param ticketQuantity le nombre de billets
     */
    public void setTicketQuantity(int ticketQuantity) {
        this.ticketQuantity = ticketQuantity;
    }

    /**
     * Renvoie le nombre d'adultes du voyage.
     *
     * @return le nombre d'adultes
     */
    public int getNumberOfAdults() {
        return numberOfAdults;
    }

    /**
     * Définit le nombre d'adultes du voyage.
     *
     * @param numberOfAdults le nombre d'adultes
     */
    public void setNumberOfAdults(int numberOfAdults) {
        this.numberOfAdults = numberOfAdults;
    }

    /**
     * Renvoie le nombre d'enfants du voyage.
     *
     * @return le nombre d'enfants
     */
    public int getNumberOfChildren() {
        return numberOfChildren;
    }

    /**
     * Définit le nombre d'enfants du voyage.
     *
     * @param numberOfChildren le nombre d'enfants
     */
    public void setNumberOfChildren(int numberOfChildren) {
        this.numberOfChildren = numberOfChildren;
    }

}
