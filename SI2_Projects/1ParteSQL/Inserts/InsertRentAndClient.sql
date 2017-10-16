USE [Aenima]
GO

IF OBJECT_ID ('dbo.InsertRentAndClient') IS NOT NULL
	DROP PROCEDURE dbo.InsertRentAndClient
GO

CREATE PROCEDURE dbo.InsertRentAndClient 
@rent_time int,
@price int,
@equipCode int,
@employNr int,
@clientName varchar(25) = NULL,
@clientNif int = NULL,
@clientAddress varchar(50) = NULL,
@serial_nr int output,
@clientCode int output
AS
SET xact_abort ON 
SET TRANSACTION ISOLATION LEVEL  REPEATABLE READ --porque faz duas leituras
BEGIN TRANSACTION
	IF(@rent_time IS NULL OR @price IS NULL OR @equipCode IS NULL OR @employNr IS NULL)
	BEGIN 
		RAISERROR('Any of the parameters cant be null.',15,1)
	END
	ELSE IF EXISTS(SELECT code FROM dbo.Equipment WHERE code = @equipCode AND active = 'T')
		AND EXISTS(SELECT number FROM dbo.Employer WHERE number = @employNr)
	BEGIN
		EXEC dbo.InsertClient @clientName, @clientNif, @clientAddress,@clientCode output
		EXEC dbo.InsertRent @rent_time, @price, @EquipCode, @employNr,@clientCode, @serial_nr output
		PRINT @clientCode
		PRINT @serial_nr
	END
	ELSE
	BEGIN
		RAISERROR('Invalid parameters. Please insert a valid client, equipementCode and employer number.',15,1)
	END
COMMIT
RETURN
GO
