USE [Aenima]
GO

IF OBJECT_ID('dbo.InsertClient') IS NOT NULL
	DROP PROCEDURE dbo.InsertClient
GO

CREATE PROCEDURE dbo.InsertClient
	@name varchar(25) = NULL,
	@nif int = NULL,
	@_address varchar(50) = NULL,
	@code int output
AS
SET xact_abort ON 
BEGIN TRANSACTION
	INSERT INTO dbo.Client(name, nif, _address) VALUES (@name, @nif, @_address)
	SELECT @code = SCOPE_IDENTITY()
	PRINT @code
COMMIT
RETURN
GO