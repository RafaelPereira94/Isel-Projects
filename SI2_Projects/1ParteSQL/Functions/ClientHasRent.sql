USE [Aenima]
GO

IF OBJECT_ID ('dbo.ClientHasRent') IS NOT NULL  
    DROP FUNCTION dbo.ClientHasRent
GO 

CREATE FUNCTION dbo.ClientHasRent (@clientId INT)
RETURNS varchar(1)
AS
BEGIN
	IF EXISTS (
		SELECT * 
		FROM dbo.Client JOIN dbo.Rent ON (code = client_code) 
		WHERE code = @clientId)
			RETURN 'T'
	RETURN 'F'
END
GO