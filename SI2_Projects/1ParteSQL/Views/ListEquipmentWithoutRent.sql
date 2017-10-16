USE [Aenima]
GO

IF OBJECT_ID ('SelectEquipmentsWithoutRentInTheLastWeek') IS NOT NULL
	DROP VIEW dbo.SelectEquipmentsWithoutRentInTheLastWeek
GO

CREATE VIEW dbo.SelectEquipmentsWithoutRentInTheLastWeek
AS
	SELECT code, equipment_name, descript
	FROM dbo.Equipment
	EXCEPT
	SELECT code, equipment_name, descript
	FROM dbo.Equipment JOIN dbo.Rent 
	ON(code = equipment_code)
	WHERE end_date > DATEADD(DAY,-7,GETDATE())
GO