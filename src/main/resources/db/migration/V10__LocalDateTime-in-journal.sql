-- Changed Date to DateTime
ALTER TABLE journal ALTER COLUMN "date" TYPE TIMESTAMP USING "date"::TIMESTAMP;