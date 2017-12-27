# Salary Timesheet

This program is intended for show and tell. The main purpose is to calculate monthly payments out of a given CSV timesheet following given calculation rules.

To make the assignment interesting, it's written in Spring and especially JPA Hibernate to build up experience. Although they are overkill for time calculations, they are useful for building up experience. Not required by the assignment but for education is that the data can be exposed as REST API through a single annotation


[Spring Data Rest](https://spring.io/guides/tutorials/react-and-spring-data-rest/)

## Compile
```sh
mvn install
mvn test
mvn package
```

```sh
java -jar target/salary-0.0.1-SNAPSHOT.jar &
```

John Smith, 8, 26.3.2014, 13:15, 2:00

Monthly Wages 03/2014:
1, John Smith, $2534.00
2, Jane Smith, $763.25
3, James Smith, $8539.72

## Rules
 - Every data row specifies one work shift and split shifts are allowed (=more than one work shift per day for one person).

 - All timestamps are given in 15-minute increments.

 - Hourly wage for all employees is $3.75. For currency calculations round dollar amounts to the nearest cent.
For time to decimal conversions round to two decimal places.

 - Overtime compensation is paid when daily working hours exceeds 8 hours.

 - When the working shift passes midnight, hours are still calculated into initial day’s total.

 - The overtime hours will be paid at the higher rate.
Other extra compensations are not included in hourly wage when calculating overtime compensations.

 - Overtime Compensation depends on the amount of daily overtime work hours:
 - Total Daily Pay = Regular Daily Wage + Evening Work Compensation + Overtime Compensations
 - Regular Daily Wage = Regular Working Hours * Hourly Wage

 - +$1.15/hour for hours between 18:00 - 06:00
 - Evening Work Compensation = Evening Hours * Evening Work Compensation

 - Overtime Compensations[] = Overtime Hours * Overtime Compensation Percent * Hourly Wage

 - < 8h = 8h
 - <10h = 8h + rest 25%
 - <12h = 8h + 2h 25% + rest 50%
 - \>12h = 8h + 2h 25% + 2h 50% + rest 100%

## Test Cases

Testcases cover the time related classes because they contain the most logic, which has a potential to break.
 - The testcases cover a range check for 24 hours to verify the regular and overtime minutes are increasing linearly for every minute.
 - Spotchecks are done on the boundries to check if the actual values match.
 - The daylightsaving check is done to see that working nighttime in march is rewarded for a non-existing hour
