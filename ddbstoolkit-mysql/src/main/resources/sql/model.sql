-- phpMyAdmin SQL Dump
-- version 4.1.9
-- http://www.phpmyadmin.net
--
-- Host: localhost:8889
-- Generation Time: Apr 10, 2015 at 09:25 PM
-- Server version: 5.5.34
-- PHP Version: 5.5.10

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

--
-- Database: `ddbstoolkit`
--

-- --------------------------------------------------------

--
-- Table structure for table `Actor`
--

CREATE TABLE `Actor` (
  `actor_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `actor_name` varchar(45) NULL,
  `film_ID` int(10) unsigned NULL,
  PRIMARY KEY (`actor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `Film`
--

CREATE TABLE `Film` (
  `film_ID` int(11) NOT NULL AUTO_INCREMENT,
  `film_name` varchar(45) NULL,
  `duration` int(11) NULL,
  `creationDate` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `longField` bigint(20) NULL,
  `floatField` float NULL,
  PRIMARY KEY (`film_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;
