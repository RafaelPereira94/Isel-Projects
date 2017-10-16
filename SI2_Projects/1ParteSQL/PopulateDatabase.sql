USE [Aenima]
GO

BEGIN TRANSACTION
	PRINT('Populating tables...')

	INSERT INTO dbo.EquipmentType(name, descript)
	VALUES
		('Canoa', ''),
		('Cisne', ''),
		('Caiaque', ''),
		('Espreguicadeira', '')

	INSERT INTO dbo.Equipment(descript, equipment_name)
	VALUES
		('','Canoa'),
		('','Canoa'),
		('','Canoa'),
		('','Cisne'),
		('','Caiaque'),
		('','Caiaque'),
		('','Espreguicadeira'),
		('','Espreguicadeira')

	INSERT INTO dbo.RentPrice(validity, price, _time, name_type)
	VALUES
		('2017-10-10', 15, 60, 'Canoa'),
		('2016-12-24', 10, 30, 'Espreguicadeira'),
		('2016-12-27', 10, 30, 'Espreguicadeira')

	INSERT INTO dbo.Employer(name)
	VALUES
		('José'),
		('Miguel'),
		('Mariana')

	INSERT INTO dbo.Client(name, nif, _address)
	VALUES
		('Ines', 254869217, 'Rua do Sol'),
		('Rafael', 845621477, 'Avenida de Berna'),
		('Renato', 963210478, 'Quinta das Conchas')

	INSERT INTO dbo.Rent(date_time, end_date, rent_time, price, equipment_code, employer_number, client_code)
	VALUES
		('02-01-2015', '02-01-2016', 60, 30, 2, 3, 2),
		(GETDATE(),'02-01-2017', 30, 12, 5, 1, 3)

	INSERT INTO dbo.Promotion(begin_date, end_date, descript)
	VALUES
		('2016-11-21 10:34:09','2016-11-27 10:34:09','Black Week'),
		('2017-03-10 10:34:09','2017-03-15 10:34:09','Feira de Marco')

	INSERT INTO dbo.ExtraTime(id, extra_time)
	VALUES
		(2, 30)

	INSERT INTO dbo.Discount(id, percentage)
	VALUES
		(1, 15)

	INSERT INTO dbo.RentPromotion(rent_serial_nr, promotion_id)
	VALUES
		(1, 1)

	PRINT('Tables populated.')
COMMIT
GO