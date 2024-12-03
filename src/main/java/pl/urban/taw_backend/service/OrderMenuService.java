package pl.urban.taw_backend.service;

import org.springframework.stereotype.Service;
import pl.urban.taw_backend.repository.OrderMenuRepository;

@Service
public class OrderMenuService {

    private final OrderMenuRepository orderMenuRepository;

    public OrderMenuService(OrderMenuRepository orderMenuRepository) {
        this.orderMenuRepository = orderMenuRepository;
    }

}
