package com.openclassrooms.tourguide.user;

/**
 * Préférences de voyage d'un utilisateur, utilisées pour le calcul des offres :
 * durée du séjour, nombre d'adultes et nombre d'enfants.
 *
 * <p><b>Exemple :</b> par défaut, un séjour d'un jour pour un adulte et aucun
 * enfant.</p>
 */
public class UserPreferences {

  private int tripDuration = 1;
  private int numberOfAdults = 1;
  private int numberOfChildren;

  public int getTripDuration() {
    return tripDuration;
  }

  public void setTripDuration(int tripDuration) {
    this.tripDuration = tripDuration;
  }

  public int getNumberOfAdults() {
    return numberOfAdults;
  }

  public void setNumberOfAdults(int numberOfAdults) {
    this.numberOfAdults = numberOfAdults;
  }

  public int getNumberOfChildren() {
    return numberOfChildren;
  }

  public void setNumberOfChildren(int numberOfChildren) {
    this.numberOfChildren = numberOfChildren;
  }

}
