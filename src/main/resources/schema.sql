  DROP TABLE IF EXISTS comments CASCADE;
  DROP TABLE IF EXISTS bookings CASCADE;
  DROP TABLE IF EXISTS requests CASCADE;
  DROP TABLE IF EXISTS items CASCADE;
  DROP TABLE IF EXISTS users CASCADE;

  CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(512) NOT NULL,
    CONSTRAINT pk_user_id PRIMARY KEY (id),
    CONSTRAINT uq_user_email UNIQUE (email)
  );

  CREATE TABLE IF NOT EXISTS requests (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  description VARCHAR(2000) NOT NULL,
  requestor_id BIGINT NOT NULL,
  CONSTRAINT pk_request_id PRIMARY KEY (id),
  CONSTRAINT fk_requestor_id_users FOREIGN KEY (requestor_id) REFERENCES users (id)
  );

  CREATE TABLE IF NOT EXISTS items (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(2000) NOT NULL,
    is_available BOOLEAN NOT NULL,
    owner_id BIGINT NOT NULL,
    request_id BIGINT,
    CONSTRAINT pk_item_id PRIMARY KEY (id),
    CONSTRAINT fk_owner_id_users FOREIGN KEY (owner_id) REFERENCES users (id)
  );

  CREATE TABLE IF NOT EXISTS bookings (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  end_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  item_id BIGINT ,
  booker_id BIGINT NOT NULL,
  status VARCHAR NOT NULL,
  CONSTRAINT pk_booking_id PRIMARY KEY (id),
  CONSTRAINT fk_item_id_items FOREIGN KEY (item_id) REFERENCES items (id),
  CONSTRAINT fk_booker_id_users FOREIGN KEY (booker_id) REFERENCES users(id)
  );

  CREATE TABLE IF NOT EXISTS comments (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  text VARCHAR(2000) NOT NULL,
  item_id BIGINT NOT NULL,
  author_id BIGINT NOT NULL,
  created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  CONSTRAINT pk_comment_id PRIMARY KEY(id),
  CONSTRAINT fk_item_id_comments_to_items FOREIGN KEY (item_id) REFERENCES items (id),
  CONSTRAINT fk_author_id_users FOREIGN KEY (author_id) REFERENCES users (id)
  );


