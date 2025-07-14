#!/bin/bash
set -e

# Create replicator user
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE USER replicator WITH REPLICATION ENCRYPTED PASSWORD '$POSTGRES_PASSWORD';
    CREATE SCHEMA archive;
    CREATE TABLE IF NOT EXISTS archive.replication_status (id SERIAL PRIMARY KEY, last_updated TIMESTAMP WITH TIME ZONE DEFAULT NOW());
    INSERT INTO archive.replication_status (last_updated) VALUES (NOW());

    -- Ensure postgres user has proper password authentication
    ALTER USER postgres WITH PASSWORD '$POSTGRES_PASSWORD';
EOSQL

# Create archive directory if it doesn't exist
if [ ! -d "/var/lib/postgresql/data/archive" ]; then
    mkdir -p /var/lib/postgresql/data/archive
    chmod 700 /var/lib/postgresql/data/archive
fi
