use[Aenima]

--Returns rent initial_time with rent_time.
IF OBJECT_ID ('CalculateEndTime') IS NOT NULL  
    DROP FUNCTION dbo.CalculateEndTime
GO  

CREATE FUNCTION dbo.CalculateEndTime (@Start_Time DATETIME, @Duration INT)
RETURNS datetime 
AS
BEGIN
	RETURN DATEADD(minute,@Duration,@Start_Time)
END
GO