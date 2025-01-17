package webhook.processor.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


public abstract class PositionState {
    protected String code;
    protected Double quantity = 0.0;
    protected Double price = 0.0;

    public PositionState() {
    }

    public PositionState(String code, Double quantity, Double price) {
        this.code = code;
        this.quantity = quantity;
        this.price = price;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
