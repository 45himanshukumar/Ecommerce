package com.ecommerce.sb_ecom.Repositry;

import com.ecommerce.sb_ecom.Model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment,Long> {

}
