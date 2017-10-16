USE [Aenima]
GO

IF OBJECT_ID('dbo.InsertPromotion') IS NOT NULL
	DROP PROCEDURE dbo.InsertPromotion
GO

CREATE PROCEDURE dbo.InsertPromotion
	@begin_date date,
	@end_date date,
	@descript varchar(50),
	@id int output
AS
SET xact_abort ON 
BEGIN TRANSACTION
	IF @end_date IS NULL
		RAISERROR('end_date cant be null.',15,1)	
	ELSE IF @begin_date IS NULL
		INSERT INTO dbo.Promotion(end_date, descript) VALUES (@end_date, @descript)
	ELSE
		INSERT INTO dbo.Promotion(begin_date, end_date, descript) VALUES (@begin_date, @end_date, @descript) 
	
	select @id = SCOPE_IDENTITY()
	print @id

COMMIT
return
GO