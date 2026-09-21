package hd.webapp;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserFoodDTO {
    private Long id;
    private Long userId;
    private String name;
    private int energy;
    private double protein;
    private double fiber;
    private double calcium;
    private String date;
    private int unit;
    private int volume;
}