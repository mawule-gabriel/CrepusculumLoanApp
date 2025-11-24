
CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(500) UNIQUE NOT NULL,
    borrower_id BIGINT NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    revoked BOOLEAN DEFAULT FALSE NOT NULL,
    CONSTRAINT fk_refresh_token_borrower FOREIGN KEY (borrower_id) 
        REFERENCES borrowers(id) ON DELETE CASCADE
);

-- Create indexes for better query performance
CREATE INDEX idx_refresh_tokens_token ON refresh_tokens(token);
CREATE INDEX idx_refresh_tokens_borrower_id ON refresh_tokens(borrower_id);
CREATE INDEX idx_refresh_tokens_expiry_date ON refresh_tokens(expiry_date);


COMMENT ON TABLE refresh_tokens IS 'Stores refresh tokens for JWT authentication with token rotation support';
COMMENT ON COLUMN refresh_tokens.token IS 'UUID-based refresh token string';
COMMENT ON COLUMN refresh_tokens.expiry_date IS 'Token expiration timestamp (7 days from creation)';
COMMENT ON COLUMN refresh_tokens.revoked IS 'Flag indicating if token has been revoked (for token rotation)';
