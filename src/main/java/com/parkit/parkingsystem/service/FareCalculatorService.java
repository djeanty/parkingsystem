package com.parkit.parkingsystem.service;

import org.joda.time.DateTime;
import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

  /**
   * The fare is calculated with the duration of the parking
   * 
   * If the parking time is less that 30 minutes, the fare is free. Otherwise, the price will depend
   * on the parking type (Car or Bike)
   * 
   * @param ticket The ticket's properties are read so that its price property is modified
   */
  public void calculateFare(Ticket ticket) {
    if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
      throw new IllegalArgumentException(
          "Out time provided is incorrect:" + ticket.getOutTime().toString());
    }

    /*
     * The logic to calculate the difference between two dates has been modified and this part of
     * the code now uses new methods.
     * 
     * DateTime is used instead of integer variables to calculate the difference between the IN and
     * OUT time. The difference is precisely calculated in minutes instead of hours. It then
     * converts the difference in hours to calculate the Fare. DateTime is not deprecated while the
     * methods used before were.
     */
    DateTime dateTimeIn = new DateTime(ticket.getInTime());
    DateTime dateTimeOut = new DateTime(ticket.getOutTime());
    double diffInMinutes = dateTimeOut.getMinuteOfDay() - dateTimeIn.getMinuteOfDay();
    double diffInHours = (dateTimeOut.getDayOfYear() - dateTimeIn.getDayOfYear()) * 24;
    double duration = diffInHours + (diffInMinutes / 60);

    // Less that 30' of parking is free, hurray!
    if (duration < 0.5) {
      ticket.setPrice(0.0);
    } else {

      switch (ticket.getParkingSpot().getParkingType()) {
        case CAR: {
          ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
          break;
        }
        case BIKE: {
          ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
          break;
        }
        default:
          throw new IllegalArgumentException("Unkown Parking Type");
      }
    }
  }

  public void applyDiscount(Ticket ticket) {
    double priceBeforeDiscount = ticket.getPrice();
    double priceAfterDiscount = priceBeforeDiscount - (0.05 * priceBeforeDiscount);
    ticket.setPrice(priceAfterDiscount);
  }
}
