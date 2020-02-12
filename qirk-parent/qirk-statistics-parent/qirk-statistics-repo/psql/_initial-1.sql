-- do it using postgres user
-- exec \q if you are already connected to clb_auth database
CREATE DATABASE qirk_stat WITH TEMPLATE=template0 ENCODING='UTF8';

-- requires password of hibernate user
\c clb_stat
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO hibernate;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO hibernate;
