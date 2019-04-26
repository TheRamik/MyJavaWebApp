CREATE PROCEDURE add_movie
(IN mID varchar(10), movieTitle varchar(100), year int, director varchar(100), star varchar(100), starID varchar(10), starYear int, genre varchar(32), new_mID varchar(10), new_starID varchar(10))
BEGIN
	DECLARE movieExists varchar(100);
	DECLARE starExists varchar(100);
	DECLARE genreExists varchar(100);
	DECLARE genreID int;
	SELECT id INTO movieExists FROM movies WHERE id = mID;	
	SELECT id INTO starExists FROM stars WHERE id = starID;
	SELECT name INTO genreExists FROM genres WHERE name = genre;
	SELECT max(id) INTO genreID FROM genres;
	
	IF(movieExists IS NULL) THEN 
		INSERT INTO movies VALUES(new_mID, movieTitle, year, director);
		IF(starExists IS NULL) THEN
			INSERT INTO stars VALUES(new_starID, star, starYear);
			INSERT INTO stars_in_movies VALUES(new_starID, new_mID);
		ELSE
			INSERT INTO stars_in_movies VALUES(starID, new_mID);
		END IF;
		IF(genreExists IS NULL) THEN
			INSERT INTO genres (name) VALUES(genre);
			INSERT INTO genres_in_movies VALUES(genreID, new_mID);
		ELSE
			INSERT INTO genres_in_movies VALUES(genreID, new_mID);
		END IF;
	ELSE
		
		IF(starExists IS NULL) THEN
			INSERT INTO stars VALUES(new_starID, star, starYear);
			INSERT INTO stars_in_movies VALUES(new_starID, mID);
		ELSE
			INSERT INTO stars_in_movies VALUES(starID, mID);
		END IF;
		IF(genreExists IS NULL) THEN
			INSERT INTO genres (name) VALUES(genre);
			INSERT INTO genres_in_movies VALUES(genreID, mID);
		ELSE
			INSERT INTO genres_in_movies VALUES(genreID, mID);
		END IF;
		
	END IF;
END
$$