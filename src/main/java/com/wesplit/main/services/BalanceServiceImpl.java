package com.wesplit.main.services;

import com.wesplit.main.entities.Balance;
import com.wesplit.main.entities.User;
import com.wesplit.main.exceptions.TransactionFailedException;
import com.wesplit.main.payloads.BalanceDTO;
import com.wesplit.main.repositories.BalanceRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
public class BalanceServiceImpl implements BalanceService {
    private final ModelMapper modelMapper;
    private final BalanceRepository balanceRepository;
    BalanceServiceImpl(BalanceRepository balanceRepository, ModelMapper modelMapper){
        this.balanceRepository=balanceRepository;
        this.modelMapper = modelMapper;
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
               log.error(e.getMessage());
               throw new TransactionFailedException("failed to create a balance record");
           }
       }
    }

    @Transactional
    @Override
    //returns false if expenses are not settled
    //returns null if expenses change direction
    //return true if expenses are settled
    public Boolean updateExpenseBalance(User user1, User user2, BigDecimal owed) {
        Boolean settled=false;
        //retrieve the balance record between two users
        Balance balance= balanceRepository.findByUser1AndUser2(user1, user2).orElseGet(()->balanceRepository.findByUser1AndUser2(user2, user1).get());
        //syncing users with the record
          if(balance.getUser1().getUserId().equals(user1.getUserId())){
              //logic to see which user owes whom
              if(owed.compareTo(BigDecimal.ZERO)>0){
                 if(balance.getOneOweTwo().compareTo(BigDecimal.ZERO)>0){
                     balance.setOneOweTwo(balance.getOneOweTwo().add(owed));
                 }
                 else{
                     if(balance.getTwoOweOne().compareTo(BigDecimal.ZERO)==0){
                         balance.setOneOweTwo(owed);
                     }
                     else {
                         BigDecimal difference=balance.getTwoOweOne().subtract(owed);
                         int compare= difference.compareTo(BigDecimal.ZERO);
                         if(compare<=0){
                             balance.setTwoOweOne(BigDecimal.ZERO);
                             balance.setOneOweTwo(difference.negate());
                             if(compare==0){
                                 settled=true;
                             }
                             else{
                                 settled=null;
                             }
                         }
                         else if(compare>0){
                             balance.setTwoOweOne(balance.getTwoOweOne().subtract(owed));
                         }
                     }
                 }
              }
              else{
                  if(balance.getTwoOweOne().compareTo(BigDecimal.ZERO)>0){
                      balance.setTwoOweOne(balance.getTwoOweOne().add(owed.negate()));
                  }
                  else{
                      if(balance.getOneOweTwo().compareTo(BigDecimal.ZERO)==0){
                          balance.setTwoOweOne(owed.negate());
                      }
                      else {
                          BigDecimal difference=balance.getOneOweTwo().subtract(owed.negate());
                          int compare= difference.compareTo(BigDecimal.ZERO);
                          if(compare<=0){
                              balance.setOneOweTwo(BigDecimal.ZERO);
                              balance.setTwoOweOne(difference.negate());
                              if(compare==0){
                                  settled=true;
                              }
                              else{
                                  settled=null;
                              }
                          }
                          else if(compare>0){
                              balance.setOneOweTwo(balance.getOneOweTwo().subtract(owed.negate()));
                          }
                      }
                  }

              }
          }
          else{
              if(owed.compareTo(BigDecimal.ZERO)>0){
                  if(balance.getTwoOweOne().compareTo(BigDecimal.ZERO)>0){
                      balance.setTwoOweOne(balance.getTwoOweOne().add(owed));
                  }
                  else{
                      if(balance.getOneOweTwo().compareTo(BigDecimal.ZERO)==0){
                          balance.setTwoOweOne(owed);
                      }
                      else {
                          BigDecimal difference=balance.getOneOweTwo().subtract(owed);
                          int compare= difference.compareTo(BigDecimal.ZERO);
                          if(compare<=0){
                              balance.setOneOweTwo(BigDecimal.ZERO);
                              balance.setTwoOweOne(difference.negate());
                              if(compare==0){
                                  settled=true;
                              }
                              else{
                                  settled=null;
                              }
                          }
                          else if(compare>0){
                              balance.setOneOweTwo(balance.getOneOweTwo().subtract(owed));
                          }
                      }
                  }

              }
              else{
                  if(balance.getOneOweTwo().compareTo(BigDecimal.ZERO)>0){
                      balance.setOneOweTwo(balance.getOneOweTwo().add(owed.negate()));
                  }
                  else{
                      if(balance.getTwoOweOne().compareTo(BigDecimal.ZERO)==0){
                          balance.setOneOweTwo(owed.negate());
                      }
                      else {
                          BigDecimal difference=balance.getTwoOweOne().subtract(owed.negate());
                          int compare= difference.compareTo(BigDecimal.ZERO);
                          if(compare<=0){
                              balance.setTwoOweOne(BigDecimal.ZERO);
                              balance.setOneOweTwo(difference.negate());
                              if(compare==0){
                                  settled=true;
                              }
                              else{
                                  settled=null;
                              }
                          }
                          else if(difference.compareTo(BigDecimal.ZERO)>0){
                              balance.setTwoOweOne(balance.getTwoOweOne().subtract(owed.negate()));
                          }
                      }
                  }
              }
          }
          try{
              balanceRepository.save(balance);
              return settled;
          } catch (Exception e) {
              log.error(e.getMessage());
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
            log.error(e.getMessage());
            throw new TransactionFailedException("failed to update balance");
        }
    }

    @Override
    public BalanceDTO getBalance(User user1, User user2) {
        //retrieve the balance record between two users
        Balance balance= balanceRepository.findByUser1AndUser2(user1, user2).orElseGet(()->balanceRepository.findByUser1AndUser2(user2, user1).get());
        return this.balanceToBalanceDTO(balance);
    }

    @Override
    public BalanceDTO balanceToBalanceDTO(Balance balance) {
        return modelMapper.map(balance, BalanceDTO.class);
    }
}
