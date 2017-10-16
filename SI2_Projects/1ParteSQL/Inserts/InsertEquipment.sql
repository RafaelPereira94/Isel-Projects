use[Aenima]
GO

IF OBJECT_ID ('InsertEquipment') IS NOT NULL
	DROP PROCEDURE dbo.InsertEquipment
GO

CREATE PROCEDURE dbo.InsertEquipment 
@description varchar(50) = NULL, 
@equip_name varchar(25) = NULL,
@code int
AS
SET xact_abort ON 
BEGIN TRANSACTION
if(@equip_name is NULL)
	RAISERROR('Please provide an equipment type!',15,1)
ELSE
BEGIN
	IF EXISTS(SELECT name FROM EquipmentType WHERE name = @equip_name)
		BEGIN
			INSERT INTO dbo.Equipment(descript, equipment_name) VALUES(@description, @equip_name)
			select @code = SCOPE_IDENTITY()
			PRINT @code
		END
	ELSE
		RAISERROR('Unknown EquipmentType, please provide a valid one!',15,1)
END
COMMIT
RETURN
GO