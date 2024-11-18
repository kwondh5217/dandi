-- 테이블 생성
CREATE TABLE Bag (
                     bagOrder TINYINT NOT NULL,
                     enabled CHAR(1) NOT NULL,
                     id INTEGER NOT NULL AUTO_INCREMENT,
                     memberId INTEGER NOT NULL,
                     createdAt DATETIME(6),
                     name VARCHAR(20) NOT NULL,
                     PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE BagItem (
                         bag_id INTEGER NOT NULL,
                         id INTEGER NOT NULL AUTO_INCREMENT,
                         item_id INTEGER NOT NULL,
                         item_order TINYINT NOT NULL,
                         createdAt DATETIME(6),
                         PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE BinlogPosition (
                                id INTEGER NOT NULL,
                                binlogPosition BIGINT,
                                updatedAt DATETIME(6),
                                binlogFileName VARCHAR(255),
                                PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE FoundItem (
                           id INTEGER NOT NULL AUTO_INCREMENT,
                           lat FLOAT(30),
                           lon FLOAT(30),
                           memberId INTEGER,
                           createdAt DATETIME(6),
                           endedAt DATETIME(6),
                           foundAt DATETIME(6),
                           address VARCHAR(255),
                           description VARCHAR(255),
                           savePlace VARCHAR(255),
                           type ENUM ('CREDIT', 'ID', 'OTHER'),
                           PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE LostItem (
                          endRouteId INTEGER,
                          id INTEGER NOT NULL AUTO_INCREMENT,
                          memberId INTEGER,
                          startRouteId INTEGER,
                          createdAt DATETIME(6),
                          endedAt DATETIME(6),
                          lostAt DATETIME(6),
                          itemDescription VARCHAR(255),
                          situationDescription VARCHAR(255),
                          PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE Comment (
                         found_item_id INTEGER,
                         id INTEGER NOT NULL AUTO_INCREMENT,
                         lost_item_id INTEGER,
                         member_id INTEGER,
                         parent_id INTEGER,
                         createdAt DATETIME(6),
                         role_type VARCHAR(10) NOT NULL,
                         content VARCHAR(255),
                         PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE Image (
                       found_id INTEGER,
                       lost_id INTEGER,
                       type VARCHAR(4) NOT NULL,
                       createdAt DATETIME(6),
                       role_type VARCHAR(10) NOT NULL,
                       id BINARY(16) NOT NULL,
                       PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE Item (
                      colorKey TINYINT NOT NULL,
                      id INTEGER NOT NULL AUTO_INCREMENT,
                      itemOrder TINYINT NOT NULL,
                      memberId INTEGER NOT NULL,
                      createdAt DATETIME(6),
                      emoticon VARCHAR(10) NOT NULL,
                      name VARCHAR(20) NOT NULL,
                      PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE LostItemAuth (
                              id INTEGER NOT NULL AUTO_INCREMENT,
                              is_read CHAR(1) NOT NULL,
                              lost_id INTEGER,
                              memberId INTEGER,
                              PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE Member (
                        bagId INTEGER,
                        commentAlarm BIT NOT NULL,
                        foundItemAlarm BIT NOT NULL,
                        id INTEGER NOT NULL AUTO_INCREMENT,
                        lostItemAlarm BIT NOT NULL,
                        createdAt DATETIME(6),
                        nickname VARCHAR(15) NOT NULL,
                        email VARCHAR(30),
                        password VARCHAR(70),
                        fcmToken VARCHAR(255),
                        memberStatus ENUM ('ACTIVE', 'BANNED', 'DISABLED') NOT NULL,
                        status ENUM ('PENDING', 'VERIFIED') NOT NULL,
                        PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE Notification (
                              commentId INTEGER,
                              confirmed CHAR(1) NOT NULL CHECK (confirmed IN ('N', 'Y')),
                              foundItemId INTEGER,
                              id INTEGER NOT NULL AUTO_INCREMENT,
                              lostItemId INTEGER,
                              memberId INTEGER,
                              routeId INTEGER,
                              createdAt DATETIME(6),
                              title VARCHAR(30),
                              TYPE VARCHAR(31) NOT NULL,
                              body VARCHAR(255),
                              PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE OutboxEvent (
                             time DATETIME(6),
                             eventId VARCHAR(255) NOT NULL,
                             eventType VARCHAR(255),
                             payload LONGTEXT,
                             status ENUM ('COMPLETED', 'FAILED', 'PENDING'),
                             PRIMARY KEY (eventId)
) ENGINE=InnoDB;

CREATE TABLE Quiz (
                      found_id INTEGER,
                      id INTEGER NOT NULL AUTO_INCREMENT,
                      answer_id BINARY(16),
                      PRIMARY KEY (id),
                      UNIQUE (found_id),
                      UNIQUE (answer_id)
) ENGINE=InnoDB;

CREATE TABLE QuizImage (
                           id INTEGER NOT NULL AUTO_INCREMENT,
                           quiz_id INTEGER,
                           image_id BINARY(16),
                           PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE QuizSolver (
                            id INTEGER NOT NULL AUTO_INCREMENT,
                            memberId INTEGER NOT NULL,
                            quiz_id INTEGER,
                            solved CHAR(1) NOT NULL CHECK (solved IN ('N', 'Y')),
                            PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE Route (
                       id INTEGER NOT NULL AUTO_INCREMENT,
                       memberId INTEGER,
                       skip CHAR(1),
                       createdAt DATETIME(6),
                       endedAt DATETIME(6),
                       endAddress VARCHAR(100),
                       startAddress VARCHAR(100),
                       snapshot VARCHAR(2000),
                       radiusTrack GEOMETRY NOT NULL,
                       track GEOMETRY NOT NULL,
                       PRIMARY KEY (id)
) ENGINE=InnoDB;

-- 외래 키 추가
ALTER TABLE BagItem ADD CONSTRAINT FK_BagItem_Bag FOREIGN KEY (bag_id) REFERENCES Bag (id);
ALTER TABLE Comment ADD CONSTRAINT FK_Comment_FoundItem FOREIGN KEY (found_item_id) REFERENCES FoundItem (id);
ALTER TABLE Comment ADD CONSTRAINT FK_Comment_LostItem FOREIGN KEY (lost_item_id) REFERENCES LostItem (id);
ALTER TABLE Comment ADD CONSTRAINT FK_Comment_Parent FOREIGN KEY (parent_id) REFERENCES Comment (id);
ALTER TABLE Image ADD CONSTRAINT FK_Image_FoundItem FOREIGN KEY (found_id) REFERENCES FoundItem (id);
ALTER TABLE Image ADD CONSTRAINT FK_Image_LostItem FOREIGN KEY (lost_id) REFERENCES LostItem (id);
ALTER TABLE LostItemAuth ADD CONSTRAINT FK_LostItemAuth_LostItem FOREIGN KEY (lost_id) REFERENCES LostItem (id);
ALTER TABLE Member ADD CONSTRAINT FK_Member_Bag FOREIGN KEY (bagId) REFERENCES Bag (id);
ALTER TABLE Quiz ADD CONSTRAINT FK_Quiz_FoundItem FOREIGN KEY (found_id) REFERENCES FoundItem (id);
ALTER TABLE Quiz ADD CONSTRAINT FK_Quiz_Image FOREIGN KEY (answer_id) REFERENCES Image (id);
ALTER TABLE QuizImage ADD CONSTRAINT FK_QuizImage_Quiz FOREIGN KEY (quiz_id) REFERENCES Quiz (id);
ALTER TABLE QuizImage ADD CONSTRAINT FK_QuizImage_Image FOREIGN KEY (image_id) REFERENCES Image (id);
ALTER TABLE QuizSolver ADD CONSTRAINT FK_QuizSolver_Quiz FOREIGN KEY (quiz_id) REFERENCES Quiz (id);
