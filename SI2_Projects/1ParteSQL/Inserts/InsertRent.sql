USE [Aenima]
GO

IF OBJECT_ID ('dbo.InsertRent') IS NOT NULL
	DROP PROCEDURE dbo.InsertRent
GO

CREATE PROCEDURE dbo.InsertRent 
@rent_time int,
@price int,
@EquipCode int,
@EmployNmb int,
@clientCode int,
@serial_nr int output
AS
SET xact_abort ON 
BEGIN TRANSACTION
	IF(@clientCode IS NULL OR @rent_time IS NULL OR @price IS NULL OR @EquipCode IS NULL OR @EmployNmb IS NULL)
	BEGIN 
		RAISERROR('Any of the parameters cant be null.',15,1)
	END
	ELSE IF EXISTS(SELECT code FROM dbo.Client WHERE code = @clientCode AND active = 'T') 
		AND EXISTS(SELECT code FROM dbo.Equipment WHERE code = @EquipCode AND active = 'T')
		AND EXISTS(SELECT number FROM dbo.Employer WHERE number = @EmployNmb)
	BEGIN 
		INSERT INTO dbo.Rent(end_date, rent_time, price, equipment_code, employer_number, client_code) 
		VALUES(dbo.CalculateEndTime(GETDATE(),@rent_time), @rent_time, @price, @EquipCode, @EmployNmb, @clientCode)
		SELECT @serial_nr = SCOPE_IDENTITY()
		print @serial_nr
	END
	ELSE
	BEGIN
		RAISERROR('Invalid parameters. Please insert a valid client, equipementCode and employer number.',15,1)
	END
COMMIT
RETURN
GO