import java.util.Date;

public class CouponModel {
        private String description;
        private Number discountPercentage;
        private Date validFrom;
        private Date validTo;

        // Getters and setters
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public Number getDiscountPercentage() { return discountPercentage; }
        public void setDiscountPercentage(Number discountPercentage) { this.discountPercentage = discountPercentage; }

        public Date getValidFrom() { return validFrom; }
        public void setValidFrom(Date validFrom) { this.validFrom = validFrom; }

        public Date getValidTo() { return validTo; }
        public void setValidTo(Date validTo) { this.validTo = validTo; }
    }