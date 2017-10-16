USE	[Aenima]
GO

IF OBJECT_ID ('dbo.SelectEquipment') IS NOT NULL
    DROP VIEW dbo.SelectEquipment
GO

CREATE VIEW dbo.SelectEquipment 
AS
	SELECT code,descript,equipment_name
	FROM Equipment
	WHERE active = 'T'
GO