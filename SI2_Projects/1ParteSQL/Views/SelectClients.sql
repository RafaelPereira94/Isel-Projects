USE [Aenima]
GO

IF OBJECT_ID('dbo.SelectClients') IS NOT NULL
	DROP VIEW dbo.SelectClients
GO

CREATE VIEW dbo.SelectClients
AS
	SELECT code, name, nif, _address
	FROM dbo.Client
	WHERE active = 'T'
GO