https://spring.io/guides/tutorials/react-and-spring-data-rest/

John Smith, 8, 26.3.2014, 13:15, 2:00

Monthly Wages 03/2014:
1, John Smith, $2534.00
2, Jane Smith, $763.25
3, James Smith, $8539.72

Every data row specifies one work shift and
split shifts are allowed (=more than one work shift per day for one person).

All timestamps are given in 15-minute increments.

Hourly wage for all employees is $3.75. For currency calculations round dollar amounts to the nearest cent.
For time to decimal conversions round to two decimal places.

Overtime compensation is paid when daily working hours exceeds 8 hours.

Note! When the working shift passes midnight, hours are still calculated into initial day’s total.

The overtime hours will be paid at the higher rate.
Other extra compensations are not included in hourly wage when calculating overtime compensations.

Overtime Compensation depends on the amount of daily overtime work hours:
Total Daily Pay = Regular Daily Wage + Evening Work Compensation + Overtime Compensations
Regular Daily Wage = Regular Working Hours * Hourly Wage
+$1.15/hour for hours between 18:00 - 06:00

Evening Work Compensation = Evening Hours * Evening Work Compensation

Overtime Compensations[] = Overtime Hours * Overtime Compensation Percent * Hourly Wage

First 2 Hours > 8 Hours = Hourly Wage + 25%
Next 2 Hours = Hourly Wage + 50%
After That = Hourly Wage + 100%

Test Cases
