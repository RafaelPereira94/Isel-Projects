USE [Aenima]
GO

IF OBJECT_ID('dbo.UpdateRentPrice') IS NOT NULL
	DROP PROCEDURE dbo.UpdateRentPrice
GO

CREATE PROCEDURE dbo.UpdateRentPrice
	@name_type varchar(25),
	@id int,
	@validity date,
	@price int,
	@_time int
AS
SET TRANSACTION ISOLATION LEVEL  REPEATABLE READ --porque faz varias leituras
SET xact_abort ON 
BEGIN TRANSACTION
	IF @validity IS NULL
		SET @validity = (SELECT validity FROM dbo.RentPrice WHERE fare_id = @id and name_type=@name_type)
	IF @price IS NULL
		SET @price = (SELECT price FROM dbo.RentPrice WHERE fare_id = @id and name_type=@name_type)
	IF @_time IS NULL
		SET @_time = (SELECT _time FROM dbo.RentPrice WHERE fare_id = @id and name_type=@name_type)

	UPDATE dbo.RentPrice SET validity=@validity, price=@price, _time=@_time WHERE fare_id=@id and name_type=@name_type
COMMIT
GO