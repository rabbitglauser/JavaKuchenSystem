CREATE DATABASE  IF NOT EXISTS `KuchenDB` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `KuchenDB`;
-- MySQL dump 10.13  Distrib 5.7.12, for Win64 (x86_64)
--
-- Host: localhost    Database: KuchenDB
-- ------------------------------------------------------
-- Server version	5.7.14-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `ANLASS`
--

DROP TABLE IF EXISTS `ANLASS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ANLASS` (
  `ID_Anlass` int(11) NOT NULL AUTO_INCREMENT,
  `Anlass` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`ID_Anlass`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ANLASS`
--

LOCK TABLES `ANLASS` WRITE;
/*!40000 ALTER TABLE `ANLASS` DISABLE KEYS */;
INSERT INTO `ANLASS` VALUES (1,'Kinder'),(2,'Geburtstag'),(3,'Hochzeit'),(4,'Business'),(5,'sonst immer');
/*!40000 ALTER TABLE `ANLASS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `EINHEIT`
--

DROP TABLE IF EXISTS `EINHEIT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `EINHEIT` (
  `ID_Einheit` int(11) NOT NULL AUTO_INCREMENT,
  `Einheit` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`ID_Einheit`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `EINHEIT`
--

LOCK TABLES `EINHEIT` WRITE;
/*!40000 ALTER TABLE `EINHEIT` DISABLE KEYS */;
INSERT INTO `EINHEIT` VALUES (1,'Prise'),(2,'l'),(3,'dl'),(4,'ml'),(5,'kg'),(6,'g'),(7,'Stück'),(8,'KaffeeLöfel'),(9,'Esslöfel'),(10,'Messerspitze'),(11,'Pck.');
/*!40000 ALTER TABLE `EINHEIT` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `KUCHEN`
--

DROP TABLE IF EXISTS `KUCHEN`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `KUCHEN` (
  `ID_Kuchen` int(11) NOT NULL AUTO_INCREMENT,
  `Kuchen` varchar(200) DEFAULT NULL,
  `Beschreibung` varchar(2000) DEFAULT NULL,
  `DauerInMinuten` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID_Kuchen`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `KUCHEN`
--

LOCK TABLES `KUCHEN` WRITE;
/*!40000 ALTER TABLE `KUCHEN` DISABLE KEYS */;
INSERT INTO `KUCHEN` VALUES (1,'Dracula-Kuchen','Das hier ist eine Kurz-Form der Zubereitung. Ich gehe davon aus, dass Frau/Mann weiß, dass man Eier in eine Schüssel aufschlägt und den elektrischen Quirl nimmt.\n\nButter schön weich werden lassen/schmelzen und etwas abkühlen lassen. Zucker und Eier rein und ordentlich rühren. Mehl, Backpulver und Stärke rein und rühren. Zimt, Streusel, Kakaopulver, Rotwein und Rum rein und rühren. In eine Kastenform füllen, bei ca. 180 °C/Stufe 3 50 - 60 Minuten backen, (ggfs.) oben abdecken. Auskühlen lassen!)\n\nMarzipan vierteln (bei 2 Händen, zwei Füßen) oder fünfteln (wenn Spinne und Kreuz dazu sollen),\nmit Lebensmittelfarbe in Hautton einfärben (Vorsichtig dosieren, je nach Marke sonst wird\'s schnell zuviel)\n\nHände und Füße formen. Die Finger lassen sich recht gut mit einer kleinen Schere einschneiden und dann mit leicht anfeuchteten Fingern ausformen. Ein klitzekleines Stückchen Marzipan noch etwas dunkler für die Finger-/Fußnägel einfärben und draufsetzen - geht recht gut mit einer Messerspitze.\n\nSpinne: Marzipan schwarz einfärben, eine kleine Kugel formen, die Beine entweder platt liegend formen oder, wenns plastisch sein soll, mit ein bisschen Blumendraht innen drin stabilisieren\n(geht evtl. mit Fondant besser - keine Erfahrung bisher damit). Die Augen mit weißer Zuckerschrift oder zwei weißen Schokostreuseln draufsetzen.',60),(2,'Caillou-Kuchen','Arbeitszeit: ca. 20 Min. / Schwierigkeitsgrad: normal\n\nDas Fett schaumig rühren, Zucker, Vanillinzucker dazurühren. Die Eier dazugeben und schaumig rühren. Die Milch unter Rühren dazugeben. Mehl und Backpulver mischen, sieben und zu der Fett-Zucker-Masse geben, gut verrühren. \n\nAuf ein gut gefettetes Backblech streichen und bei 200°C etwa 30 Minuten backen. Den fertigen Kuchen mit einer Glasur aus Puderzucker und Zitronensaft bestreichen.',20),(3,'Mikrowellenkuchen','Arbeitszeit: ca. 3 Min. / Schwierigkeitsgrad: simpel\n\nDas Ei in den Rührtopf schlagen, und schaumig rühren Nun nach und nach je 1 EL Mehl dazugeben und kräftig verrühren. Danach den Kakao mit Zucker vermischen und auch dazu geben. Das Wasser hinzufügen und nochmal kräftig verrühren. Zum Schluss das Öl gut unterrühren. \n\nDen Teig in eine Tasse gießen und für 3-4 Minuten bei 800 Watt in die Mikrowelle stellen. \n\nDanach den Kuchen herausnehmen und auf einen Teller stürzen. Nach Belieben warm oder kalt genießen.',10),(4,'Waffelbecherkuchen','Arbeitszeit: ca. 20 Min. / Schwierigkeitsgrad: simpel\n\nDen Backofen auf 180°C Ober-/Unterhitze (Heißluft: 160°C) vorheizen. Ein Backblech mit Backpapier belegen und die 24 Waffelbecher darauf stellen.\n\nFür den Rührteig die weiche Butter (oder Margarine) mit einem Mixer geschmeidig rühren. Nach und nach den Zucker und den Vanillezucker unter Rühren hinzufügen, bis eine gebundene Masse entsteht. Jedes Ei einzeln zugeben und etwa 1/2 Min. auf höchster Stufe unterrühren. Das Mehl mit Backpulver mischen, sieben und kurz auf mittlerer Stufe unterrühren. Den Teig nun in einen Spritzbeutel mit Lochtülle geben und die Waffelbecher zu knapp 2/3 mit Teig füllen. Das Backblech auf mittlerer Einschubleiste in den Backofen schieben.\n\nIm heißen Backofen ca. 15 Minuten backen. Die Kuchen anschließend abkühlen lassen.\n\nDie Kuvertüre in einen Wasserbad schmelzen und die Küchlein mit der Teigkuppel in die flüssige Schokolade tauchen.\n\nTipp: Wenn man möchte, kann man auf die warme Schokolade noch bunte Streusel, Kokosraspel oder gehackte Mandeln etc. streuen.',20),(5,'Susys Griess-Nuss-Kuchen','Arbeitszeit: ca. 15 Min. / Koch-/Backzeit: ca. 1 Std. / Schwierigkeitsgrad: normal\n\nDen Backofen auf 200°C vorheizen. Eine Kastenform einfetten, mit Grieß ausstreuen. \nDie trockenen Zutaten gut vermischen, dann die Milch dazugeben und eventuelle Aromen wie Zitronenschale/-öl oder Schokolade. Alles gut verrühren. Die recht flüssige Masse in die vorbereitete Kuchenform geben, ca. 60 min bei 200° C backen. Stäbchenprobe! \nDen Kuchen erkalten lassen bevor man ihn aus der Form nimmt, er bricht sonst leicht. \n\nDen fertigen Kuchen kann man mit Puderzucker überstäuben, mit einer Schokoladenglasur oder einem Guss aus Puderzucker und Zitronen-/Orangensaft versehen.\nDer Fantasie sind bei diesem Kuchen (fast) keine Grenzen gesetzt und er lädt buchstäblich dazu ein, neue Varianten auszuprobieren.',75),(6,'Quark-Kirschen-Kuchen','Arbeitszeit: ca. 1 Std. Ruhezeit: ca. 2 Std. / Schwierigkeitsgrad: normal\n\nKirschen abtropfen lassen,100 g Fett,100 g Zucker und Vanillinzucker cremig rühren. 4 Eier einzeln unterrühren, Quark, Sahne und Puddingpulver unterrühren.\n250 g Fett, 250 g Zucker und Salz cremig rühren, 4 Eier unterrühren. Mehl und Backpulver mischen, und durchsieben, im Wechsel mit dem Eierlikör unter die Fett-Eiercreme rühren.\nEine Springform (28cm) fetten und mit Grieß ausstreuen Teig darin glatt streichen, Kirschen darauf verteilen, Quarkmasse darauf geben.\nIm vorgeheizten Backofen 175° ( Umluft 150°) ca. 1-11/4 Stunde backen. \nIn der Form auf einem Kuchengitter abkühlen lassen.\nDanach Kuchengitter umgedreht auf die Kuchenoberfläche legen und vorsichtig aus der Form lösen. \nWenn ganz abgekühlt, Kuchen auf eine Kuchenplatte legen und mit Puderzucker bestäuben.',180),(7,'Haselnuss-Kastanien-Kuchen','Arbeitszeit: ca. 20 Min. / Schwierigkeitsgrad: simpel\n\nDie Menge reicht für eine 18er Springform, für eine 26er Springform Menge verdoppeln und ca. 10 Minuten länger backen.\n\nMargarine weich rühren, Eier, Zucker und Vanillezucker zugeben, schaumig rühren, Haselnüsse und Kastanienpüree gut untermischen, \ndas Mehl und das Backpulver unterrühren, die feinen Schokoraspel mit dem Holzlöffel unter den Teig mischen. \n\nBacken bei 170° Umluft ca. 45 Minuten. \n\nWenn der Kuchen abgekühlt ist, mit der Mischung aus Puderzucker und Vanillezucker bestäuben. \n\nDer Kuchen wird in der Mitte zusammensinken, aber das macht nichts!',30),(8,'Apfel-Quark-Kuchen','Arbeitszeit: ca. 20 Min. / Schwierigkeitsgrad: simpel / Kalorien p. P.: keine Angabe\n\nFür den Boden: \nZutaten zu einem Mürbeteig verarbeiten, 2/3 des Teiges ausrollen und in eine gefettete Springform legen, den restlichen Teig zu einer Rolle formen und den Rand damit auskleiden.\n\nFür den Belag:\nÄpfel schälen und in Spalten schneiden, Sahne steif schlagen und beiseite stellen.\nQuark, Zucker, Eier und Puddingpulver zusammen gut verrühren, die steif geschlagene Sahne unterheben und auf den vorbereiteten Teigboden geben. Die Apfelspalten darauf legen und dick mit einer Zucker- und Zimtmischung bestreuen. \nIm vorgeheizten Backofen 190°C ca. 50-60 min. backen.',80),(9,'Joghurt-Zitronen-Kuchen','Arbeitszeit: ca. 20 Min. / Schwierigkeitsgrad: normal\n\nDie Margarine mit dem Zucker und dem Vanillezucker schaumig rühren. Die Eier einzeln für je 1 Minute einrühren. Die abgeriebene Zitronenschale, 1 EL Zitronensaft und den Joghurt unterrühren. Mehl und Backpulver mischen und sieben. Das Mehl anschließend auf 2 Mal unterrühren.\nDen Teig in eine gefettete und mit Mehl bestäubte Kastenform geben und im vorgeheizten Ofen (175°C Ober/Unterhitze) auf mittlerer Schiene ca. 55 min backen (Stäbchenprobe).\nDen Kuchen 10 min abkühlen lassen und aus der Form nehmen.\nAus 1 EL Zitronensaft und dem Puderzucker einen Guss herstellen, evtl. noch etwas Wasser hinzufügen, und den noch lauwarmen Kuchen damit bestreichen.',75),(10,'Zimtrollen-Kuchen','Arbeitszeit: ca. 20 Min. / Koch-/Backzeit: ca. 30 Min. Ruhezeit: ca. 1 Std. / Schwierigkeitsgrad: normal\n\nAlle Teigzutaten zu einem schönen glatten Teig verarbeiten, der sich leicht von der Schüssel lösen sollte. Dann warm stellen und auf die doppelte Größe gehen lassen. \n\nNach dem Gehenlassen den Teig ausrollen (ca. 76 x 46 cm). Mit Butter bestreichen und mit dem Zimt-Zucker Gemisch bestreuen. \n\nVon der breiteren Seiten den Teig aufrollen und von der Rolle ca. 4 cm breite Stücke abschneiden. Diese in eine gefettete Springform nebeneinander setzen und bei 175°C 25 - 30 Min. backen. \n\nAuf den noch heißen Kuchen kommt dann noch ein Guss aus Puderzucker, Vanillezucker und Milch',120),(11,'Schoko-Nuss-Kuchen','Arbeitszeit: ca. 15 Min. / Schwierigkeitsgrad: simpel\n\nBackofen auf 175°C vorheizen und die Wärme, die dabei entsteht, gleich dazu nutzen, um die Schokolade auf einem hitzebeständigen Teller schmelzen zu lassen. Die ersten vier Zutaten so lange verrühren, bis der Zucker sich richtig aufgelöst hat. Die gemahlenen Haselnüsse mit dem Backpulver unter den Teig mischen. Die flüssige Schokolade dazugeben und die (ziemlich weiche) Masse in eine Gugelhupfform füllen. \nBei 175°C eine Stunde lang backen. Nach ca. 20 Minuten ein Stück Alufolie drüberlegen, damit er nicht so dunkel wird. ',80),(12,'Rüebli-Kuchen','Arbeitszeit: ca. 15 Min. / Koch-/Backzeit: ca. 1 Std. / Schwierigkeitsgrad: simpel\n\nBackofen auf 170 Grad (Ober-/Unterhitze) vorheizen. \nKarotten sehr fein reiben. Eier trennen. Eiweiß mit 75 g Zucker sehr steif schlagen. Eigelb mit den restlichen 75 g Zucker cremig rühren, bis sich der Zucker aufgelöst hat. Nun alle weiteren Zutaten zur Eiercreme geben und verrühren. Zum Schluss den Eischnee vorsichtig unterheben. Den Teig in eine gefettete Backform (26 cm) geben und etwa 60 Min. backen. Abgekühlten Kuchen nach Belieben mit Puderzucker bestäuben. \nDer Kuchen schmeckt am besten nach ein bis zwei Tagen.',90);
/*!40000 ALTER TABLE `KUCHEN` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `KUCHEN_ANLASS`
--

DROP TABLE IF EXISTS `KUCHEN_ANLASS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `KUCHEN_ANLASS` (
  `Kuchen_ID` int(11) NOT NULL,
  `Anlass_ID` int(11) NOT NULL,
  PRIMARY KEY (`Kuchen_ID`,`Anlass_ID`),
  KEY `fk_Kuchen_has_Anlass_Anlass1_idx` (`Anlass_ID`),
  KEY `fk_Kuchen_has_Anlass_Kuchen1_idx` (`Kuchen_ID`),
  CONSTRAINT `fk_Kuchen_has_Anlass_Anlass1` FOREIGN KEY (`Anlass_ID`) REFERENCES `ANLASS` (`ID_Anlass`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_Kuchen_has_Anlass_Kuchen1` FOREIGN KEY (`Kuchen_ID`) REFERENCES `KUCHEN` (`ID_Kuchen`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `KUCHEN_ANLASS`
--

LOCK TABLES `KUCHEN_ANLASS` WRITE;
/*!40000 ALTER TABLE `KUCHEN_ANLASS` DISABLE KEYS */;
INSERT INTO `KUCHEN_ANLASS` VALUES (1,1),(2,1),(3,1),(4,1),(10,1),(11,1),(1,2),(2,2),(3,2),(10,2),(11,2),(2,3),(6,3),(8,3),(11,3),(12,3),(8,4),(11,4),(12,4),(3,5),(5,5),(6,5),(9,5),(11,5);
/*!40000 ALTER TABLE `KUCHEN_ANLASS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `REZEPT`
--

DROP TABLE IF EXISTS `REZEPT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `REZEPT` (
  `Menge` int(11) DEFAULT NULL,
  `Kuchen_ID` int(11) NOT NULL,
  `ZUTAT_ID` int(11) NOT NULL,
  `Einheit_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`Kuchen_ID`,`ZUTAT_ID`),
  KEY `fk_Rezept_ZUTAT1_idx` (`ZUTAT_ID`),
  KEY `fk_Rezept_Einheit1_idx` (`Einheit_ID`),
  CONSTRAINT `fk_Rezept_Einheit1` FOREIGN KEY (`Einheit_ID`) REFERENCES `EINHEIT` (`ID_Einheit`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_Rezept_Kuchen` FOREIGN KEY (`Kuchen_ID`) REFERENCES `KUCHEN` (`ID_Kuchen`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_Rezept_ZUTAT1` FOREIGN KEY (`ZUTAT_ID`) REFERENCES `zutat` (`ID_Zutat`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `REZEPT`
--

LOCK TABLES `REZEPT` WRITE;
/*!40000 ALTER TABLE `REZEPT` DISABLE KEYS */;
INSERT INTO `REZEPT` VALUES (200,1,1,6),(170,1,2,6),(200,1,3,6),(50,1,4,6),(4,1,5,7),(1,1,6,11),(100,1,7,6),(1,1,8,8),(2,1,9,8),(2,1,10,4),(125,1,11,4),(100,1,41,6),(120,2,2,6),(300,2,3,6),(3,2,5,7),(1,2,6,11),(150,2,12,6),(2,2,13,11),(100,2,14,6),(50,2,15,4),(100,2,36,4),(4,3,2,9),(4,3,3,9),(1,3,5,7),(4,3,9,9),(4,3,17,9),(50,3,18,4),(100,4,1,6),(100,4,2,6),(125,4,3,6),(2,4,5,7),(1,4,6,8),(1,4,13,11),(200,4,19,6),(24,4,20,7),(250,5,2,6),(1,5,6,11),(50,5,7,6),(250,5,21,6),(250,5,22,6),(1,5,28,1),(5,5,36,3),(350,6,1,6),(350,6,2,6),(250,6,3,6),(8,6,5,7),(1,6,6,8),(1,6,13,11),(1,6,14,9),(250,6,24,6),(750,6,25,6),(100,6,26,4),(1,6,27,11),(1,6,28,1),(125,6,29,4),(70,7,1,6),(70,7,2,6),(2,7,3,9),(2,7,5,7),(1,7,6,8),(1,7,13,11),(1,7,14,9),(100,7,23,6),(70,7,31,6),(40,7,37,6),(125,8,1,6),(200,8,2,6),(250,8,3,6),(3,8,5,7),(1,8,6,8),(1,8,8,8),(250,8,25,6),(200,8,26,4),(1,8,27,11),(1,8,32,7),(500,8,33,6),(200,9,2,6),(300,9,3,6),(3,9,5,7),(1,9,6,8),(200,9,12,6),(1,9,13,11),(125,9,14,6),(1,9,15,7),(1,9,16,7),(150,9,34,6),(100,10,2,6),(480,10,3,6),(1,10,5,7),(1,10,8,8),(80,10,12,6),(1,10,13,11),(120,10,14,6),(120,10,18,4),(1,10,28,8),(1,10,35,7),(120,10,36,4),(200,11,1,6),(200,11,2,6),(6,11,5,7),(1,11,6,8),(1,11,13,11),(400,11,23,6),(150,11,37,6),(150,12,2,6),(50,12,4,6),(6,12,5,7),(1,12,10,9),(1,12,14,9),(1,12,16,7),(200,12,22,6),(100,12,23,6),(300,12,38,6);
/*!40000 ALTER TABLE `REZEPT` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ZUTAT`
--

DROP TABLE IF EXISTS `ZUTAT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ZUTAT` (
  `ID_Zutat` int(11) NOT NULL AUTO_INCREMENT,
  `Zutat` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`ID_Zutat`)
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ZUTAT`
--

LOCK TABLES `ZUTAT` WRITE;
/*!40000 ALTER TABLE `ZUTAT` DISABLE KEYS */;
INSERT INTO `ZUTAT` VALUES (1,'Butter'),(2,'Zucker'),(3,'Mehl'),(4,'Speisestärke'),(5,'Ei'),(6,'Backpulver'),(7,'Schokoladenstreusel'),(8,'Zimt'),(9,'Kakaopulver'),(10,'Rum'),(11,'Rotwein'),(12,'Margarine'),(13,'Vanillezucker'),(14,'Puderzucker'),(15,'Zitronensaft'),(16,'Zitronenschale'),(17,'Rapsöl'),(18,'Wasser'),(19,'Kuvertüre'),(20,'Waffel'),(21,'Griess'),(22,'Mandeln gemahlen'),(23,'Haselnüsse gemahlen'),(24,'Sauerkirschen'),(25,'Quark'),(26,'Rahm'),(27,'Vanillecreme'),(28,'Salz'),(29,'Eierlikör'),(31,'Kastanien'),(32,'Eigelb'),(33,'Apfel'),(34,'Joghurt'),(35,'Hefe'),(36,'Milch'),(37,'Schokolade'),(38,'Karotten'),(39,'Vanilleschote'),(40,'Beeren'),(41,'Marzipan');
/*!40000 ALTER TABLE `ZUTAT` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-09-23  9:23:42
