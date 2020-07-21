create table oauth_access_token (
token_id VARCHAR(255),
token bytea,
authentication_id VARCHAR(255),
user_name VARCHAR(255),
client_id VARCHAR(255),
authentication bytea,
refresh_token VARCHAR(255)
);

create table oauth_refresh_token (
token_id VARCHAR(255),
token bytea,
authentication bytea
);