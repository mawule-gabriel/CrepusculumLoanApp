-- Migration to add note column to payments table
ALTER TABLE payments ADD COLUMN IF NOT EXISTS note VARCHAR(255);
