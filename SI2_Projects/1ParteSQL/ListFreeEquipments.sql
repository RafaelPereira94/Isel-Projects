USE [Aenima]
GO

IF OBJECT_ID ('ListFreeEquipments') IS NOT NULL
	DROP PROCEDURE dbo.ListFreeEquipments
GO

CREATE PROCEDURE dbo.ListFreeEquipments 
	@date DATETIME,
	@equipType_Name VARCHAR(25)
AS
BEGIN TRANSACTION
	SELECT distinct Equipment.equipment_name,Equipment.code 
	FROM dbo.Equipment JOIN dbo.EquipmentType ON (Equipment.equipment_name = EquipmentType.name)
	JOIN Rent ON (Equipment.code = Rent.equipment_code) 
	WHERE rent.end_date < GETDATE() AND equipment_name = @equipType_Name
COMMIT
GO