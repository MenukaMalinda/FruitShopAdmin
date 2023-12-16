package com.ciberprotech.finalprojectadmin.listner;

import com.ciberprotech.finalprojectadmin.model.Order;

public interface OrderSelectListner {
    void selectOrder(Order order);
    void setDelivered(Order order);
}
