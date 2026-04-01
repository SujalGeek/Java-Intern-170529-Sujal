-- MySQL dump 10.13  Distrib 8.0.45, for Linux (x86_64)
--
-- Host: localhost    Database: student_analytics_user
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
-- Table structure for table `revoked_tokens`
--

DROP TABLE IF EXISTS `revoked_tokens`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `revoked_tokens` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `revokedAt` datetime(6) DEFAULT NULL,
  `token` varchar(500) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_o7nu95tpd50oqhacdvs5qk1bf` (`token`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `revoked_tokens`
--

LOCK TABLES `revoked_tokens` WRITE;
/*!40000 ALTER TABLE `revoked_tokens` DISABLE KEYS */;
/*!40000 ALTER TABLE `revoked_tokens` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `students`
--

DROP TABLE IF EXISTS `students`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `students` (
  `student_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `major` varchar(100) DEFAULT NULL,
  `max_courses_per_semester` int DEFAULT NULL,
  `semester` int DEFAULT NULL,
  `student_number` varchar(20) NOT NULL,
  `year` int DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`student_id`),
  UNIQUE KEY `UK_h7gboo6v79gig1eo7lt1fubew` (`student_number`),
  UNIQUE KEY `UK_g4fwvutq09fjdlb4bb0byp7t` (`user_id`),
  CONSTRAINT `FKdt1cjx5ve5bdabmuuf3ibrwaq` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `students`
--

LOCK TABLES `students` WRITE;
/*!40000 ALTER TABLE `students` DISABLE KEYS */;
INSERT INTO `students` VALUES (1,'2026-02-03 09:06:23.346686','IT',3,NULL,'23',NULL,1),(5,'2026-03-03 06:50:08.419630','Computer Science',3,8,'STU2026005',4,10),(6,'2026-03-04 14:01:34.718152','IT',3,2,'STU2026009',1,11),(7,'2026-03-04 14:23:06.032036','CE',5,4,'STU2026010',2,12),(8,'2026-03-05 05:13:36.971626','IT',3,6,'STU2026011',3,14),(9,'2026-03-05 06:13:27.490405','CE',3,3,'STU2026014',3,16),(13,'2026-03-05 07:25:22.351805','IT',3,5,'STU2026070',3,20),(14,'2026-03-05 10:36:12.358158','IT',3,6,'STU2026023',3,21),(15,'2026-03-09 07:33:14.283084','CE',4,5,'STU2026090',3,22),(16,'2026-03-09 08:50:26.485992','CSE',3,7,'STU2026087',4,23),(17,'2026-03-11 07:06:26.871136','CE',3,4,'STU2026065',2,29),(18,'2026-03-16 06:01:30.621604','IT',3,2,'STU2026053',1,32),(19,'2026-03-30 06:26:15.665463','CE',3,6,'STU2026234',3,34);
/*!40000 ALTER TABLE `students` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `teachers`
--

DROP TABLE IF EXISTS `teachers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `teachers` (
  `teacher_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `department` varchar(100) DEFAULT NULL,
  `employee_id` varchar(20) DEFAULT NULL,
  `office_location` varchar(100) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`teacher_id`),
  UNIQUE KEY `UK_cd1k6xwg9jqtiwx9ybnxpmoh9` (`user_id`),
  UNIQUE KEY `UK_8xdh0jsitskwq83arwxvyihhc` (`employee_id`),
  CONSTRAINT `FKb8dct7w2j1vl1r2bpstw5isc0` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teachers`
--

LOCK TABLES `teachers` WRITE;
/*!40000 ALTER TABLE `teachers` DISABLE KEYS */;
INSERT INTO `teachers` VALUES (1,'2026-02-03 09:31:16.116597','Artificial Intelligence',NULL,NULL,'+91-9876543210',3),(2,'2026-03-01 10:28:42.300679','Computer Science','EMP2026001','Block A - 204','9876543210',8),(3,'2026-03-05 04:49:29.478922','Computer Engineering','EMP2026002','BLOCK A- 2025','9909234578',13),(4,'2026-03-05 05:16:43.655409','CE','EMP2026034','BLOCK B - 200','9909234534',15),(5,'2026-03-09 08:53:10.923164','CE','EMP2026023','BLOCK C - 203','9908121212',24),(6,'2026-03-11 06:45:08.100343',NULL,'8',NULL,NULL,27),(7,'2026-03-11 07:09:47.283940','CSE','EMP2026054','Block A 234','9089764345',30),(8,'2026-03-16 05:12:22.502806','CE','EMP2026019','BLOCK C - 205','9909934567',31);
/*!40000 ALTER TABLE `teachers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `email` varchar(100) NOT NULL,
  `full_name` varchar(100) NOT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `password_hash` varchar(255) NOT NULL,
  `role` int NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `username` varchar(50) NOT NULL,
  `resetToken` varchar(255) DEFAULT NULL,
  `tokenExpiry` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `UK_6dotkott2kjsp8vw4d0m25fb7` (`email`),
  UNIQUE KEY `UK_r43af9ap4edm43mmtq01oddj6` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'2026-02-03 09:06:23.243524','mavensharma@gmail.com','Manav Sharma',_binary '','$2a$10$UfYceJ5V.S67b5wuJAeoHu8WH3h15GdNrr.MhLvUbLZlVzmC2.oiC',3,'2026-02-03 09:06:23.243524','maven12',NULL,NULL),(3,'2026-02-03 09:31:16.101851','pareshShah@example.com','Paresh Shah',_binary '','$2a$10$9ZGzMd2Sm.8GsccBDK5sxufSMZmFuRw98awBmT2PNgxppxP7FF./G',2,'2026-02-03 09:31:16.101851','pareshShah',NULL,NULL),(4,'2026-02-03 10:07:10.350922','rohitverma@example.com','Rohit Verma',_binary '','$2a$10$x9BJiALTJtMuyAx1Km2FO.fudnjXLPva7mzuiOPtSwv.PyT3RPaHO',1,'2026-02-03 10:07:10.350922','RohitV',NULL,NULL),(8,'2026-03-01 10:28:42.280843','teacher1@gmail.com','John Teacher',_binary '','$2a$10$CmCFD1MzgCdYwQX16T7Tw.SZqJQX9m1DLFhYEks6vLaINRF4ciOqO',2,'2026-03-01 10:28:42.280843','teacher1',NULL,NULL),(10,'2026-03-03 06:50:08.408548','rahul_new@gmail.com','Rahul Sharma',_binary '','$2a$10$vnnKBVyDmeA2/Jai3xpZgeOb4N9od2m1MKywq9BjP1qO4vkytnA1O',3,'2026-03-03 06:50:08.408548','rahul_sharma_99',NULL,NULL),(11,'2026-03-04 14:01:34.537605','rohanjoshi@gmail.com','Rohan Joshi',_binary '','$2a$10$qzkHBsvwyABbj1BLtZmV6.Fm4w2b26Vj0woJP2EIbqpIATYB8MGTS',3,'2026-03-04 14:01:34.545470','rohan',NULL,NULL),(12,'2026-03-04 14:23:06.031583','dhruvsharma@gmail.com','dhruv sharma',_binary '','$2a$10$Jl.e0sj.l/Ecl.AH4AYl8u.1IHbHtV5iDoCOXWWh2d8hgMoQOR0L.',3,'2026-03-04 14:23:06.031583','dhruv',NULL,NULL),(13,'2026-03-05 04:49:29.384341','jimmyverma@gmail.com','Jimmy Verma',_binary '\0','$2a$10$7GyV5t4HOnn9KuZ7DIKZ9u3Di6SL99TommbwWU1VpseCdzRgLP/W6',2,'2026-03-11 05:58:19.950589','jimmy',NULL,NULL),(14,'2026-03-05 05:13:36.922143','amansharma@student.edu.in','Aman Sharma',_binary '','$2a$10$M6kcyC6ZgYjH14aojrq/9uD5.K9U5/AA2KcEMZpZ16zcWwwzW1dRS',3,'2026-03-05 05:13:36.922143','aman133',NULL,NULL),(15,'2026-03-05 05:16:43.623006','nandsharma@gmail.com','Nand Sharma',_binary '','$2a$10$Cn3bzMk7syaYgc1G20SqjOF1yMWnWGdOTBFusFst6xhnoP/8mEZSO',2,'2026-03-05 06:12:12.851809','nand234','F49C72','2026-03-05 06:27:12.736545'),(16,'2026-03-05 06:13:27.442901','sujalverma@student.edu.in','Sujal Verma',_binary '','$2a$10$nG6o2g.F0djNS.my3eZnf.WLnXSeKXYolfG9TeyuGaCM2FqwS5bvC',3,'2026-03-05 06:13:27.442901','sujalGeek',NULL,NULL),(20,'2026-03-05 07:25:22.308438','morwanisujal875@gmail.com','Rishi Sharma',_binary '','$2a$10$fVl6XlFz6L6P9W6Q8eH6uO7jR6K6u5P6v6z6z6z6z6z6z6z6z6z6z',3,'2026-03-05 07:25:48.524014','user2','3A859D','2026-03-05 07:40:48.502940'),(21,'2026-03-05 10:36:12.313925','sujalmorwani@gmail.com','Sujal Morwani',_binary '','$2a$10$tech4.tQCo11rOLxQY4bneq/NEeVg1cTXP9sghHuMsheKys37Qbfe',3,'2026-03-16 07:23:04.925310','sujal07','A03E97','2026-03-16 07:38:04.764884'),(22,'2026-03-09 07:33:14.229866','tempgamer@gmail.com','Techinal Gamer',_binary '','$2a$10$loWBIzLB7IsoJtUxpY0D..roRrlziPmansmAy4/GxpEu0JYWS2Dxe',3,'2026-03-09 07:33:14.230873','sujal234',NULL,NULL),(23,'2026-03-09 08:50:26.435999','harshilshah@gmail.com','Harshil Shah',_binary '','$2a$10$v4irBovfzRb8KgIeEig8Qu0V/DrQN9e8UWvsDjTHz5J2XFlDdmZp.',3,'2026-03-09 08:50:26.438179','user3',NULL,NULL),(24,'2026-03-09 08:53:10.914765','meetpatel@gmail.com','Meet Patel',_binary '','$2a$10$mWXlGfVHC48tkc5lXFroGeXWTCapLrD7cNF48BOk0S8FYxVOePBhm',2,'2026-03-09 08:53:10.914765','teacher2',NULL,NULL),(25,'2026-03-09 18:07:58.870204','hiteshsharma@gmail.com','Hitesh Sharma',_binary '','$2a$10$7eLScZDatbsc98YL9DqloOJZiO2Jbl.w.3kMIXknRplUyE7iYVHnq',1,'2026-03-09 18:07:58.870204','user23',NULL,NULL),(26,'2026-03-10 17:42:31.616310','premtrivedi@gmail.com','Prem Trivedi',_binary '','$2a$10$TyC07oNfd0GfXJ1WWdqkcuYUb1Vtf8NXxLG8WkePITflboWjWuRwq',1,'2026-03-10 17:42:31.616310','user10',NULL,NULL),(27,'2026-03-11 06:45:08.032625','ayn234@gmail.com','aryn234',_binary '','$2a$10$IDt8YFhtb2gQn2gde8YIbenz8eKPSrrkKlgErToLE03V.XkOcAhcu',2,'2026-03-11 06:48:03.028569','aaryan920',NULL,NULL),(29,'2026-03-11 07:06:26.865080','modimonilhitesh@gmail.com','Modi Monil Hitesh',_binary '','$2a$10$q/1Cc/N3y6pcAfD3rzcXYefBcaj4bDzMn5V2ufYLwdk/HORfLTp6O',3,'2026-03-11 07:07:26.830940','rinstruct',NULL,NULL),(30,'2026-03-11 07:09:47.273988','rahuljoshi@gmail.com','Rahul Verma Joshi',_binary '','$2a$10$JwwsNcMYhV9RK/qXSOO3Q.sIrs51ssm3pCp0uHPyiHWQ7gOLigHEm',2,'2026-03-11 07:09:47.273988','rverma',NULL,NULL),(31,'2026-03-16 05:12:22.434873','virajhowdini@gmail.com','Viraj Howdini',_binary '','$2a$10$uBn/ESQkxsnjowXt1gexfuQ9v5xzizrP5.F2jV0zauXQaGNmroeqW',2,'2026-03-16 05:12:22.434873','viraj12',NULL,NULL),(32,'2026-03-16 06:01:30.571074','ravijoshi@gmail.com','Ravi Joshi',_binary '','$2a$10$w5wBY.zA9030O/9w99JNku.PUDjwxAEusu/v4mnffb.FsCGadFljW',3,'2026-03-16 06:01:30.571074','user34',NULL,NULL),(33,'2026-03-16 06:05:43.682901','sureshvyas@gmail.com','Suresh Vyas',_binary '','$2a$10$FB5db3aFlV2fOMmxMFG/uufTAJlRDwaaGsN9nGqBpB0LYa6l03Wfi',1,'2026-03-16 06:05:43.682901','admin23',NULL,NULL),(34,'2026-03-30 06:26:15.545182','rohitsharma@gmail.com','Rohit Sharma',_binary '','$2a$10$nP2CKT58UXiqrrehvjy2IeFr4bVik0OaZILZ0zxhMZvfvH.MaEKwe',3,'2026-03-30 06:26:15.545701','userkt',NULL,NULL);
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

-- Dump completed on 2026-03-31  6:23:42
