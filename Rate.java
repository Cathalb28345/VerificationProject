package verificationProject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Rate {
    private CarParkKind kind;
    private BigDecimal hourlyNormalRate;
    private BigDecimal hourlyreducedRate ;
    private ArrayList<Period> discount = new ArrayList<>();
    private ArrayList<Period> normal = new ArrayList<>();
    private final CalculateChargeStrategy visitorRate = new VisitorBehaviour();
    private final CalculateChargeStrategy studentRate = new StudentBehaviour();
    private final CalculateChargeStrategy staffRate = new StaffBehaviour();
    private final CalculateChargeStrategy managementRate = new ManagementBehaviour();

    
    public Rate(CarParkKind kind, BigDecimal normalRate, BigDecimal reducedRate, ArrayList<Period> reducedPeriods, ArrayList<Period> normalPeriods){
        if (reducedPeriods  == null || normalPeriods == null) {
            throw new IllegalArgumentException("time periods cannot be null enter a valid time");
        }
        // Task 3 - Check for arrayList of null
        if(reducedPeriods .contains(null) || normalPeriods.contains(null)) {
            throw new IllegalArgumentException("ArrayList cannot contain null");
        }
        if (normalRate == null || reducedRate  == null) {
            throw new IllegalArgumentException("The rates cannot be null must contain active value");
        }
        if (normalRate.compareTo(BigDecimal.ZERO) < 0 || reducedRate .compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("A rate cannot be be less than zero");
        }
        if (normalRate.compareTo(reducedRate ) < 0) {
            throw new IllegalArgumentException("The normal rate value cannot be less than reduced value rate");
        }
        if (!isValidPeriods(reducedPeriods ) || !isValidPeriods(normalPeriods)) {
            throw new IllegalArgumentException("The periods are not valid individually");
        }
        if (!isValidPeriods(reducedPeriods , normalPeriods)) {
            throw new IllegalArgumentException("The periods overlaps");
        }
     
        this.kind = kind;
        this.hourlyNormalRate = normalRate;
        this.hourlyreducedRate  = reducedRate ;
        this.discount = reducedPeriods ;
        this.normal = normalPeriods;
    }

   
    private boolean isValidPeriods(ArrayList<Period> periods1, ArrayList<Period> periods2) {
        Boolean isValid = true;
        int i = 0;
        while (i < periods1.size() && isValid) {
            isValid = isValidPeriod(periods1.get(i), periods2);
            i++;
        }
        return isValid;
    }

  
    private Boolean isValidPeriods(ArrayList<Period> list) {
        Boolean isValid = true;
        if (list.size() >= 2) {
            Period secondPeriod;
            int i = 0;
            int lastIndex = list.size()-1;
            while (i < lastIndex && isValid) {
                isValid = isValidPeriod(list.get(i), ((List<Period>)list).subList(i + 1, lastIndex+1));
                i++;
            }
        }
        return isValid;
    }

    private Boolean isValidPeriod(Period period, List<Period> list) {
        Boolean isValid = true;
        int i = 0;
        while (i < list.size() && isValid) {
            isValid = !period.overlaps(list.get(i));
            i++;
        }
        return isValid;
    }

   
    public BigDecimal calculate(Period periodStay) {
        int normalRateHours = periodStay.occurences(normal);
        int discountRateHours = periodStay.occurences(discount);
        BigDecimal rate = (this.hourlyNormalRate
                .multiply(BigDecimal.valueOf(normalRateHours)))
                .add(this.hourlyreducedRate 
                        .multiply(BigDecimal.valueOf(discountRateHours)));
        switch (kind) {
            case VISITOR:
                return visitorRate.calculate(rate);
            case STUDENT:
                return studentRate.calculate(rate);
            case STAFF:
                return staffRate.calculate(rate);
            default: //MANAGEMENT
                return managementRate.calculate(rate);
        }
    }
}