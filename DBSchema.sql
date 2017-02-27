CREATE TABLE USERS (
	username varchar(10) NOT NULL,
	password varchar(32) NOT NULL,
	nickname varchar(20) NOT NULL,
	description varchar(50),
	avatarUrl varchar(500),
	PRIMARY KEY(username))

CREATE TABLE CHANNELS (
	channelId varchar(30) NOT NULL,
	description varchar(500),
	numberOfSubscribers integer,
	isPublic boolean,
	PRIMARY KEY(channelId))
	
CREATE TABLE SUBSCRIPTIONS (
	channelId varchar(30) NOT NULL,
	userId varchar(10) NOT NULL,
	subscriptionTime timestamp,
	numberOfReadMessages integer,
	unreadMessages integer,
	unreadMentionedMessages integer,
	FOREIGN KEY(userId) REFERENCES USERS (username),
	FOREIGN KEY(channelId) REFERENCES CHANNELS (channelId))
	
CREATE TABLE MESSAGES (
	id integer NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
	channelId varchar(30) NOT NULL,
	userId varchar(10) NOT NULL,
	messageTime timestamp,
	lastModified timestamp,
	repliedToId integer,
	content varchar(500),
	PRIMARY KEY(id),
	FOREIGN KEY(userId) REFERENCES USERS (username),
	FOREIGN KEY(channelId) REFERENCES CHANNELS (channelId))
	

	
INSERT INTO USERS VALUES(?,?,?,?,?)

INSERT INTO CHANNELS VALUES(?,?,?,?)

INSERT INTO SUBSCRIPTIONS VALUES(?,?,?,?,?,?)

INSERT INTO MESSAGES (channelId, userId, messageTime, lastModified, repliedToId, content) VALUES(?,?,?,?,?,?)



DELETE FROM SUBSCRIPTIONS WHERE channelId=? AND userId=?



UPDATE CHANNELS SET numberOfSubscribers=? WHERE channelId=?

UPDATE SUBSCRIPTIONS SET numberOfReadMessages=?, unreadMessages=?, unreadMentionedMessages=? WHERE channelId=? AND userId=?

UPDATE MESSAGES SET lastModified=? WHERE id=?



SELECT * FROM USERS WHERE username=?

SELECT * FROM USERS WHERE username=? AND password=?

SELECT * FROM USERS

SELECT * FROM CHANNELS WHERE channelId=?

SELECT * FROM CHANNELS WHERE isPublic=true AND channelId=?

SELECT * FROM CHANNELS

SELECT * FROM SUBSCRIPTIONS WHERE channelId=?

SELECT * FROM SUBSCRIPTIONS WHERE userId=?

SELECT * FROM SUBSCRIPTIONS WHERE channelId=? AND userId=?

SELECT * FROM SUBSCRIPTIONS

SELECT * FROM MESSAGES WHERE channelId=? AND lastModified>=? AND repliedToId=? ORDER BY lastModified, messageTime, id

SELECT * FROM MESSAGES WHERE id=?

SELECT * FROM MESSAGES WHERE repliedToId=?

SELECT * FROM MESSAGES
