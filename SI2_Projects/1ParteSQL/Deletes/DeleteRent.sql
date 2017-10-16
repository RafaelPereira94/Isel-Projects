use[Aenima]
GO

IF OBJECT_ID ('dbo.DeleteRent') IS NOT NULL
	DROP PROCEDURE dbo.DeleteRent
GO

CREATE PROCEDURE dbo.DeleteRent @serial_nr int  --repeatable read!
AS
SET TRANSACTION ISOLATION LEVEL REPEATABLE READ
SET xact_abort ON 
BEGIN TRANSACTION
		IF NOT EXISTS(SELECT serial_nr from Rent where Rent.serial_nr = @serial_nr)
			BEGIN
				RAISERROR('No rent with the specific id!!',15,1)
			END
		IF (SELECT end_date FROM dbo.Rent WHERE serial_nr=@serial_nr) < GETDATE()
			UPDATE dbo.Rent SET active = 'F' WHERE serial_nr = @serial_nr
		ELSE
		BEGIN
			ALTER TABLE dbo.Rent DISABLE TRIGGER trg_DeleteRent
			DELETE FROM dbo.Rent WHERE serial_nr = @serial_nr
			ALTER TABLE dbo.Rent ENABLE TRIGGER trg_DeleteRent
		END
		COMMIT
GO