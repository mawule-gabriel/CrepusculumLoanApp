-- Finalized Initial Schema for Flyway Baseline
-- Borrowers table
CREATE TABLE IF NOT EXISTS borrowers (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    ghana_card_number VARCHAR(255) UNIQUE NOT NULL,
    phone_number VARCHAR(255) UNIQUE NOT NULL,
    home_address_ghana VARCHAR(255) NOT NULL,
    destination_address VARCHAR(255) NOT NULL,
    profile_picture_path VARCHAR(255),
    password VARCHAR(255) NOT NULL,
    created_at DATE DEFAULT CURRENT_DATE,
    role VARCHAR(50) DEFAULT 'BORROWER'
);

-- Guarantors table
CREATE TABLE IF NOT EXISTS guarantors (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    phone_number VARCHAR(255) NOT NULL,
    relationship VARCHAR(255),
    borrower_id BIGINT NOT NULL UNIQUE,
    CONSTRAINT fk_guarantor_borrower FOREIGN KEY (borrower_id) REFERENCES borrowers(id) ON DELETE CASCADE
);

-- Loans table
CREATE TABLE IF NOT EXISTS loans (
    id BIGSERIAL PRIMARY KEY,
    amount DECIMAL(12, 2) NOT NULL,
    monthly_payment DECIMAL(12, 2) NOT NULL,
    total_paid DECIMAL(12, 2) DEFAULT 0.00,
    balance DECIMAL(12, 2),
    start_date DATE,
    end_date DATE,
    months_duration INTEGER DEFAULT 11 NOT NULL,
    borrower_id BIGINT NOT NULL UNIQUE,
    CONSTRAINT fk_loan_borrower FOREIGN KEY (borrower_id) REFERENCES borrowers(id) ON DELETE CASCADE
);

-- Payments table (initial state without 'note')
CREATE TABLE IF NOT EXISTS payments (
    id BIGSERIAL PRIMARY KEY,
    amount_paid DECIMAL(12, 2) NOT NULL,
    payment_date DATE NOT NULL DEFAULT CURRENT_DATE,
    recorded_by VARCHAR(255),
    loan_id BIGINT NOT NULL,
    CONSTRAINT fk_payment_loan FOREIGN KEY (loan_id) REFERENCES loans(id) ON DELETE CASCADE
);

-- Refresh Tokens table
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(500) UNIQUE NOT NULL,
    borrower_id BIGINT NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    revoked BOOLEAN DEFAULT FALSE NOT NULL,
    CONSTRAINT fk_refresh_token_borrower FOREIGN KEY (borrower_id) REFERENCES borrowers(id) ON DELETE CASCADE
);

-- Indexes for performance
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_token ON refresh_tokens(token);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_borrower_id ON refresh_tokens(borrower_id);
