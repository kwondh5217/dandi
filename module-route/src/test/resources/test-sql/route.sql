CREATE TABLE IF NOT EXISTS route (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    member_id INT NOT NULL,
    track LINESTRING NOT NULL,
    radius_track POLYGON NOT NULL,
    skip CHAR(1) NOT NULL,
    snapshot VARCHAR(2000) NULL,
    start_address VARCHAR(100) NULL,
    end_address VARCHAR(100) NULL,
    created_at DATETIME NOT NULL,
    ended_at DATETIME NULL
);
