# Technologies

> Java 17  
> Spring Boot 3.X  
> JUnit 5  

# How to have gpsUtil, rewardCentral and tripPricer dependencies available ?

> Run : 
- mvn install:install-file -Dfile=libs/gpsUtil.jar -DgroupId=gpsUtil -DartifactId=gpsUtil -Dversion=1.0.0 -Dpackaging=jar  
- mvn install:install-file -Dfile=libs/RewardCentral.jar -DgroupId=rewardCentral -DartifactId=rewardCentral -Dversion=1.0.0 -Dpackaging=jar  
- mvn install:install-file -Dfile=libs/TripPricer.jar -DgroupId=tripPricer -DartifactId=tripPricer -Dversion=1.0.0 -Dpackaging=jar

# Deployment

## From release

- Download the latest release by using the following link:
  https://github.com/monsieur486/ameliorez_votre_application_avec_des_systemes_distribues/releases/download/latest/tourguide-stable-release.jar
- Run the jar with the following command :
> java -jar tourguide-stable-release.jar

## From docker

- Run the following command :
> docker pull monsieur486/projet08:latest
> 
> docker run -d -p 8080:8080 --name tourguide monsieur486/projet08:latest

or use executable file in DeploymentFiles folder