create table image (
  id serial primary key,
  name text,
  size int,
  upload_at timestamp,
  path text
);