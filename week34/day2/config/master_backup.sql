-- MySQL dump 10.13  Distrib 8.0.45, for Linux (x86_64)
--
-- Host: localhost    Database: student_analytics_db
-- ------------------------------------------------------
-- Server version	8.0.45

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `student_analytics_db`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `student_analytics_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `student_analytics_db`;

--
-- Table structure for table `question`
--

DROP TABLE IF EXISTS `question`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `question` (
  `id` int NOT NULL AUTO_INCREMENT,
  `question_title` varchar(255) DEFAULT NULL,
  `option1` varchar(255) DEFAULT NULL,
  `option2` varchar(255) DEFAULT NULL,
  `option3` varchar(255) DEFAULT NULL,
  `option4` varchar(255) DEFAULT NULL,
  `right_answer` varchar(255) DEFAULT NULL,
  `difficultylevel` varchar(255) DEFAULT NULL,
  `category` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `question`
--

LOCK TABLES `question` WRITE;
/*!40000 ALTER TABLE `question` DISABLE KEYS */;
INSERT INTO `question` VALUES (1,'Which Java keyword is used to create a subclass?','class','interface','extends','implements','extends','Easy','JAVA'),(2,'What is the output of the following Java code snippet?','4','5','6','Compile error','5','Easy','Java'),(3,'In Java, what is the default value of an uninitialized boolean variable?','true','false','0','null','false','Easy','Java'),(4,'Which Java keyword is used to explicitly throw an exception?','try','throw','catch','finally','throw','Easy','Java'),(5,'What does the \"static\" keyword mean in Java?','It indicates that a variable is constant.','It indicates that a method can be accessed without creating an instance of the class.','It indicates that a class cannot be extended.','It indicates that a variable is of primitive type.','It indicates that a method can be accessed without creating an instance of the class.','Easy','Java'),(6,'What is the correct way to declare a constant variable in Java?','constant int x = 5;','final int x = 5;','static int x = 5;','readonly int x = 5;','final int x = 5;','Easy','Java'),(7,'Which loop in Java allows the code to be executed at least once?','for loop','while loop','do-while loop','switch loop','do-while loop','Easy','Java'),(8,'What is the purpose of the \"break\" statement in Java?','To terminate a loop or switch statement and transfer control to the next statement.','To skip the rest of the code in a loop and move to the next iteration.','To define a label for a loop or switch statement.','To check a condition and execute a block of code repeatedly.','To terminate a loop or switch statement and transfer control to the next statement.','Easy','Java'),(9,'Which Java operator is used to concatenate two strings?','+','-','*','/','+','Easy','Java'),(10,'In Java, which collection class provides an implementation of a dynamic array?','HashMap','ArrayList','LinkedList','HashSet','ArrayList','Easy','Java'),(11,'Which Python function is used to calculate the length of a list?','count()','size()','length()','len()','len()','Easy','Python'),(12,'What is the output of the following Python code snippet?','[1, 2, 3]','[1, 2, 3, 4]','[4, 3, 2, 1]','Error','[1, 2, 3, 4]','Easy','Python'),(13,'Which Python statement is used to exit from a loop prematurely?','break','continue','pass','return','break','Easy','Python'),(14,'What is the purpose of the \"range()\" function in Python?','To generate a random number within a given range.','To iterate over a sequence of numbers.','To sort a list in ascending order.','To calculate the length of a string.','To iterate over a sequence of numbers.','Easy','Python'),(15,'In Python, which data type is mutable?','int','float','str','list','list','Easy','Python'),(16,'Which Python module is used for working with dates and times?','datetime','math','os','sys','datetime','Easy','Python'),(17,'Which is the size of int in Java?','2','32','8','4','4','Easy','Java'),(18,'Which of the following is NOT a primitive data type in Java?','int','float','long','String','String','Easy','Java');
/*!40000 ALTER TABLE `question` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `question_seq`
--

DROP TABLE IF EXISTS `question_seq`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `question_seq` (
  `next_val` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `question_seq`
--

LOCK TABLES `question_seq` WRITE;
/*!40000 ALTER TABLE `question_seq` DISABLE KEYS */;
INSERT INTO `question_seq` VALUES (1);
/*!40000 ALTER TABLE `question_seq` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `quiz`
--

DROP TABLE IF EXISTS `quiz`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `quiz` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(255) DEFAULT NULL,
  `category_name` varchar(255) DEFAULT NULL,
  `num_questions` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `quiz`
--

LOCK TABLES `quiz` WRITE;
/*!40000 ALTER TABLE `quiz` DISABLE KEYS */;
INSERT INTO `quiz` VALUES (1,'Basic Spring Questions 2',NULL,NULL),(2,'Basic Spring Questions 2',NULL,NULL),(3,'Spring Questions','Java',5);
/*!40000 ALTER TABLE `quiz` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `quiz_question_ids`
--

DROP TABLE IF EXISTS `quiz_question_ids`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `quiz_question_ids` (
  `quiz_id` int NOT NULL,
  `question_ids` int DEFAULT NULL,
  KEY `FKs0d7k0yq779g15wmctr0tlnbi` (`quiz_id`),
  CONSTRAINT `FKs0d7k0yq779g15wmctr0tlnbi` FOREIGN KEY (`quiz_id`) REFERENCES `quiz` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `quiz_question_ids`
--

LOCK TABLES `quiz_question_ids` WRITE;
/*!40000 ALTER TABLE `quiz_question_ids` DISABLE KEYS */;
INSERT INTO `quiz_question_ids` VALUES (1,8),(1,3),(1,9),(2,5),(2,6),(2,8),(3,7),(3,6),(3,2),(3,1),(3,9);
/*!40000 ALTER TABLE `quiz_question_ids` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `score`
--

DROP TABLE IF EXISTS `score`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `score` (
  `id` int NOT NULL AUTO_INCREMENT,
  `quiz_id` int DEFAULT NULL,
  `report_url` varchar(255) DEFAULT NULL,
  `score` int DEFAULT NULL,
  `total` int DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKpqss47h2fevnmkh76r14055o0` (`user_id`),
  CONSTRAINT `FKpqss47h2fevnmkh76r14055o0` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `score`
--

LOCK TABLES `score` WRITE;
/*!40000 ALTER TABLE `score` DISABLE KEYS */;
INSERT INTO `score` VALUES (1,2,'/files/report-17-1764849469754.txt',0,3,17),(2,2,'/files/report-17-1764850034505.txt',0,3,17),(3,2,'/files/report-17-1764850403962.txt',0,3,17),(4,2,'/files/report-17-1764851042595.txt',0,3,17),(5,2,'/files/report-17-1764851560239.txt',0,3,17),(6,2,'/files/report-17-1764851964505.txt',3,3,17),(7,1,'/files/report-17-1764911501053.txt',2,3,17),(8,1,'/files/report-17-1764912573330.txt',2,3,17),(9,1,'/files/report-17-1764912839456.txt',1,3,17),(10,2,'/files/report-19-1764914352960.txt',2,3,19),(11,3,'/files/report-17-1764928903785.txt',3,5,17);
/*!40000 ALTER TABLE `score` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(255) DEFAULT NULL,
  `full_name` varchar(255) DEFAULT NULL,
  `google_user` bit(1) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `role` enum('STUDENT','TEACHER') DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_6dotkott2kjsp8vw4d0m25fb7` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'sujal@test.com','Sujal',_binary '\0','$2a$10$4MJ6hTjq4H9tBGjL9xOo6.2iS8Qca2zKFOJoLLLN/AKxbv0HwHfnW','TEACHER'),(2,'rohanjoshi@gmail.com','Rohan Joshi',_binary '\0','$2a$10$M8O6Ms6H0XE73yIQqcjzdevaf/AmeAKsTUYBYb8unxvJ5TOmK8VJ6','TEACHER'),(7,'mrparsad@gmail.com','Unknown User',_binary '\0','$2a$10$KMT8b9OmitHPpoaCbJn5b.7.WigewiSCxvhhmKM.aUqk6oUO8Sa/W','STUDENT'),(8,'dskd@gmail.com','Unknown User',_binary '\0','$2a$10$QE4Q7XePR5otO5B3A5xzRuDQHfxmPwz2BcJYfH9PJeKjklgPNBvam','STUDENT'),(9,'sbdsdhhsdshd@gmail.com','Unknown User',_binary '\0','$2a$10$5OLibhbwlioZKtgeukqqk.EebscN/AaYkZzQMsKzeUumtH9zVKrLO','STUDENT'),(10,'lkskajsajljsks@gmail.com','Unknown User',_binary '\0','$2a$10$ty5J4DeqjeKXlLvf5uozbeAhNAMP4vktrPG62HMal/L6MeDE.fBMy','STUDENT'),(11,'sasa@gmail.com','Unknown User',_binary '\0','$2a$10$I9wGUWECKw1UyN3pDwMLUOGuOoCxKWLu4O2C/kq1PdC2109.Pu4qe','STUDENT'),(12,'hello@gmail.com','Unknown User',_binary '\0','$2a$10$dVyU4rJe6l5FMJ4a5u5jEeKYv9/Ewz0LwwrJtp2QOe3H4s/4Ax1Cm','STUDENT'),(13,'hello2@gmail.com','Unknown User',_binary '\0','$2a$10$Uu/4bQQm0CpkPW0bmUlQku79W0/V7zZd.Tc7vXOGbKHqbJtv7/y9K','STUDENT'),(14,'jkhaskshakjshk2@gmail.com','Unknown User',_binary '\0','$2a$10$2KmUxdCvph7d1N7kYpMp6uA/sJWMzEADdX8h3BsU.bflM4xXWbrk.','STUDENT'),(15,'sddsjkhdks@gmail.com','Unknown User',_binary '\0','$2a$10$FaFSDirziEVLWvjVPSRlt.7Gem9kcYeWvD7XKDy82Pxvaw0QLeu3e','STUDENT'),(16,'sujal234@gmai;com','Sujal Patel',_binary '\0','$2a$10$pFK4SGKdYOV7gaMGPr9do.GbOgr1KilcMm7EgyEkwSwnHu3lPETwq','TEACHER'),(17,'sujal2345@gmail.com','sujal morwani',_binary '\0','$2a$10$CXJWWa4D589eVZPbhBCzJO9IsbpVpDqBIWwkExmHaQuygZJcDogPi','STUDENT'),(18,'morwanisujal875@gmail.com','Sujal Morwani',_binary '',NULL,'TEACHER'),(19,'sujalmorwani@gmail.com','Sujal Morwani',_binary '',NULL,'STUDENT');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Current Database: `student_analytics_user`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `student_analytics_user` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `student_analytics_user`;

--
-- Table structure for table `question`
--

DROP TABLE IF EXISTS `question`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `question` (
  `id` int NOT NULL AUTO_INCREMENT,
  `question_title` varchar(255) DEFAULT NULL,
  `option1` varchar(255) DEFAULT NULL,
  `option2` varchar(255) DEFAULT NULL,
  `option3` varchar(255) DEFAULT NULL,
  `option4` varchar(255) DEFAULT NULL,
  `right_answer` varchar(255) DEFAULT NULL,
  `difficultylevel` varchar(255) DEFAULT NULL,
  `category` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `question`
--

LOCK TABLES `question` WRITE;
/*!40000 ALTER TABLE `question` DISABLE KEYS */;
INSERT INTO `question` VALUES (1,'Which Java keyword is used to create a subclass?','class','interface','extends','implements','extends','Easy','JAVA'),(2,'What is the output of the following Java code snippet?','4','5','6','Compile error','5','Easy','Java'),(3,'In Java, what is the default value of an uninitialized boolean variable?','true','false','0','null','false','Easy','Java'),(4,'Which Java keyword is used to explicitly throw an exception?','try','throw','catch','finally','throw','Easy','Java'),(5,'What does the \"static\" keyword mean in Java?','It indicates that a variable is constant.','It indicates that a method can be accessed without creating an instance of the class.','It indicates that a class cannot be extended.','It indicates that a variable is of primitive type.','It indicates that a method can be accessed without creating an instance of the class.','Easy','Java'),(6,'What is the correct way to declare a constant variable in Java?','constant int x = 5;','final int x = 5;','static int x = 5;','readonly int x = 5;','final int x = 5;','Easy','Java'),(7,'Which loop in Java allows the code to be executed at least once?','for loop','while loop','do-while loop','switch loop','do-while loop','Easy','Java'),(8,'What is the purpose of the \"break\" statement in Java?','To terminate a loop or switch statement and transfer control to the next statement.','To skip the rest of the code in a loop and move to the next iteration.','To define a label for a loop or switch statement.','To check a condition and execute a block of code repeatedly.','To terminate a loop or switch statement and transfer control to the next statement.','Easy','Java'),(9,'Which Java operator is used to concatenate two strings?','+','-','*','/','+','Easy','Java'),(10,'In Java, which collection class provides an implementation of a dynamic array?','HashMap','ArrayList','LinkedList','HashSet','ArrayList','Easy','Java'),(11,'Which Python function is used to calculate the length of a list?','count()','size()','length()','len()','len()','Easy','Python'),(12,'What is the output of the following Python code snippet?','[1, 2, 3]','[1, 2, 3, 4]','[4, 3, 2, 1]','Error','[1, 2, 3, 4]','Easy','Python'),(13,'Which Python statement is used to exit from a loop prematurely?','break','continue','pass','return','break','Easy','Python'),(14,'What is the purpose of the \"range()\" function in Python?','To generate a random number within a given range.','To iterate over a sequence of numbers.','To sort a list in ascending order.','To calculate the length of a string.','To iterate over a sequence of numbers.','Easy','Python'),(15,'In Python, which data type is mutable?','int','float','str','list','list','Easy','Python'),(16,'Which Python module is used for working with dates and times?','datetime','math','os','sys','datetime','Easy','Python'),(17,'Which is the size of int in Java?','2','32','8','4','4','Easy','Java'),(18,'Which of the following is NOT a primitive data type in Java?','int','float','long','String','String','Easy','Java');
/*!40000 ALTER TABLE `question` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `question_seq`
--

DROP TABLE IF EXISTS `question_seq`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `question_seq` (
  `next_val` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `question_seq`
--

LOCK TABLES `question_seq` WRITE;
/*!40000 ALTER TABLE `question_seq` DISABLE KEYS */;
INSERT INTO `question_seq` VALUES (1);
/*!40000 ALTER TABLE `question_seq` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `quiz`
--

DROP TABLE IF EXISTS `quiz`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `quiz` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(255) DEFAULT NULL,
  `category_name` varchar(255) DEFAULT NULL,
  `num_questions` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `quiz`
--

LOCK TABLES `quiz` WRITE;
/*!40000 ALTER TABLE `quiz` DISABLE KEYS */;
INSERT INTO `quiz` VALUES (1,'Basic Spring Questions 2',NULL,NULL),(2,'Basic Spring Questions 2',NULL,NULL),(3,'Spring Questions','Java',5);
/*!40000 ALTER TABLE `quiz` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `quiz_question_ids`
--

DROP TABLE IF EXISTS `quiz_question_ids`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `quiz_question_ids` (
  `quiz_id` int NOT NULL,
  `question_ids` int DEFAULT NULL,
  KEY `FKs0d7k0yq779g15wmctr0tlnbi` (`quiz_id`),
  CONSTRAINT `FKs0d7k0yq779g15wmctr0tlnbi` FOREIGN KEY (`quiz_id`) REFERENCES `quiz` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `quiz_question_ids`
--

LOCK TABLES `quiz_question_ids` WRITE;
/*!40000 ALTER TABLE `quiz_question_ids` DISABLE KEYS */;
INSERT INTO `quiz_question_ids` VALUES (1,8),(1,3),(1,9),(2,5),(2,6),(2,8),(3,7),(3,6),(3,2),(3,1),(3,9);
/*!40000 ALTER TABLE `quiz_question_ids` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `score`
--

DROP TABLE IF EXISTS `score`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `score` (
  `id` int NOT NULL AUTO_INCREMENT,
  `quiz_id` int DEFAULT NULL,
  `report_url` varchar(255) DEFAULT NULL,
  `score` int DEFAULT NULL,
  `total` int DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKpqss47h2fevnmkh76r14055o0` (`user_id`),
  CONSTRAINT `FKpqss47h2fevnmkh76r14055o0` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `score`
--

LOCK TABLES `score` WRITE;
/*!40000 ALTER TABLE `score` DISABLE KEYS */;
INSERT INTO `score` VALUES (1,2,'/files/report-17-1764849469754.txt',0,3,17),(2,2,'/files/report-17-1764850034505.txt',0,3,17),(3,2,'/files/report-17-1764850403962.txt',0,3,17),(4,2,'/files/report-17-1764851042595.txt',0,3,17),(5,2,'/files/report-17-1764851560239.txt',0,3,17),(6,2,'/files/report-17-1764851964505.txt',3,3,17),(7,1,'/files/report-17-1764911501053.txt',2,3,17),(8,1,'/files/report-17-1764912573330.txt',2,3,17),(9,1,'/files/report-17-1764912839456.txt',1,3,17),(10,2,'/files/report-19-1764914352960.txt',2,3,19),(11,3,'/files/report-17-1764928903785.txt',3,5,17);
/*!40000 ALTER TABLE `score` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(255) DEFAULT NULL,
  `full_name` varchar(255) DEFAULT NULL,
  `google_user` bit(1) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `role` enum('STUDENT','TEACHER') DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_6dotkott2kjsp8vw4d0m25fb7` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'sujal@test.com','Sujal',_binary '\0','$2a$10$4MJ6hTjq4H9tBGjL9xOo6.2iS8Qca2zKFOJoLLLN/AKxbv0HwHfnW','TEACHER'),(2,'rohanjoshi@gmail.com','Rohan Joshi',_binary '\0','$2a$10$M8O6Ms6H0XE73yIQqcjzdevaf/AmeAKsTUYBYb8unxvJ5TOmK8VJ6','TEACHER'),(7,'mrparsad@gmail.com','Unknown User',_binary '\0','$2a$10$KMT8b9OmitHPpoaCbJn5b.7.WigewiSCxvhhmKM.aUqk6oUO8Sa/W','STUDENT'),(8,'dskd@gmail.com','Unknown User',_binary '\0','$2a$10$QE4Q7XePR5otO5B3A5xzRuDQHfxmPwz2BcJYfH9PJeKjklgPNBvam','STUDENT'),(9,'sbdsdhhsdshd@gmail.com','Unknown User',_binary '\0','$2a$10$5OLibhbwlioZKtgeukqqk.EebscN/AaYkZzQMsKzeUumtH9zVKrLO','STUDENT'),(10,'lkskajsajljsks@gmail.com','Unknown User',_binary '\0','$2a$10$ty5J4DeqjeKXlLvf5uozbeAhNAMP4vktrPG62HMal/L6MeDE.fBMy','STUDENT'),(11,'sasa@gmail.com','Unknown User',_binary '\0','$2a$10$I9wGUWECKw1UyN3pDwMLUOGuOoCxKWLu4O2C/kq1PdC2109.Pu4qe','STUDENT'),(12,'hello@gmail.com','Unknown User',_binary '\0','$2a$10$dVyU4rJe6l5FMJ4a5u5jEeKYv9/Ewz0LwwrJtp2QOe3H4s/4Ax1Cm','STUDENT'),(13,'hello2@gmail.com','Unknown User',_binary '\0','$2a$10$Uu/4bQQm0CpkPW0bmUlQku79W0/V7zZd.Tc7vXOGbKHqbJtv7/y9K','STUDENT'),(14,'jkhaskshakjshk2@gmail.com','Unknown User',_binary '\0','$2a$10$2KmUxdCvph7d1N7kYpMp6uA/sJWMzEADdX8h3BsU.bflM4xXWbrk.','STUDENT'),(15,'sddsjkhdks@gmail.com','Unknown User',_binary '\0','$2a$10$FaFSDirziEVLWvjVPSRlt.7Gem9kcYeWvD7XKDy82Pxvaw0QLeu3e','STUDENT'),(16,'sujal234@gmai;com','Sujal Patel',_binary '\0','$2a$10$pFK4SGKdYOV7gaMGPr9do.GbOgr1KilcMm7EgyEkwSwnHu3lPETwq','TEACHER'),(17,'sujal2345@gmail.com','sujal morwani',_binary '\0','$2a$10$CXJWWa4D589eVZPbhBCzJO9IsbpVpDqBIWwkExmHaQuygZJcDogPi','STUDENT'),(18,'morwanisujal875@gmail.com','Sujal Morwani',_binary '',NULL,'TEACHER'),(19,'sujalmorwani@gmail.com','Sujal Morwani',_binary '',NULL,'STUDENT');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-27  5:23:41
