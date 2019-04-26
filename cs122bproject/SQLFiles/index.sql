ALTER TABLE movies ADD FULLTEXT INDEX movieIndex(id, title); 
ALTER TABLE stars ADD FULLTEXT INDEX starIndex(id, name); 