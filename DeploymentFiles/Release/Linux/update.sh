FILE="$PWD"/tourguide-stable-release.jar
if [ -f "$FILE" ]; then
    rm $FILE
fi

wget https://github.com/monsieur486/ameliorez_votre_application_avec_des_systemes_distribues/releases/download/latest/tourguide-stable-release.jar