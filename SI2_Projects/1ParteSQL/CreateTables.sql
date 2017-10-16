USE [master]
IF NOT EXISTS (SELECT name FROM master.dbo.sysdatabases WHERE name = N'Aenima')
	BEGIN
		PRINT('Creating database named Aenima...')
		CREATE DATABASE [Aenima]
		PRINT('Database created.')
	END
ELSE
	PRINT('Database already created.')

GO 
USE [Aenima]

BEGIN TRANSACTION
	IF OBJECT_ID('dbo.EquipmentType') is null
		BEGIN
			PRINT('Creating tables...')
			CREATE TABLE dbo.EquipmentType(
				name varchar(25) primary key,
				descript varchar(50)
			)
		END
	ELSE
		PRINT('Tables already created.')

	IF OBJECT_ID('dbo.Equipment') is null
		CREATE TABLE dbo.Equipment(
			code int identity(1,1) primary key,
			descript varchar(50),
			equipment_name varchar(25) NOT NULL foreign key(equipment_name) references dbo.EquipmentType(name),
			active varchar(1) default 'T' CHECK(active = 'T' OR active = 'F')
		)

	IF OBJECT_ID('dbo.RentPrice') is null
		CREATE TABLE dbo.RentPrice(
			fare_id int identity (1,1),
			validity date NOT NULL,
			price int NOT NULL CHECK(price > 0), --price comes in €
			_time int NOT NULL CHECK(_time > 0), --time comes in minutes
			name_type varchar(25) NOT NULL foreign key(name_type) references dbo.EquipmentType(name),
			primary key(name_type, fare_id)
		)

	IF OBJECT_ID('dbo.Employer') is null
		CREATE TABLE dbo.Employer(
			number int identity(1,1) primary key,
			name varchar(25) NOT NULL
		) 

	IF OBJECT_ID('dbo.Client') is null
		CREATE TABLE dbo.Client(
			code int identity(1,1) primary key,
			name varchar(25) default NULL,
			nif int default NULL,
			_address varchar(50) default NULL,
			active varchar(1) default 'T' CHECK(active = 'T' OR active = 'F')
		)

	IF OBJECT_ID('dbo.Rent') is null
		CREATE TABLE dbo.Rent(
			serial_nr int identity(1,1) primary key,
			date_time datetime default GETDATE(),
			end_date datetime NOT NULL,
			rent_time int NOT NULL CHECK(rent_time > 0), --time comes in minutes
			price int NOT NULL,
			equipment_code int NOT NULL foreign key(equipment_code) references dbo.Equipment(code),
			employer_number int NOT NULL foreign key (employer_number) references dbo.Employer(number),
			client_code int NOT NULL foreign key (client_code) references dbo.Client(code),
			active varchar(1) default 'T' CHECK(active = 'T' OR active = 'F'),
			CHECK( date_time < end_date)
		) 

	IF OBJECT_ID('dbo.Promotion') is null
		CREATE TABLE dbo.Promotion(
			id int identity(1,1) primary key,
			begin_date date default GETDATE(),
			end_date date NOT NULL,
			descript varchar(50),
			CHECK( begin_date < end_date)
		)

	IF OBJECT_ID('dbo.ExtraTime') is null
		CREATE TABLE dbo.ExtraTime(
			id int foreign key(id) references dbo.Promotion(id) primary key,
			extra_time int NOT NULL CHECK(extra_time > 0)
		)

	IF OBJECT_ID('dbo.Discount') is null
		CREATE TABLE dbo.Discount(
			id int foreign key(id) references dbo.Promotion(id) primary key,
			percentage int NOT NULL CHECK(percentage > 0 and percentage <= 100)
		)

	IF OBJECT_ID('dbo.RentPromotion') is null
		BEGIN
			CREATE TABLE dbo.RentPromotion(
				rent_serial_nr int foreign key (rent_serial_nr) references dbo.Rent(serial_nr),
				promotion_id int foreign key (promotion_id) references dbo.Promotion(id),
				primary key(rent_serial_nr, promotion_id)
			)
			PRINT('Tables created.')
		END

COMMIT