package tacos.data;

import tacos.model.TacoOrder;

public interface OrderRepository { // extends CrudRepository<TacoOrder, Long>

    TacoOrder save(TacoOrder order);

//    List<TacoOrder> findByDeliveryZip(String deliveryZip);

}
