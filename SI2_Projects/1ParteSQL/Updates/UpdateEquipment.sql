use [Aenima]
GO

IF OBJECT_ID ('dbo.UpdateEquipment') IS NOT NULL
	DROP PROCEDURE dbo.UpdateEquipment
GO

CREATE PROCEDURE dbo.UpdateEquipment 
@description VARCHAR(50),
@code INT
AS
SET XACT_ABORT ON
BEGIN TRANSACTION 
	IF NOT EXISTS (SELECT code FROM dbo.Equipment WHERE code = @code)  
		RAISERROR('Invalid code!!',15,1)
	ELSE
		UPDATE dbo.Equipment SET descript = @description WHERE code = @code
COMMIT 
GO
