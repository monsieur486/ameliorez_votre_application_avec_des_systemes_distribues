# Technologies

> Java 17  
> Spring Boot 3.X  
> JUnit 5  

# Prerequisites :

- install java 17
- install maven 3.9.10
- adjust TourGuideConfiguration.java if necessary (default values are ok for dev) 

# How to have gpsUtil, rewardCentral and tripPricer dependencies available ?

- Run : 
> mvn install:install-file -Dfile=libs/gpsUtil.jar -DgroupId=gpsUtil -DartifactId=gpsUtil -Dversion=1.0.0 -Dpackaging=jar  

> mvn install:install-file -Dfile=libs/RewardCentral.jar -DgroupId=rewardCentral -DartifactId=rewardCentral -Dversion=1.0.0 -Dpackaging=jar  

> mvn install:install-file -Dfile=libs/TripPricer.jar -DgroupId=tripPricer -DartifactId=tripPricer -Dversion=1.0.0 -Dpackaging=jar

# How to run application ?

- Run :
> mvn package

> java -jar target/tourguide-0.0.1-SNAPSHOT.jar