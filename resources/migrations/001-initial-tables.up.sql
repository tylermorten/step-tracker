CREATE TABLE steps(
 id serial primary key,
 user_id integer not null,
 steps integer not null,
 created_on timestamp default current_timestamp)