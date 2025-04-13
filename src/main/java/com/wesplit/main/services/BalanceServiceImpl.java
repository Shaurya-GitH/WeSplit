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
    public void updateExpenseBalance(User user1, User user2, BigDecimal owed) {
        //retrieve the balance record between two users
        Balance balance= balanceRepository.findByUser1AndUser2(user1, user2).orElseGet(()->balanceRepository.findByUser1AndUser2(user2, user1).get());
        //syncing users with the record
          if(balance.getUser1().getUserId().equals(user1.getUserId())){
              //logic to see which user owes whom
              if(owed.compareTo(BigDecimal.ZERO)>0){
                  BigDecimal sum= balance.getOneOweTwo().add(owed);
                  //logic to ensure one attribute is zero
                  BigDecimal temp=sum.subtract(balance.getTwoOweOne());
                  if(temp.compareTo(BigDecimal.ZERO)>0){
                      balance.setTwoOweOne(BigDecimal.ZERO);
                      balance.setOneOweTwo(temp);
                  }
                  else{
                      balance.setTwoOweOne(temp.negate());
                      balance.setOneOweTwo(BigDecimal.ZERO);
                  }

              }
              else{
                  BigDecimal sum= balance.getTwoOweOne().add(owed.negate());
                  //logic to ensure one attribute is zero
                  BigDecimal temp= balance.getOneOweTwo().subtract(sum);
                  if(temp.compareTo(BigDecimal.ZERO)>0){
                      balance.setTwoOweOne(BigDecimal.ZERO);
                      balance.setOneOweTwo(temp);
                  }
                  else{
                      balance.setTwoOweOne(temp.negate());
                      balance.setOneOweTwo(BigDecimal.ZERO);
                  }
              }
          }
          else{
              if(owed.compareTo(BigDecimal.ZERO)>0){
                  BigDecimal sum= balance.getTwoOweOne().add(owed);
                  //logic to ensure one attribute is zero
                  BigDecimal temp= balance.getOneOweTwo().subtract(sum);
                  if(temp.compareTo(BigDecimal.ZERO)>0){
                      balance.setTwoOweOne(BigDecimal.ZERO);
                      balance.setOneOweTwo(temp);
                  }
                  else{
                      balance.setTwoOweOne(temp.negate());
                      balance.setOneOweTwo(BigDecimal.ZERO);
                  }

              }
              else{
                  BigDecimal sum= balance.getOneOweTwo().add(owed.negate());
                  //logic to ensure one attribute is zero
                  BigDecimal temp= sum.subtract(balance.getTwoOweOne());
                  if(temp.compareTo(BigDecimal.ZERO)>0){
                      balance.setTwoOweOne(BigDecimal.ZERO);
                      balance.setOneOweTwo(temp);
                  }
                  else{
                      balance.setTwoOweOne(temp.negate());
                      balance.setOneOweTwo(BigDecimal.ZERO);
                  }
              }
          }
          try{
              balanceRepository.save(balance);
          } catch (Exception e) {
              throw new TransactionFailedException("Failed to update balance record");
          }
    }

    @Transactional
    @Override
    public Boolean updatePaymentBalance(User user1, User user2, BigDecimal paid) {
        Boolean settled=false;
        //retrieve the balance record between two users
        Balance balance= balanceRepository.findByUser1AndUser2(user1, user2).orElseGet(()->balanceRepository.findByUser1AndUser2(user2, user1).get());
        //syncing users with the record
        if(balance.getUser1().getUserId().equals(user1.getUserId())) {
            //logic to update balance
            BigDecimal oneOweTwo= balance.getOneOweTwo();
            if(oneOweTwo.equals(BigDecimal.ZERO)){
                balance.setTwoOweOne(balance.getTwoOweOne().add(paid));
            }
            else{
                BigDecimal difference=oneOweTwo.subtract(paid);
                if(difference.compareTo(BigDecimal.ZERO)<=0){
                    balance.setOneOweTwo(BigDecimal.ZERO);
                    balance.setTwoOweOne(balance.getTwoOweOne().add(difference.negate()));
                    settled=true;
                }
                else if(difference.compareTo(BigDecimal.ZERO)>0){
                    balance.setOneOweTwo(balance.getOneOweTwo().subtract(paid));
                }
            }
        }
        else{
            //logic to update balance
            BigDecimal twoOweOne= balance.getTwoOweOne();
            if(twoOweOne.equals(BigDecimal.ZERO)){
                balance.setOneOweTwo(balance.getOneOweTwo().add(paid));
            }
            else{
                BigDecimal difference= twoOweOne.subtract(paid);
                if(difference.compareTo(BigDecimal.ZERO)<=0){
                    balance.setTwoOweOne(BigDecimal.ZERO);
                    balance.setOneOweTwo(balance.getOneOweTwo().add(difference.negate()));
                    settled=true;
                }
                else{
                    balance.setTwoOweOne(balance.getTwoOweOne().subtract(paid));
                }
            }
        }
        try{
            balanceRepository.save(balance);
            return settled;
        } catch (Exception e) {
            throw new TransactionFailedException("failed to update balance");
        }
    }
}
