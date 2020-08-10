# CellarManager

You can find an english readme [here](https://github.com/AntoninRuan/CaveManager/tree/master#english)

# Français

CellarManager est un programme vous permettant de gérer votre cave.<br>
Il vous permet de lister les bouteilles présentes dans votre cave, mais également de savoir ou elle se trouve<br>
De plus ce programme est accompagné d'une application mobile, et ce dans le but de rendre le maintien de votre cave à jour plus simple possible.<br>
En effet il est peu commode voire impossible d'emmener son ordinateur dans la cave, et faire des allers-retours entre la cave et l'ordinateur n'est pas une option non plus.<br>
C'est pour cela que l'application vous permets d'ajouter, modifier, déplacer et enlever des bouteilles via votre téléphone.<br>

1. [Installation](https://github.com/AntoninRuan/CaveManager/tree/master#installation)
2. [License](https://github.com/AntoninRuan/CaveManager/tree/master#license) 
3. [Changelog](https://github.com/AntoninRuan/CaveManager/tree/master#changelog)

## Installation

Allez sur la page de la dernière [release][latest_release_link],

Télécharger le fichier en fonction de votre système d'exploitation :

Sur Windows vous pouvez télécharger soit le `.exe` soit `.jar`, même si le `.exe` est plus adapté.<br>
Sur Linux vous devez télécharger le `.jar`.

Vérifiez que vous tous les dépendances nécessaires au programmes sont installés.

Placez l'exécutable dans un dossier vide (appelé `CaveManager`, par exemple), puis lancez-le et commencez à utiliser CaveManager

### Dépendances

Ce programme a besoin de Java ainsi que JavaFX pour être exécuté. <br>
Vous pouvez le télécharger puis l'installer [ici][windows_java_download_fr]. <br>
Cette version de Java comprend JavaFX vous n'aurez donc rien à d'autres à installer

Si vous utilisez une version de Java plus récente que Java 8, JavaFX n'est plus inclut dans Java il est donc nécessaire de le télécharger.<br>
Vous pouvez trouver un guide d'installation [ici][javafx_download]

#### Trouver votre version de Java

Pour savoir quelle version de java est installé sur votre ordinateur, ouvrer l'invite de commande et exécuter la commande suivante

    >java -version
    java version "1.8.0_241"
    Java(TM) SE Runtime Environment (build 1.8.0_241-b07)
    Java HotSpot(TM) 64-Bit Server VM (build 25.241-b07, mixed mode)
    
Si la commande est introuvable c'est que Java n'est pas installer, sinon la version est indiquée à la première ligne, ici `1.8.0_241` nous indique
que Java 8 est installé sur l'ordinateur    

## License

Ce programme est sous la license GNU General Public License v3.0 que vous pouvez retrouver [ici](LICENSE.txt)

## Changelog

### 1.2.1

---
* Correction de bug:
    * Le bouton Ne plus me demander lors de la détection d'une mise à jour au démarrage ne fait plus crasher

### 1.2.0

---
* Passage des reports de bug, suggestion d'idée sur GitHub
* Le système de mise à jour utilise désormais GitHub et le repo: https://github.com/AntoninRuan/CellarManager
* Ajout d'un menu de paramètres

### 1.1.4

___
* Ajout d'une scroll bar permettant d'avoir des étagères de plus de 10 lignes, elles peuvent désormais en avoir 40.
* Ajout d'un style sur la pagination
* Lors de la saisie des valeurs du nombre de lignes et de colonnes d'une étagère, un message est affiché lorsque la valeur est tronqué
* Lors de la duplication d'une bouteille, la fenêtre pour la modifier s'ouvre directement, s'il celle-ci est fermé sans validation, la bouteille est supprimée
* Fix de bug:
	* Le tri par nombre marche désormais correctement
	
### 1.1.3

---
* Ajout de la possibilité d'annuler jusqu'au 10 derniers changements effectué
* La duplication par le drag&drop est désormais activé également par un shit + clic
* Ajout d'un menu de suggestion basé sur les valeurs déjà existantes lors de la saisie de bouteilles
* Ajout de la possibilité de dupliquer une bouteille	

### 1.1.2

---
* Ajout d'un changelog
* Ajout d'un lien vers le changelog dans le menu A Propos

### 1.1.1

---
* Ajout d'un copier coller à l'aide des raccourcis ctrl+c, ctrl+v pour les bouteilles
* Une bouteille drag&drop à l'aide du clique molette est dupliqué dans l'emplacement d'arrivée
* Ajout d'une recherche dans la liste des bouteilles
* Fix de bug:
	* Lors de l'édition d'une bouteille les champs domaine et édition était inversé
	* L'année de consommation ne peut plus être antérieur à celle de production
	
### 1.1.0	

---
* Ajout du drag&drop des bouteilles
* Ajout d'un bouton pour réactiver la recherche de mise à jour au lancement
* Changement du délai de sauvegarde (5min -> 30 secondes)
* Ajout d'une barre de chargement lors du téléchargement de mise à jour
* Lors de la création d'une bouteille le type Rouge est désormais sélectionné par défaut
* Fix de bug:
	* Le fichier téléchargé n'arrivait pas au bon emplacement

### 1.0.5

---
* Le titre de la fenêtre inclut désormais le nom du fichier de stockage
* Fix de bug:
	* Problèmes lorsqu'aucun fichier de sauvegarde n'existait

# English

CellarManager is a software made to help you manage your cellar.<br>
He allows you to list every bottles present in your cellar and to view where they are.<br>
Moreover you can install a smartphone app which will make your cellar more easier to update.<br>
Indeed it is not handy, maybe impossible to bring you computer in your cellar; do many round trip between your cellar and your computer neither.<br>
That why the application allows you to add, modify, move and remove bottles from you smartphone.<br>

1. [Installation](https://github.com/AntoninRuan/CaveManager/tree/master#installation-1)
2. [License](https://github.com/AntoninRuan/CaveManager/tree/master#license-1) 
3. [Changelog](https://github.com/AntoninRuan/CaveManager/tree/master#changelog-1)

## Installation

Go on the page of [latest release][latest_release_link],

Download the correct file depending on you OS :

On Windows, you have to download either the `.exe` file or the `.jar` file even though the `.exe` file is more suitable.<br>
On Linux, you have to download the `.jar` file.

Verify every program dependencies are installed on your computer. (See the dependencies section)

Put the downloaded file in an empty folder (named `CellarManager`, for example), and run it

### Dependencies

This program needs Java and JavaFX to run.<br>
You can download Java [here][windows_java_download_en].<br>
This Java version includes JavaFX, so once you have installed it, you can run the program without issues.

If you are using a newer version than Java 8, JavaFX is no longer included in it, you have to download it.<br>
You can find a installation guidance [here][javafx_download]

#### Find you Java version

To find which Java version is installed on you computer, open the command prompt and then run the following command

    >java -version
    java version "1.8.0_241"
    Java(TM) SE Runtime Environment (build 1.8.0_241-b07)
    Java HotSpot(TM) 64-Bit Server VM (build 25.241-b07, mixed mode)
    
If the command is not defined, Java is not installed on you computer you need to install it. Otherwise the version is given by the first line.
`version "1.8.0_241"` indicate you are running Java 8.  

## License

This software is under the GNU General Public License v3.0, you cand find it [here](LICENSE.txt)

## Changelog

[windows_java_download_fr]: https://www.java.com/fr/download/win10.jsp
[windows_java_download_en]: https://www.java.com/en/download/win10.jsp
[javafx_download]: https://openjfx.io/openjfx-docs/#introduction
[latest_release_link]: https://github.com/AntoninRuan/CaveManager/releases/latest