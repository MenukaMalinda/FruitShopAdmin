package com.ciberprotech.finalprojectadmin.listner;

import com.ciberprotech.finalprojectadmin.model.Product;

public interface ProductSelectListener {
    void changeStatus(Product product);
    void editProduct(Product product);
}
