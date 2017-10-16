USE [Aenima]

BEGIN TRANSACTION
	PRINT('Droping tables...')

	IF OBJECT_ID('dbo.RentPromotion') IS NOT NULL
		DROP TABLE dbo.RentPromotion

	IF OBJECT_ID('dbo.Discount') IS NOT NULL
		DROP TABLE dbo.Discount

	IF OBJECT_ID('dbo.ExtraTime') IS NOT NULL
		DROP TABLE dbo.ExtraTime

	IF OBJECT_ID('dbo.Promotion') IS NOT NULL
		DROP TABLE dbo.Promotion

	IF OBJECT_ID('dbo.Rent') IS NOT NULL
		DROP TABLE dbo.Rent

	IF OBJECT_ID('dbo.Client') IS NOT NULL
		DROP TABLE dbo.Client

	IF OBJECT_ID('dbo.Employer') IS NOT NULL
		DROP TABLE dbo.Employer

	IF OBJECT_ID('dbo.RentPrice') IS NOT NULL
		DROP TABLE dbo.RentPrice

	IF OBJECT_ID('dbo.Equipment') IS NOT NULL
		DROP TABLE dbo.Equipment

	IF OBJECT_ID('dbo.EquipmentType') IS NOT NULL
		DROP TABLE dbo.EquipmentType

	PRINT('Tables droped.')
COMMIT
GO