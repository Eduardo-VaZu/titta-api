CREATE TABLE tbl_token_blacklist
(
    jti              VARCHAR(255) PRIMARY KEY,
    fecha_expiracion DATETIME NOT NULL
);

CREATE INDEX idx_token_blacklist_expiracion ON tbl_token_blacklist (fecha_expiracion);