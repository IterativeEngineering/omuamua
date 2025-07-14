#!/bin/bash
set -e

# Wait for master to be ready - use localhost since we're in host network mode
until PGPASSWORD="${POSTGRES_PASSWORD}" psql -h localhost -p 5432 -U ${POSTGRES_USER} -d postgres -c '\l'; do
  echo "Waiting for master to be ready..."
  sleep 5
done

# Stop postgresql service
pg_ctl -D "$PGDATA" -m fast -w stop

# Clear the data directory
rm -rf "${PGDATA}"/*

# Backup main server
PGPASSWORD="${POSTGRES_PASSWORD}" pg_basebackup -h localhost -p 5432 -D "${PGDATA}" -U replicator -v -P --wal-method=stream

# Create standby.signal file
touch "${PGDATA}/standby.signal"
chmod 600 "${PGDATA}/standby.signal"

# Create directory for archive if it doesn't exist
if [ ! -d "${PGDATA}/archive" ]; then
    mkdir -p "${PGDATA}/archive"
    chmod 700 "${PGDATA}/archive"
fi

# Create or update postgresql.auto.conf
cat > "${PGDATA}/postgresql.auto.conf" << EOF
primary_conninfo = 'host=localhost port=5432 user=replicator password=${POSTGRES_PASSWORD} application_name=replica1'
restore_command = 'cp ${PGDATA}/archive/%f %p'
EOF
chmod 600 "${PGDATA}/postgresql.auto.conf"

# Start postgresql service
pg_ctl -D "$PGDATA" -w start
