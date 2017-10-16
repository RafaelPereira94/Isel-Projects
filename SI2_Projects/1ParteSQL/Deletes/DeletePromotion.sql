USE [Aenima]

IF OBJECT_ID('dbo.DeletePromotion') IS NOT NULL
	DROP PROCEDURE dbo.DeletePromotion
GO

CREATE PROCEDURE dbo.DeletePromotion @id int = null
AS
SET xact_abort ON 
BEGIN TRANSACTION
	IF NOT EXISTS(select id from Promotion where id = @id)
		RAISERROR('No Promotion found with that id!',15,1)
	ELSE
		DELETE FROM dbo.Promotion WHERE id=@id
COMMIT
GO

--EXEC dbo.DeletePromotion 2
--SELECT * FROM dbo.Promotion