package webhook.processor.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class PositionState implements Serializable {
    @Serial
    private static final long serialVersionUID = 42L;
    protected String code;
    protected Long quantity;
    protected Double price;
}
