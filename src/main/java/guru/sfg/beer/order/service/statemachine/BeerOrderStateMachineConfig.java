package guru.sfg.beer.order.service.statemachine;

import guru.sfg.beer.order.service.domain.BeerOrderEventsEnum;
import guru.sfg.beer.order.service.domain.BeerOrderStatusEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

import static guru.sfg.beer.order.service.domain.BeerOrderEventsEnum.VALIDATION_FAILED;
import static guru.sfg.beer.order.service.domain.BeerOrderEventsEnum.VALIDATION_PASSED;
import static guru.sfg.beer.order.service.domain.BeerOrderStatusEnum.NEW;
import static guru.sfg.beer.order.service.domain.BeerOrderStatusEnum.VALIDATED;
import static guru.sfg.beer.order.service.domain.BeerOrderStatusEnum.VALIDATED_EXCEPTION;

/**
 * Config for state machine
 *
 * @author Martin Wunderlich
 */
@RequiredArgsConstructor
@Configuration
@EnableStateMachineFactory
public class BeerOrderStateMachineConfig extends StateMachineConfigurerAdapter<BeerOrderStatusEnum, BeerOrderEventsEnum> {

    private final Action<BeerOrderStatusEnum, BeerOrderEventsEnum> validateOrderAction;

    @Override
    public void configure(StateMachineStateConfigurer<BeerOrderStatusEnum, BeerOrderEventsEnum> states) throws Exception {
        states.withStates()
                .initial(NEW)
                .states(EnumSet.allOf(BeerOrderStatusEnum.class))
                .end(BeerOrderStatusEnum.PICKED_UP)
                .end(BeerOrderStatusEnum.DELIVERED)
                .end(BeerOrderStatusEnum.DELIVERY_EXCEPTION)
                .end(BeerOrderStatusEnum.VALIDATED_EXCEPTION)
                .end(BeerOrderStatusEnum.ALLOCATION_EXCEPTION);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<BeerOrderStatusEnum, BeerOrderEventsEnum> transitions) throws Exception {
        transitions
                .withExternal()
                    .source(NEW).target(BeerOrderStatusEnum.VALIDATION_PENDING)
                    .event(BeerOrderEventsEnum.VALIDATE_ORDER)
                    .action(validateOrderAction)
                .and()
                .withExternal()
                    .source(NEW).target(VALIDATED)
                    .event(VALIDATION_PASSED)
                .and()
                .withExternal()
                    .source(NEW).target(VALIDATED_EXCEPTION)
                    .event(VALIDATION_FAILED);
    }
}
