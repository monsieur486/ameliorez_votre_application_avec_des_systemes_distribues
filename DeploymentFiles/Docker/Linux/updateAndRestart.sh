docker-compose stop
docker-compose pull
docker image prune -f
docker container prune -f
docker-compose up --build -d --scale tourguide=5