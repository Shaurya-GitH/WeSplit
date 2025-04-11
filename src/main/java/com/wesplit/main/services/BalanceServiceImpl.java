package com.wesplit.main.services;

import com.wesplit.main.entities.Balance;
import com.wesplit.main.entities.User;
import com.wesplit.main.exceptions.TransactionFailedException;
import com.wesplit.main.repositories.BalanceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class BalanceServiceImpl implements BalanceService {
    private BalanceRepository balanceRepository;
    BalanceServiceImpl(BalanceRepository balanceRepository){
        this.balanceRepository=balanceRepository;
    }

    @Transactional
    @Override
    public void addNewBalance(User user1, User user2) {
        boolean exists;
        //checking if the balance is already created
       if(balanceRepository.findByUser1AndUser2(user1,user2).isEmpty()){
           if(balanceRepository.findByUser1AndUser2(user2,user1).isEmpty())exists=false;
           else{
               exists=true;
           }
       }
       else exists=true;
       //creating a new balance if not already created
       if(!exists){
           Balance newBalance= Balance.builder()
                           .oneOweTwo(BigDecimal.ZERO)
                           .twoOweOne(BigDecimal.ZERO)
                           .user1(user1)
                           .user2(user2)
                           .build();
           try{
               balanceRepository.save(newBalance);
           } catch (Exception e) {
               throw new TransactionFailedException("failed to create a balance record");
           }
       }
    }

    @Transactional
    @Override
    public void updateBalance(User user1, User user2, BigDecimal owed) {
        //retrieve the balance record between two users
        Balance balance= balanceRepository.findByUser1AndUser2(user1, user2).orElseGet(()->balanceRepository.findByUser1AndUser2(user2, user1).get());
        //syncing users with the record
          if(balance.getUser1().getUserId().equals(user1.getUserId())){
              //logic to see which user owes which who
              if(owed.compareTo(BigDecimal.ZERO)>0){
                  balance.setOneOweTwo(balance.getOneOweTwo().add(owed));
              }
              else{
                  balance.setTwoOweOne(balance.getTwoOweOne().add(owed.negate()));
              }
          }
          else{
              if(owed.compareTo(BigDecimal.ZERO)>0){
                  balance.setTwoOweOne(balance.getTwoOweOne().add(owed));
              }
              else{
                  balance.setOneOweTwo(balance.getOneOweTwo().add(owed.negate()));
              }
          }
          try{
              balanceRepository.save(balance);
          } catch (Exception e) {
              throw new TransactionFailedException("Failed to update balance record");
          }
    }

}
