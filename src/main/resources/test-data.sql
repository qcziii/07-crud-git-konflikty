DELETE FROM book;
DELETE FROM author;

ALTER TABLE book ALTER COLUMN id RESTART WITH 1;
ALTER TABLE author ALTER COLUMN id RESTART WITH 1;

INSERT INTO author (name) VALUES ('Stanisław Lem');
INSERT INTO author (name) VALUES ('Olga Tokarczuk');

INSERT INTO book (title, isbn, available_copies, author_id) VALUES ('Solaris', '978-83-08-04907-1', 2, 1);
INSERT INTO book (title, isbn, available_copies, author_id) VALUES ('Cyberiada', '978-83-08-06175-2', 1, 1);
INSERT INTO book (title, isbn, available_copies, author_id) VALUES ('Bieguni', '978-83-08-04151-8', 5, 2);