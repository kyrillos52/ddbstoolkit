--
-- Database: `ddbstoolkit`
--

-- --------------------------------------------------------

--
-- Table structure for table `Actor`
--

CREATE TABLE Actor (
  actor_id SERIAL PRIMARY KEY,
  actor_name varchar(45) NULL,
  film_ID int NOT NULL REFERENCES Film (film_ID)
);

-- --------------------------------------------------------

--
-- Table structure for table `Film`
--

CREATE TABLE Film (
  film_ID SERIAL PRIMARY KEY,
  film_name varchar(45) NULL,
  duration int NULL,
  creationDate timestamp DEFAULT current_timestamp,
  longField bigint NULL,
  floatField float NULL
);
