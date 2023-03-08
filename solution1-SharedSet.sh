#!/bin/bash

# Exécute la commande pour générer le jar avec Gradle
./gradlew shadowJar

# Exécute la commande pour lancer le jar avec Java
java -jar ./build/libs/fatJarProjetConcurrent-1.0-all.jar 1