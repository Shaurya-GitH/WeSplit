package com.wesplit.main.services;

import com.wesplit.main.exceptions.ExternalAPIFailedException;
import com.wesplit.main.payloads.BalanceDTO;
import com.wesplit.main.payloads.CurrencyResponse;
import com.wesplit.main.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Service
public class CurrencyServiceImpl implements CurrencyService{
    private final RedisUtil redisUtil;

    //balances are stored in INR
    //balances are displayed in the preferred currency using cookies
    //expenses are saved and displayed in the original currency

    RestTemplate restTemplate;

    @Autowired
    CurrencyServiceImpl(RestTemplateBuilder builder, RedisUtil redisUtil){
        this.restTemplate=builder.build();
        this.redisUtil = redisUtil;
    }

    @Value("${myapp.currency_API}")
    String currencyAPI;


    @Override
    public BigDecimal updateConversion(BigDecimal money, String from) {
        //caching for 1 day
        BigDecimal multiplier=redisUtil.getValue(from,BigDecimal.class);
        if(multiplier!=null){
            return money.multiply(multiplier);
        }
        String finalAPI= currencyAPI.replace("FROM",from).replace("TO","INR");
        ResponseEntity<CurrencyResponse> response= restTemplate.exchange(finalAPI,HttpMethod.GET,null, CurrencyResponse.class);
        if(response.getStatusCode().equals(HttpStatus.OK)){
            multiplier=response.getBody().getData().getMid();
            redisUtil.setValue(from,multiplier,86400L);
            return money.multiply(multiplier);
        }
        else{
            throw new ExternalAPIFailedException("Currency API failed");
        }
    }

    @Override
    public BalanceDTO displayBalance(BalanceDTO balance, String to) {
        //caching for 1 day
        BigDecimal multiplier=redisUtil.getValue(to,BigDecimal.class);
        if(multiplier==null){
            String finalAPI= currencyAPI.replace("FROM","INR").replace("TO",to);
            ResponseEntity<CurrencyResponse> response= restTemplate.exchange(finalAPI,HttpMethod.GET,null, CurrencyResponse.class);
            if(response.getStatusCode().equals(HttpStatus.OK)) {
                multiplier = response.getBody().getData().getMid();
                redisUtil.setValue(to,multiplier,86400L);
            }
            else{
                throw new ExternalAPIFailedException("Currency API failed");
            }
        }
        if(balance.getOneOweTwo().compareTo(BigDecimal.ZERO)!=0){
            balance.setOneOweTwo(balance.getOneOweTwo().multiply(multiplier));
        }
        if(balance.getTwoOweOne().compareTo(BigDecimal.ZERO)!=0){
            balance.setTwoOweOne(balance.getTwoOweOne().multiply(multiplier));
        }
        return balance;
    }
}

