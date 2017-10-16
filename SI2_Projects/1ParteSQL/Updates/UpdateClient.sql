USE [Aenima]
GO

IF OBJECT_ID('dbo.UpdateClient') IS NOT NULL
	DROP PROCEDURE dbo.UpdateClient
GO

CREATE PROCEDURE dbo.UpdateClient
@id int,
@nif int = NULL,
@name varchar(25) = NULL,
@_address varchar(50) = NULL
AS
SET xact_abort on 
SET TRANSACTION ISOLATION LEVEL  REPEATABLE READ --porque faz varias leituras
BEGIN TRANSACTION
IF (SELECT active FROM dbo.Client WHERE code=@id) = 'F'
	PRINT N'There is no Client with that id'
ELSE IF @id IS NOT NULL
BEGIN
	IF @nif IS NULL
		SET @nif = (SELECT nif FROM dbo.Client WHERE code = @id)
	IF @name IS NULL
		SET @name = (SELECT name FROM dbo.Client WHERE code = @id)
	IF @_address IS NULL
		SET @_address = (SELECT _address FROM dbo.Client WHERE code = @id)

	UPDATE dbo.Client SET name=@name, @nif=nif, _address=@_address WHERE code=@id
END
ELSE
	PRINT N'Please provide an id to identify the client'
COMMIT