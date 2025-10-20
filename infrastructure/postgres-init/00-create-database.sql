CREATE USER passengers_user WITH PASSWORD 'dev';
CREATE USER drivers_user WITH PASSWORD 'dev';
CREATE USER trips_user WITH PASSWORD 'dev';
CREATE USER trips_rating_user WITH PASSWORD 'dev';
CREATE USER keycloak WITH PASSWORD 'dev';

CREATE DATABASE keycloak OWNER keycloak;
CREATE DATABASE passengers_db OWNER passengers_user;
CREATE DATABASE drivers_db OWNER drivers_user;
CREATE DATABASE trips_db OWNER trips_user;
CREATE DATABASE trips_rating_db OWNER trips_rating_user;

GRANT ALL PRIVILEGES ON DATABASE keycloak TO keycloak;
GRANT ALL PRIVILEGES ON DATABASE passengers_db TO passengers_user;
GRANT ALL PRIVILEGES ON DATABASE drivers_db TO drivers_user;
GRANT ALL PRIVILEGES ON DATABASE trips_db TO trips_user;
GRANT ALL PRIVILEGES ON DATABASE trips_rating_db TO trips_rating_user;
