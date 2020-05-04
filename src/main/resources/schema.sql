drop all objects;

CREATE TABLE user (
  id int NOT NULL auto_increment,
  full_name nvarchar(255),
  password nvarchar(255),
  email nvarchar(255),

  CONSTRAINT pk_user PRIMARY KEY (id)
);

CREATE TABLE issuer (
  user_id int NOT NULL,
  issuer_info nvarchar(255),

  CONSTRAINT pk_issuer PRIMARY KEY (user_id),
  CONSTRAINT fk_issuer FOREIGN KEY (user_id) REFERENCES user(id) ON UPDATE CASCADE
                                                                 ON DELETE NO ACTION
);

CREATE TABLE bidder (
  user_id int NOT NULL,
  bidder_info nvarchar(255),

  CONSTRAINT pk_bidder PRIMARY KEY (user_id),
  CONSTRAINT fk_bidder FOREIGN KEY (user_id) REFERENCES user(id) ON UPDATE CASCADE
                                                                 ON DELETE NO ACTION
);

CREATE TABLE tender (
  id int NOT NULL auto_increment,
  description nvarchar(255),
  created_at timestamp DEFAULT (LOCALTIMESTAMP()),
  status nvarchar(255) NOT NULL,
  issuer_id int NOT NULL,

  CONSTRAINT pk_tender PRIMARY KEY (id),
  CONSTRAINT fk_issuer_id FOREIGN KEY (issuer_id) REFERENCES issuer(user_id) ON UPDATE CASCADE
                                                                        ON DELETE NO ACTION
);


CREATE TABLE tender_offers (
  id int NOT NULL auto_increment,
  tender_id int NOT NULL,
  bidder_id int NOT NULL,
  offer_info nvarchar(255),
  status nvarchar(255) NOT NULL DEFAULT 'PENDING',
  created_at timestamp DEFAULT (LOCALTIMESTAMP()),

  CONSTRAINT pk_tender_offers PRIMARY KEY (id),

  CONSTRAINT fk_tender_id FOREIGN KEY (tender_id) REFERENCES tender(id) ON UPDATE CASCADE
                                                                        ON DELETE NO ACTION,
  CONSTRAINT fk_bidder_id FOREIGN KEY (bidder_id) REFERENCES bidder(user_id) ON UPDATE CASCADE
                                                                        ON DELETE NO ACTION
);