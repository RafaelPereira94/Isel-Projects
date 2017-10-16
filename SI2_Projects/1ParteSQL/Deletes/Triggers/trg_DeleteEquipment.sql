use[Aenima]

IF OBJECT_ID ('dbo.trg_DeleteEquipment') IS NOT NULL
    DROP TRIGGER dbo.trg_DeleteEquipment
GO

CREATE TRIGGER dbo.trg_DeleteEquipment
ON Equipment
INSTEAD OF DELETE
AS
	DECLARE @code INT = (SELECT code FROM deleted)
	EXEC dbo.RemoveEquipment @code
GO