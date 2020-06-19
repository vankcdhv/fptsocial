CREATE DATABASE FPTSocial
GO

USE FPTSocial
GO

CREATE TABLE accounts
(
	ID INT IDENTITY,
	UserName VARCHAR(50) UNIQUE NOT NULL,
	Password VARCHAR(50) NOT NULL, 
	Mail VARCHAR(100) NOT NULL UNIQUE,
	Status BIT NOT NULL DEFAULT 1,
	CONSTRAINT PK_Account PRIMARY KEY(ID)
)

CREATE TABLE users
(
	ID INT IDENTITY,
	AccountID INT,
	FirstName NVARCHAR(50)  NOT NULL,
	LastName NVARCHAR(50) NOT NULL,
	Gender BIT DEFAULT 1 NOT NULL,
	DOB DATE NOT NULL,
	Course INT NOT NULL,
	Department NVARCHAR(500) NOT NULL,
	Avatar VARCHAR(500),
	Cover VARCHAR(500),
	StartDate DATE,
	CONSTRAINT PK_Users PRIMARY KEY(ID),
	CONSTRAINT FK_Users_Account FOREIGN KEY(AccountID) REFERENCES dbo.accounts(ID)
)

CREATE TABLE Posts
(
	ID INT IDENTITY,
	Content NVARCHAR(MAX),
	UserID INT NOT NULL,
	Scope INT DEFAULT 1,
	CONSTRAINT PK_Posts PRIMARY KEY(ID),
	CONSTRAINT FK_Posts_Users FOREIGN KEY(UserID) REFERENCES dbo.users(ID)
)

CREATE TABLE Images
(
	ID INT IDENTITY,
	Url VARCHAR(500) NOT NULL,
	PostID INT NOT NULL,
	UserID INT NOT NULL,
	CONSTRAINT PK_Images PRIMARY KEY(ID),
	CONSTRAINT FK_Images_Posts FOREIGN KEY(PostID) REFERENCES dbo.Posts(ID),
	CONSTRAINT FK_Images_Users FOREIGN KEY(UserID) REFERENCES dbo.users(ID)
)

CREATE TABLE Friends
(
	UserAID INT,
	UserBID INT,
	StartDate DATE,
	CONSTRAINT PK_Friends PRIMARY KEY(UserAID, UserBID),
	CONSTRAINT FK_UserA_Users FOREIGN KEY(UserAID) REFERENCES dbo.users(ID),
	CONSTRAINT FK_UserB_Users FOREIGN KEY(UserBID) REFERENCES dbo.users(ID),
)

CREATE TABLE Conversations
(
	ID INT IDENTITY,
	StartDate DATE,
	LastTime DATETIME,
	CONSTRAINT PK_Conversations PRIMARY KEY(ID)
)

CREATE TABLE Messages
(
	ID INT IDENTITY,
	ConversationID INT,
	Content NVARCHAR(MAX),
	SendTime DATETIME,
	CONSTRAINT PK_Messages PRIMARY KEY(ID)
)

CREATE TABLE ConversationMember
(
	ConverID INT,
	AccountID INT,
	CONSTRAINT PK_ConversationMember PRIMARY KEY(ConverID, AccountID),
	CONSTRAINT FK_ConversationMember_Accounts FOREIGN KEY(AccountID) REFERENCES dbo.accounts(ID),
	CONSTRAINT FK_ConversationMember_Conversation FOREIGN KEY(ConverID) REFERENCES dbo.Conversations(ID)
)

select * from accounts