USE [Aenima]
GO

IF OBJECT_ID('dbo.UpdatePromotion') IS NOT NULL
	DROP PROCEDURE dbo.UpdatePromotion
GO

CREATE PROCEDURE dbo.UpdatePromotion
	@id int,
	@begin_date date,
	@end_date date,
	@descript varchar(50)
AS
SET xact_abort ON 
BEGIN TRANSACTION
	IF @begin_date IS NULL OR @end_date IS NULL
		RAISERROR('Begin and End date cant be null',15,1)	
	ELSE IF @id IS NOT NULL
	BEGIN
		IF @begin_date < GETDATE() OR (SELECT begin_date FROM dbo.Promotion WHERE id = @id)<GETDATE() /*promotion already begins*/
			RAISERROR('Cannot update Promotion that already started or ended, only the upcoming ones!',15,1)
		ELSE
		BEGIN
			IF @descript IS NULL
				SET @descript = (SELECT descript FROM dbo.Promotion WHERE id = @id)

			UPDATE dbo.Promotion SET begin_date=@begin_date, end_date=@end_date, descript=@descript WHERE id=@id
		END
	END
	ELSE
		RAISERROR('Please provide an id to identify the promotion',15,1)
COMMIT
GO

--EXEC dbo.UpdatePromotion NULL,NULL,'2016-12-27',NULL				id=NULL
--EXEC dbo.UpdatePromotion 1,NULL,'2016-12-27',NULL					begin_date=NULL
--EXEC dbo.UpdatePromotion 1,'2016-11-22','2016-12-27',NULL			promotion already begins
--EXEC dbo.UpdatePromotion 2,'2017-03-20','2017-03-25',NULL			update without description
--EXEC dbo.UpdatePromotion 2,'2016-12-20','2016-12-25',NULL			I cannot create a promotion that started yesterday
--EXEC dbo.UpdatePromotion 2,'2017-04-01','2017-04-30','Feira de Abril'		funciona normalmente c/desc
--SELECT * FROM dbo.Promotion

