package com.wesplit.main.services;

import com.wesplit.main.entities.Balance;
import com.wesplit.main.entities.User;
import com.wesplit.main.exceptions.TransactionFailedException;
import com.wesplit.main.payloads.BalanceDTO;
import com.wesplit.main.payloads.UserDebt;
import com.wesplit.main.repositories.BalanceRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Set;

@Slf4j
@Service
public class BalanceServiceImpl implements BalanceService {
    private final ModelMapper modelMapper;
    private final BalanceRepository balanceRepository;
    private final CurrencyService currencyService;

    BalanceServiceImpl(BalanceRepository balanceRepository, ModelMapper modelMapper, CurrencyService currencyService){
        this.balanceRepository=balanceRepository;
        this.modelMapper = modelMapper;
        this.currencyService = currencyService;
    }

    @Transactional
    @Override
    public void addNewBalance(User user1, User user2,Long groupId) {
        boolean exists;
        //checking if the balance is already created
        if(groupId!=null){
            exists=false;
        }
        else{
            if(balanceRepository.findByUser1AndUser2(user1,user2).isEmpty()){
                if(balanceRepository.findByUser1AndUser2(user2,user1).isEmpty())exists=false;
                else{
                    exists=true;
                }
            }
            else exists=true;
        }
        //creating a new balance if not already created
       if(!exists){
           Balance newBalance= Balance.builder()
                           .oneOweTwo(BigDecimal.ZERO)
                           .twoOweOne(BigDecimal.ZERO)
                           .user1(user1)
                           .user2(user2)
                           .build();
           if(groupId!=null){
               newBalance.setGroupId(groupId);
           }
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
    public Boolean updateExpenseBalance(User user1, User user2, BigDecimal initialOwed,String currency) {
        Boolean settled=false;
        BigDecimal owed=currencyService.updateConversion(initialOwed,currency);
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
    public Boolean updatePaymentBalance(User user1, User user2, BigDecimal initialPaid,String currency) {
        Boolean settled=false;
        BigDecimal paid=currencyService.updateConversion(initialPaid,currency);
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

    @Transactional
    @Override
    public BalanceDTO getBalance(User user1, User user2,String currency) {
        //retrieve the balance record between two users
        Balance balance= balanceRepository.findByUser1AndUser2(user1, user2).orElseGet(()->balanceRepository.findByUser1AndUser2(user2, user1).get());
        BalanceDTO balanceDTO= this.balanceToBalanceDTO(balance);
        return currencyService.displayBalance(balanceDTO,currency);
    }

    @Override
    public BalanceDTO balanceToBalanceDTO(Balance balance) {
        return modelMapper.map(balance, BalanceDTO.class);
    }

    @Transactional
    @Override
    public void updateGroupBalance(HashMap<User, BigDecimal> groupDebtTable, HashMap<User, BigDecimal> debt,String currency,Long groupId) {
        Set<User> keyset= debt.keySet();
        //creating net debt table
        for(User user:keyset){
            BigDecimal convertedDebt= currencyService.updateConversion(debt.get(user),currency);
            groupDebtTable.put(user,groupDebtTable.get(user).add(convertedDebt));
        }
        //making priority queues for creditors and debtors
        PriorityQueue<UserDebt> creditors=new PriorityQueue<>(Comparator.comparing(UserDebt::getDebt,Comparator.reverseOrder()));
        PriorityQueue<UserDebt> debtors=new PriorityQueue<>(Comparator.comparing(UserDebt::getDebt));
        Set<User> groupKeySet= groupDebtTable.keySet();
        for(User user:groupKeySet){
            BigDecimal value=groupDebtTable.get(user);
            if(value.compareTo(BigDecimal.ZERO)>0){
                UserDebt userDebt= UserDebt.builder()
                        .user(user)
                        .debt(value)
                        .build();
                debtors.add(userDebt);
            }
            else if(value.compareTo(BigDecimal.ZERO)<0){
                UserDebt userDebt= UserDebt.builder()
                        .user(user)
                        .debt(value)
                        .build();
                creditors.add(userDebt);
            }
        }
        //minimizing cash flow
        while(!debtors.isEmpty() && !creditors.isEmpty()){
            UserDebt debtor= debtors.poll();
            UserDebt creditor= creditors.poll();
            BigDecimal transaction= debtor.getDebt().add(creditor.getDebt());
            //retrieve the balance record between two users
            Balance balance= balanceRepository.findByUser1AndUser2Group(debtor.getUser(), creditor.getUser(),groupId).orElseGet(()->balanceRepository.findByUser1AndUser2Group(creditor.getUser(), debtor.getUser(),groupId).get());
            if(balance.getUser1().equals(debtor.getUser())){
                balance.setTwoOweOne(BigDecimal.ZERO);
                if(transaction.compareTo(BigDecimal.ZERO)<0){
                    balance.setOneOweTwo(debtor.getDebt());
                    UserDebt remaining= UserDebt.builder()
                            .user(creditor.getUser())
                            .debt(transaction)
                            .build();
                    creditors.add(remaining);
                }
                else if(transaction.compareTo(BigDecimal.ZERO)>0){
                    balance.setOneOweTwo(creditor.getDebt().negate());
                    UserDebt remaining= UserDebt.builder()
                            .user(debtor.getUser())
                            .debt(transaction)
                            .build();
                    debtors.add(remaining);
                }
                else if(transaction.compareTo(BigDecimal.ZERO)==0){
                    balance.setOneOweTwo(debtor.getDebt());
                }
            }
            else{
                balance.setOneOweTwo(BigDecimal.ZERO);
                if(transaction.compareTo(BigDecimal.ZERO)<0){
                    balance.setTwoOweOne(debtor.getDebt());
                    UserDebt remaining= UserDebt.builder()
                            .user(creditor.getUser())
                            .debt(transaction)
                            .build();
                    creditors.add(remaining);
                }
                else if(transaction.compareTo(BigDecimal.ZERO)>0){
                    balance.setTwoOweOne(creditor.getDebt().negate());
                    UserDebt remaining= UserDebt.builder()
                            .user(debtor.getUser())
                            .debt(transaction)
                            .build();
                    debtors.add(remaining);
                }
                else if(transaction.compareTo(BigDecimal.ZERO)==0){
                    balance.setTwoOweOne(debtor.getDebt());
                }
            }
            try{
                balanceRepository.save(balance);
            }
            catch (Exception e){
                throw new TransactionFailedException("failed to update balance");
            }
        }
    }

}
