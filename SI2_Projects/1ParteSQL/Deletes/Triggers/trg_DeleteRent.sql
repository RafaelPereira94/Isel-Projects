use[Aenima]
GO

IF OBJECT_ID ('dbo.trg_DeleteRent') IS NOT NULL
    DROP TRIGGER dbo.trg_DeleteRent
GO

CREATE TRIGGER dbo.trg_DeleteRent
ON Rent
INSTEAD OF DELETE
AS
	DECLARE @serial_nr INT = (SELECT serial_nr FROM deleted)
	EXEC dbo.DeleteRent @serial_nr
GO