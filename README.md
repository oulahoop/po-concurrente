# Projet Concurrent

## Auteur

**Maël Caubère** : mael.caubere@etu.univ-nantes.fr

## Informations

J'ai créé 2 solutions pour la problématique : 

- Une utilisant un classe SharedSet créé pour avoir Set de Java avec un lock afin d'être Thread Safe.
- Une utilisant ConcurrentHashMap de Java.

Pour plus d'information sur ces 2 soltuions, lisez le fichier [petit_rapport.pdf](https://gitlab.univ-nantes.fr/E204651X/po-concurrente/-/blob/main/petit_rapport.pdf)

Dans le cadre où avoir 2 solutions n'est pas autorisé, la solution que je pense la plus optimisé au problème est la solution2 (ConcurrentHashMap).

## Lancement du projet

Il y a plusieurs façon de lancer mon projet : 

bash :
```bash
./solution1-SharedSet.sh
```

```bash
./solution2-ConcurrentHashMap.sh
```

avec gradle à la racine du projet :
```bash
./gradlew shadowJar
``` 
puis :
```bash
java -jar ./build/libs/fatJarProjetConcurrent-1.0-all.jar 1|2
```
(1|2 est un choix entre la solution1 ou la solution2)

