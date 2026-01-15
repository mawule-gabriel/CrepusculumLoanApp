ALTER TABLE borrowers ADD COLUMN IF NOT EXISTS email VARCHAR(255);
ALTER TABLE borrowers ADD COLUMN IF NOT EXISTS password_reset_required BOOLEAN DEFAULT FALSE;

CREATE TABLE IF NOT EXISTS password_reset_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(64) UNIQUE NOT NULL,
    borrower_id BIGINT NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    used BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reset_token_borrower FOREIGN KEY (borrower_id) REFERENCES borrowers(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_password_reset_tokens_token ON password_reset_tokens(token);
