USE [Aenima]

IF OBJECT_ID('dbo.trg_DeleteClient') IS NOT NULL
	DROP TRIGGER dbo.trg_DeleteClient
GO

CREATE TRIGGER dbo.trg_DeleteClient
ON dbo.Client
INSTEAD OF DELETE AS
	DECLARE @id int = (SELECT code FROM deleted)
	IF @id IS NULL
		PRINT('Could not delete the client')
	ELSE
		EXEC dbo.DeleteClient @id
GO

-- DELETE FROM dbo.Client WHERE code=3