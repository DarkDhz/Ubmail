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

INSERT INTO banned(domain) VALUES ("ub.com");
INSERT INTO banned(domain) VALUES ("ub.es");
INSERT INTO banned(domain) VALUES ("ub.cat");
INSERT INTO banned(domain) VALUES ("ub.en");
INSERT INTO banned(domain) VALUES ("ub.ovh");
INSERT INTO banned(domain) VALUES ("ub.club");
INSERT INTO banned(domain) VALUES ("ub.gov");
INSERT INTO banned(domain) VALUES ("ub.mil");
INSERT INTO banned(domain) VALUES ("ub.net");
INSERT INTO banned(domain) VALUES ("ub.org");
INSERT INTO banned(domain) VALUES ("ub.bw");

SELECT domain FROM domains;