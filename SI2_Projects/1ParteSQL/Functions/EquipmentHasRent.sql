USE [Aenima]
GO

IF OBJECT_ID ('dbo.EquipmentHasRent') IS NOT NULL  
    DROP FUNCTION dbo.EquipmentHasRent
GO 

CREATE FUNCTION dbo.EquipmentHasRent (@EquipCode INT)
RETURNS varchar(1)
AS
BEGIN
	IF EXISTS (SELECT * 
				FROM dbo.Equipment JOIN dbo.EquipmentType ON(dbo.Equipment.equipment_name = EquipmentType.name)
				JOIN dbo.Rent ON(dbo.Equipment.code = dbo.Rent.equipment_code) 
				WHERE dbo.Equipment.code = @EquipCode)
			RETURN 'T'
	RETURN 'F'
END
GO