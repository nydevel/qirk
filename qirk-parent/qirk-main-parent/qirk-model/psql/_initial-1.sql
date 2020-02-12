-- do it using postgres user
-- exec \q if you are already connected to clb database
DROP DATABASE qirk;
DROP USER hibernate;
CREATE DATABASE qirk WITH TEMPLATE=template0 ENCODING='UTF8';
CREATE USER hibernate WITH ENCRYPTED PASSWORD '121212';
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO hibernate;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO hibernate;

-- requires password of hibernate user
-- \c clb hibernate
