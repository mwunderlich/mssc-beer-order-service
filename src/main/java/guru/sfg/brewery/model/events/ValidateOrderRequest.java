package guru.sfg.brewery.model.events;

import guru.sfg.brewery.model.BeerOrderDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Event for validate order
 *
 * @author Martin Wunderlich
 */
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class ValidateOrderRequest {
    private BeerOrderDto beerOrder;
}
