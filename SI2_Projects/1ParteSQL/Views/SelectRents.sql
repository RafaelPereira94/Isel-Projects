USE [Aenima]
GO

IF OBJECT_ID('dbo.SelectRents') IS NOT NULL
	DROP VIEW dbo.SelectRents
GO

CREATE VIEW dbo.SelectRents
AS
	SELECT serial_nr, date_time, end_date, rent_time, price, equipment_code, employer_number, client_code
	FROM dbo.Rent
	WHERE active = 'T'
GO
