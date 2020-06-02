package guru.sfg.beer.order.service.services;

import guru.sfg.beer.order.service.domain.BeerOrder;
import guru.sfg.beer.order.service.domain.BeerOrderEventsEnum;
import guru.sfg.beer.order.service.domain.BeerOrderStatusEnum;
import guru.sfg.beer.order.service.repositories.BeerOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Martin Wunderlich
 */
@Service
@RequiredArgsConstructor
public class BeerOrderManagerImpl implements BeerOrderManager {

    private final StateMachineFactory<BeerOrderStatusEnum, BeerOrderEventsEnum> stateMachineFactory;
    private final BeerOrderRepository beerOrderRepository;

    @Transactional
    @Override
    public BeerOrder newBeerOrder(BeerOrder beerOrder) {
        beerOrder.setId(null);
        beerOrder.setOrderStatus(BeerOrderStatusEnum.NEW);

        BeerOrder saveBeerOrder = beerOrderRepository.save(beerOrder);
        sendBeerOrderEvent(saveBeerOrder, BeerOrderEventsEnum.VALIDATE_ORDER);

        return saveBeerOrder;
    }

    private void sendBeerOrderEvent(BeerOrder beerOrder, BeerOrderEventsEnum eventsEnum) {
        StateMachine<BeerOrderStatusEnum, BeerOrderEventsEnum> sm = build(beerOrder);

        Message msg = MessageBuilder.withPayload(eventsEnum)
                .build();

        sm.sendEvent(msg);
    }

    private StateMachine<BeerOrderStatusEnum, BeerOrderEventsEnum> build(BeerOrder beerOrder) {
        StateMachine<BeerOrderStatusEnum, BeerOrderEventsEnum> sm = stateMachineFactory.getStateMachine(beerOrder.getId());

        sm.stop();

        sm.getStateMachineAccessor()
                .doWithAllRegions(sma -> {
                    sma.resetStateMachine(new DefaultStateMachineContext<>(beerOrder.getOrderStatus(), null, null, null));
                });

        sm.start();

        return sm;
    }
}
