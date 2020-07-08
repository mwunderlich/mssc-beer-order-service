package guru.sfg.beer.order.service.domain;

/**
 * Order events
 *
 * @author Martin Wunderlich
 */
public enum BeerOrderEventsEnum {
    VALIDATE_ORDER, VALIDATION_PASSED, VALIDATION_FAILED,
    ALLOCATE_ORDER, ALLOCATION_SUCCESS, ALLOCATION_NO_INVENTORY, ALLOCATION_FAILED,
    BEERORDER_PICKED_UP
}
