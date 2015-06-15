CREATE TABLE `Actor` (
	`actor_id`	INTEGER PRIMARY KEY AUTOINCREMENT,
	`actor_name`	TEXT,
	`film_ID`	INTEGER
);

CREATE TABLE `Film` (
	`film_ID`	INTEGER PRIMARY KEY AUTOINCREMENT,
	`film_name`	TEXT,
	`duration`	INTEGER,
	`creationDate`	INTEGER,
	`longField`	INTEGER,
	`floatField`	REAL
);