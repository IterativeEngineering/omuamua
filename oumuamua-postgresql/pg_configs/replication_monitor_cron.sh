#!/bin/bash

PGHOST=localhost
PGPORT=5433
PGUSER=postgres
PGDATABASE=postgres
export PGPASSWORD=postgres



# Log location

# Check if in recovery and if replication is active
is_replica_active=$(psql -h "$PGHOST" -p "$PGPORT" -U "$PGUSER" -tAc "SELECT status FROM pg_stat_wal_receiver;" 2>/dev/null)

if [ "$is_replica_active" != "streaming" ]; then
    echo "$(date): Replication down - blocking connections"

    # Overwrite pg_hba.conf to reject all connections except the monitor user
    echo "host all all 0.0.0.0/0 reject" > /var/lib/postgresql/data/pg_hba.conf
    echo "host all postgres 127.0.0.1/32 trust" >> /var/lib/postgresql/data/pg_hba.conf

    # Reload config
    psql -h "$PGHOST" -p "$PGPORT" -U "$PGUSER" -c "SELECT pg_reload_conf();"

    # Terminate all other connections
    psql -h "$PGHOST" -p "$PGPORT" -U "$PGUSER" -d "$PGDATABASE" -tAc "SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE usename != 'replica_monitor';"

else
    echo "$(date): Replication OK - allowing connections"

    # Restore pg_hba.conf to allow normal access
    cp /etc/postgresql/pg_hba_base.conf /etc/postgresql/pg_hba.conf
    psql -h "$PGHOST" -p "$PGPORT" -U "$PGUSER" -c "SELECT pg_reload_conf();"
fi