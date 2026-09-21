INSERT INTO authors (first_name, last_name, bio) VALUES
  ('George', 'Orwell', 'English novelist and essayist, author of dystopian classics.'),
  ('Agatha', 'Christie', 'English writer known for detective novels.'),
  ('Robert', 'Martin', 'Software engineer, author of Clean Code.'),
  ('Andy', 'Weir', 'American novelist known for science-based fiction.');

INSERT INTO books (title, isbn, genre, published_year, total_copies, available_copies, author_id) VALUES
  ('1984', '9780451524935', 'Dystopian', 1949, 4, 3, 1),
  ('Animal Farm', '9780451526342', 'Satire', 1945, 3, 3, 1),
  ('Murder on the Orient Express', '9780062693662', 'Mystery', 1934, 2, 2, 2),
  ('Clean Code', '9780132350884', 'Software Engineering', 2008, 5, 4, 3),
  ('The Martian', '9780553418026', 'Science Fiction', 2011, 3, 3, 4);

INSERT INTO members (first_name, last_name, email, join_date) VALUES
  ('Nathan', 'Sodja', 'nathan@example.com', '2025-01-15'),
  ('Priya', 'Kapoor', 'priya@example.com', '2025-03-02'),
  ('Diego', 'Alvarez', 'diego@example.com', '2025-06-20');

INSERT INTO loans (book_id, member_id, loan_date, due_date, return_date, status) VALUES
  (1, 1, CURRENT_DATE - INTERVAL 3 DAY, CURRENT_DATE + INTERVAL 11 DAY, NULL, 'ACTIVE'),
  (4, 2, CURRENT_DATE - INTERVAL 20 DAY, CURRENT_DATE - INTERVAL 6 DAY, NULL, 'ACTIVE');
