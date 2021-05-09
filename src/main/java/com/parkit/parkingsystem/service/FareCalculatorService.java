package com.parkit.parkingsystem.service;

import org.joda.time.DateTime;
import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

  public void calculateFare(Ticket ticket) {
    if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
      throw new IllegalArgumentException(
          "Out time provided is incorrect:" + ticket.getOutTime().toString());
    }

    DateTime dateTimeIn = new DateTime(ticket.getInTime());
    DateTime dateTimeOut = new DateTime(ticket.getOutTime());
    double diffInMinutes = dateTimeOut.getMinuteOfDay() - dateTimeIn.getMinuteOfDay();
    double diffInHours = (dateTimeOut.getDayOfYear() - dateTimeIn.getDayOfYear()) * 24;
    double duration = diffInHours + (diffInMinutes / 60);



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
