use[Aenima]
GO

IF OBJECT_ID ('dbo.RemoveEquipment') IS NOT NULL
	DROP PROCEDURE dbo.RemoveEquipment
GO

CREATE PROCEDURE dbo.RemoveEquipment @code int = NULL
AS
SET xact_abort ON 
BEGIN TRANSACTION
IF EXISTS(SELECT Equipment.code FROM DBO.Equipment WHERE Equipment.code = @code)
BEGIN
	IF dbo.hasRent(@code) = 'T'
		UPDATE dbo.Equipment SET active = 'F' WHERE Equipment.code = @code
	ELSE
	BEGIN
		ALTER TABLE dbo.Equipment DISABLE TRIGGER trg_DeleteEquipment
		DELETE FROM dbo.Equipment where Equipment.code = @code
		ALTER TABLE dbo.Equipment ENABLE TRIGGER trg_DeleteEquipment
	END
END
ELSE
	RAISERROR('Please provide a valid code!!',15,1)
COMMIT
GO