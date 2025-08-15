# Database Scripts

## Current Setup
The application uses **automatic database initialization via Docker** with **NO FOREIGN KEYS** as requested. Hibernate is configured with `ddl-auto: none`.

## Database Setup

### Automatic Initialization (Default)
```bash
# Just start the system - database auto-initializes!
start.bat      # Windows
./start.sh     # Linux/Mac

# Or just infrastructure
docker-compose up -d
```

**How it works:** 
- PostgreSQL container automatically runs `01-init-schema.sql` then `02-sample-data.sql`
- Database, tables, and sample data are created automatically  
- Everything runs in Docker containers - no manual setup required!

### Manual Setup (If Needed)
1. **Start PostgreSQL:**
   ```bash
   docker-compose up -d postgres
   ```

2. **Create Database & User:**
   ```sql
   docker exec -i jitsu-postgres psql -U postgres -c "CREATE DATABASE jitsu_db;"
   docker exec -i jitsu-postgres psql -U postgres -c "CREATE USER jitsu_user WITH PASSWORD 'jitsu_password';"
   docker exec -i jitsu-postgres psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE jitsu_db TO jitsu_user;"
   ```

3. **Initialize Schema:**
   ```bash
   docker exec -i jitsu-postgres psql -U jitsu_user -d jitsu_db < database/init-schema.sql
   ```

4. **Load Sample Data:**
   ```bash
   docker exec -i jitsu-postgres psql -U jitsu_user -d jitsu_db < database/sample-data.sql
   ```

## Files
- `init-schema.sql` - Creates all tables **WITHOUT foreign keys** + indexes
- `sample-data.sql` - Loads sample booking sessions, tickets, and assignments
- `README.md` - This file

## Important Notes
- **NO FOREIGN KEYS** - All tables are independent as requested
- Database setup is **REQUIRED** before starting services
- Services will fail if database is not initialized
- User passwords in sample data are BCrypt hashed
- **Tickets table** includes `end_booking_time` field for efficient cleanup scheduling