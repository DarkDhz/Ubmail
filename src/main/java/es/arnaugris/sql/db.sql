DROP DATABASE IF exists ubmail;
create database ubmail;
USE ubmail;

DROP TABLE IF EXISTS domains;
CREATE TABLE domains (id int unsigned auto_increment,
                      domain tinytext not null,
                      primary key (id)
);

CREATE TABLE banned (id int unsigned auto_increment,
                     domain tinytext not null,
                     primary key (id)
);

INSERT INTO domains(domain) VALUES ("ub.edu");
INSERT INTO domains(domain) VALUES ("www.ub.edu");
INSERT INTO domains(domain) VALUES ("campusvirtual.ub.edu");

INSERT INTO banned(domain) VALUES ("upc.edu");
INSERT INTO banned(domain) VALUES ("uab.edu");
INSERT INTO banned(domain) VALUES ("upf.edu");

SELECT domain FROM domains;