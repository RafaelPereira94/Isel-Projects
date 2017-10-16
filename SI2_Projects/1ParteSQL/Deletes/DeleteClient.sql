USE [Aenima]

IF OBJECT_ID('dbo.DeleteClient') IS NOT NULL
	DROP PROCEDURE dbo.DeleteClient
GO

CREATE PROCEDURE dbo.DeleteClient
@id int
AS
SET xact_abort ON 
BEGIN TRANSACTION
IF @id IS NULL
	RAISERROR('Id cant be null',15,1)
IF NOT EXISTS(SELECT code FROM Client where code = @id)
	RAISERROR('No such client with the specific id!',15,1)
ELSE IF dbo.ClientHasRent(@id) = 'T'
	UPDATE dbo.Client SET active='F' WHERE code=@id
ELSE
BEGIN
	ALTER TABLE dbo.Client DISABLE TRIGGER trg_DeleteClient
	DELETE FROM dbo.Client WHERE code = @id
	ALTER TABLE dbo.Client ENABLE TRIGGER trg_DeleteClient			
END
COMMIT
GO